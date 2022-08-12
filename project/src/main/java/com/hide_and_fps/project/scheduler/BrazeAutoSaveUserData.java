package com.hide_and_fps.project.scheduler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.hide_and_fps.project.util.WebClientNeedRetryException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class BrazeAutoSaveUserData implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(BrazeAutoSaveUserData.class);
	private final WebClient webClient;

	public BrazeAutoSaveUserData (WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("").defaultHeaders(header->{
			header.setContentType(MediaType.APPLICATION_JSON);
			header.set("Authorization", "");
		}).build();
	}
	
	public Mono<String> getUserDataUrl() {
		/*
		String json = """
		{
		    "segment_id" : "aa",
		    "callback_endpoint" : "http://localhost/",
		    "fields_to_export" : %s,
		    "output_format" : "zip"
		}
		""".formatted(this.fields);
		*/
		String sendBody = """
		{
		    "segment_id" : "%s",
		    "callback_endpoint" : "http://localhost/",
		    "fields_to_export" : %s,
		    "output_format" : "zip"
		}
		""".formatted("","");//BrazeDashboardConstants.BRAZE_ALL_USER_DATA_SEGMENT_ID, 
					//BrazeDashboardConstants.BRAZE_USER_DATA_EXPORT_OPTION_FIELDS);
		
		return this.webClient.post()
					.uri("")//BrazeDashboardConstants.BRAZE_API_USER_DATA_EXPORT_ENDPOINT)
					.body(Mono.just(sendBody), String.class)
					.retrieve()
					.onStatus(status -> status.value() == HttpStatus.METHOD_NOT_ALLOWED.value(), 
                    		response -> Mono.error(new Exception()))
					.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>(){})
					.map(x-> x.get("message").toString().contains("is already in progress") ? "" : (String)x.get("url")) // �̹� ���� ���� �ƴ� ��� message == "success"
					.defaultIfEmpty("");
	}
	
	public Path webClientDownloadZip(WebClient.Builder webClientBuilder, String baseUrl) {
		if(baseUrl == null || baseUrl.isBlank()) {
			return null;
		}
		//Paths.get( System.getProperty("user.home") + "/braze_monitoring_logs/braze_user_data_zip.zip" );
		Path zipPath = Paths.get("");//util.filePath + BrazeDashboardConstants.BRAZE_USER_DATA_ZIP_NAME );
		//웹 클라이언트 설정 (webClient 버퍼 사이즈 조절 Exceeded limit on max bytes to buffer : 262144 해결을 위해 -1로 설정(maxInMemorySize 함수 내용 주석 참조))
		WebClient webClient = webClientBuilder.baseUrl(baseUrl)
												.exchangeStrategies(
													ExchangeStrategies.builder()
													.codecs(configurer -> configurer.defaultCodecs()
														.maxInMemorySize(-1)
													).build()
												)
												.build();
		
		//브레이즈 api에서 내려준 zip url로 연결하여 zip파일의 dataBuffer을 얻어온다.
		//브레이즈 api에서 내려준 url로 접근시 즈그들 zip파일이 준비가 안되어 있으면 403에러를 내려주기에 retry 전략을 설정하여 400번대 에러인 경우 request를 다시 보내도록 한다.
		Flux<DataBuffer> dataBufferInZipFile = webClient.get()
				.retrieve()
			    .onStatus(HttpStatus::is4xxClientError, 
			              response -> Mono.error(new WebClientNeedRetryException("Server error", response.rawStatusCode())))
				.bodyToFlux(DataBuffer.class)
			    .retryWhen(Retry.backoff(Integer.MAX_VALUE, Duration.ofSeconds(30))
			    		.filter(throwable -> throwable instanceof WebClientNeedRetryException));

		//다운로드 받은 파일을 write한다.
		DataBufferUtils.write(dataBufferInZipFile, zipPath, StandardOpenOption.CREATE).share().block();
		
		return zipPath;
	}
	
	/**
	 * 
	 * @see 
	 * <i>Note: A PostConstruct interceptor method must not throw application 
	 * exceptions, but it may be declared to throw checked exceptions including 
	 * the java.lang.Exception if the same interceptor method interposes on 
	 * business or timeout methods in addition to lifecycle events. If a 
	 * PostConstruct interceptor method returns a value, it is ignored by 
	 * the container.</i>
	 */
	public void zipToJson(Path zipPath) {
		if(zipPath == null) {
			logger.error("=======================================");
			logger.error("ZIP Is Null Maybe ZIP File Url Is Empty");
			logger.error("=======================================");
		}else {
			//Paths.get( System.getProperty("user.home") + "/braze_monitoring_logs/braze_user_data.json" );
			Path savePath = Paths.get("");//util.filePath + BrazeDashboardConstants.BRAZE_USER_DATA_FILE_NAME );
			int i = 0;
			StringBuffer str = new StringBuffer();
			try( ZipFile zipFile = new ZipFile(zipPath.toFile()); 
				BufferedWriter writer = Files.newBufferedWriter(savePath, StandardCharsets.UTF_8
						, StandardOpenOption.CREATE
						, StandardOpenOption.SYNC);	
				){
				Enumeration<? extends ZipEntry> entry = zipFile.entries();
				while(entry.hasMoreElements()) {
					ZipEntry zipEntry = entry.nextElement();
					try( BufferedReader reader = new BufferedReader( new InputStreamReader( zipFile.getInputStream(zipEntry), StandardCharsets.UTF_8) ) ){
						String line;
						while( (line = reader.readLine() ) != null ) {
							i++;
							JSONObject data = (JSONObject)new JSONParser().parse(line);
							if(data.containsKey("external_id")) {
								str.append( data.toJSONString() +",\n");
							}
							if(i%200 == 0 && str.isEmpty() == false) {
								writer.write(str.toString());
								writer.flush();	
								str.setLength(0);
							}
						}
					} catch (ParseException e) {
						logger.error("json Parse Exception Error >>> " + e.getMessage(), e);
					}
				}
				logger.debug("Save User Data Json File Convert Complate");
			} catch (ZipException e) {
				logger.error("Zip Error >>> " + e.getMessage(), e);
			} catch (IOException e) {
				logger.error("IO Error >>> " + e.getMessage(), e);
			}finally {
				try {
					Files.deleteIfExists(zipPath);
				} catch (IOException e) {
					logger.error("Zip File Delete Failed >>> " + e.getMessage(), e);
				}
			}
		}
	}
	
	@Override
	public void run() {
		
		try {
			Path zipFile = Paths.get(""); //util.filePath + BrazeDashboardConstants.BRAZE_USER_DATA_ZIP_NAME );
			Files.deleteIfExists(zipFile);
		}catch(IOException e) {}
		
		try {
			Path jsonFile = Paths.get(""); //util.filePath + BrazeDashboardConstants.BRAZE_USER_DATA_FILE_NAME );
			Files.deleteIfExists(jsonFile);
		}catch(IOException e) {}
		
		Path zipPath = webClientDownloadZip(WebClient.builder(), getUserDataUrl().block());
		zipToJson(zipPath);
	}

}

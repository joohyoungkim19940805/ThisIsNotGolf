package com.hide_and_fps.project.scheduler;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.hide_and_fps.project.constants.IfPrdNeedToMoveJVMConstants;
import com.hide_and_fps.project.util.CommonUtil;
import com.hide_and_fps.project.util.WebClientNeedRetryException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class CompanySummaryInfo  {
	
	private final WebClient webClient;
	
	private final CommonUtil util;
	//private final WebClient companySummaryInfo;
	
	public CompanySummaryInfo(WebClient.Builder webClientBuilder, CommonUtil util ) {
		this.util = util;
		this.webClient = webClientBuilder.baseUrl("https://opendart.fss.or.kr/")
		.exchangeStrategies(
			ExchangeStrategies.builder()
			.codecs(configurer -> configurer.defaultCodecs()
				.maxInMemorySize(-1)
			).build()
		)
		.build();
	}
	
	/**
	 * 내일의 나에게....
	 * xml 파일로 줍니다
	 * zip 내용 지우고 xml 파일 읽어서 저장하도록 고쳐봅시다.
	 */
	private void getInherenceInfoZipFile() {
		Path zipPath = util.IfNoSuchFileToCreate(Paths.get(util.filePath + "TMP/zip/InherenceInfo.zip"));
		System.out.println("아마 여기");
		Flux<DataBuffer> dataBufferInZipFile = this.webClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("api/corpCode.xml")
				.queryParam("crtfc_key", IfPrdNeedToMoveJVMConstants.DART_API_KEY_MOZU123)
				.build())
			.retrieve()
		    .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new WebClientNeedRetryException("Server error", response.rawStatusCode())))
			.bodyToFlux(DataBuffer.class)
		    .retryWhen(Retry.backoff(Integer.MAX_VALUE, Duration.ofSeconds(30))
		    	.filter(throwable -> throwable instanceof WebClientNeedRetryException));
		System.out.println("맞졍?");
		//다운로드 받은 파일을 write한다.
		DataBufferUtils.write(dataBufferInZipFile, zipPath, StandardOpenOption.CREATE).share().block();

	}
	
	//getCompanyInherenceInfo
	

	
	public static void main(String a[]) {
		new CompanySummaryInfo(WebClient.builder(), new CommonUtil()).getInherenceInfoZipFile();
	}
	
}

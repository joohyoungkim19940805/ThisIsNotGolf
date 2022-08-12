package com.hide_and_fps.project.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component
public class CommonUtil {//extends AwsCliCommand{
	
	private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);
	
	public SimpleDateFormat dateFormat;
	
	public SimpleDateFormat dateTimeFormat;
	
	public Date nowDate;
	
	public String nowDateStr;
	
	public String filePath;
	
	public CommonUtil() {
		this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		this.nowDate = new Date(System.currentTimeMillis());;
		this.nowDateStr = this.dateFormat.format(this.nowDate);
		this.filePath = System.getProperty("user.home") + "/stockApp/";
	}
	
	/*
	public CommonUtil(String targetDate) throws ParseException {
		this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		this.nowDateStr = targetDate;
		this.nowDate = this.dateFormat.parse(this.nowDateStr);
		
	}
	public CommonUtil(boolean bol) throws IOException {
		this(0);
	}
	
	public CommonUtil(int commandNumber) throws IOException {
		changeCliOutput("json");
		this.dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		this.nowDate = new Date(System.currentTimeMillis());
		this.nowDateStr = this.dateFormat.format(this.nowDate);
		//로그 저장 경로
		this.filePath = System.getProperty("user.home") + "/braze_monitoring_logs/assistant/braze_log/" + "어시스턴트_실행날짜_" + this.nowDateStr+ "_" + "command_" + commandNumber;
		
		Path defultPath = Paths.get(this.filePath);

		//해당 파일의 이름이 이미 있을 경우 이름을 새롭게 지정한다.
		for(int i = 1 ; Files.isDirectory(defultPath, LinkOption.NOFOLLOW_LINKS) ; i++) {
			defultPath = Paths.get("%s_%d/".formatted(this.filePath, i));
		}
		
		this.filePath = defultPath.toString() + "\\";
		
		logger.debug("설정된 풀더 경로 >>>" + this.filePath);
		//this.nowDate = 
	}
	*/

	/**
	 * cli 출력 포맷 변경 함수
	 * @param wantOutput cli 커맨드 어떤 출력 포맷을 원하는지? (json, text 등 (cli config doc 참조))
	 * @throws IOException
	 */
	/*
	protected void changeCliOutput(String wantOutput) throws IOException {
		//aws cli 인증정보 및 설정정보 파일 불러오기
		File credentials_path = new File(System.getProperty("user.home") + USER_PORFILE_CREDENTIALS);
		File config_path = new File(System.getProperty("user.home") + USER_PORFILE_CONFIG);

		try(FileReader credentials = new FileReader(credentials_path);
			FileReader config = new FileReader(config_path);)
		{
			Properties prop_credentials = new Properties();
	        prop_credentials.load(credentials);
	        
	        Properties prop_config = new Properties();
	        prop_config.load(config);
	        
        	if ( Arrays.asList(CLI_OUTPUT_FORMAT).contains(wantOutput) 
					&& wantOutput.equals( prop_config.getProperty("output") ) == false) {
        		
        		logger.warn("cli config changed format >>> : " + wantOutput);
				Process process = Runtime.getRuntime().exec("cmd /c " + CLI_OUTPUT_SET_COMMAND.formatted(wantOutput));
				process.waitFor();
				
			}
        	
		}catch(FileNotFoundException e) {
			
			logger.error("cli 인증정보 파일이 존재하지 않습니다. cli 인증을 먼저 해주시길 바랍니다.", e);
			
		}catch(Exception e) {
			
			logger.error(e.getMessage(), e);
			
		}
	}
	*/

	/**
	 * node들의 순서가 보장되는 json prettyPrinting ( value에 "aa,bb"와 같이 ,가 들어갈 때 한 탭씩 밀리는 현상 있음 추후 수정 필요 )
	 * @param obj json 포맷인 객체
	 * @return
	 */
	public String prettyPrintingJson(Object obj) {
		
		StringBuilder prettyJSONBuilder = new StringBuilder();
		//JSONParser jsonParser = new JSONParser();
		//obj = jsonParser.parse(obj).toString();
		//System.out.println(obj.toString());
		
		String str = obj.toString().trim()
				.replaceAll("\\[", "[\n")
				.replaceAll("\\{", "{\n")
				.replaceAll("\\]", "\n]")
				.replaceAll("\\}", "\n}")
				.replaceAll("\\,", ",\n");
		
		//String strArr[] = obj.toString().trim().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		//for(String item : strArr)
		//System.out.println(str);
		String line = "";
		long tab = 1;
		long prevTab = 0;
		
		try(BufferedReader br = new BufferedReader( new StringReader(str) );){
			while ((line = br.readLine()) != null) {
				line = line.trim();
				Character c = ' ';
				boolean lastTextComma = false;
				//콤마 여부에 따라 마지막 글자 자르는 부분을 분기처리
				if(line.isBlank() == false) {
					lastTextComma = line.charAt(line.length()-1) == ',';
					if(lastTextComma) {
						c = line.charAt(line.length()-2);
					}else {
						c = line.charAt(line.length()-1);
					}
				}
				
				//제이슨 시작문자열일 경우 탭 추가 후 tab,prevTab ++
				if(c.equals('{') || c.equals('[')) {
					//tab변수와 prevTab 변수의 차이를 tab-prevTab = 1 이 나오게 만든다.
					for(;tab - prevTab > 1;) {
						if(tab>prevTab) {
							tab--;
						}else {
							//로직이 오류일 경우 무한루프 방지
							break;
						}
					}
					
					line = String.format("%"+tab*4+"s", "") + line;
					tab++;
					prevTab++;
					
				//제이슨 종료 문자열일 경우 탭 추가후 tab변수와 prevTab변수를 일치시킨 후 prevTab-- 시킨다.
				}else if(c.equals('}') || c.equals(']')) {
					
					//제이슨 종료 문자열일 경우 탭을 prevTab으로 준다.
					line = String.format("%"+prevTab*4+"s", "") + line;
					
					//해당 부분으로 마지막 문자에 Comma가 있는 경우 tab - prevTab = 2가 된다. 
					//2를 이용하여 콤마여부에 따라 탭이 다르게 추가되도록 만든다.
					if( lastTextComma == false ) {
						tab = prevTab;
					}
					prevTab --;

				//제이슨 종료,시작 문자열이 아닐 경우 탭만 추가해준다.
				}else{
					//tab변수와 prevTab 변수의 차이를 tab-prevTab = 1 이 나오게 만든다.
					for(;tab - prevTab > 1;) {
						if(tab>prevTab) {
							tab--;
						}else {
							//로직이 오류일 경우 무한루프 방지
							break;
						}
					}
					
					line = String.format("%"+tab*4+"s", "") + line;
				}

				prettyJSONBuilder.append(line+"\n");
			}
		}catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		return prettyJSONBuilder.toString();
	}
	
	/**
	 * 내용을 json 파일로 저장시키는 함수
	 * @param str 저장 시킬 내용
	 * @param fileName 저장 할 파일의 이름
	 * @return 
	 */
	public Path fileSave(Object str, String fileName, String format) {
		String dir = this.filePath + fileName.replaceAll("/", "\\\\");
		Path directoryPath = Paths.get(dir.substring(0, dir.lastIndexOf("\\")));
		Path filePath = Paths.get(dir + "_0." + format);
		
		//해당 파일의 이름이 이미 있을 경우 이름을 새롭게 지정한다.
		for(int i = 1 ;Files.exists(filePath, LinkOption.NOFOLLOW_LINKS); i++) {
			filePath = Paths.get(dir + "_%d.%s".formatted(i, format));
		}
		
		try {
			//해당 디렉터리에 경로 중 존재하지 않는 풀더가 있을 시 풀더 생성
			Files.createDirectories(directoryPath);
			
			try(BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8);){
				writer.write(str.toString());
				writer.flush();
			} catch(IOException e) {
				logger.error(e.getMessage(), e);
			} catch(NullPointerException e){
				//logger.error("저장할 내용이 비어있습니다. : " + e.getMessage() ,e);
			}finally {
				if(Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
					logger.debug( "%s에 저장 성공하였습니다.".formatted(filePath.toString()) );
					return filePath;
				}else {
					logger.error( "======%s에 저장 실패========".formatted(filePath.toString()) );
				}
			}
		}catch(AccessDeniedException e) {
			logger.error("해당하는 경로에 접근 권한이 없어 로그 저장에 실패하였습니다. : " + e.getMessage(),e);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
		
	}
	
	/**
	 * Map 객체를 Record 객체로 변환하는 함수
	 * @param <T>
	 * @param targetClass 변환하고자 하는 record의 class
	 * @param data record로 변환할 map data
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected <T extends Record> T transformMapToRecord(Class<T> targetClass, Map data) {
		try {
			// 타겟이 되는 record calss의 구성요소를 순서대로 반환
			RecordComponent[] component = targetClass.getRecordComponents();
			// record 컴포넌트의 길이
			int len = component.length;
			// record 클래스를 선언시 들어갈 parameter
			Object[] params = new Object[len];
			// 각 컴포넌트들의 클래스 타입을 지정할 배열
			Class<?>[] componentsType = new Class<?>[len];

			// 생성자에 들어갈 parameter, class type을 넣어준다.
			for(len-- ; len >= 0 ; len--) {
				// 컴포넌트의 타입을 넣어준다.
				componentsType[len] = component[len].getType();
				// 데이터를 컴포넌트의 타입으로 캐스팅하여 넣어준다.
				params[len] = component[len].getType().cast( data.get(component[len].getName()) );
			}
			
			//생성자에 컴포넌트들의 타입을 넣어준다.
			Constructor<T> target = targetClass.getDeclaredConstructor(componentsType);
			// 타겟이 되는 record를 new 한다.
			return target.newInstance(params);
		} catch (NoSuchMethodException | SecurityException | InstantiationException 
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NullPointerException | ClassCastException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * 날짜기준으로만 계산 (날짜만 필요한 경우) 
	 * @param day 빼고자 할 일수
	 * @return
	 */
	public Calendar mathSubDayOnlyDate(int day) {
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(this.dateFormat.parse(this.nowDateStr));
		} catch (ParseException e) {
			cal.setTime(this.nowDate);
		}
		cal.add(Calendar.DATE, -1*day);
		
		return cal;
	}
	
	/**
	 * 시간까지 포함해서 계산 (시간도 같이 필요한 경우)
	 * @param day 빼고자 할 일수
	 * @return
	 */
	public Calendar mathSubDay(int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.nowDate);
		cal.add(Calendar.DATE, -1*day);
		
		return cal;
	}
	
	@SuppressWarnings("finally")
	public Path IfNoSuchFileToCreate(Path path) {
		String pathStr = path.toString();
		Path createPath = Paths.get(pathStr.substring(0, pathStr.lastIndexOf("\\")));
		try {
			Files.createDirectories(createPath);
		} catch (IOException e) {
			return null;
		}
		return path;
	}
	
	//public static void main(String args[]) throws IOException {
		/*
		String test = "{\"ingestionTime\":1642289773931,\"test\":{\"qweqweqe\":\"jsdiaji\",\"tetetete:\"{\"tete\":123,\"hi\":111,\"test33:\":{\"test44\":\"test44\",\"test444\":\"test444\"},\"test333\":{\"test333\":\"test333\"}}},\"message\":{\"purchases_processed\":74,\"message\":\"success\",\"errors\":[{\"input_array\":\"purchases\",\"index\":44,\"type\":\"'quantity' is not valid\"}],\"test\":{\"tetete\":1234}},\"timestamp\":1642289772495}";
		
		
		//-----------------
		
		System.gc(); 
		long before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println(new CommonUtil().prettyPrintingJson(test));
		System.gc(); 
		long after  = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long usedMemory = (before - after)/1000;

		System.out.println("Used Memory My Code< : " + usedMemory);
		
		//---------------------
		
		System.out.println("\n" + Runtime.getRuntime().totalMemory());
		*/
		/*
		logger.debug("test_debug");
		logger.info("test_info");
		logger.warn("test_warn");
		logger.trace("test_trace");
		logger.error("test_error");
	
		new CommonUtil().saveCliOutput("json");
		Process process = Runtime.getRuntime().exec("cmd /c " + "aws ecr list-images --repository-name eland-braze-batch");
		System.out.println(process);
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
		    sb.append(line);
		}
		String result = sb.toString();
		System.out.println(result);//--max-items=50
       	*/
		//CommonUtil commUtil = new CommonUtil();
		//commUtil.changeCliOutput("json");
		
		//List<Map<String, Object>> logStream = (List<Map<String, Object>>) commUtil.getLogStream(0);
		
		//String test = "";
		//for(Map<String, Object> data : logStream) {
			//logger.debug("logStream date >>> " + commUtil.dateFormat.format( new Date((Long) data.get("creationTime")) ));

			//logger.debug(data.get("logStreamName").toString());
			
			
		//}
		
		//try(OutputStream output = new FileOutputStream(System.getProperty("user.home") + "/braze_monitoring_logs/test.txt")){
		//	output.write( test.getBytes() );
		//}
	//}
	
	/*
	public static void main(String args[]) {
		String test = "{\"ingestionTime\":1645664650846,\"message\":\"  <title>We're sorry, but something went wrong (500)<\\/title>\",\"timestamp\":1645664645865}";
		test = test.chars().filter(e -> {System.out.println((char)e); return e=='i';}).mapToObj(Character::toString)
        .collect(Collectors.joining(""));
		System.out.println(test);
	}
	*/
	/*
	public static void main(String args[]) throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        for(byte d : bytes) {System.out.println((char)d);}
        Base32 base32 = new Base32();
        System.out.println( base32.encodeToString(bytes) );
        System.out.println( base32.encodeToString(bytes).toLowerCase().replaceAll("(.{4})(?=.{4})", "$1 ") );
        KeyGenerator keyGener  = KeyGenerator.getInstance("AES");
        keyGener.init(256);
        System.out.println(keyGener.generateKey());
        SecretKey aesKey = keyGener.generateKey();
        byte[] key  = aesKey.getEncoded();
        for(byte d2 : bytes) {System.out.println(d2);}
        System.out.println(new String(key));
        System.out.println( base32.encodeToString(key) );
	}
	 */
	/*
	public static void main(String args[]) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] bytes = new byte[256];
		SecureRandom random = new SecureRandom();
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");		
		random.nextBytes(bytes);
		keyGen.init(256);
		Key key = keyGen.generateKey();
		System.out.println(key);
		cipher.init(ENCRYPT_MODE, key);
		byte[] result = cipher.doFinal(bytes);
		System.out.println(new String(result));
		Base32 base32 = new Base32();
		System.out.println(base32.encodeToString(result));
	}
	*/
	/*
	public static void main(String args[]) {
		SecureRandom random = new SecureRandom();
		System.out.println(random.nextInt());
	}
	*/
	/*
	public static void main(String args[]) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		SecureRandom random = new SecureRandom();
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");		
		keyGen.init(256, random);
		Key key = keyGen.generateKey();
		System.out.println(key);
		cipher.init(ENCRYPT_MODE, key);
		SecretKey secretKey = keyGen.generateKey();
		System.out.println(secretKey.getEncoded());
		for(byte d:secretKey.getEncoded()) {System.out.println(d);}
	}
	*/
	
}

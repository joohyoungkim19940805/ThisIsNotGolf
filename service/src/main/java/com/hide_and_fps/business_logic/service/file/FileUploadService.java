package com.hide_and_fps.business_logic.service.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

	private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);
	
	//최대로 올릴 수 있는 파일 갯수
	private int maxLimitImgLength = 20;
	
	//파일명에서 체크할 특수문자 패턴
	private String fileNamePattern = ".*[ !@#$%^&*(),?\\\\\\\":{}|<>].*";
	
	private List<String> fileMimeTypeList = Arrays.asList("image/apng",
														  "image/avif",
														  "image/bmp",
														  "image/gif",
														  "image/jpeg",
														  "image/pjpeg", //IE만 읽는 jpg image의 mime 형태
														  "image/png",
														  "image/svg+xml",
														  "image/tiff",
														  "image/webp",
														  "image/x-icon");
	
	private List<String> fileExtensionTypeList = Arrays.asList( ".apng",
																".avif",
																".bmp",
																".gif",
																".jpg", ".jpeg", ".jfif", ".pjpeg", ".pjp",
																".png",
																".svg",
																".tif",
																".tiff",
																".webp",
																".ico",
																".cur");
	
	public List<Object> fileUpload(List<MultipartFile> fileList){
		
		List<Object> result = new ArrayList<Object>();
		long errorTime = System.currentTimeMillis();
		
		//이미지 갯수 체크
		if( (fileList.size() > 0 && fileList.size() < maxLimitImgLength) == false ) 
		{
			//조건에 맞지 않을시 
			logger.error("bad image length"); 
			logger.error("["+errorTime+"]"+" >>> file list : " + fileList); 
			logger.error("["+errorTime+"]"+" >>> file ist file count : " + fileList.size()); 
			return result;//new ArrayList();
		}
		
		for(MultipartFile file : fileList) {
			
			if(fileCheck(file)) 
			{
				Object uploadedFileInfo = null;
				try {
					String savePath = System.getProperty("user.home"); //+ this.config.getString("system.file.uploadPath");
					//System.out.println(savePath);
					uploadedFileInfo = null;

					//프로젝트와 프로젝트의 실제 경로가 프론트에 노출 되지 않도록 방지하기 위해 null 값으로 변경
					//uploadedFileInfo.setFileUrl(this.config.getString("system.file.url"));
					
					result.add(uploadedFileInfo);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					logger.error("["+errorTime+"]"+" >>> file : " + file); 
				}
				
				//썸네일 별도 처리
				/*
				int width  = 45;
				int height = 0;
				HashMap<String, Integer> thumMap = new HashMap<String, Integer>();
				thumMap.put("width" , width); //섬네일높이
				thumMap.put("height", height);//섬네일넓이
				//썸네일
				ResizeImageFileFilter resizeImageFileFilter = new ResizeImageFileFilter(width, height);
				//InputStream is = multipartFile.getInputStream();
				//resizeImageFileFilter.doFilter(is, multipartFile.getContentType());
	
				FileFilter[] fileFilters = new FileFilter[2];
				fileFilters[0] = exifFileFilter;
				fileFilters[1] = resizeImageFileFilter;
	
				uploadedFileInfo = imageService.getFileUploader().uploadFile(file, imageService.setSaveFilePath("/"), true, fileFilters); // 파일업로드
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("thumImg"    , uploadedFileInfo); //섬네일업로드이미지정보
				model.put("thumImgSize", thumMap); // 섬네일업로드이미지 사이즈정보
				*/
			}
	
		}
	
		return result;
	}
	
	private boolean fileCheck(MultipartFile file){
    	
		long errorTime = System.currentTimeMillis();
    	
		//파일 이름
    	String fileName = file.getOriginalFilename();
		
		//파일명칭에 잘못된 특수문자가 들어가있는지 여부 체크
		boolean fileNameCheck = fileName.substring(0, fileName.indexOf(".")).matches(fileNamePattern);
		
		//이미지 타입 추출 ex) .jpg
		String imageType = fileName.substring(fileName.lastIndexOf('.')).toLowerCase();
		
		try {
    		// 파일의 확장자 존재 여부 체크
    		if(fileName.contains(".") == false) 
    		{
    			throw new IOException("bad file !! not found dot >> " + fileName);
    		}
    		// dot 체크 ex) 2802410394731776943_n.exe%00.jpg
    		else if(fileName.split("\\.").length > 2) 
    		{
    			throw new IOException("bad file !! many dot >> " + fileName);
    		}
    		// 파일명칭에 특수문자 여부
            else if(fileNameCheck) 
            {
            	throw new IOException("bad file !! bad file name >> " + fileName);
            }
    		// 파일 확장자 형식 체크
    		else if(
    				//fileExtensionTypeList.contains(imageType) == false // or
    				fileExtensionTypeList.stream().anyMatch(s -> s.equals(imageType)) == false // or
    				//fileExtensionTypeList.stream().noneMatch(s -> s.equals(imageType))
    				)
    		{
    			throw new IOException("bad file !! bad file extension type >> " + imageType);
    		}
    		// Mime type 체크
    		else if(
    				//fileMimeTypeList.contains( file.getContentType()) == false // or
    				fileMimeTypeList.stream().anyMatch(s -> s.equals(file.getContentType())) == false // or
    				//fileMimeTypeList.stream().noneMatch(s -> s.equals(file.getContentType()))
    				) 
    		{
    			throw new IOException("bad file !! not support mime type >> " + file.getContentType());
    		}
    		else 
    		{
    			return true;
    		}
		}catch(Exception e) {
			
			logger.error(e.getMessage(), e);
			logger.error("["+errorTime+"]"+" >>> file name : " + fileName); 
			logger.error("["+errorTime+"]"+" >>> file extension type : " + imageType);
			logger.error("["+errorTime+"]"+" >>> file mime type : " + file.getContentType());
			
			return false;
		}
    }
	
}

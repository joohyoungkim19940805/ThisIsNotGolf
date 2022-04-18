package com.hide_and_fps.business_logic.service.file;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
	
	/**	파일업로드 경로 셋팅시 현재 날짜 기준으로 yyyyMM 폴더를 추가하는 함수 
	 * @param saveFilePath	파일 저장 경로
	 * @return 파일 저장경로 + yyyyMM
	 * */
	public String setSaveFilePath (String saveFilePath)
	{
		//저장경로에 현재 yyyyMM 폴더 생성하기
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		Date date = new Date ();
		String currentDate = sdf.format(date);
		
		//saveFilePath = "images/web/" + saveFilePath + "/" + currentDate;
		saveFilePath =  saveFilePath + "/" + currentDate;
		
		return saveFilePath;
	}

}

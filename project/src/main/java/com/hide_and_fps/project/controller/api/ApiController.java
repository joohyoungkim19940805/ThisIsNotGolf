package com.hide_and_fps.project.controller.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

//import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

//import com.hide_and_fps.project.config.SocketMssageHandler;

//@RestController
public class ApiController {

	private int totalCount = 0;
	private int statusDownCount = 0;
	private int statusUpCount = 0;
	private int statusKeepCount = 0;
	
	//@PostMapping(value = "testApi", produces = MediaType.APPLICATION_JSON_VALUE)
	/*
	public String testApiPost(@RequestBody Map<String, Object> attributes, HttpServletRequest request) {
		//System.out.println(attributes);

		System.out.println("totalCount : " + attributes);
		totalCount++;
		
		return "{\"test\":\"test1\"}";
	}
	*/
	/*
	@GetMapping(value = "testApi", produces = MediaType.APPLICATION_JSON_VALUE)
	public String testApiGet(@RequestBody Map<String, Object> attributes, HttpServletRequest request) {
		System.out.println(attributes);
		System.out.println(request.getHeader("Content-Type"));
		System.out.println(request.getHeader("Authorization"));
		return null;
	}
	*/
}

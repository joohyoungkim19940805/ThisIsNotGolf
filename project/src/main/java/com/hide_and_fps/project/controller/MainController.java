package com.hide_and_fps.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.hide_and_fps.business_logic.service.file.FileUploadService;
import com.hide_and_fps.business_logic.service.system.SyAdminService;
import com.hide_and_fps.business_logic.util.CreateRandomCodeUtil;
import com.hide_and_fps.project.room.vo.RoomVO;

@Controller
@RequestMapping("")
public class MainController {

	@Autowired
	private SyAdminService syAdminService;
	
	@Autowired
	private CreateRandomCodeUtil createRandomCodeUtil;
    
	@GetMapping("/")
    public String hello(HttpSession session) {
		System.out.println("test<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		session.setAttribute("testId", "testValue");
    	HashMap<String, Object> model = new HashMap<String, Object>();
		/*
    	SyAdminDto syAdminDto = this.syAdminService.getAdminById(1);
		
		System.out.println(syAdminDto);
		System.out.println(syAdminDto.getId());	
		*/
		model.put("syAdminDto", "test<<<");
      
		/*
		List<SyAdminDto> syAdminList = this.syAdminService.searchAdminList();
		for(SyAdminDto admin:syAdminList) {
			System.out.println(admin.getName());
		}
		//System.out.println(syAdminList);
		model.put("syAdminList", syAdminList);
		*/
		return "content/home";
    }
    
    @GetMapping(value="/PrevTargetPage")
    public String page_get(HttpServletRequest request, HttpSession session) {
    	System.out.println("다른처리");
    	Map<String, ?> redirectMap = RequestContextUtils.getInputFlashMap(request);  
    	String code = null;
    	if(redirectMap != null) {
    		code = (String) redirectMap.get("code");
    		if(code != null) {
    			
    			return "/main/page";
    		}
    	}else {
    		return "/main/page";
    	}
    	return "/main/page";

    }
    
    @PostMapping(value="/PrevTargetPage")
    public String page_post(RedirectAttributes redirectAttr) {
    	
    	System.out.println("컨트롤러 시작<<<");
    	HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("test", "test");
		
		System.out.println("jsp 페이지 이동 <<<");
		String code = createRandomCodeUtil.createCode();
		
		redirectAttr.addFlashAttribute("code", code);
		
		//return this.direct("/main/page", null);
    	return "redirect:/PrevTargetPage";
    }
    
    @RequestMapping(value="/test")
    public String test() {
    	
    	return "/main/page_222";
    }
    
    @PostMapping(value = "/fileUpload")
    @ResponseBody
    public List<Object> fileUpload(@RequestPart(value="files") List<MultipartFile> fileList) {
    	//List<Object> result = FileUploadService.fileUpload(fileList);
		
    	return null;
    }
    
    @PostMapping(value = "/chatTest")
    public String chatTest() {
    	System.out.println();
    	return "/main/chat";
    }
    
    
    @PostMapping(value = "/submitTestFetch1")
    @ResponseBody
    public String submitTestFetch(@RequestBody HashMap body) {
    	System.out.println("값 테스트 >>>" + body.get("test"));
    	
    	return body.toString();
    }
    
    
    
    @PostMapping(value = "/submitTestFetch2")
    @ResponseBody
    public String submitTestFetch(@RequestParam(value="test") String test) {
    	System.out.println("값 테스트 >>>" + test);
    	
    	return test.toString();
    }
    
    @RequestMapping(value="/chatTest_multi_access", method = {RequestMethod.POST, RequestMethod.GET})
    public String chatTest_multi_access(HttpSession session) {
    	System.out.println(session);
    	System.out.println(session.getId());
    	System.out.println(session.getAttribute("testId"));
    	return "/main/chat_multi_access";
    }
    
    @RequestMapping(value="/chat_multi_access_multi_room_gate", method = {RequestMethod.POST, RequestMethod.GET})
    public String chat_multi_access_multi_room_gate(HttpSession session, Model model) {
    	model.addAttribute("room_number", createRandomCodeUtil.createCode());
    	
    	return "content/chat_multi_access_multi_room_gate";
    }
    
    @RequestMapping(value="/chat_multi_access_multi_room", method = {RequestMethod.POST, RequestMethod.GET})
    public String chat_multi_access_multi_room(HttpSession session, Model model, @RequestParam(value="access") String access_code) {
    	model.addAttribute("access_code", access_code);
    	
    	return "content/chat_room";
    }
    
}
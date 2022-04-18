package com.hide_and_fps.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
/*
 * 이 클래스의 패키지가 최상위 루트로 지정된다.
 */

@ServletComponentScan
@SpringBootApplication 
//매핑할 패키지 경로를 지정한다. 해당 클래스 외의 패키지 경로를 매핑하고 싶을 때(Autowired bean이 안잡힐 때)
@ComponentScan(basePackages = {
								"com.hide_and_fps.project.*"
								, "com.hide_and_fps.business_logic.*"
							  })
public class projectApplication {

	public static void main(String[] args) {
		System.setProperty("spring.profiles.active", "local");
		SpringApplication.run(projectApplication.class, args);
	}

}
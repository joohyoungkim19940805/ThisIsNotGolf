package com.hide_and_fps.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
/*
 * 이 클래스의 패키지가 최상위 루트로 지정된다.
 */

@ServletComponentScan

//매핑할 패키지 경로를 지정한다. 해당 클래스 외의 패키지 경로를 매핑하고 싶을 때(Autowired bean이 안잡힐 때)
@ComponentScan(basePackages = {
								"com.hide_and_fps.project.*"
							  })
@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
		/*
		//System.setProperty("spring.profiles.active", "local");
        SpringApplication application = new SpringApplication(ProjectApplication.class);
        application.setWebApplicationType(WebApplicationType.REACTIVE); 
        // starter-web은 SERVLET으로 자동으로 선택한다. 이것을 강제로 REACTIVE로 바꾼다.
        application.run(args);
        */
	}
}
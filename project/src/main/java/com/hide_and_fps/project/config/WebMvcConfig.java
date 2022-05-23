package com.hide_and_fps.project.config;


import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import com.hide_and_fps.business_logic.util.CreateRandomCodeUtil;
import com.hide_and_fps.project.interceptor.TestIntercepter;


//import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;
//import com.middleware.fs.interceptor.TestIntercepter;

@PropertySources({
	//로컬 환경에서 사용될 파일 경로
	@PropertySource(value = "classpath:/appConfig/app-config-local.properties", ignoreResourceNotFound = true),

	//서버 환경에서 사용될 파일 경로
	@PropertySource(value = "classpath:/appConfig/app-config-${spring.profiles.active}.properties", ignoreResourceNotFound = true)
})

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	Environment env;
	
	/*
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		System.out.println(System.getProperty("user.home") + env.getProperty("system.file.uploadPath"));
        registry.addResourceHandler("/static/**")
                .addResourceLocations("/static/")
                .setCachePeriod(300);
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:///" + System.getProperty("user.home") + "/TMP" )
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }
*/
	
    @Bean
    public TestIntercepter programAccessHandler() {
        return new TestIntercepter();
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.programAccessHandler())
        		.addPathPatterns("/main/*")
        		.addPathPatterns("/PrevTargetPage");
    }
    
    @Bean
    public CreateRandomCodeUtil createRandomCodeUtil() {
    	return new CreateRandomCodeUtil();
    }
}


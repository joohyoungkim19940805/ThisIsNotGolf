package com.hide_and_fps.project.config;


import java.nio.charset.StandardCharsets;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurationSupport;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.thymeleaf.spring5.ISpringWebFluxTemplateEngine;
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;

@Configuration
public class WebFluxConfig implements ApplicationContextAware, WebFluxConfigurer {
	
	@Autowired
	private ApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
		configurer.defaultCodecs().jackson2JsonEncoder(
			new Jackson2JsonEncoder(objectMapper)
		);
		configurer.defaultCodecs().jackson2JsonDecoder(
			new Jackson2JsonDecoder(objectMapper)
		);
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = context;
	}
	
	@Bean
	public ITemplateResolver thymeleafTemplateResolver() {
	    final SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
	    resolver.setApplicationContext(this.context);
	    resolver.setPrefix("classpath:/templates/");
	    resolver.setSuffix(".html");
	    resolver.setTemplateMode(TemplateMode.HTML);
	    resolver.setCacheable(false);
	    resolver.setCheckExistence(false);
	    resolver.setCharacterEncoding("UTF-8");
	    
	    return resolver;
	}
	
	@Bean
	public ISpringWebFluxTemplateEngine thymeleafTemplateEngine() {
	    SpringWebFluxTemplateEngine templateEngine = new SpringWebFluxTemplateEngine();
	    templateEngine.setTemplateResolver(thymeleafTemplateResolver());
	    templateEngine.addDialect(new LayoutDialect());
	    return templateEngine;
	}
	
	@Bean
	public ThymeleafReactiveViewResolver thymeleafReactiveViewResolver() {
	    ThymeleafReactiveViewResolver viewResolver = new ThymeleafReactiveViewResolver();
	    viewResolver.setTemplateEngine(thymeleafTemplateEngine());

	    return viewResolver;
	}
	
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
	    registry.viewResolver(thymeleafReactiveViewResolver());
	}
	
	//@Bean
	//public LayoutDialect layoutDialect() {
	//   return new LayoutDialect();
	//}
}



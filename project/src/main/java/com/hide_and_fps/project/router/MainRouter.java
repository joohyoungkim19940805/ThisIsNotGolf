package com.hide_and_fps.project.router;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.hide_and_fps.project.handler.MainHandler;

@Configuration
public class MainRouter {
	
	
	@Bean
	public RouterFunction<ServerResponse> index1(MainHandler webFluxHandler){
		return RouterFunctions.route(RequestPredicates.GET("/")
				.and(RequestPredicates.accept(MediaType.TEXT_HTML)),
				webFluxHandler::index);
	}
	
	@Bean
	public RouterFunction<ServerResponse> index2(MainHandler webFluxHandler){
		return RouterFunctions.route(RequestPredicates.GET("/home")
				.and(RequestPredicates.accept(MediaType.TEXT_HTML)),
				webFluxHandler::mainPage);
	}
	
	@Bean
	public RouterFunction<ServerResponse> home(MainHandler webFluxHandler){
		return RouterFunctions.route(RequestPredicates.GET("/homeTest")
				.and(RequestPredicates.accept(MediaType.TEXT_HTML)),
				webFluxHandler::homeTest);
	}
	
	@Bean
	public RouterFunction<ServerResponse> twich(MainHandler webFluxHandler){
		return RouterFunctions.route(RequestPredicates.GET("/twich")//"sexy/cute/jureureu")
				.and(RequestPredicates.accept(MediaType.TEXT_HTML)),
				webFluxHandler::twich);
	}
}
package com.hide_and_fps.project.router;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.hide_and_fps.project.handler.MainHandler;

import reactor.core.publisher.Mono;

@Configuration
public class MainRouter {
	
	private AtomicInteger counter = new AtomicInteger(0);
	
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
	public RouterFunction<ServerResponse> testApi(){
		
		return RouterFunctions.route(RequestPredicates.POST("/testApi"), 
					req -> {
						System.out.println(counter.get());
						return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
							Mono.just( Map.ofEntries( Map.entry("resCount", counter.getAndIncrement()) ) )
							, Map.class);
							});
	}
}
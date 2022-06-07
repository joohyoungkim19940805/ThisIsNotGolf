package com.hide_and_fps.project.router;

import java.awt.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.hide_and_fps.project.handler.MainHandler;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
	public RouterFunction<ServerResponse> testApi(MainHandler webFluxHandler){
		return RouterFunctions.route(RequestPredicates.POST("/testApi")
				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
				webFluxHandler::testApi);
		/*
		return RouterFunctions.route(RequestPredicates.POST("/testApi"), 
					req -> {
						req.formData().subscribe(x->{
							System.out.println(x);
						});
						return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
							Mono.just( Map.ofEntries( Map.entry("resCount", counter.getAndIncrement()) ) )
							, Map.class);
							});
							*/
	}
	/*
	@Setter
	@Getter
	@ToString
	public static class User {
	    private Map<String,Object> attributes;
	}
	*/
}
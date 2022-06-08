package com.hide_and_fps.project.router;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
import reactor.core.scheduler.Schedulers;

@Configuration
public class MainRouter {
	
	private AtomicInteger counter = new AtomicInteger(0);
	private AtomicInteger reqCounter = new AtomicInteger(0);
	
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
		//for(int i ) {}
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

	@SuppressWarnings("unchecked")
	public Mono<ServerResponse> testApi(ServerRequest request){
		int i = reqCounter.getAndIncrement();
		if(i > 40000 ) {
			System.out.println("reqCounter : " + reqCounter.get());
		}
		return request.bodyToMono(JSONObject.class)
		        .publishOn(Schedulers.elastic())
		        .switchIfEmpty(Mono.error(new IllegalStateException("user required")))
		        .flatMap(attributes -> {
		            
		        	ArrayList attrArr = (ArrayList) attributes.get("attributes");
		            
		        	HashMap map = (HashMap) attrArr.parallelStream()
		            				.collect(Collectors.toMap(k-> ((Map)k).get("external_id"), v -> "", (k1, k2) -> k2));
		            int j = counter.addAndGet(map.size());
		            if(j > 3200000) {
			            System.out.println("totalCount : " + counter.get());
		            }
		            return ServerResponse.ok().build();
		        })
		        .onErrorResume(err -> {
		            err.printStackTrace();
		            
		            return ServerResponse.badRequest().build();
		        });

		
		/*
		
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
				Mono.just( Map.ofEntries( Map.entry("resCount", counter.getAndIncrement()) ) )
				, Map.class);
		 */
	}
	
}
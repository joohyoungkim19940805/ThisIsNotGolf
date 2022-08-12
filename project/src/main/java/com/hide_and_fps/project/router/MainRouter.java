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

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;


@Configuration
public class MainRouter {
	
	@Bean
	public RouterFunction<ServerResponse> index1(MainHandler webFluxHandler){
		return route(GET("/")
				.and(accept(MediaType.TEXT_HTML)),
				webFluxHandler::index);
	}
	
	@Bean
	public RouterFunction<ServerResponse> index2(MainHandler webFluxHandler){
		return route(GET("/home")
				.and(accept(MediaType.TEXT_HTML)),
				webFluxHandler::mainPage);
	}
	
	
	
	
	@Bean
	public RouterFunction<ServerResponse> aquarium(MainHandler webFluxHandler){
		return route(GET("/aquarium")
				.and(accept(MediaType.TEXT_HTML)),
				webFluxHandler::aquarium);
	}
	@Bean
	public RouterFunction<ServerResponse> home(MainHandler webFluxHandler){
		return route(GET("/homeTest")
				.and(accept(MediaType.TEXT_HTML)),
				webFluxHandler::homeTest);
	}
	@Bean
	public RouterFunction<ServerResponse> cotton__123(MainHandler webFluxHandler){
		return route(GET("/sexy_cute_jururu")
				.and(accept(MediaType.TEXT_HTML)),
				webFluxHandler::cotton__123);
	}
	@Bean
	public RouterFunction<ServerResponse> viichan6(MainHandler webFluxHandler){
		return route(GET("/viichan6")
				.and(accept(MediaType.TEXT_HTML)),
				webFluxHandler::viichan6);
	}
	@Bean
	public RouterFunction<ServerResponse> vo_ine(MainHandler webFluxHandler){
		return route(GET("/vo_ine")
				.and(accept(MediaType.TEXT_HTML)),
				webFluxHandler::vo_ine);
	}
	@Bean
	public RouterFunction<ServerResponse> gosegugosegu(MainHandler webFluxHandler){
		return route(GET("/gosegugosegu")
				.and(accept(MediaType.TEXT_HTML)),
				webFluxHandler::gosegugosegu);
	}
	@Bean
	public RouterFunction<ServerResponse> jingburger(MainHandler webFluxHandler){
		return route(GET("/jingburger")
				.and(accept(MediaType.TEXT_HTML)),
				webFluxHandler::jingburger);
	}
	@Bean
	public RouterFunction<ServerResponse> lilpaaaaaa(MainHandler webFluxHandler){
		return route(GET("/lilpaaaaaa")
				.and(accept(MediaType.TEXT_HTML)),
				webFluxHandler::lilpaaaaaa);
	}
	@Bean
	public RouterFunction<ServerResponse> woowakgood(MainHandler webFluxHandler){
		return route(GET("/woowakgood")
				.and(accept(MediaType.TEXT_HTML)),
				webFluxHandler::woowakgood);
	}
	
}
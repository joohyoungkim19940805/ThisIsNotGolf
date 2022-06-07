package com.hide_and_fps.project.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.hide_and_fps.project.entity.MainEntity;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class MainHandler {
	
	private AtomicInteger counter = new AtomicInteger(0);
	private AtomicInteger reqCounter = new AtomicInteger(0);
	
	public Mono<ServerResponse> index(ServerRequest request){
		final Map<String, MainEntity> data = new HashMap<>();
		data.put("data", new MainEntity("b", (long)10000, "trrtrtrt", (int)20, 200, 2200));
		return ServerResponse.ok().contentType(MediaType.TEXT_HTML).render("index", data);
	}
	
	public Mono<ServerResponse> homeTest(ServerRequest request){
		return ServerResponse.ok().contentType(MediaType.parseMediaType("text/html;charset=UTF-8")).render("content/homeTest", Map.ofEntries(
					Map.entry("data", new MainEntity("a", (long)9999, "asasas", (int)10, 100, 100))
				));
	}
	
	public Mono<ServerResponse> mainPage(ServerRequest request){
		return ServerResponse.ok().contentType(MediaType.parseMediaType("text/html;charset=UTF-8")).render("content/mainPage", Map.ofEntries(
					Map.entry("data", new MainEntity("a", (long)9999, "asasas", (int)10, 100, 100))
				));
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

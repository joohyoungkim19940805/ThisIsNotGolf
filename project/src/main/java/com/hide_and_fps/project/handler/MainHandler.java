package com.hide_and_fps.project.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.hide_and_fps.project.entity.MainEntity;

import reactor.core.publisher.Mono;

@Component
public class MainHandler {
	
	public Mono<ServerResponse> home(ServerRequest request){
		return ServerResponse.ok().contentType(MediaType.parseMediaType("text/html;charset=UTF-8")).render("content/home", Map.ofEntries(
					Map.entry("data", new MainEntity("a", (long)9999, "asasas", (int)10, 100, 100))
				));
	}
	
	public Mono<ServerResponse> index(ServerRequest request){
		final Map<String, MainEntity> data = new HashMap<>();
		data.put("data", new MainEntity("b", (long)10000, "trrtrtrt", (int)20, 200, 2200));
		return ServerResponse.ok().contentType(MediaType.TEXT_HTML).render("index", data);
	}
}

package com.hide_and_fps.project.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import static java.util.Map.ofEntries;
import static java.util.Map.entry;
import static java.util.List.of;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class MainHandler {
	
	private AtomicInteger counter = new AtomicInteger(0);
	private AtomicInteger reqCounter = new AtomicInteger(0);
	private Entry<String,Object> channelInfo = entry("idList", List.of(
			entry("cotton__123","주르르"),
			entry("viichan6","비챤"),
			entry("vo_ine","아이네"),
			entry("gosegugosegu","고세구"),
			entry("jingburger","징버거"),
			entry("lilpaaaaaa","릴파"),
			entry("woowakgood","우왁굳"),
			entry("111roentgenium","뢴트게늄")
			)
		);
	public Mono<ServerResponse> index(ServerRequest request){
		final Map<String, MainEntity> data = new HashMap<>();
		data.put("data", new MainEntity("b", (long)10000, "trrtrtrt", (int)20, 200, 2200));
		return ok().contentType(MediaType.TEXT_HTML).render("index", data);
	}
	public Mono<ServerResponse> mainPage(ServerRequest request){
		return ok().contentType(MediaType.parseMediaType("text/html;charset=UTF-8")).render("content/mainPage", ofEntries(
					entry("data", new MainEntity("a", (long)9999, "asasas", (int)10, 100, 100))
				));
	}
	

	
	public Mono<ServerResponse> aquarium(ServerRequest request){
		final Map<String, MainEntity> data = new HashMap<>();
		data.put("data", new MainEntity("b", (long)10000, "trrtrtrt", (int)20, 200, 2200));
		return ok().contentType(MediaType.TEXT_HTML).render("test/aquarium", data);
	}
	
	public Mono<ServerResponse> homeTest(ServerRequest request){
		return ok().contentType(MediaType.parseMediaType("text/html;charset=UTF-8")).render("content/homeTest", ofEntries(
					entry("data", new MainEntity("a", (long)9999, "asasas", (int)10, 100, 100))
				));
	}
	
	public Mono<ServerResponse> cotton__123(ServerRequest request){
		return ok().contentType(MediaType.parseMediaType("text/html;charset=UTF-8")).render("test/twich", ofEntries(
				entry("id", "cotton__123"), channelInfo
			));
	}
	public Mono<ServerResponse> viichan6(ServerRequest request){
		return ok().contentType(MediaType.parseMediaType("text/html;charset=UTF-8")).render("test/twich", ofEntries(
				entry("id", "viichan6"), channelInfo
			));
	}
	public Mono<ServerResponse> vo_ine(ServerRequest request){
		return ok().contentType(MediaType.parseMediaType("text/html;charset=UTF-8")).render("test/twich", ofEntries(
				entry("id", "vo_ine"), channelInfo
			));
	}
	public Mono<ServerResponse> gosegugosegu(ServerRequest request){
		return ok().contentType(MediaType.parseMediaType("text/html;charset=UTF-8")).render("test/twich", ofEntries(
				entry("id", "gosegugosegu"), channelInfo
			));
	}
	public Mono<ServerResponse> jingburger(ServerRequest request){
		return ok().contentType(MediaType.parseMediaType("text/html;charset=UTF-8")).render("test/twich", ofEntries(
				entry("id", "jingburger"), channelInfo
			));
	}
	public Mono<ServerResponse> lilpaaaaaa(ServerRequest request){
		return ok().contentType(MediaType.parseMediaType("text/html;charset=UTF-8")).render("test/twich", ofEntries(
				entry("id", "lilpaaaaaa"), channelInfo
			));
	}
	public Mono<ServerResponse> woowakgood(ServerRequest request){
		return ok().contentType(MediaType.parseMediaType("text/html;charset=UTF-8")).render("test/twich", ofEntries(
				entry("id", "woowakgood"), channelInfo
			));
	}
	public Mono<ServerResponse> roentgenium(ServerRequest request){
		return ok().contentType(MediaType.parseMediaType("text/html;charset=UTF-8")).render("test/twich", ofEntries(
				entry("id", "111roentgenium"), channelInfo
			));
	}
	
}

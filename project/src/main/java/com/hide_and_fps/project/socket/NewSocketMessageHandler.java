package com.hide_and_fps.project.socket;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.DonationEvent;
import com.github.twitch4j.chat.events.channel.FollowEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.hide_and_fps.project.twich.TwichEventMerged;
import com.hide_and_fps.project.vo.ClientInfoVO;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;

import static java.util.Map.entry;

@Component("ReactiveWebSocketHandler")
public class NewSocketMessageHandler implements WebSocketHandler {
	public final Map<String, Map<String, Object>> userRoom = new ConcurrentHashMap<>();
    //private static final ObjectMapper json = new ObjectMapper();
    //private TwichEventMerged twichEvent = new TwichEventMerged()
    /*
    private Map<String, Map<String, Object>> userRoomHandler(WebSocketSession webSocketSession) {
    	if(this.userRoom.containsKey(webSocketSession.getId()) == false) {
	    	this.userRoom.put(webSocketSession.getId(), Map.ofEntries( 
	    															entry("socket", webSocketSession),
	    															entry("twich", new TwichEventMerged())
	    															));
    	}
    	return this.userRoom;
    }
    */
    /*
    private Flux<String> eventFlux = Flux.generate(sink -> {
    	JSONObject data = twichEvent.messageRoom.poll();
        sink.next(data != null ? data.toJSONString() : "{}");
    });

    private Flux<String> intervalFlux = Flux.interval(Duration.ofMillis(50L))
      .zipWith(eventFlux, (time, event) -> event);

     */
    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {

    	//return ((TwichEventMerged)userRoomHandler(webSocketSession).get(webSocketSession.getId()).get("twich")).connectTwich(webSocketSession);
    	return  new TwichEventMerged().connectTwich(webSocketSession);
    	//return twichEvent.connectTwich(webSocketSession)
    			//.send(intervalFlux.map(webSocketSession::textMessage));
		          //.and(webSocketSession.receive()
		          //  .map(WebSocketMessage::getPayloadAsText).log());

    }

}
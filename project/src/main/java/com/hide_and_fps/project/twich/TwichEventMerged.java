package com.hide_and_fps.project.twich;

import static java.util.Map.entry;

import java.time.Duration;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.JSONObject;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.adapter.ReactorNettyWebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.DonationEvent;
import com.github.twitch4j.chat.events.channel.FollowEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
public class TwichEventMerged extends Bot{
	private final Queue<JSONObject> messageRoom = new ConcurrentLinkedQueue<>();
	
	private boolean isRun = false;
	private Flux<String> eventFlux = Flux.generate(sink -> {
    	JSONObject data = this.messageRoom.poll();
        sink.next(data != null ? data.toJSONString() : "{}");
    });
	private Flux<String> intervalFlux = Flux.interval(Duration.ofMillis(50L))
		      .zipWith(eventFlux, (time, event) -> event);
	private WebSocketSession session = null;
	public TwichEventMerged() {
		super();
	}
	/*
	 * 
	 */
	public Mono<Void> connectTwich(WebSocketSession webSocketSession) {
		if(isRun == false) {
			String id = webSocketSession.getHandshakeInfo().getUri().getQuery().replaceAll("=", "").replaceAll("id", "").replaceAll("\"", "");
			super.start(id);
			ChannelNotificationOnDonation();
			ChannelNotificationOnFollow();
			ChannelNotificationOnSubscription();
			WriteChannelChatToConsole();
			isRun = true;
			this.session = webSocketSession;
		}
		return webSocketSession.send(intervalFlux.map(webSocketSession::textMessage));
	}
	
    /** 도네이션 이벤트 등록
     * Register events of this class with the EventManager/EventHandler
     *
     * @param eventHandler SimpleEventHandler
     */
    private void ChannelNotificationOnDonation() {
        super.getTwichEventHandler().onEvent( DonationEvent.class, event -> messageRoom.offer(onDonation(event)) );
    }
    public JSONObject onDonation(DonationEvent event) {
    	if(session != null && session.isOpen() == false) {
    		super.close();
    		isRun = false;
    	}
    	return new JSONObject(Map.ofEntries(Map.entry("donation", Map.ofEntries(
				/* *
				 * user : 도네를 한 유저
				 * source : 도네이션의 소스
				 * currency : 도네이션의 화폐
				 * amount : 도네이션을 한 가격
				 * message : 도네이션의 메시지
				 * channel : 도네이션의 채널 명
				 * */
					entry("user", event.getUser().getName()), 
					entry("amount", event.getAmount()),
					entry("message", event.getMessage()),
					entry("channel", event.getChannel().getName())
					))
				));
    }
    
    /** 팔로우 이벤트 등록
     * Register events of this class with the EventManager/EventHandler
     *
     * @param eventHandler SimpleEventHandler
     */
    private void ChannelNotificationOnFollow() {
    	super.getTwichEventHandler().onEvent( FollowEvent.class, event -> messageRoom.offer(onFollow(event)) );
    }
    public JSONObject onFollow(FollowEvent event) {
    	if(session != null && session.isOpen() == false) {
    		super.close();
    	}
    	return new JSONObject(Map.ofEntries(Map.entry("follow", Map.ofEntries(
				/* *
				 * user : 팔로우를 한 유저
				 * channel : 팔로우 한 채널 명칭
				 * */
						entry("user", event.getUser().getName()),
						entry("channel", event.getChannel().getName())
					))
				));
    }
    
    /** 구독 이벤트 등록
     * Register events of this class with the EventManager/EventHandler
     *
     * @param eventHandler SimpleEventHandler
     * @return 
     */
    public void ChannelNotificationOnSubscription() {
    	super.getTwichEventHandler().onEvent(SubscriptionEvent.class, event -> messageRoom.offer(onSubscription(event)) );
    }
    public JSONObject onSubscription(SubscriptionEvent event) {
    	if(session != null && session.isOpen() == false) {
    		super.close();
    	}
    	return new JSONObject(Map.ofEntries(Map.entry("subscription", Map.ofEntries(
				/* *
				 * user : 구독을를 한 유저
				 * messageEvent : 원시 메시지 데이터 중 메시지 ?
				 * subscriptionPlan : 구독 계획 ? 비용?
				 * message : 구독 메시지
				 * months : 구독 개월 수 (연속 x 누적 O)
				 * gifted : 선물 받았는지 여부
				 * giftedBy: 선물 한 사용자
				 * subStreak : 구독을 연속한 개월 수 (누적 x 연속 O)
				 * giftMonths : 단일 또는 여러 달의 선물의 일부로서, 선물받은 개월 수
				 * multiMonthDuration : 구매한 구독 개월 수
				 * multiMonthTenure : 이미 제공된 여러 달 구독 기간의 길이 ?
				 * flags : ??
				 * channel : 구독 한 채널 명칭
				 * */
						entry("user", event.getUser().getName()),
						entry("subscriptionPlan", event.getSubscriptionPlan()),
						entry("message", event.getMessage().isPresent() ? event.getMessage().get() : ""),
						entry("months", event.getMonths()),
						entry("subStreak", event.getSubStreak()),
						entry("flags", event.getFlags()),
						entry("channel", event.getChannel().getName())
					))
				));
    }
    
    /** 채팅 이벤트 등록
     * Register events of this class with the EventManager/EventHandler
     *
     * @param eventHandler SimpleEventHandler
     * @return 
     */
    public void WriteChannelChatToConsole() {
    	super.getTwichEventHandler().onEvent(ChannelMessageEvent.class, event -> messageRoom.offer(onChannelMessage(event)) );
    }
    public JSONObject onChannelMessage(ChannelMessageEvent event) {
    	if(session != null && session.isOpen() == false) {
    		super.close();
    	}
    	return new JSONObject(Map.ofEntries(Map.entry("chat", Map.ofEntries(
				entry("user", event.getUser().getName()),
				entry("message", event.getMessage()),
				entry("firedAt", event.getFiredAt().getTimeInMillis())
			))
		));
    }
}

package com.hide_and_fps.project.twich;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.JSONObject;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.DonationEvent;
import com.github.twitch4j.chat.events.channel.FollowEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
public abstract class TwichEventMerged extends Bot {
	
	private boolean isRun = false;
	public TwichEventMerged() {
		super();
	}
	/*
	 * 
	 */
	public WebSocketSession connectTwich(WebSocketSession webSocketSession) {
		if(isRun == false) {
			super.start();
			ChannelNotificationOnDonation();
			ChannelNotificationOnFollow();
			ChannelNotificationOnSubscription();
			WriteChannelChatToConsole();
			
			isRun = true;
		}else if(webSocketSession.getHandshakeInfo().getUri().getPath().equals("/room/newChannel")){
			super.setBot().newSetClient(UriComponentsBuilder.fromUri(webSocketSession.getHandshakeInfo().getUri()).build().getQueryParams().get("channel").get(0));
		}

		return webSocketSession;
	}
	
	public final Queue<JSONObject> messageRoom = new ConcurrentLinkedQueue<>();

	
    /** 도네이션 이벤트 등록
     * Register events of this class with the EventManager/EventHandler
     *
     * @param eventHandler SimpleEventHandler
     */
    private void ChannelNotificationOnDonation() {
        super.getTwichEventHandler().onEvent( DonationEvent.class, event -> messageRoom.offer(onDonation(event)) );
    }
    public abstract JSONObject onDonation(DonationEvent event);
    
    /** 팔로우 이벤트 등록
     * Register events of this class with the EventManager/EventHandler
     *
     * @param eventHandler SimpleEventHandler
     */
    private void ChannelNotificationOnFollow() {
    	super.getTwichEventHandler().onEvent( FollowEvent.class, event -> messageRoom.offer(onFollow(event)) );
    }
    public abstract JSONObject onFollow(FollowEvent event);
    
    /** 구독 이벤트 등록
     * Register events of this class with the EventManager/EventHandler
     *
     * @param eventHandler SimpleEventHandler
     * @return 
     */
    public void ChannelNotificationOnSubscription() {
    	super.getTwichEventHandler().onEvent(SubscriptionEvent.class, event -> messageRoom.offer(onSubscription(event)) );
    }
    public abstract JSONObject onSubscription(SubscriptionEvent event);
    
    /** 채팅 이벤트 등록
     * Register events of this class with the EventManager/EventHandler
     *
     * @param eventHandler SimpleEventHandler
     * @return 
     */
    public void WriteChannelChatToConsole() {
    	super.getTwichEventHandler().onEvent(ChannelMessageEvent.class, event -> messageRoom.offer(onChannelMessage(event)) );
    }
    public abstract JSONObject onChannelMessage(ChannelMessageEvent event);
}

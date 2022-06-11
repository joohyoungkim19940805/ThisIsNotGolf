package com.hide_and_fps.project.socket;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.DonationEvent;
import com.github.twitch4j.chat.events.channel.FollowEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.hide_and_fps.project.twich.TwichEventMerged;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

import static java.util.Map.entry;

@Component("ReactiveWebSocketHandler")
public class NewSocketMessageHandler implements WebSocketHandler {

    //private static final ObjectMapper json = new ObjectMapper();
    private TwichEventMerged twichEvent = new TwichEventMerged() {
		@Override
		public JSONObject onDonation(DonationEvent event) {
			System.out.println("Donation!!!!!!");
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

		@Override
		public JSONObject onFollow(FollowEvent event) {
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

		@Override
		public JSONObject onSubscription(SubscriptionEvent event) {
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

		@Override
		public JSONObject onChannelMessage(ChannelMessageEvent event) {
			return new JSONObject(Map.ofEntries(Map.entry("chat", Map.ofEntries(
							entry("user", event.getUser().getName()),
							entry("message", event.getMessage()),
							entry("firedAt", event.getFiredAt().getTimeInMillis())
						))
					));
		}
    };
    private Flux<String> eventFlux = Flux.generate(sink -> {
    	JSONObject data = twichEvent.messageRoom.poll();
        sink.next(data != null ? data.toJSONString() : "{}");
    });

    private Flux<String> intervalFlux = Flux.interval(Duration.ofMillis(50L))
      .zipWith(eventFlux, (time, event) -> event);

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
    	return twichEvent.connectTwich(webSocketSession)
    			.send(intervalFlux
		          .map(webSocketSession::textMessage));
		          //.and(webSocketSession.receive()
		          //  .map(WebSocketMessage::getPayloadAsText).log());

    }

}
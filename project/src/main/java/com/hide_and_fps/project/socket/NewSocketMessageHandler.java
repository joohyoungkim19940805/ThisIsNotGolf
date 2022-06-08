package com.hide_and_fps.project.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hide_and_fps.project.twich.Bot;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;

@Component("ReactiveWebSocketHandler")
public class NewSocketMessageHandler implements WebSocketHandler {


    private static final ObjectMapper json = new ObjectMapper();
    private Bot twichBot = new Bot();

    private Flux<String> eventFlux = Flux.generate(sink -> {

        try {
            sink.next(json.writeValueAsString(
            		 Map.ofEntries(
                 			Map.entry(randomUUID().toString(), now().toString())
                 		)
        			));
        } catch (JsonProcessingException e) {
            sink.error(e);
        }
    });

    private Flux<String> intervalFlux = Flux.interval(Duration.ofMillis(1000L))
      .zipWith(eventFlux, (time, event) -> event);

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.send(intervalFlux
          .map(webSocketSession::textMessage))
          .and(webSocketSession.receive()
            .map(WebSocketMessage::getPayloadAsText).log());
    }

}
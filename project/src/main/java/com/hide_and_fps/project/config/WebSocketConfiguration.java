package com.hide_and_fps.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

	/**시그널링 서버 구현, 클라이언트가 WebSocket 연결로 등록할 엔드포인트를 만든다.
	 * 
	 */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketMssageHandler(), "/{room}")
          		.setAllowedOrigins("*");
    }
}
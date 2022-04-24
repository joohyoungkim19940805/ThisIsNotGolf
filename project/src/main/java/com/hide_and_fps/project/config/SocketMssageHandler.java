package com.hide_and_fps.project.config;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.hide_and_fps.business_logic.vo.clientInfo.ClientInfoVO;

import static java.util.Map.entry;

@Component
public class SocketMssageHandler extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(SocketMssageHandler.class);
	
	//message를 수신한 사용자 리스트
    List<String> sessionIdList = new CopyOnWriteArrayList<>();    
    
    private final Map<String, WebSocketSession> receptionSessionsMap = new ConcurrentHashMap<>();

    private int sendTimeLimit = (int)TimeUnit.SECONDS.toMillis(10);
    
    private int sendBufferSizeLimit = 512 * 1024;
    
					    
    //발송한 사람의 session을 제외한 receptionSessionsList(수신 유저 리스트)에 message를 전달한다.
    @Override
    public void handleTextMessage(WebSocketSession sendSession, TextMessage message) throws InterruptedException, IOException {
    	sessionIdList.parallelStream().forEach(sessionId-> {
    		logger.debug(message.getPayload());
			WebSocketSession webSocketSession = receptionSessionsMap.get(sessionId);
            if (sendSession.getId().equals(webSocketSession.getId()) == false) {
                	sendMessage(webSocketSession, message);
            }

            /*
            TextMessage message2 = new TextMessage("{\"event\":\"serverMessage\",\"serverMin\":\"전체 메시지 테스트입니다.\"}".getBytes());
            webSocketSession.sendMessage(message2);
            */
    	});

    	//new ConcurrentWebSocketSessionDecorator ();

    }
    
    //메시지를 수신한 세션 값을 add한다.
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    			
    	//새로운 접속자에게 자기자신의 클라이언트 정보 넘겨주기
    	byte[] clientInfo = setClientInfoTemplate(session, "user").getBytes();
		sendMessage(session, new TextMessage(clientInfo));
		
		// 새로운 접속자에게 기존에 방에 접속 중인 유저들 정보 넘겨주기
		sendMessage(session, new TextMessage( createRoomAccessUsersInfo().getBytes()) );
    	
		// 기존에 접속 중이던 유저들에게 새로운 유저 정보 넘겨주기
		byte[] newUserClientInfo = setClientInfoTemplate(session, "new_access").getBytes();
    	sessionIdList.parallelStream().forEach(sessionId-> {
			sendMessage(receptionSessionsMap.get(sessionId), new TextMessage(newUserClientInfo));
		});
		sessionIdList.add(session.getId());
    	receptionSessionsMap.put(session.getId(), new ConcurrentWebSocketSessionDecorator (session, sendTimeLimit, sendBufferSizeLimit));
    	super.afterConnectionEstablished(session);
    }
    
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		outRoomUserRemove(session);
	}
	
	@Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
		outRoomUserRemove(session);
    }
	
	private void sendMessage(WebSocketSession session,TextMessage message) {
		try {
			session.sendMessage(message);
		}catch(IOException e) {
			try {
				if(session.isOpen()) {
					sendMessage(session, message);
				}else {
					outRoomUserRemove(session);
					session.close();
				}
			} catch (IOException e1) {
				session = null;
			}
		}
	}
	
	private void outRoomUserRemove(WebSocketSession session) {
		sessionIdList.remove(session.getId());
		receptionSessionsMap.remove(session.getId());
		byte[] deleteUserInfo = setClientInfoTemplate(session, "delete").getBytes();

		sessionIdList.parallelStream().forEach(sessionId-> {
			sendMessage(receptionSessionsMap.get(sessionId), new TextMessage(deleteUserInfo));
		});
	}
	
	public String createRoomAccessUsersInfo() throws Exception{
		String roomAccessUsess = """
				{
					"data" : [
												%s
							],
					"event" : "user_list"
				}
				""";
		String users = sessionIdList.parallelStream()
											.map(sessionId ->  {
												WebSocketSession session = receptionSessionsMap.get(sessionId);
												return setClientInfoTemplate(session, "user_list");
											})
											.collect(Collectors.joining(","));
		return roomAccessUsess.formatted( users );
	}
	public String setClientInfoTemplate(WebSocketSession session, String event_type) {
		
		return new ClientInfoVO(Map.ofEntries(
				entry("client_id",session.getId())
				, entry("client_room_url", session.getUri().getPath())
				, entry("access_time", new Date().getTime())
				, entry("event",event_type)
				, entry("access_user", sessionIdList.size())
		)).getClientInfo();
	}
}
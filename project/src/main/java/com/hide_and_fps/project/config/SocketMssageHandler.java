package com.hide_and_fps.project.config;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.hide_and_fps.project.room.vo.RoomVO;
import com.hide_and_fps.project.user.vo.ClientInfoVO;

import static java.util.Map.entry;

@Component
public class SocketMssageHandler extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(SocketMssageHandler.class);
	
    private final RoomVO room = new RoomVO();
    
    private int sendTimeLimit = (int)TimeUnit.SECONDS.toMillis(10);
    
    private int sendBufferSizeLimit = 512 * 1024;
    
					    
    //발송한 사람의 session을 제외한 receptionSessionsList(수신 유저 리스트)에 message를 전달한다.
    @Override
    public void handleTextMessage(WebSocketSession sendSession, TextMessage message) throws InterruptedException, IOException {
    	//sessionIdList.parallelStream().forEach(sessionId-> {
    	if(sendSession.isOpen()) {
	    	room.get( sendSession.getUri().getPath() ).parallelStream().forEach(client ->{
	    		//logger.debug(message.getPayload());
	            if (sendSession.getId().equals(client.getClientId()) == false) {
	                	sendMessage(client.getSessionDecorator(), message);
	            }
	
	            /*
	            TextMessage message2 = new TextMessage("{\"event\":\"serverMessage\",\"serverMin\":\"전체 메시지 테스트입니다.\"}".getBytes());
	            webSocketSession.sendMessage(message2);
	            */
	    	});
    	}

    	//new ConcurrentWebSocketSessionDecorator ();

    }
    
    //메시지를 수신한 세션 값을 add한다.
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    	ClientInfoVO clientInfoVo = setClientInfoTemplate(session);
    	if(session.getUri().getPath().equals("/game")) {
    		String roomIdWaiting = room.accessRoom();
    		if(roomIdWaiting.isEmpty() == false) {
	    		String roomInfo = """
	    				{
	    				"data":{"access":"%s"},"event":"access_room"
	    				}""".formatted(roomIdWaiting);
	    		sendMessage(session, new TextMessage(roomInfo));
    		}
    	}else {
        	//접속자 데이터 생성
        	room.settingRoom(session.getUri().getPath(), clientInfoVo);
        	
	    	//새로운 접속자에게 자기자신의 클라이언트 정보 넘겨주기
			sendMessage(clientInfoVo.getSessionDecorator(), new TextMessage(clientInfoVo.changeEventType("user")));
			
			// 기존에 접속 중이던 유저들에게 새로운 유저 정보 넘겨주기
			String newUserClientInfo = clientInfoVo.changeEventType("new_access");
			room.get(session.getUri().getPath()).parallelStream().forEach(client -> {
				if(session.getId().equals(client.getClientId()) == false) {
					sendMessage(client.getSessionDecorator(), new TextMessage( newUserClientInfo ));
				}
				//sendMessage(receptionSessionsMap.get(sessionId), new TextMessage(newUserClientInfo));
			});
			
			// 새로운 접속자에게 기존에 방에 접속 중인 유저들 정보 넘겨주기
			sendMessage(clientInfoVo.getSessionDecorator(), new TextMessage( createRoomAccessUsersInfo(clientInfoVo)) );
	    	
			//sessionIdList.add(session.getId());
	    	//receptionSessionsMap.put(session.getId(), new ConcurrentWebSocketSessionDecorator (session, sendTimeLimit, sendBufferSizeLimit));
	    	super.afterConnectionEstablished(session);
    	}
    }
    
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		sendOutUserInfo(session);
	}
	
	@Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) {
		sendOutUserInfo(session);
    }
	
	private void sendMessage(WebSocketSession session,TextMessage message) {
		try {
			if(session.isOpen()) {
				session.sendMessage(message);
			}
		}catch(IOException e) {
			if(session.isOpen()) {
				sendMessage(session, message);
			}else {
				sendOutUserInfo(session);
			}
		}
	}
	
	private void sendOutUserInfo(WebSocketSession session) {
    	if(session.getUri().getPath().equals("/game")) {
    		System.out.println(session.getUri().getPath());
    	}else {
			try {
					ClientInfoVO client = room.outRoomRemoveUser(session);
					if(client != null) {
						String deleteUserInfo = client.changeEventType("delete");
								//setClientInfoTemplate(session, "delete").getClientInfoToByte();
						List<ClientInfoVO> clientList = room.get(session.getUri().getPath());
						Optional.ofNullable(clientList)
							    .orElseGet(Collections::emptyList)
							    .stream()
							    .forEach(notOutRoomClient-> {
									if(client.getClientId().equals(notOutRoomClient.getClientId()) == false) {
										sendMessage(notOutRoomClient.getSessionDecorator() , new TextMessage(deleteUserInfo));
									}
								});
						
					}
				}catch(NotFoundException e){
					logger.error("error<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", e);
				}/*
				session.close();
			} catch (IOException e) {
				session = null;
			} catch (NotFoundException e) {
				try{
					session.close();
				} catch (IOException e1) {
					session = null;
				}
			}*/
    	}
	}
	
	public String createRoomAccessUsersInfo(ClientInfoVO clientInfoVo) {
		String roomAccessUsess = """
				{
					"data" : [
												%s
							],
					"event" : "user_list"
				}
				""";
		String users= room.get(clientInfoVo.getClient_room_url()).parallelStream()
											.filter(x-> x.getClientId().equals(clientInfoVo.getClientId()) == false)
											.map(client ->  {
												return client.changeEventType("user_list");
												//WebSocketSession session = receptionSessionsMap.get(sessionId);
												//return setClientInfoTemplate(session, "user_list").getSendClientInfoTemplate();
											})
											.collect(Collectors.joining(","));
		return roomAccessUsess.formatted( users );
	}
	public ClientInfoVO setClientInfoTemplate(WebSocketSession session) {
		
		return new ClientInfoVO(Map.ofEntries(
				entry("client_id",session.getId())
				, entry("client_room_url", session.getUri().getPath())
				, entry("access_time", new Date().getTime())
				, entry("event","")
				, entry("access_user", room.roomUserCount(session.getUri().getPath()))
				, entry("sessionDecorator", new ConcurrentWebSocketSessionDecorator (session, sendTimeLimit, sendBufferSizeLimit))
		));
	}
}
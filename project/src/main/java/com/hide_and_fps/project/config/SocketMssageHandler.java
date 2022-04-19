package com.hide_and_fps.project.config;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.Arrays;
import java.util.Collections;
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

@Component
public class SocketMssageHandler extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(SocketMssageHandler.class);
	
	//message를 수신한 사용자 리스트
    List<String> sessionIdList = new CopyOnWriteArrayList<>();    
    
    private final Map<String, WebSocketSession> receptionSessionsMap = new ConcurrentHashMap<>();

    private int sendTimeLimit = (int)TimeUnit.SECONDS.toMillis(10);
    
    private int sendBufferSizeLimit = 512 * 1024;
    
    private final String eventClientMsgTemplate = """
											{
												"client_info" : 
																{
																	"client_id" : "%s" ,
																	"client_room_url" : "%s"
																	"access_time" : "%d"
																},
												"event" : "%s",
												"access_user" : %d
											}""";
    private final List<String> createEventClientMsgTemplate = Arrays.asList( eventClientMsgTemplate.replaceAll("[{}]", "").split("\n") );
									    
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
    			
    	byte[] clientInfo = eventClientMsgTemplate.formatted(session.getId(), session.getUri().getPath(), "user", sessionIdList.size()).getBytes();
		sendMessage(session, new TextMessage(clientInfo));
		sendMessage(session, new TextMessage( createRoomAccessUsersInfo().getBytes()) );
    	byte[] newUserClientInfo = eventClientMsgTemplate.formatted(session.getId(), session.getUri().getPath(), "new_access", sessionIdList.size()).getBytes();
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
		byte[] deleteUserInfo = eventClientMsgTemplate.formatted(session.getId(), session.getUri().getPath(), "delete", sessionIdList.size()).getBytes();
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
												return eventClientMsgTemplate.formatted(session.getId(), session.getUri(), "user_list", sessionIdList.size());
											})
											.collect(Collectors.joining(","));
		return roomAccessUsess.formatted( users );
	}
	
	@SuppressWarnings("unchecked")
	private String createEventClientMsgTemplate(Map data) {
		return eventClientMsgTemplate.formatted(
					Arrays.stream( eventClientMsgTemplate.split("\n") )
						.map(str -> {
								return ((Entry<String, Object>) data.entrySet().stream().filter(entry -> {
									return str.contains((CharSequence) ((Entry<String, String>) entry).getKey());
											}).findFirst()
											.orElseGet(() -> Map.entry("a", "a")) )
											.getValue().toString();
						}).toArray()
				);
	}
	
	public static void main(String a[]) {
		String eventClientMsgTemplate = """
										{
											"client_info" : 
															{
																"client_id" : "%s" ,
																"client_room_url" : "%s",
																"test" : "%s",
																"access_time" : "%s"
															},
											"event" : "%s",
											"access_user" : %s
										}""";
		List<String> createEventClientMsgTemplate = Arrays.asList( eventClientMsgTemplate.replaceAll("[{}\t,\" ]", "").stripLeading().split("\n") );
		Object qq[] = {"1","2",3,"4",5};
		//String test = eventClientMsgTemplate.formatted(qq);
		
		Map data = Map.ofEntries(
				Map.entry("client_id", "idVal"),
				Map.entry("access_time", 9999999),
				Map.entry("event", "eventName"),
				Map.entry("access_user", 5),
				Map.entry("test", "test"),
				Map.entry("client_room_url", "urlVal")
				);
		System.out.println(data);
		/*
		Object test2[] = Arrays.asList( eventClientMsgTemplate.split("\n") ).parallelStream()
						.map(str -> {
								return ((Entry<String, Object>) data.entrySet().parallelStream().filter(entry -> {
									System.out.println(str);
									return str.contains((CharSequence) ((Entry<String, String>) entry).getKey());
											}).findFirst()
										.orElseGet(() -> Map.entry("", "")) )
										.getValue().toString();
						})
						.filter(values -> values.isBlank()==false)
						.toArray();
		*/
		Object test2[] = createEventClientMsgTemplate.parallelStream()
				.map(str -> {
						return ((Entry<String, Object>) data.entrySet().parallelStream().filter(entry -> {
							System.out.println(str);
							return str.contains((CharSequence) ((Entry<String, String>) entry).getKey());
									}).findFirst()
								.orElseGet(() -> Map.entry("", "")) )
								.getValue().toString();
				})
				.filter(values -> values.isBlank()==false)
				.toArray();
		
		System.out.println(eventClientMsgTemplate.formatted( test2 ));

		createEventClientMsgTemplate.forEach(System.out::println);

	}
}
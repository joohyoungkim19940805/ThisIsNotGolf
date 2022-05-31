package com.hide_and_fps.project.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.ibatis.javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import com.hide_and_fps.project.room.vo.RoomVO;
import com.hide_and_fps.project.user.vo.ClientInfoVO;

import static java.util.Map.entry;

/**
 * 소켓 메시지 핸들링 하는 클래스
 * @author joohyoung.kim
 * @see extends : org.springframework.web.socket.handler.TextWebSocketHandler
 * <pre>
 * 		use end point soket url : 'room/game'
 * 		use end point soket url : 'room/roomId?access=${roomIdNumber}'
 * 		example : RtcManager.js 39line ~ 89line (this.socket.onmessage)
 * </pre>
 * @version 1.0
 */
@Component
public class SocketMssageHandler extends TextWebSocketHandler {
	
	/**
	 * 로거 : SocketMssageHandler.class
	 */
	private static final Logger logger = LoggerFactory.getLogger(SocketMssageHandler.class);
	
	/**
	 * room 내 데이터 핸들링을 위한 클래스로 이 클래스는 반드시 싱글톤으로 사용 할 것
	 */
    private final RoomVO room = new RoomVO();
    
    /**
     * 전송 시간 제한
     */
    private int sendTimeLimit = (int)TimeUnit.SECONDS.toMillis(10);
    
    /**
     * 전송 버퍼 크기 제한
     */
    private int sendBufferSizeLimit = 512 * 1024;

    /**
     * room 접속 시 queryString에 들어가는 parameter의 키값
     */
    private String roomIdKey = "access";
    
    /**
     * 발송한 사람의 session을 제외한 receptionSessionsList(수신 유저 리스트)에 message를 전달한다.
     * @param sendSession 
     * 			메시지를 전송한 사람의 session
     * @param message
     * 			전송한 메시지의 내용 
     * @author joohyoung.kim
     * @version 1.0
     */
    @Override
    public void handleTextMessage(WebSocketSession sendSession, TextMessage message) throws InterruptedException, IOException {
    	if(sendSession.isOpen()) {
	    	room.get( getQueryMap( sendSession ).get(0) ).stream().forEach(client ->{
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

    }
    
    /**
     * 사용자가 웹 소켓을 연결할 때 최조 실행되는 method로 2개의 endPoint 사용 중 (/room/game, /room/roomId(매개변수 access=${roomIdNumber}))
     * @param session
     * 			소켓 연결한 유저의 session
     * @author joohyoung.kim
     * @version 1.0
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

	    //System.out.println( parameters.get("access") );
    	
    	if(session.getUri().getPath().equals("/room/game")) 
    	{
    		roomGameEndPoint(session);
    	}
    	else if(session.getUri().getPath().equals("/room/roomId")) 
    	{
    	    ClientInfoVO clientInfoVo = setClientInfoTemplate(session);
    	    roomRoomIdAccessEndPoint(session, clientInfoVo);
    	}
    }
    
    /**
     * 사용자가 웹 소켓을 연결할 때 room/game end point를 처리하는 함수
     * @param session : 연결자의 session
     * @author joohyoung.kim
     * @version 1.0
     */
    private void roomGameEndPoint(WebSocketSession session) {
		String roomIdWaiting = room.getWaitingRoomId();
		if(roomIdWaiting.isEmpty() == false) 
		{
    		String roomInfo = """
    				{
    				"data":{"access":"%s"},"event":"access_room"
    				}""".formatted(roomIdWaiting);
    		sendMessage(session, new TextMessage(roomInfo));
		}
    }
    /**
     * 사용자가 웹 소켓을 연결할 때 room/roomId?access=${roomIdNumber} end point를 처리하는 함수
     * @param session : 연결자의 session
     * @param clientInfoVo : 클라이언트에게 넘겨줄 접속자 자기 자신의 클라이언트 정보를 set해서 넘겨준다.
     * @author joohyoung.kim
     * @version 1.0
     */
    private void roomRoomIdAccessEndPoint(WebSocketSession session, ClientInfoVO clientInfoVo) throws Exception {
    	
    	// room이 가득 차서 접속 실패했을 경우
    	if(room.accessRoom(getQueryMap( session ).get(0), clientInfoVo) == false) 
    	{
    		String roomIdWaiting = room.getWaitingRoomId();
    		if(roomIdWaiting.isEmpty() == false) {
	    		String roomInfo = """
	    				{
	    				"data":{"access":"%s"},"event":"access_room"
	    				}""".formatted(roomIdWaiting);
	    		sendMessage(session, new TextMessage(roomInfo));
    		}
    	}
    	else 
    	{
    		//새로운 접속자에게 자기자신의 클라이언트 정보 넘겨주기
    		sendMessage(clientInfoVo.getSessionDecorator(), new TextMessage(clientInfoVo.changeEventType("user")));
    		
    		// 기존에 접속 중이던 유저들에게 새로운 유저 정보 넘겨주기
    		String newUserClientInfo = clientInfoVo.changeEventType("new_access");
    		room.get(getQueryMap( session ).get(0)).parallelStream().forEach(client -> {
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
    
    /**
     * 접속자가 web socket 연결을 끊을시 실행되는 함수
     * @param session : 연결을 끊은 접속자의 session
     * @param status : 끊어진 접속의 status 값 status 값은 see doc 참조
     * @see org.springframework.web.socket.CloseStatus CloseStatus 클래스의 상수값 내용 참조
     * @author joohyoung.kim
     * @version 0.9
     */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		sendOutUserInfo(session);
	}
	
    /**
     * web soket에서 모종의 이유로 오류가 난 경우로 Override로 구현 된 내용은 사용자는 out 시키게 하고 있다.
     * @param session : 오류가 난 접속자의 session
     * @param throwable : 
     * @see java.lang.Throwable Throwable
     * @author joohyoung.kim
     * @version 0.1
     */
	@Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) {
		sendOutUserInfo(session);
    }
	
    /**
     * session 상태에 따라 target이 되는 session에 message를 전달하는 함수
     * @param session : message를 전달할 target이 되는 session
     * @param message : 전달 할 message
     * @author joohyoung.kim
     * @version 0.1
     */
	private void sendMessage(WebSocketSession session, TextMessage message) {
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
	
    /**
     * target이 되는 session 유저의 소켓 연결을 해제하고 room에서 out 시키는 함수
     * @param session : 연결을 끊을 접속자의 session
     * @author joohyoung.kim
     * @version 0.9
     */
	private void sendOutUserInfo(WebSocketSession session) {
    	if(session.getUri().getPath().equals("/room/game")) {
    		System.out.println(session.getUri().getPath());
    	}else {
			try {
					ClientInfoVO client = room.outRoomRemoveUser(session);
					if(client != null) {
						String deleteUserInfo = client.changeEventType("delete");
								//setClientInfoTemplate(session, "delete").getClientInfoToByte();
						List<ClientInfoVO> clientList = room.get(getQueryMap( session ).get(0));
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
	
    /**
     * 현재 room에 접속 중인 유저들의 client info를 해당 룸에 room에 최초 접속하는 유저에게 전달해주기 위해 접속 중인 유저들의 정보를 json 형태로 만들어주는 함수
     * @param clientInfoVo : 접속을 시도한 유저의 클라이언트 정보
     * @return String : 접속 중인 유저들의 정보로 json String 형태
     * @author joohyoung.kim
     * @version 1.0
     */
	public String createRoomAccessUsersInfo(ClientInfoVO clientInfoVo) {
		String roomAccessUsess = """
				{
					"data" : [
												%s
							],
					"event" : "user_list"
				}
				""";
		String users = room.get(clientInfoVo.getClient_room_url()).parallelStream()
											.filter(x-> x.getClientId().equals(clientInfoVo.getClientId()) == false)
											.map(client ->  {
												return client.changeEventType("user_list");
												//WebSocketSession session = receptionSessionsMap.get(sessionId);
												//return setClientInfoTemplate(session, "user_list").getSendClientInfoTemplate();
											})
											.collect(Collectors.joining(","));
		return roomAccessUsess.formatted( users );
	}
	
    /**
     * session에 해당하는 접속자의 클라이언트 정보를 생성하여 반환하는 함수
     * @param session : 클라이언트 정보를 발급받을 session
     * @return ClientInfoVO : 해당 session의 클라이언트 정보
     * @author joohyoung.kim
     * @version 1
     */
	public ClientInfoVO setClientInfoTemplate(WebSocketSession session) {
		
		return new ClientInfoVO(Map.ofEntries(
				entry("client_id",session.getId())
				, entry("client_room_url", getQueryMap( session ).get(0))
				, entry("access_time", new Date().getTime())
				, entry("event","")
				, entry("access_user", room.roomUserCount( getQueryMap( session ).get(0) ))
				, entry("sessionDecorator", new ConcurrentWebSocketSessionDecorator (session, sendTimeLimit, sendBufferSizeLimit))
		));
	}
	
    /**
     * RoomVO 객체의 getWaitingRoomId 함수로 발급받아 방에 접속 요청을 할 때 room/roomId?access=${roomIdNumber}에서 access queryString parameter를 가져오는 함수
     * @param targetSession : 접근 요청자의 session
     * @return List<String> : access의 값을 list 형태로 담아서 리턴
     * @author joohyoung.kim
     * @version 0.9
     */
	private List<String> getQueryMap(WebSocketSession targetSession) {
		MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUri(targetSession.getUri()).build().getQueryParams();
	    return parameters.get(roomIdKey);
	}
	
	/*
	public static void main(String args[]) {
		final List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40);
		final AtomicInteger counter = new AtomicInteger(0);
		
		System.out.println(
				list.parallelStream().filter(e->e%2==0).collect(Collectors.groupingBy(s -> counter.getAndIncrement()/7))
				);
	
		Map map = list.parallelStream().collect(Collectors.groupingBy(s -> {
			if(s % 2 == 0) {
				return counter.getAndIncrement()/7;
			}else {
				return "a";
			}
		}));
		map.remove("a");
		System.out.println(
						map
				);
	}
	*/
}
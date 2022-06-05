package com.hide_and_fps.project.vo;

import static java.util.Map.entry;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.ibatis.javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.util.UriComponentsBuilder;

import com.hide_and_fps.project.config.SocketMssageHandler;
import com.hide_and_fps.project.config.CreateRandomCodeUtil;

/**
 * room 내 데이터를 핸들링하는 클래스로 이 클래스는 싱글톤으로만 사용하도록 한다.
 * @author joohyoung.kim
 * @see 
 * <pre>
 * 		RoomVO room = new RoomVO() //( Use Singleton pattern ) 
 * </pre>
 * @version 1.0
 */
public class RoomVO extends ConcurrentHashMap<String, CopyOnWriteArrayList<ClientInfoVO>>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5921574994014358062L;
	
	/**
	 * 랜덤 코드 발급시 사용하는 객체
	 */
	private final CreateRandomCodeUtil randomCode = new CreateRandomCodeUtil();	
	
	/**
	 * 읽기 전용으로 미리 접속 대기 시킬 룸 리스트를 만들어놓는다. (현재 defult 100개 할당)
	 */
	private final CopyOnWriteArrayList<String> roomWaiting = new CopyOnWriteArrayList<>();
	
	/**
	 * 로거 : RoomVO.class
	 */
	private static final Logger logger = LoggerFactory.getLogger(RoomVO.class);
	
    /**
     * RoomVO의 생성자로 roomWaiting의 빈룸 100개를 생성하고 싱글 스레드로 room의 대기 상태 객체를 감시하는  반복 루프를 실행한다.
     * @author joohyoung.kim
     * @version 1.0
     */
	public RoomVO() {
		CopyOnWriteArrayList<ClientInfoVO> empty = new CopyOnWriteArrayList<>();
		empty.add(new ClientInfoVO());
		super.put("game", empty);
		for(int i = 0 ;i < 100; i++) 
		{
			String roomId = getRoomNumber();
			
			roomWaiting.add(roomId);
			super.put(roomId, new CopyOnWriteArrayList<ClientInfoVO>());
		}
		/*
		ExecutorService  thread = Executors.newSingleThreadExecutor();
		thread.execute(()->{
			try {
			while (true) {
				roomWaiting.stream().forEach(roomId->{
					if(super.containsKey(roomId) && super.get(roomId).size() >= 8 ) 
					{
						roomWaiting.remove(roomId);
						String newRoomId = getRoomNumber();
						roomWaiting.add(newRoomId);
						super.put(newRoomId, new CopyOnWriteArrayList<ClientInfoVO>());		
					}
				});
				try{
	                Thread.sleep(500);
	            }catch (InterruptedException e){}
			}
			}catch(Exception e) {
				logger.error(e.getMessage(),e);
			}
		});
		*/
	}
	
    /**
     * room에 access를 시도할 때 대기 중인 room의 roomId를 넘겨주는 함수로 해당 roomId를 받은 유저가 바로 room에 접속하는 것은 아니다.
     * @return String : 대기중인 방의 roomId
     * <pre>
     * 		String roomId = room.accessRoom();
     * 		if(roomId.isEmpty() == false){}
     * </pre>
     * @author joohyoung.kim
     * @version 1.0
     */
	public String getWaitingRoomId() {
		return roomWaiting.parallelStream().filter(roomId -> {
			int roomSize = super.get(roomId).size();
			if(roomSize >= 0 && roomSize <= 8) 
			{
				return true;
			}
			return false;
		}).findFirst().orElseGet(String::new);
	}
	
    /**
     * getWaitingRoomId 함수를 이용하여 waiting 중인 room의 roomId를 발급받은 유저를 room에 접속시키는 함수
     * @param roomId : 접속하려는 roomId
     * @param clientInfoVo : 접속자의 정보
     * @return boolean : 접속 성공 여부로 성공 true, 실패 false
     * <pre>
     * 		if(room.accessRoom(roomId, clientInfoVo) == false){
     * 			//... (room이 가득 찼을 경우 다시 시도하도록 한다.)
     * 		}else{
     * 			//... 성공할 경우의 내용
     * 		}
     * </pre>
     * @author joohyoung.kim
     * @version 0.1
     */
	public boolean accessRoom(String roomId, ClientInfoVO clientInfoVo) {
		/*if(super.containsKey(roomId) == false) {
			super.put(roomId, new CopyOnWriteArrayList<>());
		}else*/ 
		// room에 사람이 가득 찼을 시 (roomId를 발급받고 접속하는 동안 이미 8명이 가득 차버린 경우)
		if(super.get(roomId).size() >= 8) 
		{
			return false;
		} 
		// room에 자리가 있을 경우
		else 
		{
			super.get(roomId).add(clientInfoVo);
			return true;
		}
	}
	
    /**
     * roomId에 해당하는 room에 접속 중인 사용자의 명수로 아무도 없거나, room 자체가 없는 경우 0
     * @param roomId : count 하려는 roomId
     * @return int : 접속자의 수 해당 roomId 자체가 없을 경우( null일 경우 ) return은 0
     * @author joohyoung.kim
     * @version 0.9
     */
	public int roomUserCount(String roomId) {
		@SuppressWarnings("rawtypes")
		List roomUserList = super.get(roomId);
		if(roomUserList != null){
			return roomUserList.size();
		}else {
			return 0;
		}
	}
	
    /**
     * 해당 유저가 접속 중인 room에서 해당 유저를 제거(나가기 혹은 오류로인한 접속 해제)
     * @param session : room에서 나가려는 유저의 session
     * @return ClientInfoVO : 나간 유저의 client 정보
     * <pre>
     * 		ClientInfoVO outUserClient = room.outRoomRemoveUser(session);
     * </pre>
     * @author joohyoung.kim
     * @version 1.0
     */
	public ClientInfoVO outRoomRemoveUser(WebSocketSession session) throws NotFoundException {
		ClientInfoVO client = null;
		MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams();
		String roomIdKey = parameters.get("access").get(0);
		List<ClientInfoVO> roomUserList = super.get( roomIdKey );
		AtomicInteger index = new AtomicInteger();
		
		if(roomUserList != null) {
			//System.out.println(session.getId());
			client = roomUserList.stream()
								.filter(item -> {
									index.getAndIncrement();
									return item.getClientId().equals(session.getId());
								})
								.findFirst()
								.orElseGet(ClientInfoVO::new)
								.get();
			if(client != null) {
				roomUserList.remove(index.get()-1);
				if(roomUserCount(client.getClient_room_url()) == 0) {
					super.remove(client.getClient_room_url());
					roomWaiting.remove(client.getClient_room_url());
					roomWaiting.add(getRoomNumber());
					//System.out.println(client.getClient_room_url() + "방 제거 완료 <<<");
					//System.out.println(super.get(client.getClient_room_url()));
				}
			}else {
				throw new NotFoundException("Not Found User Client");
			}
		}else {
			throw new NotFoundException("Not Found Room");
		}
		return client;
	}
	
    /**
     * 32바이트로 이루어진 랜덤 코드 발급받기
     * @return String : 랜덤으로 발급받은 코드로 base32로 인코딩 된 상태이다.
     * <pre>
     * 		String randomCode = new CreateRandomCodeUtil().createCode(new byte[32]);
     * </pre>
     * @author joohyoung.kim
     * @version 1.0
     */
	public String getRoomNumber() {
		return randomCode.createCode(new byte[32]);
	}
	/*	
	public static void main(String a[]) {

		RoomVO room = new RoomVO();
		ClientInfoVO client = null;
		int sendTimeLimit = (int)TimeUnit.SECONDS.toMillis(10);
	    
	    int sendBufferSizeLimit = 512 * 1024;
		ClientInfoVO client1 =  new ClientInfoVO(Map.ofEntries(
				entry("client_id", "testId")
				, entry("client_room_url", "testURL")
				, entry("access_time", new Date().getTime())
				, entry("event","")
				, entry("access_user", 5)
		));
		ClientInfoVO client2 = new ClientInfoVO();
		CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
		list.add("1");
		list.add("2");
		list.add(0,"1");
		list.add(0,"2");
		System.out.println(list);
		list.remove("2");
		list.remove("2");
		System.out.println(list);

		//System.out.println(room);
		//System.out.println(room.roomWaiting);
		
	}		 
	*/
}

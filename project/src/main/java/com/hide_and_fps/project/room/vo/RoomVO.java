package com.hide_and_fps.project.room.vo;

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
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import com.hide_and_fps.project.user.vo.ClientInfoVO;
import com.hide_and_fps.business_logic.util.CreateRandomCodeUtil;

public class RoomVO extends ConcurrentHashMap<String, CopyOnWriteArrayList<ClientInfoVO>>{
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 5921574994014358062L;

	private final CreateRandomCodeUtil randomCode = new CreateRandomCodeUtil();	
	
	private final CopyOnWriteArrayList<String> roomWaiting = new CopyOnWriteArrayList<>();
	
	private final ConcurrentHashMap<String, String> clientMapping = new ConcurrentHashMap<>();
	
	public RoomVO() {
		CopyOnWriteArrayList<ClientInfoVO> empty = new CopyOnWriteArrayList<>();
		empty.add(new ClientInfoVO());
		super.put("game", empty);
		for(int i = 0 ;i < 100; i++) {
			String roomId = getRoomNumber();
			
			roomWaiting.add(roomId);
			super.put(roomId, new CopyOnWriteArrayList<ClientInfoVO>());
		}
		
		ExecutorService  thread = Executors.newSingleThreadExecutor();
		/*
		thread.execute(()->{
			while (true) {
				roomWaiting.stream().forEach(roomId->{
					if(super.get(roomId).size() >= 8 ) {
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
		});*/
		thread.submit(new Thread(()->{
			while (true) {
				roomWaiting.stream().forEach(roomId->{
					if(super.get(roomId).size() >= 8 ) {
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
		}) );
		
	}
	
	public String accessRoom() {
		return roomWaiting.parallelStream().filter(roomId->{
			int roomSize = super.get(roomId).size();
			if(roomSize >= 0 && roomSize <= 8) {
				return true;
			}
			return false;	
		}).findFirst().orElseGet(String::new);
	}
	
	public boolean settingRoom(String roomId, ClientInfoVO clientInfoVo) {
		
		if(super.containsKey(roomId) == false) {
			super.put(roomId, new CopyOnWriteArrayList<>());
		}else if(super.get(roomId).size() >= 8) {
			return false;
		}
		super.get(roomId).add(clientInfoVo);
		return true;
	}
	
	public int roomUserCount(String roomId) {
		@SuppressWarnings("rawtypes")
		List roomUserList = super.get(roomId);
		if(roomUserList != null){
			return roomUserList.size();
		}else {
			return -1;
		}
	}
	
	public ClientInfoVO outRoomRemoveUser(WebSocketSession session) throws NotFoundException {
		ClientInfoVO client = null;
		List<ClientInfoVO> roomUserList = super.get(session.getUri().getPath());
		AtomicInteger index = new AtomicInteger();
		
		if(roomUserList != null) {
			System.out.println(session.getId());
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
					System.out.println(client.getClient_room_url() + "방 제거 완료 <<<");
					System.out.println(super.get(client.getClient_room_url()));
				}
			}else {
				throw new NotFoundException("Not Found User Client");
			}
		}else {
			throw new NotFoundException("Not Found Room");
		}
		return client;
	}
	
	public String getRoomNumber() {
		return "/" + randomCode.createCode(new byte[32]);
	}
	
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
}

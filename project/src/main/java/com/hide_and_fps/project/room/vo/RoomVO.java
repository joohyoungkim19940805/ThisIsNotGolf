package com.hide_and_fps.project.room.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.socket.WebSocketSession;

import com.hide_and_fps.project.user.vo.ClientInfoVO;

public class RoomVO extends ConcurrentHashMap<String, CopyOnWriteArrayList<ClientInfoVO>>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5921574994014358062L;

	public boolean settingRoom(String room_id, WebSocketSession session, ClientInfoVO clientInfoVo) {
		if(super.containsKey(room_id) == false) {
			super.put(room_id, new CopyOnWriteArrayList<>());
		}else if(super.get(room_id).size() > 8) {
			return false;
		}
		super.get(room_id).add(clientInfoVo);
		return true;
	}
	
	public int roomUserCount(String room_id) {
		if(super.containsKey(room_id)){
			return super.get(room_id).size();
		}else {
			return 0;
		}
	}
	
	public ClientInfoVO outRoomRemoveUser(WebSocketSession session) throws NotFoundException {
		ClientInfoVO client = null;
		List<ClientInfoVO> roomUserList = super.get(session.getUri().getPath());
		AtomicInteger index = new AtomicInteger();
		if(roomUserList != null) {
			client = roomUserList.stream().filter( x -> {
					index.getAndIncrement();
					return x.getClientId().equals(session.getId());
				} ).findFirst().get();
			if(client != null) {
				roomUserList.remove(index.get()-1);
				if(roomUserCount(client.getClient_room_url()) == 0) {
					super.remove(client.getClient_room_url());
					System.out.println(client.getClient_room_url() + "방 제거 완료 <<<");
				}
			}else {
				throw new NotFoundException("Not Found User Client");
			}
		}else {
			throw new NotFoundException("Not Found Room");
		}
		return client;
	}
	
	public static void main(String a[]) {
		Map<String, Integer> test = new HashMap<String, Integer>();
		
		test.put("test1", 1);
		test.put("test2", 2);
		
		System.out.println(test.get("test1"));
		System.out.println(test.get("test3"));
	}
}

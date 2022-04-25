package com.hide_and_fps.project.room.vo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.web.socket.WebSocketSession;

import com.hide_and_fps.project.user.vo.ClientInfoVO;

public class RoomVO extends ConcurrentHashMap<String, CopyOnWriteArrayList<ClientInfoVO>>{
	
	Map<String, Integer> userIndex = new HashMap<String, Integer>();
    /**
	 * 
	 */
	private static final long serialVersionUID = 5921574994014358062L;

	public boolean settingRoom(String room_id, WebSocketSession session, ClientInfoVO clientInfoVo) {
		if(super.containsKey(room_id) == false) {
			super.put(room_id, new CopyOnWriteArrayList<>());
		}else if(super.get(room_id).size() > 8) {
			return false;
		}else {
			userIndex.put(clientInfoVo.getClientId(), super.get(room_id).size());
		}
		
		super.get(room_id).add(clientInfoVo);
		return true;
	}
}

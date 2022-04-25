package com.hide_and_fps.project.user.vo;

import java.util.Map;

import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

public class ClientInfoVO {

	private final String clientInfoTemplate = 
			"""
			{
				"client_info" : 
								{
									"client_id" : "%s" ,
									"client_room_url" : "%s",
									"access_time" : "%d"
								},
				"event" : "%s",
				"access_user" : %d
			}""";
	
	private String client_id;
	private String client_room_url;
	private long access_time;
	/**
	 * event 상태값 및 순서도
	 * user(자기 자신의 정보) -> user_list(자기 자신에게 현재 접속중인 room 유저들 리스트 전달) -> new_access(Others : 방에 있는 타인들에게 자신의 정보 전달) 
	 */
	private String event;
	private int access_user;
	
	private ConcurrentWebSocketSessionDecorator sessionDecorator;
	
	public ClientInfoVO() {}
	
	public ClientInfoVO(Map map){
		this.client_id = (String) map.get("client_id");
		this.client_room_url = (String) map.get("client_room_url");
		this.access_time = (long) map.get("access_time");
		this.event = (String) map.get("event");
		this.access_user = (int) map.get("access_user");
		this.sessionDecorator = (ConcurrentWebSocketSessionDecorator) map.get("sessionDecorator");
	}
	
	public String getSendClientInfoTemplate() {
		return clientInfoTemplate.formatted(
				this.client_id
				, this.client_room_url
				, this.access_time
				, this.event
				, this.access_user
				);
	}
	
	public byte[] getClientInfoToByte() {
		return clientInfoTemplate.formatted(
				this.client_id
				, this.client_room_url
				, this.access_time
				, this.event
				, this.access_user
				).getBytes();
	}
	
	public String changeEventType(String event) {
		this.event = event;
		return getSendClientInfoTemplate();
	}
	
	public String getClientId() {
		return this.client_id;
	}
	
	public ConcurrentWebSocketSessionDecorator getSessionDecorator() {
		return this.sessionDecorator;
	}
	
}

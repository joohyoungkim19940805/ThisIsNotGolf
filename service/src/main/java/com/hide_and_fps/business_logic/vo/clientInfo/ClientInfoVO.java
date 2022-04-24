package com.hide_and_fps.business_logic.vo.clientInfo;

import java.util.Map;

public class ClientInfoVO {

	private final String clientInfoTemplate = 
			"""
			{
				"client_info" : 
								{
									"client_id" : "%s" ,
									"client_room_url" : "%s",
									"access_time" : "%s"
								},
				"event" : "%s",
				"access_user" : %s
			}""";
	
	private String client_id;
	private String client_room_url;
	private long access_time;
	private String event;
	private int access_user;
	
	public ClientInfoVO() {}
	
	public ClientInfoVO(String client_id
						, String client_room_url
						, long access_time
						, String event
						, int access_user){
		this.client_id = client_id;
		this.client_room_url = client_room_url;
		this.access_time = access_time;
		this.event = event;
		this.access_user = access_user;
	}
	
	public ClientInfoVO(Map map){
		this.client_id = (String) map.get("client_id");
		this.client_room_url = (String) map.get("client_room_url");
		this.access_time = (long) map.get("access_time");
		this.event = (String) map.get("event");
		this.access_user = (int) map.get("access_user");
	}
	
	public String getClientInfo() {
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
	
}

/**
 * RtcClient들을 핸들링하는 클래스
 */
class RtcManager{
	
	constructor(Client, room_id){
		
		this.socket = new WebSocket('ws://localhost:8079/room/roomId?access='+room_id);
		//신호 서버로 메시지를 보내기 위한 send 메소드
		//webSocket.send('클라이언트에서 서버로 답장을 보냅니다'); //프론트에서 서버로 데이터를 보낼 때는 send를 사용함.
		this.send = (message)=> this.socket.send(JSON.stringify(message));
		
		this.room_data;
		this.client_info;
		
		Client.prototype.send = this.send;
		Client.prototype.client_info = '';
		
		this.socketEventAdd(Client);
	}
	
		
	/**
	 * 각 이벤트들의 구현체를 관리하는 함수
	 */
	socketEventAdd(Client){
		
			
		/**
		 * 페이지 이동 시 직접 소켓을 클로즈하고 rtc client에 연결된 데이터 채널을 직접 끊는다.
		 * 간헐적으로 서버에서 소켓이 close 되지 않거나 간헐적으로 데이터 채널이 닫히지 않는 현상 대응
		 */			
		window.addEventListener("beforeunload",()=>{
			socket.close();
			if(this.room_data){
				this.room_data.forEach(e=>e.accessUser != '' ? e.dataChannel.close() : undefined);
			}
		});
				
		/**
		 * 서버쪽이랑 연결된 순간 onopen 이벤트가 실행된다.
		 */
		this.socket.onopen = e => {
			console.log(e);
		    console.log("웹소컷 서버와 연결 완료");
		    
		};
		
		/**
		 * 웹 소켓과 연결이 끊겼을 경우
		 */
		this.socket.onclose = e => {
			console.log(e);
			console.log('웹소켓 닫는다.');
		};
		
		/**
		 * 소켓으로부터 메시지를 전달받았을 경우 각 메시지 이벤트마다 분기 처리
		 */
		this.socket.onmessage = msg =>{
			console.log(msg)
			let content = JSON.parse(msg.data);
			
			if(!this.room_data){
				this.room_data = [...Array(content.access_user)];
			}
			
			switch(content.event){
				case "user" :
					//then 전역변수 콜백
					this.userHandle(content, Client).then(client_id => accessUserAddView(client_id));
					//
					break;
					
				case "user_list" :
					this.userListHandle(content, Client);
					break;
					
				case "new_access" :
					this.newAccessHandle(content, Client).then();
					break;
					
				case "offer" :
					this.offerHandle(content);
					break;
					
				case "answer" :
					this.answerHandle(content);
					break;
					
				case "candidate" :
					this.candidateHandle(content);
					break;
					
				case "delete" : 
					//then 전역변수 콜백
					this.deleteRoomHandle(content.client_info.client_id).then(client_id=>deleteUserRemoveView(client_id));
					//
					break;
					
				case "serverMessage" :
					break;
				case "access_room" :
					top.location.href = '/chat_multi_access_multi_room?access='+data.access
				default :
					break;
					
			}
		}	
	}
	
	/**
	 * web soket 서버로부터 발급받은 자기 자신의 클라이언트 정보를 RtcClient의 prototype과 manager에 저장한다. (클라이언트가 자기 자신을 기억하도록한다.)
	 * @param {JSON} client_info web soket 서버로부터 발급받은 자기 자신의 클라이언트 정보
	 * @param {class} Client RtcClient 클래스
	 * @returns {Promise} 비동기 함수로서 promise를 반환하도록 한다.
	 */
	userHandle({client_info}, Client){
		return new Promise(resolve =>{
			
			this.client_info = Object.freeze(client_info);
			Client.prototype.client_info = this.client_info;
			
			return setTimeout(resolve(client_info.client_id),0);
			
		});
	}
	
	/**
	 * web soket 서버로부터 현재 room에 참가 중인 유저들을 각 RtcClient에 등록한다.
	 * @param {JSON} data web soket 서버로부터 전달받은 현재 room에 참가 중인 유저들의 클라이언트 정보
	 * @param {class} Client RtcClient 클래스
	 */
	userListHandle({data}, Client){
		data.map((e,i) => {
			let create_chat_room = new Client();
			create_chat_room.access_user = e.client_info.client_id
			this.room_data[i] = create_chat_room; 
		});
	}
	
	/**
	 * 현재 참가 중인 room에 신규 유저가 접속할 시 새로운 RtcClient에 신규 유저의 클라이언트 정보를 입력한다.
	 * @param {JSON} client_info 현재 room에 참가 요청을 한 새로운 유저의 클라이언트 정보 
	 * @param {class} Client RtcClient 클래스
	 * @returns {Promise} 비동기 함수로서 promise를 반환하도록 한다.
	 */
	newAccessHandle({client_info}, Client){
		return new Promise(resolve =>{
			
			let new_user_access_room = new Client(client_info.client_id);
			this.room_data.push(new_user_access_room);
			
			return setTimeout(resolve(client_info.client_id),0);
		});
	}
	
	offerHandle({data}){
		let find_target_room = this.room_data.find(e => e.access_user == data.offer_req_id &&
		        										data.target_res_id == this.client_info.client_id &&
	    												e.channelReady == true &&
	    												e.peerReady == true);

	    if(find_target_room){
			find_target_room.access_user = data.offer_req_id;
			
			find_target_room.peerConnection.setRemoteDescription(new RTCSessionDescription(data));

			let {client_id} = this.client_info
			let send = this.send;
			find_target_room.peerConnection.createAnswer(function(answer) {
				answer['answer_req_id'] = client_id;
				find_target_room.peerConnection.setLocalDescription(answer);
			        send({
			            event : "answer",
			            data : answer
			        });
			}, function(error) {
				console.log("Error creating an answer");
				console.log(error);
			});
		}
	}
	
	answerHandle({data}){
		let find_target_room = this.room_data.find(e => e.access_user == data.answer_req_id &&
    													e.channelReady == true &&
    													e.peerReady == true);
   		if(find_target_room){
    		find_target_room.peerConnection.setRemoteDescription(new RTCSessionDescription(data));
		}
	}
	
	candidateHandle({data, client_info}){
		let check_room_access_user = this.room_data.find(e => e.access_user == client_info.client_id);
		if(check_room_access_user){
			try{
				check_room_access_user.peerConnection.addIceCandidate(new RTCIceCandidate(data));
			}catch(error){
				console.error("candidateEvent Error");
				console.error(error);
				deleteRoomHandle(check_room_access_user.access_user);
			}
		}
	}
	
	deleteRoomHandle(access_user){
		return new Promise(resolve => {
			
			let close_room_index = this.room_data.findIndex(e => e.access_user == access_user);
			let closeTargetUser = this.room_data[close_room_index].access_user;
			if(close_room_index != -1){
				this.room_data.splice(close_room_index, 1)
			}
			
			return setTimeout(resolve(closeTargetUser),0);
		});
	}
	
	socketClose(){
		this.socket.close();
	}
	
	sendToClient(message){
		return new Promise(resolve =>{
			if(message){
				
				let sendObject = {
					'client_id' : this.client_info.client_id,
					'message' : message
				}
				let result = this.room_data.reduce( (t,e) => {
					if(e.peerReady == false && e.channelReady == false){
						e.dataChannel.send( JSON.stringify(sendObject) );
						t.push(e.aacess_user);
					}
					return t;
				},[]);
				if(result.length == 0){
					throw Error('다른 클라이언트에 메시지 전송을 실패하였습니다.')
				}
				
				return setTimeout(resolve(sendObject), 0);
				
			}else{
				throw Error('다른 클라이언트에 전송할 데이터가 없습니다.');
			}
		});	
	}
}
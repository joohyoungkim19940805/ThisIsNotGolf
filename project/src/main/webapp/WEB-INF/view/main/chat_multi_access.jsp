<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript">
	const configuration = Object.freeze({
		  'iceServers': [
			    {
			      'urls': 'stun:stun.l.google.com:19302'
			    },
			    {
			      'urls': 'turn:10.158.29.39:3478?transport=udp',
			      'credential': 'XXXXXXXXXXXXX',
			      'username': 'XXXXXXXXXXXXXXX'
			    },
			    {
			      'urls': 'turn:10.158.29.39:3478?transport=tcp',
			      'credential': 'XXXXXXXXXXXXX',
			      'username': 'XXXXXXXXXXXXXXX'
			    }
			  ]
			});
	
	const socket = new WebSocket('ws://localhost:8079/${access_code}');
	
	//신호 서버로 메시지를 보내기 위한 send 메소드
	//webSocket.send('클라이언트에서 서버로 답장을 보냅니다'); //프론트에서 서버로 데이터를 보낼 때는 send를 사용함.
	const send = (message)=> socket.send(JSON.stringify(message));
	
	const offerList = [];
	
	var roomData;
	
	var client_info;
	
	//서버쪽이랑 연결된 순간 onopen 이벤트가 실행된다.
	socket.onopen = function(e) {
		console.log(e);
	    console.log("웹소컷 서버와 연결 완료");
	    
	};
	
	socket.onclose = (e) => {
		console.log(e);
		console.log('웹소켓 닫는다.');
	};
	
	window.onbeforeunload = function(e) {
		socket.close();
		if(roomData){
			roomData.filter(e=>e.accessUser != '')
					.map(e=>e.dataChannel.close());
		}
	};
	
	//서버쪽에서 메세지가 전달된 순간 onmessage가 실행된다.
	socket.onmessage = function(msg) {
		console.log(roomData);
		//서버에서 받은 메세지가 event.data에 담긴다.
	    console.log("Got message", msg);
	    var content = JSON.parse(msg.data);
	    var data = content.data;
	    if(!roomData){
	    	//console.log(content.access_user)
			roomData = [...Array(content.access_user)];
			//console.log('start');
			//console.log(roomData);
	    }
	    switch (content.event) {
	    	case "user_list":
	    		console.log(content);
	    		data.map((e,i) => {
	    			let createChatRoom = new Chat_client();
	    			createChatRoom.accessUser = e.client_info.client_id;
	    			roomData[i] = createChatRoom;
	    		});
	    		//roomData.map((e,i)=> roomData[i] = new Chat_client())
	    		break
	    	case "new_access":
	    		console.log('new USER >>>>');
	    		console.log(content);
	    		let newAccessUserCount = content.access_user - roomData.length;
	    		console.log(newAccessUserCount);
				
	    		let newUserAccessRoom = new Chat_client(content.client_info.client_id);
				console.log(newUserAccessRoom);
				roomData.push(newUserAccessRoom);
				/*
	    		if(newAccessUserCount > 0){
	    			[...Array(newAccessUserCount)].forEach(()=>{
	    				let newUserAccessRoom = new Chat_client("test");
	    				newUserAccessRoom.accessUser = content.client_info.client_id;
						console.log(newUserAccessRoom);
	    				roomData.push(newUserAccessRoom);
	    			});
	    		}
				*/
	    		break;
		    case "user":
		    	client_info = Object.freeze(content.client_info);
		    	accessUserAddView(client_info.client_id);
		    	//roomData.push(new Chat_client());
		    	//console.log('데이터 생성 후 ');
		    	//console.log(roomData);
		    	break;
		    // when somebody wants to call us
		    case "offer":
		    	/*
		    	let chatClient = new Chat_client('test');
		    	chatClient.accessUser = data.offer_req_id;
		    	handleOffer(chatClient, data);
		    	roomData.push(chatClient);
		        */
		        let findTargetRoom = roomData.find(e => e.accessUser == data.offer_req_id &&
		        										data.target_res_id == client_info.client_id &&
	    												e.channelReady == true &&
	    												e.peerReady == true);
		        if(findTargetRoom){
		        	console.log('findTargetRoom.accessUser  >>>' )
		        	console.log(findTargetRoom.accessUser );
		        	findTargetRoom.accessUser = data.offer_req_id;
		        	handleOffer(findTargetRoom, data);
		        }
		    	break;
		    case "answer":
	    		console.log(roomData.find(e=> e.peerReady == false && e.channelReady == false))
	    		if(roomData){
	    			let findTargetRoom = roomData.find(e => e.accessUser == data.answer_req_id &&
	    													e.channelReady == true &&
	    													e.peerReady == true);
	    		
		    		if(findTargetRoom){
		    			handleAnswer(findTargetRoom, data);
		    		}
	    		}
		        break;
		    // when a remote peer sends an ice candidate to us
		    case "candidate":
		    	console.log(content);
		    	let checkRoomAccessUser = roomData.find(e => e.accessUser == content.client_info.client_id);
		        console.log('checkRoomAccessUser');
		    	console.log(checkRoomAccessUser)
		    	if(checkRoomAccessUser){
		    		handleCandidate(checkRoomAccessUser, data);
		    	}
		        break;
		    case "delete":
		    	deleteUserRemoveView(content.client_info.client_id);
		    	deleteRoomData(content.client_info.client_id);
		    case "serverMessage":
		    	break;	
		    default:
		        break;
	    }
	};
	
	class Chat_client{
		constructor(accessUser){
			
			this.peerConnection = new RTCPeerConnection(configuration)
			this.dataChannel = this.peerConnection.createDataChannel("dataChannel", { 
				reliable: true 
			});
			
			this.accessUser = accessUser;
			
			if(this.accessUser === undefined){
				this.peerConnection.createOffer(offer => {
					offer['offer_req_id'] = client_info.client_id;
					offer['target_res_id'] = this.accessUser;
				    send({
				        event : "offer",
				        data : offer
				    });
				    this.peerConnection.setLocalDescription(offer);
				}, error => {
					console.log('error =>');
					console.log(error);
				});
			}
			this.addEvents();
		}
		
		addEvents(){
			var accessUser = this.accessUser;
			//원격지의 피어에 이 클라이언트를 후보로 등록할 수 있도록 전송해준다.
			this.peerConnection.onicecandidate = (event)=> {
				console.log("onicecandidate>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
				console.log(event);
				if(event.candidate){
					send({
						event : "candidate",
						data : event.candidate,
						'client_info' : client_info
					});
				}
			}
			
			this.dataChannel.onerror = function(error) {
		        console.log("Error occured on datachannel:", error);
		    };

		    // when we receive a message from the other peer, printing it on the console
		    this.dataChannel.onmessage = function(event) {
		    	console.log(event)
		    	viewMessage( JSON.parse( event.data ) );
		    };
		    
		    this.dataChannel.onclose = function(event) {
		        console.log("data channel is closed");
		        console.log(event);
		       	console.log(accessUser);
     			deleteUserRemoveView(accessUser);
		       	deleteRoomData(accessUser)
		    };
		  	
		    this.dataChannel.onopen = (event) => {
		    	console.log('dataChannel onopen event<<<');
		    	console.log(event);
		    	console.log(this.accessUser);
		    	accessUserAddView(this.accessUser)
		    }
		    
		    this.peerConnection.ondatachannel = (event) => {
		  		console.log("ondatachannel 이벤트 실행<<<");
		  		console.log(event);
		  		this.accessUser = this.accessUser;
		        this.dataChannel = event.channel;
		  	};
		  	
		  	this.peerConnection.onconnectionstatechange = (event) => {
				switch(this.peerConnection.connectionState) {
				case "new":
					break;
				case "connecting":
					break;
				case "connected":
					// The connection has become fully connected
					break;
				case "disconnected":
					break;
				case "failed":
					// One or more transports has terminated unexpectedly or in an error
					break;
				case "closed":
					// The connection has been closed
					break;
				}
		  	}
		  	this.peerConnection.oniceconnectionstatechange = (event) => {
				switch(this.peerConnection.connectionState) {
				case "new":
					break;
				case "checking":
					break;
				case "connected":
					// The connection has become fully connected
					break;
				case "completed":
					break;
				case "failed":
					// One or more transports has terminated unexpectedly or in an error
					break;
				case "disconnected":
					// The connection has been closed
					break;
				case "closed":
					// The connection has been closed
					break;
				}
		  	}
		}
		
		get peerReady(){
			return 	(		this.peerConnection.iceConnectionState != 'connected' 
						&&	this.peerConnection.connectionState != 'connected'
					);
		}
		
		get channelReady(){
			return (this.dataChannel.readyState != 'open');
		}
	}
	
	function handleOffer(Chat_client_class, offer){
		Chat_client_class.peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
		Chat_client_class.peerConnection.createAnswer(function(answer) {
			answer['answer_req_id'] = client_info.client_id;
			Chat_client_class.peerConnection.setLocalDescription(answer);
		        send({
		            event : "answer",
		            data : answer
		        });
		}, function(error) {
			console.log("Error creating an answer");
		});
	}
	
	function handleAnswer(Chat_client_class, answer) {
		Chat_client_class.peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
		console.log("connection established successfully!!");
	};
	
	function handleCandidate(Chat_client_class, candidate) {
		//원격지의 다른 피어가 보낸 ice를 후보자로 등록한다.
		try{
			Chat_client_class.peerConnection.addIceCandidate(new RTCIceCandidate(candidate)).then();
		}catch{
			console.log(Chat_client_class);
			console.log("errorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerror");
			deleteRoomData(Chat_client_class.accessUser);
		}	
	};
	
	function deleteRoomData(targetAccessUser){
		console.log("delete user >>>>> " + targetAccessUser);
		let closeTargetRoomIndex = roomData.findIndex(e => e.accessUser == targetAccessUser);
		console.log(closeTargetRoomIndex);
		if(closeTargetRoomIndex != -1){
			
			roomData.splice(closeTargetRoomIndex, 1)
		}
	}

	
	
	
	var textArr;
	var chatContentArr;
	var chatContentTemplate = (()=>{
		let div = document.createElement('div');
		let idWrap = document.createElement('span');
		let chatMessage = document.createElement('span');
		idWrap.className = 'chat_id';
		chatMessage.className = 'chat_message';
		div.append(...[idWrap, chatMessage]);
		return div;
	})();
	var accessUserListArr
	window.onload = () =>{
		textArr = document.querySelector('#chat_message_input'); 
		chatContentArr = document.querySelector('#chat_message_content');
		accessUserListArr = document.querySelector('.chat_user_list');
	}
	function sendMessage() {
		let message;
		if(textArr){
			message = {
				chat_id : client_info.client_id,
				chat_message : textArr.value
			}
			let result = this.roomData.filter(e=>{
				if(e.peerReady == false && e.channelReady == false){
					e.dataChannel.send( JSON.stringify(message) );
					return e.accessUser;
				}
			});
			console.log('result <<<');
			console.log(result);
			if(result.length == 0){
				alert('다른 클라이언트에 메시지 전송을 실패하였습니다.')
			}else{
				viewMessage(message);
				textArr.value = '';	
			}
		}else{
			alert('채팅 화면 로드 실패');
		}
	}
	function viewMessage(message){
		if(chatContentArr){
			let chatWrap = chatContentTemplate.cloneNode(true);
			chatWrap.querySelector('.chat_id').textContent = message.chat_id
			chatWrap.querySelector('.chat_message').textContent = message.chat_message;
			chatContentArr.append(chatWrap);
		}
	}
	function accessUserAddView(accessUser){
		console.log(accessUser);
		if(accessUserListArr){
			let item = document.createElement('li');
			item.textContent = accessUser;
			accessUserListArr.append(item);
		}
	}
	function deleteUserRemoveView(accessUser){
		let chatUserList = document.querySelector('.chat_user_list');
		let removeTarget = [...chatUserList.children].find(e => e.textContent == accessUser)
		console.log(removeTarget);
		if(removeTarget !== undefined){
			chatUserList.removeChild(removeTarget);
		}
	}
</script>

<style>
	span.chat_id:after {
    	content: " : ";
	}
</style>

</head>
<body>

<div class="container">
	<div class="chat_wrapper" style="display: flex;writing-mode: vertical-lr;gap: 1rem;">
		<div class="chat_view_wrapper" style="height:500px">
			<div id="chat_message_content" class="chat_content" style="width:500px; height:100%; writing-mode: horizontal-tb;overflow-y: scroll;overflow-x: clip;overflow-wrap: break-word;border: inset;">
			</div>
			<div class="chat_user_list_wrapper" style="writing-mode: horizontal-tb;overflow: scroll;height: 100%;min-width: 5rem;max-width: 10rem;border: inset;">
				<ul class="chat_user_list" style="list-style: decimal-leading-zero;">

				</ul>
			</div>
		</div>
		<div class="chat_interface_wrapper" style="display:flex;writing-mode: horizontal-tb;column-gap: 3%;">
			<div class="chat_text_wrapper" style="width: 100%;">
				<input id="chat_message_input" type="text" style="width: inherit;" onkeyup="javascript: if(event.keyCode === 13) sendMessage();"/>
			</div>
			<div class="chat_button_wrapper" style="min-width: fit-content;">
				<div class="button_arr">
					<button type="button" class="chat_message_send_button" onclick="sendMessage()">send</button>
					<button type="button" class="chat_access_button" onclick="">access</button>
				</div>
			</div>
		</div>
	</div>
</div>

</body>
</html>
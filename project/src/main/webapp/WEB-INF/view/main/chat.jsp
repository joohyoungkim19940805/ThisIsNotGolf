<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript">

	//STUN, TURN 설정값으로 둘 중 하나를 써야함
	//public한 서버 주소가 없으면 WebRtc로 연결된 p2p는 동작하지 않는다.(로컬 환경이 아닌 이상, 로컬이면 null로 줘도 됨)
	//방화벽과 NAT(Network Address Traversal)의 문제
	//튜토리얼에 있는 STUN 서버를 쓴다.
	const configuration = Object.freeze({
			"iceServers" : [ {
								"url" : "stun:stun2.1.google.com:19302"
							} ]
			});
	/*or
		{
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
		}
	*/


	//시그널링 서버로 연결 될 웹 소켓 클라이언트 셋팅
	const socketUrl = new WebSocket('ws://localhost:8079/video_chat');
	window.onbeforeunload = function(e) {
		socketUrl.close();
		dataChannel.close();
	};
	
	socketUrl.onclose = (e) => {
		console.log(e);
		console.log('웹소켓 닫는다.');
	}; // disable onclose handler first
	//신호 서버로 메시지를 보내기 위한 send 메소드
	//webSocket.send('클라이언트에서 서버로 답장을 보냅니다'); //프론트에서 서버로 데이터를 보낼 때는 send를 사용함.
	const send = (message)=> socketUrl.send(JSON.stringify(message));
	
	//서버쪽이랑 연결된 순간 onopen 이벤트가 실행된다.
	socketUrl.onopen = function(e) {
		console.log(e);
	    console.log("웹소컷 서버와 연결 완료");
	    setting();
	};

	var client_info;
	
	//서버쪽에서 메세지가 전달된 순간 onmessage가 실행된다.
	socketUrl.onmessage = function(msg) {
		//서버에서 받은 메세지가 event.data에 담긴다.
		console.log(msg);
	    console.log("Got message", msg.data);
	    var content = JSON.parse(msg.data);
	    var data = content.data;
	    console.log(content)
	    switch (content.event) {
	    case "user":
	    	client_info = Object.freeze(content.client_info);
	    	break;
	    // when somebody wants to call us
	    case "offer":
	        handleOffer(data);
	        break;
	    case "answer":
	        handleAnswer(data);
	        break;
	    // when a remote peer sends an ice candidate to us
	    case "candidate":
		    console.log(peerConnection.iceConnectionState)
		    console.log(peerConnection.connectionState)
	        handleCandidate(data);
	        break;
	    case "serverMessage":
	    	console.log(content.serverMin);
	    default:
	        break;
	    }
	    /*
	    if(true){
	    	webSocket.close()
	   	}
	    */
	};

	//원격지 클라이언트와 연결해줄 연결객체
	const peerConnection = new RTCPeerConnection(configuration);
	
	//원격지 클라이언트와 소통할 데이터가 오고 갈 채널을 생성한다.
	var dataChannel = peerConnection.createDataChannel("dataChannel", { 
		reliable: true 
	});
	
	function setting() {

		console.log(dataChannel);
		
		//원격지의 피어에 이 클라이언트를 후보로 등록할 수 있도록 전송해준다.
		peerConnection.onicecandidate = (event)=> {
			console.log('이벤트 >>> candidate')
			console.log(event);
			console.log(event.candidate);
			if(event.candidate){
				console.log(client_info);
				event.candidate.client_id = client_info.client_id;
				event.candidate.client_room_url = client_info.client_room_url;
				
				console.log(`ice 보낸다 >>>`)
				console.log(event.candidate.client_info)
				
				send({
					event : "candidate",
					data : event.candidate,
					'client_info' : client_info
				});
			}
		}
	
	    dataChannel.onerror = function(error) {
	        console.log("Error occured on datachannel:", error);
	    };

	    // when we receive a message from the other peer, printing it on the console
	    dataChannel.onmessage = function(event) {
	    	console.log(event);
	        console.log("message:", event.data);
	    };
	    
	    dataChannel.onopen = (event) => {
	    	console.log('dataChannel onopen event<<<');
	    	console.log(event);
	    }
	    
	    dataChannel.onclose = function() {
	        console.log("data channel is closed");
	        dataChannel.close();
	    };
	  
	  	peerConnection.ondatachannel = function (event) {
	  		console.log("ondatachannel 이벤트 실행<<<");
	  		console.log(event);
	        dataChannel = event.channel;
	  	};
	    
	}
	
	function createOffer(){
		peerConnection.createOffer(function(offer) {
			console.log("offer 생성 시작 >>>");
			offer['testData'] = 'qweqwe';
			console.log(offer);
		    send({
		        event : "offer",
		        data : offer
		    });
		    peerConnection.setLocalDescription(offer);
		    console.log(offer);
		    console.log("offer 생성 종료");
		}, function(error) {
			console.log('error =>');
			console.log(error);
		    // Handle error here
		});
	}
	
	function handleOffer(offer){
		peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
		
		peerConnection.createAnswer(function(answer) {
		    peerConnection.setLocalDescription(answer);
		        send({
		            event : "answer",
		            data : answer
		        });
		}, function(error) {
			console.log("Error creating an answer");
		});
	}
	
	function handleCandidate(candidate) {
		console.log('candidate 등록>>>')
		console.log(candidate);
		//원격지의 다른 피어가 보낸 ice를 후보자로 등록한다.
		peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
	};

	
	function handleAnswer(answer) {
	    peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
	    console.log("connection established successfully!!");
	    console.log(peerConnection.iceConnectionState)
	    console.log(peerConnection.connectionState)
	};
	
	function sendMessage() {
		var input = document.getElementById("messageInput");
	    dataChannel.send(input.value);
	    input.value = "";
	}
	/*
	const constraints = {
		    video: true,audio : true
		};
		navigator.mediaDevices.getUserMedia(constraints)
			.then(function(stream) {
				//use the stream 
			})
			.catch(function(err) {
				handle the error
			});
		
	var constraints = {
		    video : {
		        frameRate : {
		            ideal : 10,
		            max : 15
		        },
		        width : 1280,
		        height : 720,
		        facingMode : "user"
		    }
		};
	peerConnection.addStream(stream);
	
	peerConnection.onaddstream = function(event) {
	    videoElement.srcObject = event.stream;
	};
	*/
</script>
</head>
<body>

 <div class="container">
  <h1>A Demo for messaging in WebRTC</h1>

  <h3>
   Run two instances of this webpage along with the server to test this
   application.<br> Create an offer, and then send the message. <br>Check
   the browser console to see the output.
  </h3>

  <!--WebRTC related code-->
  <button type="button" class="btn btn-primary" onclick='createOffer()'>Create
   Offer</button>
  <input id="messageInput" type="text" class="form-control"
   placeholder="message">
  <button type="button" class="btn btn-primary" onclick='sendMessage()'>SEND</button>
  <!--WebRTC related code-->

 </div>
 <div class="footer">This application is intentionally made simple
  to avoid cluttering with non WebRTC related code.</div>

</body>
</html>
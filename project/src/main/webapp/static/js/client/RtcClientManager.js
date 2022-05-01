class Client{
	constructor(access_user){
			
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

		this.peerConnection = new RTCPeerConnection(configuration)
		this.dataChannel = this.peerConnection.createDataChannel("dataChannel", { 
			reliable: true 
		});
		
		this.access_user = access_user;
		console.log(this.access_user);
		if(this.access_user === undefined){
			this.peerConnection.createOffer(offer => {
				offer['offer_req_id'] = this.client_info.client_id;
				offer['target_res_id'] = this.access_user;
			    this.send({
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
			//원격지의 피어에 이 클라이언트를 후보로 등록할 수 있도록 전송해준다.
			this.peerConnection.onicecandidate = (event)=> {
				console.log("onicecandidate>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
				console.log(event);
				if(event.candidate){
					this.send({
						event : "candidate",
						data : event.candidate,
						'client_info' : this.client_info
					});
				}
			}
			
			this.dataChannel.onerror = function(error) {
		        console.log("Error occured on datachannel:", error);
		    };

		    // when we receive a message from the other peer, printing it on the console
		    this.dataChannel.onmessage = function(event) {
		    	console.log(event)
		    	//viewMessage( JSON.parse( event.data ) );
		    };
		    
		    this.dataChannel.onclose = function(event) {
		        console.log("data channel is closed");
		        console.log(event);
     			//deleteUserRemoveView(this.access_user);
		       	//deleteRoomData(this.access_user)
		    };
		  	
		    this.dataChannel.onopen = (event) => {
		    	console.log('dataChannel onopen event<<<');
		    	console.log(event);
		    	//accessUserAddView(this.access_user)
		    }
		    
		    this.peerConnection.ondatachannel = (event) => {
		  		console.log("ondatachannel 이벤트 실행<<<");
		  		console.log(event);
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
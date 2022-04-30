class SocketManager{
	constructor(Client, room_data, room_number){

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
		
		this.socket = new WebSocket('ws://localhost:8079/'+room_number);
		
		//신호 서버로 메시지를 보내기 위한 send 메소드
		//webSocket.send('클라이언트에서 서버로 답장을 보냅니다'); //프론트에서 서버로 데이터를 보낼 때는 send를 사용함.
		this.send = (message)=> this.socket.send(JSON.stringify(message));

		this.socketEventAdd(Client, room_data)
	}
	
	socketEventAdd(Client, room_data){
					
		window.addEventListener("beforeunload",()=>{
			socket.close();
			if(room_data){
				room_data.filter(e=>e.accessUser != '')
						.map(e=>e.dataChannel.close());
			}
		});
				
		//서버쪽이랑 연결된 순간 onopen 이벤트가 실행된다.
		this.socket.onopen = e => {
			console.log(e);
		    console.log("웹소컷 서버와 연결 완료");
		    
		};
		
		this.socket.onclose = e => {
			console.log(e);
			console.log('웹소켓 닫는다.');
		};
		
		this.socket.onmessage = msg =>{
			console.log(msg)
			let content = JSON.parse(msg.data);
			let data = content.data
			if(!room_data){
				room_data = [...Array(content.access_user)];
			}
			switch(content.event){
				case "room_number" : 
					this.send({test1:"test1"});
					this.socket = new WebSocket('ws://localhost:8079/'+content.room_number);
					this.send({test2:"test2"});
				case "user" :
					this.client_info = Object.freeze(content.client_info);
				default :
					break;
			}
		}
			
	}
	
	socketClose(){
		this.socket.close();
	}
}
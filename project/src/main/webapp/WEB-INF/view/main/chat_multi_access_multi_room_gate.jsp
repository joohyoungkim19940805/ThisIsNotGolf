<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript">
alert("??");
	const socket = new WebSocket('ws://localhost:8079/game');
	
	//신호 서버로 메시지를 보내기 위한 send 메소드
	//webSocket.send('클라이언트에서 서버로 답장을 보냅니다'); //프론트에서 서버로 데이터를 보낼 때는 send를 사용함.
	const send = (message)=> socket.send(JSON.stringify(message));
	
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
	};
	
	//서버쪽에서 메세지가 전달된 순간 onmessage가 실행된다.
	socket.onmessage = function(msg) {;
		//서버에서 받은 메세지가 event.data에 담긴다.
	    console.log("Got message", msg);
	    var content = JSON.parse(msg.data);
	    var data = content.data;
		console.log(data);
		alert(data.size);
	    switch (content.event) {
	    	case "access_room":
				top.location.href = '/chat_multi_access_multi_room?access='+data.access.substring(1)
	    		//roomData.map((e,i)=> roomData[i] = new Chat_client())
	    		break;
		    case "serverMessage":
		    	break;	
		    default:
		        break;
	    }
	};
	
</script>

<style>
	span.chat_id:after {
    	content: " : ";
	}
</style>

</head>
<body>


</body>
</html>
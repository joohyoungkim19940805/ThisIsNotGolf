<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="/static/js/client/SocketManager.js"></script>
<script type="text/javascript" src="/static/js/client/RtcClientManager.js"></script>
<script type="text/javascript">
var test2;
var test = new SocketManager(Client, "${access_code}");

var send = (message) =>{
	
	test.room_data.map(e=>e.dataChannel.send( JSON.stringify(message) ));
}
</script>
</head>
<body>

</body>
</html>
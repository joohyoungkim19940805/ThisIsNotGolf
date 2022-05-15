<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="/static/js/client/RtcManager.js"></script>
<script type="text/javascript" src="/static/js/client/RtcClient.js"></script>
<script type="text/javascript">
	var rtcManager = new RtcManager(RtcClient, "${access_code}");
	
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
	window.onload = () => {
		textArr = document.querySelector('#chat_message_input'); 
		chatContentArr = document.querySelector('#chat_message_content');
		accessUserListArr = document.querySelector('.chat_user_list');
	}
	function sendMessage() {
		let message;
		if(textArr){
			rtcManager.sendToClient(textArr.value)
				.then((result)=>{
					messageViewLoder(result);
					
				})
				.catch((errorMessage)=>{
					console.log(errorMessage);
				});
			
			textArr.value = '';
		}else{
			alert('채팅 화면 로드 실패');
		}
	}
	function messageViewLoder(message){
		console.log(message);
		if(chatContentArr){
			let chatWrap = chatContentTemplate.cloneNode(true);
			chatWrap.querySelector('.chat_id').textContent = message.client_id
			chatWrap.querySelector('.chat_message').textContent = message.message;
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
		console.log(accessUser);
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
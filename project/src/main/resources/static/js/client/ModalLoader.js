class ModalLoader{
	constructor(){
	
	}

	setModalAlert(){
		this.modalAlert = Object.assign( document.createElement('dialog'), {id:'modal', calss:'type_alert'});
		Object.assign(this.modalAlert.style, {position: 'absolute', width: '100%', height: '100%', opacity: '1', justifyContent: 'center', alignItems: 'flex-start', top: '0px', left: '0px', paddingTop: '5%', background: 'none', border: 'none'})
		
		this.modalAlert.innerHTML = 
			`
			<div class="modal_wrapper" style="display: flex; justify-content: center; align-items: flex-start;">
				<div class="modal_content" style="background-color: azure;width: auto;height: auto;display: flex;justify-content: center;align-items: center;border-radius: 1vmax;font-family: monospace;font-weight: bold;flex-direction: column-reverse;font-size: 1vmax;padding: 1%;">
					<button style="margin-top: 5%;background-color: #9da3a647;color: black;border-radius: 1vmax;font-family: monospace;"
							onclick="this.offsetParent.close();this.parentElement.querySelectorAll('div').forEach(e=>e.remove());">확인</button>
				</div>
			</div>
			`
		document.querySelector('body').append(this.modalAlert);
		this.modalAlert.onclick = function(e) {
			if(e.path[0] === this&& this.open){
				this.close();
				this.querySelectorAll('.modal_content div').forEach(e=>e.remove());
				return false;
			}else{
				return false;
			}
		}
	}
	createModalAlertText(text){
				
		this.modalAlert.querySelector('.modal_content').append(...text.map(e => Object.assign( document.createElement('div'), {textContent:e}) ));
		this.modalAlert.showModal();
	}
}
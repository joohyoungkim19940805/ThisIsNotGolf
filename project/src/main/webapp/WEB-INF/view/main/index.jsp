<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>Insert title here</title>

<script type="text/javascript">

console.log('${syAdminDto}');

console.log('${syAdminDto.getId()}');

const message = {"메시지 이름" : "${Message.getMessage('common.access.withoutAuthority')}"};
</script>
</head>
<body>
<h1>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!</h1>
asdasdasdasdasdasd
<br/>

<div>
	<button id="fileUpload">파일 업로드하기</button>
</div>


<img id="file_upload" src="/static/images/btn_file_add.png"/>

<input	type="file" id="fileInput" name="uploadFile"
	accept=".jpg, .png, .pdf, .jpeg" multiple="multiple" title="파일첨부" 
	style="display:none; opacity:0; visibility:hidden" />

<div class="result" style="justify-content: space-between; display: flex;">
	<div class="file_wrap">
	</div>
	
	<div class="file_upload_result">
	</div>
</div>

<script>
	class InputFileHandler {
		constructor(upload = document.querySelector('#file_upload'),
					targetFiles = document.querySelector('#fileInput'),
					fileUploadButton = document.querySelector('#fileUpload')){
			
			this.fileSizeLimitToMB = Object.freeze([1]);
			// https://developer.mozilla.org/en-US/docs/Web/Media/Formats/Image_types
			this.fileTypes = Object.freeze( [
			  "image/apng",
			  "image/avif",
			  "image/bmp",
			  "image/gif",
			  "image/jpeg",
			  "image/pjpeg", //IE만 읽는 jpg image의 mime 형태
			  "image/png",
			  "image/svg+xml",
			  "image/tiff",
			  "image/webp",
			  "image/x-icon"
			] );
			const fileList = [];
			
			//업로드 이미지를 클릭할 시 input file이 클릭되도록 이벤트 등록
			upload.onclick = () => targetFiles.click();
			
			//파일 업로드하기 버튼 클릭시 서버에 파일 데이터를 보내서 서버 경로에 파일을 저장시킨다.
			fileUploadButton.onclick = ()=> this.fileUpload(fileList).then((result)=>{
				console.log(result instanceof Error);
				if(result instanceof Error) {
					alert(result.toString())
				}else{
					let renderingData = result.reduce((t,e,i)=>{
						let div = document.createElement('div');
						Object.assign( div.style, { display : 'flex', flex : '1 1', gap : '1rem', alignItems : 'center' });
						console.log(e);
						let obj = {}
						obj.name = e.originFileName;
						obj.size = e.fileSize;
						obj.type = e.extension;
						obj.url = e.fileUrl + "/" + e.saveFileName;
						
						t.push([obj]);
						
						return t;
					},[])
					
					this.renderingComponent(document.querySelector('.file_upload_result'), renderingData);
					alert("업로드 성공");
				}
			});	
			
			//input file이 변동 될 때의 이벤트 등록
			targetFiles.onchange = (event) => {
				// 해당 이벤트에서 file 객체 가져오기
				let files = event.target.files;
				
				//잘못된 형식의 파일 필터링하여 실패된 값들만 리턴받아 변수에 대입
				let failData = this.fileFilter(files, {isTrue:false, isFalse:true});
				
				//실패된 값이 없을 경우
				if(failData.length == 0){
					fileList.push(files);
				}else{
					//실패된 값이 있을 경우 얼럿 텍스트 생성
					let alertText = '잘못된 형식의 파일이 있습니다. \n';
					failData.forEach(e=>{
						let fileSize = (e.size / (1024*1024)).toFixed(1);
						if(fileSize > this.fileSizeLimitToMB[0]){
							alertText += e.name + '은 1MB가 초과되는 파일입니다. -> ' + fileSize + 'MB \n';
						}
						if(this.fileTypes.some(type=> type == e.type) == false){
							alertText += e.name + '은 이미지 형식이 아닙니다.';
						}
						alertText += '\n'
					});
					
					//얼럿 노출
					alert(alertText);
					
					//잘못된 형식의 파일은 필터링 하여 적정한 값인 파일만 리턴받아 list에 push한다.
					fileList.push(this.fileFilter(files, {isTrue:true, isFalse:false}));
				}
				//업로드 할 파일 리스트를 view에 노출시킨다.
				//첫번쨰 매개변수 undefined는 디폴트 값으로 들어가게 하기 위해 undefined 값을 보내주는 것
				this.renderingComponent(undefined, fileList);
			}
			
		}
		
		//데이터를 view에 노출시키는 함수
		renderingComponent(targetWrap = document.querySelector('.file_wrap'), fileList){
			
			const fragment = new DocumentFragment();
			let result = fileList.reduce((t,e,i)=>{
				
				[...e].forEach(e=>{
					let div = document.createElement('div');
					Object.assign( div.style, { display : 'flex', flex : '1 1', gap : '1rem', alignItems : 'center' });
					
					div.append(this.createViewFileComponent(e));
					fragment.append(div)
					t.push(e);
				});
				
				return t;
			},[])
			
			//list에 있는 데이터를 targetWrap HTML에 랜더링 시킨다. (기존 데이터를 제거 후 새 데이터로 replace)
			targetWrap.replaceChildren(fragment);
			
			//return result;
		}
		
		//객체에서 url이라는 key값이 없고 타입이 File 타입인 경우 File 타입의 url을 생성하여 리턴하는 함수
		createFileObjectURL(obj){
			console.log(Object.prototype.toString.call(obj));
			if(Object.prototype.hasOwnProperty.call(obj,'url')){
				return obj.url;
			}else if(obj instanceof File){
				return URL.createObjectURL(obj);
			}else{
				return;
			}
		}
		
		//fileName, size, type 별로 view에 그려줄 html 객체를 생성하는 함수
		createViewFileComponent(file){
			let fragment = new DocumentFragment();
			let image = document.createElement('img')
			image.src = this.createFileObjectURL(file);
			Object.assign( image.style, {width : '100px', height : '100px'} );
			
			let name = document.createElement('div');
			name.className = 'fileName';
			let size = document.createElement('div');
			size.className = 'size';
			let type = document.createElement('div')
			type.className = 'type';
			
			name.textContent = 'name : ' + file.name;
			size.textContent = 'size : ' + file.size;
			type.textContent = 'type : ' + file.type;
			
			fragment.append(...[image,name,size,type]);
			return fragment
		}
		
		//파일의 타입과 사이즈를 체크하여 벨리데이션 하는 함수
		fileFilter(files, filterYnOption = {isTrue : true, isFalse : false}){
			//console.log(files);
			return [...files].filter(obj=>{
				if(this.fileTypes.some(e=> e == obj.type) == false) {
					//throw 'This File Type Is Not Image File';
					return filterYnOption.isFalse;
				}
				else if( (obj.size / (1024*1024)).toFixed(3) > this.fileSizeLimitToMB[0] ) {
					//throw 'This File Size Over Limit Max File Size';
					return filterYnOption.isFalse;
				}
				else{
					return filterYnOption.isTrue;
				}
			});
		} 
		
		//파일 업로드를 수행하는 함수
		async fileUpload(fileList=[]){
			
			if(fileList.length == 0){
				alert('파일이 선택되지 않음');
				return;
			}
			
			let formData = new FormData();
			//fileList에 있는 File 객체를 form에 추가한다.
			fileList.map(e=>{
				[...e].forEach(file=>{
					formData.append('files', file, file.name);
				});
			})
			/* form 태그에 제대로 값이 셋팅됐는지 확인
			for(let data of formData.entries()){
				console.log(data[0] + " : " + data[1].name);
			}
			*/
			
			//서버에 파일 업로드를 요청
			return await fetch('/fileUpload', { 
				method : 'POST', 
				headers : {},
				body : formData
			}).then((response)=>{
				console.log(response);
				//console.log(response.json()); response.json()은 read객체로 한 번 읽은 후 다시 사용 불가
				if(response.ok || response.status === 200){
					console.log(response);
					return response.json();
				}else{
					throw new Error ('서버 통신 오류, 다음 기회에..');
					
					//ex)
					//throw new EvalError ('서버 통신 오류, 다음 기회에..'); //or
					//throw new RangeError ('서버 통신 오류, 다음 기회에..'); //or
				}
			}).catch(errorMessage=>{
				return errorMessage
			});/*.then((result)=>{
				console.log(result instanceof Error);
				if(result instanceof Error) {
					alert(result.toString())
				}else{
					let renderingData = result.reduce((t,e,i)=>{
						let div = document.createElement('div');
						Object.assign( div.style, { display : 'flex', flex : '1 1', gap : '1rem', alignItems : 'center' });
						console.log(e);
						let obj = {}
						obj.name = e.originFileName;
						obj.size = e.fileSize;
						obj.type = e.extension;
						obj.url = e.fileUrl + "/" + e.saveFileName;
						
						t.push([obj]);
						
						return t;
					},[])
					
					this.renderingComponent(document.querySelector('.file_upload_result'), renderingData);
					alert("업로드 성공");
				}
			});	*/
		}
		
	}
	//const inputFileHandler = new InputFileHandler();
	new InputFileHandler();
	
</script>


<form method=post action="/PrevTargetPage">
<input type=submit value=확인>
</form>

<form method=post action="/chatTest">
<input type=submit value=chat>
</form>

<a href="/chatTest_multi_access">chatTest_multi_access</a>

<input id="textValue" type=text/>
<button onclick="submitTestFetch1(this)">버튼1</button>
<button onclick="submitTestFetch2(this)">버튼2</button>
<script>

	function submitTestFetch1(obj){

		fetch('/submitTestFetch1',{
			method : 'POST',
			headers : {'Content-Type': 'application/json; charset=utf-8'},
			body : JSON.stringify( {'test' : document.querySelector('#textValue').value.toString() } )
		}).then(res=>{
			console.log(res)
		})
	}

	function submitTestFetch2(obj){
		let data = new FormData();
		data.append("test", document.querySelector('#textValue').value);
		
		fetch('/submitTestFetch2',{
			method : 'POST',
			headers : {},
			body : data
		}).then(res=>{
			console.log(res)
		})
	}

</script>
</body>
</html>
function chkForm(){
	var f = document.frm;
	
	if(f.title.value ==''){ //값이 없으면
		alert("제목을 입력해주십시오.");
		return false;
	}
	
	if(f.user_id.value ==''){ //값이 없으면
		alert("아이디를 입력해주십시오.");
		return false;
	}
	
	f.submit(); //폼태그 전송
	
	
}
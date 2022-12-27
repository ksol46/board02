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
	
	function chkDelete(board_no) {
		const result = confirm("삭제하시겠습니까?");
		
		if(result) {
			const url = location.origin; //'http://localhost:8082'
			
			location.href = url + "/board02/delete?board_no=" + board_no; //페이지 이동
				
			} else {
				return false;
			}
		}
	
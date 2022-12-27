package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import DTO.Board;

public class BoardDAO {
	//DB설정
	final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    
    public Connection open () {
    	Connection conn = null;
    	try {
    		Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(JDBC_URL, "test", "test1234");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return conn; //데이터 베이스의 연결 객체를 리턴
    }

    
    //게시판 리스트 가져오기
    public ArrayList<Board> getList() throws Exception{
    	Connection conn = open();
    	ArrayList<Board> boardList = new ArrayList<>(); //데이터 저장 할 arraylist
    	
    	String sql = "select board_no, title, user_id, to_char(reg_date, 'yyyy.mm.dd') reg_date, views from board";
    	PreparedStatement pstmt = conn.prepareStatement(sql); //쿼리문 등록 -> 컴파일
    	ResultSet rs = pstmt.executeQuery(); //쿼리문 실행 -> 데이터 베이스 결과 저장
    	
    	//리소스 자동 닫기 (try-whit-resource)
    	//try 괄호 안에 변수명을 적으면 알아서 적용이 된다.
		/*
		 * try (conn; pstmt; rs) {
		 * 
		 * } catch (Exception e) {
		 * 
		 * } finally {
		 * conn.close();
		 * ps.close();
		 * rs.close();
		 * }
		 */
    	
    	try(conn; pstmt; rs){
    		while (rs.next()) {
    			Board b = new Board();
    			b.setBoard_no(rs.getInt(1));
    			b.setTitle(rs.getString(2));
    			b.setUser_id(rs.getString(3));
    			b.setReg_date(rs.getString(4));
    			b.setViews(rs.getInt(5));
    			
    			boardList.add(b); //board객체 7개가 다 들어있음
    			
    		}
    			return boardList;
    	}
    	
    }
    
    //게시판 내용 가져오기
    public Board getView(int board_no) throws Exception {
    	Connection conn = open();
    	Board b = new Board();
    	
    	String sql = " select board_no, title, user_id, to_char(reg_date, 'yyyy.mm.dd') reg_date, views, content from board where board_no = ?";
    	
    	PreparedStatement pstmt = conn.prepareStatement(sql);
    	pstmt.setInt(1, board_no);
    	ResultSet rs = pstmt.executeQuery(); //쿼리문 실행 -> 데이터 베이스 결과 저장
    	
    	rs.next();
    	
    	try(conn; pstmt; rs) {
    			b.setBoard_no(rs.getInt(1));
    			b.setTitle(rs.getString(2));
    			b.setUser_id(rs.getString(3));
    			b.setReg_date(rs.getString(4));
    			b.setViews(rs.getInt(5));
    			b.setContent(rs.getString(6));
    			
    			pstmt.executeQuery();
        		return b;
    		}
	}
    
    //조회수 증가
    public void updateViews(int board_no) throws Exception {
    	Connection conn = open();
    	
    	String sql = "update board set views = (views + 1) where board_no = ?";
    	PreparedStatement pstmt = conn.prepareStatement(sql); //쿼리문 등록 -> 컴파일
    	
    	try (conn; pstmt){
    		pstmt.setInt(1, board_no);
    		pstmt.executeUpdate();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    }

    //게시판 글 등록
    public void insertBoard(Board b) throws Exception {
    
    	Connection conn = open();
    	
    	String sql = "insert into board (board_no, user_id, title, content, reg_date, views)";
    		   sql +=" values(board_seq.nextval, ?, ?, ?, sysdate, 0)";
    		   
        PreparedStatement pstmt = conn.prepareStatement(sql);
        System.out.println("dao");
        try (conn; pstmt) {
			pstmt.setString(1, b.getUser_id());
			pstmt.setString(2, b.getTitle());
			pstmt.setString(3, b.getContent());
			pstmt.executeUpdate();
		} 
        System.out.println("sql");
    }
    
    //글 수정 화면 보여주기
	public Board getViewForEdit (int board_no) throws Exception {
		Connection conn = open();
    	Board b = new Board();
    	
    	String sql = " select board_no, title, user_id, to_char(reg_date, 'yyyy.mm.dd') reg_date, views, content from board where board_no = ?";
    	
    	PreparedStatement pstmt = conn.prepareStatement(sql);
    	pstmt.setInt(1, board_no);
    	ResultSet rs = pstmt.executeQuery(); //쿼리문 실행 -> 데이터 베이스 결과 저장
    	
    	rs.next();
    	
    	try(conn; pstmt; rs) {
    			b.setBoard_no(rs.getInt(1));
    			b.setTitle(rs.getString(2));
    			b.setUser_id(rs.getString(3));
    			b.setReg_date(rs.getString(4));
    			b.setViews(rs.getInt(5));
    			b.setContent(rs.getString(6));
    			
    			pstmt.executeQuery();
        		return b;
    	}
	}
	
	//게시판 글 수정
	public void updateBoard (Board b) throws Exception {
		Connection conn = open();
    	
    	String sql = "update board set title = ?, user_id = ?, content = ? where board_no = ?";
    	PreparedStatement pstmt = conn.prepareStatement(sql); //쿼리문 등록 -> 컴파일
    	
    	try (conn; pstmt){
    		pstmt.setString(1, b.getTitle());
    		pstmt.setString(2, b.getUser_id());
    		pstmt.setString(3, b.getContent());
    		pstmt.setInt(4, b.getBoard_no());
    		
    		//수정된 글이 없을 경우
    		if (pstmt.executeUpdate() !=1) {
    			throw new Exception("DB에러");
    		}
    	}
	}
	
	
	//게시판 글 삭제
	public void deleteBoard(int board_no) throws Exception {
		Connection conn = open();
		
		String sql = "delete from board where board_no=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		try (conn; pstmt){
			pstmt.setInt(1, board_no);
			
			//삭제된 글이 없을 경우
			if(pstmt.executeUpdate() != 1) {
				throw new Exception("DB에러");
			}
		}
	}

	
	
	
}

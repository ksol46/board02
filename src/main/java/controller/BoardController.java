package controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;

import DAO.BoardDAO;
import DTO.Board;

@WebServlet("/")
public class BoardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BoardDAO dao;
	private ServletContext ctx;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		//init은 서블릿 객체 생성시 딱 한 번만 실행하므로 객체를 한 번 생성해 공유할 수 있다.
		dao = new BoardDAO();
		ctx = getServletContext(); //ServletContext: 웹 어플리케이션의 자원 관리
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); //request 객체에 저장된 한글 깨짐 방지
		doPro(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); //request 객체에 저장된 한글 깨짐 방지
		doPro(request, response);
	}
				//routing : 길을 찾아줌, 역할을 해준다.
	protected void doPro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String context = request.getContextPath(); // 톰캣의 Context path를 가져온다(server.xml에서 확인)
		String command = request.getServletPath();
		String site = null;
		
		//경로 라우팅 (경로를 찾아줌)
		switch (command) {
		case "/list":
			site = getList(request);
			break;
		case "/view":
			site = getView(request);
			break;
		case "/write" : //글쓰기 화면만 보여줌
			site = "write.jsp";
			break;
		case "/insert": //insert하는 기능 
			site = insertBoard(request);
			break;
		case "/edit": //수정 화면을 보여줌
			site = getViewForEdit(request);
			break;
		case "/update": //업데이트 기능
			site = updateBoard(request);
			break;
		case "/delete":
			site = deleteBoard(request);
			break;
		}
		
		
		/*
		둘 다 페이지를 이동한다.
		redirect : URL의 변화 있음, 객체의 재사용 X (request, response객체)
		  -> DB에 변화가 생기는 요청에 사용(글쓰기, 회원가입 등등)
		  insert,  update, delete 조심..
		
		forward: URL의 변화 없음(보안..), 객체의 재사용 O (request, response객체)
		  -> 단순하게 보여주는것. (리스트나 검색 등등)
		 */
		
		
		//post요청 처리후에는 redirect 방법으로 이동 할 수 있어야 한다.
			//startWith : 시작하는 곳을 찾음
		if(site.startsWith("redirect:/")) { //redirect/ 문자열 이후 경로만 가지고 옴
			String rview = site.substring("redirect:/".length());
							//substring : 문자열을 자름. length : 길이만큼 잘라준다.
			System.out.println(rview);
			response.sendRedirect(rview);
		} else { //forward
			ctx.getRequestDispatcher("/" + site).forward(request, response);
		}
	}
	
	public String getList(HttpServletRequest request) {
		List<Board> list;
		
		try {
			list = dao.getList();
			request.setAttribute("boardList", list);
		} catch (Exception e) {
			e.printStackTrace();
			ctx.log("게시판 목록 생성 과정에서 문제 발생"); //log : 콘솔에 찍힘
			
			//나중에 사용자한테 에러메시지를 보여주기 위해 저장
			request.setAttribute("error","게시판 목록이 정상적으로 처리되지 않았습니다!");
		}
		
		return "index.jsp";
	}
	
	public String getView(HttpServletRequest request) {
		int board_no = Integer.parseInt(request.getParameter("board_no"));
		try {
			dao.updateViews(board_no); //조회수 증가
			Board b = dao.getView(board_no);
			request.setAttribute("board", b);
		} catch (Exception e) {
			e.printStackTrace();
			ctx.log("게시글을 과정에서 과정에서 문제 발생");
			request.setAttribute("error","게시글을 정상적으로 가져오지 않았습니다!");
		}
		
		return "view.jsp";
	}
	
	 public String insertBoard(HttpServletRequest request) {
		 System.out.println("insert");
		 Board b = new Board();
		 
		 try{
			 
			 BeanUtils.populate(b, request.getParameterMap());
			 
			 dao.insertBoard(b);
			 System.out.println("bean");
			 
		 }catch (Exception e) {
			 e.printStackTrace();
				ctx.log("추가 과정에서 문제 발생!!");
				try {
					//get 방식으로 넘겨줄 때 한글 깨짐을 방지한다.
					String encodeName = URLEncoder.encode("게시물이 정상적으로 등록되지 않았습니다!!","UTF-8");
					return "redirect:/list?error=" + encodeName;
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
		 }
		 //사용자에게 에러메시지를 보여주기 위해 저장
		 request.setAttribute("error","게시글을 정상적으로 등록되지 않았습니다!");
		 return getList(request); 
		 }
	 		return "redirect:/list";
	 		//return "/list";
	 	}
	 
	 
	 public String getViewForEdit (HttpServletRequest request) {
			int board_no = Integer.parseInt(request.getParameter("board_no"));
			
			try {
				Board b = dao.getViewForEdit(board_no);
				request.setAttribute("board", b);
			} catch (Exception e) {
				e.printStackTrace();
				ctx.log("게시글을 과정에서 과정에서 문제 발생");
				request.setAttribute("error","게시글을 정상적으로 가져오지 않았습니다!");
			}
			
			return "edit.jsp";
		}
	 
	 public String updateBoard (HttpServletRequest request) {
		 Board b = new Board();
		 
		 try {
			 //getParameter에서 얻어온 값을 전부 다 Board 객체로 만들어 준다.
			BeanUtils.populate(b, request.getParameterMap());
			dao.updateBoard(b);
			
		} catch (Exception e) {
			e.printStackTrace();
			ctx.log("수정 과정에서 문제 발생!!");
			try {
				//get 방식으로 넘겨줄 때 한글 깨짐을 방지한다.
				String encodeName = URLEncoder.encode("게시물이 정상적으로 수정되지 않았습니다!!","UTF-8");
				return "redirect:/view?board_no=" + b.getBoard_no() + "&error=" + encodeName;
			
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
		}
			
	 }
		 return "redirect:/view?board_no=" + b.getBoard_no();
	 }
	 
	 
	 public String deleteBoard(HttpServletRequest request) {
		 int board_no = Integer.parseInt(request.getParameter("board_no"));
		 try {
			 dao.deleteBoard(board_no);
		} catch (Exception e) {
			e.printStackTrace();
			ctx.log("게시글을 삭제하는 과정에서 문제 발생");
			try {
				String encodeName = URLEncoder.encode("게시물이 정상적으로 등록되지 않았습니다!!","UTF-8");
				return "redirect:/list?error=" + encodeName;
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
	 }
		}
		 return "redirect:/list";
	 }
	 
	 
}

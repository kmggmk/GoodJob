package com.goodjob.test.mg;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/itvWrite.do")
public class InterviewWrite extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


	        // JSP 페이지로 포워딩
	        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/interview/itvwrite.jsp");
	        dispatcher.forward(req, resp);
	    }
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String id = (String)session.getAttribute("id");
		
		request.setCharacterEncoding("UTF-8");
	    
		String ITV_CPNAME = request.getParameter("itvCpName");
	    String cpSeq = request.getParameter("cp_seq");
	    String itvCareer = request.getParameter("ITV_CAREER");
	    String itvMeetdate = request.getParameter("ITV_MEETDATE");
	    String itvEvaluation = request.getParameter("ITV_EVALUATION");
	    String itvDifficulty = request.getParameter("ITV_DIFFICULTY");
	    String[] itvCategories = request.getParameterValues("ITV_CATEGORY");
	    String itvPersonnel = request.getParameter("ITV_PERSONNEL");
	    String itvQuestion = request.getParameter("ITV_QUESTION");
	    String itvTip = request.getParameter("ITV_TIP");
	    String itvWhether = request.getParameter("ITV_WHETHER");
	
	    
	    System.out.println("ITV_CPNAME:" + ITV_CPNAME);
	    System.out.println("cpSeq: " + cpSeq);
	    System.out.println("itvCareer: " + itvCareer);
	    System.out.println("itvMeetdate: " + itvMeetdate);
	    System.out.println("itvEvaluation: " + itvEvaluation);
	    System.out.println("itvDifficulty: " + itvDifficulty);
	    System.out.println("itvCategories: " + Arrays.toString(itvCategories));
	    System.out.println("itvPersonnel: " + itvPersonnel);
	    System.out.println("itvQuestion: " + itvQuestion);
	    System.out.println("itvTip: " + itvTip);
	    System.out.println("itvWhether: " + itvWhether);
	    System.out.println("id" + id);
	    
	    // 파라미터 값을 사용하여 필요한 로직 처리
	    // 예: 데이터베이스에 면접 후기 정보 저장
	    
	    InterviewDTO dto = new InterviewDTO();
	    dto.setITV_CPNAME(ITV_CPNAME);
	    dto.setCP_SEQ(cpSeq);
	    dto.setITV_CAREER(itvCareer);
	    dto.setITV_MEETDATE(itvMeetdate);
	    dto.setITV_EVALUATION(itvEvaluation);
	    dto.setITV_DIFFICULTY(itvDifficulty);
	    dto.setITV_CATEGORY(String.join(",", itvCategories));
	    dto.setITV_PERSONNEL(itvPersonnel);
	    dto.setITV_QUESTION(itvQuestion);
	    dto.setITV_TIP(itvTip);
	    dto.setITV_WHETHER(itvWhether);
	    dto.setID(id);
	    
	    InterviewDAO dao = new InterviewDAO();
	    int result = dao.Write(dto);
	    
	    if (result > 0) {
	      response.sendRedirect("/good/interview.do");
	    } else {
	    	System.out.println("interviewWrite 글쓰기 실패");
	      response.sendRedirect("/good/interview.do");
	    }
	  }
	}
	
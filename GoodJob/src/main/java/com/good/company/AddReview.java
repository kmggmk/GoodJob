package com.good.company;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.json.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.good.board.repository.BoardDAO;
import com.good.company.model.CompanyDTO;
import com.good.company.model.ReviewDTO;
import com.good.company.repository.CompanyDAO;
import com.good.company.repository.ReviewDAO;

@WebServlet("/user/company/review/addreview.do")
public class AddReview extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String cp_seq = req.getParameter("cp_seq");
		String page = req.getParameter("page");
	
		CompanyDAO dao = new CompanyDAO();
		CompanyDTO dto = dao.get(cp_seq);
		
		ReviewDAO rdao = new ReviewDAO();
		//ReviewDTO rdto = new ReviewDTO();
		ArrayList<String> showTagList = rdao.showTagList();
		//System.out.println(showTagList.size());
	
		req.setAttribute("cp_seq",cp_seq);
		req.setAttribute("page",page);
		req.setAttribute("dto", dto);
		req.setAttribute("showTagList",showTagList);

		RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/company/review/addreview.jsp");
		dispatcher.forward(req, resp);
		
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		
		HttpSession session = req.getSession();
		String id = (String)session.getAttribute("id");
		
		
		double salary_score = Double.parseDouble(req.getParameter("salary_score"));
		double welfare_score= Double.parseDouble(req.getParameter("welfare_score"));
		double stability_score = Double.parseDouble(req.getParameter("stability_score"));
		double culture_score= Double.parseDouble(req.getParameter("culture_score"));
		double growth_score=  Double.parseDouble(req.getParameter("growth_score"));
		
		String linereview = req.getParameter("linereview");
		String good = req.getParameter("good");
		String bad = req.getParameter("bad");
		String tag_keyword = req.getParameter("tag_keyword");
		int cp_rv_confirm = Integer.parseInt(req.getParameter("cp_rv_confirm"));
		String cp_seq = req.getParameter("cp_seq");
		
		
		ReviewDAO dao = new ReviewDAO();
		ReviewDTO dto = new ReviewDTO();
		
		dto.setSalary_score(salary_score);
		dto.setWelfare_score(welfare_score);
		dto.setStability_score(stability_score);
		dto.setCulture_score(culture_score);
		dto.setGrowth_score(growth_score);
		dto.setLinereview(linereview);
		dto.setGood(good);
		dto.setBad(bad);
		dto.setId(id);
		dto.setCp_rv_confirm(cp_rv_confirm);
		dto.setCp_seq(cp_seq);
		
		
		
		
		
		
		///TODO 해시태그작업
		if (tag_keyword != null && !tag_keyword.equals("")&& !tag_keyword.equals("[]")) {
			try {
				
				//[{"value":"자바"},{"value":"코딩"},{"value":"게시판"}]
				//System.out.println(tag);
				JSONParser parser = new JSONParser();				
				JSONArray arr = (JSONArray)parser.parse(tag_keyword); //배열 > JSONArray
				
				for (Object obj : arr) { //arr(JSONObject)이지만 바로 가져오면 오류나서 일단 Object로 가져옴
					JSONObject tagObj = (JSONObject)obj;
					String tagName = (String)tagObj.get("value");
					//System.out.println(tagName);
					
					//해시태그 추가 (유니크조건확인)
					if(dao.existHashtag(tagName)) {					
						dao.addHashtag(tagName);
					} 
					//해시태그번호
					String hseq = dao.getHseq(tagName);
					
					
					//관계추가
					HashMap<String,String> map = new HashMap<String,String>();
					map.put("bseq", bseq);
					map.put("hseq", hseq);
					dao.addTagging(map);
					
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
		
		
		//TODO DB작업하기
		//int result = dao.addReview();
		//String path = "/good/user/company/companyview.do?cp_seq="+cp_seq;
		//if (result == 1) {
//		resp.sendRedirect("path");
		//resp.sendRedirect("/user/company/companylist.do");
		resp.sendRedirect("/good/user/company/companylist.do");
		/*} else {
			resp.setCharacterEncoding("UTF-8");
			PrintWriter writer = resp.getWriter();
			writer.print(OutputUtil.redirect("실패했습니다."));
			writer.close();
		}*/
		
		
	}
}
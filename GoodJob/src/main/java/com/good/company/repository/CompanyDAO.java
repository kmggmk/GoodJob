package com.good.company.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.good.company.model.CompanyDTO;
import com.test.util.DBUtil;

public class CompanyDAO {
	private Connection conn;
	private Statement stat;
	private PreparedStatement pstat;
	private ResultSet rs;

	public CompanyDAO() {
		this.conn = DBUtil.open();
	}

	public void close()  {
        try {
            this.conn.close();
        } catch (Exception e) {
            System.out.println("CompanyDAO.close 오류");
            e.printStackTrace();
        }
    }
	public ArrayList<CompanyDTO> rcrtCompany() {

		try {

			String sql = "select cp_seq, cp_name, image from tblCompany";

			stat = conn.createStatement();
			rs = stat.executeQuery(sql);

			ArrayList<CompanyDTO> listCompany = new ArrayList<CompanyDTO>();
			while (rs.next()) {

				CompanyDTO dto = new CompanyDTO();

				dto.setCp_seq(rs.getString("cp_seq"));
				dto.setCp_name(rs.getString("cp_name"));
				dto.setCp_address(rs.getString("cp_address"));

				listCompany.add(dto);

			}
			return listCompany;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public List<CompanyDTO> searchCompany(String input) {
		try {
			String sql = "SELECT * FROM (SELECT * FROM tblcompany WHERE cp_name LIKE ? order by cp_name) WHERE ROWNUM <= 5";

			pstat = conn.prepareStatement(sql);
			String search = input + "%";
			pstat.setString(1, search);

			rs = pstat.executeQuery();

			List<CompanyDTO> list = new ArrayList<>();
			while (rs.next()) {
				CompanyDTO dto = new CompanyDTO();
				dto.setCp_name(rs.getString("cp_name"));
				dto.setCp_seq(rs.getString("cp_seq"));

				list.add(dto);
			}
			pstat.close();
			return list;

		} catch (Exception e) {
			System.out.println("CompanyDAO.searchCompany");
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 등록된 기업수 불러오는 메서드
	 * @return 기업수
	 */
	public int countCompanys() {
		
		try {

			String sql = "select count(*) as cnt from tblCompany";

			stat = conn.createStatement();
			rs = stat.executeQuery(sql);

			if (rs.next()) {
				return rs.getInt("cnt");
			}

		} catch (Exception e) {
			System.out.println("등록된 기업수 로드 실패");
			e.printStackTrace();
		}
		
		return 0;
		
	}
	
	/**
	 * 지유)기업 목록, 상세 최신 정보 불러오는 메서드
	 * @return 기업정보(일반/재무/고용)
	 */
	public ArrayList<CompanyDTO> comListInfo(HashMap<String,String> map){
		
	
		try {
			
			String sql ="";
			String where = "";
			
			
			//검색
			if(map.get("search").equals("y")) {
				
				where = String.format("where %s like '%%%s%%'",map.get("colmn"),map.get("word"));
				sql = String.format("select * from (select a.*, rownum as rnum from vwNewComListInfo a %s) where rnum between %s and %s"
						, where
						, map.get("begin")
						, map.get("end"));
			}else {
				sql = String.format("select * from (select a.*, rownum as rnum from vwNewComListInfo a) where rnum between %s and %s"
						, map.get("begin")
						, map.get("end"));
			}
			
			
		
			//TODO 연봉검색, 주소검색, 업종검색
			

			stat = conn.createStatement();
			rs = stat.executeQuery(sql);

			ArrayList<CompanyDTO> listCompanyInfo = new ArrayList<CompanyDTO>();
			
			while (rs.next()) {

				CompanyDTO dto = new CompanyDTO();

				dto.setCp_seq(rs.getString("cp_seq"));
				dto.setCp_name(rs.getString("cp_name"));
				dto.setCp_address(rs.getString("cp_address"));
				dto.setCeo(rs.getString("ceo"));
				dto.setFounded(rs.getString("founded"));
				dto.setImage(rs.getString("image"));
				
				dto.setIdst_code(rs.getString("idst_code"));
				dto.setIdst_name(rs.getString("idst_name"));
				
			
				dto.setHire_member(rs.getInt("hire_member"));
				dto.setHire_new(rs.getInt("hire_new"));
				dto.setHire_retired(rs.getInt("hire_retired"));
				dto.setHire_avr_year(rs.getInt("hire_avr_year"));
				dto.setHire_avr_salary(rs.getInt("hire_avr_salary"));
				dto.setHire_regdate(rs.getString("hire_regdate"));
				
				
				dto.setFnc_sales(rs.getLong("fnc_sales"));
				dto.setFnc_ebit(rs.getLong("fnc_ebit"));
				dto.setFnc_income(rs.getLong("fnc_income"));
				dto.setFnc_period(rs.getString("fnc_period"));
				dto.setFnc_regdate(rs.getString("fnc_regdate"));
				
				
				listCompanyInfo.add(dto);

			}
			return listCompanyInfo;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
		
	}

	public int searchCompanyCount(HashMap<String, String> map) {
		
		try {

			String where ="";
			String sql = "";
			
			if (map.get("search").equals("y")) {
				
				where = String.format("where %s like '%%%s%%'", map.get("column"),map.get("word"));
				sql = String.format("select count(*) as search_cnt  from vwComListInfo %s" , where);
				
			}
			
			pstat = conn.prepareStatement(sql);

			rs = pstat.executeQuery();

			if (rs.next()) {
				
				return rs.getInt("search_cnt");
			
			}

		} catch (Exception e) {
			System.out.println("CompanyDAO.searchCompanyCount");
			e.getStackTrace();
		}
		
		return 0;
	}
	
	//TODO job dao 만들기
	

}




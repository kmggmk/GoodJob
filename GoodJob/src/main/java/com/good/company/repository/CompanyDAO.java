package com.good.company.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.good.board.model.CommentDTO;
import com.good.company.model.CompanyDTO;
import com.good.company.model.ScrapDTO;
import com.good.matching.model.MatchingDTO;
import com.test.util.DBUtil;

public class CompanyDAO {
	private Connection conn;
	private Statement stat;
	private PreparedStatement pstat;
	private ResultSet rs;

	public CompanyDAO() {
		this.conn = DBUtil.open();
	}

	public void close() {

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

	public ArrayList<CompanyDTO> mainComList(HashMap<String, String> map) {

		try {
			
			String sql ="select * from vwMainComList where score > 3.5";
			String where = " and com_rcrt_cnt > 0";
			
			//검색
			if(map.get("hiring").equals("y")) {
				
				sql += where;
			
			}
			System.out.println(sql);

			stat = conn.createStatement();
			rs = stat.executeQuery(sql);

			ArrayList<CompanyDTO> mainComInfo = new ArrayList<CompanyDTO>();

			while (rs.next()) {

				CompanyDTO dto = new CompanyDTO();

				dto.setCp_seq(rs.getString("cp_seq"));
				dto.setCp_name(rs.getString("cp_name"));

				dto.setImage(rs.getString("image"));
				dto.setReview_avg(rs.getString("score"));
				dto.setIdst_name(rs.getString("idst_name"));
				dto.setCom_rcrt_cnt(rs.getInt("com_rcrt_cnt"));
				mainComInfo.add(dto);

			}
			System.out.println(mainComInfo.size());
			return mainComInfo;

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
	 * 
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
	 * 
	 * @return 기업정보(일반/재무/고용)
	 */
	public ArrayList<CompanyDTO> comListInfo(HashMap<String, String> map) {

		try {

			String sql = "";
			String where = "";

	        // 검색어 조건 추가 >>> conflict 나서 일단 주석 처리 해놔요 -희연
	        if (map.get("search").equals("y")) {
	            where += "cp_name LIKE '%" + map.get("word") + "%' AND ";
	        }

	        // 채용 중인 기업 조건 추가
	        if (map.get("hiring").equals("y")) {
	            where += "com_rcrt_cnt > 0 AND ";
	        }

	        // 연봉 조건 추가
	        if (map.get("salary_seq") != null && !map.get("salary_seq").isEmpty()) {
	            where += "hire_avr_salary >= " + map.get("salary_seq") + " AND ";
	        }

	        // 지역 조건 추가
	        if (map.get("cp_address") != null && !map.get("cp_address").isEmpty()) {
	            String[] locations = map.get("cp_address").split(",");
	            where += "(";
	            for (int i = 0; i < locations.length; i++) {
	                where += "cp_address LIKE '%" + locations[i] + "%'";
	                if (i < locations.length - 1) {
	                    where += " OR ";
	                }
	            }
	            where += ") AND ";
	        }

	        // 조건절 마지막의 AND 제거
	        if (where.endsWith("AND ")) {
	            where = where.substring(0, where.length() - 4);
	        }

	        // 정렬 기준 추가
	        String orderBy = "";
	        if (map.get("sort").equals("review")) {
	            orderBy = "com_rv_cnt DESC";
	        } else if (map.get("sort").equals("salary")) {
	            orderBy = "hire_avr_salary DESC";
	        }

	        sql = "SELECT * FROM (SELECT a.*, ROWNUM AS rnum FROM (SELECT * FROM vwListCompany " +
	                (where.isEmpty() ? "" : "WHERE " + where) +
	                " ORDER BY " + orderBy + ") a) WHERE rnum BETWEEN " + map.get("begin") + " AND " + map.get("end");

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
				// dto.setHire_regdate(rs.getString("hire_regdate"));

				dto.setFnc_sales(rs.getLong("fnc_sales"));
				dto.setFnc_ebit(rs.getLong("fnc_ebit"));
				dto.setFnc_income(rs.getLong("fnc_income"));
				dto.setFnc_period(rs.getString("fnc_period"));
				// dto.setFnc_regdate(rs.getString("fnc_regdate"));

				dto.setCom_rcrt_cnt(rs.getInt("com_rcrt_cnt"));
				dto.setCom_scrap_cnt(rs.getInt("com_scrap_cnt"));
				dto.setCom_rv_cnt(rs.getInt("com_rv_cnt"));
				
				listCompanyInfo.add(dto);

			}
	        //System.out.println(listCompanyInfo);
			return listCompanyInfo;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}
	public int getCompanyCount(HashMap<String, String> map) {
	    try {
	        String sql = "";
	        String where = "";

	        // 검색어 조건 추가
	        if (map.get("search").equals("y")) {
	            where += "cp_name LIKE '%" + map.get("word") + "%' AND ";
	        }

	        // 채용 중인 기업 조건 추가
	        if (map.get("hiring").equals("y")) {
	            where += "com_rcrt_cnt > 0 AND ";
	        }

	        // 연봉 조건 추가
	        if (map.get("salary_seq") != null && !map.get("salary_seq").isEmpty()) {
	            where += "hire_avr_salary >= " + map.get("salary_seq") + " AND ";
	        }

	        // 지역 조건 추가
	        if (map.get("cp_address") != null && !map.get("cp_address").isEmpty()) {
	            String[] locations = map.get("cp_address").split(",");
	            where += "(";
	            for (int i = 0; i < locations.length; i++) {
	                where += "cp_address LIKE '%" + locations[i] + "%'";
	                if (i < locations.length - 1) {
	                    where += " OR ";
	                }
	            }
	            where += ") AND ";
	        }

	        // 조건절 마지막의 AND 제거
	        if (where.endsWith("AND ")) {
	            where = where.substring(0, where.length() - 4);
	        }

	        sql = "SELECT COUNT(*) AS cnt FROM vwListCompany " +
	                (where.isEmpty() ? "" : "WHERE " + where);

	        stat = conn.createStatement();
	        rs = stat.executeQuery(sql);

	        if (rs.next()) {
	            return rs.getInt("cnt");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return 0;
	}
	/**
	 * 채용 공고가 있는 기업 목록 조회 메서드
	 * @return
	 */
	/*public ArrayList<CompanyDTO> getCompaniesWithRecruitment() {
	    try {
	        LocalDate currentDate = LocalDate.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        String formatCurrentDate = currentDate.format(formatter);

	        String sql = "SELECT c.cp_seq, c.cp_name, c.cp_address, c.image " +
	                     "FROM tblCompany c " +
	                     "INNER JOIN tblRecruit r ON c.cp_seq = r.cp_seq " +
	                     "WHERE r.enddate >= ? " +
	                     "GROUP BY c.cp_seq, c.cp_name, c.cp_address, c.image " +
	                     "HAVING COUNT(r.rcrt_seq) > 0";

	        pstat = conn.prepareStatement(sql);
	        pstat.setString(1, formatCurrentDate);

	        rs = pstat.executeQuery();

	        ArrayList<CompanyDTO> companiesWithRecruitment = new ArrayList<>();

	        while (rs.next()) {
	            CompanyDTO dto = new CompanyDTO();

	            dto.setCp_seq(rs.getString("cp_seq"));
	            dto.setCp_name(rs.getString("cp_name"));
	            dto.setCp_address(rs.getString("cp_address"));
	            dto.setImage(rs.getString("image"));

	            companiesWithRecruitment.add(dto);
	        }

	        return companiesWithRecruitment;

	    } catch (Exception e) {
	        System.out.println("RecruitDAO.getCompaniesWithRecruitment");
	        e.printStackTrace();
	    }

	    return null;
	}*/
	
	
	/**
	 * 검색결과에 따른 기업수를 부러오는 메서드
	 * 
	 * @param map
	 * @return
	 */
	/*public int searchCompanyCount(HashMap<String, String> map) {

		try {

			String where = "";
			String sql = "";

//			if (map.get("search").equals("y")) {
//				
//				where = String.format("where cp_name like '%%%s%%'",map.get("word"));
//				sql = String.format("select count(*) as search_cnt from vwComListInfo %s" , where);
//				
//			}

			if (map.get("search").equals("y") && map.get("hiring").equals("y")) {

				where = String.format("where cp_name like '%%%s%%' and com_rcrt_cnt > 0", map.get("word"));
				sql = String.format("select count(*) as search_cnt from vwNewComListInfo %s", where);

			} else if (map.get("search").equals("y") && map.get("hiring").equals("n")) {

				where = String.format("where cp_name like '%%%s%%'", map.get("word"));
				sql = String.format("select count(*) as search_cnt from vwNewComListInfo %s", where);

			} else if (map.get("search").equals("n") && map.get("hiring").equals("y")) {

				where = String.format("where com_rcrt_cnt > 0");
				sql = String.format("select count(*) as search_cnt from vwNewComListInfo %s", where);

			} else {

				sql = String.format("select count(*) as search_cnt from vwNewComListInfo");
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
	}*/

//TODO 수정해야함
	public CompanyDTO get(String cp_seq) {

		try {
			String sql = "select * from vwComDetailInfo where cp_seq = ?";

			pstat = conn.prepareStatement(sql);
			pstat.setString(1, cp_seq);

			rs = pstat.executeQuery();

			if (rs.next()) {

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

				dto.setHire_member(rs.getInt("hire_member"));
				dto.setHire_new(rs.getInt("hire_new"));
				dto.setHire_retired(rs.getInt("hire_retired"));
				dto.setHire_avr_year(rs.getInt("hire_avr_year"));
				dto.setHire_avr_salary(rs.getInt("hire_avr_salary"));
				dto.setHire_regdate(rs.getString("hire_regdate"));

				dto.setCom_rcrt_cnt(rs.getInt("com_rcrt_cnt"));
				dto.setCom_scrap_cnt(rs.getInt("com_scrap_cnt"));


				return dto;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public ArrayList<Long>[] getCompanyFinance(String cp_seq) {
		ArrayList<Long> salesList = new ArrayList<>();
		ArrayList<Long> ebitList = new ArrayList<>();
		ArrayList<Long> incomeList = new ArrayList<>();
		String sql = "select * from tblFinance where cp_seq = ?";

		try (PreparedStatement pstat = conn.prepareStatement(sql)) {
			pstat.setString(1, cp_seq);
			try (ResultSet rs = pstat.executeQuery()) {
				while (rs.next()) {

					long sales = rs.getLong("fnc_sales");
					long ebit = rs.getLong("fnc_ebit");
					long income = rs.getLong("fnc_income");

					salesList.add(sales);
					ebitList.add(ebit);
					incomeList.add(income);
				}
			}
		} catch (Exception e) {
			System.out.println("getCompanyFinance");
			e.printStackTrace();
		}

		return new ArrayList[] { salesList, ebitList, incomeList };
	}

	/**
	 * 업계평균연봉 조회 메서드
	 * 
	 * @param idst_code
	 * @return
	 */
	public int getIdstSalary(String idst_code) {
		try {
			String sql = "select * from vwIdstAvgSalary where idst_code = ?";

			pstat = conn.prepareStatement(sql);
			pstat.setString(1, idst_code);

			rs = pstat.executeQuery();

			if (rs.next()) {

				int idst_avg_salary = rs.getInt("idst_avg_salary");

				return idst_avg_salary;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int AddLiveComment(CommentDTO cmdto) {
		// 실시간 댓글 insert
		try {

			String sql = "INSERT INTO tblLiveComment (LIVE_SEQ, LIVE_CONTENT, CP_SEQ, ID, LIVE_CM_REGDATE) VALUES (SEQLIVECOMMENT.nextval, ?, ?, ?, sysdate)";
			pstat = conn.prepareStatement(sql);
			pstat.setString(1, cmdto.getContent());
			pstat.setString(2, cmdto.getCp_seq());
			pstat.setString(3, cmdto.getId());

			return pstat.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("실시간 댓글 insert 실패");
		}

		return 0;
	}

	public CommentDTO getComment(String cp_seq) {
		try {

			String sql = "select * from vwLiveComment where  cp_seq = ? and live_seq = (select max(live_seq) from vwlivecomment where cp_seq=?)";

			pstat = conn.prepareStatement(sql);
			pstat.setString(1, cp_seq);
			pstat.setString(2, cp_seq);

			rs = pstat.executeQuery();

			if (rs.next()) {

				CommentDTO dto = new CommentDTO();

				dto.setCm_seq(rs.getString("live_seq"));
				dto.setContent(rs.getString("live_content"));
				dto.setCp_seq(rs.getString("cp_seq"));
				dto.setRegdate(rs.getString("live_cm_regdate"));
				dto.setNickname(rs.getString("nickname"));

				return dto;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public ArrayList<CommentDTO> listComment(String cp_seq) {
		try {

			String sql = "SELECT * FROM vwLiveComment WHERE cp_seq = ? ORDER BY LIVE_seq ASC";
			pstat = conn.prepareStatement(sql);
			pstat.setString(1, cp_seq);
			rs = pstat.executeQuery();
			ArrayList<CommentDTO> clist = new ArrayList<>();

			while (rs.next()) {
				CommentDTO dto = new CommentDTO();

				dto.setCm_seq(rs.getString("live_seq"));
				dto.setContent(rs.getString("live_content"));
				dto.setCp_seq(rs.getString("cp_seq"));
				dto.setRegdate(rs.getString("live_cm_regdate"));
				dto.setNickname(rs.getString("nickname"));
				dto.setId(rs.getString("id"));

				clist.add(dto);
			}
			return clist;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int delComment(String seq) {
		try {
			String sql = "delete from tblliveComment where live_seq = ?";
			System.out.println("a");
			pstat = conn.prepareStatement(sql);
			System.out.println(seq);
			pstat.setString(1, seq);
			System.out.println("b");
			System.out.println(sql);
			
			
			return pstat.executeUpdate();
			

		} catch (Exception e) {
			System.out.println("CompanyDAO.delComment");
			e.printStackTrace();
		}

		return 0;

	}

	//TODO job dao 만들기
	
	
	public CompanyDTO getMatchingInfo(MatchingDTO mdto) {
		
		try {
				
				String sql = "select * from vwCompanyInfo where cp_seq = ?";
				
				pstat = conn.prepareStatement(sql);
				pstat.setString(1, mdto.getCp_seq());
				
				rs = pstat.executeQuery();
				
				CompanyDTO dto = new CompanyDTO();
				
				if(rs.next()) {
					
					dto.setCp_name(rs.getString("cp_name"));
					dto.setReview_avg(rs.getString("score"));
					dto.setIdst_name(rs.getString("idst_name"));
					dto.setImage(rs.getString("image"));
					dto.setWel_avg(rs.getString("wel_avg"));
					dto.setStab_avg(rs.getString("stab_avg"));
					dto.setSal_avg(rs.getString("sal_avg"));
					dto.setCul_avg(rs.getString("cul_avg"));
					dto.setPot_avg(rs.getString("pot_avg"));
					dto.setHire_avr_salary(rs.getInt("hire_avr_salary"));
					
					return dto;
					
				}
				
			} catch (Exception e) {
				System.out.println("기업 매칭 정보 로드 실패");
				e.printStackTrace();
			}
		
		return null;
		
	}
	
	

	public int addScrap(String id, String cpSeq) {
		// 실시간 댓글 insert
				try {

					String sql = "INSERT INTO tblscrap (id, cp_seq) VALUES (?, ?)";
					pstat = conn.prepareStatement(sql);
					pstat.setString(1, id);
					pstat.setString(2, cpSeq);
					
					return pstat.executeUpdate();

				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("스크랩 insert 실패");
				}

				return 0;
	}

	public ArrayList<ScrapDTO> writeScrap(String id) {
	    try {
	    	String sql = "SELECT s.id, c.cp_name, c.image AS image, c.cp_seq, i.idst_name FROM tblScrap s JOIN tblCompany c ON s.cp_seq = c.cp_seq JOIN tblIndustry i ON c.idst_code = i.idst_code LEFT JOIN tblRecruit r ON c.cp_seq = r.cp_seq where s.id=?";
	        pstat = conn.prepareStatement(sql);
	        pstat.setString(1, id);
	        rs = pstat.executeQuery(); 

	        ArrayList<ScrapDTO> ScrapList = new ArrayList<ScrapDTO>();
	        while (rs.next()) {
	            ScrapDTO dto = new ScrapDTO();
	            dto.setCp_name(rs.getString("cp_name"));
	            dto.setID(rs.getString("id"));
	            dto.setImage(rs.getString("image"));
	            dto.setIdst_name(rs.getString("idst_name"));
	            dto.setCp_seq(rs.getString("cp_seq"));
	            ScrapList.add(dto);
	        }
	        return ScrapList;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	

	
	
	public HashMap<String, String> getUpdateDate(){
		
		HashMap<String,String> map = new HashMap<>();
		
		try {	
				
				String sql = "select * from vwUpdateDate";
				
				stat = conn.createStatement();
				rs = stat.executeQuery(sql);
				
				
				while(rs.next()) {
					map.put(rs.getString("data_type"), rs.getString("regdate"));
				}
				
			} catch (Exception e) {
				System.out.println("CompanyDAO.getUpdateDate");
				e.printStackTrace();
			}
			
		return map;
	}
	
	public int delScrap(String cp_seq) {
		try {
			String sql = "delete from tblscrap where cp_seq = ?";

			pstat = conn.prepareStatement(sql);
		
			pstat.setString(1, cp_seq);
				
			return pstat.executeUpdate();
			

		} catch (Exception e) {
			System.out.println("delScrap");
			e.printStackTrace();
		}

		return 0;
		
	}
public ArrayList<String> getTaglist(String cp_seq){
		
		try {	
				
				String sql = "select * from vwCompanyTag where cp_seq = ?";
				
				pstat = conn.prepareStatement(sql);
				pstat.setString(1, cp_seq);
				
				rs = pstat.executeQuery();
				
				ArrayList<String> list = new ArrayList<>();
				
				while(rs.next() && list.size() != 2) {
					
					list.add(rs.getString("tag_keyword"));
					
				}
				
				return list;
				
			} catch (Exception e) {
				System.out.println("CompanyDAO.getTaglist");
				e.printStackTrace();
			}
		
		return null;
		
	}
	public CompanyDTO getCompanyBySeq(String cp_seq) {
		try {
			
			String sql = "select * from tblCompany where cp_seq = ?";
			pstat = conn.prepareStatement(sql);
			pstat.setString(1,cp_seq);
			rs = pstat.executeQuery();
			
			if(rs.next()){
				CompanyDTO dto = new CompanyDTO();
				dto.setCp_name(rs.getString("cp_name"));
				dto.setCp_address(rs.getString("cp_address"));
				dto.setImage(rs.getString("image"));
			
				return dto;
			}
		}catch (Exception e) {
	            System.out.println("CompanyDAO.getCompanyByCpSeq");
	            e.printStackTrace();
	        }
	        return null;
	    }

	/**
	 * 지유)등록된 리뷰수 불러오는 메서드
	 * 
	 * @return 리뷰수
	 */
	public int countRiview(String cp_seq) {
		try {
			String sql = "select count(*) as cnt from tblCompanyReview where cp_seq = ?";
		
			pstat = conn.prepareStatement(sql);
			

			pstat.setString(1, cp_seq);
			rs = pstat.executeQuery();

			while (rs.next()) {
				int cnt = rs.getInt("cnt");
				System.out.println(cnt);
				return cnt;
			}

		} catch (Exception e) {
			System.out.println("ReviewDAO.countRiview");
			e.printStackTrace();
		}

		return 0;
	}
	
	public ArrayList<String> getTopTagsByCpSeq(String cp_seq) {
	    try {
	        ArrayList<String> topTags = new ArrayList<>();
	        String sql = "select t.tag_keyword from tblReviewTag rt join tblTag t on rt.tag_seq = t.tag_seq join tblcompanyreview cr on rt.cp_rv_seq = cr.cp_rv_seq where cr.cp_seq =? group by t.tag_keyword order by count(*) desc ";

	        pstat = conn.prepareStatement(sql);
	        pstat.setString(1, cp_seq);
	        rs = pstat.executeQuery();

	        while (rs.next()) {
	            topTags.add(rs.getString("tag_keyword"));
	        }
	        //System.out.println("getTopTagsByCpSeq"+topTags);
	        return topTags;

	    } catch (Exception e) {
	        System.out.println("CompanyDAO.getTopTagsByCpSeq");
	        e.printStackTrace();
	        return null;
	    }
	}


	
	

}

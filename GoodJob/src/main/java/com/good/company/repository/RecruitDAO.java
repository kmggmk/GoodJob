package com.good.company.repository;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.good.matching.model.RecruitDTO;
import com.test.util.DBUtil;

public class RecruitDAO {

	private Connection conn;
	private Statement stat;
	private PreparedStatement pstat;
	private ResultSet rs;

	public RecruitDAO() {
		this.conn = DBUtil.open();
	}
	
	public int addRecruit(RecruitDTO dto) {
		//queryParamNoReturn
				try {

					String sql = "insert into tblRecruit values (seqRecruit.nextVal, ?, ?, ?, ?, ?, ?)";

					pstat = conn.prepareStatement(sql);
					pstat.setString(1, dto.getRcrt_address());
					pstat.setString(2, dto.getRcrt_career());
					pstat.setString(3, dto.getRcrt_salary());
					pstat.setString(4, dto.getStartdate());
					pstat.setString(5, dto.getEnddate());
					pstat.setString(6, dto.getCp_seq());

					return pstat.executeUpdate();

				} catch (Exception e) {
					System.out.println("addRecruit");
					e.printStackTrace();
				}
				
				return 0;
			}
	public ArrayList<RecruitDTO> RecruitList() {
		try {
			String sql = "select * from tblRecruit";

			stat = conn.createStatement();
			rs = stat.executeQuery(sql);

			ArrayList<RecruitDTO> list = new ArrayList<>();
			while (rs.next()) {
				RecruitDTO dto = new RecruitDTO();
				dto.setRcrt_seq(rs.getString("rcrt_seq"));

				list.add(dto);
			}
			return list;

		} catch (Exception e) {
			System.out.println("RecruitList");
			e.printStackTrace();
		}
		return null;
	}

}
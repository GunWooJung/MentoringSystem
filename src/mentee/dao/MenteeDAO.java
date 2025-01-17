package mentee.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mentoring.dto.MenteeDTO;
import mentoring.dto.MentorDTO;
import mentoring.dto.ReviewDTO;

public class MenteeDAO {

	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost :1521:xe";
	private String username = "c##java";
	private String password = "1234";

	private Connection con;
	private PreparedStatement pstmt;
	private ResultSet rs;

	// private static MemberDAO instance;
	private static MenteeDAO instance = new MenteeDAO();

	public MenteeDAO() {
		try {
			Class.forName(driver);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} // 생성

	}

	public static MenteeDAO getInstance() {
		/*
		 * if(instance ==null) { synchronized(MemberDAO.class) { instance=new
		 * MemberDAO(); } }
		 */
		return instance;
	}

	public void getCnnection() {

		try {
			con = DriverManager.getConnection(url, username, password);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int Login(String id, String password) {
		int mentee_seq = -1;

		getCnnection();
		String sql = "select * from  mentee where id = ? and pwd=  ?";
		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, id);
			pstmt.setString(2, password);

			rs = pstmt.executeQuery();

			if (rs.next())
				mentee_seq = rs.getInt("mentee_seq");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return mentee_seq;

	}

	public boolean IdCheck(String id) {
		boolean idchek = false;

		getCnnection();
		String sql = "select * from  mentee where id = ?";
		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, id);

			rs = pstmt.executeQuery();

			if (rs.next())
				idchek = true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return idchek;

	}

	public int Join(MenteeDTO dto) {
		int line = 0;
		getCnnection();
		String sql = "insert into   mentee values(mentee_seq.NEXTVAL,?,?,?,?,?)";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, dto.getName());
			pstmt.setString(2, dto.getId());
			pstmt.setString(3, dto.getPwd());
			pstmt.setString(4, dto.getEmail());
			pstmt.setString(5, dto.getPhone());

			line = pstmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return line;

	}

	public String MentoringCheck(int mentee_seq) {
		String name = null;
		getCnnection();

		String sql = "select MENTOR_SEQ from M"
				+ ""
				+ "ENTORING where  MENTEE_SEQ =?";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, mentee_seq);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				int mentor_seq = rs.getInt("mentor_seq");

				String sql1 = "select * from mentor where mentor_seq = ?";
				pstmt = con.prepareStatement(sql1);
				pstmt.setInt(1, mentor_seq);
				rs = pstmt.executeQuery();

				if (rs.next())
					name = rs.getString("name");

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
				if (rs != null)
					rs.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return name;
	}

	public MentorDTO Mentorinformtion(int mentee_seq) {
		//먼토 정보 뽑기 받은 값 
		//받은 닶 내 번호
		//대기중 테이블 검색->멘토 번호->멘토/대기중 멘토스퀸시 같으면 뱉기
		MentorDTO dto = null;
		getCnnection();
		// 멘토 스퀸시 뽑기
		String sql = "select * from MENTORING, mentor where MENTEE_SEQ=? and MENTORING.mentor_seq =mentor.mentor_seq";
		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, mentee_seq);

			rs = pstmt.executeQuery();

			
			if (rs.next()) {
				dto = new MentorDTO();
				dto.setName(rs.getString("name"));
				dto.setDepartment(rs.getString("department"));
				dto.setEmail(rs.getString("email"));
				dto.setPhone(rs.getString("phone"));
				
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
				if (rs != null)
					rs.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return dto;

	}

	public List<MentorDTO> DepartmenSort(int department) {
		List<MentorDTO> list = new ArrayList<>();
		String s = "";
		String sql = null;
		getCnnection();
		//분야별 멘토 데이커 테이블 출력 ->조회할때 상태가 1이면 거르기
		try {
			// 데이터베이스 연결 설정

			if (department < 5) {
				if (department == 1)
					s = "프론트엔드";
				else if (department == 2)
					s = "백엔드";
				else if (department == 3)
					s = "네트워크";
				else if (department == 4)
					s = "클라우드";
				
				// 문자열에서 공백 제거
				s = s.trim();
				sql = "select * from MENTOR where department = ?  and STATUS=0  order by mentor_seq asc";
			} //1-4 검색 문장
			else if (department == 5) {
				sql = "select * from MENTOR  where  STATUS=0  order by mentor_seq asc";
			}//5 검색 문장

			// PreparedStatement 또는 Statement 생성
			if (sql != null) {
				if (department < 5) {
					pstmt = con.prepareStatement(sql);
					pstmt.setString(1, s);
				} else {
					pstmt = con.prepareStatement(sql);
				}

				// 쿼리 실행
				rs = pstmt.executeQuery();

				// 결과 처리
				while (rs.next()) {
					MentorDTO dto = new MentorDTO(); // assuming mentor_seq is of type Integer

					dto.setMentor_seq(rs.getInt("mentor_seq"));
					dto.setName(rs.getString("name"));
					dto.setDepartment(rs.getString("department")); // assuming department column exists in the result
					list.add(dto);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public int MentoringRequest(int mentee_seq, int mentor_seq) {
		
		//멘토에게 신청 꾸욱->대기목록에 추가
		//->한번확인 거치기 신청-> 조회->없어? 신청! 있어?불가
		int check = 0;
		getCnnection();
		String sql1 = "select * from waiting where mentor_seq = ? and mentee_seq = ? ";

		try {
			pstmt = con.prepareStatement(sql1);
			pstmt.setInt(1, mentor_seq);
			pstmt.setInt(2, mentee_seq);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				check = -1;
				return check;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//내가 그사람에게 조회 신청을 하였는가?
		String sql = "insert into waiting  VALUES(?,?)";

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, mentor_seq);
			pstmt.setInt(2, mentee_seq);

			check = pstmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return check;
	}

	public int RequestCancell(int mentee_seq, int mentor_seq) {
		int cancell = 0;
		getCnnection();
		String sql = "Delete  waiting where mentor_seq=? and mentee_seq =?";

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, mentor_seq);
			pstmt.setInt(2, mentee_seq);

			cancell = pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return cancell;
	}

	public List<MentorDTO> RequestList(int mentee_seq) {
		List<MentorDTO> list = new ArrayList<>();
		getCnnection();
		String sql = "select * from waiting ,mentor where mentee_seq =? and waiting.mentor_seq=mentor.mentor_seq order by  mentor.mentor_seq asc";
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, mentee_seq);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				MentorDTO dto = new MentorDTO();
				dto.setMentor_seq(rs.getInt("mentor_seq"));
				dto.setName(rs.getString("name"));
				list.add(dto);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public int reviewWrite(int mentee_seq, int mentor_seq, String reivew) {
		int write =0;
		getCnnection();
		String sql = "insert into review values(?,?,?)";
		
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, mentor_seq);
			pstmt.setInt(2, mentee_seq);
			pstmt.setString(3, reivew);
			
			write = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return write;
	}
	

	public List<ReviewDTO> reviewList(){
		List<ReviewDTO> reviewList = new ArrayList<>();
		getCnnection();
		String sql = "select * from review "
				+ "join mentor using(mentor_seq)";
		
		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ReviewDTO dto = new ReviewDTO();
				dto.setMentor_seq(rs.getInt("mentor_seq"));
				dto.setName(rs.getString("name"));
				dto.setReview(rs.getString("review"));
				reviewList.add(dto);
				
			}
			if(reviewList.size() == 0) {
				reviewList = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs != null)
					rs.close();
				if(pstmt != null)
					pstmt.close();
				if(con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
		return reviewList;

	}
	
	public List<MentorDTO>  ReviewPossibility(int mentee_seq){
		List<MentorDTO> list = new ArrayList<>();
		getCnnection();
		String sql = "select * from  end,mentor where mentee_seq = ? and end.mentor_seq=mentor.mentor_seq";
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1,  mentee_seq);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				MentorDTO dto = new MentorDTO();
				dto.setMentor_seq(rs.getInt("mentor_seq"));
				dto.setName(rs.getString("name"));
				list.add(dto);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
		
		return list;

}
}

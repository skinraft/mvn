package mvn.servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import mvn.sql.DBUtil;

/**
 * Servlet implementation class Userinfo
 */
public class Userinfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG =LogManager.getLogger(Userinfo.class);

	/**
	 * Default constructor.
	 */
	public Userinfo() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		LOG.debug(request.getRequestURL() + "------------");
		try {
			Connection conn = DBUtil.getConnectionAli();
			String sql = "SELECT * FROM device";
			PreparedStatement p = conn.prepareStatement(sql);
			ResultSet result = p.executeQuery();
			while (result.next()) {
				JSONObject object = new JSONObject();
				object.put("status", "true");
				object.put("id", result.getInt("id"));
				object.put("macAddress", result.getString("macAddress"));
				object.put("did", result.getString("did"));
				object.put("ipAddress", result.getString("ipAddress"));
				object.put("productKey", result.getString("productKey"));
				object.put("productName", result.getString("productName"));
				object.put("remark", result.getString("remark"));
				out.println("<a>" + object.toString() + "</a>");
				LOG.debug(object.toString());
			}
			result.close();
			p.close();
			conn.close();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

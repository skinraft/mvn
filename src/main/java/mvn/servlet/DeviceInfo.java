package mvn.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import mvn.sql.DeviceInfox;

/**
 * Servlet implementation class DeviceInfo
 */
@WebServlet("/getdeviceInfo")
public class DeviceInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(DeviceInfo.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeviceInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String mac = request.getParameter("mac");
		String result = "";
		if (null == mac) {
			result = "{status:false,msg:\"mac参数不能为空\"}";
		} else {
			result = DeviceInfox.findDeviceByMac(mac).toString();
		}
		out.println(result);
		out.close();
		LOG.debug(request.getRequestURL() + "-->" + result);
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

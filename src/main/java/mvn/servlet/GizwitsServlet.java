package mvn.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gizwits.GizwitsNoti;

/**
 * Servlet implementation class GizwitsServlet
 */
@WebServlet("/GizwitsServlet")
public class GizwitsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(GizwitsNoti.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GizwitsServlet() {
		super();
		try {
			String productKey = "cd688469b42446e99a6130c9550c9ee7";
			String authId = "KC7uTcnVRgutOvCDMhaNiA";
			String authSecret = "aGTHDITNT1GVXr18ivH+CA";
			String subkey = "demo_ptj";
			String[] events = { "device.attr_fault", "device.attr_alert", "device.online", "device.offline",
					"device.status.raw", "device.status.kv", "datapoints.changed" };
			JSONObject product = new JSONObject().put("product_key", productKey).put("auth_id", authId)
					.put("auth_secret", authSecret).put("subkey", subkey).put("events", events);
			JSONArray products = new JSONArray().put(product);
			GizwitsNoti gizwitsNoti = new GizwitsNoti(products);
			gizwitsNoti.connect();
		} catch (JSONException e) {
			e.printStackTrace();
			logger.error("exception:---" + e.toString());
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
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

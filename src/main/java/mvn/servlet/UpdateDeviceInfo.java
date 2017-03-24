package mvn.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import mvn.entity.DeviceEntity;
import mvn.sql.DeviceInfox;

/**
 * Servlet implementation class UpdateDeviceInfo
 */
@WebServlet("/UpdateDeviceInfo")
public class UpdateDeviceInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(UpdateDeviceInfo.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateDeviceInfo() {
		super();
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
		Map<String, String[]> map = (Map<String, String[]>) request.getParameterMap();
		for (String name : map.keySet()) {
			if (name.contains("cd688469b42446e99a6130c9550c9ee7")) {
				try {
					LOG.debug(name);
					JSONObject object = new JSONObject(name);
					if (!object.isNull("rfid")) {
						// 传输rfid数据
					} else {
						// 正常更新同步设备数据
						DeviceEntity d = DeviceInfox.findDeviceByMac(object.getString("mac"));
						DeviceEntity device = new DeviceEntity();
						boolean execute = false;
						device.setMac_address(object.getString("mac"));
						device.setDid(object.getString("did"));
						device.setProduct_key(object.getString("product_key"));
						if (object.getString("event_type").equals("device_status_kv")) {
							JSONObject devicejson = object.getJSONObject("data");
							device.setDoor_open(devicejson.getInt("door_open"));
							device.setLight(devicejson.getInt("light"));
							device.setScaning(devicejson.getInt("scanning"));
							device.setOnline(1);
							device.setWork_model(devicejson.getInt("model"));
							device.setIp_address("192.168.1.2");
							device.setProduct_name("智能酒柜");
							device.setRemark("新潮智能酒柜");
							device.setScan_time(devicejson.getInt("scan_time"));
							device.setSet_temp(devicejson.getInt("set_temp"));
							device.setReal_temp(devicejson.getInt("real_temp"));
							if (d.status) {
								// 存在该设备
								execute = DeviceInfox.updateDeviceInfo(device);
								device.setStatus(execute);
								if (execute) {
									device.setMsg("操作成功");
									out.println("{status:true,msg:操作成功}");
								} else {
									device.setMsg("操作失败");
									out.println("{status:true,msg:操作失败}");
								}
							} else {
								// 不存在该设备
								execute = DeviceInfox.addDeviceToSql(device);
								device.setStatus(execute);
								if (execute) {
									device.setMsg("操作成功");
									out.println("{status:true,msg:操作成功}");
								} else {
									device.setMsg("操作失败");
									out.println("{status:true,msg:操作失败}");
								}
							}
						} else if (object.getString("event_type").equals("device_offline")) {
							// 设备离线
							device.setOnline(0);
							execute = DeviceInfox.updateDeviceStatusByMac(device);
							device.setStatus(execute);
							if (execute) {
								device.setMsg("操作成功");
								out.println("{status:true,msg:操作成功}");
							} else {
								device.setMsg("操作失败");
								out.println("{status:true,msg:操作失败}");
							}
						}
					}
				} catch (JSONException e) {
					LOG.debug(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		out.close();
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
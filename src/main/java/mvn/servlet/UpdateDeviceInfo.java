package mvn.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.json.JSONException;
import org.json.JSONObject;

import mvn.entity.DeviceEntity;
import mvn.entity.DeviceLogx;
import mvn.entity.RfidLogx;
import util.Stringx;

/**
 * Servlet implementation class UpdateDeviceInfo
 */
@WebServlet("/UpdateDeviceInfo")
public class UpdateDeviceInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(UpdateDeviceInfo.class);
	HashMap<String, String> rfid = new HashMap<String, String>();
	Session session = null;
	SessionFactory factory = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateDeviceInfo() {
		super();
		Configuration conn = new Configuration();
		factory = conn.configure().buildSessionFactory();
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
					session = factory.openSession();
					session.beginTransaction();
					if (!object.isNull("rfid")) {
						// 传输rfid数据
						String content = object.getJSONObject("rfid").getString("raw");
						if (content.equals("01010101010101010101")) {
							// 开始记录,先移除记录表map中上次的数据
							synchronized (rfid) {
								if (rfid.containsKey(object.getString("mac"))) {
									rfid.remove(object.getString("mac"));
								}
							}
						} else if (content.equals("02020202020202020202")) {
							// 结束记录,更新到数据库,移除记录表中上次的数据
							synchronized (rfid) {
								if (session.isOpen()) {
									RfidLogx rfidLogx = new RfidLogx();
									rfidLogx.setMac_address(object.getString("mac"));
									rfidLogx.setDate(Stringx.unixToJava(object.getInt("created_at") + "", ""));
									rfidLogx.setNote(rfid.get(object.getString("mac")));
									rfid.remove(object.getString("mac"));
									session.save(rfidLogx);
									session.getTransaction().commit();
								}
							}
						} else {
							// 叠加记录标签
							synchronized (rfid) {
								String c = rfid.get(object.getString("mac"));
								c += content;
								rfid.put(object.getString("mac"), c);
							}
						}
					} else {
						// 正常更新同步设备数据
						DeviceEntity device = new DeviceEntity();
						device.setMac_address(object.getString("mac"));
						device.setDid(object.getString("did"));
						device.setProduct_key(object.getString("product_key"));
						device.setUpdate_time(Stringx.unixToJava(object.getInt("created_at") + "", ""));
						// 设备普通日志
						DeviceLogx deviceLogx = new DeviceLogx();
						deviceLogx.setDate(device.getUpdate_time());
						deviceLogx.setMac_address(device.getMac_address());
						deviceLogx.setNote(object.toString());
						session.save(deviceLogx);
						if (object.getString("event_type").equals("device_status_kv")) {
							JSONObject devicejson = object.getJSONObject("data");
							device.setDoor_open(devicejson.getInt("door_open"));
							device.setLight(devicejson.getInt("light"));
							device.setScaning(devicejson.getInt("scanning"));
							device.setOnline(1);
							device.setWork_model(devicejson.getInt("model"));
							device.setProduct_name("智能酒柜");
							device.setScan_time(devicejson.getInt("scan_time"));
							device.setSet_temp(devicejson.getInt("set_temp"));
							device.setReal_temp(devicejson.getInt("real_temp"));
						} else if (object.getString("event_type").equals("device_offline")
								|| object.getString("event_type").equals("device_online")) {
							// 设备上线/离线
							if (object.getString("event_type").equals("device_offline")) {
								device.setOnline(0);
							} else {
								device.setOnline(1);
								device.setIp_address(object.getString("ip"));
							}
						}
						// 查询数据库中是否有该设备
						Query<?> q = session.createQuery(
								"from mvn.entity.DeviceEntity where mac_address='" + device.getMac_address() + "'");
						java.util.List<?> list = q.getResultList();
						if (list.isEmpty()) {
							// 保存新设备
							session.save(device);
						} else {
							// 更新老设备
							DeviceEntity d = (DeviceEntity) list.get(0);
							d.update(device);
							session.update(d);
						}
						session.getTransaction().commit();
						session.close();
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

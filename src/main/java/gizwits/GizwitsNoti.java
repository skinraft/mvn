package gizwits;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mvn.entity.DeviceEntity;
import mvn.entity.DeviceLogx;
import mvn.entity.RfidLogx;
import util.Stringx;

public class GizwitsNoti implements GizwitsCallBack {
	private static final Logger logger = LogManager.getLogger(GizwitsNoti.class);
	// 机智云noti2 ssl服务地址
	private static final String GIZWITS_NOTI_HOST = "snoti.gizwits.com";
	// 机智云noti2 ssl服务端口
	private static final int GIZWITS_NOTI_PORT = 2017;
	private JSONArray products; // 登录noti2的product信息
	private ReceiveThread receiveThread; // 接收socket报文的线程
	private SendThread sendThread; // 向socket发送login，ping的线程
	private Socket socket; // sslsocket对象
	private PrintWriter pw; // socket的OutputStream字符流对象
	private boolean isConnect; // socket连接状态
	private boolean isLogin; // 登录状态
	private int reconnCount; // 重连次数
	private final int MAXCONNECT = 720; // 最大重连数
	private final int TIMEOUT = 10000; // 等待接收socket消息超时时间

	public GizwitsNoti(JSONArray products) {
		this.products = products;
		Configuration conn = new Configuration();
		factory = conn.configure().buildSessionFactory();
	}

	private Socket createSslSocket() throws IOException, NoSuchAlgorithmException, KeyManagementException {
		SSLContext context = SSLContext.getInstance("SSL");
		context.init(null, // 初始化，不发送客户端证书，也不验证服务端证书
				new TrustManager[] { new MyX509TrustManager() }, new SecureRandom());
		SSLSocketFactory fcty = context.getSocketFactory();
		SSLSocket socket = (SSLSocket) fcty.createSocket(GIZWITS_NOTI_HOST, GIZWITS_NOTI_PORT);
		socket.setKeepAlive(true); // 开启socket的保活
		socket.setSoTimeout(TIMEOUT); // 设置socket的接收消息超时时间
		isConnect = true;
		return socket;
	}

	private class MyX509TrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	private class SendThread extends Thread {
		private boolean hasSendLogin = false; // 是否发送登录指令

		@Override
		public void run() {
			while (isConnect) {
				try {
					if (!isLogin) {
						if (!hasSendLogin) {
							sendLoginMsg();
							hasSendLogin = true;
						} else {
							Thread.sleep(1000); // 等待登录结果
						}
					} else {
						for (int i = 0; i < 60; i++) { // 1秒钟检查一次连接状态，1分钟发送一次ping指令
							Thread.sleep(1000);
							if (!isConnect) {
								logger.debug("noti接口SendThread退出...." + Thread.currentThread().getName());
								return;
							}
						}
						sendPingMsg();
					}
				} catch (SocketException e) {
					e.printStackTrace();
					logger.error("exception: ---" + e.toString());
					reconnect(); // 连接被断开重连
					return;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("exception: ---" + e.toString());
				}
			}
			logger.debug("noti接口SendThread退出...." + Thread.currentThread().getName());
		}

		public void sendLoginMsg() throws IOException {
			String msg = "";
			try {
				msg = new JSONObject().put("cmd", "login_req").put("prefetch_count", 50).put("data", products)
						.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String sendMsg = msg + "\n";
			sendMsg(sendMsg);
			logger.debug("登录发送:" + sendMsg);
		}

		public void sendPingMsg() throws IOException {
			String sendMsg = "{\"cmd\": \"ping\"}\n";
			sendMsg(sendMsg);
			logger.debug("发送心跳:" + sendMsg);
		}

		public void sendMsg(String sendMsg) throws IOException {
			pw.write(sendMsg); // 往socket写入消息
			pw.flush(); // 让socket发送已写入的消息
			socket.setSoTimeout(TIMEOUT); // 设置socket的接收消息超时时间
		}
	}

	private class ReceiveThread extends Thread {
		@Override
		public void run() {

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				while (isConnect) {
					String line = reader.readLine(); // 读取socket消息
					if (line != null) {
						logger.debug("收到机智云通知: " + line);
						try {
							JSONObject json = new JSONObject(line);
							String cmd = json.getString("cmd");
							switch (cmd) {
							case "login_res": // 登录请求的返回
								checkLogin(json);
								break;
							case "pong": // ping指令的返回
								setTimeout();
								System.out.println("pong success.");
								break;
							case "event_push": // 设备消息
								replyAck(json);
								push(json);
								break;
							case "remote_control_res": // 控制指令的返回
								setTimeout();
								control(json);
								System.out.println("opearte success.");
								// 信息传递到服务器
								break;
							case "invalid_msg":
								logger.error(json);
								int errorCode = json.getInt("error_code");
								if (4000 == errorCode) { // noti2服务端内部错误
									reconnect();
								} else { // noti2客户端错误
									disconnect();
								}
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("exception:---" + e.toString());
						}
					}
				}
				reader.close();
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				logger.error("exception:---" + e.toString());
				reconnect(); // 接收消息超时重连
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("exception:---" + e.toString());
				disconnect();
			}
			logger.debug("noti接口ReceiveThread退出...." + Thread.currentThread().getName());
		}

		private void checkLogin(JSONObject json) {
			try {
				JSONObject data = json.getJSONObject("data");
				boolean result = data.getBoolean("result");
				if (result) {
					isLogin = true; // 登录成功
					socket.setSoTimeout(0); // 设置接收消息超时时间为永久
					reconnCount = 0; // 重置重连次数
					logger.info("login success.");
					System.out.println("login success.");
				} else {
					logger.info("login fail, msg: " + data.getString("msg"));
					disconnect(); // 登录失败断开连接
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("exception:---" + e.toString());
				disconnect(); // 异常断开连接
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("exception:---" + e.toString());
			}
		}

		private void setTimeout() throws SocketException {
			socket.setSoTimeout(0); // 设置接收消息超时时间为永久
		}

		private void replyAck(JSONObject json) // 回复noti服务端ack
		{
			try {
				String sendMsg = "{\"cmd\": \"event_ack\",\"delivery_id\": " + json.getLong("delivery_id") + "}\n";
				logger.debug("发送ack:" + sendMsg);
				pw.write(sendMsg);
				pw.flush();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("exception:---" + e.toString());
			}
		}
	}

	private class ReconnectThread extends Thread {
		@Override
		public void run() {
			try {
				Thread.sleep(5000);
				logger.debug("开始执行重连...");
				disconnect();
				if (sendThread != null) {
					sendThread.join(3000); // 等待发送线程结束
				}
				if (sendThread != null) {
					receiveThread.join(3000); // 等待接收线程结束
				}
				if (reconnCount < MAXCONNECT) {
					reconnCount++;
					connect(); // 重新开始连接
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("exception:---" + e.toString());
			}
		}
	}

	public void connect() // 开启连接
	{
		try {
			logger.debug("开始连接noti...");
			isConnect = false;
			isLogin = false;
			socket = createSslSocket();
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			sendThread = new SendThread();
			sendThread.start();
			receiveThread = new ReceiveThread();
			receiveThread.start();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("exception:---" + e.toString());
			reconnect();
		}
	}

	public void disconnect() // 断开socket连接
	{
		try {
			logger.debug("终止连接noti....");
			if (isConnect) {
				isConnect = false;
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("exception:---" + e.toString());
		}
	}

	private void reconnect() // 重连socket
	{
		logger.debug("准备执行重连...");
		new ReconnectThread().start();
	}

	public void remoteControl() // 发送远程控制指令
	{
		System.out.println("准备远程控制...");
		try {
			if (isLogin) {
				String productKey = "cd688469b42446e99a6130c9550c9ee7";
				String did = "99CShdMHXW6ZS39CV4qWYU";
				String mac = "--test4noti2";
				String cmd = "write";
				String type = null;
				Object value = null;
				if (cmd.equals("write")) {
					type = "raw";
					String valueStr = "raw";
					value = new JSONArray(valueStr);
				} else if (cmd.equals("write_attrs")) {
					type = "attrs";
					String valueStr = "attrs";
					value = new JSONObject(valueStr);
				}
				JSONObject dataData = new JSONObject().put("did", did).put("mac", mac).put("product_key", productKey)
						.put(type, value);
				JSONObject data = new JSONObject().put("cmd", cmd).put("source", "noti").put("data", dataData);
				JSONArray datas = new JSONArray().put(data);
				JSONObject rc = new JSONObject().put("cmd", "remote_control_req").put("data", datas);
				System.out.println("发送rc:" + rc.toString());
				pw.write(rc.toString() + "\n");
				pw.flush();

			} else {
				System.out.println("连接不成功，无法发送控制");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("exception:---" + e.toString());
		}
	}

	/**
	 * @param bytes
	 * @return
	 */
	public static byte[] decode(final byte[] bytes) {
		return Base64.decodeBase64(bytes);
	}

	/**
	 * 二进制数据编码为BASE64字符串
	 *
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	public static String encode(final byte[] bytes) {
		return new String(Base64.encodeBase64(bytes));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	HashMap<String, String> rfid = new HashMap<String, String>();
	Session session = null;
	SessionFactory factory = null;

	@Override
	public void push(JSONObject object) {
		try {
			String eventType = object.getString("event_type");
			DeviceEntity device = new DeviceEntity();
			DeviceLogx deviceLogx = new DeviceLogx();
			RfidLogx rfidLogx = new RfidLogx();
			session = factory.openSession();
			session.beginTransaction();
			// 设备信息更新
			device.setMac_address(object.getString("mac"));
			device.setDid(object.getString("did"));
			device.setProduct_key(object.getString("product_key"));
			device.setUpdate_time(Stringx.unixToJava(object.getInt("created_at") + "", ""));
			// 设备普通日志
			deviceLogx.setDate(device.getUpdate_time());
			deviceLogx.setMac_address(device.getMac_address());
			deviceLogx.setNote(object.toString());
			session.save(deviceLogx);
			//
			JSONObject devicejson = object.getJSONObject("data");
			if (eventType.equals("device_status_kv")) {
				// 设备数据点部分
				device.setDoor_open(devicejson.getInt("door_open"));
				device.setLight(devicejson.getInt("light"));
				device.setScaning(devicejson.getInt("scanning"));
				device.setOnline(1);
				device.setWork_model(devicejson.getInt("model"));
				device.setProduct_name("智能酒柜");
				device.setScan_time(devicejson.getInt("scan_time"));
				device.setSet_temp(devicejson.getInt("set_temp"));
				device.setReal_temp(devicejson.getInt("real_temp"));
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
			} else if (eventType.endsWith("device_status_raw")) {
				if (!devicejson.isNull("raw")) {
					// 透传数据
					String content = XRfidDataUtil.bytesToHexString(decode(devicejson.getString("raw").getBytes()));
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
							rfidLogx.setMac_address(object.getString("mac"));
							rfidLogx.setDate(Stringx.unixToJava(object.getInt("created_at") + "", ""));
							rfidLogx.setNote(rfid.get(object.getString("mac")));
							rfid.remove(object.getString("mac"));
							session.save(rfidLogx);
						}
					} else {
						// 叠加记录标签
						synchronized (rfid) {
							String c = rfid.get(object.getString("mac"));
							c += content;
							rfid.put(object.getString("mac"), c);
						}
					}
				}
			}
			// 提交数据库事物
			session.getTransaction().commit();
			session.close();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void control(JSONObject control) {

	}
}

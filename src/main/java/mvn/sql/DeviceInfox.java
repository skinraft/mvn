package mvn.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mvn.entity.DeviceEntity;

public class DeviceInfox {
	/***
	 * 根据mac值查询某一个设备
	 * 
	 * @param macAddress
	 * @return
	 */
	public static DeviceEntity findDeviceByMac(String macAddress) {
		DeviceEntity device = new DeviceEntity();
		try {
			String SQL = "SELECT * FROM device_info WHERE mac_address='" + macAddress + "'";
			Connection conn = DBUtil.getConnectionAli();
			PreparedStatement p = conn.prepareStatement(SQL);
			ResultSet result = p.executeQuery();
			if (result.next()) {
				device.setDid(result.getString("did"));
				device.setEnable(result.getInt("is_enable"));
				device.setIp_address(result.getString("ip_address"));
				device.setLight(result.getInt("light"));
				device.setMac_address(result.getString("mac_address"));
				device.setOnline(result.getInt("is_online"));
				device.setProduct_key(result.getString("product_key"));
				device.setProduct_name(result.getString("product_name"));
				device.setReal_temp(result.getInt("real_temp"));
				device.setRemark(result.getString("remark"));
				device.setScan_device_type(result.getString("scan_device_type"));
				device.setScan_time(result.getInt("scan_time"));
				device.setScaning(result.getInt("scaning"));
				device.setSet_temp(result.getInt("set_temp"));
				device.setUpdate_time(result.getString("update_time"));
				device.setWork_model(result.getInt("work_model"));
				device.setDoor_open(result.getInt("door_open"));
				device.setStatus(true);
				device.setMsg("操作成功");
			} else {
				device.setStatus(false);
				device.setMsg("没有找到该设备");
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
		}
		return device;
	}

	/****
	 * 保存新设备
	 * 
	 * @param device
	 * @return
	 */
	public static boolean addDeviceToSql(DeviceEntity device) {
		boolean execute = false;
		try {
			String SQL = "INSERT INTO device_info (mac_address,did,ip_address,product_key,product_name,remark,"
					+ "is_online,set_temp,real_temp,light,scaning,scan_time,is_enable,door_open)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			Connection conn = DBUtil.getConnectionAli();
			PreparedStatement p = conn.prepareStatement(SQL);
			p.setString(1, device.getMac_address());
			p.setString(2, device.getDid());
			p.setString(3, device.getIp_address());
			p.setString(4, device.getProduct_key());
			p.setString(5, device.getProduct_name());
			p.setString(6, device.getRemark());
			p.setInt(7, device.getOnline());
			p.setInt(8, device.getSet_temp());
			p.setInt(9, device.getReal_temp());
			p.setInt(10, device.getLight());
			p.setInt(11, device.getScaning());
			p.setInt(12, device.getScan_time());
			p.setInt(13, device.getEnable());
			p.setInt(14, device.getDoor_open());
			execute = p.execute();
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
		}
		return execute;
	}

	/***
	 * 更新设备信息
	 * 
	 * @param device
	 * @return
	 */
	public static boolean updateDeviceInfo(DeviceEntity device) {
		boolean execute = false;
		try {
			Connection conn = DBUtil.getConnectionAli();
			String SQL = "UPDATE device_info SET did='" + device.getDid() + "',is_online=" + device.getOnline()
					+ ",light=" + device.getLight() + ",set_temp=" + device.getSet_temp() + ",real_temp="
					+ device.getReal_temp() + ",door_open=" + device.getDoor_open() + ",scaning=" + device.getScaning()
					+ ",scan_time=" + device.getScan_time() + ",work_model=" + device.getWork_model() + ",update_time='"
					+ device.getUpdate_time() + "' where mac_address='" + device.getMac_address() + "'";
			PreparedStatement p = conn.prepareStatement(SQL);
			execute = p.execute();
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
		}
		return execute;
	}

	/***
	 * 更新设备的在线状态
	 * 
	 * @param device
	 * @return
	 */
	public static boolean updateDeviceStatusByMac(DeviceEntity device) {
		boolean execute = false;
		try {
			Connection conn = DBUtil.getConnectionAli();
			String SQL = "";
			if (null != device.getIp_address()) {
				SQL = "UPDATE device_info SET is_online=" + device.getOnline() + ",update_time='"
						+ device.getUpdate_time() + "',ip_address='" + device.getIp_address() + "' where mac_address='"
						+ device.getMac_address() + "'";
			} else {
				SQL = "UPDATE device_info SET is_online=" + device.getOnline() + ",update_time='"
						+ device.getUpdate_time() + "' where mac_address='" + device.getMac_address() + "'";
			}
			PreparedStatement p = conn.prepareStatement(SQL);
			execute = p.execute();
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
		}
		return execute;
	}
}

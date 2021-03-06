package mvn.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import util.Stringx;

public class DeviceLogx {
	/***
	 * 增加记录设备的运行记录
	 * 
	 * @param macAddress
	 * @param date
	 * @param note
	 */
	public static void addLogToDB(String macAddress, String note) {
		try {
			Connection conn = DBUtil.getConnectionAli();
			String SQL = "INSERT INTO device_status_log (mac_address,date,note)VALUES(?,?,?)";
			PreparedStatement p = conn.prepareStatement(SQL);
			p.setString(1, macAddress);
			p.setString(2, Stringx.getCurrentTime());
			p.setString(3, note);
			p.execute();
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
	}

	/***
	 * 添加rfid记录数据
	 * 
	 * @param macAddress
	 * @param note
	 */
	public static void addRfidToDB(String macAddress, String note) {
		try {
			Connection conn = DBUtil.getConnectionAli();
			String SQL = "INSERT INTO device_rfid_log (mac_address,date,note)VALUES(?,?,?)";
			PreparedStatement p = conn.prepareStatement(SQL);
			p.setString(1, macAddress);
			p.setString(2, Stringx.getCurrentTime());
			p.setString(3, note);
			p.execute();
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
	}
}

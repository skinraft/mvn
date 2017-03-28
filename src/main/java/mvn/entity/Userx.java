package mvn.entity;

import java.io.Serializable;
import java.util.Date;

public class Userx implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -474506698445422784L;
	int id;
	String user_id;
	String user_token;
	String user_phone;
	String mac_address;
	Date reg_date;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setReg_date(Date reg_date) {
		this.reg_date = reg_date;
	}

	public Date getReg_date() {
		return reg_date;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_token() {
		return user_token;
	}

	public void setUser_token(String user_token) {
		this.user_token = user_token;
	}

	public String getUser_phone() {
		return user_phone;
	}

	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}

	public String getMac_address() {
		return mac_address;
	}

	public void setMac_address(String mac_address) {
		this.mac_address = mac_address;
	}

}

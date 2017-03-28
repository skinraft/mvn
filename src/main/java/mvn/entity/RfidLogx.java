package mvn.entity;

import java.io.Serializable;

public class RfidLogx implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2823398120564641150L;
     
	int id;
	String mac_address;
	String date;
	String note;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMac_address() {
		return mac_address;
	}
	public void setMac_address(String mac_address) {
		this.mac_address = mac_address;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	
}

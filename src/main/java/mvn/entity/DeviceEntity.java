package mvn.entity;

public class DeviceEntity extends BaseEntity {
	/**
	 * 
	 */
	public static final long serialVersionUID = -6477475764709881600L;
	int id;
	String mac_address;
	String did;
	String ip_address;
	String product_key;
	String product_name;
	String remark;
	int online;
	int set_temp;
	int real_temp;
	int light;
	int scaning;
	int scan_time;
	String scan_device_type;
	String update_time;
	int work_model;
	int enable;
	int door_open;

	public void update(DeviceEntity device) {
		this.did = device.getDid();
		this.ip_address = device.getIp_address();
		this.remark = device.getRemark();
		this.online = device.getOnline();
		this.set_temp = device.getSet_temp();
		this.real_temp = device.real_temp;
		this.light = device.light;
		this.scaning = device.getScaning();
		this.scan_time = device.getScan_time();
		this.scan_device_type = device.getScan_device_type();
		this.update_time = device.getUpdate_time();
		this.work_model = device.getWork_model();
		this.enable = device.getEnable();
		this.door_open = device.getDoor_open();
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setDoor_open(int door_open) {
		this.door_open = door_open;
	}

	public int getDoor_open() {
		return door_open;
	}

	public String getMac_address() {
		return mac_address;
	}

	public void setMac_address(String mac_address) {
		this.mac_address = mac_address;
	}

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public String getIp_address() {
		return ip_address;
	}

	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}

	public String getProduct_key() {
		return product_key;
	}

	public void setProduct_key(String product_key) {
		this.product_key = product_key;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getOnline() {
		return online;
	}

	public void setOnline(int online) {
		this.online = online;
	}

	public int getSet_temp() {
		return set_temp;
	}

	public void setSet_temp(int set_temp) {
		this.set_temp = set_temp;
	}

	public int getReal_temp() {
		return real_temp;
	}

	public void setReal_temp(int real_temp) {
		this.real_temp = real_temp;
	}

	public int getLight() {
		return light;
	}

	public void setLight(int light) {
		this.light = light;
	}

	public int getScaning() {
		return scaning;
	}

	public void setScaning(int scaning) {
		this.scaning = scaning;
	}

	public int getScan_time() {
		return scan_time;
	}

	public void setScan_time(int scan_time) {
		this.scan_time = scan_time;
	}

	public String getScan_device_type() {
		return scan_device_type;
	}

	public void setScan_device_type(String scan_device_type) {
		this.scan_device_type = scan_device_type;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public int getWork_model() {
		return work_model;
	}

	public void setWork_model(int work_model) {
		this.work_model = work_model;
	}

	public int getEnable() {
		return enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}

	@Override
	public String toString() {
		if (null == mac_address) {
			return "{status:" + status + ",msg:" + msg + "}";
		}
		return "{status:" + status + ",msg:" + msg + ",data:{mac_address:" + mac_address + ", did:" + did
				+ ", ip_address:" + ip_address + ", product_key:" + product_key + ", product_name:" + product_name
				+ ", remark:" + remark + ", online:" + online + ", set_temp:" + set_temp + ", real_temp:" + real_temp
				+ ", light:" + light + ", scaning:" + scaning + ", scan_time:" + scan_time + ", scan_device_type:"
				+ scan_device_type + ", update_time:" + update_time + ", work_model:" + work_model + ", enable:"
				+ enable + ",door_open:" + door_open + "}}";
	}

}

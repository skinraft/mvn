package gizwits;

import org.json.JSONObject;

/***
 * 收到机智云的信息
 * 
 * @author techssd
 *
 */
public interface GizwitsCallBack {
	// 设备信息推送
	void push(JSONObject push);

	// 设备远程控制
	void control(JSONObject control);
}

package restaurant.vo;

/**
 * @author house
 *
 */
public class AdminVo {

	String admin_id;
	String beacon_id;
	String admin_phone;
	String admin_name;
	String password;
	String admin_picture;
	
	
	
	public String getAdmin_picture() {
		return admin_picture;
	}
	public void setAdmin_picture(String admin_picture) {
		this.admin_picture = admin_picture;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAdmin_id() {
		return admin_id;
	}
	public void setAdmin_id(String admin_id) {
		this.admin_id = admin_id;
	}
	public String getBeacon_id() {
		return beacon_id;
	}
	public void setBeacon_id(String beacon_id) {
		this.beacon_id = beacon_id;
	}
	public String getAdmin_phone() {
		return admin_phone;
	}
	public void setAdmin_phone(String admin_phone) {
		this.admin_phone = admin_phone;
	}
	public String getAdmin_name() {
		return admin_name;
	}
	public void setAdmin_name(String admin_name) {
		this.admin_name = admin_name;
	}
	

}

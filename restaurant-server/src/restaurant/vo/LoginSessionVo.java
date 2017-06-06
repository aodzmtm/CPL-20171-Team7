package restaurant.vo;


/**
 * Created by house on 2017-04-05.
 */

/**
 * @author house
 *
 */
public class LoginSessionVo {

    int loginType;
    String beacon_id;
    String member_id;
    String admin_id;
    String admin_picture;
    String member_picture;
    String admin_name;
    String member_name;
    
    
    
    
	public String getAdmin_picture() {
		return admin_picture;
	}
	public void setAdmin_picture(String admin_picture) {
		this.admin_picture = admin_picture;
	}
	public String getMember_picture() {
		return member_picture;
	}
	public void setMember_picture(String member_picture) {
		this.member_picture = member_picture;
	}
	public String getAdmin_name() {
		return admin_name;
	}
	public void setAdmin_name(String admin_name) {
		this.admin_name = admin_name;
	}
	public String getMember_name() {
		return member_name;
	}
	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}
	public String getBeacon_id() {
		return beacon_id;
	}
	public void setBeacon_id(String beacon_id) {
		this.beacon_id = beacon_id;
	}
	public int getLoginType() {
		return loginType;
	}
	public void setLoginType(int loginType) {
		this.loginType = loginType;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public String getAdmin_id() {
		return admin_id;
	}
	public void setAdmin_id(String admin_id) {
		this.admin_id = admin_id;
	}


}

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
    String member_id;
    String admin_id;
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

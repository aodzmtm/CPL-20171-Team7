package restaurant.vo;
/**
 * Created by house on 2017-03-30.
 */

public class ReviewVo {
	   String member_name;
	    Double review_eval;
	    int review_id;
	    String review_picture;
	    String review_info;
	    String write_date;
	    String member_id;
	    String admin_id;
	    String admin_picture;
	    String admin_name;
	    String member_picture;
	    int menu_id;
	    int store_id;

	    
	    public String getAdmin_picture() {
			return admin_picture;
		}

		public void setAdmin_picture(String admin_picture) {
			this.admin_picture = admin_picture;
		}

		public String getAdmin_id() {
			return admin_id;
		}

		public void setAdmin_id(String admin_id) {
			this.admin_id = admin_id;
		}

		public String getAdmin_name() {
			return admin_name;
		}

		public void setAdmin_name(String admin_name) {
			this.admin_name = admin_name;
		}

		public Double getReview_eval() {
			return review_eval;
		}

		public void setReview_eval(Double review_eval) {
			this.review_eval = review_eval;
		}

		public String getMember_name() {
	        return member_name;
	    }

	    public void setMember_name(String member_name) {
	        this.member_name = member_name;
	    }

	   

	    public int getStore_id() {
	        return store_id;
	    }

	    public void setStore_id(int store_id) {
	        this.store_id = store_id;
	    }

	    public int getMenu_id() {
	        return menu_id;
	    }

	    public void setMenu_id(int menu_id) {
	        this.menu_id = menu_id;
	    }


	    public int getReview_id() {
	        return review_id;
	    }

	    public void setReview_id(int review_id) {
	        this.review_id = review_id;
	    }

	    public String getReview_picture() {
	        return review_picture;
	    }

	    public void setReview_picture(String review_picture) {
	        this.review_picture = review_picture;
	    }

	    public String getReview_info() {
	        return review_info;
	    }

	    public void setReview_info(String review_info) {
	        this.review_info = review_info;
	    }

	    public String getWrite_date() {
	        return write_date;
	    }

	    public void setWrite_date(String write_date) {
	        this.write_date = write_date;
	    }

	    public String getMember_id() {
	        return member_id;
	    }

	    public void setMember_id(String member_id) {
	        this.member_id = member_id;
	    }


	    public String getMember_picture() {
	        return member_picture;
	    }

	    public void setMember_picture(String member_picture) {
	        this.member_picture = member_picture;
	    }


	}

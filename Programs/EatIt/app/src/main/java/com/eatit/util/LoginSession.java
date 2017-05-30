package com.eatit.util;

public class LoginSession {
    public static  LoginSession instance;
    private static int loginType;
    private static String member_id;
    private static String admin_id;
    private static String beacon_id;
    private static String member_name;
    private static String admin_name;
    private static String member_picture;
    private static String admin_picture;

    public  LoginSession(){
        instance=this;
    }

    public LoginSession getInstance () {
        return instance;
    }

    public String getBeacon_id() {
        return beacon_id;
    }

    public void setBeacon_id(String beacon_id) {
        LoginSession.beacon_id = beacon_id;
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

    public static String getMember_name() {
        return member_name;
    }

    public static void setMember_name(String member_name) {
        LoginSession.member_name = member_name;
    }

    public static String getAdmin_name() {
        return admin_name;
    }

    public static void setAdmin_name(String admin_name) {
        LoginSession.admin_name = admin_name;
    }

    public static String getMember_picture() {
        return member_picture;
    }

    public static void setMember_picture(String member_picture) {
        LoginSession.member_picture = member_picture;
    }

    public static String getAdmin_picture() {
        return admin_picture;
    }

    public static void setAdmin_picture(String admin_picture) {
        LoginSession.admin_picture = admin_picture;
    }
}

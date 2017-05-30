package com.eatit.vo;


public class Restaurant {
    private int storeID;
    private int category;
    private String res_name;
    private String addr;
    private String grade;
    private String phone;
    private String beaconID;
    private String picture;

    public Restaurant(){

    }

    public Restaurant(int storeID, int category, String res_name, String addr, String grade, String phone, String beaconID, String picture){
        this.storeID = storeID;
        this.category = category;
        this.res_name = res_name;
        this.addr = addr;
        this.grade = grade;
        this.phone = phone;
        this.beaconID = beaconID;
        this.picture = picture;
    }

    public int getStoreID(){
        return storeID;
    }

    public void setStoreID(int storeID){
        this.storeID = storeID;
    }

    public String getRes_name(){
        return res_name;
    }

    public void setRes_name(String res_name){
        this.res_name = res_name;
    }

    public String getAddr(){
        return addr;
    }

    public void setAddr(String addr){
        this.addr = addr;
    }

    public String getGrade(){
        return grade;
    }

    public void setGrade(String grade){
        this.grade = grade;
    }

    public int getCategory(){
        return category;
    }

    public void setCategory(int category){
        this.category = category;
    }

    public String getPhone(){
        return phone;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getBeaconID() { return beaconID;}

    public void setBeaconID(String beaconID){
        this.beaconID = beaconID;
    }

    public String getPicture(){return picture;}

    public void setPicture(String picture){
        this.picture = picture;
    }
}

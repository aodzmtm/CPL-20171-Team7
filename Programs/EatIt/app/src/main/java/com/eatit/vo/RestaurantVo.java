package com.eatit.vo;

public class RestaurantVo {
    int store_id;
    String store_adress;
    String store_picture;
    String store_eval;
    String store_name;
    String store_phone;
    String store_info;
    String beacon_id;
    public String getStore_info() {
        return store_info;
    }

    public String getBeacon_id() {
        return beacon_id;
    }

    public void setBeacon_id(String beacon_id) {
        this.beacon_id = beacon_id;
    }

    public void setStore_info(String store_info) {
        this.store_info = store_info;
    }

    public int getStore_id() {
        return store_id;
    }
    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }
    public String getStore_adress() {
        return store_adress;
    }
    public void setStore_adress(String store_adress) {
        this.store_adress = store_adress;
    }
    public String getStore_picture() {
        return store_picture;
    }
    public void setStore_picture(String store_picture) {
        this.store_picture = store_picture;
    }
    public String getStore_eval() {
        return store_eval;
    }
    public void setStore_eval(String store_eval) {
        this.store_eval = store_eval;
    }
    public String getStore_name() {
        return store_name;
    }
    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }
    public String getStore_phone() {
        return store_phone;
    }
    public void setStore_phone(String store_phone) {
        this.store_phone = store_phone;
    }
}


package com.eatit.vo;

public class MenuVo {
    int menu_id;
    String menu_name;
    String menu_price;
    int store_id;
    String menu_info;
    double menu_eval;
    String menu_picture;
    public String getMenu_price() {
        return menu_price;
    }

    public void setMenu_price(String menu_price) {
        this.menu_price = menu_price;
    }
    public int getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(int menu_id) {
        this.menu_id = menu_id;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public String getMenu_info() {
        return menu_info;
    }

    public void setMenu_info(String menu_info) {
        this.menu_info = menu_info;
    }

    public double getMenu_eval() {
        return menu_eval;
    }

    public void setMenu_eval(double menu_eval) {
        this.menu_eval = menu_eval;
    }

    public String getMenu_picture() {
        return menu_picture;
    }

    public void setMenu_picture(String menu_picture) {
        this.menu_picture = menu_picture;
    }

}
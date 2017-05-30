package com.eatit.vo;

/*회원 Event
        경품 Event
        할인 Event
        커플 Event
        회원 Event
        제휴 Event
        점심 Event
        저녘 Event
        one day\nEvent*/
public class EventVo {

    int event_id;
    int store_id;
    String event_name;
    String event_price;
    String start_date;
    String end_date;
    String event_picture;
    String event_info;
    String event_type;


    public String getEvent_picture() {
        return event_picture;
    }

    public void setEvent_picture(String event_picture) {
        this.event_picture = event_picture;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_price() {
        return event_price;
    }

    public void setEvent_price(String event_price) {
        this.event_price = event_price;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getEvent_info() {
        return event_info;
    }

    public void setEvent_info(String event_info) {
        this.event_info = event_info;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }
}

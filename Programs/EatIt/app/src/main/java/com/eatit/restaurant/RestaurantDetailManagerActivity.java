package com.eatit.restaurant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.eatit.util.LoadCircleImagefromUrl;
import com.eatit.util.LoadImagefromUrl;
import com.eatit.vo.EventVo;
import com.eatit.vo.MenuVo;
import com.eatit.vo.RestaurantVo;
import com.eatit.vo.ReviewVo;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static com.eatit.util.CommonManager.SERVER_ADDR;

public class RestaurantDetailManagerActivity extends AppCompatActivity {

    String server_address= SERVER_ADDR;

    ArrayList<RestaurantVo> restaurants;//가게 리스트
    ArrayList<MenuVo> menu;//메뉴 리스트
    ArrayList<ReviewVo> reviews;
    ArrayList<EventVo> events ;
    ScrollView scrollView;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_manager);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0x80331401));

        final Intent i = getIntent();
        getStore( Integer.parseInt(i.getStringExtra("store_id")));
        this.setTitle(restaurants.get(0).getStore_name());
        scrollView.post(new Runnable() {    //스레드로 조정
            @Override
            public void run() {
                // TODO Auto-generated method stub
                scrollView.scrollTo(0, 0);
            }
        });

        TabHost tabHost = (TabHost)findViewById(R.id.tab_host);
        tabHost.setup();
        TabHost.TabSpec spec1 = tabHost.newTabSpec("Tab1").setContent(R.id.tab1).setIndicator("메뉴");
        tabHost.addTab(spec1);
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Tab2").setContent(R.id.tab2).setIndicator("이벤트");
        tabHost.addTab(spec2);
        TabHost.TabSpec spec3 = tabHost.newTabSpec("Tab3").setContent(R.id.tab3).setIndicator("리뷰");
        tabHost.addTab(spec3);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                if(tabId.equals("Tab1")) {

                    getMenu(Integer.parseInt(i.getStringExtra("store_id")));

                }else if (tabId.equals("Tab2")){

                    getEvent(Integer.parseInt(i.getStringExtra("store_id")));

                }else if (tabId.equals("Tab3")) {


                    getReview(Integer.parseInt(i.getStringExtra("store_id")));
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Intent i = getIntent();


        getMenu(Integer.parseInt(i.getStringExtra("store_id")));
        getReview(Integer.parseInt(i.getStringExtra("store_id")));
        getEvent(Integer.parseInt(i.getStringExtra("store_id")));




        Button addMenuBtn = (Button) findViewById(R.id.addMenuBtn);
        addMenuBtn.setOnClickListener(new Button.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              Intent intent = new Intent(
                                                      RestaurantDetailManagerActivity.this, // ?꾩옱 ?붾㈃???쒖뼱沅뚯옄
                                                      InsertMenuActivity.class); // ?ㅼ쓬 ?섏뼱媛??대옒??吏??
                                              intent.putExtra( "store_id", i.getStringExtra("store_id") );

                                              startActivity(intent); // ?ㅼ쓬 ?붾㈃?쇰줈 ?섏뼱媛꾨떎
                                          }
                                      }
        );

        Button addEventBtn = (Button)findViewById(R.id.addEventBtn);
        addEventBtn.setOnClickListener(new Button.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               Intent intent = new Intent(
                                                       RestaurantDetailManagerActivity.this, // ?꾩옱 ?붾㈃???쒖뼱沅뚯옄
                                                       InsertEventActivity.class); // ?ㅼ쓬 ?섏뼱媛??대옒??吏??
                                               intent.putExtra( "store_id", i.getStringExtra("store_id") );
                                               startActivity(intent); // ?ㅼ쓬 ?붾㈃?쇰줈 ?섏뼱媛꾨떎
                                           }
                                       }
        );

        Button addReviewBtn = (Button)findViewById(R.id.addReviewBtn);
        addReviewBtn.setOnClickListener(new Button.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(
                                                        RestaurantDetailManagerActivity.this, // ?꾩옱 ?붾㈃???쒖뼱沅뚯옄
                                                        InsertReviewActivity.class); // ?ㅼ쓬 ?섏뼱媛??대옒??吏??
                                                intent.putExtra( "store_id", i.getStringExtra("store_id") );
                                                startActivity(intent); // ?ㅼ쓬 ?붾㈃?쇰줈 ?섏뼱媛꾨떎
                                            }
                                        }
        );





    }

    //가게 데이터
    public void getStore(final int store_id) {
        Thread thread = new Thread() {
            @Override

            public void run() {

                String urlString = server_address+"selectRestaurant.do";
                StringBuilder sb = new StringBuilder();
                //adding some data to send along with the request to the server
                sb.append("store_id="+String.valueOf(store_id));
                URL url;
                try {
                    System.out.println("실행");
                    url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10 * 1000);
                    conn.setReadTimeout(10 * 1000);
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");

                    OutputStreamWriter wr = new OutputStreamWriter(conn
                            .getOutputStream());
                    wr.write(sb.toString());
                    wr.flush();
                    wr.close();
                    InputStream myInputStream = conn.getInputStream();
                    StringBuffer json = new StringBuffer();
                    String line = null;
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                        while ((line = reader.readLine()) != null) {
                            json.append(line);
                        }
                    } catch (Exception e) {
                        System.out.println("Error reading JSON string:" + e.toString());

                    }
                    System.out.println(json.toString());
                    restaurants = new ArrayList<RestaurantVo>();


                    try {
                        JSONParser jsonParser = new JSONParser();
                        // JSON데이터를 넣어 JSON Object 로 만들어 준다.
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(json.toString());
                        // books의 배열을 추출
                        JSONArray jsonInfoArray = (JSONArray) jsonObject.get("store");

                        for (int i = 0; i < jsonInfoArray.size(); i++) {
                            RestaurantVo restaurantVo = new RestaurantVo();
                            // 배열 안에 있는것도 JSON형식 이기 때문에 JSON Object 로 추출
                            JSONObject jsonVoObject = (JSONObject) jsonInfoArray.get(i);
                            // JSON name으로 추출
                            restaurantVo.setStore_adress(jsonVoObject.get("store_address").toString());
                            if(jsonVoObject.get("store_eval").toString() != "")
                                restaurantVo.setStore_eval(jsonVoObject.get("store_eval").toString());
                            else
                                restaurantVo.setStore_eval("0.0");
                            restaurantVo.setStore_phone(jsonVoObject.get("store_phone").toString());
                            restaurantVo.setStore_name(jsonVoObject.get("store_name").toString());
                            restaurantVo.setStore_info(jsonVoObject.get("store_info").toString());
                            restaurantVo.setStore_picture(jsonVoObject.get("store_picture").toString());
                            restaurants.add(restaurantVo);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    System.out.println("error");
                    //handle the exception !
                    //Log.d(TAG,e.getMessage());
                }
            }
        };
        thread.start();


        try {
            thread.join();
            //평점
            String str = restaurants.get(0).getStore_eval();
            TextView rateTextView = (TextView) findViewById(R.id.rateTextView);
            final SpannableStringBuilder sps = new SpannableStringBuilder(str);
            sps.setSpan(new AbsoluteSizeSpan(60), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            rateTextView.setText("");
            rateTextView.setText("평점\n");
            rateTextView.append(sps);
            TextView storeInfoTextView = (TextView) findViewById(R.id.store_info);
            TextView storeAddressTextView = (TextView) findViewById(R.id.store_address);
            TextView storePhoneTextView = (TextView) findViewById(R.id.store_phone);


            storeInfoTextView.setText(restaurants.get(0).getStore_info());

            storeAddressTextView.setText(restaurants.get(0).getStore_adress());
            storePhoneTextView.setText(restaurants.get(0).getStore_phone());


            iv = (ImageView)findViewById(R.id.imageView);
            scrollView = (ScrollView)findViewById(R.id.scrollView);
            new LoadImagefromUrl( ).execute(  iv,restaurants.get(0).getStore_picture());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    //메뉴 데이터
    public void getMenu(final int store_id)
    {

        Thread thread = new Thread() {
            @Override

            public void run() {
                String urlString = server_address+"selectMenu.do";
                StringBuilder sb = new StringBuilder();
                //adding some data to send along with the request to the server
                sb.append("store_id="+String.valueOf(store_id));
                URL url;
                try {
                    System.out.println("실행");
                    url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10 * 1000);
                    conn.setReadTimeout(10 * 1000);
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");

                    OutputStreamWriter wr = new OutputStreamWriter(conn
                            .getOutputStream());
                    wr.write(sb.toString());
                    wr.flush();
                    wr.close();
                    InputStream myInputStream = conn.getInputStream();
                    StringBuffer json = new StringBuffer();
                    String line = null;
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                        while ((line = reader.readLine()) != null) {
                            json.append(line);
                        }
                    } catch (Exception e) {
                        System.out.println("Error reading JSON string:" + e.toString());

                    }
                    System.out.println(json.toString());
                    menu = new ArrayList<MenuVo>();

                    try {
                        JSONParser jsonParser = new JSONParser();
                        // JSON데이터를 넣어 JSON Object 로 만들어 준다.
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(json.toString());
                        // books의 배열을 추출
                        JSONArray jsonInfoArray = (JSONArray) jsonObject.get("menu");

                        for (int i = 0; i < jsonInfoArray.size(); i++) {
                            // 배열 안에 있는것도 JSON형식 이기 때문에 JSON Object 로 추출
                            MenuVo menuVo = new MenuVo();
                            JSONObject jsonVoObject = (JSONObject) jsonInfoArray.get(i);
                            // JSON name으로 추출
                            menuVo.setMenu_id(Integer.parseInt(jsonVoObject.get("menu_id").toString()));
                            menuVo.setMenu_price(jsonVoObject.get("menu_price").toString());
                            menuVo.setMenu_name(jsonVoObject.get("menu_name").toString());
                            menuVo.setMenu_picture(jsonVoObject.get("menu_picture").toString());
                            menu.add(menuVo);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    System.out.println("error");
                    //handle the exception !
                    //Log.d(TAG,e.getMessage());
                }
            }
        };
        thread.start();


        try {
            thread.join();
            //메뉴
            final LinearLayout inLayout = (LinearLayout)findViewById(R.id.menuView);
            inLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LayoutInflater inflaters = null;
            LinearLayout listLineViewItem = null;
            LinearLayout inLineLayout = null;

            for(int i=0; i<menu.size(); i++)
            {

                if(i%2 ==0)
                {
                    //horizontal
                    inflaters = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    listLineViewItem = (LinearLayout) inflaters.inflate(R.layout.menu_line, null);
                    inLineLayout = (LinearLayout) listLineViewItem.findViewById(R.id.menuLineView);
                }

                MenuVo item = new MenuVo();
                item = menu.get(i);
                final int menu_id=menu.get(i).getMenu_id();
                final String menu_name=menu.get(i).getMenu_name().toString();
                LinearLayout listViewItem = (LinearLayout) inflater.inflate(R.layout.menu, null);

                listViewItem.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new AlertDialog.Builder(RestaurantDetailManagerActivity.this)

                                .setTitle("메뉴 삭제") //팝업창 타이틀바

                                .setMessage("(" + menu_name + ")" + "메뉴를 삭제하시겠습니까?")  //팝업창 내용

                                .setNeutralButton("뒤로", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dlg, int sumthin) {
                                    }
                                })
                                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        Thread thread = new Thread() {
                                            @Override

                                            public void run() {

                                                String urlString = server_address + "deleteMenu.do";
                                                StringBuilder sb = new StringBuilder();
                                                //adding some data to send along with the request to the server
                                                sb.append("menu_id=" + Integer.toString(menu_id));
                                                URL url;
                                                try {
                                                    System.out.println("실행");
                                                    url = new URL(urlString);
                                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                                    conn.setConnectTimeout(10 * 1000);
                                                    conn.setReadTimeout(10 * 1000);
                                                    conn.setRequestProperty("Cache-Control", "no-cache");
                                                    conn.setRequestProperty("Content-Type", "application/json");
                                                    conn.setRequestProperty("Accept", "application/json");
                                                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                                                    conn.setDoOutput(true);
                                                    conn.setDoInput(true);
                                                    conn.setRequestMethod("POST");

                                                    OutputStreamWriter wr = new OutputStreamWriter(conn
                                                            .getOutputStream());
                                                    wr.write(sb.toString());
                                                    wr.flush();
                                                    wr.close();
                                                    InputStream myInputStream = conn.getInputStream();
                                                } catch (Exception e) {
                                                    System.out.println("error");
                                                    //handle the exception !
                                                    //Log.d(TAG,e.getMessage());
                                                }
                                            }
                                        };
                                        thread.start();
                                        try {
                                            thread.join();
                                            Intent i = getIntent();
                                                             /*   inLayout.removeAllViews();*/
                                            getMenu(Integer.parseInt(i.getStringExtra("store_id")));

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }//삭제
                                })//삭제
                                .show(); // 팝업창 보여줌
                        return false;
                    }
                });
                listViewItem.setOnClickListener(new LinearLayout.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(
                                RestaurantDetailManagerActivity.this, // ?꾩옱 ?붾㈃???쒖뼱沅뚯옄
                                MenuInfoActivity.class); // ?ㅼ쓬 ?섏뼱媛??대옒??吏??
                        intent.putExtra( "menu_id",  Integer.toString(menu_id));
                        intent.putExtra( "menu_name", menu_name);
                        startActivity(intent); // ?ㅼ쓬 ?붾㈃?쇰줈 ?섏뼱媛꾨떎
                    }
                });
                ImageView miv = (ImageView) listViewItem.findViewById(R.id.menuView);
                new LoadImagefromUrl( ).execute(  miv,item.getMenu_picture());
                TextView tv = (TextView) listViewItem.findViewById(R.id.menuName);
                tv.setText(item.getMenu_name().toString());
                TextView tv1 = (TextView) listViewItem.findViewById(R.id.menuPrice);
                tv1.setText(item.getMenu_price().toString());
                inLineLayout.addView(listViewItem);
                if(menu.size() == i+1)
                {
                    listLineViewItem.setGravity(CENTER_HORIZONTAL);

                    LinearLayout noItem = (LinearLayout) inflater.inflate(R.layout.menu, null);
                    inLineLayout.addView(noItem);
                    inLayout.addView(listLineViewItem);


                }
                else if(i%2 !=  0)
                {
                    listLineViewItem.setGravity(CENTER_HORIZONTAL);
                    inLayout.addView(listLineViewItem);
            /*                inLayout.setGravity(CENTER_HORIZONTAL);*/

                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    //리뷰 데이터
    public void getReview(final int store_id)
    {
        Thread thread = new Thread() {
            @Override

            public void run() {
                String urlString = server_address+"selectReview.do";
                StringBuilder sb = new StringBuilder();
                //adding some data to send along with the request to the server
                sb.append("store_id="+String.valueOf(store_id));
                URL url;
                try {
                    System.out.println("실행");
                    url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10 * 1000);
                    conn.setReadTimeout(10 * 1000);
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");

                    OutputStreamWriter wr = new OutputStreamWriter(conn
                            .getOutputStream());
                    wr.write(sb.toString());
                    wr.flush();
                    wr.close();
                    InputStream myInputStream = conn.getInputStream();
                    StringBuffer json = new StringBuffer();
                    String line = null;
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                        while ((line = reader.readLine()) != null) {
                            json.append(line);
                        }
                    } catch (Exception e) {
                        System.out.println("Error reading JSON string:" + e.toString());

                    }
                    System.out.println(json.toString());
                    reviews = new ArrayList<ReviewVo>();

                    try {
                        JSONParser jsonParser = new JSONParser();
                        // JSON데이터를 넣어 JSON Object 로 만들어 준다.
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(json.toString());
                        // books의 배열을 추출
                        JSONArray jsonInfoArray = (JSONArray) jsonObject.get("review");

                        for (int i = 0; i < jsonInfoArray.size(); i++) {
                            // 배열 안에 있는것도 JSON형식 이기 때문에 JSON Object 로 추출
                            ReviewVo reviewVo = new ReviewVo();
                            JSONObject jsonVoObject = (JSONObject) jsonInfoArray.get(i);
                            // JSON name으로 추출

                            if(!jsonVoObject.get("member_name").toString().isEmpty()||!jsonVoObject.get("member_name").toString().equals(""))
                                reviewVo.setMember_picture(jsonVoObject.get("member_picture").toString());
                            else
                                reviewVo.setMember_picture(jsonVoObject.get("admin_picture").toString());


                            reviewVo.setReview_picture(jsonVoObject.get("review_picture").toString());
                            reviewVo.setReview_info(jsonVoObject.get("review_info").toString());
                            reviewVo.setWrite_date(jsonVoObject.get("write_date").toString());
                            reviewVo.setMember_id(jsonVoObject.get("member_id").toString());
                            reviewVo.setReview_eval(jsonVoObject.get("review_eval").toString());
                            if(!jsonVoObject.get("member_name").toString().isEmpty()||!jsonVoObject.get("member_name").toString().equals(""))
                                reviewVo.setMember_name(jsonVoObject.get("member_name").toString());
                            else
                                reviewVo.setMember_name(jsonVoObject.get("admin_name").toString());
                            reviews.add(reviewVo);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    System.out.println("error");
                    //handle the exception !
                    //Log.d(TAG,e.getMessage());
                }
            }
        };
        thread.start();

        try {
            thread.join();
            //메뉴
            LinearLayout inLayout = (LinearLayout)findViewById(R.id.reviewView);
            inLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for(int i=0; i<reviews.size(); i++)
            {
                ReviewVo item = new ReviewVo();
                item = reviews.get(i);
                final String key1=reviews.get(i).getMember_id().toString();
                LinearLayout listViewItem = (LinearLayout) inflater.inflate(R.layout.review, null);



                ImageView miv = (ImageView) listViewItem.findViewById(R.id.memberView);
                new LoadCircleImagefromUrl().execute(  miv,item.getMember_picture());
                ImageView miv1 = (ImageView) listViewItem.findViewById(R.id.reviewView);
                new LoadImagefromUrl( ).execute(  miv1,item.getReview_picture());
                TextView tv = (TextView) listViewItem.findViewById(R.id.reviewInfo);
                tv.setText(item.getReview_info().toString());
                TextView tv1 = (TextView) listViewItem.findViewById(R.id.writeDate);
                tv1.setText(item.getWrite_date().toString());
                TextView tv2 = (TextView) listViewItem.findViewById(R.id.memberName);
                tv2.setText(item.getMember_name().toString());
                RatingBar rating= (RatingBar) listViewItem.findViewById(R.id.memberEval);
                rating.setStepSize((float) 0.5);
                rating.setRating(Float.parseFloat(item.getReview_eval().toString()));
                rating.setIsIndicator(false);
                inLayout.addView(listViewItem);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //이벤트 데이터
    public void getEvent(final int store_id)
    {



        Thread thread = new Thread() {
            @Override

            public void run() {
                String urlString = server_address+"selectEvent.do";
                StringBuilder sb = new StringBuilder();
                //adding some data to send along with the request to the server
                sb.append("store_id="+String.valueOf(store_id));
                URL url;
                try {
                    System.out.println("실행");
                    url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10 * 1000);
                    conn.setReadTimeout(10 * 1000);
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");

                    OutputStreamWriter wr = new OutputStreamWriter(conn
                            .getOutputStream());
                    wr.write(sb.toString());
                    wr.flush();
                    wr.close();
                    InputStream myInputStream = conn.getInputStream();
                    StringBuffer json = new StringBuffer();
                    String line = null;
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                        while ((line = reader.readLine()) != null) {
                            json.append(line);
                        }
                    } catch (Exception e) {
                        System.out.println("Error reading JSON string:" + e.toString());

                    }
                    System.out.println(json.toString());
                    events = new ArrayList<EventVo>();

                    try {
                        JSONParser jsonParser = new JSONParser();
                        // JSON데이터를 넣어 JSON Object 로 만들어 준다.
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(json.toString());
                        // books의 배열을 추출
                        JSONArray jsonInfoArray = (JSONArray) jsonObject.get("event");

                        for (int i = 0; i < jsonInfoArray.size(); i++) {
                            // 배열 안에 있는것도 JSON형식 이기 때문에 JSON Object 로 추출
                            EventVo event = new EventVo();
                            JSONObject jsonVoObject = (JSONObject) jsonInfoArray.get(i);
                            // JSON name으로 추출
                            event.setEvent_picture(jsonVoObject.get("event_picture").toString());
                            event.setEvent_name(jsonVoObject.get("event_name").toString());
                            event.setEvent_price(jsonVoObject.get("event_price").toString());
                            event.setEvent_info(jsonVoObject.get("event_info").toString());
                            event.setEvent_type(jsonVoObject.get("event_type").toString());
                            event.setEvent_id(Integer.parseInt(jsonVoObject.get("event_id").toString()));
                            events.add(event);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    System.out.println("error");
                    //handle the exception !
                    //Log.d(TAG,e.getMessage());
                }
            }
        };
        thread.start();
        try {
            thread.join();
            //메뉴
            final  LinearLayout inLayout = (LinearLayout)findViewById(R.id.eventView);
            inLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for(int i=0; i<events.size(); i++)
            {
                EventVo item = new EventVo();
                item = events.get(i);
                final int event_id=events.get(i).getEvent_id();
                final String event_name=events.get(i).getEvent_name().toString();
                LinearLayout listViewItem = (LinearLayout) inflater.inflate(R.layout.event, null);

                listViewItem.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new AlertDialog.Builder(RestaurantDetailManagerActivity.this)

                                .setTitle("이벤트 삭제") //팝업창 타이틀바

                                .setMessage("(" + event_name + ")" + "이벤트를 삭제하시겠습니까?")  //팝업창 내용

                                .setNeutralButton("뒤로", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dlg, int sumthin) {
                                    }
                                })
                                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        Thread thread = new Thread() {
                                            @Override

                                            public void run() {

                                                String urlString = server_address + "deleteEvent.do";
                                                StringBuilder sb = new StringBuilder();
                                                //adding some data to send along with the request to the server
                                                sb.append("event_id=" + Integer.toString(event_id));
                                                URL url;
                                                try {
                                                    System.out.println("실행");
                                                    url = new URL(urlString);
                                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                                    conn.setConnectTimeout(10 * 1000);
                                                    conn.setReadTimeout(10 * 1000);
                                                    conn.setRequestProperty("Cache-Control", "no-cache");
                                                    conn.setRequestProperty("Content-Type", "application/json");
                                                    conn.setRequestProperty("Accept", "application/json");
                                                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                                                    conn.setDoOutput(true);
                                                    conn.setDoInput(true);
                                                    conn.setRequestMethod("POST");

                                                    OutputStreamWriter wr = new OutputStreamWriter(conn
                                                            .getOutputStream());
                                                    wr.write(sb.toString());
                                                    wr.flush();
                                                    wr.close();
                                                    InputStream myInputStream = conn.getInputStream();
                                                } catch (Exception e) {
                                                    System.out.println("error");
                                                    //handle the exception !
                                                    //Log.d(TAG,e.getMessage());
                                                }
                                            }
                                        };
                                        thread.start();
                                        try {
                                            thread.join();
                                            Intent i = getIntent();
                                            inLayout.removeAllViews();
                                            getEvent(Integer.parseInt(i.getStringExtra("store_id")));
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }//삭제
                                })//삭제
                                .show(); // 팝업창 보여줌
                        return false;
                    }
                });
                ImageView miv = (ImageView) listViewItem.findViewById(R.id.eventView);
                new LoadImagefromUrl( ).execute(  miv,item.getEvent_picture());
                TextView tv = (TextView) listViewItem.findViewById(R.id.eventName);
                tv.setText(item.getEvent_name().toString());
                TextView tv1 = (TextView) listViewItem.findViewById(R.id.eventPrice);
                tv1.setText(item.getEvent_price().toString());
                TextView tv2 = (TextView) listViewItem.findViewById(R.id.eventInfo);
                tv2.setText(item.getEvent_info().toString());
                TextView tv3 = (TextView) listViewItem.findViewById(R.id.eventType);
                tv3.setText(item.getEvent_type().toString());
                inLayout.addView(listViewItem);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}





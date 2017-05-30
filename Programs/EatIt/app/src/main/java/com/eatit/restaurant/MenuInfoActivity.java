package com.eatit.restaurant;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
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

import com.eatit.util.LoadImagefromUrl;
import com.eatit.vo.MenuVo;

import static com.eatit.util.CommonManager.SERVER_ADDR;

public class MenuInfoActivity extends AppCompatActivity {
    String server_address= SERVER_ADDR;
    ArrayList<MenuVo> menu;//메뉴 리스트

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_info);

        final Intent i = getIntent();
        this.setTitle(i.getStringExtra("menu_name")+" 메뉴 정보");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0x80331401));


        getMenu(Integer.parseInt(i.getStringExtra("menu_id")));


    }


    //메뉴 데이터
    public void getMenu(final int menu_id)
    {

        Thread thread = new Thread() {
            @Override

            public void run() {
                String urlString = server_address+"selectMenuInfo.do";
                StringBuilder sb = new StringBuilder();
                //adding some data to send along with the request to the server
                sb.append("menu_id="+String.valueOf(menu_id));
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
                            menuVo.setMenu_price(jsonVoObject.get("menu_price").toString());
                            menuVo.setMenu_name(jsonVoObject.get("menu_name").toString());
                            menuVo.setMenu_info(jsonVoObject.get("menu_info").toString());
                            if(jsonVoObject.get("menu_eval").toString() != "")
                                menuVo.setMenu_eval(Double.parseDouble(jsonVoObject.get("menu_eval").toString()));
                            else
                                menuVo.setMenu_eval(Double.parseDouble("0.0"));

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

            for(int i=0; i<menu.size(); i++)
            {
                MenuVo item = new MenuVo();
                item = menu.get(i);
                ImageView miv = (ImageView) findViewById(R.id.menuView);
                new LoadImagefromUrl( ).execute(  miv,item.getMenu_picture());
                TextView tv = (TextView) findViewById(R.id.menuName);
                tv.setText(item.getMenu_name().toString());
                TextView tv1 = (TextView) findViewById(R.id.menuPrice);
                tv1.setText(item.getMenu_price().toString());
                TextView tv2 = (TextView) findViewById(R.id.menuInfo);
                tv2.setText(item.getMenu_info().toString());
                TextView tv3 = (TextView) findViewById(R.id.menuEval);
                tv3.setText(String.valueOf(Float.parseFloat(String.valueOf(item.getMenu_eval()))));
                RatingBar rating= (RatingBar) findViewById(R.id.menuEvalRatingBar);
                rating.setStepSize((float) 0.5);
                rating.setRating(Float.parseFloat(String.valueOf(item.getMenu_eval())));
                rating.setIsIndicator(false);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


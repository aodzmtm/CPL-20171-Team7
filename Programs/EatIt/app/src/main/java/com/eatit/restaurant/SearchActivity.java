package com.eatit.restaurant;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

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

import com.eatit.vo.Restaurant;

import static com.eatit.util.CommonManager.SERVER_ADDR;

public class SearchActivity extends AppCompatActivity {
    final String server_address= SERVER_ADDR;
    ArrayList<Restaurant> items = new ArrayList<>();
    EditText search;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0x80331401));

        this.setTitle("식당 검색");

        search = (EditText) findViewById(R.id.search);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = search.getText().toString();
                System.out.println(searchText);
                //가게 이름 검색하기
                getStoreName(searchText);
                // 필터는 getStoreName함수로 간다     restaurantAdapter.fillter(searchText);
            }
        });

        initLayout();
    }

    private void initLayout(){
        mRecyclerView = (RecyclerView)findViewById(R.id.store_list);
    }

    //가게 데이터
    public void getStoreName(final String store_name) {
        Thread thread = new Thread() {
            @Override

            public void run() {
                String urlString = server_address+"selectStore.do";
                StringBuilder sb = new StringBuilder();
                //adding some data to send along with the request to the server
                sb.append("store_name="+store_name);
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
                    items = new ArrayList<Restaurant>();

                    try {
                        JSONParser jsonParser = new JSONParser();
                        // JSON데이터를 넣어 JSON Object 로 만들어 준다.
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(json.toString());
                        // books의 배열을 추출
                        JSONArray jsonInfoArray = (JSONArray) jsonObject.get("store");

                        for (int i = 0; i < jsonInfoArray.size(); i++) {
                            Restaurant restaurantVo = new Restaurant();
                            // 배열 안에 있는것도 JSON형식 이기 때문에 JSON Object 로 추출
                            JSONObject jsonVoObject = (JSONObject) jsonInfoArray.get(i);
                            // JSON name으로 추출
                            restaurantVo.setStoreID(Integer.parseInt(jsonVoObject.get("store_id").toString()));
                            restaurantVo.setAddr(jsonVoObject.get("store_address").toString());
                            String eval = jsonVoObject.get("store_eval").toString();
                            if(eval.isEmpty()){
                                restaurantVo.setGrade("0.0");
                            }
                            else{
                                restaurantVo.setGrade(eval);
                            }
                            restaurantVo.setRes_name(jsonVoObject.get("store_name").toString());
                            restaurantVo.setBeaconID(jsonVoObject.get("beacon_id").toString());
                            restaurantVo.setCategory(10);
                            restaurantVo.setPhone(jsonVoObject.get("store_phone").toString());
                            restaurantVo.setPicture(jsonVoObject.get("store_picture").toString());
                            items.add(restaurantVo);
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

            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            TestRecyclerViewAdapter mRecyclerViewAdapter = new TestRecyclerViewAdapter(items);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package com.eatit.restaurant;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.eatit.util.LoadCircleImagefromUrl;
import com.eatit.util.LoginSession;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.eatit.util.CommonManager.SERVER_ADDR;

public class DrawerActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView navigationView;

    private View navHeader;
    private ImageView profile_img;
    private TextView profile_name;
    private TextView profile_id;

    LoginSession loginSession = new LoginSession();

    private String server_address = SERVER_ADDR;
    private int storeID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, 0, 0);
        //mDrawer.setDrawerListener(mDrawerToggle);
        mDrawer.addDrawerListener(mDrawerToggle);

        navigationView = (NavigationView) findViewById(R.id.left_drawer);
        setUpNavigationView();

        navHeader = navigationView.getHeaderView(0);
        profile_img = (ImageView)navHeader.findViewById(R.id.profile_img);
        profile_name = (TextView)navHeader.findViewById(R.id.profile_name);
        profile_id = (TextView)navHeader.findViewById(R.id.profile_email);

        System.out.println("++++++"+loginSession.getInstance().getBeacon_id()+"\n");
        System.out.println("++++++"+loginSession.getInstance().getAdmin_id()+"\n");
        System.out.println("++++++"+loginSession.getInstance().getMember_id()+"\n");
        System.out.println("++++++"+loginSession.getInstance().getLoginType()+"\n");

        // guest의 경우. 최초 로그인 전에는 값 할당 없이 변수 자체가 null
        if(loginSession.getInstance().getAdmin_id() == null && loginSession.getInstance().getMember_id() == null){
            profile_img.setImageResource(R.drawable.member);
            profile_name.setText("Guest");
            profile_id.setText("");

            navigationView.getMenu().findItem(R.id.drawer_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.drawer_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_search).setVisible(true);
            navigationView.getMenu().findItem(R.id.drawer_my_store).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_register).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_refresh).setVisible(true);
        }

        // guest의 경우. 로그아웃에 의한 값이 비어 있는 상태
        else if(loginSession.getInstance().getAdmin_id().isEmpty() && loginSession.getInstance().getMember_id().isEmpty()){
            profile_img.setImageResource(R.drawable.member);
            profile_name.setText("Guest");
            profile_id.setText("");

            navigationView.getMenu().findItem(R.id.drawer_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.drawer_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_search).setVisible(true);
            navigationView.getMenu().findItem(R.id.drawer_my_store).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_register).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_refresh).setVisible(true);
        }
        // member의 경우
        else if(!loginSession.getInstance().getMember_id().isEmpty() && loginSession.getInstance().getLoginType() == 0){
            // 이미지 설정
            new LoadCircleImagefromUrl().execute(profile_img, loginSession.getInstance().getMember_picture());
            profile_name.setText(loginSession.getInstance().getMember_name());
            profile_id.setText(loginSession.getInstance().getMember_id());

            navigationView.getMenu().findItem(R.id.drawer_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.drawer_search).setVisible(true);
            navigationView.getMenu().findItem(R.id.drawer_my_store).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_register).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_refresh).setVisible(true);
        }

        // admin의 경우
        else if(!loginSession.getInstance().getAdmin_id().isEmpty() && loginSession.getInstance().getLoginType() == 1){
            new LoadCircleImagefromUrl().execute(profile_img, loginSession.getInstance().getAdmin_picture());
            profile_name.setText(loginSession.getInstance().getAdmin_name());
            profile_id.setText(loginSession.getInstance().getAdmin_id());

            getStoreID(loginSession.getInstance().getBeacon_id());

            navigationView.getMenu().findItem(R.id.drawer_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.drawer_search).setVisible(true);
            if(storeID == -1) {
                navigationView.getMenu().findItem(R.id.drawer_my_store).setVisible(false);
                navigationView.getMenu().findItem(R.id.drawer_register).setVisible(true);
            }
            else{
                navigationView.getMenu().findItem(R.id.drawer_my_store).setVisible(true);
                navigationView.getMenu().findItem(R.id.drawer_register).setVisible(false);
            }
            navigationView.getMenu().findItem(R.id.drawer_refresh).setVisible(true);
        }

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("왜 안돼");

        return super.onOptionsItemSelected(item) || mDrawerToggle.onOptionsItemSelected(item);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.drawer_my_store:
                        Intent myStore_intent = new Intent(DrawerActivity.this, RestaurantDetailManagerActivity.class);
                        myStore_intent.putExtra("store_id", Integer.toString(storeID));
                        myStore_intent.toString();
                        startActivity(myStore_intent);
                        break;
                    case R.id.drawer_search:
                        Intent search_intent=new Intent(DrawerActivity.this, SearchActivity.class);
                        startActivity(search_intent);
                        break;
                    case R.id.drawer_refresh:
                        Intent refresh_intent=new Intent(DrawerActivity.this, MainActivity.class);
                        refresh_intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(refresh_intent);
                        finish();
                        break;
                    case R.id.drawer_logout:
                        loginSession.setAdmin_id(null);
                        loginSession.setBeacon_id(null);
                        loginSession.setLoginType(-1);
                        loginSession.setMember_id(null);
                        storeID = -1;
                        Intent logout_intent = new Intent(DrawerActivity.this, MainActivity.class);
                        logout_intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(logout_intent);
                        finish();
                        break;
                    case R.id.drawer_login:
                        Intent login_intent = new Intent(DrawerActivity.this, LoginActivity.class);
                        startActivity(login_intent);
                        break;
                    case R.id.drawer_register:
                        Intent register_intent = new Intent(DrawerActivity.this, InsertStoreActivity.class);
                        startActivity(register_intent);
                        break;
                }
                return true;
            }
        });
    }

    private void getStoreID(final String b_id) {
        Thread thread = new Thread() {
            @Override

            public void run() {
                String urlString = server_address + "selectStore.do";
                StringBuilder sb = new StringBuilder();
                //adding some data to send along with the request to the server
                sb.append("beacon_id=");
                sb.append(b_id);

                System.out.println("query: " + sb.toString());
                try {
                    System.out.println("실행");
                    URL url = new URL(urlString);
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

                    StringBuffer json = new StringBuffer();
                    String line = null;
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        while ((line = reader.readLine()) != null) {
                            json.append(line);
                        }
                    } catch (Exception e) {
                        System.out.println("Error reading JSON string:" + e.toString());
                    }
                    System.out.println(json.toString());

                    try {
                        JSONParser jsonParser = new JSONParser();
                        // JSON데이터를 넣어 JSON Object 로 만들어 준다.
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(json.toString());
                        // books의 배열을 추출
                        JSONArray jsonInfoArray = (JSONArray) jsonObject.get("store");

                        if (jsonInfoArray.size() == 0)
                            storeID = -1;

                        else {
                            // 배열 안에 있는것도 JSON형식 이기 때문에 JSON Object 로 추출
                            JSONObject jsonVoObject = (JSONObject) jsonInfoArray.get(0);
                            // JSON name으로 추출
                            storeID = Integer.parseInt(jsonVoObject.get("store_id").toString());
                        }

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    System.out.println("error");
                    e.printStackTrace();
                    //handle the exception !
                    //Log.d(TAG,e.getMessage());
                }
            }
        };
        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

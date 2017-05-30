package com.eatit.restaurant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.eatit.parcel.ParcelableRestaurant;

import static com.eatit.util.CommonManager.SERVER_ADDR;


public class MainActivity extends DrawerActivity {
    private static final String TAG = "esbscan";
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
    final int tab_cnt = 6;
    final String server_address = SERVER_ADDR;

    private BeaconManager beaconManager;

    // 비컨 스캔 결과
    private ArrayList<Integer> majorList = new ArrayList<Integer>();

    // 서버와 통신 결과
    private ArrayList<ParcelableRestaurant> resList = new ArrayList<ParcelableRestaurant>();

    // 식당 분류 결과. 한식, 중식, 일식, 양식, 커피, 기타
    private ArrayList<ParcelableRestaurant> kList = new ArrayList<ParcelableRestaurant>();
    private ArrayList<ParcelableRestaurant> cList = new ArrayList<ParcelableRestaurant>();
    private ArrayList<ParcelableRestaurant> jList = new ArrayList<ParcelableRestaurant>();
    private ArrayList<ParcelableRestaurant> wList = new ArrayList<ParcelableRestaurant>();
    private ArrayList<ParcelableRestaurant> coffeeList = new ArrayList<ParcelableRestaurant>();
    private ArrayList<ParcelableRestaurant> etcList = new ArrayList<ParcelableRestaurant>();


    @BindView(R.id.materialViewPager)
    MaterialViewPager mViewPager;
    /*private MaterialViewPager mViewPager;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("");
        ButterKnife.bind(this);

        RestaurantApplication app = (RestaurantApplication) getApplication();
        beaconManager = app.getBeaconManager();
        beaconManager.setForegroundScanPeriod(TimeUnit.SECONDS.toMillis(7), TimeUnit.SECONDS.toMillis(10));
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                if (!beacons.isEmpty()) {
                    majorList.clear();
                    resList.clear();
                    kList.clear();
                    cList.clear();
                    jList.clear();
                    wList.clear();
                    coffeeList.clear();
                    etcList.clear();

                    // majorList 생성
                    System.out.println("---beacon loop start---");
                    for (int i = 0; i < beacons.size(); i++) {
                        majorList.add(beacons.get(i).getMajor());
                    }
                    System.out.println("---beacon loop end---");

                    // 서버와 통신

                    Thread thread = new Thread() {
                        @Override

                        public void run() {
                            String urlString = server_address + "selectStore.do";
                            StringBuilder sb = new StringBuilder();
                            //adding some data to send along with the request to the server
                            sb.append("beacon_id=");

                            for (int i = 0; i < majorList.size(); i++) {
                                sb.append(majorList.get(i));

                                if (i != (majorList.size() - 1)) {
                                    sb.append(",");
                                }
                            }
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

                                    for (int i = 0; i < jsonInfoArray.size(); i++) {

                                        // 배열 안에 있는것도 JSON형식 이기 때문에 JSON Object 로 추출
                                        JSONObject jsonVoObject = (JSONObject) jsonInfoArray.get(i);
                                        // JSON name으로 추출
                                        int storeID = Integer.parseInt(jsonVoObject.get("store_id").toString());
                                        int category = Integer.parseInt(jsonVoObject.get("store_type").toString());
                                        String addr = jsonVoObject.get("store_address").toString();
                                        String grade = jsonVoObject.get("store_eval").toString();
                                        String phone = jsonVoObject.get("store_phone").toString();
                                        String name = jsonVoObject.get("store_name").toString();
                                        jsonVoObject.get("store_info").toString();
                                        String picture = jsonVoObject.get("store_picture").toString();
                                        String beaconID = jsonVoObject.get("beacon_id").toString();

                                        System.out.println("aa store name " + name);
                                        System.out.println("aa category " + name);
                                        System.out.println("");

                                        resList.add(new ParcelableRestaurant(storeID, category, name, addr, grade, phone, beaconID, picture));
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

                    try{
                        thread.join();

                        for (int i = 0; i < resList.size(); i++)
                            System.out.println("식당: " + resList.toString());

                        for (int i = 0; i < resList.size(); i++) {
                            if (resList.get(i).getCategory() == 0)
                                kList.add(resList.get(i));

                            else if (resList.get(i).getCategory() == 1)
                                cList.add(resList.get(i));

                            else if (resList.get(i).getCategory() == 2)
                                jList.add(resList.get(i));

                            else if (resList.get(i).getCategory() == 3)
                                wList.add(resList.get(i));

                            else if (resList.get(i).getCategory() == 4)
                                coffeeList.add(resList.get(i));

                            else if (resList.get(i).getCategory() == 5)
                                etcList.add(resList.get(i));
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                    mViewPager.getViewPager().getAdapter().notifyDataSetChanged();
                }
            }
        });

        //mViewPager = (MaterialViewPager)findViewById(R.id.materialViewPager);

        final Toolbar toolbar = mViewPager.getToolbar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position % tab_cnt) {
                    case 0:
                        return RecyclerViewFragment.newInstance(position, kList);
                    case 1:
                        return RecyclerViewFragment.newInstance(position, cList);
                    case 2:
                        return RecyclerViewFragment.newInstance(position, jList);
                    case 3:
                        return RecyclerViewFragment.newInstance(position, wList);
                    case 4:
                        return RecyclerViewFragment.newInstance(position, coffeeList);
                    case 5:
                        return RecyclerViewFragment.newInstance(position, etcList);
                    default:
                        return RecyclerViewFragment.newInstance(position, etcList);
                }
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public int getCount() {
                return tab_cnt;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position % tab_cnt) {
                    case 0:
                        return "한식";
                    case 1:
                        return "중식";
                    case 2:
                        return "일식";
                    case 3:
                        return "양식";
                    case 4:
                        return "카페";
                    case 5:
                        return "기타";
                }
                return "";
            }
        });



       /* mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.green,
                                "http://i.imgur.com/3rkfR1q.gif");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.blue,
                                "http://i.imgur.com/hrhri3l.jpg");
                    case 2:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.cyan,
                                "http://i.imgur.com/1NPtJcX.png");
                    case 3:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.green,
                                "http://i.imgur.com/IF0bAhs.jpg");
                    case 4:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.blue,
                                "http://i.imgur.com/MngF9KV.png"
                        );
                    case 5:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.cyan,
                                "http://i.imgur.com/YzqpjLT.jpg"
                        );
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });*/

        /*mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        Bitmap bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.header_1);
                        Drawable bmDrawable1 = new BitmapDrawable(getResources(), bm1);
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.lightCoral,
                                bmDrawable1);
                    case 1:
                        Bitmap bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.header_2);
                        Drawable bmDrawable2 = new BitmapDrawable(getResources(), bm2);
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.lightPink,
                                bmDrawable2);
                    case 2:
                        Bitmap bm3 = BitmapFactory.decodeResource(getResources(), R.drawable.header_3);
                        Drawable bmDrawable3 = new BitmapDrawable(getResources(), bm3);
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.lightCyan,
                                bmDrawable3);
                    case 3:
                        Bitmap bm4 = BitmapFactory.decodeResource(getResources(), R.drawable.header_4);
                        Drawable bmDrawable4 = new BitmapDrawable(getResources(), bm4);
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.lightYellow,
                                bmDrawable4);
                    case 4:
                        Bitmap bm5 = BitmapFactory.decodeResource(getResources(), R.drawable.header_5);
                        Drawable bmDrawable5 = new BitmapDrawable(getResources(), bm5);
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.tan,
                                bmDrawable5);
                    case 5:
                        Bitmap bm6 = BitmapFactory.decodeResource(getResources(), R.drawable.header_6);
                        Drawable bmDrawable6 = new BitmapDrawable(getResources(), bm6);
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.lightGreen,
                                bmDrawable6);
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });*/

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        Bitmap bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.header_1);
                        Drawable bmDrawable1 = new BitmapDrawable(getResources(), bm1);
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.lightCoral,
                                bmDrawable1);
                    case 1:
                        Bitmap bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.header_2);
                        Drawable bmDrawable2 = new BitmapDrawable(getResources(), bm2);
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.lightPink,
                                bmDrawable2);
                    case 2:
                        Bitmap bm3 = BitmapFactory.decodeResource(getResources(), R.drawable.header_3);
                        Drawable bmDrawable3 = new BitmapDrawable(getResources(), bm3);
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.lightCyan,
                                bmDrawable3);
                    case 3:
                        Bitmap bm4 = BitmapFactory.decodeResource(getResources(), R.drawable.header_4);
                        Drawable bmDrawable4 = new BitmapDrawable(getResources(), bm4);
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.lightYellow,
                                bmDrawable4);
                    case 4:
                        Bitmap bm5 = BitmapFactory.decodeResource(getResources(), R.drawable.header_5);
                        Drawable bmDrawable5 = new BitmapDrawable(getResources(), bm5);
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.tan,
                                bmDrawable5);
                    case 5:
                        Bitmap bm6 = BitmapFactory.decodeResource(getResources(), R.drawable.header_6);
                        Drawable bmDrawable6 = new BitmapDrawable(getResources(), bm6);
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.lightGreen,
                                bmDrawable6);
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

       /* final View logo = findViewById(R.id.logo_white);
        if (logo != null) {
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.notifyHeaderChanged();
                    Toast.makeText(getApplicationContext(), "검색", Toast.LENGTH_SHORT).show();
                }
            });
        }*/

        // 검색 버튼
        final View logo = findViewById(R.id.logo_img);
        if (logo != null) {
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.notifyHeaderChanged();
                    /* toast test
                    Toast.makeText(getApplicationContext(), "검색", Toast.LENGTH_SHORT).show();
                    */
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Log.d(TAG, "startRanging");
                beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
        beaconManager.disconnect();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* 어플리케이션 시작시, Bluetooth와 GPS 실행 요청*/
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }
}

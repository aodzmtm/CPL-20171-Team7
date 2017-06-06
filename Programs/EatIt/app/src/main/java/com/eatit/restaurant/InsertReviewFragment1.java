package com.eatit.restaurant;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

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

import com.eatit.util.CustomSpinnerAdapt;
import com.eatit.util.LoadImagefromUrl;
import com.eatit.vo.MenuVo;

import static com.eatit.util.CommonManager.SERVER_ADDR;

public class InsertReviewFragment1 extends Fragment implements View.OnClickListener {
    private final String server_address= SERVER_ADDR;
    private ArrayList<String> menuname = new ArrayList<String>();
    private ArrayList<Integer> menuID=new ArrayList<Integer>();

    private int storeId;
    private int rate=5;
    private int menu;

    private ImageView menuView;
    private TextView menuView_txt;
    private ArrayList<String> MenuIMG= new ArrayList<>();

    private DataCallbackHandler dataCallbackhandler;
    private ArrayList<MenuVo> menuArray;//메뉴 리스트
    private Spinner RW_spin;

    public InsertReviewFragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.fragment_insert_review_fragment1, null);

        storeId = getArguments().getInt("store_id");
        view.findViewById(R.id.RWrite_btn1).setOnClickListener(this);

        TextView name = (TextView)view.findViewById(R.id.Rwrite_letter);
        Typeface Hi = Typeface.createFromAsset(getActivity().getAssets(),"fonts/JejuHallasan.ttf");
        name.setTypeface(Hi);

        RW_spin = (Spinner) view.findViewById(R.id.Rwrite_spin);
        getMenu(storeId);

        menuView = (ImageView) view.findViewById(R.id.menuView);
        menuView_txt=(TextView) view.findViewById(R.id.menuView_txt);

        RW_spin.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener(){
                    public void onItemSelected( AdapterView<?> parent, View v, int pos, long id){
                        Log.i("ReviewActivity.spinner ","pos=" +pos +", id= " + id);
                        Log.i("Spinner","menuid"+menuID.get(pos));

                        if(pos>0) {
                            new LoadImagefromUrl().execute(menuView, MenuIMG.get(pos));
                            menuView_txt.setText("");
                            menu=menuID.get(pos);
                        }
                        else{
                            menuView.setImageResource(0);
                            menuView_txt.setText("아래 박스를 통해, 메뉴 선택해주세요");
                            menu=0;
                        }
                    }
                    public void onNothingSelected(AdapterView<?> parent){
                        Log.i("Review.spinner","선택 없음");
                    }
                }
        );
        
        SmileRating smileRating = (SmileRating) view.findViewById(R.id.smile_rating);
        smileRating.setSelectedSmile(BaseRating.GREAT, true);

        smileRating.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(@BaseRating.Smiley int smiley, boolean reselected) {

                switch (smiley) {
                    case SmileRating.BAD:
                        rate=2;
                        break;
                    case SmileRating.GOOD:
                        rate=4;
                        break;
                    case SmileRating.GREAT:
                        rate=5;
                        break;
                    case SmileRating.OKAY:
                        rate=3;
                        break;
                    case SmileRating.TERRIBLE:
                        rate=1;
                        break;
                }
            }
        });

        return view;
    }

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
                    menuArray = new ArrayList<MenuVo>();

                    try {
                        JSONParser jsonParser = new JSONParser();
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(json.toString());
                        JSONArray jsonInfoArray = (JSONArray) jsonObject.get("menu");

                        for (int i = 0; i < jsonInfoArray.size(); i++) {
                            MenuVo menuVo = new MenuVo();
                            JSONObject jsonVoObject = (JSONObject) jsonInfoArray.get(i);
                            menuVo.setMenu_id(Integer.parseInt(jsonVoObject.get("menu_id").toString()));
                            menuVo.setMenu_name(jsonVoObject.get("menu_name").toString());
                            menuVo.setMenu_picture(jsonVoObject.get("menu_picture").toString());
                            menuArray.add(menuVo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    System.out.println("error");
                }
            }
        };
        thread.start();


        try {
            thread.join();
            
            menuname.add("메뉴 선택");
            menuID.add(0);
            MenuIMG.add("");

            for(int i=0; i<menuArray.size(); i++)
            {
                MenuVo item = new MenuVo();
                item = menuArray.get(i);
                menu=menuArray.get(0).getMenu_id();        
                menuname.add(item.getMenu_name().toString());
                menuID.add(item.getMenu_id()+1);
                MenuIMG.add(item.getMenu_picture());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String[] MenuName = menuname.toArray(new String[menuname.size()]);

        CustomSpinnerAdapt cusAdapter = new CustomSpinnerAdapt(getActivity().getBaseContext(), MenuName);
        RW_spin.setAdapter(cusAdapter);
    }


    @Override
    public void onClick(View view) {
        if(menu >0 )
            dataCallbackhandler.DataCallbackHandler(menu, rate);
        else
            Toast.makeText(getActivity(), "메뉴를 선택해 주세요", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        if( context instanceof DataCallbackHandler){
            dataCallbackhandler = (DataCallbackHandler) context;
        }else{
            throw new RuntimeException(context.toString()+"must implement Review Write Data CallbackHandler");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        dataCallbackhandler=null;
    }

    public interface DataCallbackHandler{
        void DataCallbackHandler(int menu, int rate);
    }
}

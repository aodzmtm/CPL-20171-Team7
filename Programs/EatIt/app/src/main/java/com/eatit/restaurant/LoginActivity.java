package com.eatit.restaurant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.eatit.util.LoginSession;
import com.eatit.vo.LoginSessionVo;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.eatit.util.CommonManager.SERVER_ADDR;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_email)    EditText _emailText;
    @BindView(R.id.input_password)    EditText _passwordText;
    @BindView(R.id.btn_login)    Button _loginButton;
    @BindView(R.id.link_signup)    TextView _signupLink;
    @BindView(R.id.link_signup_admin) TextView _signupLink2;

    String server_address = SERVER_ADDR;
    LoginSessionVo loginSessionVo = new LoginSessionVo();
    LoginSession loginSession = new LoginSession();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0x80331401));
        this.setTitle("로그인");
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), InsertMemberActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                //finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        _signupLink2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), InsertAdminActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                //finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

/*
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
*/

        ///////////////////////////////////////////////////

        Thread thread = new Thread() {
            @Override

            public void run() {

                String urlString = server_address + "selectLogin.do";
                StringBuilder sb = new StringBuilder();
                //adding some data to send along with the request to the server
                sb.append("login_id=" + _emailText.getText().toString() + "&password=" + _passwordText.getText().toString());

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
                        JSONArray jsonInfoArray = (JSONArray) jsonObject.get("loginSession");

                        for (int i = 0; i < jsonInfoArray.size(); i++) {
                            // 배열 안에 있는것도 JSON형식 이기 때문에 JSON Object 로 추출
                            JSONObject jsonVoObject = (JSONObject) jsonInfoArray.get(i);
                            // JSON name으로 추출
                            loginSessionVo.setAdmin_name(jsonVoObject.get("admin_name").toString());
                            loginSessionVo.setMember_name(jsonVoObject.get("member_name").toString());
                            loginSessionVo.setAdmin_picture(jsonVoObject.get("admin_picture").toString());
                            loginSessionVo.setMember_picture(jsonVoObject.get("member_picture").toString());
                            loginSessionVo.setAdmin_id(jsonVoObject.get("admin_id").toString());
                            loginSessionVo.setMember_id(jsonVoObject.get("member_id").toString());
                            loginSessionVo.setBeacon_id(jsonVoObject.get("beacon_id").toString());
                            loginSessionVo.setLoginType(Integer.parseInt(jsonVoObject.get("loginType").toString()));
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

            System.out.println("----------------------" + loginSessionVo.getMember_id());
            System.out.println("----------------------" + loginSessionVo.getAdmin_id());
            System.out.println("----------------------" + loginSessionVo.getMember_picture());
            System.out.println("----------------------" + loginSessionVo.getAdmin_picture());
            System.out.println("----------------------" + loginSessionVo.getMember_name());
            System.out.println("----------------------" + loginSessionVo.getAdmin_name());
            loginSession.getInstance().setAdmin_name(loginSessionVo.getAdmin_name());
            loginSession.getInstance().setMember_name(loginSessionVo.getMember_name());

            loginSession.getInstance().setAdmin_picture(loginSessionVo.getAdmin_picture());
            loginSession.getInstance().setMember_picture(loginSessionVo.getMember_picture());

            loginSession.getInstance().setAdmin_id(loginSessionVo.getAdmin_id());
            loginSession.getInstance().setMember_id(loginSessionVo.getMember_id());
            loginSession.getInstance().setBeacon_id(loginSessionVo.getBeacon_id());
            loginSession.getInstance().setLoginType(loginSessionVo.getLoginType());
            Intent intent = new Intent(
                    LoginActivity.this, // ?꾩옱 ?붾㈃???쒖뼱沅뚯옄
                    MainActivity.class); // ?ㅼ쓬 ?섏뼱媛??대옒??吏??
            intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent); // ?ㅼ쓬 ?붾㈃?쇰줈 ?섏뼱媛꾨떎
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /////////////////////////////////////////////////////////
/*
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);*/

        //아래 코드 고쳐야 할부분
        ///////////////////////////////////////////////////////////////////
        ProgressDialog pb = new ProgressDialog(LoginActivity.this);
        pb.setMessage("로그인 중 입니다.....");
        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pb.setCanceledOnTouchOutside(false);
        pb.show();
        this.finish();

    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }
*/
    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
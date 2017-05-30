package com.eatit.restaurant;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.eatit.util.CommonManager.SERVER_ADDR;

public class InsertAdminActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_iMAGE = 2;

    private Uri mImageCaptureUri;
    private ImageView iv_UserPhoto;
    private int id_view;
    private String absoultePath;

    private BeaconManager beaconManager;
    private Region region;
    static String store_ID;

    static String serviceDomain =  SERVER_ADDR;
    static String postUrl = serviceDomain + "insertAdmin.do";
    static String CRLF = "\r\n";
    static String twoHyphens = "--";
    static String boundary = "*****b*o*u*n*d*a*r*y*****";
    private String pictureFileName = null;
    private DataOutputStream dataStream = null;

    @Override
    public void onClick(View v) {
        id_view = v.getId();
        if(v.getId() == R.id.user_image) {
            DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doTakePhotoAction();
                }
            };
            DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doTakeAlbumAction();
                }
            };

            DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };

            new AlertDialog.Builder(this)
                    .setTitle("업로드할 이미지 선택")
                    .setPositiveButton("사진촬영", cameraListener)
                    .setNeutralButton("앨범선택", albumListener)
                    .setNegativeButton("취소", cancelListener)
                    .show();
        }


    }


    enum ReturnCode { noPicture, unknown, http201, http400, http401, http403, http404, http500};
    private String TAG = "멀티파트 테스트";

    @BindView(R.id.input_name)  EditText _nameText;
    @BindView(R.id.input_mobile)    EditText _mobileText;
    @BindView(R.id.beacon_uuid)    EditText _beaconText;
    @BindView(R.id.btn_beacon)  Button _beaconButton;
    @BindView(R.id.input_email)    EditText _emailText;
    @BindView(R.id.input_password)    EditText _passwordText;
    @BindView(R.id.input_reEnterPassword)    EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup)    Button _signupButton;
    @BindView(R.id.link_login)  TextView _loginLink;

    String admin_name;
    String admin_phone;
    String beacon_id;
    String admin_id;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_admin);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0x80331401));


        this.setTitle("관리자 등록");
        ButterKnife.bind(this);
        iv_UserPhoto = (ImageView) this.findViewById(R.id.user_image);
        iv_UserPhoto.setOnClickListener(this);
       /* Button btn_agreeJoin = (Button) this.findViewById(R.id.btn_UploadPicture);

        btn_agreeJoin.setOnClickListener(this);*/

        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    store_ID = Integer.toString(nearestBeacon.getMajor());
                }
            }
        });
        region = new Region("ranged region", null, null, null);

        _beaconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _beaconText.setText("Beacon ID:\n"+ store_ID);
            }
        });

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }


    public void doTakePhotoAction()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    public void doTakeAlbumAction()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode != RESULT_OK)
            return;

        switch(requestCode)
        {
            case PICK_FROM_ALBUM:
            {
                mImageCaptureUri = data.getData();
                Log.d("SmartWheel",mImageCaptureUri.getPath().toString());
            }

            case PICK_FROM_CAMERA:
            {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_iMAGE);
                break;
            }
            case CROP_FROM_iMAGE:
            {
                if(resultCode != RESULT_OK) {
                    return;
                }

                final Bundle extras = data.getExtras();
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+
                        "/SmartWheel/"+System.currentTimeMillis()+".jpg";

                if(extras != null)
                {
                    Bitmap photo = extras.getParcelable("data"); // CROP된 BITMAP
                    iv_UserPhoto.setImageBitmap(photo); // 레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌

                    storeCropImage(photo, filePath); // CROP된 이미지를 외부저장소, 앨범에 저장한다.
                    absoultePath = filePath;
                    break;

                }
                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists())
                {
                    f.delete();
                }
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);

        super.onPause();
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(InsertAdminActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        admin_name= _nameText.getText().toString();
        admin_phone = _mobileText.getText().toString().substring(0,3)+"-"
                +_mobileText.getText().toString().substring(3,7)+"-"
                +_mobileText.getText().toString().substring(7);
        beacon_id= store_ID;                                                        //비컨으로부터 값 받아오기
        //  beacon_id= "abc2";                                                        //test
        admin_id = _emailText.getText().toString();
        password = _passwordText.getText().toString();


        //아래 코드 고쳐야 할부분
        ///////////////////////////////////////////////////////////////////
        ProgressDialog pb = new ProgressDialog(InsertAdminActivity.this);
        pb.setMessage("저장 중 입니다.....");
        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pb.setCanceledOnTouchOutside(false);
        pb.show();

        Thread thread = new Thread() {
            @Override
            public void run() {
                uploadPicture(absoultePath);
                //path가 null일때는 사진이 전송 안되게 구현
            }
        };
        thread.start();

        ////////////////////////////////////////////////////////////////////
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }
    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String major = store_ID;
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("세 글자 이상 입력하세요.");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("올바른 메일 주소를 입력하세요.");
            valid = false;
        } else {
            _emailText.setError(null);
        }
        try {
            if (major.isEmpty()) {
                _beaconText.setError("올바른 비콘 ID가 아닙니다.");
                valid = false;
            } else {
                _beaconText.setError(null);
            }
        }
        catch(Exception e){e.printStackTrace();}
        if (mobile.isEmpty() || mobile.length() != 11) {
            _mobileText.setError("올바른 핸드폰 번호를 입력하세요.");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("영어 숫자를 조합하여 4~10자리 입력하세요.");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("올바른 비밀번호가 아닙니다.");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

    public ReturnCode uploadPicture(String pictureFileName)        {
        this.pictureFileName = pictureFileName;
        File uploadFile = null;

        if(pictureFileName != null)
            uploadFile = new File(pictureFileName);


        try     {
            FileInputStream fileInputStream = null;
            if(uploadFile  != null)
                fileInputStream = new FileInputStream(uploadFile);
            URL connectURL = new URL(postUrl);
            HttpURLConnection conn = (HttpURLConnection)connectURL.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection","Keep-Alive");
            conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);
            conn.connect();

            dataStream = new DataOutputStream(conn.getOutputStream());

            //아래에 고쳐야 할 부분
            //////////////////////////////////////////////////////////////////////////////////////////////////

            writeFormField("admin_id",admin_id  );
            writeFormField("admin_name",admin_name );
            writeFormField("beacon_id",beacon_id );
            writeFormField("password",password );
            writeFormField("admin_phone",admin_phone  );


            if( fileInputStream  != null)
                writeFileField("photo", pictureFileName, "image/jpg", fileInputStream,pictureFileName);
            ///////////////////////////////////////////////////////////////////////////////////////////////////

            dataStream.writeBytes(twoHyphens + boundary + twoHyphens + CRLF);
            if( fileInputStream  != null)
                fileInputStream.close();
            dataStream.flush();
            dataStream.close();
            dataStream = null;
            Log.d("업로드 테스트", "***********전송완료***********");


            String response = getResponse(conn);
            int responseCode = conn.getResponseCode();
            if (response.contains("uploaded successfully")) {
                this.finish();
                return ReturnCode.http201;
            }
            else
                return ReturnCode.http401;
        }
        catch (MalformedURLException mue) {
            Log.e(TAG, "error: " + mue.getMessage(), mue);
            return ReturnCode.http400;
        }
        catch (IOException ioe) {
            Log.e(TAG, "error: " + ioe.getMessage(), ioe);
            return ReturnCode.http500;
        }
        catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
            return ReturnCode.unknown;
        }
    }




    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private String getResponse(HttpURLConnection conn) {

        try {
            DataInputStream dis = new DataInputStream(conn.getInputStream());
            byte []        data = new byte[1024];
            int             len = dis.read(data, 0, 1024);
            dis.close();
            int responseCode = conn.getResponseCode();
            if (len > 0)
                return new String(data, 0, len);
            else
                return "";
        }
        catch(Exception e)     {
            Log.e(TAG, "AndroidUploader: "+e);
            return "";
        }
    }



    private String getResponseOrig(HttpURLConnection conn)        {
        InputStream is = null;
        try   {
            is = conn.getInputStream();
            int ch;
            StringBuffer sb = new StringBuffer();
            while( ( ch = is.read() ) != -1 ) {
                sb.append( (char)ch );
            }
            return sb.toString();   // TODO Auto-generated method stub
        }
        catch(Exception e)   {
            Log.e(TAG, "AndroidUploader: "+e);
        }
        finally   {
            try {
                if (is != null)
                    is.close();
            } catch (Exception e) {}
        }
        return "";
    }


    /**
     * write one form field to dataSream
     * @param fieldName
     * @param fieldValue
     */
    private void writeFormField(String fieldName, String fieldValue)  {
        try  {

            dataStream.writeBytes(twoHyphens + boundary + CRLF);
            dataStream.writeBytes("Content-Disposition: form-data; name=\"");
            dataStream.write(fieldName.toString().getBytes());
            dataStream.writeBytes("\"" + CRLF);
            dataStream.writeBytes(CRLF);
            dataStream.write(fieldValue.toString().getBytes());
            dataStream.writeBytes(CRLF);
        }    catch(Exception e)   {
            Log.e(TAG, "AndroidUploader.writeFormField: " + e.getMessage());
        }
    }



    /**
     * write one file field to dataSream
     * @param fieldName - name of file field
     * @param fieldValue - file name
     * @param type - mime type
     * @param fis - stream of bytes that get sent up
     */
    private void writeFileField(
            String fieldName,
            String fieldValue,
            String type,
            FileInputStream fis,
            String path)  {
        try {
            dataStream.writeBytes(twoHyphens + boundary + CRLF);
            dataStream.writeBytes("Content-Disposition: form-data; name=\""
                    + fieldName
                    + "\";filename=\""
                    + fieldValue
                    + "\""
                    + CRLF);
            dataStream.writeBytes("Content-Type: " + type +  CRLF);
            dataStream.writeBytes(CRLF);
            // create a buffer of maximum size
            dataStream.write(convertBitmap(path));

            dataStream.writeBytes(CRLF);
        }
        catch(Exception e)  {
            Log.e(TAG, "AndroidUploader.writeFormField: got: " + e.getMessage());
        }
    }
    public   byte[] convertBitmap(String path) {
        byte[] byteArray =null;
        try {
            Bitmap src = BitmapFactory.decodeFile(path);
            int height = src.getHeight();
            int width = src.getWidth();
            Bitmap resized;
            if(width >1025 || height >1025) {
                if (GetExifOrientation(path) == 90) {
                    src = Bitmap.createScaledBitmap(src, src.getWidth()/ (src.getWidth() / 1000 + 1), src.getHeight() / (src.getWidth() / 1000 + 1), true);
                    resized = imgRotate(src);
                } else {
                    resized = Bitmap.createScaledBitmap(src, width / (width / 1000 + 1), height / (width / 1000 + 1), true);
                }
            }else
            {
                if (GetExifOrientation(path) == 90) {
                    resized = imgRotate(src);
                }else {
                    resized = src;
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 100, out);
            byteArray = out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  byteArray;
    }
    private Bitmap imgRotate(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        bmp.recycle();
        return resizedBitmap;
    }
    public synchronized static int GetExifOrientation(String filepath)

    {
        int degree = 0;
        ExifInterface exif = null;
        try
        {
            exif = new ExifInterface(filepath);
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
        if (exif != null)
        {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1)
            {
                // We only recognize a subset of orientation tag values.
                switch(orientation)
                {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }


    private void storeCropImage(Bitmap bitmap, String filePath) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartWheel";
        File directory_SmartWheel = new File(dirPath);

        if(!directory_SmartWheel.exists())
            directory_SmartWheel.mkdir();

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try {

            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(copyFile)));

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

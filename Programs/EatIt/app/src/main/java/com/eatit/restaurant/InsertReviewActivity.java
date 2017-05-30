package com.eatit.restaurant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.eatit.util.LoginSession;

import static com.eatit.util.CommonManager.SERVER_ADDR;

public class InsertReviewActivity extends AppCompatActivity implements InsertReviewFragment1.DataCallbackHandler, InsertReviewFragment2.DataCallbackHandler2{

    static String serviceDomain =  SERVER_ADDR;
    static String postUrl = serviceDomain + "insertReview.do";
    static String CRLF = "\r\n";
    static String twoHyphens = "--";
    static String boundary = "*****b*o*u*n*d*a*r*y*****";
    private String pictureFileName = null;
    private DataOutputStream dataStream = null;
    enum ReturnCode { noPicture, unknown, http201, http400, http401, http403, http404, http500};
    private String TAG = "멀티파트 테스트";

    LoginSession loginSession = new LoginSession();//로그인 세션

    double review_eval;
    String review_picture="";
    String review_info="";
    String write_date="";
    String member_id="";
    String admin_id="";
    int menu_id;
    int store_id;

    /*************
     **On Create*
     *************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_review);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0x80331401));
        final Intent i = getIntent();
        this.setTitle("리뷰 등록");

        //로그인 정보 불러오기
        System.out.println("++++++"+loginSession.getInstance().getAdmin_id()+"\n");
        System.out.println("++++++"+loginSession.getInstance().getMember_id()+"\n");
        System.out.println("++++++"+loginSession.getInstance().getLoginType()+"\n");

        ///////////////////////////////////////////////////////////////////
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        write_date = df.format(new Date());
        review_picture="";
        review_eval = 5;
        review_info = "";
        menu_id = 1;
        store_id =Integer.parseInt(i.getStringExtra("store_id"));
        member_id = loginSession.getInstance().getMember_id();
        if(loginSession.getInstance().getLoginType()==1)
            admin_id=loginSession.getInstance().getAdmin_id();
        /////////////////////////////////////////////////////////////////
        Fragment fragment = new InsertReviewFragment1();
        Bundle bundle = new Bundle(1);
        bundle.putInt("store_id",store_id);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    /*************
     **UploadPicture : call by Thread Run
     *************/
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

            //////////////////////////////////////////////////////////////////////////////////////////////////
            writeFormField("review_eval", Double.toString(review_eval));
            writeFormField("review_info", review_info);
            writeFormField("write_date", write_date);
            writeFormField("member_id", member_id);
            writeFormField("menu_id", Integer.toString(menu_id));
            writeFormField("store_id", Integer.toString(store_id));
            writeFormField("admin_id", admin_id);
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




    /*************
     **getResponse :Server*
     *************/
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




    /*************
     **Fragment Call back Data*
     *************/
    @Override
    public void DataCallbackHandler(int menu, int rate){
        this.menu_id=menu;
        this.review_eval=rate;

        Fragment review2 = new InsertReviewFragment2();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, review2);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void DataCallbackHandler2(String coment, String picture){
        this.review_info=coment;
        this.review_picture=picture;

        if(review_picture!=null)
            Log.d("Picture", review_picture);
        Log.d("review_eval", Double.toString(review_eval));
        Log.d("review_info", review_info);
        Log.d("write_date", write_date);
        Log.d("member_id", member_id);
        Log.d("menu_id", Integer.toString(menu_id));
        Log.d("store_id", Integer.toString(store_id));
        Log.d("admin_id", admin_id);


        //아래 코드 고쳐야 할부분
        ///////////////////////////////////////////////////////////////////
        ProgressDialog pb = new ProgressDialog(InsertReviewActivity.this);
        pb.setMessage("저장 중 입니다.....");
        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pb.setCanceledOnTouchOutside(false);
        pb.show();

        Thread thread = new Thread() {
            @Override

            public void run() {
                uploadPicture(review_picture);
            }
        };
        thread.start();

    }
}


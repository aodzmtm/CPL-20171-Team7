package com.eatit.restaurant;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import static com.eatit.util.CommonManager.SERVER_ADDR;

public class InsertMenuActivity extends AppCompatActivity {
    static String serviceDomain =  SERVER_ADDR;
    static String postUrl = serviceDomain + "insertMenu.do";
    static String CRLF = "\r\n";
    static String twoHyphens = "--";
    static String boundary = "*****b*o*u*n*d*a*r*y*****";
    private String pictureFileName = null;
    private DataOutputStream dataStream = null;
    enum ReturnCode { noPicture, unknown, http201, http400, http401, http403, http404, http500};
    private String TAG = "멀티파트 테스트";

    
    private final int PICK_FROM_ALBUM = 1;

    private Uri mImageCaptureUri = null;

    
    String  menu_name;
    String menu_price;
    int store_id;
    String  menu_info;
    
    private EditText MenuName;
    private EditText MenuPrice;
    private EditText MenuInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_menu);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0x80331401));


        final Intent i = getIntent();
        this.setTitle("메뉴 등록");


        TextView name = (TextView) findViewById(R.id.Rwrite_letter);
        Typeface Hi = Typeface.createFromAsset(this.getAssets(), "fonts/JejuHallasan.ttf");
        name.setTypeface(Hi);

        
        menu_name=null;
        menu_price=null;
        menu_info=null;
        store_id =Integer.parseInt(i.getStringExtra("store_id"));
        

        MenuName = (EditText) findViewById(R.id.menu_name_edit);
        MenuName.setError("Menu name is required"); 
        
        MenuName.setError(null);

        MenuPrice= (EditText) findViewById(R.id.menu_price_edit);
        MenuPrice.setError("Menu Price is required"); 
       
        MenuPrice.setError(null);

        MenuInfo=(EditText) findViewById(R.id.android_textview_border);
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

            

            writeFormField("menu_name",menu_name );
            writeFormField("menu_price",menu_price );
            writeFormField("menu_info",menu_info );
            writeFormField("store_id", Integer.toString(store_id));

            if( fileInputStream  != null)
                writeFileField("photo", pictureFileName, "image/jpg", fileInputStream,pictureFileName);
            

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
            return sb.toString();   
            
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

    
    public void onNextClick(View v) {
        menu_name = MenuName.getText().toString();
        menu_info = MenuInfo.getText().toString();
        menu_price = Add_comma_to_num(MenuPrice.getText().toString());

        if (menu_name.isEmpty())
            Toast.makeText(InsertMenuActivity.this, "메뉴 이름은 필수입니다.", Toast.LENGTH_SHORT).show();
        if(menu_price.isEmpty())
            Toast.makeText(InsertMenuActivity.this, "메뉴 가격은 필수입니다.", Toast.LENGTH_SHORT).show();
        if(menu_info.isEmpty())
            Toast.makeText(InsertMenuActivity.this, "메뉴 설명은 필수입니다.", Toast.LENGTH_SHORT).show();
        else if(mImageCaptureUri == null) {
            Toast.makeText(InsertMenuActivity.this, "메뉴 사진 첨부는 필수입니다.", Toast.LENGTH_SHORT).show();
        }else{
            //아래 코드 고쳐야 할부분
            ///////////////////////////////////////////////////////////////////
            ProgressDialog pb = new ProgressDialog(InsertMenuActivity.this);
            pb.setMessage("저장 중 입니다.....");
            pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pb.setCanceledOnTouchOutside(false);
            pb.show();


            Thread thread = new Thread() {
                @Override

                public void run() {
                    if (mImageCaptureUri != null)
                        uploadPicture(getPath(mImageCaptureUri));
                    else
                        uploadPicture(null);
                }
            };
            thread.start();
        }
    }
    
    public void CamOnClick(View v) {
        new TedPermission(InsertMenuActivity.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

        Intent intent2 = new Intent(Intent.ACTION_PICK);
        intent2.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent2, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case PICK_FROM_ALBUM:
                mImageCaptureUri = data.getData();
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");
                break;

        }

    }
    
    
    public String getPath(Uri uri) {
        String[] projection = {
                MediaStore.Images.Media.DATA
        };
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(columnIndex);
    }

    
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "이벤트 등록이 취소 됩니다.\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            finish();
        }

    };

    
    String Add_comma_to_num(String str) {
        String result="";

        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator(',');

        DecimalFormat df = new DecimalFormat("###,###,###,###");
        df.setDecimalFormatSymbols(dfs);

        try {
            double inputNum = Double.parseDouble(str);
            result = df.format(inputNum).toString();
        } catch (NumberFormatException e) {
            
        }
        return result;
    }
}

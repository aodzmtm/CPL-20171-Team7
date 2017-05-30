package com.eatit.restaurant;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.eatit.util.LoginSession;

import static com.eatit.util.CommonManager.SERVER_ADDR;

public class InsertStoreActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_iMAGE = 2;

    private Uri mImageCaptureUri;
    private ImageView iv_UserPhoto;
    private int id_view;
    private String absoultePath;

    static String serviceDomain =  SERVER_ADDR;
    static String postUrl = serviceDomain + "insertStore.do";
    static String CRLF = "\r\n";
    static String twoHyphens = "--";
    static String boundary = "*****b*o*u*n*d*a*r*y*****";
    private String pictureFileName = null;
    private DataOutputStream dataStream = null;

    LoginSession loginSession = new LoginSession();//로그인 세션


    private ArrayList<String> SpinnerItem;
    private Spinner spinner;

    @Override
    public void onClick(View v) {
        id_view = v.getId();
        if(v.getId() == R.id.store_image) {
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

    @BindView(R.id.input_storename)    EditText _nameText;
    @BindView(R.id.input_storenum)    EditText _mobileText;
    @BindView(R.id.btn_storesignup)    Button _signupButton;
    @BindView(R.id.input_storeare)      EditText _store_address;
    @BindView(R.id.input_storeInfo)     EditText _store_info;


    //아래 코드 고쳐야 할부분
    ///////////////////////////////////////////////////////////////////
    String store_address;
    String store_name;
    String store_phone;
    String store_info;
    String beacon_id;
    String store_type;


    /////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_store);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0x80331401));

        this.setTitle("가게 등록");
        ButterKnife.bind(this);
        iv_UserPhoto = (ImageView) this.findViewById(R.id.store_image);
        iv_UserPhoto.setOnClickListener(this);
       /* Button btn_agreeJoin = (Button) this.findViewById(R.id.btn_UploadPicture);

        btn_agreeJoin.setOnClickListener(this);*/

        _store_address = (EditText)findViewById(R.id.input_storeare);
        _store_info = (EditText)findViewById(R.id.input_storeInfo);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });



        SpinnerItem = new ArrayList<String>();
        SpinnerItem.add("가게 타입");
        SpinnerItem.add("한식");
        SpinnerItem.add("중식");
        SpinnerItem.add("일식");
        SpinnerItem.add("양식");
        SpinnerItem.add("카페");
        SpinnerItem.add("기타");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spin_item_event, SpinnerItem);

        spinner = (Spinner) findViewById(R.id.Event_Type);
        spinner.setAdapter(adapter);
        spinner.setPrompt("이벤트 타입을 골라주세요");
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?>  parent, View view, int position, long id) {

                int typeNum= position-1;
                store_type = String.valueOf(typeNum);
                System.out.println("store_type+++++++:::::::"+store_type);
            /*    Toast.makeText(MainActivity.this,

                        adspin.getItem(position) + "을 선택 했습니다.", 1).show();
*/
            }

            public void onNothingSelected(AdapterView<?>  parent) {

            }

        });
    }

    private Uri createImageFile(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        Uri uri = Uri.fromFile(new File(storageDir, imageFileName));
        return uri;
    }

    public void doTakePhotoAction()
    {
       /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);*/
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//촬영한 이미지를 저장할 path 생성
        mImageCaptureUri = createImageFile();
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,  mImageCaptureUri);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent , PICK_FROM_CAMERA);
        }


       /* String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);*/
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
                final String[] filePathColumn = {MediaStore.Images.Media.DATA};
                final Cursor imageCursor = this.getContentResolver().query(mImageCaptureUri, filePathColumn,null,null,null);
                imageCursor.moveToFirst();
                final int columnIndex = imageCursor.getColumnIndex(filePathColumn[0]);
                final String photoPath= imageCursor.getString(columnIndex);
                imageCursor.close();

                absoultePath =  photoPath;

                //이미지를 불러올때 고용량의 경우 OutOfMemory가 발생할 수 있으므로
                //이미지 크기를 줄여서 호출함


                Bitmap src = BitmapFactory.decodeFile(photoPath);
                int height = src.getHeight();
                int width = src.getWidth();
                Bitmap resized;
                if(width >1025 || height >1025) {
                    if (GetExifOrientation(photoPath) == 90) {
                        src = Bitmap.createScaledBitmap(src, src.getWidth()/ (src.getWidth() / 1000 + 1), src.getHeight() / (src.getWidth() / 1000 + 1), true);
                        resized = imgRotate(src);
                    } else {
                        resized = Bitmap.createScaledBitmap(src, width / (width / 1000 + 1), height / (width / 1000 + 1), true);
                    }
                }else
                {
                    if (GetExifOrientation(photoPath) == 90) {
                        resized = imgRotate(src);
                    }else {
                        resized = src;
                    }
                }


                try{

                    iv_UserPhoto.setImageBitmap(null);
                    iv_UserPhoto.setImageBitmap(resized );

                }catch (Exception e){
                    e.getStackTrace();
                }
                //이미지 편집 호출
                break;
            }

            case PICK_FROM_CAMERA:
            {
                System.out.println("++++++++++mImageCaptureUri+"+ mImageCaptureUri.getPath());
                String photoPath =  mImageCaptureUri.getPath();
                //이미지를 불러올때 고용량의 경우 OutOfMemory가 발생할 수 있으므로
                //이미지 크기를 줄여서 호출함
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap imageBitmap = BitmapFactory.decodeFile(photoPath,options);
                absoultePath =  photoPath;
                try{
                    // 기본 카메라 모듈을 이용해 촬영할 경우 가끔씩 이미지가
                    // 회전되어 출력되는 경우가 존재하여
                    // 이미지를 상황에 맞게 회전시킨다
                    ExifInterface exif = new ExifInterface(photoPath);
                    int exifOrientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);

                    //회전된 이미지를 다시 회전시켜 정상 출력
                    imageBitmap = rotate(imageBitmap, exifDegree);

                    //회전시킨 이미지를 저장
                    saveExifFile(imageBitmap, photoPath);
                    iv_UserPhoto.setImageBitmap(null);
                    iv_UserPhoto.setImageBitmap(imageBitmap);
                    //비트맵 메모리 반환
                    //imageBitmap.recycle();
                }catch (IOException e){
                    e.getStackTrace();
                }
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

    public void saveExifFile(Bitmap imageBitmap, String savePath){
        FileOutputStream fos = null;
        File saveFile = null;

        try{
            saveFile = new File(savePath);
            fos = new FileOutputStream(saveFile);
            //원본형태를 유지해서 이미지 저장
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

        }catch(FileNotFoundException e){
            //("FileNotFoundException", e.getMessage());
        }catch(IOException e){
            //("IOException", e.getMessage());
        }finally {
            try {
                if(fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
            }
        }
    }




    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90){
            return 90;
        }else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        }else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        }
        return 0;
    }


    public Bitmap rotate(Bitmap bitmap, int degrees){
        Bitmap retBitmap = bitmap;

        if(degrees != 0 && bitmap != null){
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted) {
                    retBitmap = converted;
                    bitmap.recycle();
                    bitmap = null;
                }
            }
            catch(OutOfMemoryError ex) {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return retBitmap;
    }


    public File getImageFile(Uri uri){
        String[] projection = { MediaStore.Images.Media.DATA };

        if(uri == null){
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Cursor mCursor = getContentResolver().query(uri, projection, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc");

        if(mCursor == null || mCursor.getCount() < 1){
            return null;
        }

        int idxColumn = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(idxColumn);

        if(mCursor != null){
            mCursor.close();
            mCursor = null;
        }

        return new File(path);
    }


    public boolean createTempFile(File copyFile, File oriFile) {
        boolean result = true;
        try {
            InputStream inputStream = new FileInputStream(oriFile);
            OutputStream outputStream = new FileOutputStream(copyFile);

            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            result = false;
        } catch (IOException e) {
            result = false;
        }
        return result;
    }



    private void setImageInfo(Uri getIamgePath){
        try {
            ExifInterface exif = new ExifInterface(getIamgePath.getPath());

            String imageSize = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH) + " x " + exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);

           /* File file = new File(mCurrentPhotoPath.getPath());
           */
           /*String imageLength = String.valueOf(file.length()) + " Bytes";*/

            /*tvSize.setText(imageSize);
            tvLength.setText(imageLength);
*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void signup() {
        Log.d(TAG, "StoreSignup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(InsertStoreActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        store_name= _nameText.getText().toString();
        store_phone = _mobileText.getText().toString().substring(0,3)+"-"
                +_mobileText.getText().toString().substring(3,7)+"-"
                +_mobileText.getText().toString().substring(7);
        store_address = _store_address.getText().toString();
        store_info= _store_info.getText().toString();

        beacon_id=loginSession.getInstance().getBeacon_id().toString();



        //아래 코드 고쳐야 할부분
        ///////////////////////////////////////////////////////////////////
        ProgressDialog pb = new ProgressDialog(InsertStoreActivity.this);
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
        String mobile = _mobileText.getText().toString();
        String address = _store_address.getText().toString();
        String information = _store_info.getText().toString();

        if (name.isEmpty() || name.length() < 1) {
            _nameText.setError("한 글자 이상 입력하세요.");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length() > 11) {
            _mobileText.setError("올바른 전화번호를 입력하세요.");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (address.isEmpty()) {
            _store_address.setError("올바른 주소를 입력하세요.");
            valid = false;
        } else {
            _store_address.setError(null);
        }
        if (mobile.isEmpty()) {
            _store_info.setError("가게 정보를 입력하세요.");
            valid = false;
        } else {
            _store_info.setError(null);
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
            System.out.println("store_typeData ="+store_type);

            writeFormField("store_address",store_address);
            writeFormField("store_name",store_name );
            writeFormField("store_phone",store_phone );
            writeFormField("store_info",store_info );
            writeFormField("store_type",store_type);
            writeFormField("beacon_id",beacon_id );


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
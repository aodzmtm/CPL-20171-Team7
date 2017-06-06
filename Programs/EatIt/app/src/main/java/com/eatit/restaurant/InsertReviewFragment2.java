package com.eatit.restaurant;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;

import static android.app.Activity.RESULT_OK;

public class InsertReviewFragment2 extends Fragment implements View.OnClickListener{

    private final int PICK_FROM_CAMERA=0;
    private final int PICK_FROM_ALBUM=1;

    private DataCallbackHandler2 dataCallbackhandler2;
    private Uri mImageCaptureUri=null;

    EditText coment;

    public InsertReviewFragment2() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insert_review_fragment2, null);


        TextView name = (TextView) view.findViewById(R.id.Rwrite_letter2);
        Typeface Hi = Typeface.createFromAsset(getActivity().getAssets(), "fonts/JejuHallasan.ttf");
        name.setTypeface(Hi);

        coment = (EditText) view.findViewById(R.id.android_textview_border);

        view.findViewById(R.id.RWrite_btn2).setOnClickListener(this);

        CircleButton CAM = (CircleButton) view.findViewById(R.id.RWrite_cam);
        CAM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TedPermission(getActivity())
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .check();

                Intent intent2 = new Intent(Intent.ACTION_PICK);
                intent2.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent2, PICK_FROM_ALBUM);
            }
        });

        return view;
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
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(columnIndex);
    }

    @Override
    public void onClick(View view) {
        if(mImageCaptureUri != null)
            dataCallbackhandler2.DataCallbackHandler2(coment.getText().toString(), getPath(mImageCaptureUri).toString());
        else
            dataCallbackhandler2.DataCallbackHandler2(coment.getText().toString(), null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof DataCallbackHandler2) {
            dataCallbackhandler2 = (DataCallbackHandler2) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement Review Write Data CallbackHandler");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dataCallbackhandler2 = null;
    }

    public interface DataCallbackHandler2 {
        void DataCallbackHandler2(String comment, String picture);
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getActivity(), "리뷰 등록이 취소 됩니다.\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            dataCallbackhandler2.DataCallbackHandler2("@", "");
        }

    };
}

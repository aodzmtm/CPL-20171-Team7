package com.eatit.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LoadImagefromUrl extends AsyncTask< Object, Void, Bitmap > {
    ImageView ivPreview = null;

    @Override
    protected Bitmap doInBackground(Object... params ) {
        this.ivPreview = (ImageView) params[0];
        String url = (String) params[1];
        return loadBitmap( url );
    }

    @Override
    protected void onPostExecute( Bitmap result ) {
        super.onPostExecute( result );
        ivPreview.setImageBitmap( result );
    }

    public Bitmap loadBitmap(String url ) {
        URL newurl = null;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.outWidth = 50;
        options.outHeight =50;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 2;
        options.inDither = true;

        try {
            newurl = new URL(url);
            bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream(),null,options);
         /*   Bitmap resized = Bitmap.createScaledBitmap(bitmap,50, 50, true);*/
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
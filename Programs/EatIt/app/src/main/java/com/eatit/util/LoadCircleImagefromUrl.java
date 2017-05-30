package com.eatit.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class LoadCircleImagefromUrl extends AsyncTask< Object, Void, Bitmap > {
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

        return getCircleBitmap(bitmap);
    }
    public Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        final Paint paint = new Paint();

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        final RectF rectF = new RectF(rect);

        final float roundPx = 400;

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;

    }




    public Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output = null;
        try{
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(output);

            final int color = Color.RED;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawOval(rectF, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            bitmap.recycle();


        }catch(Exception e)
        {e.printStackTrace();}

        return output;
    }

}

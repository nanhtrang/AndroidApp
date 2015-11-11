package com.thefour.newsrecommender.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Quang Quang on 8/31/2015.
 */
public class DownloadImageTask extends AsyncTask<String,Void ,Bitmap>
{
    private final String LOG_TAG = DownloadImageTask.class.getSimpleName();
    ImageView mImageView;
    public DownloadImageTask(ImageView pImageView){
        mImageView=pImageView;
    }
    @Override
    protected Bitmap doInBackground(String... params) {
        String urldisplay = params[0];
        Bitmap desImage = null;
        try{
            InputStream inputStream = new URL(urldisplay).openStream();
            desImage = BitmapFactory.decodeStream(inputStream);
        }catch(Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
        return desImage;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        //if(bitmap!=null)
            mImageView.setImageBitmap(bitmap);
    }
}

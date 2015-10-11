package com.thefour.newsrecommender.app;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.thefour.newsrecommender.app.data.NewsContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Quang Quang on 10/11/2015.
 */

//params is the URL to server
public class UpdateCategoriesTask extends AsyncTask<String, Void, Void> {
    private static final String LOG_TAG = UpdateCategoriesTask.class.getSimpleName();
    private Context mContext;

    public UpdateCategoriesTask(Context pContext){
        mContext=pContext;
    }
    @Override
    protected Void doInBackground(String... params) {
        // declare urlConnection and BufferedReader
        // so that they can be closed in the finally block
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //categoriesJson contain raw JSON response as a String
        String categoriesJsonStr = null;

        try{
            //Construct the URL for query
            URL url = new URL(params[0]);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Read the input Stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream==null){
                //Nothing to do
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line=reader.readLine())!=null){
                //for easier for debuging
                buffer.append(line+"\n");
            }
            if(buffer.length()==0){
                return null;
                //stream was empty. no point in parsing
            }
            categoriesJsonStr = buffer.toString();

        }catch (IOException e){
            Log.e(LOG_TAG,"error",e);
            return null;
        }
        finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (final IOException e) {
                Log.e(LOG_TAG, "Error closing stream", e);
            }
        }


        //Now parsing the categoriesJson
        final String OWM_ID="id";
        final String OWM_NAME="name";
        final String OWM_LIST_CATEGORIES = "categories";
        try{
            JSONObject categoriesJson = new JSONObject(categoriesJsonStr);
            JSONArray categoriesArray = categoriesJson.getJSONArray(OWM_LIST_CATEGORIES);
            Vector<ContentValues> cVVector =
                    new Vector<ContentValues>(categoriesArray.length());

            for(int i=0; i<categoriesArray.length();++i){
                int id;
                String name;
                JSONObject categoryObject = categoriesArray.getJSONObject(i);
                id = categoryObject.getInt(OWM_ID);
                name = categoryObject.getString(OWM_NAME);
                ContentValues value = new ContentValues();
                value.put(NewsContract.CategoryEntry._ID,id);
                value.put(NewsContract.CategoryEntry.COLUMN_CATEGORY_NAME,name);
                value.putNull(NewsContract.CategoryEntry.COLUMN_ICON_RESOURCE);
                value.putNull(NewsContract.CategoryEntry.COLUMN_IMAGE_RESOURCE);
                cVVector.add(value);
            }
            if(cVVector.size()!=0){
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                long oldCategoriesDeleted = mContext.getContentResolver()
                        .delete(NewsContract.CategoryEntry.CONTENT_URI,null,null);
                long categoriesInserted = mContext.getContentResolver()
                        .bulkInsert(NewsContract.CategoryEntry.CONTENT_URI,cvArray);
                Log.i(LOG_TAG,"UpdateCategoriesTask() running "+
                                categoriesInserted+" categories inserted "+
                                oldCategoriesDeleted+" old categories delected");
            }
        }catch(JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }
}

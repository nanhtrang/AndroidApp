/* switch to syncAdapter */


//package com.thefour.newsrecommender.app.service;
//
//import android.app.IntentService;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.net.Uri;
//import android.util.Log;
//
//import com.thefour.newsrecommender.app.Main2Activity;
//import com.thefour.newsrecommender.app.data.NewsContract;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Vector;
//
///**
// * Created by Quang Quang on 1/25/2016.
// */
//public class NewsRecommenderService extends IntentService {
//    private static final String LOG_TAG = NewsRecommenderService.class.getSimpleName();
//    public static final String URL_SERVER = "server_url";
//    public NewsRecommenderService() {
//        super("NewsRecommenderService");
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        HttpURLConnection urlConnection = null;
//        BufferedReader reader = null;
//
//        SharedPreferences setting = this.getSharedPreferences(Main2Activity.PREFS_NAME,this.MODE_PRIVATE);
//        int totalNewsInDatabase = setting.getInt(Main2Activity.NEWS_COUNT, 0);
//        String urlString = intent.getStringExtra(URL_SERVER);
//        urlString = urlString.replaceAll("offset=0", "offset=" + Integer.toString(totalNewsInDatabase));
//
//        // Will contain the raw JSON response as a string.
//        String listNewsJsonStr = null;
//        try{
//            final String LISTNEWS_URL = urlString;
//            Uri uri = Uri.parse(LISTNEWS_URL);
//            URL url = new URL(uri.toString());
//            Log.d(LOG_TAG,"SyncService connect server: "+url.toString());
//            urlConnection = (HttpURLConnection)url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            urlConnection.connect();
//
//            //read the input stream into a String
//            InputStream inputStream = urlConnection.getInputStream();
//            StringBuffer buffer = new StringBuffer();
//            if(inputStream==null){
//                //do nothing
//                return ;
//            }
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                // But it does make debugging a *lot* easier if you print out the completed
//                // buffer for debugging.
//                buffer.append(line + "\n");
//            }
//            if (buffer.length() == 0) {
//                // Stream was empty.  No point in parsing.
//                return ;
//            }
//            listNewsJsonStr = buffer.toString();
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "IO Error ", e);
//            // If the code didn't successfully get the news data, there's no point in attempting
//            // to parse it.
//            return ;
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (final IOException e) {
//                    Log.e(LOG_TAG, "Error closing stream", e);
//                }
//            }
//        }
//        //parse Json
//        // news information
//        final String OWN_LIST="news";
//        final String OWN_ID = "id";
//        final String OWN_TITLE = "title";
//        final String OWN_CONTENT_URL = "contenturl";
//        final String OWN_CATEGORY_ID="categoryid";
//        final String OWN_SOURCE_ID="magazineid";
//        final String OWN_DESCRIPTION="description";
//        final String OWN_IMAGE_URL="imageurl";
//        final String OWN_TIME="newstime";
//        final String OWN_RATING = "rating";
//
//        try{
//            JSONObject newsJson = new JSONObject(listNewsJsonStr);
//            JSONArray newsArray = newsJson.getJSONArray(OWN_LIST);
//
//            Vector<ContentValues> cVVector = new Vector<ContentValues>(newsArray.length());
//            for(int i=0 ; i<newsArray.length();++i){
//                int id;
//                String title;
//                String contentUrl;
//                int categoryId;
//                int sourceId;
//                String description;
//                String imageUrl;
//                String time;
//                int rating;
//
//                JSONObject newsObject = newsArray.getJSONObject(i);
//                id = newsObject.getInt(OWN_ID);
//                title = newsObject.getString(OWN_TITLE);
//                contentUrl = newsObject.getString(OWN_CONTENT_URL);
//                categoryId = newsObject.getInt(OWN_CATEGORY_ID);
//                sourceId = newsObject.getInt(OWN_SOURCE_ID);
//                description = newsObject.getString(OWN_DESCRIPTION);
//                imageUrl = newsObject.getString(OWN_IMAGE_URL);
//                time = newsObject.getString(OWN_TIME);
//                rating = newsObject.getInt(OWN_RATING);
//
//                ContentValues value = new ContentValues();
//                value.put(NewsContract.NewsEntry._ID,id);
//                value.put(NewsContract.NewsEntry.COLUMN_TITLE,title);
//                value.put(NewsContract.NewsEntry.COLUMN_CONTENT_URL,contentUrl);
//                value.put(NewsContract.NewsEntry.COLUMN_CATEGORY_ID,categoryId);
//                value.put(NewsContract.NewsEntry.COLUMN_SOURCE_ID,sourceId);
//                value.put(NewsContract.NewsEntry.COLUMN_DESC,description);
//                value.put(NewsContract.NewsEntry.COLUMN_IMAGE_URL,imageUrl);
//                value.put(NewsContract.NewsEntry.COLUMN_TIME,time);
//                value.put(NewsContract.NewsEntry.COLUMN_RATING,rating);
//                value.putNull(NewsContract.NewsEntry.COLUMN_IMAGE_PATH);
//
//                cVVector.add(value);
//            }
//            if(cVVector.size()>0){
//                ContentValues[] cvArray = new ContentValues[cVVector.size()];
//                cVVector.toArray(cvArray);
//                int rowsDeleted = 0;
//                //mContext.getContentResolver().delete(NewsContract.NewsEntry.CONTENT_URI, null, null);
//                Log.d(LOG_TAG,"UpdateListNewsTask() running, deleting old data......\n"+rowsDeleted+" rows deleted");
//                int rowsInserted = this.getContentResolver()
//                        .bulkInsert(NewsContract.NewsEntry.CONTENT_URI,cvArray);
//
//                int totalCount = setting.getInt(Main2Activity.NEWS_COUNT, 0);
//                totalCount+=rowsInserted;
//                setting.edit().putInt(Main2Activity.NEWS_COUNT,totalCount).commit();
//
//                Date now = new Date();
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                Log.d(LOG_TAG,"sync service's running, "+rowsInserted+ " rows inserted");
//                Log.d(LOG_TAG,"total count news after sync: "+totalCount);
//            }
//
//        }catch (JSONException e) {
//            Log.e(LOG_TAG, e.getMessage(), e);
//            e.printStackTrace();
//        }
//        return;
//    }
//
//    static public class AlarmReceiver extends BroadcastReceiver{
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String url = intent.getStringExtra(NewsRecommenderService.URL_SERVER);
//            if(url == null){
//                Log.e(LOG_TAG,"no url data in intent");
//                return ;
//            }
//            Intent sendIntent = new Intent(context,NewsRecommenderService.class);
//            sendIntent.putExtra(NewsRecommenderService.URL_SERVER,url);
//            context.startService(sendIntent);
//        }
//
//        public AlarmReceiver() {
//            super();
//        }
//    }
//}

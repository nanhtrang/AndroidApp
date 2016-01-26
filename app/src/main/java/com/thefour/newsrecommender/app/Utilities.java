package com.thefour.newsrecommender.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.thefour.newsrecommender.app.sync.NewsSyncAdapter;

/**
 * Created by Quang Quang on 10/12/2015.
 */
public class Utilities {
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
//    public static void updateListNews(Context c){
//        String serverNewsUpdateUrl = c.getString(R.string.update_list_news_by_category_id);
//
//        Intent syncNRService = new Intent(c,NewsRecommenderService.AlarmReceiver.class);
//        syncNRService.putExtra(NewsRecommenderService.URL_SERVER,serverNewsUpdateUrl);
//        //c.startService(syncNRService);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(c,101,syncNRService,PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager alarmManager =(AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME,5000,pendingIntent);
//
//
//    }//version using NewsRecommenderService

    public static void updateListNews(Context c){
        NewsSyncAdapter.syncImmediately(c);
    }
}

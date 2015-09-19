package com.thefour.newsrecommender.app.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import com.thefour.newsrecommender.app.data.TestUtilities;
import android.util.Log;

import com.thefour.newsrecommender.app.R;

/**
 * Created by Quang Quang on 8/13/2015.
 */
public class TestProvider extends AndroidTestCase {
    private final String LOG_TAG = TestProvider.class.getSimpleName();
    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the WeatherProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                NewsProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: WeatherProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + NewsContract.CONTENT_AUTHORITY,
                    providerInfo.authority, NewsContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }
    public void testType(){
        String type = mContext.getContentResolver().getType(NewsContract.NewsEntry.CONTENT_URI);
        assertEquals("Error: the NewsEntry CONTENT_URI should return NewsEntry.CONTENT_TYPE", NewsContract.NewsEntry.CONTENT_TYPE,type);

        long categoryId =1;
        type = mContext.getContentResolver().getType(NewsContract.NewsEntry.buildNewsCategoryId(categoryId));
        assertEquals("ERROR: the NewsEntry CONTENT_URI with Category should return NewsEntry.CONTENT_TYPE",NewsContract.NewsEntry.CONTENT_TYPE,type);

        type = mContext.getContentResolver().getType(NewsContract.CategoryEntry.CONTENT_URI);
        assertEquals("SourceEntry CONTENT_URI should return CONTENT_TYPE", NewsContract.CategoryEntry.CONTENT_TYPE,type);

        type = mContext.getContentResolver().getType(NewsContract.NewsSourceEntry.CONTENT_URI);
        assertEquals("NewsSourceEntry.CONTENT_URI should return CONTENT_TYPE", NewsContract.NewsSourceEntry.CONTENT_TYPE,type);


    }
    public void testBasicNewsQuery(){
        NewsDbHelper dbHelper = new NewsDbHelper(getContext());

        Cursor newsCursor = mContext.getContentResolver().query(NewsContract.NewsEntry.CONTENT_URI,
                new String[]{"NEWS._ID", "NEWS." + NewsContract.NewsEntry.COLUMN_TITLE},
                null,
                null,
                null);
        for(int i =0 ;i<newsCursor.getCount();++i){
            newsCursor.moveToPosition(i);
            String debug = "the "+ Integer.toString(i)+ "record _ID: "+newsCursor.getString(0)+" Title: "+newsCursor.getString(1);
        }
        assertTrue("Empty Cursor return on query news", newsCursor.moveToFirst());
        newsCursor.close();

        Cursor categoryCursor = mContext.getContentResolver().query(NewsContract.CategoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
                );
        assertTrue("Empty Cursor return from query category", categoryCursor.moveToFirst());
        assertEquals("Expected 4 records from category query", 4, categoryCursor.getCount());
        categoryCursor.close();



        Cursor newsCursorByCategoryId = mContext.getContentResolver().query(NewsContract.NewsEntry.buildNewsCategoryId(3),
                new String[]{"NEWS._ID","NEWS."+ NewsContract.NewsEntry.COLUMN_TITLE, NewsContract.CategoryEntry.COLUMN_CATEGORY_NAME},
                null,
                null,
                null);
        assertTrue("Empty Cursor return on query News by category Id ", newsCursorByCategoryId.moveToFirst());
        for(int i=0 ;i<newsCursorByCategoryId.getCount();++i){
            newsCursorByCategoryId.moveToPosition(i);
            String debug = "record "+Integer.toString(i)+" : Title: "+ newsCursorByCategoryId.getString(1)+" Category: "+newsCursorByCategoryId.getString(2);
            int x=1+213;
            debug = "adfsa";

        }
        newsCursorByCategoryId.close();

        Cursor sourceCursor = mContext.getContentResolver().query(NewsContract.NewsSourceEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertTrue("Empty Cursor return from source query ",sourceCursor.moveToFirst());
        sourceCursor.close();
//
//        dbHelper.getWritableDatabase().delete("NEWS",null,null);
//        dbHelper.getWritableDatabase().delete(NewsContract.NewsSourceEntry.TABLE_NAME,null,null);
//        dbHelper.getWritableDatabase().delete(NewsContract.CategoryEntry.TABLE_NAME,null,null);
    }
    public void testInsertReadProvider(){
        NewsDbHelper dbHelper = new NewsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        ContentValues tinhteValue = new ContentValues();
        tinhteValue.put(NewsContract.NewsSourceEntry.COLUMN_SOURCE_NAME, "Tinh Tế");
        tinhteValue.put(NewsContract.NewsSourceEntry.COLUMN_LOGO_URL, "http://abc.xyz");
        tinhteValue.putNull(NewsContract.NewsSourceEntry.COLUMN_LOGO_FILE_PATH);

        Uri sourceUri = getContext().getContentResolver().insert(NewsContract.NewsSourceEntry.CONTENT_URI,tinhteValue);
        long sourceRowId = ContentUris.parseId(sourceUri);
        assertTrue(sourceRowId != -1);


        //int rowsDeleted = db.delete(NewsContract.NewsSourceEntry.TABLE_NAME, NewsContract.NewsSourceEntry._ID+"=?",new String[]{Long.toString(sourceRowId)});
        int rowsDeleted = getContext().getContentResolver()
                .delete(NewsContract.NewsSourceEntry.CONTENT_URI,NewsContract.NewsSourceEntry._ID+"=?",new String[]{Long.toString(sourceRowId)});
        assertTrue(rowsDeleted == 1);

        ContentValues categorySport = new ContentValues();
        categorySport.put(NewsContract.CategoryEntry.COLUMN_CATEGORY_NAME,"Thể Thao");
        categorySport.put(NewsContract.CategoryEntry.COLUMN_ICON_RESOURCE,R.drawable.technology_icon);
        categorySport.put(NewsContract.CategoryEntry.COLUMN_IMAGE_RESOURCE,R.drawable.entertainment_image);

        Uri categoryUri = getContext().getContentResolver().insert(NewsContract.CategoryEntry.CONTENT_URI,categorySport);
        long categoryRowId = ContentUris.parseId(categoryUri);
        assertTrue(categoryRowId!=-1);

        rowsDeleted = getContext().getContentResolver()
                .delete(NewsContract.CategoryEntry.CONTENT_URI, NewsContract.CategoryEntry._ID + "=?", new String[]{Long.toString(categoryRowId)});
        assertTrue(rowsDeleted == 1);

        ContentValues news1 = new ContentValues();
        news1.put(NewsContract.NewsEntry.COLUMN_TITLE,"Hy Lạp chính thức được cứu");
        news1.put(NewsContract.NewsEntry.COLUMN_CONTENT_URL, "http://kinhdoanh.vnexpress.net/tin-tuc/quoc-te/hy-lap-chinh-thuc-duoc-cuu-3262290.html");
        news1.put(NewsContract.NewsEntry.COLUMN_CATEGORY_ID, 1);
        news1.put(NewsContract.NewsEntry.COLUMN_SOURCE_ID, 1);
        news1.put(NewsContract.NewsEntry.COLUMN_DESC, "Chính quyền Athens và các chủ nợ vừa đạt thỏa thuận về gói giải cứu cung cấp tới 86 tỷ euro (94 tỷ USD), người phát ngôn Bộ Tài chính Hy Lạp cho biết.");
        news1.put(NewsContract.NewsEntry.COLUMN_IMAGE_URL, "http://c1.f25.img.vnecdn.net/2015/08/11/Hy-Lap-2802-1439279226.jpg");
        news1.putNull(NewsContract.NewsEntry.COLUMN_IMAGE_PATH);
        news1.put(NewsContract.NewsEntry.COLUMN_TIME, "2015-08-11 14:59");
        news1.put(NewsContract.NewsEntry.COLUMN_RATING, 1995);

        Uri newsUri = getContext().getContentResolver().insert(NewsContract.NewsEntry.CONTENT_URI,news1);
        long newsRowId = ContentUris.parseId(newsUri);
        assertTrue(newsRowId!=-1);
        rowsDeleted = getContext().getContentResolver()
                .delete(NewsContract.NewsEntry.CONTENT_URI, NewsContract.NewsEntry._ID+"=?",new String[]{Long.toString(newsRowId)});
        assertTrue(rowsDeleted==1);
        db.close();
    }

    public void testUpdate(){
        NewsDbHelper dbHelper = new NewsDbHelper(mContext);

        ContentValues news1 = new ContentValues();
        news1.put(NewsContract.NewsEntry.COLUMN_TITLE,"Hy Lạp chính thức được cứu");
        news1.put(NewsContract.NewsEntry.COLUMN_CONTENT_URL, "http://kinhdoanh.vnexpress.net/tin-tuc/quoc-te/hy-lap-chinh-thuc-duoc-cuu-3262290.html");
        news1.put(NewsContract.NewsEntry.COLUMN_CATEGORY_ID, 1);
        news1.put(NewsContract.NewsEntry.COLUMN_SOURCE_ID, 1);
        news1.put(NewsContract.NewsEntry.COLUMN_DESC, "Chính quyền Athens và các chủ nợ vừa đạt thỏa thuận về gói giải cứu cung cấp tới 86 tỷ euro (94 tỷ USD), người phát ngôn Bộ Tài chính Hy Lạp cho biết.");
        news1.put(NewsContract.NewsEntry.COLUMN_IMAGE_URL, "http://c1.f25.img.vnecdn.net/2015/08/11/Hy-Lap-2802-1439279226.jpg");
        news1.putNull(NewsContract.NewsEntry.COLUMN_IMAGE_PATH);
        news1.put(NewsContract.NewsEntry.COLUMN_TIME, "2015-08-11 14:59");
        news1.put(NewsContract.NewsEntry.COLUMN_RATING, 1995);

        Uri news1Uri = getContext().getContentResolver().insert(NewsContract.NewsEntry.CONTENT_URI,news1);
        long news1Id = ContentUris.parseId(news1Uri);
        assertTrue(news1Id != -1);

        ContentValues news2 = new ContentValues();
        news2.put(NewsContract.NewsEntry._ID,news1Id);
        news2.put(NewsContract.NewsEntry.COLUMN_TITLE,"Hy Lạp chính thức KHÔNG được cứu");
        news2.put(NewsContract.NewsEntry.COLUMN_CONTENT_URL, "http://kinhdoanh.vnexpress.net/tin-tuc/quoc-te/hy-lap-chinh-thuc-duoc-cuu-3262290.html");
        news2.put(NewsContract.NewsEntry.COLUMN_CATEGORY_ID, 1);
        news2.put(NewsContract.NewsEntry.COLUMN_SOURCE_ID, 1);
        news2.put(NewsContract.NewsEntry.COLUMN_DESC, "Chính quyền Athens và các chủ nợ vừa đạt thỏa thuận về gói giải cứu cung cấp tới 86 tỷ euro (94 tỷ USD), người phát ngôn Bộ Tài chính Hy Lạp cho biết.");
        news2.put(NewsContract.NewsEntry.COLUMN_IMAGE_URL, "http://c1.f25.img.vnecdn.net/2015/08/11/Hy-Lap-2802-1439279226.jpg");
        news2.putNull(NewsContract.NewsEntry.COLUMN_IMAGE_PATH);
        news2.put(NewsContract.NewsEntry.COLUMN_TIME, "2015-08-11 14:59");
        news2.put(NewsContract.NewsEntry.COLUMN_RATING, 1995);

        int rowsEffected = getContext().getContentResolver()
                .update(NewsContract.NewsEntry.CONTENT_URI,news2, NewsContract.NewsEntry._ID+"=?",new String[]{Long.toString(news1Id)});
        assertEquals(1,rowsEffected);
        int rowsDeleted = getContext().getContentResolver().delete(NewsContract.NewsEntry.CONTENT_URI, NewsContract.NewsEntry.COLUMN_TITLE+"=?",new String[]{"Hy Lạp chính thức KHÔNG được cứu"});
        assertTrue(rowsDeleted!=0);

    }
}

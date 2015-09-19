package com.thefour.newsrecommender.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;
import android.util.Log;

import com.thefour.newsrecommender.app.R;

/**
 * Created by Quang Quang on 8/10/2015.
 */
public class TestDb extends AndroidTestCase {
    public static String LOG_TAG = TestDb.class.getSimpleName();

    void deleteDatabase() {
        getContext().deleteDatabase(NewsDbHelper.DATABASE_NAME);
    }

    public void setUp(){
        deleteDatabase();
    }

    public void testCreateDb() throws Throwable{
        mContext.deleteDatabase(NewsDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new NewsDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());


        Cursor categoryQuery = db.rawQuery("SELECT * FROM "+ NewsContract.CategoryEntry.TABLE_NAME,null);


        int count = categoryQuery.getCount();
        assertEquals("Category table has "+Integer.toString(count)+" record(s)",4,count);

        for(int i=0;i<count;++i){
            categoryQuery.moveToPosition(i);
            Log.d(LOG_TAG,"QUANG QUANG DEBUG: Category Name: "+
                    categoryQuery.getString(categoryQuery.getColumnIndex(NewsContract.CategoryEntry.COLUMN_CATEGORY_NAME)));
        }

        //insert fake dât to News Table
        Cursor category = db.rawQuery("SELECT "+ NewsContract.CategoryEntry._ID+","+ NewsContract.CategoryEntry.COLUMN_CATEGORY_NAME+" FROM "+ NewsContract.CategoryEntry.TABLE_NAME,null);
        assertEquals("query category expected 4 records",4,category.getCount());
        final int COLUMN_INDEX_ID = category.getColumnIndex(NewsContract.CategoryEntry._ID);
        final int COLUMN_INDEX_NAME=category.getColumnIndex(NewsContract.CategoryEntry.COLUMN_CATEGORY_NAME);

        int categoryHighlightIndex=-1;
        int categoryEntertainmentIndex=-1;
        int categoryBusinessIndex=-1;
        int categoryTechIndex=-1;
        for(int i=0;i< category.getCount();++i){
            category.moveToPosition(i);
            String categoryName = category.getString(COLUMN_INDEX_NAME);
            switch (categoryName){
                case "Nổi Bật":
                    categoryHighlightIndex= category.getInt(COLUMN_INDEX_ID);
                    break;
                case "Giải Trí":
                    categoryEntertainmentIndex=category.getInt(COLUMN_INDEX_ID);
                    break;
                case "Kinh Doanh":
                    categoryBusinessIndex = category.getInt(COLUMN_INDEX_ID);
                    break;
                case "Công Nghệ":
                    categoryTechIndex=category.getInt(COLUMN_INDEX_ID);
                    break;

            }
        }
        assertNotSame("expected not -1",-1,categoryHighlightIndex);
        assertNotSame("expected not -1", -1, categoryBusinessIndex);
        assertNotSame("expected not -1", -1, categoryEntertainmentIndex);
        assertNotSame("expected not -1",-1,categoryTechIndex);

        Cursor queryNews = db.rawQuery("SELECT * FROM "+ NewsContract.NewsEntry.TABLE_NAME,null);
        assertEquals("number of news record should be 2",3,queryNews.getCount());
        db.close();
    }

}

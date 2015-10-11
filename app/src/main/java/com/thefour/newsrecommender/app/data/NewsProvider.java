package com.thefour.newsrecommender.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.sql.SQLDataException;

/**
 * Created by Quang Quang on 8/12/2015.
 */
public class NewsProvider extends ContentProvider {
    public static final int NEWS = 100;
    public static final int NEWS_WITH_CATEGORY=101;
    //Not need for now
    //private static final int NEWS_WITH_CATEGORY_AND_SOURCE = 102;
    public static final int CATEGORY = 200;
    public static final int SOURCE = 300;

    private static SQLiteQueryBuilder sNewsByCategoryIdAndSourceIdQueryBuilder;
    static{
        sNewsByCategoryIdAndSourceIdQueryBuilder = new SQLiteQueryBuilder();
        sNewsByCategoryIdAndSourceIdQueryBuilder.setTables(
                NewsContract.NewsEntry.TABLE_NAME + " INNER JOIN " +
                        NewsContract.CategoryEntry.TABLE_NAME +
                        " ON (" + NewsContract.NewsEntry.TABLE_NAME + "." + NewsContract.NewsEntry.COLUMN_CATEGORY_ID +
                        " = " + NewsContract.CategoryEntry.TABLE_NAME + "." + NewsContract.CategoryEntry._ID +
                        ") INNER JOIN " + NewsContract.NewsSourceEntry.TABLE_NAME + " ON (" +
                        NewsContract.NewsEntry.TABLE_NAME + "." + NewsContract.NewsEntry.COLUMN_SOURCE_ID + " = " +
                        NewsContract.NewsSourceEntry.TABLE_NAME + "." + NewsContract.NewsSourceEntry._ID+")"
        );

    }

    private NewsDbHelper mOpenHelper;
    private static String sCategoryIdSelection = NewsContract.NewsEntry.TABLE_NAME+"."+
            NewsContract.NewsEntry.COLUMN_CATEGORY_ID+"=?";
    private static String sCategoryNameSelection = NewsContract.NewsSourceEntry.TABLE_NAME+"."+
            NewsContract.NewsSourceEntry.COLUMN_SOURCE_NAME+"=?";


    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = NewsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority,NewsContract.PATH_NEWS,NEWS);
        matcher.addURI(authority,NewsContract.PATH_NEWS+"/*",NEWS_WITH_CATEGORY);
        matcher.addURI(authority,NewsContract.PATH_CATEGORY,CATEGORY);
        matcher.addURI(authority,NewsContract.PATH_NEWS_SOURCE,SOURCE);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new NewsDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int matcher = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (matcher){
            case NEWS:
//                retCursor = mOpenHelper.getReadableDatabase().query(NewsContract.NewsEntry.TABLE_NAME,
//                        projection,selection,selectionArgs,null,null,sortOrder);
                retCursor = sNewsByCategoryIdAndSourceIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case NEWS_WITH_CATEGORY:
                retCursor= getNewsByCategory(uri,projection,sortOrder);
                break;
            case CATEGORY:
                retCursor= mOpenHelper.getReadableDatabase().query(NewsContract.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder) ;
                break;

            case SOURCE:
                retCursor = mOpenHelper.getReadableDatabase().query(NewsContract.NewsSourceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri.toString());
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getNewsByCategory(Uri uri,String[] projection,String sortOrder)
    {
        String [] selectionArgs;
        String selection;
        String categoryId = NewsContract.NewsEntry.getCategoryIdFromUri(uri);
        if(categoryId!=null){
            selectionArgs =new String[]{categoryId};
            selection = sCategoryIdSelection;

        }
        else
        {
            String categoryName = NewsContract.NewsEntry.getCategoryNameFromUri(uri);
            if(categoryName==null) {
                return null;
            }
            selectionArgs =new String[]{categoryName};
            selection = sCategoryNameSelection;
        }
        return sNewsByCategoryIdAndSourceIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);


    };

    @Override
    public String getType(Uri uri) {
        final int matcher = sUriMatcher.match(uri);
        switch (matcher){
            case NEWS:
                return NewsContract.NewsEntry.CONTENT_TYPE;
            case NEWS_WITH_CATEGORY:
                return NewsContract.NewsEntry.CONTENT_TYPE;
            case CATEGORY:
                return NewsContract.CategoryEntry.CONTENT_TYPE;
            case SOURCE:
                return NewsContract.NewsSourceEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri.toString());
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri retUri;
        switch (match){
            case NEWS:{
                long _id = db.insert(NewsContract.NewsEntry.TABLE_NAME,null,values);
                if(_id>0){
                    retUri = NewsContract.NewsEntry.buildNewsUri(_id);
                }else{
                    throw new SQLException("Failed to insert row into "+uri);
                }
                break;
            }
            case CATEGORY:{
                long _id = db.insert(NewsContract.CategoryEntry.TABLE_NAME,null,values);
                if(_id>0){
                    retUri = NewsContract.CategoryEntry.buildCategoryUri(_id);
                }else{
                    throw new SQLException("Failed to insert row into "+uri);
                }
                break;
            }
            case SOURCE:{
                long _id = db.insert(NewsContract.NewsSourceEntry.TABLE_NAME,null,values);
                if(_id>0){
                    retUri = NewsContract.NewsSourceEntry.buildNewsSourceUri(_id);
                }else {
                    throw new SQLException("Failed to insert row into "+uri);
                }
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknows Uri: "+uri);
            }

        }
        getContext().getContentResolver().notifyChange(uri,null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        // Student: Use the uriMatcher to match the WEATHER and LOCATION URI's we are going to
        // handle.  If it doesn't match these, throw an UnsupportedOperationException.
        final int match = sUriMatcher.match(uri);
        int returnRowsDeleted=0;
        if(null== selection){
            selection="1";
        }
        switch (match) {
            case NEWS: {
                returnRowsDeleted = db.delete(NewsContract.NewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case SOURCE: {
                returnRowsDeleted = db.delete(NewsContract.NewsSourceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case CATEGORY: {
                returnRowsDeleted = db.delete(NewsContract.CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("unknown uri: " + uri.toString());
            }
        }
        if(returnRowsDeleted!=0)
            getContext().getContentResolver().notifyChange(uri, null);
        // Student: return the actual rows deleted
        return returnRowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;
        switch (match)
        {
            case NEWS:{
                rowsUpdated = db.update(NewsContract.NewsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case CATEGORY:{
                rowsUpdated = db.update(NewsContract.CategoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case SOURCE:{
                rowsUpdated = db.update(NewsContract.NewsSourceEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown Uri: "+uri.toString());
            }

        }
        if(rowsUpdated!=0)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        db.beginTransaction();
        int returnCount = 0;
        switch (match) {
            case NEWS:
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(NewsContract.NewsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case CATEGORY:
                try{
                    for(ContentValues value:values){
                        long _id = db.insert(NewsContract.CategoryEntry.TABLE_NAME,null,value);
                        if (_id != -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}

package com.thefour.newsrecommender.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Quang Quang on 8/6/2015.
 */
public class NewsContract  {
    private static final String LOG_TAG = NewsContract.class.getSimpleName();
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website. A convenient string to use for the
    // content authority is package name for the app, which is guaranteed to be unique on the
    // device
    public static final String CONTENT_AUTHORITY = "com.thefour.newsrecommender.app";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_NEWS = "news";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_NEWS_SOURCE = "source";



    public static final class NewsEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;

        public static Uri buildNewsCategoryId(long categoryId){
            Uri returnUri = CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).appendQueryParameter(NewsEntry.COLUMN_CATEGORY_ID,Long.toString(categoryId)).build();
            //CONTENT://com.thefour.newsrecommender.app/news/[Category Query]
            Log.d(LOG_TAG,"buildNewsSourceCategory() return Uri: "+returnUri.toString());
            return returnUri ;
        }
        public static Uri buildNewsCategoryName(String categoryName){
            Uri returnUri = CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).appendQueryParameter(CategoryEntry.COLUMN_CATEGORY_NAME,categoryName).build();
            return returnUri;
        }
        public static Uri buildNewsUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }


        public static String getCategoryIdFromUri(Uri uri){
            return uri.getQueryParameter(NewsEntry.COLUMN_CATEGORY_ID);
        }
        public static String getCategoryNameFromUri(Uri uri){
            return uri.getQueryParameter(CategoryEntry.COLUMN_CATEGORY_NAME);
        }

        public static final String TABLE_NAME = "NEWS";
        public static final String COLUMN_CATEGORY_ID = "CategoryId";
        public static final String COLUMN_SOURCE_ID = "NewsSourceId";
        public static final String COLUMN_TITLE = "Title";
        public static final String COLUMN_DESC = "Description";
        public static final String COLUMN_IMAGE_URL = "ImageUrl";
        public static final String COLUMN_IMAGE_PATH = "ImagePath";
        public static final String COLUMN_CONTENT_URL = "ContentUrl";
        public static final String COLUMN_TIME = "DateTime";
        public static final String COLUMN_RATING = "Rating";
    }

    public static class CategoryEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;


        public static final String TABLE_NAME = "CATEGORY";
        public static final String COLUMN_CATEGORY_NAME = "CategoryName";
        public static final String COLUMN_ICON_RESOURCE = "Icon";
        public static final String COLUMN_IMAGE_RESOURCE = "Image";

        public static Uri buildCategoryUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
    }

    public static class NewsSourceEntry implements BaseColumns{
        public static final Uri
                CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEWS_SOURCE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_NEWS_SOURCE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_NEWS_SOURCE;


        public static final String TABLE_NAME = "SOURCE";
        public static final String COLUMN_SOURCE_NAME ="Name";
        public static final String COLUMN_LOGO_URL = "LogoUrl";
        public static final String COLUMN_LOGO_FILE_PATH ="FilePath";

        public static Uri buildNewsSourceUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
    }

}

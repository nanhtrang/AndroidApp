package com.thefour.newsrecommender.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.thefour.newsrecommender.app.R;
import com.thefour.newsrecommender.app.data.NewsContract.NewsEntry;
import com.thefour.newsrecommender.app.data.NewsContract.CategoryEntry;
import com.thefour.newsrecommender.app.data.NewsContract.NewsSourceEntry;

/**
 * Created by Quang Quang on 8/5/2015.
 */
public class NewsDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    Context mContext;
    private final String LOG_TAG = NewsDbHelper.class.getSimpleName();

    static final String DATABASE_NAME = "newsrecommender.db";

    public NewsDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext= context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_NEWS_TABLE = "Create TABLE " +
                NewsEntry.TABLE_NAME + " ("+
                NewsEntry._ID + " INTEGER PRIMARY KEY , "+
                NewsEntry.COLUMN_TITLE + " TEXT NOT NULL, "+
                NewsEntry.COLUMN_CATEGORY_ID + " INTEGER NOT NULL, "+
                NewsEntry.COLUMN_SOURCE_ID + " INTEGER NOT NULL, "+
                NewsEntry.COLUMN_CONTENT_URL+ " TEXT NOT NULL, "+
                NewsEntry.COLUMN_DESC +" TEXT NOT NULL, "+
                NewsEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, "+
                NewsEntry.COLUMN_IMAGE_PATH + " TEXT, "+
                NewsEntry.COLUMN_RATING + " INTEGER NOT NULL, "+
                NewsEntry.COLUMN_TIME + " TEXT NOT NULL, "+
                " FOREIGN KEY (" + NewsEntry.COLUMN_CATEGORY_ID+") REFERENCES "+CategoryEntry.TABLE_NAME+"(" + CategoryEntry._ID + "), "+
                " FOREIGN KEY (" + NewsEntry.COLUMN_SOURCE_ID+ ") REFERENCES "+ NewsSourceEntry.TABLE_NAME+"("+ NewsSourceEntry._ID +")"
                +");";
        Log.d(LOG_TAG, "QUANG QUANG DEBUG: this is Sqlcommand creating News Table: " + SQL_CREATE_NEWS_TABLE);


        final String SQL_CREATE_CATEGORY_TABLE = "Create TABLE "+ CategoryEntry.TABLE_NAME + "("+
                CategoryEntry._ID + " INTEGER PRIMARY KEY , "+
                CategoryEntry.COLUMN_CATEGORY_NAME + " TEXT NOT NULL, "+
                CategoryEntry.COLUMN_ICON_RESOURCE + " INTEGER, "+
                CategoryEntry.COLUMN_IMAGE_RESOURCE+ " INTEGER, " +
                " UNIQUE (" + CategoryEntry.COLUMN_CATEGORY_NAME  +
                 ") ON CONFLICT REPLACE" +
                ");";
        Log.d(LOG_TAG, "QUANG QUANG DEBUG: this is Sqlcommand to create Category table: " + SQL_CREATE_CATEGORY_TABLE);

        final String SQL_CREATE_SOURCE_TABLE = "Create TABLE "+ NewsSourceEntry.TABLE_NAME + "("+
                NewsSourceEntry._ID + " INTEGER PRIMARY KEY , "+
                NewsSourceEntry.COLUMN_SOURCE_NAME + " TEXT, "+
                NewsSourceEntry.COLUMN_LOGO_URL + " TEXT, "+
                NewsSourceEntry.COLUMN_LOGO_FILE_PATH + " TEXT, "+
                " UNIQUE ("+NewsSourceEntry.COLUMN_SOURCE_NAME+") ON CONFLICT REPLACE"+
                ");";
        Log.d(LOG_TAG, "QUANG QUANG DEBUG: Sqlcommand to create News Source Table: " + SQL_CREATE_SOURCE_TABLE);
        db.execSQL(SQL_CREATE_NEWS_TABLE);
        db.execSQL(SQL_CREATE_CATEGORY_TABLE);
        db.execSQL(SQL_CREATE_SOURCE_TABLE);

        ContentValues contentValues = new ContentValues();
        contentValues.put(CategoryEntry._ID,"01");
        contentValues.put(CategoryEntry.COLUMN_CATEGORY_NAME, mContext.getString(R.string.category_highlight));
        contentValues.put(CategoryEntry.COLUMN_ICON_RESOURCE,R.drawable.news_icon);
        contentValues.put(CategoryEntry.COLUMN_IMAGE_RESOURCE, R.drawable.news_image);

        //long insertCategory = db.insert(CategoryEntry.TABLE_NAME, null, contentValues);

        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(CategoryEntry._ID,"02");
        contentValues1.put(CategoryEntry.COLUMN_CATEGORY_NAME,mContext.getString(R.string.category_business));
        contentValues1.put(CategoryEntry.COLUMN_ICON_RESOURCE,R.drawable.business_icon);
        contentValues1.put(CategoryEntry.COLUMN_IMAGE_RESOURCE, R.drawable.business_image);

        //db.insert(CategoryEntry.TABLE_NAME, null, contentValues1);

        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(CategoryEntry._ID,"03");
        contentValues2.put(CategoryEntry.COLUMN_CATEGORY_NAME,mContext.getString(R.string.category_entertainment));
        contentValues2.put(CategoryEntry.COLUMN_IMAGE_RESOURCE,R.drawable.entertainment_image);
        contentValues2.put(CategoryEntry.COLUMN_ICON_RESOURCE, R.drawable.entertainment_icon);

        //db.insert(CategoryEntry.TABLE_NAME, null, contentValues2);

        ContentValues contentValues3 = new ContentValues();
        contentValues3.put(CategoryEntry._ID,"04");
        contentValues3.put(CategoryEntry.COLUMN_CATEGORY_NAME,mContext.getString(R.string.category_technology));
        contentValues3.put(CategoryEntry.COLUMN_ICON_RESOURCE,R.drawable.technology_icon);
        contentValues3.put(CategoryEntry.COLUMN_IMAGE_RESOURCE, R.drawable.technology_image);

        //db.insert(CategoryEntry.TABLE_NAME,null,contentValues3);

        //insert fake data to News source table

        ContentValues source1 = new ContentValues();
        source1.put(NewsSourceEntry._ID,"01");
        source1.put(NewsSourceEntry.COLUMN_SOURCE_NAME , "VnExpress");
        source1.put(NewsSourceEntry.COLUMN_LOGO_URL, "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-xpa1/v/t1.0-1/p56x56/1377372_612208245488345_284676873_n.jpg?oh=cafb3c8b5ca51051adfa018ecacf37c8&oe=5649AEA0&__gda__=1447184298_ae81baa3e86096898d5c90c86ec5c400");
        source1.putNull(NewsSourceEntry.COLUMN_LOGO_FILE_PATH);

        db.insert(NewsSourceEntry.TABLE_NAME, null, source1);

        ContentValues source2 = new ContentValues();
        source2.put(NewsSourceEntry._ID,"02");
        source2.put(NewsSourceEntry.COLUMN_SOURCE_NAME,"Thanh Niên");
        source2.put(NewsSourceEntry.COLUMN_LOGO_URL, "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-xft1/v/t1.0-1/10442564_10152486130851251_1078779416261664405_n.jpg?oh=97d3306d10d180e51b24cfa721517e4e&oe=564DC3CB&__gda__=1447647326_f8d24fd50384e9f3a7c20141ccf3477c");
        source2.putNull(NewsSourceEntry.COLUMN_LOGO_FILE_PATH);

        db.insert(NewsSourceEntry.TABLE_NAME, null, source2);


        //this code for initialize CategoryId
        Cursor category = db.query(CategoryEntry.TABLE_NAME,new String[]{CategoryEntry._ID,CategoryEntry.COLUMN_CATEGORY_NAME},null,null,null,null,null);
        final int COLUMN_INDEX_ID = 0;
        final int COLUMN_INDEX_NAME=1;

        int categoryHighlightId=1;
        int categoryEntertainmentId=3;
        int categoryBusinessId=2;
        int categoryTechId=4;

        //this code for initialize Source ID
        int newsSourceVnExpressId = 1;
        int newsSourceThanhNienId = 2;

        // start insert some fake date news
        ContentValues news1 = new ContentValues();
        news1.put(NewsEntry._ID,"1");
        news1.put(NewsEntry.COLUMN_TITLE,"Hy Lạp chính thức được cứu");
        news1.put(NewsEntry.COLUMN_CONTENT_URL,"http://kinhdoanh.vnexpress.net/tin-tuc/quoc-te/hy-lap-chinh-thuc-duoc-cuu-3262290.html");
        news1.put(NewsEntry.COLUMN_CATEGORY_ID,categoryBusinessId);
        news1.put(NewsEntry.COLUMN_SOURCE_ID, newsSourceVnExpressId);
        news1.put(NewsEntry.COLUMN_DESC, "Chính quyền Athens và các chủ nợ vừa đạt thỏa thuận về gói giải cứu cung cấp tới 86 tỷ euro (94 tỷ USD), người phát ngôn Bộ Tài chính Hy Lạp cho biết.");
        news1.put(NewsEntry.COLUMN_IMAGE_URL, "http://c1.f25.img.vnecdn.net/2015/08/11/Hy-Lap-2802-1439279226.jpg");
        news1.putNull(NewsEntry.COLUMN_IMAGE_PATH);
        news1.put(NewsEntry.COLUMN_TIME, "2015-08-11 14:59");
        news1.put(NewsEntry.COLUMN_RATING, 1995);

        //long index = db.insert(NewsEntry.TABLE_NAME, null, news1);

        ContentValues news2 = new ContentValues();
        news2.put(NewsEntry._ID,2);
        news2.put(NewsEntry.COLUMN_TITLE,"Máy bay không người lái có thể bị bắn hạ bởi… âm thanh");
        news2.put(NewsEntry.COLUMN_CONTENT_URL,"http://www.thanhnien.com.vn/cong-nghe-thong-tin/may-bay-khong-nguoi-lai-co-the-bi-ban-ha-boi-am-thanh-595700.html");
        news2.put(NewsEntry.COLUMN_CATEGORY_ID,categoryTechId);
        news2.put(NewsEntry.COLUMN_SOURCE_ID,newsSourceThanhNienId);
        news2.put(NewsEntry.COLUMN_DESC,"(TNO) Máy bay không người lái (UAV) từ lâu là vũ khí vô cùng nguy hiểm với khả năng do thám quân sự và tấn công chớp nhoáng nhưng rất khó phát hiện. Tuy nhiên, nghiên cứu mới của các nhà khoa học Hàn Quốc cho biết sóng âm có khả năng tấn công UAV, theo Daily News New York (Mỹ) ngày 10.8");
        news2.put(NewsEntry.COLUMN_IMAGE_URL,"http://static.thanhnien.com.vn/uploaded/hoanguy/2015_08_11/drone_kwyn.jpg?width=500");
        news2.putNull(NewsEntry.COLUMN_IMAGE_PATH);
        news2.put(NewsEntry.COLUMN_TIME, "2015-08-11 15:25");
        news2.put(NewsEntry.COLUMN_RATING,500);

        //long index1 = db.insert(NewsEntry.TABLE_NAME,null,news2);

        ContentValues news3 = new ContentValues();
        news3.put(NewsEntry._ID,3);
        news3.put(NewsEntry.COLUMN_TITLE,"Bộ tứ siêu đẳng - bom xịt của mùa phim hè 2015");
        news3.put(NewsEntry.COLUMN_CONTENT_URL,"http://giaitri.vnexpress.net/tin-tuc/phim/diem-phim/bo-tu-sieu-dang-bom-xit-cua-mua-phim-he-2015-3260989.html");
        news3.put(NewsEntry.COLUMN_CATEGORY_ID,categoryEntertainmentId);
        news3.put(NewsEntry.COLUMN_SOURCE_ID,newsSourceVnExpressId);
        news3.put(NewsEntry.COLUMN_DESC,"Kịch bản dài dòng và tẻ nhạt, kỹ xảo sơ sài, các pha hành động ít ỏi, diễn viên không thu hút bằng dàn sao cũ khiến phim siêu anh hùng mới nhất dựa trên truyện tranh Marvel gây thất vọng.");
        news3.put(NewsEntry.COLUMN_IMAGE_URL, "http://c1.f9.img.vnecdn.net/2015/08/10/604383ac-6bb3-4649-a500-0d5-6118-1439185082.jpg");
        news3.putNull(NewsEntry.COLUMN_IMAGE_PATH);
        news3.put(NewsEntry.COLUMN_TIME, "2015-08-11 9:15");
        news3.put(NewsEntry.COLUMN_RATING,300);
        //long index2 = db.insert(NewsEntry.TABLE_NAME,null,news3);


//        db.insert(NewsEntry.TABLE_NAME,null,news3);
//        db.insert(NewsEntry.TABLE_NAME,null,news2);
//        db.insert(NewsEntry.TABLE_NAME,null,news1);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        db.execSQL("DROP TABLE IF EXISTS "+NewsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+CategoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+NewsSourceEntry.TABLE_NAME);
        onCreate(db);
    }
}

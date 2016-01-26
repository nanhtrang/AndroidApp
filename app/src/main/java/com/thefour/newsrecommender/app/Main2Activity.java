package com.thefour.newsrecommender.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.thefour.newsrecommender.app.data.NewsContract;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Main2Activity extends AppCompatActivity {

    private static final int REQUEST_CATEGORIES_CHANGE_CODE = 100;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ImageView mImageHeader;
    private ImageView mImageIconHeader;
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String INTERESTING_CATEGORY = "interestingCategory";
    public static final String IS_FIRST_RUN = "isFirstRun";
    public static final String NEWS_COUNT = "total_news_in_database";
    Cursor mFullCategory;

    String[] mCategoryIdArray;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences setting = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> userSelectedCategory = new HashSet<String>();
        userSelectedCategory = setting.getStringSet(INTERESTING_CATEGORY, null);
        //in case user select later button at the first application run

        if (userSelectedCategory != null) {
            mCategoryIdArray = new String[userSelectedCategory.size()];
            mCategoryIdArray = (String[]) userSelectedCategory.toArray(mCategoryIdArray);
        }
        mFullCategory = getContentResolver().query(NewsContract.CategoryEntry.CONTENT_URI,
                new String[]{NewsContract.CategoryEntry._ID, NewsContract.CategoryEntry.COLUMN_CATEGORY_NAME},
                null,
                null,
                null);

        if (setting.getBoolean(IS_FIRST_RUN, true))
        //if(true)
        {
            setting.edit().putBoolean(IS_FIRST_RUN, false).commit();
            //getSupportFragmentManager().beginTransaction().add(R.id.main_container, new CategoryPickerFragment()).commit();
            Intent intent = new Intent(getBaseContext(), ChangeCategoryActivity.class);
            finish();
            startActivity(intent);
        }

        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.htab_collapse_toolbar);

        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.appbar);

        FragmentManager fm = getSupportFragmentManager();
        mSectionsPagerAdapter = new SectionsPagerAdapter(fm);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.view_pager_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mImageHeader = (ImageView)findViewById(R.id.image_header);
        mImageIconHeader = (ImageView)findViewById(R.id.image_icon_header);
        mImageIconHeader.setVisibility(View.INVISIBLE);
        setHeaderImage(0);
        //mImageHeader.setColorFilter(Color.argb(100,250,200,100));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabBar);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Toast.makeText(getApplicationContext(),mSectionsPagerAdapter.getPageTitle(position),Toast.LENGTH_SHORT).show();
                setHeaderImage(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CATEGORIES_CHANGE_CODE) {
            recreate();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_change_categories) {
            //getSupportFragmentManager().beginTransaction().replace(R.id.pager,new CategoryPickerFragment()).commit();
            Intent intent = new Intent();
            intent.setClass(this, ChangeCategoryActivity.class);
            intent.putExtra(CategoryPickerFragment.CHANGE_CATEGORIES, true);
            startActivityForResult(intent, REQUEST_CATEGORIES_CHANGE_CODE);
            return true;
        }
        if (id == R.id.action_update) {
//            UpdateCategoriesTask updateCategoriesTask = new UpdateCategoriesTask(this);
//            String serverNewsUpdateUrl = getString(R.string.update_list_news_by_category_id);
//            String serverCategoriesUpdateUrl = getString(R.string.update_categories_url);
//            //updateCategoriesTask.execute("http://10.0.2.2:8080/RankedListNews/categories");
//            //updateCategoriesTask.execute(serverCategoriesUpdateUrl);
//            //UpdateListNewsTask updateTask = new UpdateListNewsTask(this);
//            //updateTask.execute("http://10.0.2.2:8080/RankedListNews/toprankedlistnews?offset=0&limit=75");
//            //updateTask.execute(serverNewsUpdateUrl);
////            updateTask.execute("http://10.0.2.2:8080/RankedListNews/toprankedlistnews?offset=100&limit=100");
////            updateTask.execute("http://10.0.2.2:8080/RankedListNews/toprankedlistnews?offset=200&limit=100");
//
//            Intent syncNRService = new Intent(this,NewsRecommenderService.class);
//            syncNRService.putExtra(NewsRecommenderService.URL_SERVER,serverNewsUpdateUrl);
//            startService(syncNRService);
            Utilities.updateListNews(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main2 Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.thefour.newsrecommender.app/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main2 Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.thefour.newsrecommender.app/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


//    public class StatePagerAdapter extends FragmentStatePagerAdapter {
//
//        public StatePagerAdapter(FragmentManager fm) {
//
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            // getItem is called to instantiate the fragment for the given page.
//            // Return a PlaceholderFragment (defined as a static inner class below).
//            //return ListNewsFragment.newInstance(position + 1);
//            if (position == 0) {
//                return ListNewsFragment.newInstance(0, 0);//special case, return highlight
//            }
//            //---this is original values before add the highlight tab at the first section
//            // section number start at 1, but the mCategoryIdArray start at 0.
//            //return ListNewsFragment.newInstance(position+1,Integer.parseInt(mCategoryIdArray[position]));
//            //----ending backup code the new code for adding highlight tab below
//            return ListNewsFragment.newInstance(position, Integer.parseInt(mCategoryIdArray[position - 1]));
//        }
//
//        @Override
//        public int getCount() {
//            SharedPreferences setting = getSharedPreferences(Main2Activity.PREFS_NAME, MODE_PRIVATE);
//            Set<String> userSelectedCategory = new HashSet<String>();
//            userSelectedCategory = setting.getStringSet(INTERESTING_CATEGORY, null);
//            if (userSelectedCategory == null) {
//                return 0;
//            }
//            return userSelectedCategory.size() + 1;//+1 for the Hightlight tab which is always there
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            Locale l = Locale.getDefault();
//            if (position == 0)
//                return getString(R.string.news_tab_hightlight);
//            int categoryId = Integer.parseInt(mCategoryIdArray[position - 1]);//because the first position is always highlight tab
//            for (int i = 0; i < mFullCategory.getCount(); ++i) {
//                mFullCategory.moveToPosition(i);
//                if (mFullCategory.getInt(0) == categoryId) {
//                    return mFullCategory.getString(1);
//                }
//            }
//            //0 is category _ID, 1 is COLUMN_CATEGORY_NAME;
//            return "categoryId " + Integer.toString(categoryId) + " not found";
//        }
//    }
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return ListNewsFragment.newInstance(position + 1);
            if (position == 0) {
                return ListNewsFragment.newInstance(0, 0);//special case, return highlight
            }
            //---this is original values before add the highlight tab at the first section
            // section number start at 1, but the mCategoryIdArray start at 0.
            //return ListNewsFragment.newInstance(position+1,Integer.parseInt(mCategoryIdArray[position]));
            //----ending backup code the new code for adding highlight tab below
            return ListNewsFragment.newInstance(position, Integer.parseInt(mCategoryIdArray[position - 1]));
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            //return 3;
            SharedPreferences setting = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
            Set<String> userSelectedCategory = new HashSet<String>();
            userSelectedCategory = setting.getStringSet(INTERESTING_CATEGORY, null);
            if (userSelectedCategory == null) {
                return 0;
            }
            return userSelectedCategory.size() + 1;//+1 for the Hightlight tab which is always there

        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            if (position == 0)
                return getString(R.string.news_tab_hightlight);
            int categoryId = Integer.parseInt(mCategoryIdArray[position - 1]);//because the first position is always highlight tab
            for (int i = 0; i < mFullCategory.getCount(); ++i) {
                mFullCategory.moveToPosition(i);
                if (mFullCategory.getInt(0) == categoryId) {
                    return mFullCategory.getString(1);
                }
            }
            //0 is category _ID, 1 is COLUMN_CATEGORY_NAME;
            return "categoryId " + Integer.toString(categoryId) + " not found";

        }
    }

    void setHeaderImage(int position){

        String title = mSectionsPagerAdapter.getPageTitle(position).toString();
//        Drawable drawable = null;
//
//        Context context = getApplicationContext();
//        ImageView headerView = mImageHeader;
//        if (title.equals(getResources().getString(R.string.category_highlight)))  {
//            drawable= ContextCompat.getDrawable(context, R.drawable.news_image);
//        } else if (title.equals(getResources().getString(R.string.category_business)))
//        {
//            drawable=ContextCompat.getDrawable(context, R.drawable.business_resized);
//
//        } else if (title.equals(getResources().getString(R.string.category_education))) {
//            drawable = ContextCompat.getDrawable(context,R.drawable.books);
//        } else if (title.equals(getResources().getString(R.string.category_entertainment))) {
//            drawable = ContextCompat.getDrawable(context,R.drawable.entertainment);
//        } else if (title.equals(getResources().getString(R.string.category_sport))) {
//            drawable = ContextCompat.getDrawable(context,R.drawable.sports);
//        } else if (title.equals(getResources().getString(R.string.category_topical))) {
//            drawable = ContextCompat.getDrawable(context,R.drawable.news2);
//        } else if (title.equals(getResources().getString(R.string.category_global))) {
//            drawable = ContextCompat.getDrawable(context,R.drawable.global);
//        } else if (title.equals(getResources().getString(R.string.category_science_technology))) {
//            drawable = ContextCompat.getDrawable(context,R.drawable.science);
//        }
//        headerView.setImageDrawable(drawable);

        Context context = getApplicationContext();
        ImageView headerView = mImageHeader;
        ImageView iconHeaderView = mImageIconHeader;
        if (title.equals(getResources().getString(R.string.category_highlight)))  {
            Glide.with(getApplicationContext()).load(R.drawable.news2).fitCenter().into(headerView);
            //Glide.with(getApplicationContext()).load(R.drawable.news_icon_red).fitCenter().into(iconHeaderView);
        } else if (title.equals(getResources().getString(R.string.category_business)))
        {
            Glide.with(getApplicationContext()).load(R.drawable.business_resized).fitCenter().into(headerView);
           // Glide.with(getApplicationContext()).load(R.drawable.business_stock_icon).fitCenter().into(iconHeaderView);

        } else if (title.equals(getResources().getString(R.string.category_education))) {
            Glide.with(getApplicationContext()).load(R.drawable.books).fitCenter().into(headerView);
           // Glide.with(getApplicationContext()).load(R.drawable.eductation_green_icon).fitCenter().into(iconHeaderView);
        } else if (title.equals(getResources().getString(R.string.category_entertainment))) {
            Glide.with(getApplicationContext()).load(R.drawable.entertainment).fitCenter().into(headerView);
            //Glide.with(getApplicationContext()).load(R.drawable.entertainment_blue_music_icon).fitCenter().into(iconHeaderView);
        } else if (title.equals(getResources().getString(R.string.category_sport))) {
            Glide.with(getApplicationContext()).load(R.drawable.sports).fitCenter().into(headerView);
            //Glide.with(getApplicationContext()).load(R.drawable.sports_cup_icon).fitCenter().into(iconHeaderView);
        } else if (title.equals(getResources().getString(R.string.category_topical))) {
            Glide.with(getApplicationContext()).load(R.drawable.news_image).fitCenter().into(headerView);
           // Glide.with(getApplicationContext()).load(R.drawable.news_green_icon).fitCenter().into(iconHeaderView);
        } else if (title.equals(getResources().getString(R.string.category_global))) {
            Glide.with(getApplicationContext()).load(R.drawable.global).fitCenter().into(headerView);
            //Glide.with(getApplicationContext()).load(R.drawable.global_icon).fitCenter().into(iconHeaderView);
        } else if (title.equals(getResources().getString(R.string.category_science_technology))) {
            Glide.with(getApplicationContext()).load(R.drawable.science).fitCenter().into(headerView);
            //Glide.with(getApplicationContext()).load(R.drawable.science_icon).fitCenter().into(iconHeaderView);
        }

    }
}

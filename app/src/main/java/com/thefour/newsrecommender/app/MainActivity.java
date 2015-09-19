package com.thefour.newsrecommender.app;


import java.util.HashSet;

import java.util.Locale;
import java.util.Set;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import android.view.Menu;
import android.view.MenuItem;

import com.thefour.newsrecommender.app.data.NewsContract;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    private static final int REQUEST_CATEGORIES_CHANGE_CODE = 100;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    Cursor mFullCategory;
    String[] mCategoryIdArray;

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String INTERESTING_CATEGORY = "interestingCategory";
    public static final String IS_FIRST_RUN = "isFirstRun";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences setting = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        Set<String> userSelectedCategory = new HashSet<String>();
        userSelectedCategory = setting.getStringSet(INTERESTING_CATEGORY,null);
        //in case user select later button at the first application run

        if(userSelectedCategory!=null) {
            mCategoryIdArray = new String[userSelectedCategory.size()];
            mCategoryIdArray = (String[]) userSelectedCategory.toArray(mCategoryIdArray);
        }


        mFullCategory = getContentResolver().query(NewsContract.CategoryEntry.CONTENT_URI,
                new String[]{NewsContract.CategoryEntry._ID, NewsContract.CategoryEntry.COLUMN_CATEGORY_NAME},
                null,
                null,
                null);

        setContentView(R.layout.activity_main);
        if(setting.getBoolean(IS_FIRST_RUN,true))
        //if(true)
        {
            setting.edit().putBoolean(IS_FIRST_RUN,false).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.main_container,new CategoryPickerFragment()).commit();
        }else{
            // Set up the action bar.
            setContentView(R.layout.fragment_view_pager);
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            // When swiping between different sections, select the corresponding
            // tab. We can also use ActionBar.Tab#select() to do this if we have
            // a reference to the Tab.
            mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    actionBar.setSelectedNavigationItem(position);

                }
            });

            // For each of the sections in the app, add a tab to the action bar.
            for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
                // Create a tab with text corresponding to the page title defined by
                // the adapter. Also specify this Activity object, which implements
                // the TabListener interface, as the callback (listener) for when
                // this tab is selected.
                actionBar.addTab(
                        actionBar.newTab()
                                .setText(mSectionsPagerAdapter.getPageTitle(i))
                                .setTabListener(this));
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if(id==R.id.action_change_categories){
            //getSupportFragmentManager().beginTransaction().replace(R.id.pager,new CategoryPickerFragment()).commit();
            Intent intent = new Intent();
            intent.setClass(this,ChangeCategoryActivity.class);
            intent.putExtra(CategoryPickerFragment.CHANGE_CATEGORIES,true);
            startActivityForResult(intent, REQUEST_CATEGORIES_CHANGE_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CATEGORIES_CHANGE_CODE){
            this.recreate();
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return ListNewsFragment.newInstance(position + 1);

            return ListNewsFragment.newInstance(position+1,Integer.parseInt(mCategoryIdArray[position]));
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            //return 3;
            SharedPreferences setting = getSharedPreferences(MainActivity.PREFS_NAME,MODE_PRIVATE);
            Set<String> userSelectedCategory = new HashSet<String>();
              userSelectedCategory = setting.getStringSet(INTERESTING_CATEGORY,null);

            return userSelectedCategory.size();

        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();

            int categoryId = Integer.parseInt(mCategoryIdArray[position]);
            for(int i=0;i< mFullCategory.getCount();++i){
                mFullCategory.moveToPosition(i);
                if(mFullCategory.getInt(0)==categoryId){
                    return mFullCategory.getString(1);
                }
            }
            //0 is category _ID, 1 is COLUMN_CATEGORY_NAME;
            return "categoryId "+Integer.toString(categoryId)+" not found";


//            switch (position) {
//                case 0: {
//                    userSelectedCategory.
//                    return getString(R.string.title_section1).toUpperCase(l);
//                }
//                case 1: {
//                    return getString(R.string.title_section2).toUpperCase(l);
//                }
//                case 2:
//                    return getString(R.string.title_section3).toUpperCase(l);
//            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }


    }
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static android.app.Fragment newInstance(int sectionNumber) {
//            android.app.Fragment fragment = new ListNewsFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        public PlaceholderFragment() {
//        }
//
//
//    }

}

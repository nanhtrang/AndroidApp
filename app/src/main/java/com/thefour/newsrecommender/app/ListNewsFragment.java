package com.thefour.newsrecommender.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.thefour.newsrecommender.app.data.NewsContract.NewsEntry;
import com.thefour.newsrecommender.app.data.NewsContract.CategoryEntry;
import com.thefour.newsrecommender.app.data.NewsContract.NewsSourceEntry;

import com.thefour.newsrecommender.app.data.NewsContract;

/**
 * Created by Quang Quang on 8/5/2015.
 */
public class ListNewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int NEWS_LOADER=0;

    private static final String[] NEWS_COLUMN = {
            NewsContract.NewsEntry.TABLE_NAME+"."+ NewsContract.NewsEntry._ID,
            NewsContract.NewsEntry.COLUMN_TITLE,
            NewsEntry.COLUMN_CATEGORY_ID,
            NewsEntry.COLUMN_SOURCE_ID,
            NewsEntry.COLUMN_DESC,
            NewsEntry.COLUMN_CONTENT_URL,
            NewsEntry.COLUMN_IMAGE_URL,
            NewsEntry.COLUMN_IMAGE_PATH,
            NewsEntry.COLUMN_TIME,
            NewsEntry.COLUMN_RATING,
            NewsSourceEntry.COLUMN_SOURCE_NAME,
            NewsSourceEntry.COLUMN_LOGO_FILE_PATH
    };

    static final int COL_NEWS_ID=0;
    static final int COL_TITLE=1;
    static final int COL_CATEGORY_ID=2;
    static final int COL_SOURCE_ID=3;
    static final int COL_DESC=4;
    static final int COL_CONTENT_URI = 5;
    static final int COL_IMAGE_URL =6;
    static final int COL_IMAGE_PATH =7;
    static final int COL_TIME=8;
    static final int COL_RATING =9;
    static final int COL_SOURCE_NAME=10;
    static final int COL_LOGO_FILE_PATH =11;
    private static final String LOG_TAG = ListNewsFragment.class.getSimpleName();

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_CATEGORY_ID = "section_category_id";

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerCursorAdapter mAdapter;
    private int mPosition;
    int mCategoryId;
    private boolean mLoadingMore;
    private int mTotalNewsInDatabase;
    public ListNewsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ListNewsFragment newInstance(int sectionNumber, int categoryId) {
        ListNewsFragment fragment = new ListNewsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putInt(ARG_CATEGORY_ID,categoryId);
        fragment.setArguments(args);
        Log.d(LOG_TAG,"news ListNewsFragment "+sectionNumber+" "+"categoryID: "+categoryId);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(NEWS_LOADER,null,this);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategoryId = this.getArguments().getInt(ARG_CATEGORY_ID);
        SharedPreferences setting = getActivity().getPreferences(getActivity().MODE_PRIVATE);
        mTotalNewsInDatabase = setting.getInt(Main2Activity.NEWS_COUNT,0);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int categoryId = getArguments().getInt(ARG_CATEGORY_ID);
        Uri newsUri;
        if(categoryId==0)//speacial category this is highlight section
        {
            newsUri = NewsEntry.CONTENT_URI;
        }else {
            newsUri = NewsEntry.buildNewsCategoryId(categoryId);
        }
        //Uri newsUri = NewsEntry.CONTENT_URI;
        String sortOrder = NewsEntry.COLUMN_RATING+" DESC";
        Log.i(LOG_TAG,"News in category Uri: "+newsUri);

        return new CursorLoader(getActivity(),newsUri,NEWS_COLUMN,null, null,sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG,"onLoadFinished(): "+data.getCount()+" records loaded");


        mAdapter.swapCursor(data);
        mLoadingMore=false;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        mLoadingMore = true;
        View rootView = inflater.inflate(R.layout.recycle_view_news, container, false);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycle_view_news);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new RecyclerCursorAdapter(null, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for gingerbread and newer versions
            //mListView.setNestedScrollingEnabled(true);
        }

        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    Context mContext = getContext();
                    SharedPreferences setting = mContext.getSharedPreferences(Main2Activity.PREFS_NAME, mContext.MODE_PRIVATE);
                    int totalNewsInDatabase = setting.getInt(Main2Activity.NEWS_COUNT,0);
                    if (!mLoadingMore || totalItemCount<4) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            mLoadingMore = true;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                            //Toast.makeText(getContext(), "loading is done", Toast.LENGTH_SHORT).show();
                            if (Utilities.isOnline(getContext())) {
                                UpdateListNewsTask updateListNews = new UpdateListNewsTask(getContext());
                                String url = getContext().getString(R.string.update_list_news_by_category_id);
//                                url = url.replaceAll("categoryid=0", "categoryid=" + Integer.toString(mCategoryId));
//                                url = url.replaceAll("offset=0", "offset=" + Integer.toString(totalItemCount));
                                url = url.replaceAll("offset=0", "offset=" + Integer.toString(totalNewsInDatabase));
                                //Toast.makeText(getContext(),url,Toast.LENGTH_SHORT).show();
                                updateListNews.execute(url);
                                Log.d(LOG_TAG, "update category url: " + url);
                            } else {
                                Toast.makeText(getContext(), getContext().getString(R.string.no_internet_connection_message), Toast.LENGTH_SHORT).show();
                                mLoadingMore = false;
                            }
                        }
                    }
                }
            }
        });
        return rootView;
    }
        //onItemClickListener
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Cursor cursor = mAdapter.getCursor();
//                cursor.moveToPosition(position);
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(cursor.getString(COL_CONTENT_URI)));
//                startActivity(intent);
//                mPosition= position;
//                mListView.smoothScrollToPosition(mPosition);
//            }
//        });
//        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                //what is the bottom iten that is visible
//                int lastInScreen = firstVisibleItem + visibleItemCount;
//                //is the bottom item visible & not loading more already ? Load more !
//                if ((lastInScreen == totalItemCount) && !(mLoadingMore)) {
////                    Thread thread = new Thread(null, loadMoreListItems);
////                    thread.start();
//                    //Toast.makeText(getContext(), "Loading more news...", Toast.LENGTH_SHORT).show();
//                    mLoadingMore = true;
//                    //end of listnews loading more
//                    if(Utilities.isOnline(getContext())){
//                        UpdateListNewsTask updateListNews = new UpdateListNewsTask(getContext());
//                        String url = getContext().getString(R.string.update_list_news_by_category_id);
//                        url= url.replaceAll("categoryid=0", "categoryid=" + Integer.toString(mCategoryId));
//                        url= url.replaceAll("offset=0","offset="+Integer.toString(totalItemCount));
//                        //Toast.makeText(getContext(),url,Toast.LENGTH_SHORT).show();
//                        updateListNews.execute(url);
//                        Log.d(LOG_TAG,"update category url: "+url);
//                    }else{
//                        Toast.makeText(getContext(),getContext().getString(R.string.no_internet_connection_message),Toast.LENGTH_SHORT).show();
//                        mLoadingMore = false;
//                    }
//
//                }
//            }
//        });
//
//        return rootView;
//    }


}

package com.thefour.newsrecommender.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thefour.newsrecommender.app.data.NewsContract;

import java.util.List;

/**
 * Created by Quang Quang on 1/7/2016.
 */
public class RecyclerCursorAdapter extends RecyclerView.Adapter<RecyclerCursorAdapter.ViewHolder>{

    final int VIEW_TYPE_COUNT = 2;
    final int VIEW_TYPE_HIGHLIGHT =1;
    final int VIEW_TYPE_NORMAL =0;

    private Cursor mCursor;
    private Context mContext;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final ImageView mNewsImageView;
        public final ImageView mNewsLogoView;
        public final TextView mTitleView;
        public final TextView mSourceView;
        public final TextView mTimeView;

        public ViewHolder(View view) {
            super(view);
            mNewsImageView = (ImageView)view.findViewById(R.id.imageViewNews);
            mNewsLogoView = (ImageView)view.findViewById(R.id.imageViewLogo);
            mTitleView = (TextView)view.findViewById(R.id.textViewNewsTitle);
            mSourceView = (TextView)view.findViewById(R.id.textViewSource);
            mTimeView = (TextView)view.findViewById(R.id.textViewTime);

        }

    }

    public RecyclerCursorAdapter(Cursor dataSet, Context context){
        mCursor = dataSet;
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        boolean isSuccess = mCursor.moveToPosition(position);
        int rating = mCursor.getInt(ListNewsFragment.COL_RATING);
        if(rating< NewsContract.STAND_OUT_RATING)
            return VIEW_TYPE_NORMAL;
        else
            return VIEW_TYPE_HIGHLIGHT;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if(viewType == VIEW_TYPE_HIGHLIGHT){
            //inflat list_item_news
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_news,parent,false);
        }else{
            //inflat list_item_normal_news
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_normal_news,parent,false);
        }
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTitleView.setText(mCursor.getString(ListNewsFragment.COL_TITLE));
        holder.mSourceView.setText(mCursor.getString(ListNewsFragment.COL_SOURCE_NAME));
        String time = mCursor.getString(ListNewsFragment.COL_TIME);
        //very stupid simplify time format
        //TODO write the format method for time "10 minutes ago" or "2 days ago"
        time = time.replaceAll("2015","");
        time = time.replaceAll(":00","");
        holder.mTimeView.setText(time);
        Glide.with(mContext).load(mCursor.getString(ListNewsFragment.COL_IMAGE_URL)).into(holder.mNewsImageView);
        //new DownloadImageTask(holder.mNewsImageView).execute(mCursor.getString(ListNewsFragment.COL_IMAGE_URL));
        int newsSourceId = mCursor.getInt(ListNewsFragment.COL_SOURCE_ID);
        switch (newsSourceId){
            case 1:{
                //thanhnien
                Glide.with(mContext).load(R.drawable.thanhnien_logo100x100).into(holder.mNewsLogoView);
                break;
            }
            case 2:{
                //vnexpress
                Glide.with(mContext).load(R.drawable.vnexpress_logo).into(holder.mNewsLogoView);
                break;
            }
            case 3:{
                //tuoi tre
                Glide.with(mContext).load(R.drawable.logo_tuoitre_vuong).into(holder.mNewsLogoView);
                break;
            }
        }
    }


    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }
}

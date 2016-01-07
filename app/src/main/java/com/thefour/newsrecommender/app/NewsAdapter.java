package com.thefour.newsrecommender.app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thefour.newsrecommender.app.data.NewsContract;


/**
 * Created by Quang Quang on 8/3/2015.
 */
public class NewsAdapter extends CursorAdapter {
    Cursor mCursor;
    Context mContext;

    final int VIEW_TYPE_COUNT = 2;
    final int VIEW_TYPE_HIGHLIGHT =1;
    final int VIEW_TYPE_NORMAL =0;
    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor c = getCursor();
        boolean isSuccess = c.moveToPosition(position);
        int rating = c.getInt(ListNewsFragment.COL_RATING);
        if(rating<NewsContract.STAND_OUT_RATING)
            return VIEW_TYPE_NORMAL;
        else
            return VIEW_TYPE_HIGHLIGHT;
    }

    public NewsAdapter(Context context, Cursor c, int flags){
        super(context,c,flags);
        mCursor= c;
        mContext = context;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view;
        int rating = cursor.getInt(ListNewsFragment.COL_RATING);
        String title = cursor.getString(ListNewsFragment.COL_TITLE);
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId=0;
        switch(viewType){
            case VIEW_TYPE_HIGHLIGHT:
                layoutId = R.layout.list_item_news;
                break;
            case VIEW_TYPE_NORMAL:
                layoutId = R.layout.list_item_normal_news;
                break;
        }
        view = LayoutInflater.from(context).inflate(layoutId,parent,false);
        //view = LayoutInflater.from(context).inflate(R.layout.list_item_news, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();
        holder.mTitleView.setText(cursor.getString(ListNewsFragment.COL_TITLE));
        holder.mSourceView.setText(cursor.getString(ListNewsFragment.COL_SOURCE_NAME));
        String time = cursor.getString(ListNewsFragment.COL_TIME);
        //very stupid simplify time format
        //TODO write the format method for time "10 minutes ago" or "2 days ago"
        time = time.replaceAll("2015","");
        time = time.replaceAll(":00","");
        holder.mTimeView.setText(time);
        Glide.with(context).load(cursor.getString(ListNewsFragment.COL_IMAGE_URL)).into(holder.mNewsImageView);
        //new DownloadImageTask(holder.mNewsImageView).execute(cursor.getString(ListNewsFragment.COL_IMAGE_URL));
        int newsSourceId = cursor.getInt(ListNewsFragment.COL_SOURCE_ID);
        switch (newsSourceId){
            case 1:{
                //thanhnien
                Glide.with(context).load(R.drawable.thanhnien_logo100x100).into(holder.mNewsLogoView);
                break;
            }
            case 2:{
                //vnexpress
                Glide.with(context).load(R.drawable.vnexpress_logo).into(holder.mNewsLogoView);
                break;
            }
            case 3:{
                //tuoi tre
                Glide.with(context).load(R.drawable.logo_tuoitre_vuong).into(holder.mNewsLogoView);
                break;
            }
        }

    }

    public static class ViewHolder{
        public final ImageView mNewsImageView;
        public final ImageView mNewsLogoView;
        public final TextView mTitleView;
        public final TextView mSourceView;
        public final TextView mTimeView;

        public ViewHolder(View view) {
            mNewsImageView = (ImageView)view.findViewById(R.id.imageViewNews);
            mNewsLogoView = (ImageView)view.findViewById(R.id.imageViewLogo);
            mTitleView = (TextView)view.findViewById(R.id.textViewNewsTitle);
            mSourceView = (TextView)view.findViewById(R.id.textViewSource);
            mTimeView = (TextView)view.findViewById(R.id.textViewTime);

        }
    }
}

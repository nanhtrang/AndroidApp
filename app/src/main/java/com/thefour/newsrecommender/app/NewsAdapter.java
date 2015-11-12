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


/**
 * Created by Quang Quang on 8/3/2015.
 */
public class NewsAdapter extends CursorAdapter {
    public NewsAdapter(Context context, Cursor c, int flags){
        super(context,c,flags);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_news,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();
        holder.mTitleView.setText(cursor.getString(ListNewsFragment.COL_TITLE));
        holder.mSourceView.setText(cursor.getString(ListNewsFragment.COL_SOURCE_NAME));
        holder.mTimeView.setText(cursor.getString(ListNewsFragment.COL_TIME));
        Glide.with(context).load(cursor.getString(ListNewsFragment.COL_IMAGE_URL)).into(holder.mNewsImageView);
        //new DownloadImageTask(holder.mNewsImageView).execute(cursor.getString(ListNewsFragment.COL_IMAGE_URL));

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

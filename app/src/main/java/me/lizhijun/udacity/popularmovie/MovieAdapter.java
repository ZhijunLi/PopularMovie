package me.lizhijun.udacity.popularmovie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * This is MovieAdapter
 * Created by lizhijun on 2017/4/12.
 */

public class MovieAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private Context context=null;
    private List<Movie> movieList = null;

    public MovieAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Movie getItem(int position) {
        return movieList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.item_movie,null);
            holder = new Holder();
            holder.ivCover=(ImageView)convertView.findViewById(R.id.iv_item_movie);
            holder.tvTitle=(TextView)convertView.findViewById(R.id.tv_movie_title);
            holder.tvDesc=(TextView)convertView.findViewById(R.id.tv_movie_desc);
            holder.tvRank=(TextView)convertView.findViewById(R.id.tv_movie_rank);
            convertView.setTag(holder);
        }else{
            holder=(Holder) convertView.getTag();
        }


        Picasso.with(context).load(movieList.get(position).getCover()).into(holder.ivCover);
        holder.tvTitle.setText(movieList.get(position).getTitle());
        holder.tvDesc.setText(movieList.get(position).getDesc());
        holder.tvRank.setText(context.getString(R.string.rank)+":"+movieList.get(position).getRank());
        return convertView;
    }

    private class Holder{
        TextView tvTitle,tvDesc,tvRank;
        ImageView ivCover;

        public TextView getTvTitle() {
            return tvTitle;
        }

        public void setTvTitle(TextView tvTitle) {
            this.tvTitle = tvTitle;
        }

        public TextView getTvDesc() {
            return tvDesc;
        }

        public void setTvDesc(TextView tvDesc) {
            this.tvDesc = tvDesc;
        }

        public TextView getTvRank() {
            return tvRank;
        }

        public void setTvRank(TextView tvRank) {
            this.tvRank = tvRank;
        }

        public ImageView getIvCover() {
            return ivCover;
        }

        public void setIvCover(ImageView ivCover) {
            this.ivCover = ivCover;
        }
    }
}

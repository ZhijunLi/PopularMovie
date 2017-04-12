package me.lizhijun.udacity.popularmovie;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie model class
 * Created by lizhijun on 2017/4/12.
 */
public class Movie implements Parcelable{
    private int id;
    private String title;
    private String desc;
    private String cover;
    private double rank;
    private int vote_count;
    private double vote_average;
    private String release_date;

    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        desc = in.readString();
        cover = in.readString();
        rank = in.readDouble();
        vote_count = in.readInt();
        vote_average = in.readDouble();
        release_date = in.readString();
    }

    public Movie() {

    }

    public static final Creator<Movie> MOVIE_CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(cover);
        dest.writeDouble(rank);
        dest.writeInt(vote_count);
        dest.writeDouble(vote_average);
        dest.writeString(release_date);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}

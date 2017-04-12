package me.lizhijun.udacity.popularmovie;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {


    private ImageView iv_cover;
    private TextView tv_title;
    private TextView tv_desc;
    private TextView tv_vote_average;
    private TextView tv_vote_count;
    private TextView tv_release_date;
    Toolbar toolbar;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        movie = getIntent().getParcelableExtra("movie");
        if (movie == null){
            Toast.makeText(this,this.getResources().getString(R.string.error_movie_not_find),Toast.LENGTH_SHORT).show();
            finish();
        }

        iv_cover = (ImageView) findViewById(R.id.iv_detail_cover);
        tv_title = (TextView) findViewById(R.id.tv_detail_title);
        tv_desc = (TextView) findViewById(R.id.tv_detail_desc);
        tv_vote_average = (TextView) findViewById(R.id.tv_detail_vote_average);
        tv_vote_count = (TextView) findViewById(R.id.tv_detail_vote_count);
        tv_release_date = (TextView) findViewById(R.id.tv_detail_release_date);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(movie.getTitle());
        setSupportActionBar(toolbar);

        initData();


    }

    private void initData(){
        tv_title.setText("《"+movie.getTitle()+"》");
        tv_desc.setText(movie.getDesc());
        tv_vote_average.setText(this.getResources().getString(R.string.vote_average)+":"+movie.getVote_average());
        tv_vote_count.setText(this.getResources().getString(R.string.vote_count)+":"+movie.getVote_count());
        tv_release_date.setText(this.getResources().getString(R.string.release_date)+":"+movie.getRelease_date());
        Picasso.with(this).load(movie.getCover()).into(iv_cover);
    }

}

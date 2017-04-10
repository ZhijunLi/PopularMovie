package me.lizhijun.udacity.popularmovie;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import me.lizhijun.udacity.popularmovie.utils.HttpUtil;

public class DetailActivity extends AppCompatActivity {

    private int id = 0;
    private String baseUrl = "https://api.themoviedb.org/3/movie/%d?api_key=f64293a46346406382595accebf7664a&language=zh";

    private String imgPath = "http://image.tmdb.org/t/p/w185";

    private ImageView iv_cover;
    private TextView tv_title;
    private TextView tv_desc;
    private TextView tv_vote_average;
    private TextView tv_vote_count;
    private TextView tv_release_date;
    Toolbar toolbar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        id = getIntent().getIntExtra("id",0);
        if (id == 0){
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
        toolbar.setTitle(getIntent().getStringExtra("title"));
        setSupportActionBar(toolbar);

        progressDialog = ProgressDialog.show(DetailActivity.this,
                this.getResources().getString(R.string.loding_title),
                this.getResources().getString(R.string.loding_msg), true, false);

        HttpUtil httpUtil = new HttpUtil(String.format(baseUrl,id),HttpUtil.METHOD_GET,null);
        httpUtil.setResposeListener(new HttpUtil.OnResponseListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    Map<String,Object> map ;
                    JSONObject resultJson = new JSONObject(result);
                    Context context = DetailActivity.this;
                    tv_title.setText("《"+resultJson.getString("title")+"》");
                    tv_desc.setText(resultJson.getString("overview"));
                    tv_vote_average.setText(context.getResources().getString(R.string.vote_average)+":"+resultJson.getString("vote_average"));
                    tv_vote_count.setText(context.getResources().getString(R.string.vote_count)+":"+resultJson.getString("vote_count"));
                    tv_release_date.setText(context.getResources().getString(R.string.release_date)+":"+resultJson.getString("release_date"));
                    Picasso.with(context).load(imgPath+resultJson.getString("poster_path")).into(iv_cover);
                } catch (JSONException e) {
                    Toast.makeText(DetailActivity.this,DetailActivity.this.getResources().getText(R.string.error_json),Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFaild(String error) {
                progressDialog.dismiss();
                Toast.makeText(DetailActivity.this,error,Toast.LENGTH_SHORT).show();
            }
        }).start();

    }

}

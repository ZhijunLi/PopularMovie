package me.lizhijun.udacity.popularmovie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.lizhijun.udacity.popularmovie.utils.HttpUtil;

public class MainActivity extends AppCompatActivity {

    private String[] url = {"http://api.themoviedb.org/3/movie/popular?language=zh&api_key=f64293a46346406382595accebf7664a&language=zh&page=1",
            "http://api.themoviedb.org/3/movie/top_rated?language=zh&api_key=f64293a46346406382595accebf7664a&language=zh&page=1"};
    private static int INDEX_POP = 0;
    private static int INDEX_RATED = 1;

    //当前加载的url
    private int index = INDEX_POP;

    List<Movie> lists = new ArrayList<>();

    private GridView gv_list;
    private MovieAdapter adapter;
    private HttpUtil httpUtil;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gv_list = (GridView) findViewById(R.id.gv_list);

        adapter = new MovieAdapter(this,lists);

        gv_list.setAdapter(adapter);
        gv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id",lists.get(position).getId());
                bundle.putString("title",lists.get(position).getTitle());
                bundle.putParcelable("movie",lists.get(position));

                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        httpUtil = new HttpUtil(this,url[index],HttpUtil.METHOD_GET,null);
        fetchMovieData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_refresh:
                fetchMovieData();
                break;
            case R.id.menu_pop:
                index = INDEX_POP;
                fetchMovieData();
                break;
            case R.id.menu_rated:
                index = INDEX_RATED;
                fetchMovieData();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * 获取电影信息
     */
    private void fetchMovieData(){

        //显示ProgressDialog
        progressDialog = ProgressDialog.show(MainActivity.this,
                this.getResources().getString(R.string.loding_title),
                this.getResources().getString(R.string.loding_msg), true, true);

        httpUtil.setUrl(url[index]);
        httpUtil.setResposeListener(new HttpUtil.OnResponseListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    String imgPath = "http://image.tmdb.org/t/p/w185";
                    Movie movieModel;
                    lists.clear();
                    JSONObject resultJson = new JSONObject(result);
                    JSONArray movies = resultJson.getJSONArray("results");
                    for (int i=0;i<movies.length();i++){
                        JSONObject movie = movies.getJSONObject(i);
                        movieModel = new Movie();
                        movieModel.setId(movie.getInt("id"));
                        movieModel.setTitle(movie.getString("title"));
                        movieModel.setRank(movie.getDouble("vote_average"));
                        movieModel.setDesc(movie.getString("overview"));
                        movieModel.setCover(imgPath + movie.getString("poster_path"));
                        movieModel.setVote_average(movie.getDouble("vote_average"));
                        movieModel.setVote_count(movie.getInt("vote_count"));
                        movieModel.setRelease_date(movie.getString("release_date"));

                        lists.add(movieModel);
                    }
                    gv_list.setAdapter(adapter);

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this,MainActivity.this.getResources().getText(R.string.error_json),Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFaild(String error) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,error,Toast.LENGTH_SHORT).show();
            }
        }).start();
    }


}

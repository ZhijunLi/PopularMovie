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
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.lizhijun.udacity.popularmovie.utils.HttpUtil;

public class MainActivity extends AppCompatActivity {

    private String[] url = {"http://api.themoviedb.org/3/movie/popular?language=zh&api_key=f64293a46346406382595accebf7664a&language=zh&page=1",
            "http://api.themoviedb.org/3/movie/top_rated?language=zh&api_key=f64293a46346406382595accebf7664a&language=zh&page=1"};
    private static int INDEX_POP = 0;
    private static int INDEX_RATED = 1;

    //当前加载的url
    private int index = INDEX_POP;

    List<Map<String,Object>> lists = new ArrayList<>();

    private GridView gv_list;
    private SimpleAdapter adapter;
    private HttpUtil httpUtil;
    private ProgressDialog progressDialog;
    //    private FetchMovieTask task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gv_list = (GridView) findViewById(R.id.gv_list);

        adapter = new SimpleAdapter(this,lists,R.layout.item_movie,
                new String[]{"title","cover","rank","desc"},
                new int[]{R.id.tv_movie_title,R.id.iv_item_movie,R.id.tv_movie_rank,R.id.tv_movie_desc});

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {

                switch (view.getId()){
                    case R.id.iv_item_movie:
                        Picasso.with(MainActivity.this).load((String)data).into((ImageView)view);
                        break;
                    case R.id.tv_movie_rank:
                        ((TextView)view).setText(MainActivity.this.getString(R.string.rank)+":"+data);
                        break;
                    case R.id.tv_movie_title:
                    case R.id.tv_movie_desc:
                        ((TextView)view).setText((String)data);
                        break;
                    default:
                }
                return true;
            }
        });


        gv_list.setAdapter(adapter);
        gv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("id",Integer.parseInt(lists.get(position).get("id").toString()));
                intent.putExtra("title",(String)(lists.get(position).get("title")));
                startActivity(intent);

            }
        });

        httpUtil = new HttpUtil(url[index],HttpUtil.METHOD_GET,null);
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
                this.getResources().getString(R.string.loding_msg), true, false);

        httpUtil.setUrl(url[index]);
        httpUtil.setResposeListener(new HttpUtil.OnResponseListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    String imgPath = "http://image.tmdb.org/t/p/w185";
                    Map<String,Object> map ;
                    lists.clear();
                    JSONObject resultJson = new JSONObject(result);
                    JSONArray movies = resultJson.getJSONArray("results");
                    for (int i=0;i<movies.length();i++){
                        JSONObject movie = movies.getJSONObject(i);
                        map = new HashMap<>();
                        map.put("title",movie.getString("title"));
                        map.put("rank",movie.getDouble("vote_average"));
                        map.put("desc",movie.getString("overview"));
                        map.put("cover",imgPath+movie.getString("poster_path"));
                        map.put("id",movie.getInt("id"));
                        lists.add(map);
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



//
//    class FetchMovieTask extends AsyncTask<String, Integer, String>{
//
//        @Override
//        protected String doInBackground(String... params) {
//            return getData(params[0]);
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//
//            try {
//                String imgPath = "http://image.tmdb.org/t/p/w185";
//                Map<String,Object> map ;
//                JSONObject result = new JSONObject(s);
//                JSONArray movies = result.getJSONArray("results");
//                for (int i=0;i<movies.length();i++){
//                    JSONObject movie = movies.getJSONObject(i);
//                    map = new HashMap<>();
//                    map.put("title",movie.getString("title"));
//                    map.put("rank",movie.getDouble("vote_average"));
//                    map.put("desc",movie.getString("overview"));
//                    map.put("cover",imgPath+movie.getString("poster_path"));
//                    map.put("id",movie.getInt("id"));
//                    lists.add(map);
//
//                }
//                updateGridView();
//
//            } catch (JSONException e) {
//                Toast.makeText(MainActivity.this,"JSON解析错误",Toast.LENGTH_SHORT).show();
//            }
//            Log.d("json",s);
//        }
//
//
//
//        private String getData(String urlstr){
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            // Will contain the raw JSON response as a string.
//            String forecastJsonStr = "";
//
//            try {
//                URL url = new URL(urlstr);
//
//                // Create the request to OpenWeatherMap, and open the connection
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                // Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if (inputStream == null) {
//                    // Nothing to do.
//                    forecastJsonStr = null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                    // But it does make debugging a *lot* easier if you print out the completed
//                    // buffer for debugging.
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    // Stream was empty.  No point in parsing.
//                    forecastJsonStr = null;
//                }
//                forecastJsonStr = buffer.toString();
//            } catch (IOException e) {
//                Log.e("PlaceholderFragment", "Error ", e);
//                // If the code didn't successfully get the weather data, there's no point in attempting
//                // to parse it.
//                forecastJsonStr = "";
//            } finally{
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e("PlaceholderFragment", "Error closing stream", e);
//                    }
//                }
//            }
//
//            return forecastJsonStr;
//        }
//
//
//    }


}

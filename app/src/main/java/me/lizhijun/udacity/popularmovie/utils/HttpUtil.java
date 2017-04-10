package me.lizhijun.udacity.popularmovie.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 封装的一个简单的网络请求库，兼容Https协议，Https协议全部通过
 * Created by lizhijun on 2017/4/11.
 */

public class HttpUtil {
    public static String METHOD_POST = "POST";
    public static String METHOD_GET = "GET";
    private String url = "";
    private String method = "";
    private String params = "";

    private WebTask task;
    private OnResponseListener listener;


    public HttpUtil(String url,String method,String params){
        this.url = url;
        this.method = method;
        this.params = params;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public void setMethod(String method){
        this.method = method;
    }
    public void setParams(String params){
        this.params = params;
    }

    /**
     * 设置网络请求监听回调
     * @param listener
     */
    public HttpUtil setResposeListener(OnResponseListener listener){
        this.listener = listener;
        return this;
    }

    public void start(String url,String method,String params){
        this.url = url;
        this.method = method;
        this.params = params;
        this.start();
    }

    public void start(){
        try {

            task = new WebTask();
            task.execute(url,method,params);
        }catch (Exception e){
            listener.onFaild(e.toString());
        }
    }

    /**
     * 网络请求回调接口
     */
    public interface OnResponseListener{
        void onSuccess(String result);
        void onFaild(String error);
    }

    /**
     * 网络请求异步类
     */
    class WebTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return getData(params[0],params[1],params[2]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                listener.onSuccess(s);
            } catch (JSONException e) {
                //解析错误，说明不是json，是错误信息
                listener.onFaild(s);
            }
        }



        /**
         * 发起https请求并获取结果
         *
         * @param requestUrl 请求地址
         * @param requestMethod 请求方式（GET、POST）
         * @param params 提交的数据
         * @return String
         */
        public String httpsRequest(String requestUrl, String requestMethod, String params){
            String resultJson = "";
            StringBuffer buffer = new StringBuffer();
            try {
                TrustManager[] tm = {new MyX509TrustManager()};
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, tm, new java.security.SecureRandom());
                // 从上述SSLContext对象中得到SSLSocketFactory对象
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                URL _url = new URL(requestUrl);
                HttpsURLConnection http = (HttpsURLConnection) _url.openConnection();
                // 设置域名校验
                http.setHostnameVerifier(new TrustAnyHostnameVerifier());
                http.setSSLSocketFactory(ssf);
                // 连接超时
                http.setConnectTimeout(25000);
                // 读取超时 --服务器响应比较慢，增大时间
                http.setReadTimeout(25000);
                // 设置请求方式（GET/POST）
                http.setRequestMethod(requestMethod);

                // if ("GET".equalsIgnoreCase(requestMethod))
                http.connect();

                // 当有数据需要提交时
                if (null != params) {
                    OutputStream outputStream = http.getOutputStream();
                    // 注意编码格式，防止中文乱码
                    outputStream.write(params.getBytes("UTF-8"));
                    outputStream.close();
                }

                // 将返回的输入流转换成字符串
                InputStream inputStream = http.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String str = null;
                while ((str = bufferedReader.readLine()) != null) {
                    buffer.append(str);
                }
                bufferedReader.close();
                inputStreamReader.close();
                // 释放资源
                inputStream.close();
                inputStream = null;
                http.disconnect();
                resultJson = buffer.toString();
            } catch (ConnectException ce) {
                return ce.toString();
            } catch (Exception e) {
                return "网络错误，请检查网络再重试";
            }
            return resultJson;
        }


        /**
         * 发起http请求并获取结果
         * @param requestUrl
         * @param requestMethod
         * @param params
         * @return
         */
        public String httpRequest(String requestUrl,String requestMethod,String params){
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String resultStr = "";

            try {

                URL url = new URL(requestUrl);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    resultStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    resultStr = null;
                }
                resultStr = buffer.toString();
            } catch (IOException e) {
                return "网络错误，请检查网络再重试";
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        return "Error closing stream,"+e.toString();
                    }
                }
            }
            return resultStr;
        }


        private String getData(String urlstr,String method,String params){
            try{
                String p[] = urlstr.split(":");
                //根据协议区别开http协议和https协议，调用不同的方法
                if(p[0].equals("http")){
                    Log.e("http",urlstr);
                    return httpRequest(urlstr,method,params);
                }else if (p[0].equals("https")){
                    Log.e("https",urlstr);
                    return httpsRequest(urlstr,method,params);
                }else{
                    Log.e("非法协议",urlstr);
                    return "非法协议"+urlstr;
                }
            }catch (Exception e){
                listener.onFaild(e.toString());
            }
            return "";
        }



    }




    /**
     * 不进行主机名确认
     */
    class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
    /**
     * 信任所有主机 对于任何证书都不做SSL检测
     * 安全验证机制，而Android采用的是X509验证
     */
    class MyX509TrustManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }
}

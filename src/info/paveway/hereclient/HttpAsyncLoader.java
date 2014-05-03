package info.paveway.hereclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

/**
 * HTTP同期ローダークラス
 *
 * @version 1.0 新規作成
 */
public class HttpAsyncLoader extends AsyncTaskLoader<String> {

    private static final String TAG = HttpAsyncLoader.class.getSimpleName();

    /** URL */
    private String mUrl;
    private String mId;
    private String mLatitude;
    private String mLongitude;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param url URL
     */
    public HttpAsyncLoader(Context context, String url, String id, String latitude, String longitude) {
        super(context);
        Log.i(TAG, "HttpAsyncLoader() IN");

        mUrl = url;
        mId = id;
        mLatitude = latitude;
        mLongitude = longitude;

        Log.i(TAG, "HttpAsyncLoader() OUT(OK)");
    }

    /**
     * バックグラウンド処理を行う。
     *
     * @return 取得した文字列
     */
    @Override
    public String loadInBackground() {
        Log.i(TAG, "loadInBackground() IN");

        String result = null;

        // HTTPクライアントを生成する。
        HttpClient httpClient = new DefaultHttpClient();
        try {
            List<NameValuePair> entities = new ArrayList<NameValuePair>();

            entities.add(new BasicNameValuePair("id", mId));
            entities.add(new BasicNameValuePair("latitude", mLatitude));
            entities.add(new BasicNameValuePair("longitude", mLongitude));
            HttpPost httpPost = new HttpPost(mUrl);
            httpPost.setEntity(new UrlEncodedFormEntity(entities, "UTF-8"));
            // レスポンス
            result = httpClient.execute(httpPost, new HttpResponseHandler());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        Log.i(TAG, "loadInBackground() OUT(OK)");
        return result;
    }

    /**
     * HTTPレスポンスヘルパークラス
     *
     */
    private class HttpResponseHandler implements ResponseHandler<String> {

        @Override
        public String handleResponse(HttpResponse response) throws IOException, ClientProtocolException {
            // 正常終了の場合
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                Log.i(TAG, "loadInBackground() OUT(OK) result=[" + result + "]");
                return result;

            // 正常終了以外
            } else {
                return null;
            }
        }
    }
}

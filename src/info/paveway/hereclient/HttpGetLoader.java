package info.paveway.hereclient;

import info.paveway.hereclient.CommonConstants.Key;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

/**
 * HTTP GETローダークラス
 *
 * @version 1.0 新規作成
 */
public class HttpGetLoader extends AsyncTaskLoader<String> {

    /** ロガー */
    private Logger mLogger = new Logger(HttpGetLoader.class);

    /** パラメータ */
    private Bundle mParams;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param params パラメータ
     */
    public HttpGetLoader(Context context, Bundle params) {
        super(context);
        mParams = params;
    }

    /**
     * ロードされた時に呼び出される。
     *
     * @param レスポンス文字列
     */
    @Override
    public String loadInBackground() {
        mLogger.i("IN");

        String result = null;

        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet httpGet = new HttpGet(mParams.getString(Key.URL));
            result = httpClient.execute(httpGet, new HttpResponseHandler());
        } catch (Exception e) {
            mLogger.e(e);

        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        mLogger.i("OUT(OK) result=[" + result + "]");
        return result;
    }
}

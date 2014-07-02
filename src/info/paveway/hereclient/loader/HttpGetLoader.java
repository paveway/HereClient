package info.paveway.hereclient.loader;

import info.paveway.hereclient.CommonConstants.HttpKey;
import info.paveway.log.Logger;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

/**
 * ここにいるクライアント
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
        mLogger.d("IN");

        mParams = params;

        mLogger.d("OUT(OK)");
    }

    /**
     * ロードされた時に呼び出される。
     *
     * @param レスポンス文字列
     */
    @Override
    public String loadInBackground() {
        mLogger.i("IN");

        // レスポンス文字列
        String result = null;

        // HTTPクライアントを生成する。
        HttpClient httpClient = new DefaultHttpClient();
        try {
            // HTTP GETメソッドを生成する。
            HttpGet httpGet = new HttpGet(mParams.getString(HttpKey.URL));

            // HTTP GETメソッドを実行し、レスポンス文字列を取得する。
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

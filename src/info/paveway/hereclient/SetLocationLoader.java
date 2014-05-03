package info.paveway.hereclient;

import info.paveway.hereclient.CommonConstants.ENCODING;
import info.paveway.hereclient.CommonConstants.Key;

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
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

/**
 * ロケーションデータ設定ローダークラス
 *
 * @version 1.0 新規作成
 */
public class SetLocationLoader extends AsyncTaskLoader<String> {

    /** ロガー */
    private Logger mLogger = new Logger(SetLocationLoader.class);

    /** パラメータバンドル */
    private Bundle mParams;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param params パラメータバンドル
     */
    public SetLocationLoader(Context context, Bundle params) {
        super(context);
        mLogger.d("IN");

        mParams = params;

        mLogger.d("OUT(OK)");
    }

    /**
     * バックグラウンド処理を行う。
     *
     * @return 取得した文字列
     */
    @Override
    public String loadInBackground() {
        mLogger.i("IN");

        String result = null;

        // HTTPクライアントを生成する。
        HttpClient httpClient = new DefaultHttpClient();
        try {
            List<NameValuePair> entities = new ArrayList<NameValuePair>();

            entities.add(new BasicNameValuePair(Key.ID,        (String)mParams.get(Key.ID)));
            entities.add(new BasicNameValuePair(Key.LATITUDE,  (String)mParams.get(Key.LATITUDE)));
            entities.add(new BasicNameValuePair(Key.LONGITUDE, (String)mParams.get(Key.LONGITUDE)));
            HttpPost httpPost = new HttpPost((String)mParams.get(Key.URL));
            httpPost.setEntity(new UrlEncodedFormEntity(entities, ENCODING.UTF_8));

            // レスポンス
            result = httpClient.execute(httpPost, new HttpResponseHandler());
        } catch (Exception e) {
            mLogger.e(e);

        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        mLogger.i("OUT(OK)");
        return result;
    }

    /**
     * HTTPレスポンスヘルパークラス
     *
     */
    private class HttpResponseHandler implements ResponseHandler<String> {

        /** ロガー */
        private Logger mLogger = new Logger(HttpResponseHandler.class);

        @Override
        public String handleResponse(HttpResponse response) throws IOException, ClientProtocolException {
            mLogger.i("IN");

            // 正常終了の場合
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                String result = EntityUtils.toString(response.getEntity(), ENCODING.UTF_8);
                mLogger.i("OUT(OK) result=[" + result + "]");
                return result;

            // 正常終了以外
            } else {
                mLogger.i("OUT(NG)");
                return null;
            }
        }
    }
}

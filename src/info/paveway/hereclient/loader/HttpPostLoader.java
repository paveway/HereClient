package info.paveway.hereclient.loader;

import info.paveway.hereclient.CommonConstants.Encoding;
import info.paveway.hereclient.CommonConstants.ParamKey;
import info.paveway.log.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

/**
 * ここにいるここにいるクライアント
 * HTTP POSTローダー抽象クラス
 *
 * @version 1.0 新規作成
 *
 */
public class HttpPostLoader extends AsyncTaskLoader<String> {

    /** ロガー */
    private Logger mLogger = new Logger(HttpPostLoader.class);

    /** パラメータ */
    protected Bundle mParams;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param params パラメータ
     */
    public HttpPostLoader(Context context, Bundle params) {
        // スーパークラスのコンストラクタを呼び出す。
        super(context);

        mLogger.d("IN");

        // パラメータを設定する。
        mParams = params;

        mLogger.d("OUT(OK)");
    }

    /**
     * バックグラウンド処理を行う。
     *
     * @param 返却されたレスポンス文字列。
     */
    @Override
    public String loadInBackground() {
        mLogger.i("IN");

        String result = "";

        HttpClient httpClient = new DefaultHttpClient();
        try {
            List<NameValuePair> entities = setEntities();

            // HTTP POSTメソッドを生成する。
            HttpPost httpPost = new HttpPost((String)mParams.getString(ParamKey.URL));

            // パラメータを設定する。
            httpPost.setEntity(new UrlEncodedFormEntity(entities, Encoding.UTF_8));

            // HTTP POSTメソッドを実行する。
            result = httpClient.execute(httpPost, new HttpResponseHandler());
        } catch (Exception e) {
            mLogger.e(e);

        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        mLogger.i("OUT(OK) result=[" + result + "]");
        return result;
    }

    /**
     * HTTP POSTパラメータを設定する。
     *
     * @return HTTP POSTパラメータリスト
     */
    private List<NameValuePair> setEntities() {
        mLogger.d("IN");

        List<NameValuePair> entities = new ArrayList<NameValuePair>();

        Iterator<String> itr = mParams.keySet().iterator();
        while (itr.hasNext()) {
            String key = itr.next();
            entities.add(new BasicNameValuePair(key, mParams.getString(key)));
        }

        mLogger.d("OUT(OK)");
        return entities;
    }
}

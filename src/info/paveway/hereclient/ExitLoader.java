package info.paveway.hereclient;

import info.paveway.hereclient.CommonConstants.Encoding;
import info.paveway.hereclient.CommonConstants.Key;

import java.util.ArrayList;
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
 * 退室ローダークラス
 *
 * @version 1.0 新規作成
 *
 */
public class ExitLoader extends AsyncTaskLoader<String> {

    /** ロガー */
    private Logger mLogger = new Logger(ExitLoader.class);

    /** パラメータ */
    private Bundle mParams;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param params パラメータ
     */
    public ExitLoader(Context context, Bundle params) {
        // スーパークラスのコンストラクタを呼び出す。
        super(context);

        // パラメータを設定する。
        mParams = params;
    }

    /**
     * バックグラウンド処理を行う。
     *
     * @param 返却されたレスポンス文字列。
     */
    @Override
    public String loadInBackground() {
        String result = null;

        HttpClient httpClient = new DefaultHttpClient();
        try {
            List<NameValuePair> entities = setEntities();

            HttpPost httpPost = new HttpPost((String)mParams.getString(Key.URL));
            httpPost.setEntity(new UrlEncodedFormEntity(entities, Encoding.UTF_8));

            result = httpClient.execute(httpPost, new HttpResponseHandler());
        } catch (Exception e) {
            mLogger.e(e);

        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return result;
    }

    protected List<NameValuePair> setEntities() {
        List<NameValuePair> entities = new ArrayList<NameValuePair>();

        entities.add(new BasicNameValuePair(Key.ROOM_NO,  mParams.getString(Key.ROOM_NO)));
        entities.add(new BasicNameValuePair(Key.PASSWORD, mParams.getString(Key.PASSWORD)));
        entities.add(new BasicNameValuePair(Key.USER_ID,  mParams.getString(Key.USER_ID)));

        return entities;
    }
}

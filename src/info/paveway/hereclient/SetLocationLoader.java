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
 * ロケーションデータ設定ローダークラス
 *
 * @version 1.0 新規作成
 */
public class SetLocationLoader extends AsyncTaskLoader<String> {

    /** ロガー */
    private Logger mLogger = new Logger(SetLocationLoader.class);

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param params パラメータ
     */
    public SetLocationLoader(Context context, Bundle params) {
        super(context);
        mLogger.d("IN");

        mParams = params;

        mLogger.d("OUT(OK)");
    }

    /** パラメータバンドル */
    protected Bundle mParams;

    @Override
    public String loadInBackground() {
        mLogger.i("IN");

        String result = null;

        // HTTPクライアントを生成する。
        HttpClient httpClient = new DefaultHttpClient();
        try {
            List<NameValuePair> entities = setEntities();

            HttpPost httpPost = new HttpPost((String)mParams.get(Key.URL));
            httpPost.setEntity(new UrlEncodedFormEntity(entities, Encoding.UTF_8));

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
     * エンティティリストを設定する。
     *
     * @return エンティティリスト
     */
    protected List<NameValuePair> setEntities() {
        List<NameValuePair> entities = new ArrayList<NameValuePair>();

        entities.add(new BasicNameValuePair(Key.USER_ID,                  mParams.getString(Key.USER_ID)));
        entities.add(new BasicNameValuePair(Key.ROOM_NO,   String.valueOf(mParams.getLong(  Key.ROOM_NO))));
        entities.add(new BasicNameValuePair(Key.NICKNAME,                 mParams.getString(Key.NICKNAME)));
        entities.add(new BasicNameValuePair(Key.LATITUDE,                 mParams.getString(Key.LATITUDE)));
        entities.add(new BasicNameValuePair(Key.LONGITUDE,                mParams.getString(Key.LONGITUDE)));

        return entities;
    }
}

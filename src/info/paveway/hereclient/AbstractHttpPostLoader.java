package info.paveway.hereclient;

import info.paveway.hereclient.CommonConstants.Encoding;
import info.paveway.hereclient.CommonConstants.Key;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

/**
 * HTTP POSTローダー抽象クラス
 *
 * @version 1.0 新規作成
 *
 */
public abstract class AbstractHttpPostLoader extends AsyncTaskLoader<String> {

    /** ロガー */
    private Logger mLogger = new Logger(AbstractHttpPostLoader.class);

    /** パラメータ */
    protected Bundle mParams;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param params パラメータ
     */
    public AbstractHttpPostLoader(Context context, Bundle params) {
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

    /**
     * パラメータを設定する。
     *
     * @return パラメータリスト
     */
    abstract protected List<NameValuePair> setEntities();
}

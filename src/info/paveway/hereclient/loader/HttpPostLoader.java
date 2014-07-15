package info.paveway.hereclient.loader;

import info.paveway.log.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Bundle;

/**
 * ここにいるここにいるクライアント
 * HTTP POSTローダー抽象クラス
 *
 * @version 1.0 新規作成
 *
 */
public class HttpPostLoader extends AbstractBaseLoader {

    /** ロガー */
    private Logger mLogger = new Logger(HttpPostLoader.class);

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param params パラメータ
     */
    public HttpPostLoader(Context context, Bundle params) {
        // スーパークラスのコンストラクタを呼び出す。
        super(context, params);

        mLogger.d("IN");
        mLogger.d("OUT(OK)");
    }

    /**
     * HTTP通信を実行する。
     *
     * @param client HTTPクライアント
     * @return レスポンス文字列
     * @throws ClientProtocolException クライアントプロトコルエラー
     * @throws IOException IOエラー
     */
    @Override
    protected String execute(HttpClient httpClient) throws ClientProtocolException, IOException {
        List<NameValuePair> entities = setEntities();

        // HTTP POSTメソッドを生成する。
        HttpPost httpPost = new HttpPost((String)mParams.getString(ParamKey.URL));

        // パラメータを設定する。
        httpPost.setEntity(new UrlEncodedFormEntity(entities, Encoding.UTF_8));

        // HTTP POSTメソッドを実行する。
        return httpClient.execute(httpPost, new HttpResponseHandler());
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

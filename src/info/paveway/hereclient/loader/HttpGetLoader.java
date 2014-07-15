package info.paveway.hereclient.loader;


import java.io.IOException;

import info.paveway.log.Logger;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.os.Bundle;

/**
 * HTTP GETローダークラス
 *
 * @version 1.0 新規作成
 *
 */
public class HttpGetLoader extends AbstractBaseLoader {

    /** ロガー */
    private Logger mLogger = new Logger(HttpGetLoader.class);

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param params パラメータ
     */
    public HttpGetLoader(Context context, Bundle params) {
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
        // HTTP GETメソッドを生成する。
        HttpGet httpGet = new HttpGet(mParams.getString(ParamKey.URL));

        // HTTP GETメソッドを実行し、レスポンス文字列を取得する。
        return httpClient.execute(httpGet, new HttpResponseHandler());
    }
}

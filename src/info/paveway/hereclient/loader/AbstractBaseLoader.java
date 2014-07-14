package info.paveway.hereclient.loader;

import info.paveway.log.Logger;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

/**
 * 基底ローダー抽象クラス
 *
 * @version 1.0 新規作成
 *
 */
public abstract class AbstractBaseLoader extends AsyncTaskLoader<String> {

    /** ロガー */
    private Logger mLogger = new Logger(AbstractBaseLoader.class);

    /** エンコーディング定数 */
    public class Encoding {
        /** UTF-8 */
        public static final String UTF_8 = "UTF-8";
    }

    /** パラメータキー */
    public class ParamKey {
        /** URL */
        public static final String URL = "url";
    }

    /** パラメータ */
    protected Bundle mParams;


    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param params パラメータ
     */
    public AbstractBaseLoader(Context context, Bundle params) {
        // スーパークラスのコンストラクタを呼び出す。
        super(context);

        // パラメータを保存する。
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

        // レスポンス文字列
        String result = null;

        // HTTPクライアントを生成する。
        HttpClient httpClient = new DefaultHttpClient();
        try {
            // HTTP通信を行う。
            result = execute(httpClient);
        } catch (Exception e) {
            mLogger.e(e);

        } finally {
            // HTTPクライアントを終了する。
            httpClient.getConnectionManager().shutdown();
        }

        mLogger.i("OUT(OK) result=[" + result + "]");
        return result;
    }

    /**
     * HTTP通信を実行する。
     *
     * @param client HTTPクライアント
     * @return レスポンス文字列
     * @throws ClientProtocolException クライアントプロトコルエラー
     * @throws IOException IOエラー
     */
    abstract protected String execute(HttpClient client) throws ClientProtocolException, IOException;
}

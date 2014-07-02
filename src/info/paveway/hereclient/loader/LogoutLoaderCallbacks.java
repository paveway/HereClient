package info.paveway.hereclient.loader;

import info.paveway.hereclient.CommonConstants.HttpKey;
import info.paveway.log.Logger;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;

/**
 * ここにいるクライアント
 * ログアウトローダーコールバッククラス
 *
 * @version 1.0 新規作成
 */
public class LogoutLoaderCallbacks extends AbstractBaseLoaderCallbacks {

    /** ロガー */
    private Logger mLogger = new Logger(LogoutLoaderCallbacks.class);

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param listener レスポンス受信リスナー
     */
    public LogoutLoaderCallbacks(Context context, OnReceiveResponseListener listener) {
        super(context, listener);

        mLogger.d("IN");
        mLogger.d("OUT(OK)");
    }

    /**
     * ローダーを生成する。
     *
     * @param bundle バンドル
     * @return ローダー
     */
    @Override
    protected Loader<String> createLoader(Bundle bundle) {
        mLogger.d("IN");

        Loader<String> loader = new HttpPostLoader(mContext, bundle);

        mLogger.d("OUT(OK)");
        return loader;
    }

    /**
     * HTTP POSTローダークラス
     *
     */
    private static class HttpPostLoader extends AbstractHttpPostLoader {

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
         * HTTP POSTパラメータを設定する。
         *
         * @return HTTP POSTパラメータリスト
         */
        @Override
        protected List<NameValuePair> setEntities() {
            mLogger.d("IN");

            List<NameValuePair> entities = new ArrayList<NameValuePair>();

            entities.add(new BasicNameValuePair(HttpKey.USER_NAME,     mParams.getString(HttpKey.USER_NAME)));
            entities.add(new BasicNameValuePair(HttpKey.USER_PASSWORD, mParams.getString(HttpKey.USER_PASSWORD)));

            mLogger.d("OUT(OK)");
            return entities;
        }
    }
}

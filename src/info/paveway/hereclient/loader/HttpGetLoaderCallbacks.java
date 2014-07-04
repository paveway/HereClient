package info.paveway.hereclient.loader;

import info.paveway.log.Logger;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;

/**
 * ここにいるクライアント
 * ルームリストローダーコールバッククラス
 *
 * @version 1.0 新規作成
 */
public class HttpGetLoaderCallbacks extends AbstractBaseLoaderCallbacks {

    /** ロガー */
    private Logger mLogger = new Logger(HttpGetLoaderCallbacks.class);

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param listener レスポンス受信リスナー
     */
    public HttpGetLoaderCallbacks(Context context, OnReceiveResponseListener listener) {
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

        Loader<String> loader = new HttpGetLoader(mContext, bundle);

        mLogger.d("OUT(OK)");
        return loader;
    }
}

package info.paveway.hereclient;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;

/**
 * 入室ローダーコールバッククラス
 *
 * @version 1.0 新規作成
 */
public class EnterLoaderCallbacks extends AbstractBaseLoaderCallbacks {

    /** ロガー */
    private Logger mLogger = new Logger(EnterLoaderCallbacks.class);

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param listener レスポンス受信リスナー
     */
    public EnterLoaderCallbacks(Context context, OnReceiveResponseListener listener) {
        super(context, listener);
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

        Loader<String> loader = new EnterLoader(mContext, bundle);

        mLogger.d("OUT(OK)");
        return loader;
    }
}

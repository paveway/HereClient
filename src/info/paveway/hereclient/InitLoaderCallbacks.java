package info.paveway.hereclient;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;

/**
 * 初期化ローダーコールバッククラス
 *
 * @version 1.0 新規作成
 */
public class InitLoaderCallbacks extends AbstractBaseLoaderCallbacks {

    /** ロガー */
    private Logger mLogger = new Logger(InitLoaderCallbacks.class);

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param listener レスポンス受信リスナー
     */
    public InitLoaderCallbacks(Context context, OnReceiveResponseListener listener) {
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

        Loader<String> loader = new HttpGetLoader(mContext, bundle);

        mLogger.d("OUT(OK)");
        return loader;
    }
}

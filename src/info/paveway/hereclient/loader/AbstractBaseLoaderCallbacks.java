package info.paveway.hereclient.loader;

import info.paveway.log.Logger;
import info.paveway.util.StringUtil;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.widget.Toast;

/**
 * ここにいるクライアント
 * 基底ローダーコールバック抽象クラス
 *
 * @version 1.0 新規作成
 */
public abstract class AbstractBaseLoaderCallbacks implements LoaderCallbacks<String> {

    /** ロガー */
    private Logger mLogger = new Logger(AbstractBaseLoaderCallbacks.class);

    /** コンテキスト */
    protected Context mContext;

    /** ID */
    protected int mId;

    /** バンドル */
    protected Bundle mBundle;

    /** レスポンス受信リスナー */
    protected OnReceiveResponseListener mListener;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param listener レスポンス受信リスナー
     */
    public AbstractBaseLoaderCallbacks(Context context, OnReceiveResponseListener listener) {
        mLogger.d("IN");

        mContext = context;
        mListener = listener;

        mLogger.d("OUT(OK)");
    }

    /**
     * 生成された時に呼び出される。
     *
     * @param id ローダーID
     * @param bundle 引き渡されたデータ
     */
    @Override
    public Loader<String> onCreateLoader(int id, Bundle bundle) {
        mLogger.i("IN id=[" + id + "]");

        mId = id;
        mBundle = bundle;
        Loader<String> loader = createLoader(bundle);
        loader.forceLoad();

        mLogger.i("OUT(OK)");
        return loader;
    }

    /**
     * 終了する時に呼び出される。
     *
     * @param loader ローダー
     * @param response 取得したレスポンス文字列
     */
    @Override
    public void onLoadFinished(Loader<String> loader, String response) {
        mLogger.i("IN response=[" + response + "]");

        // 対象のローダーの場合
        if (loader.getId() == mId) {
            // レスポンス文字列がある場合
            if (StringUtil.isNotNullOrEmpty(response)) {
                // 終了時の処理を行う。
                mListener.onReceive(response, mBundle);

            // エラーの場合
            } else {
                Toast.makeText(mContext, "通信中にエラーが発生しました", Toast.LENGTH_SHORT).show();
            }
        }

        mLogger.i("OUT(OK)");
    }

    /**
     * リセットされた時に呼び出される。
     *
     * @param loader ローダー
     */
    @Override
    public void onLoaderReset(Loader<String> loader) {
        mLogger.i("IN");

        // 何もしない。

        mLogger.i("OUT(OK)");
    }

    /**
     * ローダーを生成する。
     *
     * @param bundle バンドル
     * @return ローダー
     */
    abstract protected Loader<String> createLoader(Bundle bundle);
}

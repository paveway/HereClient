package info.paveway.hereclient.loader;

import info.paveway.hereclient.dialog.ProgressStatusDialog;
import info.paveway.log.Logger;
import info.paveway.util.StringUtil;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

/**
 * 基底ローダーコールバック抽象クラス
 *
 * @version 1.0 新規作成
 *
 */
public abstract class AbstractBaseLoaderCallbacks implements LoaderCallbacks<String> {

    /** ロガー */
    private Logger mLogger = new Logger(AbstractBaseLoaderCallbacks.class);

    /** コンテキスト */
    protected Context mContext;

    /** ハンドラー */
    protected Handler mHandler = new Handler();

    /** ローダーID */
    protected int mLoaderId;

    /** バンドル */
    protected Bundle mBundle;

    /** レスポンス受信リスナー */
    protected OnReceiveResponseListener mListener;

    /** 処理中ダイアログ */
    private ProgressStatusDialog mProgressDialog;

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
     * @param loaderId ローダーID
     * @param bundle 引き渡されたデータ
     */
    @Override
    public Loader<String> onCreateLoader(int loaderId, Bundle bundle) {
        mLogger.i("IN loaderId=[" + loaderId + "]");

        // 処理状態表示ダイアログを表示する場合
        if (isShowProgressStatusDialog()) {
            // 処理中ダイアログを表示する。
            FragmentManager manager = ((ActionBarActivity)mContext).getSupportFragmentManager();
            if (null != mProgressDialog) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            mProgressDialog = ProgressStatusDialog.newInstance("処理中", "しばらくお待ちください");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show(manager, ProgressStatusDialog.class.getSimpleName());
        }

        mLoaderId = loaderId;
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

        // 処理状態表示ダイアログを表示する場合
        if (isShowProgressStatusDialog()) {
            // 処理中ダイアログを閉じる。　
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            });
        }

        // 対象のローダーの場合
        if (loader.getId() == mLoaderId) {
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

        // 処理状態表示ダイアログを表示する場合
        if (isShowProgressStatusDialog()) {
            // 処理中ダイアログを閉じる。　
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // 処理中ダイアログが有効な場合
                    if (null != mProgressDialog) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                }
            });
        }

        mLogger.i("OUT(OK)");
    }

    /**
     * 処理状態表示ダイアログを表示するかチェックする。
     *
     * @return 判定結果 true:表示する / false:表示しない
     */
    protected boolean isShowProgressStatusDialog() {
        return (mContext instanceof ActionBarActivity);
    }

    /**
     * ローダーを生成する。
     *
     * @param bundle バンドル
     * @return ローダー
     */
    abstract protected Loader<String> createLoader(Bundle bundle);
}

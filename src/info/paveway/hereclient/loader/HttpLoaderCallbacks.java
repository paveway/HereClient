package info.paveway.hereclient.loader;

import java.lang.reflect.Constructor;

import info.paveway.log.Logger;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;

/**
 * HTTPローダーコールバッククラス
 *
 * @version 1.0 新規作成
 *
 */
public class HttpLoaderCallbacks<T> extends AbstractBaseLoaderCallbacks {

    /** ロガー */
    private Logger mLogger = new Logger(HttpLoaderCallbacks.class);

    /** ローダークラス */
    private Class<T> mLoaderClass;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param listener レスポンス受信リスナー
     */
    public HttpLoaderCallbacks(Context context, OnReceiveResponseListener listener, Class<T> loaderClass) {
        super(context, listener);

        mLoaderClass = loaderClass;

        mLogger.d("IN");
        mLogger.d("OUT(OK)");
    }

    /**
     * ローダーを生成する。
     *
     * @param bundle バンドル
     * @return ローダー
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Loader<String> createLoader(Bundle bundle) {
        mLogger.d("IN");

        Loader<String> loader = null;
        try {
            // コンストラクタを取得する。
            Class<?>[] parameterTypes = {Context.class, Bundle.class};
            Constructor<?> constructor = mLoaderClass.getConstructor(parameterTypes);

            // コンストラクタが取得できない場合
            if (null == constructor) {
                // nullを返却する。
                return null;
            }

            // インスタンス化する。
            Object[] args = {mContext, bundle};
            loader = (Loader<String>)constructor.newInstance(args);
        } catch (Exception e) {
            mLogger.e(e);
        }

        mLogger.d("OUT(OK)");
        return loader;
    }
}

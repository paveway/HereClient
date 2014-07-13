package info.paveway.hereclient.loader;

import android.os.Bundle;

/**
 * レスポンス受信リスナーインターフェースクラス
 *
 * @version 1.0 新規作成
 *
 */
public interface OnReceiveResponseListener {

    /**
     * 受信した時に呼び出される。
     *
     * @param response レスポンスデータ
     * @param bundle バンドル
     */
    void onReceive(String response, Bundle bundle);
}

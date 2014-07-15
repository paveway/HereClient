package info.paveway.hereclient;

import info.paveway.hereclient.CommonConstants.Action;
import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.PrefsKey;
import info.paveway.log.Logger;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * ここにいるクライアント
 * 設定画面
 *
 * @version 1.0 新規作成
 *
 */
public class SettingsPreferenceActivity extends PreferenceActivity {

    /** ロガー */
    private Logger mLogger = new Logger(SettingsPreferenceActivity.class);

    /** プリフェレンス */
    private SharedPreferences mPrefs;

    /**
     * 生成した時に呼び出される。
     *
     * @param savedInstanceState 保存した時のインスタンスの状態
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mLogger.d("IN");

        // スーパークラスのメソッドを呼び出す。
        super.onCreate(savedInstanceState);

        // 設定リソースを追加する。
        addPreferencesFromResource(R.xml.preference_settings);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(SettingsPreferenceActivity.this);

        mLogger.d("OUT(OK)");
    }

    /**
     * 画面がバックグラウンドになった時に呼び出される。
     */
    @Override
    public void onPause() {
        mLogger.d("IN");

        // スーパークラスのメソッドを呼び出す。
        super.onPause();

        // 更新間隔を取得する。
        String interval = mPrefs.getString(PrefsKey.INTERVAL_LIST, String.valueOf(CommonConstants.DEFAULT_INTERVAL));
        mLogger.d("interval=[" + interval + "]");

        // 設定結果をブロードキャストする。
        Intent intent = new Intent();
        intent.setAction(Action.ACTION_SETTINGS);
        intent.putExtra(ExtraKey.INTERVAL, interval);
        sendBroadcast(intent);

        mLogger.d("OUT(OK)");
    }
}

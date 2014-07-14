package info.paveway.hereclient;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * ここにいるクライアント
 * 設定画面
 *
 * @version 1.0 新規作成
 *
 */
public class SettingsPreferenceActivity extends PreferenceActivity {

    /**
     * 生成した時に呼び出される。
     *
     * @param savedInstanceState 保存した時のインスタンスの状態
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // スーパークラスのメソッドを呼び出す。
        super.onCreate(savedInstanceState);

        // 設定リソースを追加する。
        addPreferencesFromResource(R.xml.preference_settings);
    }
}

package info.paveway.hereclient;

import info.paveway.hereclient.dialog.LoginDialog;
import info.paveway.log.Logger;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

/**
 * ここにいるクライアント
 * メイン画面クラス
 *
 * @version 1.0 新規作成
 *
 */
public class MainActivity extends AbstractBaseActivity {

    /** ロガー */
    private Logger mLogger = new Logger(MainActivity.class);

    /**
     * 生成された時に呼び出される。
     *
     * @param savedInstanceState 保存した時のインスタンスの状態
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLogger.d("IN");

        // スーパークラスのメソッドを呼び出す。
        super.onCreate(savedInstanceState);

        // レイアウトを設定する。
        setContentView(R.layout.activity_main);

        // ログインダイアログを表示する。
        FragmentManager manager = getSupportFragmentManager();
        LoginDialog loginUserDialog = LoginDialog.newInstance();
        loginUserDialog.setCancelable(false);
        loginUserDialog.show(manager, LoginDialog.class.getSimpleName());

        mLogger.d("OUT(OK)");
    }
}

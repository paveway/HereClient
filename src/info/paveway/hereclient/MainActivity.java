package info.paveway.hereclient;

import info.paveway.hereclient.dialog.InfoDialog;
import info.paveway.hereclient.dialog.LoginDialog;
import info.paveway.log.Logger;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * ここにいるクライアント
 * メイン画面クラス
 *
 * @version 1.0 新規作成
 *
 */
public class MainActivity extends ActionBarActivity {

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
        setContentView(R.layout.activity_main);

        // ログインダイアログを表示する。
        FragmentManager manager = getSupportFragmentManager();
        LoginDialog loginUserDialog = LoginDialog.newInstance();
        loginUserDialog.setCancelable(false);
        loginUserDialog.show(manager, LoginDialog.class.getSimpleName());

        mLogger.d("OUT(OK)");
    }

    /**
     * メニューが生成された時に呼び出される。
     *
     * @param menu メニュー
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mLogger.d("IN");

        getMenuInflater().inflate(R.menu.main, menu);

        mLogger.d("OUT(OK)");
        return true;
    }

    /**
     * メニューが選択された時に呼び出される。
     *
     * @param item 選択されたメニュー
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mLogger.d("IN");

        int id = item.getItemId();
        if (id == R.id.menu_info) {
            // バージョン情報ダイアログを表示する。
            FragmentManager manager = getSupportFragmentManager();
            InfoDialog infoDialog = InfoDialog.newInstance();
            infoDialog.show(manager, InfoDialog.class.getSimpleName());
            return true;
        }

        mLogger.d("OUT(OK)");
        return super.onOptionsItemSelected(item);
    }
}

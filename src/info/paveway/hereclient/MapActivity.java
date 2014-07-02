package info.paveway.hereclient;

import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.HttpKey;
import info.paveway.hereclient.CommonConstants.LoaderId;
import info.paveway.hereclient.CommonConstants.Url;
import info.paveway.hereclient.data.RoomData;
import info.paveway.hereclient.data.UserData;
import info.paveway.hereclient.loader.ExitRoomLoaderCallbacks;
import info.paveway.hereclient.loader.LogoutLoaderCallbacks;
import info.paveway.hereclient.loader.OnReceiveResponseListener;
import info.paveway.log.Logger;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * ここにいるクライアント
 * マップ画面クラス
 *
 * @version 1.0 新規作成
 *
 */
public class MapActivity extends ActionBarActivity {

    /** ロガー */
    private Logger mLogger = new Logger(MapActivity.class);

    /** ユーザデータ */
    private UserData mUserData;

    /** ルームデータ */
    private RoomData mRoomData;

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
        setContentView(R.layout.activity_map);

        // インテントを取得する。
        Intent intent = getIntent();
        // インテントが取得できない場合
        if (null == intent) {
            // 終了する。
            Toast.makeText(this, "不正な画面遷移です", Toast.LENGTH_SHORT).show();
            finish();
            mLogger.d("OUT(NG)");
            return;
        }

        // ユーザデータを取得する。
        mUserData = (UserData)intent.getSerializableExtra(ExtraKey.USER_DATA);
        // ユーザデータが取得できない場合
        if (null == mUserData) {
            // 終了する。
            Toast.makeText(this, "不正な画面遷移です", Toast.LENGTH_SHORT).show();
            finish();
            mLogger.d("OUT(NG)");
            return;
        }

        // ルームデータを取得する。
        mRoomData = (RoomData)intent.getSerializableExtra(ExtraKey.ROOM_DATA);
        // ルームデータが取得できない場合
        if (null == mRoomData) {
            // 終了する。
            Toast.makeText(this, "不正な画面遷移です", Toast.LENGTH_SHORT).show();
            finish();
            mLogger.d("OUT(NG)");
            return;
        }

        // ボタンにリスナーを設定する。
        ((Button)findViewById(R.id.exitRoomButton)).setOnClickListener(new ButtonOnClickListener());
        ((Button)findViewById(R.id.logoutButton)).setOnClickListener(new ButtonOnClickListener());

        mLogger.d("OUT(OK)");
    }

    /**
     * ボタンクリックリスナークラス
     *
     */
    private class ButtonOnClickListener implements OnClickListener {

        /** ロガー */
        private Logger mLogger = new Logger(ButtonOnClickListener.class);

        /**
         * ボタンがクリックされた時に呼び出される。
         *
         * @param v クリックされたボタン
         */
        @Override
        public void onClick(View v) {
            mLogger.d("IN");

            // ボタンにより処理を判別する。
            switch (v.getId()) {
            // 退室ボタンの場合
            case R.id.exitRoomButton: {
                // パラメータを生成する。
                Bundle params = new Bundle();
                params.putString(HttpKey.URL,       Url.EXIT_ROOM);
                params.putString(HttpKey.USER_ID, String.valueOf(mUserData.getId()));

                // ログアウトローダーをロードする。
                getSupportLoaderManager().restartLoader(
                        LoaderId.LOGOUT, params, new LogoutLoaderCallbacks(
                                MapActivity.this, new LogoutOnReceiveResponseListener()));
                break;
            }

            // ログアウトボタンの場合
            case R.id.logoutButton: {
                // パラメータを生成する。
                Bundle params = new Bundle();
                params.putString(HttpKey.URL,       Url.EXIT_ROOM);
                params.putString(HttpKey.ROOM_ID, String.valueOf(mRoomData.getId()));
                params.putString(HttpKey.USER_ID, String.valueOf(mUserData.getId()));

                // 退室ローダーをロードする。
                getSupportLoaderManager().restartLoader(
                        LoaderId.EXIT_ROOM, params, new ExitRoomLoaderCallbacks(
                                MapActivity.this, new ExitRoomOnReceiveResponseListener()));
                break;
            }
            }

            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * 退室レスポンス受信リスナークラス
     *
     */
    private class ExitRoomOnReceiveResponseListener implements OnReceiveResponseListener {

        /** ロガー */
        private Logger mLogger = new Logger(ExitRoomOnReceiveResponseListener.class);

        /**
         * レスポンス受信した時に呼び出される。
         *
         * @param response レスポンス文字列
         * @param bundle バンドル
         */
        @Override
        public void onReceive(String response, Bundle bundle) {
            mLogger.d("IN");

            // 終了する。
            finish();

            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * ログアウトレスポンス受信リスナークラス
     *
     */
    private class LogoutOnReceiveResponseListener implements OnReceiveResponseListener {

        /** ロガー */
        private Logger mLogger = new Logger(LogoutOnReceiveResponseListener.class);

        /**
         * レスポンス受信した時に呼び出される。
         *
         * @param response レスポンス文字列
         * @param bundle バンドル
         */
        @Override
        public void onReceive(String response, Bundle bundle) {
            mLogger.d("IN");

            // 終了する。
            finish();

            mLogger.d("OUT(OK)");
        }
    }
}

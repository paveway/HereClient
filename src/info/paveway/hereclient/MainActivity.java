package info.paveway.hereclient;

import info.paveway.hereclient.CommonConstants.IntefaceName;
import info.paveway.hereclient.CommonConstants.Key;
import info.paveway.hereclient.CommonConstants.LoaderId;
import info.paveway.hereclient.CommonConstants.RequestCode;
import info.paveway.hereclient.CommonConstants.Url;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * ココにイル クライアント
 * メイン画面
 *
 * @version 1.0 新規作成
 */
public class MainActivity extends FragmentActivity {

    /** ロガー */
    private Logger mLogger = new Logger(MainActivity.class);

    /** ID値 */
    private static final String USER_ID = MacAddress.getMacAddressString(IntefaceName.WLAN0);

    /** 部屋番号値 */
    private TextView mRoomNoValue;

    /** パスワード入力値 */
    private EditText mPasswordValue;

    /** ニックネーム入力値 */
    private EditText mNicknameValue;

    /** 部屋番号ボタン */
    private Button mRoomNoButton;

    /** 入室ボタン */
    private Button mEnterButton;

    /** 退室ボタン */
    private Button mExitButton;

    /** クリアボタン */
    private Button mClearButton;

    /** 部屋番号 */
    private long mRoomNo;

    /** パスワード */
    private String mPassword;

    /** ニックネーム */
    private String mNickname;

    /** 部屋データリスト */
    private List<RoomData> mRoomDataList = new ArrayList<RoomData>();

    /**
     * 生成された時に呼び出される。
     *
     * @param savedInstanceState 保存した時のインスタンスの状態
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLogger.i("IN");

        // スーパークラスのメソッドを呼び出す。
        super.onCreate(savedInstanceState);

        // レイアウトを設定する。
        setContentView(R.layout.main_activity);

        mRoomNoValue   = (TextView)findViewById(R.id.roomNoValue);
        mPasswordValue = (EditText)findViewById(R.id.passwordValue);
        mNicknameValue = (EditText)findViewById(R.id.nicknameValue);
        mRoomNoButton  = (Button)findViewById(R.id.roomNoButton);
        mEnterButton   = (Button)findViewById(R.id.enterButton);
        mExitButton    = (Button)findViewById(R.id.exitButton);
        mClearButton   = (Button)findViewById(R.id.clearButton);
        mRoomNoButton.setOnClickListener(new ButtonOnClickListener());
        mEnterButton.setOnClickListener( new ButtonOnClickListener());
        mExitButton.setOnClickListener(  new ButtonOnClickListener());
        mClearButton.setOnClickListener( new ButtonOnClickListener());

        // 設定値を読み出す。
        readPrefs();

        // 入室済みの場合
        if ((0 != mRoomNo) && !"".equals(mPassword) && !"".equals(mNickname)) {
            mRoomNoValue.setText(String.valueOf(mRoomNo));
            mPasswordValue.setText(mPassword);
            mNicknameValue.setText(mNickname);
            mRoomNoButton.setEnabled(false);
            mPasswordValue.setEnabled(false);
            mNicknameValue.setEnabled(false);
            mEnterButton.setEnabled(true);
            mExitButton.setEnabled(true);

        // 未入室の場合
        } else {
            // パラメータを生成する。
            Bundle params = new Bundle();
            params.putString(Key.URL, Url.INIT);

            // 初期化ローダーをロードし、初期データを取得する。
            getSupportLoaderManager().initLoader(
                    LoaderId.INIT, params, new InitLoaderCallbacks(MainActivity.this));
        }

        mRoomNo = 1;
        mPassword = "0000";
        mNickname = "ニックネーム";

        mLogger.i("OUT(OK)");
    }

    /**
     * 他の画面の呼び出しからの戻った時に呼び出される。
     *
     * @param requestCode 要求コード
     * @param resultCode 結果コード
     * @param intent データ
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        mLogger.i("IN requestCode=[" + requestCode + "] resultCode=[" + resultCode + "]");

        // 要求コードにより処理を判別する。
        switch (requestCode) {
        // 設定画面の場合
        case RequestCode.SETTINGS:
            // 設定値を読み出す。
            readPrefs();
            break;

        // 上記以外
        default:
            // 何もしない。
            mLogger.w("Unknown Request Code.");
            break;
        }

        mLogger.i("OUT(OK)");
    }

    /**
     * メニューを生成した時に呼び出される。
     *
     * @param menu メニュー
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * メニュー項目が選択された時に呼び出される。
     *
     * @param item メニュー項目
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = true;

        // メニュー項目IDにより処理を判別する。
        switch (item.getItemId()) {
        // 設定メニューの場合
        case R.id.settings_menu:
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(intent, RequestCode.SETTINGS);
            break;

        // 上記以外
        default:
            // スーパークラスのメソッドを呼び出す。
            ret = super.onOptionsItemSelected(item);
            break;
        }

        return ret;
    }

    /**
     * キーを押した時に呼び出される。
     *
     * @param keyCode キーコード
     * @param event キーイベント
     * @return 処理結果
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // バックキーではない場合
        if(keyCode != KeyEvent.KEYCODE_BACK){
            // スーパークラスのメソッドを呼び出す。
            return super.onKeyDown(keyCode, event);

        // バックキーの場合
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("終了確認");
            builder.setMessage("終了します");
            builder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            // 処理済みとする。
            return false;
        }
    }

    /**
     * 設定値を読み出す。
     */
    private void readPrefs() {
        mLogger.d("IN");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        mRoomNo = prefs.getLong(Key.ROOM_NO, 0);
        mPassword = prefs.getString(Key.PASSWORD, "");
        mNickname = prefs.getString(Key.NICKNAME, "");

        mLogger.d("OUT(OK)");
    }

    /**************************************************************************/
    /**
     * ボタンクリックリスナークラス
     *
     */
    private class ButtonOnClickListener implements OnClickListener {

        /**
         * ボタンがクリックされた時に呼び出される。
         *
         * @param v クリックされたボタン
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            // 部屋番号選択ボタンの場合
            case R.id.roomNoButton:

                break;

            // 入室ボタンの場合
            case R.id.enterButton: {
                // パラメータを生成する。
                Bundle params = new Bundle();
                params.putString(Key.URL,      Url.ENTER);
                params.putString(Key.ROOM_NO,  String.valueOf(mRoomNo));
                params.putString(Key.PASSWORD, mPassword);
                params.putString(Key.USER_ID,  USER_ID);
                params.putString(Key.NICKNAME, mNickname);

                // 入室ローダーをロードする。
                getSupportLoaderManager().restartLoader(
                        LoaderId.ENTER, params, new EnterLoaderCallbacks(MainActivity.this));
                break;
            }

            // 退室ボタンの場合
            case R.id.exitButton: {
                // パラメータを生成する。
                Bundle params = new Bundle();
                params.putString(Key.URL,      Url.EXIT);
                params.putString(Key.ROOM_NO,  String.valueOf(mRoomNo));
                params.putString(Key.PASSWORD, mPassword);
                params.putString(Key.USER_ID,  USER_ID);

                // 退室ローダーをロードする。
                getSupportLoaderManager().restartLoader(
                        LoaderId.EXIT, params, new ExitLoaderCallbacks(MainActivity.this));
                break;
            }

            // クリアボタンの場合
            case R.id.clearButton:
                mRoomNoValue.setText("");
                mPasswordValue.setText("");
                mNicknameValue.setText("");
                mRoomNo = 0;
                mPassword = "";
                mNickname = "";
                break;

            // 上記以外
            default:
                // 何もしない。
                break;
            }
        }
    }

    /**************************************************************************/
    /**
     * 初期ローダーコールバッククラス
     */
    private class InitLoaderCallbacks implements LoaderCallbacks<String> {

        /** コンテキスト */
        private Context mContext;

        /**
         * コンストラクタ
         *
         * @param context コンテキスト
         */
        public InitLoaderCallbacks(Context context) {
            mContext = context;
        }

        /**
         * 生成された時に呼び出される。
         *
         * @param id ID
         * @param bundle 引き渡されたデータ
         */
        @Override
        public Loader<String> onCreateLoader(int id, Bundle bundle) {
            Loader<String> loader = new InitLoader(mContext, bundle);
            loader.forceLoad();
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
            // 初期化ローダーの場合
            if (LoaderId.INIT == loader.getId()) {
                // レスポンス文字列がある場合
                if (StringUtil.isNotNullOrEmpty(response)) {
                    try {
                        JSONObject json = new JSONObject(response);
                        boolean status = json.getBoolean(Key.STATUS);
                        // 正常終了の場合
                        if (status) {
                            JSONArray rooms = json.getJSONArray(Key.ROOMS);
                            int roomsNum = rooms.length();
                            for (int i = 0; i < roomsNum; i++) {
                                JSONObject room = rooms.getJSONObject(i);

                                RoomData roomData = new RoomData();
                                roomData.setRoomNo(room.getLong(Key.ROOM_NO));
                                roomData.setUsed(room.getBoolean(Key.USED));
                                roomData.setPassword(room.getString(Key.PASSWORD));
                                roomData.setUserId(room.getString(Key.USER_ID));
                                roomData.setNickname(room.getString(Key.NICKNAME));
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                                try {
                                    roomData.setUpdate(format.parse(room.getString(Key.UPDATE)));
                                } catch (ParseException e) {
                                    mLogger.e(e);
                                }

                                mRoomDataList.add(roomData);
                            }

                        // 異常終了の場合
                        } else {

                        }
                    } catch (JSONException e) {
                        mLogger.e(e);
                    }
                }
            }
        }

        /**
         * リセットされた時に呼び出される。
         *
         * @param loader ローダー
         */
        @Override
        public void onLoaderReset(Loader<String> loader) {
            // 何もしない。
        }
    }

    /**************************************************************************/
    /**
     * 入室ローダーコールバッククラス
     */
    private class EnterLoaderCallbacks implements LoaderCallbacks<String> {

        /** コンテキスト */
        private Context mContext;

        /**
         * コンストラクタ
         *
         * @param context コンテキスト
         */
        public EnterLoaderCallbacks(Context context) {
            mContext = context;
        }

        /**
         * 生成された時に呼び出される。
         *
         * @param id ID
         * @param bundle 引き渡されたデータ
         */
        @Override
        public Loader<String> onCreateLoader(int id, Bundle bundle) {
            Loader<String> loader = new EnterLoader(mContext, bundle);
            loader.forceLoad();
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
            // 入室ローダーの場合
            if (LoaderId.ENTER == loader.getId()) {
                // レスポンス文字列がある場合
                if (StringUtil.isNotNullOrEmpty(response)) {
                    try {
                        JSONObject json = new JSONObject(response);
                        boolean status = json.getBoolean(Key.STATUS);
                        // 正常終了の場合
                        if (status) {
                            JSONArray rooms = json.getJSONArray(Key.ROOMS);
                            int roomsNum = rooms.length();
                            for (int i = 0; i < roomsNum; i++) {
                                JSONObject room = rooms.getJSONObject(i);

                                RoomData roomData = new RoomData();
                                roomData.setRoomNo(room.getLong(Key.ROOM_NO));
                                roomData.setUsed(room.getBoolean(Key.USED));
                                roomData.setPassword(room.getString(Key.PASSWORD));
                                roomData.setUserId(room.getString(Key.USER_ID));
                                roomData.setNickname(room.getString(Key.NICKNAME));
                                DateFormat format = DateFormat.getDateInstance();
                                try {
                                    roomData.setUpdate(format.parse(room.getString(Key.UPDATE)));
                                } catch (ParseException e) {
                                    mLogger.e(e);
                                }

                                mRoomDataList.add(roomData);
                            }

                        // 異常終了の場合
                        } else {

                        }
                    } catch (JSONException e) {
                        mLogger.e(e);
                    }
                }
            }
        }

        /**
         * リセットされた時に呼び出される。
         *
         * @param loader ローダー
         */
        @Override
        public void onLoaderReset(Loader<String> loader) {
            // 何もしない。
        }
    }

    /**************************************************************************/
    /**
     * 退室ローダーコールバッククラス
     */
    private class ExitLoaderCallbacks implements LoaderCallbacks<String> {

        /** コンテキスト */
        private Context mContext;

        /**
         * コンストラクタ
         *
         * @param context コンテキスト
         */
        public ExitLoaderCallbacks(Context context) {
            mContext = context;
        }

        /**
         * 生成された時に呼び出される。
         *
         * @param id ID
         * @param bundle 引き渡されたデータ
         */
        @Override
        public Loader<String> onCreateLoader(int id, Bundle bundle) {
            Loader<String> loader = new EnterLoader(mContext, bundle);
            loader.forceLoad();
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
            // 退室ローダーの場合
            if (LoaderId.EXIT == loader.getId()) {
                // レスポンス文字列がある場合
                if (StringUtil.isNotNullOrEmpty(response)) {
                    try {
                        JSONObject json = new JSONObject(response);
                        boolean status = json.getBoolean(Key.STATUS);
                        // 正常終了の場合
                        if (status) {
                            JSONArray rooms = json.getJSONArray(Key.ROOMS);
                            int roomsNum = rooms.length();
                            for (int i = 0; i < roomsNum; i++) {
                                JSONObject room = rooms.getJSONObject(i);

                                RoomData roomData = new RoomData();
                                roomData.setRoomNo(room.getLong(Key.ROOM_NO));
                                roomData.setUsed(room.getBoolean(Key.USED));
                                roomData.setPassword(room.getString(Key.PASSWORD));
                                roomData.setUserId(room.getString(Key.USER_ID));
                                roomData.setNickname(room.getString(Key.NICKNAME));
                                DateFormat format = DateFormat.getDateInstance();
                                try {
                                    roomData.setUpdate(format.parse(room.getString(Key.UPDATE)));
                                } catch (ParseException e) {
                                    mLogger.e(e);
                                }

                                mRoomDataList.add(roomData);
                            }

                        // 異常終了の場合
                        } else {

                        }
                    } catch (JSONException e) {
                        mLogger.e(e);
                    }
                }
            }
        }

        /**
         * リセットされた時に呼び出される。
         *
         * @param loader ローダー
         */
        @Override
        public void onLoaderReset(Loader<String> loader) {
            // 何もしない。
        }
    }
}

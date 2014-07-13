package info.paveway.hereclient.dialog;

import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.LoaderId;
import info.paveway.hereclient.CommonConstants.ParamKey;
import info.paveway.hereclient.CommonConstants.PrefsKey;
import info.paveway.hereclient.CommonConstants.Url;
import info.paveway.hereclient.R;
import info.paveway.hereclient.RoomListActivity;
import info.paveway.hereclient.data.UserData;
import info.paveway.hereclient.loader.HttpPostLoaderCallbacks;
import info.paveway.hereclient.loader.OnReceiveResponseListener;
import info.paveway.log.Logger;
import info.paveway.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * ここにいるクライアント
 * ログインダイアログクラス
 *
 * @version 1.0 新規作成
 *
 */
public class LoginDialog extends AbstractBaseDialogFragment {

    /** ロガー */
    private Logger mLogger = new Logger(LoginDialog.class);

    /** ハンドラー */
    private Handler mHandler = new Handler();

    /** プリフェレンス */
    private SharedPreferences mPrefs;

    /** ユーザ名入力 */
    private EditText mUserNameValue;

    /** パスワード入力 */
    private EditText mPasswordValue;

    /** ログイン済みチェックボックス */
    private CheckBox mLoggedCheckBox;

    /** ユーザ名 */
    private String mUserName;

    /** パスワード */
    private String mUserPassword;

    /**
     * インスタンスを返却する。
     *
     * @return インスタンス
     */
    public static LoginDialog newInstance() {
        LoginDialog instance = new LoginDialog();
        return instance;
    }

    /**
     * 生成した時に呼び出される。
     *
     * @param savedInstanceState 保存した時のインスタンスの状態
     * @return ダイアログ
     */
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mLogger.d("IN");

        // 設定値を取得する。
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean logged = mPrefs.getBoolean(PrefsKey.LOGGED, false);
        String userName = mPrefs.getString(PrefsKey.USER_NAME, "");
        String userPassword = mPrefs.getString(PrefsKey.USER_PASSWORD, "");

        // ログイン済みフラグが未設定またはユーザ名、ユーザパスワードが未設定の場合
        if (!logged || StringUtil.isNullOrEmpty(userName) || StringUtil.isNullOrEmpty(userPassword)) {
            // 設定値をクリアする。
            Editor editor = mPrefs.edit();
            editor.putBoolean(PrefsKey.LOGGED, false);
            editor.putString(PrefsKey.USER_NAME, "");
            editor.putString(PrefsKey.USER_PASSWORD, "");
            editor.commit();

        // すべて設定されている場合
        } else {
            // ログイン処理を行う。
            mUserName = userName;
            mUserPassword = userPassword;
            login();
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_login, null);

        // 入力項目を取得する。
        mUserNameValue  = (EditText)rootView.findViewById(R.id.userNameValue);
        mPasswordValue  = (EditText)rootView.findViewById(R.id.passwordValue);
        mLoggedCheckBox = (CheckBox)rootView.findViewById(R.id.loggedCheckBox);

        // ログインダイアログを設定する。
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_login_title);
        builder.setPositiveButton(R.string.dialog_login_button, null);
        builder.setNeutralButton(R.string.dialog_regist_user_button, null);
        builder.setNegativeButton(R.string.dialog_end_button, null);
        builder.setView(rootView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        // ボタン押下でダイアログが閉じないようにリスナーを設定する。
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // ログインボタン
                ((AlertDialog)dialog).getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doLoginButton();
                    }
                });

                // ユーザ登録ボタン
                ((AlertDialog)dialog).getButton(Dialog.BUTTON_NEUTRAL).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doRegistUserButton();
                    }
                });

                // 終了ボタン
                ((AlertDialog)dialog).getButton(Dialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doEndButton();
                    }
                });
            }
        });

        mLogger.d("OUT(OK)");
        return dialog;
    }

    /**
     * ログインボタンの処理を行う。
     */
    private void doLoginButton() {
        mLogger.d("IN");

        // 入力値を取得する。
        String userName = mUserNameValue.getText().toString();
        String userPassword = mPasswordValue.getText().toString();

        // 未入力がある場合
        if (StringUtil.isNullOrEmpty(userName) ||
            StringUtil.isNullOrEmpty(userPassword)) {
            toast(R.string.error_input_all);
            return;
        }

        // ログイン処理を行う。
        mUserName = userName;
        mUserPassword = userPassword;
        login();

        mLogger.d("OUT(OK)");
    }

    private void login() {
        mLogger.d("IN");

        // ログイン処理を行う。
        // パラメータを生成する。
        Bundle params = new Bundle();
        params.putString(ParamKey.URL,           Url.LOGIN);
        params.putString(ParamKey.USER_NAME,     mUserName);
        params.putString(ParamKey.USER_PASSWORD, mUserPassword);

        // ログインローダーをロードする。
        getActivity().getSupportLoaderManager().restartLoader(
                LoaderId.LOGIN, params, new HttpPostLoaderCallbacks(
                        getActivity(), new LoginOnReceiveResponseListener()));

        mLogger.d("OUT(OK)");
    }

    /**
     * ユーザ登録ボタンの処理を行う。
     */
    private void doRegistUserButton() {
        mLogger.d("IN");

        String userName = mUserNameValue.getText().toString();
        String password = mPasswordValue.getText().toString();

        if (StringUtil.isNullOrEmpty(userName) ||
            StringUtil.isNullOrEmpty(password)) {
            toast(R.string.error_input_all);
            return;
        }

        // ユーザ登録処理を行う。
        // パラメータを生成する。
        Bundle params = new Bundle();
        params.putString(ParamKey.URL,           Url.REGIST_USER);
        params.putString(ParamKey.USER_NAME,     userName);
        params.putString(ParamKey.USER_PASSWORD, password);

        // ユーザ登録ローダーを表示する。
        getActivity().getSupportLoaderManager().restartLoader(
                LoaderId.REGIST_USER, params, new HttpPostLoaderCallbacks(
                        getActivity(), new RegistUserOnReceiveResponseListener()));

        mLogger.d("OUT(OK)");
    }

    /**
     * 終了ボタンの処理を行う。
     */
    private void doEndButton() {
        mLogger.d("IN");

        // 終了する。
        dismiss();
        getActivity().finish();

        mLogger.d("OUT(OK)");
    }

    /**************************************************************************/
    /**
     * ログインレスポンス受信リスナークラス
     *
     */
    private class LoginOnReceiveResponseListener implements OnReceiveResponseListener {

        /** ロガー */
        private Logger mLogger = new Logger(LoginOnReceiveResponseListener.class);

        /**
         * レスポンス受信した時に呼び出される。
         *
         * @param response レスポンス文字列
         * @param bundle バンドル
         */
        @Override
        public void onReceive(String response, Bundle bundle) {
            mLogger.d("IN response=[" + response + "]");

            // ユーザ名とパスワードをクリアする。
            mUserName = "";
            mUserPassword = "";

            try {
                JSONObject json = new JSONObject(response);

                // ステータスを取得する。
                boolean status = json.getBoolean(ParamKey.STATUS);

                // ログイン成功の場合
                if (status) {
                    // 次回ログイン済みがチェックされている場合
                    if (mLoggedCheckBox.isChecked()) {
                        // ログイン情報を保存する。
                        Editor editor = mPrefs.edit();
                        editor.putBoolean(PrefsKey.LOGGED,       true);
                        editor.putString(PrefsKey.USER_NAME,     mUserName);
                        editor.putString(PrefsKey.USER_PASSWORD, mUserPassword);
                        editor.commit();
                    }

                    // ユーザデータを生成する。
                    UserData userData = new UserData();
                    userData.setId(        json.getLong(  ParamKey.USER_ID));
                    userData.setName(      json.getString(ParamKey.USER_NAME));
                    userData.setPassword(  json.getString(ParamKey.USER_PASSWORD));
                    userData.setUpdateTime(json.getLong(  ParamKey.USER_UPDATE_TIME));

                    // ルーム一覧画面を表示する。
                    Intent intent = new Intent(getActivity(), RoomListActivity.class);
                    intent.putExtra(ExtraKey.USER_DATA, userData);
                    startActivity(intent);

                    // 終了する。
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                            getActivity().finish();
                        }
                    });

                // エラーまたはログインできない場合
                } else {
                    toast(R.string.error_login);
                }
            } catch (JSONException e) {
                mLogger.e(e);
                toast(R.string.error_response);
            }

            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * ユーザ登録レスポンス受信リスナークラス
     *
     */
    private class RegistUserOnReceiveResponseListener implements OnReceiveResponseListener {

        /** ロガー */
        private Logger mLogger = new Logger(RegistUserOnReceiveResponseListener.class);

        /**
         * レスポンス受信した時に呼び出される。
         *
         * @param response レスポンス文字列
         * @param bundle バンドル
         */
        @Override
        public void onReceive(String response, Bundle bundle) {
            mLogger.d("IN response=[" + response + "]");

            try {
                JSONObject json = new JSONObject(response);

                // ステータスを取得する。
                boolean status = json.getBoolean(ParamKey.STATUS);

                // 登録成功の場合
                if (status) {
                    // 次回ログイン済みがチェックされている場合
                    if (mLoggedCheckBox.isChecked()) {
                        // ログイン情報を保存する。
                        Editor editor = mPrefs.edit();
                        editor.putBoolean(PrefsKey.LOGGED,       true);
                        editor.putString(PrefsKey.USER_NAME,     mUserName);
                        editor.putString(PrefsKey.USER_PASSWORD, mUserPassword);
                        editor.commit();
                    }

                    // ユーザデータを生成する。
                    UserData userData = new UserData();

                    userData.setId(        json.getLong(  ParamKey.USER_ID));
                    userData.setName(      json.getString(ParamKey.USER_NAME));
                    userData.setPassword(  json.getString(ParamKey.USER_PASSWORD));
                    userData.setUpdateTime(json.getLong(  ParamKey.USER_UPDATE_TIME));

                    // ルーム一覧画面を表示する。
                    Intent intent = new Intent(getActivity(), RoomListActivity.class);
                    intent.putExtra(ExtraKey.USER_DATA, userData);
                    startActivity(intent);

                    // 終了する。
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                            getActivity().finish();
                        }
                    });

                // エラーの場合
                } else {
                    toast(R.string.error_regist_user);
                }
            } catch (JSONException e) {
                mLogger.e(e);
                toast(R.string.error_response);
            }

            mLogger.d("OUT(OK)");
        }
    }
}

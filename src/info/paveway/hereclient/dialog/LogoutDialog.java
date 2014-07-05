package info.paveway.hereclient.dialog;

import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.LoaderId;
import info.paveway.hereclient.CommonConstants.ParamKey;
import info.paveway.hereclient.CommonConstants.Url;
import info.paveway.hereclient.MainActivity;
import info.paveway.hereclient.R;
import info.paveway.hereclient.data.UserData;
import info.paveway.hereclient.loader.HttpPostLoaderCallbacks;
import info.paveway.hereclient.loader.OnReceiveResponseListener;
import info.paveway.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * ここにいるクライアント
 * ログアウトダイアログクラス
 *
 * @version 1.0 新規作成
 *
 */
public class LogoutDialog extends AbstractBaseDialogFragment {

    /** ロガー */
    private Logger mLogger = new Logger(LogoutDialog.class);

    /** ハンドラー */
    private Handler mHandler = new Handler();

    /** ユーザデータ */
    private UserData mUserData;

    /**
     * インスタンスを返却する。
     *
     * @return インスタンス
     */
    public static LogoutDialog newInstance(UserData userData) {
        LogoutDialog instance = new LogoutDialog();
        Bundle args = new Bundle();
        args.putSerializable(ExtraKey.USER_DATA, userData);
        instance.setArguments(args);
        return instance;
    }

    /**
     * 生成した時に呼び出される。
     *
     * @param savedInstanceState 保存した時のインスタンスの状態
     * @return ダイアログ
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mLogger.d("IN");

        mUserData = (UserData)getArguments().getSerializable(ExtraKey.USER_DATA);

        // ログアウトダイアログを設定する。
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_logout_title);
        builder.setMessage(R.string.dialog_logout_message);
        builder.setPositiveButton(R.string.dialog_logout_button, null);
        builder.setNegativeButton(R.string.dialog_cancel_button,  null);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        // ボタン押下でダイアログが閉じないようにリスナーを設定する。
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // ログアウトボタン
                ((AlertDialog)dialog).getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doLogoutButton();
                    }
                });

                // キャンセルボタン
                ((AlertDialog)dialog).getButton(Dialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doCancelButton();
                    }
                });
            }
        });

        mLogger.d("OUT(OK)");
        return dialog;
    }

    /**
     * ログアウトボタンの処理を行う。
     */
    private void doLogoutButton() {
        mLogger.d("IN");

        // ログアウト処理を行う。
        // パラメータを生成する。
        Bundle params = new Bundle();
        params.putString(ParamKey.URL,           Url.LOGOUT);
        params.putString(ParamKey.USER_NAME,     mUserData.getName());
        params.putString(ParamKey.USER_PASSWORD, mUserData.getPassword());

        // ログアウトローダーをロードする。
        getActivity().getSupportLoaderManager().restartLoader(
                LoaderId.LOGOUT, params, new HttpPostLoaderCallbacks(
                        getActivity(), new LogoutOnReceiveResponseListener()));

        mLogger.d("OUT(OK)");
    }

    /**
     * キャンセルボタンの処理を行う。
     */
    private void doCancelButton() {
        mLogger.d("IN");

        dismiss();

        mLogger.d("OUT(OK)");
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
            mLogger.d("IN response=[" + response + "]");

            try {
                JSONObject json = new JSONObject(response);

                // ステータスを取得する。
                boolean status = json.getBoolean(ParamKey.STATUS);

                // ログアウト成功の場合
                if (status) {

                // エラーまたはログインできない場合
                } else {
                    toast(R.string.error_logout);
                }
            } catch (JSONException e) {
                mLogger.e(e);
                toast(R.string.error_response);
            }

            // 終了する。
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                    getActivity().finish();

                    // ログイン画面を表示する。
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

            mLogger.d("OUT(OK)");
        }
    }
}

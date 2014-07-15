package info.paveway.hereclient.dialog;

import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.LoaderId;
import info.paveway.hereclient.CommonConstants.ParamKey;
import info.paveway.hereclient.CommonConstants.Url;
import info.paveway.hereclient.R;
import info.paveway.hereclient.data.UserData;
import info.paveway.hereclient.loader.HttpLoaderCallbacks;
import info.paveway.hereclient.loader.HttpPostLoader;
import info.paveway.hereclient.loader.OnReceiveResponseListener;
import info.paveway.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * ここにいるクライアント
 * ユーザ削除ダイアログクラス
 *
 * @version 1.0 新規作成
 *
 */
public class DeleteUserDialog extends AbstractBaseDialogFragment {

    /** ロガー */
    private Logger mLogger = new Logger(DeleteUserDialog.class);

    /** ユーザデータ */
    private UserData mUserData;

    /**
     * インスタンスを返却する。
     *
     * @return インスタンス
     */
    public static DeleteUserDialog newInstance(UserData userData) {
        DeleteUserDialog instance = new DeleteUserDialog();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_delete_user_title);
        builder.setPositiveButton(R.string.dialog_delete_button, null);
        builder.setNegativeButton(R.string.dialog_cancel_button,  null);
        builder.setMessage(R.string.dialog_delete_user_message);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        // ボタン押下でダイアログが閉じないようにリスナーを設定する。
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // 削除ボタン
                ((AlertDialog)dialog).getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doDeleteButton();
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
     * 削除ボタンの処理を行う。
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void doDeleteButton() {
        mLogger.d("IN");

        // ユーザ削除処理を行う。
        // パラメータを生成する。
        Bundle params = new Bundle();
        params.putString(ParamKey.URL,           Url.DELETE_USER);
        params.putString(ParamKey.USER_NAME,     mUserData.getName());
        params.putString(ParamKey.USER_PASSWORD, mUserData.getPassword());

        // ユーザ削除ローダーをロードする。
        getActivity().getSupportLoaderManager().restartLoader(
                LoaderId.DELETE_USER, params, new HttpLoaderCallbacks(
                        getActivity(), new DeleteUserOnReceiveResponseListener(), HttpPostLoader.class));

        mLogger.d("OUT(OK)");
    }

    /**
     * キャンセルボタンの処理を行う。
     */
    private void doCancelButton() {
        mLogger.d("IN");

        // ダイアログを終了する。
        dismiss();

        mLogger.d("OUT(OK)");
    }

    /**************************************************************************/
    /**
     * ユーザ削除レスポンス受信リスナークラス
     *
     */
    private class DeleteUserOnReceiveResponseListener implements OnReceiveResponseListener {

        /** ロガー */
        private Logger mLogger = new Logger(DeleteUserOnReceiveResponseListener.class);

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

                // 削除成功の場合
                if (status) {
                    getActivity().finish();

                // エラーまたはログインできない場合
                } else {
                    toast(R.string.error_delete_user);
                }
            } catch (JSONException e) {
                mLogger.e(e);
                toast(R.string.error_response);
            }

            mLogger.d("OUT(OK)");
        }
    }
}

package info.paveway.hereclient.dialog;

import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.LoaderId;
import info.paveway.hereclient.CommonConstants.ParamKey;
import info.paveway.hereclient.CommonConstants.Url;
import info.paveway.hereclient.R;
import info.paveway.hereclient.data.RoomData;
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
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * ここにいるクライアント
 * 退室ダイアログクラス
 *
 * @version 1.0 新規作成
 *
 */
public class ExitRoomDialog extends AbstractBaseDialogFragment {

    /** ロガー */
    private Logger mLogger = new Logger(ExitRoomDialog.class);

    /** ハンドラー */
    private Handler mHandler = new Handler();

    /** ユーザデータ */
    private UserData mUserData;

    /** ルームデータ */
    private RoomData mRoomData;

    /**
     * インスタンスを返却する。
     *
     * @return インスタンス
     */
    public static ExitRoomDialog newInstance(UserData userData, RoomData roomData) {
        ExitRoomDialog instance = new ExitRoomDialog();
        Bundle args = new Bundle();
        args.putSerializable(ExtraKey.USER_DATA, userData);
        args.putSerializable(ExtraKey.ROOM_DATA, roomData);
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
        mRoomData = (RoomData)getArguments().getSerializable(ExtraKey.ROOM_DATA);

        // 退室ダイアログを設定する。
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_exit_room_title);
        builder.setMessage(R.string.dialog_exit_room_message);
        builder.setPositiveButton(R.string.dialog_exit_room_button, null);
        builder.setNegativeButton(R.string.dialog_cancel_button,  null);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        // ボタン押下でダイアログが閉じないようにリスナーを設定する。
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // 退室ボタン
                ((AlertDialog)dialog).getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doExitRoomButton();
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
     * 退室ボタンの処理を行う。
     */
    private void doExitRoomButton() {
        mLogger.d("IN");

        // 退室処理を行う。
        // パラメータを生成する。
        Bundle params = new Bundle();
        params.putString(ParamKey.URL,       Url.EXIT_ROOM);
        params.putString(ParamKey.ROOM_NAME, mRoomData.getName());
        params.putString(ParamKey.ROOM_KEY,  mRoomData.getPassword());
        params.putString(ParamKey.USER_ID,   String.valueOf(mUserData.getId()));
        params.putString(ParamKey.USER_NAME, mUserData.getName());

        // 退室ローダーをロードする。
        getActivity().getSupportLoaderManager().restartLoader(
                LoaderId.EXIT_ROOM, params, new HttpPostLoaderCallbacks(
                        getActivity(), new ExitRoomOnReceiveResponseListener()));

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
            mLogger.d("IN response=[" + response + "]");

            try {
                JSONObject json = new JSONObject(response);

                // ステータスを取得する。
                boolean status = json.getBoolean(ParamKey.STATUS);

                // 退室成功の場合
                if (status) {

                // エラーまたは退室できない場合
                } else {
                    toast(R.string.error_exit_room);
                }
            } catch (JSONException e) {
                mLogger.e(e);
                toast(R.string.error_response);
            }

            // 終了する。
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // ダイアログを終了する。
                    dismiss();

                    // 呼び出し元画面を終了する。
                    getActivity().finish();
                }
            });

            mLogger.d("OUT(OK)");
        }
    }
}

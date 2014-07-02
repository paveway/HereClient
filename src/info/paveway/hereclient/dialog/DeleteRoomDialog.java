package info.paveway.hereclient.dialog;

import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.HttpKey;
import info.paveway.hereclient.CommonConstants.LoaderId;
import info.paveway.hereclient.CommonConstants.Url;
import info.paveway.hereclient.data.RoomData;
import info.paveway.hereclient.data.UserData;
import info.paveway.hereclient.loader.DeleteUserLoaderCallbacks;
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
import android.widget.Toast;

/**
 * ここにいるクライアント
 * ユーザ削除ダイアログクラス
 *
 * @version 1.0 新規作成
 *
 */
public class DeleteRoomDialog extends AbstractBaseDialogFragment {

    /** ロガー */
    private Logger mLogger = new Logger(DeleteRoomDialog.class);

    /** ユーザデータ */
    private UserData mUserData;

    /** ルームデータ */
    private RoomData mRoomData;

    /**
     * インスタンスを返却する。
     *
     * @return インスタンス
     */
    public static DeleteRoomDialog newInstance(UserData userData, RoomData roomData) {
        DeleteRoomDialog instance = new DeleteRoomDialog();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ルーム削除");
        builder.setPositiveButton("削除", null);
        builder.setNegativeButton("キャンセル",  null);
        builder.setMessage("「" + mRoomData.getName() + "」ルームを削除しますか");
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
    private void doDeleteButton() {
        mLogger.d("IN");

        // ルーム削除処理を行う。
        // パラメータを生成する。
        Bundle params = new Bundle();
        params.putString(HttpKey.URL,           Url.DELETE_ROOM);
        params.putString(HttpKey.ROOM_NAME,     String.valueOf(mRoomData.getId()));
        params.putString(HttpKey.ROOM_KEY,      mRoomData.getPassword());
        params.putString(HttpKey.OWNER_ID,      String.valueOf(mUserData.getId()));
        params.putString(HttpKey.OWNER_NAME,    mUserData.getName());

        // ユーザ削除ローダーをロードする。
        getActivity().getSupportLoaderManager().restartLoader(
                LoaderId.DELETE_USER, params, new DeleteUserLoaderCallbacks(
                        getActivity(), new DeleteUserOnReceiveResponseListener()));

        mLogger.d("OUT(OK)");
    }

    /**
     * キャンセルボタンの処理を行う。
     */
    private void doCancelButton() {
        mLogger.d("IN");

        dismiss();
//        getActivity().finish();

        mLogger.d("OUT(OK)");
    }

    /**************************************************************************/
    /**
     * ログインレスポンス受信リスナークラス
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
                boolean status = json.getBoolean(HttpKey.STATUS);

                // 削除成功の場合
                if (status) {
                    getActivity().finish();

                // エラーまたはログインできない場合
                } else {
                    Toast.makeText(getActivity(), "ユーザを削除できませんでした", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                mLogger.e(e);
                Toast.makeText(getActivity(), "エラーが発生しました", Toast.LENGTH_SHORT).show();
            }

            mLogger.d("OUT(OK)");
        }
    }
}

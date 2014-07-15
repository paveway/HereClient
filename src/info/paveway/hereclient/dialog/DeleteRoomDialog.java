package info.paveway.hereclient.dialog;

import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.LoaderId;
import info.paveway.hereclient.CommonConstants.ParamKey;
import info.paveway.hereclient.CommonConstants.Url;
import info.paveway.hereclient.R;
import info.paveway.hereclient.RoomListActivity;
import info.paveway.hereclient.data.RoomData;
import info.paveway.hereclient.data.UserData;
import info.paveway.hereclient.loader.HttpLoaderCallbacks;
import info.paveway.hereclient.loader.HttpPostLoader;
import info.paveway.log.Logger;
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
        builder.setTitle(R.string.dialog_delete_room_title);
        builder.setPositiveButton(R.string.dialog_delete_button, null);
        builder.setNegativeButton(R.string.dialog_cancel_button,  null);
        String message =
                getResourceString(R.string.dialog_delete_room_message_prefix) +
                mRoomData.getName() +
                getResourceString(R.string.dialog_delete_room_message_suffix);
        builder.setMessage(message);
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

        // ルーム削除処理を行う。
        // パラメータを生成する。
        Bundle params = new Bundle();
        params.putString(ParamKey.URL,       Url.DELETE_ROOM);
        params.putString(ParamKey.ROOM_NAME, mRoomData.getName());
        params.putString(ParamKey.ROOM_KEY,  mRoomData.getPassword());
        params.putString(ParamKey.USER_ID,   String.valueOf(mUserData.getId()));
        params.putString(ParamKey.USER_NAME, mUserData.getName());

        // ルーム削除ローダーをロードする。
        getActivity().getSupportLoaderManager().restartLoader(
                LoaderId.DELETE_USER, params, new HttpLoaderCallbacks(
                        getActivity(), ((RoomListActivity)getActivity()).new DeleteRoomOnReceiveResponseListener(), HttpPostLoader.class));

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
}

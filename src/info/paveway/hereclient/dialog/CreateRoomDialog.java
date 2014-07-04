package info.paveway.hereclient.dialog;

import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.LoaderId;
import info.paveway.hereclient.CommonConstants.ParamKey;
import info.paveway.hereclient.CommonConstants.Url;
import info.paveway.hereclient.R;
import info.paveway.hereclient.RoomListActivity;
import info.paveway.hereclient.data.UserData;
import info.paveway.hereclient.loader.HttpPostLoaderCallbacks;
import info.paveway.log.Logger;
import info.paveway.util.StringUtil;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * ここにいるクライアント
 * ルーム作成ダイアログクラス
 *
 * @version 1.0 新規作成
 *
 */
public class CreateRoomDialog extends AbstractBaseDialogFragment {

    /** ロガー */
    private Logger mLogger = new Logger(CreateRoomDialog.class);

    /** ユーザデータ */
    private UserData mUserData;

    /** ルーム名入力 */
    private EditText mRoomNameValue;

    /** ルームキー入力 */
    private EditText mRoomKeyValue;

    /**
     * インスタンスを返却する。
     *
     * @param userData ユーザデータ
     * @return インスタンス
     */
    public static CreateRoomDialog newInstance(UserData userData) {
        CreateRoomDialog instance = new CreateRoomDialog();
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

        // 引数のユーザデータを取得する。
        mUserData = (UserData)getArguments().getSerializable(ExtraKey.USER_DATA);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_create_room, null);

        mRoomNameValue = (EditText)rootView.findViewById(R.id.roomNameValue);
        mRoomKeyValue  = (EditText)rootView.findViewById(R.id.roomKeyValue);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("新規ルーム作成");
        builder.setPositiveButton("作成", null);
        builder.setNegativeButton("キャンセル",  null);
        builder.setView(rootView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        // ボタン押下でダイアログが閉じないようにリスナーを設定する。
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // 作成ボタン
                ((AlertDialog)dialog).getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doCreateRoomButton();
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
     * 作成ボタンの処理を行う。
     */
    private void doCreateRoomButton() {
        mLogger.d("IN");

        String roomName = mRoomNameValue.getText().toString();
        String roomKey  = mRoomKeyValue.getText().toString();

        if (StringUtil.isNullOrEmpty(roomName) ||
            StringUtil.isNullOrEmpty(roomKey)) {
            Toast.makeText(getActivity(), "すべて入力してください", Toast.LENGTH_SHORT).show();
            return;
        }

        // ルーム作成処理を行う。
        // パラメータを生成する。
        Bundle params = new Bundle();
        params.putString(ParamKey.URL,        Url.CREATE_ROOM);
        params.putString(ParamKey.ROOM_NAME,  roomName);
        params.putString(ParamKey.ROOM_KEY,   roomKey);
        params.putString(ParamKey.OWNER_ID,   String.valueOf(mUserData.getId()));
        params.putString(ParamKey.OWNER_NAME, mUserData.getName());

        // ルーム作成ローダーをロードする。
        getActivity().getSupportLoaderManager().restartLoader(
                LoaderId.NEW_ROOM, params, new HttpPostLoaderCallbacks(
                        getActivity(), ((RoomListActivity)getActivity()).new CreateRoomOnReceiveResponseListener()));

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

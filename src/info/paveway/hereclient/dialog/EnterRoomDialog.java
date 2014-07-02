package info.paveway.hereclient.dialog;

import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.HttpKey;
import info.paveway.hereclient.CommonConstants.LoaderId;
import info.paveway.hereclient.CommonConstants.Url;
import info.paveway.hereclient.MapActivity;
import info.paveway.hereclient.R;
import info.paveway.hereclient.data.RoomData;
import info.paveway.hereclient.loader.EnterRoomLoaderCallbacks;
import info.paveway.hereclient.loader.OnReceiveResponseListener;
import info.paveway.log.Logger;
import info.paveway.util.StringUtil;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ここにいるクライアント
 * 入室ダイアログクラス
 *
 * @version 1.0 新規作成
 */
public class EnterRoomDialog extends AbstractBaseDialogFragment {

    /** ロガー */
    private Logger mLogger = new Logger(EnterRoomDialog.class);

    /** ルームデータ */
    private RoomData mRoomData;

    /** ルームキー入力 */
    private EditText mRoomKeyValue;

    /**
     * インスタンスを返却する。
     *
     * @return インスタンス
     */
    public static EnterRoomDialog newInstance(RoomData roomData) {
        EnterRoomDialog instance = new EnterRoomDialog();
        Bundle args = new Bundle();
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

        mRoomData = (RoomData)getArguments().getSerializable(ExtraKey.ROOM_DATA);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_enter_room, null);

        ((TextView)rootView.findViewById(R.id.roomNameValue)).setText(mRoomData.getName());
        mRoomKeyValue = (EditText)rootView.findViewById(R.id.roomKeyValue);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("入室");
        builder.setPositiveButton("入室", null);
        builder.setNegativeButton("キャンセル",  null);
        builder.setView(rootView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        // ボタン押下でダイアログが閉じないようにリスナーを設定する。
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog)dialog).getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doEnterRoomButton();
                    }
                });

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
     * 入室ボタンの処理を行う。
     */
    private void doEnterRoomButton() {
        mLogger.d("IN");

        String roomKey = mRoomKeyValue.getText().toString();

        if (StringUtil.isNullOrEmpty(roomKey)) {
            Toast.makeText(getActivity(), "ルームキーが未入力です", Toast.LENGTH_SHORT).show();
            return;
        }

        // ログイン処理を行う。
        // パラメータを生成する。
        Bundle params = new Bundle();
        params.putString(HttpKey.URL,       Url.ENTER_ROOM);
        params.putString(HttpKey.ROOM_NAME, mRoomData.getName());
        params.putString(HttpKey.ROOM_KEY,  roomKey);

        // 入室ローダーをロードする。
        getActivity().getSupportLoaderManager().restartLoader(
                LoaderId.ENTER_ROOM, params, new EnterRoomLoaderCallbacks(
                        getActivity(), new EnterRoomOnReceiveResponseListener()));

        // 終了する。
        dismiss();

        mLogger.d("OUT(OK)");
    }

    /**
     * キャンセルボタンの処理を行う。
     */
    private void doCancelButton() {
        mLogger.d("IN");

        // 終了する。
        dismiss();

        mLogger.d("OUT(OK)");
    }

    /**************************************************************************/
    /**
     * 入室レスポンス受信リスナークラス
     *
     */
    private class EnterRoomOnReceiveResponseListener implements OnReceiveResponseListener {

        /** ロガー */
        private Logger mLogger = new Logger(EnterRoomOnReceiveResponseListener.class);

        /**
         * レスポンス受信した時に呼び出される。
         *
         * @param response レスポンス文字列
         * @param bundle バンドル
         */
        @Override
        public void onReceive(String response, Bundle bundle) {
            mLogger.d("IN response=[" + response + "]");

            // マップ画面を表示する。
            RoomData roomData = new RoomData();
            Intent intent = new Intent(getActivity(), MapActivity.class);
            intent.putExtra(ExtraKey.ROOM_DATA, roomData);
            startActivity(intent);

            mLogger.d("OUT(OK)");
        }
    }
}
package info.paveway.hereclient.dialog;

import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.LoaderId;
import info.paveway.hereclient.CommonConstants.ParamKey;
import info.paveway.hereclient.CommonConstants.PrefsKey;
import info.paveway.hereclient.CommonConstants.Url;
import info.paveway.hereclient.MapActivity;
import info.paveway.hereclient.R;
import info.paveway.hereclient.data.RoomData;
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
import android.widget.TextView;

/**
 * ここにいるクライアント
 * 入室ダイアログクラス
 *
 * @version 1.0 新規作成
 */
public class EnterRoomDialog extends AbstractBaseDialogFragment {

    /** ロガー */
    private Logger mLogger = new Logger(EnterRoomDialog.class);

    /** ハンドラー */
    private Handler mHandler = new Handler();

    /** プリフェレンス */
    private SharedPreferences mPrefs;

    /** ユーザデータ */
    private UserData mUserData;

    /** ルームデータ */
    private RoomData mRoomData;

    /** ルームキー入力 */
    private EditText mRoomKeyValue;

    /** 入室済みチェックボックス */
    private CheckBox mEnteredRoomCheckBox;

    /** ルームキー */
    private String mRoomKey;

    /**
     * インスタンスを返却する。
     *
     * @param userData ユーザデータ
     * @param roomData ルームデータ
     * @return インスタンス
     */
    public static EnterRoomDialog newInstance(UserData userData, RoomData roomData) {
        EnterRoomDialog instance = new EnterRoomDialog();
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
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mLogger.d("IN");

        // 設定値を取得する。
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean enteredRoom = mPrefs.getBoolean(PrefsKey.ENTERED_ROOM, false);
        String roomKey = mPrefs.getString(PrefsKey.ROOM_KEY, "");

        // 入室済みフラグまたはルームキーが未設定の場合
        if (!enteredRoom || StringUtil.isNullOrEmpty(roomKey)) {
            // 設定値をクリアする。
            Editor editor = mPrefs.edit();
            editor.putBoolean(PrefsKey.ENTERED_ROOM, false);
            editor.putString(PrefsKey.ROOM_KEY, "");
            editor.commit();

         // すべて設定されている場合
        } else {
            // 入室処理を行う。
            mRoomKey = roomKey;
            enterRoom();
        }

        // ユーザデータ、ルームデータを取得する。
        mUserData = (UserData)getArguments().getSerializable(ExtraKey.USER_DATA);
        mRoomData = (RoomData)getArguments().getSerializable(ExtraKey.ROOM_DATA);

        // ルートビューを取得する。
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_enter_room, null);

        // ルーム名を設定する。
        ((TextView)rootView.findViewById(R.id.roomNameValue)).setText(mRoomData.getName());

        // ルームキー入力を取得する。
        mRoomKeyValue = (EditText)rootView.findViewById(R.id.roomKeyValue);

        // 次回入室済みチェックボックスを取得する。
        mEnteredRoomCheckBox = (CheckBox)rootView.findViewById(R.id.enteredRoomCheckBox);

        // 入室ダイアログを生成する。
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_enter_room_title);
        builder.setPositiveButton(R.string.dialog_enter_room_button, null);
        builder.setNegativeButton(R.string.dialog_cancel_button,  null);
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

        // 入力値を取得する。
        String roomKey = mRoomKeyValue.getText().toString();

        // 未入力の場合
        if (StringUtil.isNullOrEmpty(roomKey)) {
            // 終了する。
            toast(R.string.error_input_room_key);
            mLogger.w("OUT(NG)");
            return;
        }

        // 入室処理を行う。
        mRoomKey = roomKey;
        enterRoom();

        mLogger.d("OUT(OK)");
    }

    /**
     * 入室処理を行う。
     */
    private void enterRoom() {
        // パラメータを生成する。
        Bundle params = new Bundle();
        params.putString(ParamKey.URL,       Url.ENTER_ROOM);
        params.putString(ParamKey.ROOM_NAME, mRoomData.getName());
        params.putString(ParamKey.ROOM_KEY,  mRoomKey);
        params.putString(ParamKey.USER_ID,   String.valueOf(mUserData.getId()));
        params.putString(ParamKey.USER_NAME, mUserData.getName());

        // 入室ローダーをロードする。
        getActivity().getSupportLoaderManager().restartLoader(
                LoaderId.ENTER_ROOM, params, new HttpPostLoaderCallbacks(
                        getActivity(), new EnterRoomOnReceiveResponseListener()));
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

            try {
                JSONObject json = new JSONObject(response);

                // ステータスを取得する。
                boolean status = json.getBoolean(ParamKey.STATUS);

                // 登録成功の場合
                if (status) {
                    // 次回入室済みがチェックされていない場合
                    boolean enteredRoom = true;
                    if (!mEnteredRoomCheckBox.isChecked()) {
                        // 入室情報をクリアする。
                        enteredRoom = false;
                        mRoomKey = "";
                    }
                    // 入室情報を保存する。
                    Editor editor = mPrefs.edit();
                    editor.putBoolean(PrefsKey.ENTERED_ROOM, enteredRoom);
                    editor.putString( PrefsKey.ROOM_KEY,     mRoomKey);
                    editor.commit();

                    // マップ画面を表示する。
                    Intent intent = new Intent(getActivity(), MapActivity.class);
                    intent.putExtra(ExtraKey.USER_DATA, mUserData);
                    intent.putExtra(ExtraKey.ROOM_DATA, mRoomData);
                    startActivity(intent);

                    // 終了する。
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // ダイアログを終了する。
                            dismiss();
                        }
                    });

            // エラーの場合
            } else {
                toast(R.string.error_enter_room);
            }
        } catch (JSONException e) {
            mLogger.e(e);
            toast(R.string.error_response);
        }

            mLogger.d("OUT(OK)");
        }
    }
}

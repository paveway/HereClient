package info.paveway.hereclient.dialog;

import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.LoaderId;
import info.paveway.hereclient.CommonConstants.MemoRangeValue;
import info.paveway.hereclient.CommonConstants.ParamKey;
import info.paveway.hereclient.CommonConstants.Url;
import info.paveway.hereclient.R;
import info.paveway.hereclient.data.RoomData;
import info.paveway.hereclient.data.UserData;
import info.paveway.hereclient.loader.HttpPostLoaderCallbacks;
import info.paveway.hereclient.loader.OnReceiveResponseListener;
import info.paveway.log.Logger;
import info.paveway.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * ここにいるクライアント
 * メモ編集ダイアログクラス
 *
 * @version 1.0 新規作成
 */
public class EditMemoDialog extends AbstractBaseDialogFragment {

    /** ロガー */
    private Logger mLogger = new Logger(EditMemoDialog.class);

    /** ハンドラー */
    private Handler mHandler = new Handler();

    /** ユーザデータ */
    private UserData mUserData;

    /** ルームデータ */
    private RoomData mRoomData;

    /** メモ緯度 */
    private double mMemoLatitude;

    /** メモ経度 */
    private double mMemoLongitude;

    /** タイトル */
    private EditText mMemoTitleValue;

    /** 内容 */
    private EditText mMemoContentValue;

    /** 公開範囲 */
    private RadioGroup mRangeRadioGroup;

    /**
     * インスタンスを返却する。
     *
     * @return インスタンス
     */
    public static EditMemoDialog newInstance(UserData userData, RoomData roomData, Double latitude, Double longitude) {
        EditMemoDialog instance = new EditMemoDialog();
        Bundle args = new Bundle();
        args.putSerializable(ExtraKey.USER_DATA,      userData);
        args.putSerializable(ExtraKey.ROOM_DATA,      roomData);
        args.putDouble(      ExtraKey.MEMO_LATITUDE,  latitude);
        args.putDouble(      ExtraKey.MEMO_LONGITUDE, longitude);
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

        mUserData      = (UserData)getArguments().getSerializable(ExtraKey.USER_DATA);
        mRoomData      = (RoomData)getArguments().getSerializable(ExtraKey.ROOM_DATA);
        mMemoLatitude  = (Double)getArguments().getDouble(        ExtraKey.MEMO_LATITUDE);
        mMemoLongitude = (Double)getArguments().getDouble(        ExtraKey.MEMO_LONGITUDE);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_edit_memo, null);

        mMemoTitleValue   = (EditText)rootView.findViewById(R.id.memoTitleValue);
        mMemoContentValue = (EditText)rootView.findViewById(R.id.memoContentValue);
        mRangeRadioGroup  = (RadioGroup)rootView.findViewById(R.id.rangeRadioGroup);
        ((RadioButton)rootView.findViewById(R.id.rangeRadioButton1)).setChecked(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_memo_title);
        builder.setPositiveButton(R.string.dialog_create_button, null);
        builder.setNegativeButton(R.string.dialog_cancel_button,  null);
        builder.setView(rootView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        // ボタン押下でダイアログが閉じないようにリスナーを設定する。
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // 作成ボタンの場合
                ((AlertDialog)dialog).getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doCreateMemoButton();
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
     * 作成ボタンの処理を行う。
     */
    private void doCreateMemoButton() {
        mLogger.d("IN");

        String memoTitle       = mMemoTitleValue.getText().toString();
        String memoContent     = mMemoContentValue.getText().toString();
        int rangeRadioButtonId = mRangeRadioGroup.getCheckedRadioButtonId();

        if (StringUtil.isNullOrEmpty(memoTitle)) {
            toast(R.string.error_input_all);
            return;
        }

        int memoRangeValue = 0;
        switch (rangeRadioButtonId) {
        case R.id.rangeRadioButton1:
            memoRangeValue = MemoRangeValue.SELF;
            break;

        case R.id.rangeRadioButton2:
            memoRangeValue = MemoRangeValue.MEMBER;
            break;

        case R.id.rangeRadioButton3:
            memoRangeValue = MemoRangeValue.ALL;
            break;
        }

        // メモ編集処理を行う。
        // パラメータを生成する。
        Bundle params = new Bundle();
        params.putString(ParamKey.URL,          Url.EDIT_MEMO);
        params.putString(ParamKey.ROOM_ID,      String.valueOf(mRoomData.getId()));
        params.putString(ParamKey.ROOM_NAME,                   mRoomData.getName());
        params.putString(ParamKey.USER_ID,      String.valueOf(mUserData.getId()));
        params.putString(ParamKey.USER_NAME,                   mUserData.getName());
        params.putString(ParamKey.MEMO_TITLE,                  memoTitle);
        params.putString(ParamKey.MEMO_CONTENT,                memoContent);
        params.putString(ParamKey.MEMO_RANGE,   String.valueOf(memoRangeValue));
        params.putString(ParamKey.LATITUDE,     String.valueOf(mMemoLatitude));
        params.putString(ParamKey.LONGITUDE,    String.valueOf(mMemoLongitude));

        // メモ編集ローダーをロードする。
        getActivity().getSupportLoaderManager().restartLoader(
                LoaderId.EDIT_MEMO, params, new HttpPostLoaderCallbacks(
                        getActivity(), new EditMemoOnReceiveResponseListener()));

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
     * メモ編集レスポンス受信リスナークラス
     *
     */
    private class EditMemoOnReceiveResponseListener implements OnReceiveResponseListener {

        /** ロガー */
        private Logger mLogger = new Logger(EditMemoOnReceiveResponseListener.class);

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
                    // ダイアログを閉じる。
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                        }
                    });

                // 登録できない場合
                } else {
                    toast(R.string.error_edit_memo);
                }
            } catch (JSONException e) {
                mLogger.e(e);
                toast(R.string.error_response);
            }

            mLogger.d("OUT(OK)");
        }
    }
}

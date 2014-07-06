package info.paveway.hereclient.dialog;

import info.paveway.hereclient.CommonConstants.ExtraKey;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressStatusDialog extends DialogFragment {

    private static ProgressDialog mProgressDialog = null;

    // インスタンス生成はこれを使う
    public static ProgressStatusDialog newInstance(String title, String message) {
        ProgressStatusDialog instance = new ProgressStatusDialog();

        // ダイアログにパラメータを渡す
        Bundle arguments = new Bundle();
        arguments.putString(ExtraKey.PROGRESS_TITLE, title);
        arguments.putString(ExtraKey.PROGRESS_MESSAGE, message);
        instance.setArguments(arguments);

        return instance;
    }

    // ProgressDialog作成
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        if (mProgressDialog != null) {
            return mProgressDialog;
        }

        // パラメータを取得
        String title = getArguments().getString(ExtraKey.PROGRESS_TITLE);
        String message = getArguments().getString(ExtraKey.PROGRESS_MESSAGE);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setCancelable(false);

        return mProgressDialog;
    }

    // progressDialog取得
    @Override
    public Dialog getDialog(){
        return mProgressDialog;
    }

    // ProgressDialog破棄
    @Override
    public void onDestroy(){
        super.onDestroy();
        mProgressDialog = null;
    }
}

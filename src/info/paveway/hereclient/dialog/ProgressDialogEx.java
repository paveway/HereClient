package info.paveway.hereclient.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogEx extends DialogFragment {

    private static ProgressDialog progressDialog = null;

    // インスタンス生成はこれを使う
    public static ProgressDialogEx newInstance(String title, String message) {
        ProgressDialogEx instance = new ProgressDialogEx();

        // ダイアログにパラメータを渡す
        Bundle arguments = new Bundle();
        arguments.putString("title", title);
        arguments.putString("message", message);
        instance.setArguments(arguments);

        return instance;
    }

    // ProgressDialog作成
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        if (progressDialog != null) {
            return progressDialog;
        }

        // パラメータを取得
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setCancelable(false);

        return progressDialog;
    }

    // progressDialog取得
    @Override
    public Dialog getDialog(){
        return progressDialog;
    }

    // ProgressDialog破棄
    @Override
    public void onDestroy(){
        super.onDestroy();
        progressDialog = null;
    }
}

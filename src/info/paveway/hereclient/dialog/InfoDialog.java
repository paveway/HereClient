package info.paveway.hereclient.dialog;

import info.paveway.hereclient.R;
import info.paveway.log.Logger;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

/**
 * ここにいるクライアント
 * 情報ダイアログクラス
 *
 * @version 1.0 新規作成
 */
public class InfoDialog extends AbstractBaseDialogFragment {

    /** ロガー */
    private Logger mLogger = new Logger(InfoDialog.class);

    /**
     * インスタンスを生成する。
     *
     * @return インスタンス
     */
    public static InfoDialog newInstance() {
        InfoDialog instance = new InfoDialog();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.info_dialog_title);
        String message =
                getResourceString(R.string.info_dialog_message_prefix) +
                getVersionName() +
                getResourceString(R.string.info_dialog_message_suffix);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.info_dialog_positive_button, null);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        mLogger.d("OUT(OUT)");
        return dialog;
    }

    /**
     * バージョン名を取得する。
     *
     * @return バージョン名
     */
    private String getVersionName() {
        String versionName = "";
        PackageManager packageManager = getActivity().getPackageManager();
        try {
            PackageInfo packageInfo =
                packageManager.getPackageInfo(getActivity().getPackageName(), PackageManager.GET_ACTIVITIES);
            versionName = packageInfo.versionName;
        } catch (NameNotFoundException e) {
            mLogger.e(e);
        }

        return versionName;
    }
}

package info.paveway.hereclient.dialog;

import info.paveway.log.Logger;
import android.app.Dialog;
import android.os.Bundle;

/**
 * Dialogを外からセットできるエラー表示用DialogFragment
 */
public class ErrorDialogFragment extends AbstractBaseDialogFragment {

    /** ロガー */
    private Logger mLogger = new Logger(ErrorDialogFragment.class);

    /** タグ */
    public static final String TAG = ErrorDialogFragment.class.getSimpleName();

    /** ダイアログ */
    private Dialog mDialog;

    /*
     * コンストラクタ
     * Dialogインスタンスの初期化だけを行う。
     */
    public ErrorDialogFragment() {
        super();
        mLogger.d("IN");

        mDialog = null;

        mLogger.d("OUT(OK)");
    }

    /**
     * ダイアログを設定する。
     *
     * @param dialog ダイアログ
     */
    public void setDialog(Dialog dialog) {
        mLogger.d("IN");

        mDialog = dialog;

        mLogger.d("OUT(OK)");
    }

    /*
     * セット済みのDialogをそのまま使ってダイアログを初期化
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mLogger.d("IN");

        mLogger.d("OUT(OK)");
        return mDialog;
    }
}

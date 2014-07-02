package info.paveway.hereclient.dialog;

import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

/**
 * ここにいるクライアント
 * 抽象ベースダイアログクラス
 *
 * @version 1.0 新規作成
 */
public abstract class AbstractBaseDialogFragment extends DialogFragment {

    /**
     * トースト表示する。
     *
     * @param id 文字列リソースID
     */
    protected void toast(int id) {
        Activity activity = getActivity();
        Toast.makeText(activity, activity.getResources().getString(id), Toast.LENGTH_SHORT).show();
    }

    /**
     * リソース文字列を取得する。
     *
     * @param id リソース文字列ID
     * @return
     */
    protected String getResourceString(int id) {
        return getActivity().getResources().getString(id);
    }
}

package info.paveway.util;

import info.paveway.log.Logger;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtil {

    /** ロガー */
    private static Logger mLogger = new Logger(ServiceUtil.class);

    /**
     * サービスが起動しているかチェックする。
     *
     * @param context コンテキスト
     * @param serviceName チェックするサービスクラス
     * @return チェック結果 true:起動している。 / false:起動していない。
     */
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        mLogger.d("IN");

        ActivityManager activityManager =
                (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        // 起動しているサービスのリストを取得する。
        List<RunningServiceInfo> services =
                activityManager.getRunningServices(Integer.MAX_VALUE);

        // 起動しているサービス数分繰り返す。
        for (RunningServiceInfo info : services) {
            // 位置サービスの場合
            if (serviceClass.getCanonicalName().equals(info.service.getClassName())) {
                mLogger.d("OUT(OK) result=[true]");
                return true;
            }
        }

        mLogger.d("OUT(OK) result=[false]");
        return false;
    }
}

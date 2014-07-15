package info.paveway.hereclient.service;

import info.paveway.hereclient.CommonConstants;
import info.paveway.hereclient.CommonConstants.Action;
import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.PrefsKey;
import info.paveway.log.Logger;
import info.paveway.util.StringUtil;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

/**
 * ここにいるクライアント
 * 位置サービスクラス
 *
 * @version 1.0 新規作成
 *
 */
public class LocationService extends Service {

    /** ロガー */
    private Logger mLogger = new Logger(LocationService.class);

    /** プリフェレンス */
    private SharedPreferences mPrefs;

    /** ロケーションクライアント */
    private LocationClient mLocationClient;

    /** ロケーションリクエスト */
    private LocationRequest mLocationRequest;

    /** ロケーションリスナー */
    private LocationListener mLocationListener;

    /** 設定ブロードキャストレシーバー */
    private SettingsBroadcastReceiver mSettingsReceiver;

    /**
     * バインドした時に呼び出される。
     *
     * @param intent インテント
     * @return バインダー
     */
    @Override
    public IBinder onBind(Intent intent) {
        mLogger.d("IN");

        // バインドは拒否する。

        mLogger.d("OUT(OK)");
        return null;
    }

    /**
     * 生成された時に呼び出される。
     */
    @Override
    public void onCreate() {
        mLogger.d("IN");

        // プリフェレンスを取得する。
        mPrefs = PreferenceManager.getDefaultSharedPreferences(LocationService.this);

        // ロケーションリクエストを生成する。
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        long interval = CommonConstants.DEFAULT_INTERVAL;
        String intervalStr = prefs.getString(PrefsKey.INTERVAL_LIST, String.valueOf(interval));
        if (StringUtil.isNotNullOrEmpty(intervalStr)) {
            interval = Long.parseLong(intervalStr);
        }
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setInterval(interval * CommonConstants.MILLI_SEC);
        mLocationRequest.setFastestInterval(CommonConstants.DEFAULT_INTERVAL * CommonConstants.MILLI_SEC);

        // ロケーションリスナーを生成する。
        mLocationListener = new UserLocationListener();

        // ロケーションクライアントを生成する。
        mLocationClient =
                new LocationClient(
                        LocationService.this,
                        new LocationConnectionCallbacks(),
                        new LocationOnConnectionFailedListener());

        // 接続する。
        mLocationClient.connect();

        // 設定ブロードキャストレシーバーを登録する。
        mSettingsReceiver = new SettingsBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.ACTION_SETTINGS);
        registerReceiver(mSettingsReceiver, filter);

        mLogger.d("OUT(OK)");
    }

    /**
     * 終了する時に呼び出される。
     */
    @Override
    public void onDestroy() {
        mLogger.d("IN");

        // 設定ブロードキャストレシーバーが有効な場合
        if (null != mSettingsReceiver) {
            // 登録を解除する。
            unregisterReceiver(mSettingsReceiver);
        }

        // スーパークラスのメソッドを呼び出す。
        super.onDestroy();

        mLogger.d("OUT(OK)");
    }

    /**************************************************************************/
    /**
     * ロケーション接続コールバッククラス
     *
     */
    private class LocationConnectionCallbacks implements ConnectionCallbacks {

        /** ロガー */
        private Logger mLogger = new Logger(LocationConnectionCallbacks.class);

        /**
         * 接続した時に呼び出される。
         *
         * @param bundle バンドル
         */
        @Override
        public void onConnected(Bundle bundle) {
            mLogger.i("IN");

            // ロケーション更新を開始する。
            startLocationUpdates();

            mLogger.i("OUT(OK)");
        }

        /**
         * 切断した時に呼び出される。
         */
        @Override
        public void onDisconnected() {
            mLogger.i("IN");

            // ロケーション更新を停止する。
            stopLocationUpdates();

            mLogger.i("OUT(OK)");
        }

        /**
         * ロケーション更新を開始する。
         */
        private void startLocationUpdates() {
            mLogger.d("IN");

            // 接続済みの場合
            if (mLocationClient.isConnected()) {
                // ロケーション更新を要求する。
                mLocationClient.requestLocationUpdates(mLocationRequest, mLocationListener);
            }

            mLogger.d("OUT(OK)");
        }

        /**
         * ロケーション更新を停止する。
         */
        private void stopLocationUpdates() {
            mLogger.d("IN");

            // ロケーション更新を解除する。
            mLocationClient.removeLocationUpdates(mLocationListener);

            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * ロケーション接続失敗リスナークラス
     *
     */
    private class LocationOnConnectionFailedListener implements OnConnectionFailedListener {

        /** ロガー */
        private Logger mLogger = new Logger(LocationOnConnectionFailedListener.class);

        /**
         * 接続が失敗した時に呼び出される。
         *
         * @param connectionResult 接続結果
         */
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            mLogger.i("IN");

//            // 解決策がある場合
//            if (connectionResult.hasResolution()) {
//                try {
//                    // エラーを解決してくれるインテントを投げる。
//                    connectionResult.startResolutionForResult(
//                        LocationService.this, RequestCode.CONNECTION_FAILURE_RESOLUTION_REQUEST);
//                } catch (IntentSender.SendIntentException e) {
//                    mLogger.e(e);
//                }
//
//            // 解決策がない場合
//            } else {
//                // 解決策がない場合はエラーダイアログを出します
//                showErrorDialog(
//                    connectionResult.getErrorCode(),
//                    RequestCode.CONNECTION_FAILURE_RESOLUTION_REQUEST);
//            }

            mLogger.i("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * ユーザロケーションリスナークラス
     *
     */
    private class UserLocationListener implements LocationListener {

        /** ロガー */
        private Logger mLogger = new Logger(UserLocationListener.class);

        /**
         * ロケーションが変更された時に呼び出される。
         *
         * @param location ロケーションオブジェクト
         */
        @Override
        public void onLocationChanged(Location location) {
            mLogger.i("IN");

            Intent intent = new Intent();
            intent.setAction(Action.ACTION_LOCATION);
            intent.putExtra(ExtraKey.USER_LATITUDE, location.getLatitude());
            intent.putExtra(ExtraKey.USER_LONGITUDE, location.getLongitude());
            sendBroadcast(intent);

            mLogger.i("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * 設定ブロードキャストレシーバー
     *
     */
    public class SettingsBroadcastReceiver extends BroadcastReceiver {

        /** ロガー */
        private Logger mLogger = new Logger(SettingsBroadcastReceiver.class);

        /**
         * ブロードキャストを受信した時に呼び出される。
         *
         * @param context コンテキスト
         * @param intent インテント
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            mLogger.d("IN");

            // インテントが取得できない場合
            if (null == intent) {
                // 終了する。
                mLogger.w("OUT(NG)");
                return;
            }

            // 更新間隔を取得する。
            long interval = CommonConstants.DEFAULT_INTERVAL;
            String intervalStr = mPrefs.getString(PrefsKey.INTERVAL_LIST, String.valueOf(interval));
            if (StringUtil.isNotNullOrEmpty(intervalStr)) {
                interval = Long.parseLong(intervalStr);
            }

            // 現在の設定と異なる場合
            if (mLocationRequest.getInterval() != interval) {
                // 更新間隔を設定する。
                mLocationRequest.setInterval(interval);
            }

            mLogger.d("OUT(OK)");
        }
    }
}

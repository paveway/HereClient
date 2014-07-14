package info.paveway.hereclient.service;

import info.paveway.hereclient.CommonConstants.Action;
import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.PrefsKey;
import info.paveway.log.Logger;
import android.app.Service;
import android.content.Intent;
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

    /** 更新間隔デフォルト値(秒) */
    private static final long DEFAULT_INTERVAL = 30;

    /** ミリ秒 */
    private static final long MILLI_SEC = 1000;

    /** ロケーションクライアント */
    private LocationClient mLocationClient;

    /** ロケーションリクエスト */
    private LocationRequest mLocationRequest;

    /** ロケーションリスナー */
    private LocationListener mLocationListener;

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

        // ロケーションリクエストを生成する。
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        long interval = prefs.getLong(PrefsKey.INTERVAL_LIST, DEFAULT_INTERVAL);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setInterval(interval * MILLI_SEC);
        mLocationRequest.setFastestInterval(DEFAULT_INTERVAL * MILLI_SEC);

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
}

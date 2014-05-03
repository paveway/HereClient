package info.paveway.hereclient;

import info.paveway.hereclient.CommonConstants.INTERFACE_NAME;
import info.paveway.hereclient.CommonConstants.Key;
import info.paveway.hereclient.CommonConstants.LOADER_ID;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * ココにイル クライアント
 * メイン画面
 *
 * @version 1.0 新規作成
 */
public class MainActivity extends FragmentActivity {

    /** ロガー */
    private Logger mLogger = new Logger(MainActivity.class);

    /** URLキー */
    private static final String URL_KEY = "url";
//    private static final String URL_VALUE = "http://www.drk7.jp/weather/json/27.js";
    /** URL値 */
    private static final String URL_VALUE = "http://here-paveway-info3.appspot.com/setlocation";

    /** ロケーションマネージャー */
    private LocationManager mLocationManager;

    /** ニックネーム値 */
    private EditText mNicknameValue;

    private GoogleMap mGoogleMap;

    /** 開始フラグ */
    private boolean mStartFlg;

    /**
     * 生成された時に呼び出される。
     *
     * @param savedInstanceState 保存した時のインスタンスの状態
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLogger.i("IN");

        // スーパークラスのメソッドを呼び出す。
        super.onCreate(savedInstanceState);

        // レイアウトを設定する。
        setContentView(R.layout.activity_main);

        // ロケーションマネージャーを取得する。
        mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        // ロケーションマネージャーが取得できない場合
        if (null == mLocationManager) {
            // 終了する。
            finish();
            mLogger.w("OUT(NG)");
            return;
        }

        // ニックネーム値を取得する。
        mNicknameValue = (EditText)findViewById(R.id.nicknameValue);

        ((Button)findViewById(R.id.startButton)).setOnClickListener(new ButtonOnClickListener());
        ((Button)findViewById(R.id.stopButton)).setOnClickListener(new ButtonOnClickListener());

        mStartFlg = true;

        // MapFragmentの取得（2）
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        try {
            // マップオブジェクトを取得する（3）
            mGoogleMap = mapFragment.getMap();

            // Activityが初めて生成されたとき（4）
            if (savedInstanceState == null) {

                // フラグメントを保存する（5）
                mapFragment.setRetainInstance(true);

                // 地図の初期設定を行う（6）
                mapInit();
            }
        }
        // GoogleMapが使用できないとき
        catch (Exception e) {
        }


        // 主処理を開始する。
        start();

        mLogger.i("OUT(OK)");
    }

 // 地図の初期設定
    private void mapInit() {

        // 地図タイプを設定する。
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 現在位置ボタンの表示する。
        mGoogleMap.setMyLocationEnabled(true);

//        // 東京駅の位置、ズーム設定（3）
//        CameraPosition camerapos =
//                new CameraPosition.Builder().target(
//                        new LatLng(35.681382, 139.766084)).zoom(15.5f).build();
//
//        // 地図の中心を変更する（4）
//        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));
    }

    /**
     * 主処理を開始する。
     */
    private void start() {
        mLogger.d("IN");

        // プロバイダの選択基準を設定する。
        Criteria criteria = new Criteria();
        criteria.setBearingRequired(false);  // 方位不要
        criteria.setSpeedRequired(false);    // 速度不要
        criteria.setAltitudeRequired(false); // 高度不要

        try {
            // 位置情報の更新を要求する。
            mLocationManager.requestSingleUpdate(criteria, new LocationListenerImpl(), null);
        } catch (Exception e) {
            mLogger.e(e);
        }

        mLogger.d("OUT(OK)");
    }

    /**
     * ロケーションリスナークラス
     *
     */
    private class LocationListenerImpl implements LocationListener {

        private Marker mMarker;

        /**
         * ロケーションが変更された時に呼び出される。
         *
         * @param location ロケーションオブジェクト
         */
        @Override
        public void onLocationChanged(Location location) {
            // 開始の場合
            if (mStartFlg) {
                // ローダーを初期化する。
                Bundle bundle = new Bundle();

                bundle.putString(Key.URL,       URL_VALUE);
                bundle.putString(Key.ID,        MacAddress.getMacAddressString(INTERFACE_NAME.WLAN0));
                bundle.putString(Key.LATITUDE,  String.valueOf(location.getLatitude()));
                bundle.putString(Key.LONGITUDE, String.valueOf(location.getLatitude()));

                if (null != mMarker) {
                    mMarker.remove();
                }

                MarkerOptions options = new MarkerOptions();
                options.position(new LatLng(location.getLatitude(), location.getLongitude()));
                mMarker = mGoogleMap.addMarker(options);

                getSupportLoaderManager().initLoader(LOADER_ID.HTTP, bundle, new HttpLoaderCallbacks(MainActivity.this));
            }
        }

        @Override
        public void onProviderDisabled(String arg0) {}

        @Override
        public void onProviderEnabled(String arg0) {}

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
    }

    /**
     * HTTPローダーコールバッククラス
     *
     */
    private class HttpLoaderCallbacks implements LoaderCallbacks<String> {

        /** コンテキスト */
        private Context mContext;

        /**
         * コンストラクタ
         *
         * @param context コンテキスト
         */
        public HttpLoaderCallbacks(Context context) {
            mContext = context;
        }

        /**
         * ローダーが生成された時に呼び出される。
         */
        @Override
        public Loader<String> onCreateLoader(int id, Bundle bundle) {
            // ローダーを生成する。

            Loader<String> loader =
                    new SetLocationLoader(mContext, bundle);
            loader.forceLoad();
            return loader;
        }

        /**
         * ローダー内の処理が終了した時に呼び出される。
         */
        @Override
        public void onLoadFinished(Loader<String> loader, String body) {
            if (LOADER_ID.HTTP == loader.getId()) {
                if ((null != body) || !"".equals(body)) {
                    Toast.makeText(mContext, "Response=[" + body + "]", Toast.LENGTH_SHORT).show();
                }
            }
        }

        /**
         * ローダーがリセットされた時に呼び出される。
         */
        @Override
        public void onLoaderReset(Loader<String> loader) {
            // 何もしない。
        }
    }

    private class ButtonOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.startButton:
//                mStartFlg = true;
                break;

            case R.id.stopButton:
//                mStartFlg = false;
                break;
            }
        }
    }
}

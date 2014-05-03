package info.paveway.hereclient;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String URL_KEY = "url";
//    private static final String URL_VALUE = "http://www.drk7.jp/weather/json/27.js";
    private static final String URL_VALUE = "http://here-paveway-info3.appspot.com/location";

    private static final int LOADER_ID = 0;

    private LocationManager mLocationManager;

    private TextView mBodyValue;

    /**
     * 生成された時に呼び出される。
     *
     * @param savedInstanceState 保存した時のインスタンスの状態
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // スーパークラスのメソッドを呼び出す。
        super.onCreate(savedInstanceState);

        // レイアウトを設定する。
        setContentView(R.layout.activity_main);

        // LocationManagerのインスタンスを取得する（1）
        mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        if (null == mLocationManager) {
            finish();
            return;
        }

        // ボディ値を取得する。
        mBodyValue = (TextView)findViewById(R.id.bodyValue);

        start();
    }

    private void start() {
        // プロバイダの選択基準を設定する。
        Criteria criteria = new Criteria();
        criteria.setBearingRequired(false);  // 方位不要
        criteria.setSpeedRequired(false);    // 速度不要
        criteria.setAltitudeRequired(false); // 高度不要

        try {
            // 位置情報の更新を要求する。
            mLocationManager.requestSingleUpdate(
                criteria,
                // 位置情報リスナーを生成する。
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        // ローダーを初期化する。
                        Bundle bundle = new Bundle();
                        bundle.putString(URL_KEY,     URL_VALUE);
                        bundle.putString("id", MacAddress.getMacAddressString("wlan0"));
                        bundle.putString("latitude",  String.valueOf(location.getLatitude()));
                        bundle.putString("longitude", String.valueOf(location.getLatitude()));
                        getSupportLoaderManager().initLoader(LOADER_ID, bundle, new HttpLoaderCallbacks(MainActivity.this));
                    }

                    @Override
                    public void onProviderDisabled(String arg0) {}

                    @Override
                    public void onProviderEnabled(String arg0) {}

                    @Override
                    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
                },
                null);
        } catch (Exception e) {
            Log.e("TAG", "", e);
        }
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
                    new HttpAsyncLoader(
                            mContext,
                            bundle.getString(URL_KEY),
                            bundle.getString("id"),
                            bundle.getString("latitude"),
                            bundle.getString("longitude"));
            loader.forceLoad();
            return loader;
        }

        /**
         * ローダー内の処理が終了した時に呼び出される。
         */
        @Override
        public void onLoadFinished(Loader<String> loader, String body) {
            if (LOADER_ID == loader.getId()) {
                if ((null != body) || !"".equals(body)) {
                    mBodyValue.setText(body);

                } else {
                    mBodyValue.setText("データを取得できませんでした");
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
}

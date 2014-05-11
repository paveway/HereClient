package info.paveway.hereclient;

import info.paveway.hereclient.CommonConstants.IntefaceName;
import info.paveway.hereclient.CommonConstants.Key;
import info.paveway.hereclient.CommonConstants.LoaderId;
import info.paveway.hereclient.CommonConstants.RequestCode;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
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

    /** URL値 */
    private static final String URL_VALUE = "http://here-paveway-info3.appspot.com/setlocation";

    /** ID値 */
    private static final String ID = MacAddress.getMacAddressString(IntefaceName.WLAN0);

    /** ロケーションクライアント */
    private LocationClient mLocationClient;

    /** ロケーションリクエスト */
    private LocationRequest mLocationRequest;

    /** ロケーションリスナー */
    private LocationListener mLocationListener;

    /** 位置情報をリクエスト済みかどうか(onPause->onResume対策) */
    boolean mLocationUpdatesRequested = false;

    /** ニックネーム */
    private String mNickname;

    /** Googleマップ */
    private GoogleMap mGoogleMap;

    /** マップ初期化済みフラグ */
    private boolean mInitedMap;

    /** 開始フラグ */
    private boolean mStartFlg;

    /** 処理中フラグ */
    private static boolean mProcessingFlg = false;

    /** マーカーマップ */
    private static Map<String, Marker> mMarkerMap = new HashMap<String, Marker>();

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
        setContentView(R.layout.main_activity);

        // 設定値を読み出す。
        readPreferences();

        // ロケーションリクエストを生成する。
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(30 * 1000);

        // ロケーションリスナーを生成する。
        mLocationListener = new LocationListenerImpl();

        // ロケーションクライアントを生成する。
        mLocationClient =
                new LocationClient(
                        MainActivity.this,
                        new ConnectionCallbacksImpl(),
                        new OnConnectionFailedListenerImpl());

        // 開始切替ボタンにリスナーを設定する。
        ((ToggleButton)findViewById(R.id.startButton)).setOnCheckedChangeListener(new StartOnCheckedChangeListener());

        /** マップ初期化済みフラグをクリアする。 */
        mInitedMap = false;

        // 開始フラグをクリアする。
        mStartFlg = false;

        // マップフラグメントを取得する。
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        try {
            // マップオブジェクトを取得する。
            mGoogleMap = mapFragment.getMap();

            // Activityが初めて生成された場合
            if (savedInstanceState == null) {
                // フラグメントを保存する。
                mapFragment.setRetainInstance(true);

                // 地図の初期設定を行う。
                mapInit();
            }
        } catch (Exception e) {
            mLogger.e(e);
            finish();
            return;
        }

        mLogger.i("OUT(OK)");
    }

    /**
     * 他の画面の呼び出しからの戻った時に呼び出される。
     *
     * @param requestCode 要求コード
     * @param resultCode 結果コード
     * @param intent データ
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        mLogger.i("IN requestCode=[" + requestCode + "] resultCode=[" + resultCode + "]");

        // 要求コードにより処理を判別する。
        switch (requestCode) {
        // 接続エラー解決要求の場合
        case RequestCode.CONNECTION_FAILURE_RESOLUTION_REQUEST:
            // 結果コードにより処理を判別する。
            switch (resultCode) {
            // 正常終了の場合
            case RESULT_OK:
                // 接続済みの場合
                if (servicesConnected()) {
                    // ロケーション更新を開始する。
                    startLocationUpdates();

                    // ロケーション更新を要求済みにする。
                    mLocationUpdatesRequested = true;
                }
                break;

            // 上記以外
            default:
                // 何もしない。
                mLogger.w("Result NG.");
                break;
            }
            break;

        // 設定画面の場合
        case RequestCode.SETTINGS:
            // 設定値を読み出す。
            readPreferences();
            break;

        // 上記以外
        default:
            // 何もしない。
            mLogger.w("Unknown Request Code.");
            break;
        }

        mLogger.i("OUT(OK)");
    }

    /**
     * 開始した時に呼び出される。
     */
    @Override
    protected void onStart() {
        mLogger.i("IN");

        // スーパークラスのメソッドを呼び出す。
        super.onStart();

        // 未接続の場合
        if (!mLocationClient.isConnected() && !mLocationClient.isConnecting()) {
            // ロケーションクライアントを接続する。
            mLocationClient.connect();
        }

        mLogger.i("OUT(OK)");
    }

    /**
     * 再開した時に呼び出される。
     */
    @Override
    protected void onResume() {
        mLogger.i("IN");

        // スーパークラスのメソッドを呼び出す。
        super.onResume();

        // 接続済みの場合
        if (mLocationUpdatesRequested && mLocationClient.isConnected()) {
            // 更新を開始する。
            startLocationUpdates();
        }

        mLogger.i("OUT(OK)");
    }

    /**
     * 一時停止した時に呼び出される。
     */
    @Override
    protected void onPause() {
        mLogger.i("IN");

        // 接続済みの場合
        if (mLocationClient.isConnected()) {
            // 更新を停止する。
            stopLocationUpdates();
        }

        // スーパークラスのメソッドを呼び出す。
        super.onPause();

        mLogger.i("OUT(OK)");
    }

    /**
     * 停止した時に呼び出される。
     */
    @Override
    public void onStop() {
        mLogger.i("IN");

        // 切断する。
        mLocationClient.disconnect();

        // スーパークラスのメソッドを呼び出す。
        super.onStop();

        mLogger.i("OUT(OK)");
    }

    /**
     * メニューを生成した時に呼び出される。
     *
     * @param menu メニュー
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * メニュー項目が選択された時に呼び出される。
     *
     * @param item メニュー項目
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = true;

        // メニュー項目IDにより処理を判別する。
        switch (item.getItemId()) {
        // 設定メニューの場合
        case R.id.settings_menu:
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(intent, RequestCode.SETTINGS);
            break;

        // 上記以外
        default:
            // スーパークラスのメソッドを呼び出す。
            ret = super.onOptionsItemSelected(item);
            break;
        }

        return ret;
    }

    /**
     * キーを押した時に呼び出される。
     *
     * @param keyCode キーコード
     * @param event キーイベント
     * @return 処理結果
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // バックキーではない場合
        if(keyCode != KeyEvent.KEYCODE_BACK){
            // スーパークラスのメソッドを呼び出す。
            return super.onKeyDown(keyCode, event);

        // バックキーの場合
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("終了確認");
            builder.setMessage("終了します");
            builder.setPositiveButton("はい", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            // 処理済みとする。
            return false;
        }
    }

    /**
     * 設定値を読み出す。
     */
    private void readPreferences() {
        mLogger.d("IN");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        mNickname = sharedPreferences.getString("pref_nickname_key", null);
        mLogger.d("Nickname=[" + mNickname + "]");

        mLogger.d("OUT(OK)");
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

    private boolean servicesConnected() {
        mLogger.d("IN");

        // Google Play Servicesが利用可能かどうかチェック
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == resultCode) {
            // Google Play Servicesが利用可能な場合
            mLogger.d("OUT(OK)");
            return true;
        } else {
            // Google Play Servicesが何らかの理由で利用できない場合

            // 解決策が書いてあるダイアログが貰えるので、DialogFragmentで表示する
            showErrorDialog(resultCode, 0);
            mLogger.d("OUT(NG)");
            return false;
        }

    }

    // 地図の初期設定
    private void mapInit() {
        mLogger.d("IN");

        // 地図タイプを設定する。
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 現在位置ボタンの表示する。
        mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListenerImpl());

        mLogger.d("OUT(OK)");
    }

    /**
     * マーカーを設定する。
     *
     * @param id ID
     * @param latitude 緯度
     * @param longitude 経度
     */
    private void setMarker(String id, String nickname, double latitude, double longitude) {
        mLogger.d("IN id=[" + id + "] nickname=[" + nickname + "] latitude=[" + latitude + "] longitude=[" + longitude + "]");

        Marker marker = mMarkerMap.get(id);
        if (null != marker) {
            mLogger.d("Marker exist.");
            marker.remove();
            mMarkerMap.remove(id);

        } else {
            mLogger.d("Marker not exist.");
        }

        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(latitude, longitude));
        if (!((null == nickname) || "".equals(nickname))) {
            options.title(nickname);
        } else {
            options.title(id);
        }
        mMarkerMap.put(id, mGoogleMap.addMarker(options));

        mLogger.d("OUT(OK)");
    }

    /**
     * 接続コールバッククラス
     *
     */
    private class ConnectionCallbacksImpl implements ConnectionCallbacks {

        /** ロガー */
        private Logger mLogger = new Logger(ConnectionCallbacksImpl.class);

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

            // ロケーション更新を要求済みに設定する。
            mLocationUpdatesRequested = true;

            // マップ未初期化の場合
            if (!mInitedMap) {
                Location location = mLocationClient.getLastLocation();

                // 現在位置、ズーム設定を行う。
                CameraPosition camerapos =
                    new CameraPosition.Builder().target(
                        new LatLng(location.getLatitude(), location.getLongitude())).zoom(15.0f).build();

                // 地図の中心を変更する。
                mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));

                // マップ初期化済みとする。
                mInitedMap = true;
            }

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

            // ロケーション更新を未要求に設定する。
            mLocationUpdatesRequested = false;

            mLogger.i("OUT(OK)");
        }
    }

    /**
     * 接続失敗リスナークラス
     *
     */
    private class OnConnectionFailedListenerImpl implements OnConnectionFailedListener {

        /** ロガー */
        private Logger mLogger = new Logger(OnConnectionFailedListenerImpl.class);

        /**
         * 接続が失敗した時に呼び出される。
         *
         * @param connectionResult 接続結果
         */
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            mLogger.i("IN");

            // 解決策がある場合
            if (connectionResult.hasResolution()) {
                try {
                    // エラーを解決してくれるインテントを投げる。
                    connectionResult.startResolutionForResult(MainActivity.this, RequestCode.CONNECTION_FAILURE_RESOLUTION_REQUEST);
                } catch (IntentSender.SendIntentException e) {
                    mLogger.e(e);
                }

            // 解決策がない場合
            } else {
                // 解決策がない場合はエラーダイアログを出します
                showErrorDialog(connectionResult.getErrorCode(), RequestCode.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            }

            mLogger.i("OUT(OK)");
        }
    }

    /**
     * エラーダイアログを表示します。
     *
     * @param errorCode
     *            {@link GooglePlayServicesUtil#isGooglePlayServicesAvailable(android.content.Context)}でもらえたコード
     * @param requestCode
     *            特に使わない場合は0にします
     */
    protected void showErrorDialog(int errorCode, int requestCode) {
        // Google Play servicesからエラーダイアログを受け取る
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, requestCode);

        if (errorDialog != null) {
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();
            errorFragment.setDialog(errorDialog);
            errorFragment.show(getSupportFragmentManager(), ErrorDialogFragment.TAG);
        }
    }

    /**
     * Dialogを外からセットできるエラー表示用DialogFragment
     */
    public static class ErrorDialogFragment extends DialogFragment {

        /** ロガー */
        private Logger mLogger = new Logger(ErrorDialogFragment.class);

        /** タグ */
        public static final String TAG = ErrorDialogFragment.class.getSimpleName();

        /** ダイアログ */
        private Dialog mDialog;

        /*
         * コンストラクタ。Dialogインスタンスの初期化だけを行う。
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

    /**
     * ロケーションリスナークラス
     *
     */
    private class LocationListenerImpl implements LocationListener {

        /** ロガー */
        private Logger mLogger = new Logger(LocationListenerImpl.class);

        /**
         * ロケーションが変更された時に呼び出される。
         *
         * @param location ロケーションオブジェクト
         */
        @Override
        public void onLocationChanged(Location location) {
            mLogger.i("IN");

            // 開始の場合
            if (mStartFlg) {
                mLogger.i("start.");

                // 処理中ではない場合
                if (!mProcessingFlg) {
                    mLogger.i("not processing.");

                    // 処理中に設定する。
                    mProcessingFlg = true;

                    // ローダーを初期化する。
                    Bundle bundle = new Bundle();

                    bundle.putString(Key.URL,       URL_VALUE);
                    bundle.putString(Key.ID,        MacAddress.getMacAddressString(IntefaceName.WLAN0));
                    bundle.putString(Key.NICKNAME,  mNickname);
                    bundle.putString(Key.LATITUDE,  String.valueOf(location.getLatitude()));
                    bundle.putString(Key.LONGITUDE, String.valueOf(location.getLongitude()));

                    // マーカーを設定する。
                    setMarker(ID, mNickname, location.getLatitude(), location.getLongitude());

                    // ローダーを再スタートする。
                    getSupportLoaderManager().restartLoader(
                            LoaderId.SET_LOCATION,
                            bundle,
                            new HttpLoaderCallbacks(MainActivity.this));
                } else {
                    mLogger.i("processing.");
                }
            } else {
                mLogger.i("stop.");
            }

            mLogger.i("OUT(OK)");
        }
    }

    /**
     * HTTPローダーコールバッククラス
     *
     */
    private class HttpLoaderCallbacks implements LoaderCallbacks<String> {

        /** ロガー */
        private Logger mLogger = new Logger(HttpLoaderCallbacks.class);

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
            mLogger.i("IN");

            // ローダーを生成する。

            Loader<String> loader =
                    new SetLocationLoader(mContext, bundle);
            loader.forceLoad();

            mLogger.i("OUT(OK)");
            return loader;
        }

        /**
         * ローダー内の処理が終了した時に呼び出される。
         */
        @Override
        public void onLoadFinished(Loader<String> loader, String response) {
            mLogger.i("IN");

            // ロケーション設定の場合
            if (LoaderId.SET_LOCATION == loader.getId()) {
                // レスポンス文字列がある場合
                if (StringUtil.isNotNullOrEmpty(response)) {
//                    Toast.makeText(mContext, "Response=[" + response + "]", Toast.LENGTH_SHORT).show();
                    try {
                        // JSON文字列を生成する。
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray locations = jsonObject.getJSONArray(Key.LOCATIONS);

                        // ロケーション情報分繰り返す。
                        for (int i = 0; i < locations.length(); i++) {
                            JSONObject location = locations.getJSONObject(i);

                            // IDを取得する。
                            String id = location.getString(Key.ID);

                            // 自分のデータ以外の場合
                            if (!ID.equals(id)) {
                                // マーカーを設定する。
                                setMarker(
                                        id,
                                        location.getString(Key.NICKNAME),
                                        Double.parseDouble(location.getString(Key.LATITUDE)),
                                        Double.parseDouble(location.getString(Key.LONGITUDE)));
                            }
                        }
                    } catch (JSONException e) {
                        mLogger.e(e);
                    }
                }

                mProcessingFlg = false;
            }

            mLogger.i("OUT(OK)");
        }

        /**
         * ローダーがリセットされた時に呼び出される。
         */
        @Override
        public void onLoaderReset(Loader<String> loader) {
            mLogger.i("IN");

            // 何もしない。

            mLogger.i("OUT(OK)");
        }
    }

    /**
     * 開始ボタンクリックリスナークラス
     *
     */
    private class StartOnCheckedChangeListener implements OnCheckedChangeListener {

        /** ロガー */
        private Logger mLogger = new Logger(StartOnCheckedChangeListener.class);

        /**
         * ボタンが変更された時に呼び出される。
         *
         * @param buttonView ボタン
         * @param isChecked チェック状態
         */
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mLogger.i("IN isChecked=[" + isChecked + "]");

            mStartFlg = isChecked;

            mLogger.i("OUT(OK)");
        }
    }

    /**
     * マーカークリックリスナークラス
     *
     */
    private class OnMarkerClickListenerImpl implements OnMarkerClickListener {

        /** ロガー */
        private Logger mLogger = new Logger(StartOnCheckedChangeListener.class);

        /**
         * マーカーをクリックした時に呼び出される。
         *
         * @param marker マーカー
         */
        @Override
        public boolean onMarkerClick(Marker marker) {
            mLogger.i("IN");

            Toast.makeText(
                    MainActivity.this,
                    "Title    =[" + marker.getTitle()              + "]\n" +
                    "Latitude =[" + marker.getPosition().latitude  + "]\n" +
                    "Longitude=[" + marker.getPosition().longitude + "]",
                    Toast.LENGTH_SHORT).show();

            mLogger.i("OUT(OK)");
            return false;
        }
    }
}

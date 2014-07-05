package info.paveway.hereclient;

import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.LoaderId;
import info.paveway.hereclient.CommonConstants.ParamKey;
import info.paveway.hereclient.CommonConstants.RequestCode;
import info.paveway.hereclient.CommonConstants.Url;
import info.paveway.hereclient.data.LocationData;
import info.paveway.hereclient.data.RoomData;
import info.paveway.hereclient.data.UserData;
import info.paveway.hereclient.dialog.ErrorDialogFragment;
import info.paveway.hereclient.dialog.ExitRoomDialog;
import info.paveway.hereclient.dialog.LogoutDialog;
import info.paveway.hereclient.loader.HttpPostLoaderCallbacks;
import info.paveway.hereclient.loader.OnReceiveResponseListener;
import info.paveway.log.Logger;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
 * ここにいるクライアント
 * マップ画面クラス
 *
 * @version 1.0 新規作成
 *
 */
public class MapActivity extends AbstractBaseActivity {

    /** ロガー */
    private Logger mLogger = new Logger(MapActivity.class);

    /** ドロワーリスト */
    private ListView mDrawerList;

    /** ドロワーレイアウト */
    private DrawerLayout mDrawerLayout;

    /** ドロワートグル */
    private ActionBarDrawerToggle mDrawerToggle;

    /** ユーザデータ */
    private UserData mUserData;

    /** ルームデータ */
    private RoomData mRoomData;

    /** ロケーションクライアント */
    private LocationClient mLocationClient;

    /** ロケーションリクエスト */
    private LocationRequest mLocationRequest;

    /** ロケーションリスナー */
    private LocationListener mLocationListener;

    /** 位置情報をリクエスト済みかどうか(onPause->onResume対策) */
    boolean mLocationUpdatesRequested = false;

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
        mLogger.d("IN");

        // スーパークラスのメソッドを呼び出す。
        super.onCreate(savedInstanceState);

        // レイアウトを設定する。
        setContentView(R.layout.activity_map);

        // インテントを取得する。
        Intent intent = getIntent();
        // インテントが取得できない場合
        if (null == intent) {
            // 終了する。
            toast(R.string.error_illegal_transition);
            finish();
            mLogger.d("OUT(NG)");
            return;
        }

        // ユーザデータを取得する。
        mUserData = (UserData)intent.getSerializableExtra(ExtraKey.USER_DATA);
        // ユーザデータが取得できない場合
        if (null == mUserData) {
            // 終了する。
            toast(R.string.error_illegal_transition);
            finish();
            mLogger.d("OUT(NG)");
            return;
        }

        // ルームデータを取得する。
        mRoomData = (RoomData)intent.getSerializableExtra(ExtraKey.ROOM_DATA);
        // ルームデータが取得できない場合
        if (null == mRoomData) {
            // 終了する。
            toast(R.string.error_illegal_transition);
            finish();
            mLogger.d("OUT(NG)");
            return;
        }

        // リソースを取得する。
        mResources = getResources();

        // ドロワーリストアイテムを設定する。
        String[] drawerListItems = {
                "退室",
                "ログアウト"};
        mDrawerList = (ListView)findViewById(R.id.drawerList);
        mDrawerList.setAdapter(
                new ArrayAdapter<String>(
                    this, android.R.layout.simple_list_item_1, drawerListItems));
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                // 退室の場合
                case 0: {
                    // 退室ダイアログを表示する。
                    FragmentManager manager = getSupportFragmentManager();
                    ExitRoomDialog dialog = ExitRoomDialog.newInstance(mUserData, mRoomData);
                    dialog.setCancelable(false);
                    dialog.show(manager, ExitRoomDialog.class.getSimpleName());
                    break;
                }

                // ログアウトの場合
                case 1: {
                    // ログアウトダイアログを表示する。
                    FragmentManager manager = getSupportFragmentManager();
                    LogoutDialog looutDialog = LogoutDialog.newInstance(mUserData);
                    looutDialog.setCancelable(false);
                    looutDialog.show(manager, LogoutDialog.class.getSimpleName());
                    break;
                }
                }
            }
        });

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        mDrawerToggle =
                new ActionBarDrawerToggle(
                    this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // アプリアイコンのクリック有効化
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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
                        MapActivity.this,
                        new ConnectionCallbacksImpl(),
                        new OnConnectionFailedListenerImpl());

        /** マップ初期化済みフラグをクリアする。 */
        mInitedMap = false;

        // 開始フラグをクリアする。
        mStartFlg = true;

        // マップフラグメントを取得する。
        MapFragment mapFragment = (MapFragment)(getFragmentManager().findFragmentById(R.id.map));
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
            toast(R.string.error_init_map);
            finish();
            return;
        }

        mLogger.d("OUT(OK)");
    }

    /**
     * 生成する前に呼び出される。
     *
     * @param savedInstanceState 保存された時のインスタンスの状態
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        mLogger.d("IN");

        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();

        mLogger.d("OUT(OK)");
    }

    /**
     * コンフィグレーションが変更された時に呼び出される。
     *
     * @param newConfig 新しいコンフィグ
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mLogger.d("IN");

        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);

        mLogger.d("OUT(OK)");
    }

    /**
     * メニューが生成された時に呼び出される。
     *
     * @param menu メニュー
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mLogger.d("IN");

        getMenuInflater().inflate(R.menu.map, menu);

        mLogger.d("OUT(OK)");
        return true;
    }

    /**
     * メニューが選択された時に呼び出される。
     *
     * @param item 選択されたメニュー
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mLogger.d("IN");

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
        // 退室ルームの場合
        case R.id.menu_exit_room: {
            // 退室ダイアログを表示する。
            FragmentManager manager = getSupportFragmentManager();
            ExitRoomDialog dialog = ExitRoomDialog.newInstance(mUserData, mRoomData);
            dialog.setCancelable(false);
            dialog.show(manager, ExitRoomDialog.class.getSimpleName());
            return true;
        }

        // ログアウトの場合
        case R.id.menu_logout: {
            // ログアウトダイアログを表示する。
            FragmentManager manager = getSupportFragmentManager();
            LogoutDialog looutDialog = LogoutDialog.newInstance(mUserData);
            looutDialog.setCancelable(false);
            looutDialog.show(manager, LogoutDialog.class.getSimpleName());
            return true;
        }
        }

        mLogger.d("OUT(OK)");
        return super.onOptionsItemSelected(item);
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
                if (isServicesConnected()) {
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
     * 戻るボタンが押された時に呼び出される。
     */
    @Override
    public void onBackPressed() {
        // ログアウトダイアログを表示する。
        FragmentManager manager = getSupportFragmentManager();
        LogoutDialog looutDialog = LogoutDialog.newInstance(mUserData);
        looutDialog.setCancelable(false);
        looutDialog.show(manager, LogoutDialog.class.getSimpleName());
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

    /**
     * 接続済みかチェックする。
     *
     * @return 接続状況 true:接続済み / false:未接続
     */
    private boolean isServicesConnected() {
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

    /**
     * 地図の初期化を行う。
     */
    private void mapInit() {
        mLogger.d("IN");

        // 地図タイプを設定する。
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 現在位置ボタンの表示する。
        mGoogleMap.setMyLocationEnabled(true);

        // マーカークリックリスナーを設定する。
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
    private void setMarker(LocationData locationData) {
        mLogger.d("IN");

        String name = locationData.getName();

        // IDのマーカーを取得する。
        Marker marker = mMarkerMap.get(name);
        // 取得できた場合
        if (null != marker) {
            // マーカーを削除する。
            mLogger.d("Marker exist.");
            marker.remove();
            mMarkerMap.remove(name);

        } else {
            mLogger.d("Marker not exist.");
        }

        //
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(locationData.getLatitude(), locationData.getLongitude()));
        options.title(name);
        mMarkerMap.put(name, mGoogleMap.addMarker(options));

        mLogger.d("OUT(OK)");
    }

    /**
     * エラーダイアログを表示します。
     *
     * @param errorCode エラーコード
     * @param requestCode リクエストコード 未使用の場合0
     */
    protected void showErrorDialog(int errorCode, int requestCode) {
        // Google Play servicesからエラーダイアログを受け取る
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, requestCode);

        // エラーダイアログを取得できた場合
        if (errorDialog != null) {
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();
            errorFragment.setDialog(errorDialog);
            errorFragment.show(getSupportFragmentManager(), ErrorDialogFragment.TAG);

        }
    }

    /**************************************************************************/
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
                // 現在位置を取得する。
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

    /**************************************************************************/
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
                    connectionResult.startResolutionForResult(
                        MapActivity.this, RequestCode.CONNECTION_FAILURE_RESOLUTION_REQUEST);
                } catch (IntentSender.SendIntentException e) {
                    mLogger.e(e);
                }

            // 解決策がない場合
            } else {
                // 解決策がない場合はエラーダイアログを出します
                showErrorDialog(
                    connectionResult.getErrorCode(),
                    RequestCode.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            }

            mLogger.i("OUT(OK)");
        }
    }

    /**************************************************************************/
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

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    // ローダーを初期化する。
                    Bundle bundle = new Bundle();

                    bundle.putString(ParamKey.URL,       Url.SEND_LOCATION);
                    bundle.putString(ParamKey.ROOM_NAME, mRoomData.getName());
                    bundle.putString(ParamKey.ROOM_KEY,  mRoomData.getPassword());
                    bundle.putString(ParamKey.USER_ID,   String.valueOf(mUserData.getId()));
                    bundle.putString(ParamKey.USER_NAME, mUserData.getName());
                    bundle.putString(ParamKey.LATITUDE,  String.valueOf(latitude));
                    bundle.putString(ParamKey.LONGITUDE, String.valueOf(longitude));

                    LocationData locationData = new LocationData();
                    locationData.setId(mUserData.getId());
                    locationData.setName(mUserData.getName());
                    locationData.setLatitude(latitude);
                    locationData.setLongitude(longitude);

                    // マーカーを設定する。
                    setMarker(locationData);

                    // ローダーを再スタートする。
                    getSupportLoaderManager().restartLoader(
                            LoaderId.SEND_LOCATION,
                            bundle,
                            new HttpPostLoaderCallbacks(
                                MapActivity.this, new SendLocationOnReceiveResponseListener()));
                } else {
                    mLogger.i("processing.");
                }
            } else {
                mLogger.i("stop.");
            }

            mLogger.i("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * 位置情報レスポンス受信リスナークラス
     *
     */
    private class SendLocationOnReceiveResponseListener implements OnReceiveResponseListener {

        /** ロガー */
        private Logger mLogger = new Logger(SendLocationOnReceiveResponseListener.class);

        /**
         * レスポンスを受信した時に呼び出される。
         *
         * @param response レスポンス文字列
         * @param bundle バンドル
         */
        @Override
        public void onReceive(String response, Bundle bundle) {
            mLogger.d("IN");

            try {
                // JSON文字列を生成する。
                JSONObject json = new JSONObject(response);

                // ステータスを取得する。
                boolean status = json.getBoolean(ParamKey.STATUS);

                // 削除成功の場合
                if (status) {
                    // 位置データ数を取得する。
                    int locationDataNum = json.getInt(ParamKey.LOCATION_DATA_NUM);

                    // 位置データがある場合
                    if (0 < locationDataNum) {
                        JSONArray locationDatas = json.getJSONArray(ParamKey.LOCATION_DATAS);

                        // 位置データ数分繰り返す。
                        for (int i = 0; i < locationDataNum; i++) {
                            JSONObject locationDataObj = locationDatas.getJSONObject(i);

                            // 位置データを生成する。
                            LocationData locationData = new LocationData();
                            locationData.setId(locationDataObj.getLong(ParamKey.USER_ID));
                            locationData.setName(locationDataObj.getString(ParamKey.USER_NAME));
                            locationData.setLatitude(locationDataObj.getDouble(ParamKey.LATITUDE));
                            locationData.setLongitude(locationDataObj.getDouble(ParamKey.LONGITUDE));
                            locationData.setUpdateTime(locationDataObj.getLong(ParamKey.LOCATION_UPDATE_TIME));

                            // マーカーを設定する。
                            setMarker(locationData);
                        }
                    }

                }
            } catch (JSONException e) {
                mLogger.e(e);
            }
            mProcessingFlg = false;

            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * マーカークリックリスナークラス
     *
     */
    private class OnMarkerClickListenerImpl implements OnMarkerClickListener {

        /** ロガー */
        private Logger mLogger = new Logger(OnMarkerClickListenerImpl.class);

        /**
         * マーカーをクリックした時に呼び出される。
         *
         * @param marker マーカー
         */
        @Override
        public boolean onMarkerClick(Marker marker) {
            mLogger.i("IN");

            // 位置情報を表示する。
            String text =
                    "Title    =[" + marker.getTitle()              + "]\n" +
                    "Latitude =[" + marker.getPosition().latitude  + "]\n" +
                    "Longitude=[" + marker.getPosition().longitude + "]";
            toast(text);

            mLogger.i("OUT(OK)");
            return false;
        }
    }
}

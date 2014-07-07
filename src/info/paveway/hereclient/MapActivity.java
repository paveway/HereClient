package info.paveway.hereclient;

import info.paveway.hereclient.CommonConstants.Action;
import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.LoaderId;
import info.paveway.hereclient.CommonConstants.ParamKey;
import info.paveway.hereclient.CommonConstants.Url;
import info.paveway.hereclient.data.LocationData;
import info.paveway.hereclient.data.RoomData;
import info.paveway.hereclient.data.UserData;
import info.paveway.hereclient.dialog.EditMemoDialog;
import info.paveway.hereclient.dialog.ExitRoomDialog;
import info.paveway.hereclient.dialog.LogoutDialog;
import info.paveway.hereclient.loader.HttpPostLoaderCallbacks;
import info.paveway.hereclient.loader.OnReceiveResponseListener;
import info.paveway.hereclient.service.LocationService;
import info.paveway.log.Logger;
import info.paveway.util.ServiceUtil;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
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

    /** 位置ブロードキャストレシーバー */
    private LocationBroadcastReceiver mLocationReceiver;

    /** 位置情報接続失敗ブロードキャストレシーバー */
    private LocationFailedReceiver mLocationFailedReceiver;

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

    /** Googleマップ */
    private GoogleMap mGoogleMap;

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

        // マップフラグメントを取得する。
        MapFragment mapFragment = (MapFragment)(getFragmentManager().findFragmentById(R.id.map));
        try {
            // マップオブジェクトを取得する。
            mGoogleMap = mapFragment.getMap();

            // Activityが初めて生成された場合
            if (null == savedInstanceState) {
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

        // 位置ブロードキャストレシーバーを登録する。
        mLocationReceiver = new LocationBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.ACTION_LOCATION);
        registerReceiver(mLocationReceiver, filter);

        // 情報取得失敗ブロードキャストレシーバーを登録する。
        mLocationFailedReceiver = new LocationFailedReceiver();
        IntentFilter failedFilter = new IntentFilter();
        failedFilter.addAction(Action.ACTION_LOCATION_FAILED);
        registerReceiver(mLocationFailedReceiver, failedFilter);

        // 位置サービスを開始する。
        if (!startLocationService()) {
            mLogger.w("OUT(NG)");
            toast(R.string.error_start_location_service);
            finish();
            return;
        }

        mLogger.d("OUT(OK)");
    }

    @Override
    public void onDestroy() {
        mLogger.d("IN");

        // 一サービスを停止する。
        stopLocationService();

        if (null != mLocationFailedReceiver) {
            // 位置情報取得失敗ブロードキャストレシーバーの登録を解除する。
            unregisterReceiver(mLocationFailedReceiver);
            mLocationFailedReceiver = null;
        }

        if (null != mLocationReceiver) {
            // 位置ブロードキャストレシーバーの登録を解除する。
            unregisterReceiver(mLocationReceiver);
            mLocationReceiver = null;
        }

        super.onDestroy();

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
            mLogger.d("OUT(OK)");
            return true;
        }

        // ログアウトの場合
        case R.id.menu_logout: {
            // ログアウトダイアログを表示する。
            FragmentManager manager = getSupportFragmentManager();
            LogoutDialog looutDialog = LogoutDialog.newInstance(mUserData);
            looutDialog.setCancelable(false);
            looutDialog.show(manager, LogoutDialog.class.getSimpleName());
            mLogger.d("OUT(OK)");
            return true;
        }
        }

        mLogger.d("OUT(OK)");
        return super.onOptionsItemSelected(item);
    }

    /**
     * 戻るボタンが押された時に呼び出される。
     */
    @Override
    public void onBackPressed() {
        mLogger.d("IN");

        // ログアウトダイアログを表示する。
        FragmentManager manager = getSupportFragmentManager();
        LogoutDialog looutDialog = LogoutDialog.newInstance(mUserData);
        looutDialog.setCancelable(false);
        looutDialog.show(manager, LogoutDialog.class.getSimpleName());

        mLogger.d("OUT(OK)");
    }

    /**************************************************************************/
    /*** 内部メソッド                                                       ***/
    /**************************************************************************/
    /**
     * 地図の初期化を行う。
     */
    private void mapInit() {
        mLogger.d("IN");

        // 地図タイプを設定する。
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 現在位置ボタンの表示する。
        mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.setOnMapClickListener(new MapOnMapClickListener());

        // マーカークリックリスナーを設定する。
        mGoogleMap.setOnMarkerClickListener(new MapOnMarkerClickListener());

        mLogger.d("OUT(OK)");
    }

    /**
     * 位置マーカーを設定する。
     *
     * @param locationData 位置データ
     */
    private void setLocationMarker(LocationData locationData) {
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

        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(locationData.getLatitude(), locationData.getLongitude()));
        options.title(name);
        mMarkerMap.put(name, mGoogleMap.addMarker(options));

        mLogger.d("OUT(OK)");
    }

    /**
     * 位置サービスを起動する。
     */
    private boolean startLocationService() {
        mLogger.d("IN");

        ComponentName name = null;

        // 位置サービスが停止してる場合
        if (!ServiceUtil.isServiceRunning(MapActivity.this, LocationService.class)) {
            // 位置サービスを開始する。
            name = startService(new Intent(this, LocationService.class));
            mLogger.d("ComponentName=[" + name + "]");
        }
        if (null != name) {
            mLogger.d("OUT(OK)");
            return true;
        } else {
            mLogger.d("OUT(NG)");
            return false;
        }
    }

    /**
     * 位置サービスを停止する。
     */
    private void stopLocationService() {
        mLogger.d("IN");

        // 監視サービスを停止する。
        boolean result = stopService(new Intent(MapActivity.this, LocationService.class));
        mLogger.d("result=[" + result + "]");

        mLogger.d("OUT(OK)");
    }

    /**************************************************************************/
    /**
     * 位置情報ブロードキャストレシーバー
     *
     */
    public class LocationBroadcastReceiver extends BroadcastReceiver {

        /** ロガー */
        private Logger mLogger = new Logger(LocationBroadcastReceiver.class);

        @Override
        public void onReceive(Context context, Intent intent) {
            mLogger.d("IN");

            // インテントが取得できない場合
            if (null == intent) {
                // 終了する。
                mLogger.d("OUT(NG)");
                return;
            }

            // 引継ぎデータから緯度、経度を取得する。
            double latitude = intent.getDoubleExtra(ExtraKey.USER_LATITUDE, -1);
            double longitude = intent.getDoubleExtra(ExtraKey.USER_LONGITUDE, -1);
            // 緯度または経度が取得できない場合
            if ((-1 == latitude) || (-1 == longitude)) {
                // 終了する。
                mLogger.d("OUT(NG)");
                return;
            }

            CameraPosition cameraPos = new CameraPosition.Builder()
            .target(new LatLng(latitude, longitude)).zoom(7.0f)
            .bearing(0).build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));

            // ローダーを初期化する。
            Bundle bundle = new Bundle();
            bundle.putString(ParamKey.URL,       Url.SEND_LOCATION);
            bundle.putString(ParamKey.ROOM_NAME, mRoomData.getName());
            bundle.putString(ParamKey.ROOM_KEY,  mRoomData.getPassword());
            bundle.putString(ParamKey.USER_ID,   String.valueOf(mUserData.getId()));
            bundle.putString(ParamKey.USER_NAME, mUserData.getName());
            bundle.putString(ParamKey.LATITUDE,  String.valueOf(latitude));
            bundle.putString(ParamKey.LONGITUDE, String.valueOf(longitude));

            // ローダーを再スタートする。
            getSupportLoaderManager().restartLoader(
                    LoaderId.SEND_LOCATION,
                    bundle,
                    new HttpPostLoaderCallbacks(
                        MapActivity.this, new SendLocationOnReceiveResponseListener()));

            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * 位置接続失敗ブロードキャストレシーバー
     *
     */
    private class LocationFailedReceiver extends BroadcastReceiver {

        /** ロガー */
        private Logger mLogger = new Logger(LocationFailedReceiver.class);

        @Override
        public void onReceive(Context context, Intent intent) {
            mLogger.d("IN");

            mLogger.d("OUT(OK)");
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
                            setLocationMarker(locationData);
                        }
                    }

                }
            } catch (JSONException e) {
                mLogger.e(e);
            }

            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * マッククリックリスナークラス
     *
     */
    private class MapOnMapClickListener implements OnMapClickListener {

        /** ロガー */
        private Logger mLogger = new Logger(MapOnMarkerClickListener.class);

        /**
         * マップがクリックされた時に呼び出される。
         *
         * @param latlng 緯度経度データ
         */
        @Override
        public void onMapClick(LatLng latlng) {
            mLogger.d("IN");

            // メモ編集ダイアログを表示する。
            FragmentManager manager = getSupportFragmentManager();
            EditMemoDialog dialog = EditMemoDialog.newInstance(mUserData, mRoomData, latlng.latitude, latlng.longitude);
            dialog.setCancelable(false);
            dialog.show(manager, EditMemoDialog.class.getSimpleName());

            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * マップマーカークリックリスナークラス
     *
     */
    private class MapOnMarkerClickListener implements OnMarkerClickListener {

        /** ロガー */
        private Logger mLogger = new Logger(MapOnMarkerClickListener.class);

        /**
         * マーカーをクリックした時に呼び出される。
         *
         * @param marker マーカー
         */
        @Override
        public boolean onMarkerClick(Marker marker) {
            mLogger.d("IN");

            // 位置情報を表示する。
            marker.setSnippet(marker.getPosition().toString());

            mLogger.d("OUT(OK)");
            return false;
        }
    }
}

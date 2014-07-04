package info.paveway.hereclient;

import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.LoaderId;
import info.paveway.hereclient.CommonConstants.ParamKey;
import info.paveway.hereclient.CommonConstants.Url;
import info.paveway.hereclient.data.RoomData;
import info.paveway.hereclient.data.UserData;
import info.paveway.hereclient.dialog.CreateRoomDialog;
import info.paveway.hereclient.dialog.DeleteRoomDialog;
import info.paveway.hereclient.dialog.DeleteUserDialog;
import info.paveway.hereclient.dialog.EnterRoomDialog;
import info.paveway.hereclient.dialog.InfoDialog;
import info.paveway.hereclient.dialog.LogoutDialog;
import info.paveway.hereclient.loader.OnReceiveResponseListener;
import info.paveway.hereclient.loader.HttpGetLoaderCallbacks;
import info.paveway.log.Logger;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * ここにいるクライアント
 * ルーム一覧画面クラス
 *
 * @version 1.0 新規作成
 *
 */
public class RoomListActivity extends ActionBarActivity {

    /** ロガー */
    private Logger mLogger = new Logger(RoomListActivity.class);

    /** ハンドラー */
    private Handler mHandler = new Handler();

    /** リソース */
    protected Resources mResources;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    /** ユーザデータ */
    private UserData mUserData;

    /** ルームデータリスト */
    private List<RoomData> mRoomDataList;

    /** ルーム名リスト */
    private List<String> mRoomNameList;

    /** ルーム名リストアダプター */
    private ArrayAdapter<String> mRoomNameListAdapter;

    /** ルーム作成ダイアログ */
    private CreateRoomDialog mCreateRoomDialog;

    /** ルーム削除ダイアログ */
    private DeleteRoomDialog mDeleteRoomDialog;

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
        setContentView(R.layout.activity_room_list);

        // インテントを取得する。
        Intent intent = getIntent();
        // インテントが取得できない場合
        if (null == intent) {
            // 終了する。
            finish();
            mLogger.w("OUT(NG)");
            return;
        }

        // ユーザデータを取得する。
        mUserData = (UserData)intent.getSerializableExtra(ExtraKey.USER_DATA);
        // ユーザデータが取得できない場合
        if (null == mUserData) {
            // 終了する。
            finish();
            mLogger.w("OUT(NG)");
            return;
        }

        // リソースを取得する。
        mResources = getResources();

        String[] drawerListItems = {
                "新規ルーム",
                "更新",
                "ログアウト",
                "ユーザ削除",
                getResourceString(R.string.menu_info)};
        mDrawerList = (ListView)findViewById(R.id.drawerList);
        mDrawerList.setAdapter(
                new ArrayAdapter<String>(
                    this, android.R.layout.simple_list_item_1, drawerListItems));
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                // 新規ルームの場合
                case 0: {
                    // 新規ルームダイアログを表示する。
                    FragmentManager manager = getSupportFragmentManager();
                    mCreateRoomDialog = CreateRoomDialog.newInstance(mUserData);
                    mCreateRoomDialog.show(manager, CreateRoomDialog.class.getSimpleName());
                    break;
                }

                // 更新の場合
                case 1: {
                    // ルームデータリストを取得する。
                    getRoomDataList();
                    break;
                }

                // ログアウトの場合
                case 2: {
                    // ログアウトダイアログを表示する。
                    FragmentManager manager = getSupportFragmentManager();
                    LogoutDialog looutDialog = LogoutDialog.newInstance(mUserData);
                    looutDialog.setCancelable(false);
                    looutDialog.show(manager, LogoutDialog.class.getSimpleName());
                    break;
                }

                // ユーザ削除の場合
                case 3: {
                    // ユーザ削除ダイアログを表示する。
                    FragmentManager manager = getSupportFragmentManager();
                    DeleteUserDialog deleteUserDialog = DeleteUserDialog.newInstance(mUserData);
                    deleteUserDialog.setCancelable(false);
                    deleteUserDialog.show(manager, DeleteUserDialog.class.getSimpleName());
                    break;
                }

                // バージョン情報の場合
                case 4: {
                    // バージョン情報ダイアログを表示する。
                    FragmentManager manager = getSupportFragmentManager();
                    InfoDialog infoDialog = InfoDialog.newInstance();
                    infoDialog.show(manager, InfoDialog.class.getSimpleName());
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

        // ルームデータリストを生成する。
        mRoomDataList = new ArrayList<RoomData>();

        // ルーム名リストを生成する。
        mRoomNameList = new ArrayList<String>();

        // ルーム名リストアダプターを仮作成する。
        mRoomNameListAdapter =
                new ArrayAdapter<String>(
                        RoomListActivity.this, android.R.layout.simple_list_item_1, mRoomNameList);

        // ルーム名リストビューを設定する。
        ListView roomNameListView = (ListView)findViewById(R.id.roomListView);
        roomNameListView.setAdapter(mRoomNameListAdapter);
        roomNameListView.setOnItemClickListener(new RoomNameOnItemClickListener());
        roomNameListView.setOnItemLongClickListener(new RoomNameOnItemLongClickListener());

        // ルームデータリストを取得する。
        getRoomDataList();

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

        getMenuInflater().inflate(R.menu.room_list, menu);

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
        // 新規ルームの場合
        case R.id.menu_create_room: {
            // 新規ルームダイアログを表示する。
            FragmentManager manager = getSupportFragmentManager();
            mCreateRoomDialog = CreateRoomDialog.newInstance(mUserData);
            mCreateRoomDialog.show(manager, CreateRoomDialog.class.getSimpleName());
            return true;
        }

        // 更新の場合
        case R.id.menu_update_room_list: {
            // ルームデータリストを取得する。
            getRoomDataList();
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

        // ユーザ削除の場合
        case R.id.menu_delete_user: {
            // ユーザ削除ダイアログを表示する。
            FragmentManager manager = getSupportFragmentManager();
            DeleteUserDialog deleteUserDialog = DeleteUserDialog.newInstance(mUserData);
            deleteUserDialog.setCancelable(false);
            deleteUserDialog.show(manager, DeleteUserDialog.class.getSimpleName());
            return true;
        }

        // バージョン情報の場合
        case R.id.menu_info:
            // バージョン情報ダイアログを表示する。
            FragmentManager manager = getSupportFragmentManager();
            InfoDialog infoDialog = InfoDialog.newInstance();
            infoDialog.show(manager, InfoDialog.class.getSimpleName());
            return true;
        }

        mLogger.d("OUT(OK)");
        return super.onOptionsItemSelected(item);
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
     * リソース文字列を返却する。
     *
     * @param id 文字列のリソースID
     * @return リソース文字列
     */
    protected String getResourceString(int id) {
        return mResources.getString(id);
    }

    /**
     * ルームデータリストを取得する。
     */
    private void getRoomDataList() {
        mLogger.d("IN");

        // パラメータを生成する。
        Bundle params = new Bundle();
        params.putString(ParamKey.URL, Url.ROOM_LIST);

        // ルームリストローダーをロードする。
        getSupportLoaderManager().restartLoader(
                LoaderId.ROOM_LIST, params, new HttpGetLoaderCallbacks(
                        RoomListActivity.this, new RoomListOnReceiveResponseListener()));

        mLogger.d("OUT(OK)");
    }

    /**************************************************************************/
    /**
     * ルーム名アイテムクリックリスナークラス
     *
     */
    private class RoomNameOnItemClickListener implements OnItemClickListener {

        /** ロガー */
        private Logger mLogger = new Logger(RoomNameOnItemClickListener.class);

        /**
         * ルーム名アイテムがクリックされた時に呼び出される。
         *
         * @param parent 親ビュー
         * @param view クリックされたビュー
         * @param position クリックされたリストの位置
         * @param id クリックされたビューのID
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mLogger.d("IN");

            // 入室ダイアログを表示する。
            FragmentManager manager = getSupportFragmentManager();
            EnterRoomDialog enterRoomDialog = EnterRoomDialog.newInstance(mUserData, mRoomDataList.get(position));
            enterRoomDialog.show(manager, EnterRoomDialog.class.getSimpleName());

            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * ルーム名アイテムロングクリックリスナークラス
     *
     */
    private class RoomNameOnItemLongClickListener implements OnItemLongClickListener {

        /** ロガー */
        private Logger mLogger = new Logger(RoomNameOnItemLongClickListener.class);

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            mLogger.d("IN");

            // ルーム削除ダイアログを表示する。
            FragmentManager manager = getSupportFragmentManager();
            mDeleteRoomDialog = DeleteRoomDialog.newInstance(mUserData, mRoomDataList.get(position));
            mDeleteRoomDialog.show(manager, DeleteRoomDialog.class.getSimpleName());

            mLogger.d("OUT(OK)");
            return true;
        }
    }

    /**************************************************************************/
    /**
     * ルームリストレスポンス受信リスナークラス
     *
     */
    private class RoomListOnReceiveResponseListener implements OnReceiveResponseListener {

        /** ロガー */
        private Logger mLogger = new Logger(RoomListOnReceiveResponseListener.class);

        /**
         * レスポンス受信した時に呼び出される。
         *
         * @param response 受信したレスポンス文字列
         * @param bundle バンドル
         */
        @Override
        public void onReceive(String response, Bundle bundle) {
            mLogger.d("IN response=[" + response + "]");

            try {
                JSONObject json = new JSONObject(response);

                // ステータスを取得する。
                boolean status = json.getBoolean(ParamKey.STATUS);

                // 登録成功の場合
                if (status) {
                    // ルームデータリストをクリアする。
                    mRoomDataList.clear();

                    // ルームデータ数を取得する。
                    int roomDataNum = json.getInt(ParamKey.ROOM_DATA_NUM);

                    // ルームデータがある場合
                    if (0 < roomDataNum) {
                        JSONArray roomDatas = json.getJSONArray(ParamKey.ROOM_DATAS);
                        // ルームデータ数分繰り返す。
                        for (int i = 0; i < roomDataNum; i++) {
                            JSONObject roomDataObj = roomDatas.getJSONObject(i);
                            RoomData roomData = new RoomData();

                            roomData.setId(roomDataObj.getLong(ParamKey.ROOM_ID));
                            roomData.setName(roomDataObj.getString(ParamKey.ROOM_NAME));
                            roomData.setPassword(roomDataObj.getString(ParamKey.ROOM_KEY));
                            roomData.setOwnerId(roomDataObj.getLong(ParamKey.OWNER_ID));
                            roomData.setOwnerName(roomDataObj.getString(ParamKey.OWNER_NAME));
                            roomData.setUpdateTime(roomDataObj.getLong(ParamKey.ROOM_UPDATE_TIME));

                            mRoomDataList.add(roomData);
                        }
                    }

                    // ルーム名リストアダプタをクリアする。
                    mRoomNameListAdapter.clear();

                    // ルーム名リストを生成する。
                    for (RoomData data : mRoomDataList) {
                        mRoomNameList.add(data.getName());
                    }

                    // ルーム名リストアダプタを更新する。
                    mRoomNameListAdapter.notifyDataSetChanged();

                // エラーの場合
                } else {
                    Toast.makeText(RoomListActivity.this, "ルーム名を取得できませんでした", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                mLogger.e(e);
                Toast.makeText(RoomListActivity.this, "エラーが発生しました", Toast.LENGTH_SHORT).show();
            }


            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * ルーム作成レスポンス受信リスナークラス
     *
     */
    public class CreateRoomOnReceiveResponseListener implements OnReceiveResponseListener {

        /** ロガー */
        private Logger mLogger = new Logger(CreateRoomOnReceiveResponseListener.class);

        /**
         * レスポンス受信した時に呼び出される。
         *
         * @param response レスポンス文字列
         * @param bundle バンドル
         */
        @Override
        public void onReceive(String response, Bundle bundle) {
            mLogger.d("IN response=[" + response + "]");

            try {
                // レスポンス文字列からJSONオブジェクトを生成する。
                JSONObject json = new JSONObject(response);

                // ステータスを取得する。
                boolean status = json.getBoolean(ParamKey.STATUS);

                // 正常終了の場合
                if (status) {
                    // ルーム作成ダイアログを閉じる。
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCreateRoomDialog.dismiss();
                        }
                    });

                    // ルームデータリストをクリアする。
                    mRoomDataList.clear();

                    // ルームデータ数を取得する。
                    int roomDataNum = json.getInt(ParamKey.ROOM_DATA_NUM);

                    // ルームデータがある場合
                    if (0 < roomDataNum) {
                        JSONArray roomDatas = json.getJSONArray(ParamKey.ROOM_DATAS);
                        // ルームデータ数分繰り返す。
                        for (int i = 0; i < roomDataNum; i++) {
                            JSONObject roomDataObj = roomDatas.getJSONObject(i);
                            RoomData roomData = new RoomData();

                            roomData.setId(roomDataObj.getLong(ParamKey.ROOM_ID));
                            roomData.setName(roomDataObj.getString(ParamKey.ROOM_NAME));
                            roomData.setPassword(roomDataObj.getString(ParamKey.ROOM_KEY));
                            roomData.setOwnerId(roomDataObj.getLong(ParamKey.OWNER_ID));
                            roomData.setOwnerName(roomDataObj.getString(ParamKey.OWNER_NAME));
                            roomData.setUpdateTime(roomDataObj.getLong(ParamKey.ROOM_UPDATE_TIME));

                            mRoomDataList.add(roomData);
                        }
                    }

                    // ルーム名リストアダプタをクリアする。
                    mRoomNameListAdapter.clear();

                    // ルーム名リストを生成する。
                    for (RoomData data : mRoomDataList) {
                        mRoomNameList.add(data.getName());
                    }

                    // ルーム名リストアダプタを更新する。
                    mRoomNameListAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                mLogger.e(e);
                Toast.makeText(RoomListActivity.this, "エラーが発生しました", Toast.LENGTH_SHORT).show();
            }

            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * ルーム削除レスポンス受信リスナークラス
     *
     */
    public class DeleteRoomOnReceiveResponseListener implements OnReceiveResponseListener {

        /** ロガー */
        private Logger mLogger = new Logger(DeleteRoomOnReceiveResponseListener.class);

        /**
         * レスポンス受信した時に呼び出される。
         *
         * @param response レスポンス文字列
         * @param bundle バンドル
         */
        @Override
        public void onReceive(String response, Bundle bundle) {
            mLogger.d("IN response=[" + response + "]");

            try {
                JSONObject json = new JSONObject(response);

                // ステータスを取得する。
                boolean status = json.getBoolean(ParamKey.STATUS);

                // 削除成功の場合
                if (status) {
                    // ルーム削除ダイアログを閉じる。
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mDeleteRoomDialog.dismiss();
                        }
                    });

                    // ルームデータリストをクリアする。
                    mRoomDataList.clear();

                    // ルームデータ数を取得する。
                    int roomDataNum = json.getInt(ParamKey.ROOM_DATA_NUM);

                    // ルームデータがある場合
                    if (0 < roomDataNum) {
                        JSONArray roomDatas = json.getJSONArray(ParamKey.ROOM_DATAS);
                        // ルームデータ数分繰り返す。
                        for (int i = 0; i < roomDataNum; i++) {
                            JSONObject roomDataObj = roomDatas.getJSONObject(i);
                            RoomData roomData = new RoomData();

                            roomData.setId(roomDataObj.getLong(ParamKey.ROOM_ID));
                            roomData.setName(roomDataObj.getString(ParamKey.ROOM_NAME));
                            roomData.setPassword(roomDataObj.getString(ParamKey.ROOM_KEY));
                            roomData.setOwnerId(roomDataObj.getLong(ParamKey.OWNER_ID));
                            roomData.setOwnerName(roomDataObj.getString(ParamKey.OWNER_NAME));
                            roomData.setUpdateTime(roomDataObj.getLong(ParamKey.ROOM_UPDATE_TIME));

                            mRoomDataList.add(roomData);
                        }
                    }

                    // ルーム名リストアダプタをクリアする。
                    mRoomNameListAdapter.clear();

                    // ルーム名リストを生成する。
                    for (RoomData data : mRoomDataList) {
                        mRoomNameList.add(data.getName());
                    }

                    // ルーム名リストアダプタを更新する。
                    mRoomNameListAdapter.notifyDataSetChanged();

                // エラーまたはログインできない場合
                } else {
                    Toast.makeText(RoomListActivity.this, "ユーザを削除できませんでした", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                mLogger.e(e);
                Toast.makeText(RoomListActivity.this, "エラーが発生しました", Toast.LENGTH_SHORT).show();
            }

            mLogger.d("OUT(OK)");
        }
    }
}

package info.paveway.hereclient;

import info.paveway.hereclient.CommonConstants.ExtraKey;
import info.paveway.hereclient.CommonConstants.HttpKey;
import info.paveway.hereclient.CommonConstants.JSONKey;
import info.paveway.hereclient.CommonConstants.LoaderId;
import info.paveway.hereclient.CommonConstants.Url;
import info.paveway.hereclient.data.RoomData;
import info.paveway.hereclient.data.UserData;
import info.paveway.hereclient.dialog.CreateRoomDialog;
import info.paveway.hereclient.dialog.DeleteUserDialog;
import info.paveway.hereclient.dialog.EnterRoomDialog;
import info.paveway.hereclient.loader.LogoutLoaderCallbacks;
import info.paveway.hereclient.loader.OnReceiveResponseListener;
import info.paveway.hereclient.loader.RoomListLoaderCallbacks;
import info.paveway.log.Logger;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    /** ユーザデータ */
    private UserData mUserData;

    /** ルームデータリスト */
    private List<RoomData> mRoomDataList;

    /** ルーム名リスト */
    private List<String> mRoomNameList;

    /** ルーム名リストアダプター */
    private ArrayAdapter<String> mRoomNameListAdapter;

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

        // 各ウィジットを設定する。
        ((Button)findViewById(R.id.createRoomButton)).setOnClickListener(new ButtonOnClickListener());
        ((Button)findViewById(R.id.logoutButton)).setOnClickListener(new ButtonOnClickListener());

        // ルームデータリストを取得する。
        getRoomDataList();

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

        switch (item.getItemId()) {
        // ユーザ削除の場合
        case R.id.menu_delete_user:
            // ユーザ削除ダイアログを表示する。
            FragmentManager manager = getSupportFragmentManager();
            DeleteUserDialog deleteUserDialog = DeleteUserDialog.newInstance(mUserData);
            deleteUserDialog.setCancelable(false);
            deleteUserDialog.show(manager, DeleteUserDialog.class.getSimpleName());
           return true;
        }

        mLogger.d("OUT(OK)");
        return super.onOptionsItemSelected(item);
    }

    /**
     * ルームデータリストを取得する。
     */
    private void getRoomDataList() {
        mLogger.d("IN");

        // パラメータを生成する。
        Bundle params = new Bundle();
        params.putString(HttpKey.URL, Url.ROOM_LIST);

        // ルームリストローダーをロードする。
        getSupportLoaderManager().restartLoader(
                LoaderId.ROOM_LIST, params, new RoomListLoaderCallbacks(
                        RoomListActivity.this, new RoomListOnReceiveResponseListener()));

        mLogger.d("OUT(OK)");
    }

    /**************************************************************************/
    /**
     * ボタンクリックリスナークラス
     *
     */
    private class ButtonOnClickListener implements OnClickListener {

        /** ロガー */
        private Logger mLogger = new Logger(ButtonOnClickListener.class);

        /**
         * ボタンがクリックされた時に呼び出される。
         *
         * @param v クリックされたボタン
         */
        @Override
        public void onClick(View v) {
            mLogger.d("IN");

            // ボタンIDにより処理を判別する。
            switch (v.getId()) {
            // 新規ルームボタンの場合
            case R.id.createRoomButton:
                // 新規ルームダイアログを表示する。
                FragmentManager manager = getSupportFragmentManager();
                CreateRoomDialog newRoomDialog = CreateRoomDialog.newInstance(mUserData);
                newRoomDialog.show(manager, CreateRoomDialog.class.getSimpleName());
                break;

            // ログアウトボタンの場合
            case R.id.logoutButton:
                // ログアウト処理を行う。
                // パラメータを生成する。
                Bundle params = new Bundle();
                params.putString(HttpKey.URL,           Url.LOGOUT);
                params.putString(HttpKey.USER_NAME,     mUserData.getName());
                params.putString(HttpKey.USER_PASSWORD, mUserData.getPassword());

                // ルームリストローダーをロードする。
                getSupportLoaderManager().restartLoader(
                        LoaderId.LOGOUT, params, new LogoutLoaderCallbacks(
                                RoomListActivity.this, new LogoutOnReceiveResponseListener()));
                break;
            }

            mLogger.d("OUT(OK)");
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
                boolean status = json.getBoolean(HttpKey.STATUS);

                // 登録成功の場合
                if (status) {
                    // ルームデータリストをクリアする。
                    mRoomDataList.clear();

                    // ルームデータ数を取得する。
                    int roomDataNum = json.getInt(JSONKey.ROOM_DATA_NUM);

                    // ルームデータがある場合
                    if (0 < roomDataNum) {
                        JSONArray roomDatas = json.getJSONArray(JSONKey.ROOM_DATAS);
                        // ルームデータ数分繰り返す。
                        for (int i = 0; i < roomDataNum; i++) {
                            JSONObject roomDataObj = roomDatas.getJSONObject(i);
                            RoomData roomData = new RoomData();

                            roomData.setId(roomDataObj.getLong(HttpKey.ROOM_ID));
                            roomData.setName(roomDataObj.getString(HttpKey.ROOM_NAME));

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
     * ログアウトレスポンス受信リスナークラス
     *
     */
    private class LogoutOnReceiveResponseListener implements OnReceiveResponseListener {

        /** ロガー */
        private Logger mLogger = new Logger(LogoutOnReceiveResponseListener.class);

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
                boolean status = json.getBoolean(HttpKey.STATUS);

                // 登録成功の場合
                if (status) {
                    RoomListActivity.this.finish();

                // エラーの場合
                } else {
                    Toast.makeText(RoomListActivity.this, "ログアウトできませんでした", Toast.LENGTH_SHORT).show();
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
            EnterRoomDialog loginRoomDialog = EnterRoomDialog.newInstance(mRoomDataList.get(position));
            loginRoomDialog.show(manager, EnterRoomDialog.class.getSimpleName());

            mLogger.d("OUT(OK)");
        }
    }

    private class RoomNameOnItemLongClickListener implements OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            return false;
        }
    }
}

package info.paveway.hereclient;

/**
 * ここにいるクライアント
 * 共通定数クラス
 *
 * @version 1.0 新規作成
 *
 */
public class CommonConstants {

    /**
     * URL定数
     *
     */
    public class Url {
        /** ベースURL */
        public static final String BASE = "http://here-paveway-info3.appspot.com/";

        /** ユーザ登録URL */
        public static final String REGIST_USER = BASE + "registUser";

        /** ユーザ削除URL */
        public static final String DELETE_USER = BASE + "deleteUser";

        /** ログインURL */
        public static final String LOGIN = BASE + "login";

        /** ログアウトURL */
        public static final String LOGOUT = BASE + "logout";

        /** ルームリストURL */
        public static final String ROOM_LIST = BASE + "roomList";

        /** ルーム作成URL */
        public static final String CREATE_ROOM = BASE + "createRoom";

        /** ルーム削除URL */
        public static final String DELETE_ROOM = BASE + "deleteRoom";

        /** 入室URL */
        public static final String ENTER_ROOM = BASE + "enterRoom";

        /** 退室URL */
        public static final String EXIT_ROOM = BASE + "exitRoom";

        /** 位置情報送信URL */
        public static final String SEND_LOCATION = BASE + "sendLocation";

        /** メモ編集URL */
        public static final String EDIT_MEMO = BASE + "editMemo";

        /** メモ削除URL */
        public static final String DELETE_MEMO = BASE + "deleteMemo";

    }

    /**
     * パラメータキー定数
     *
     */
    public class ParamKey {
        /** URLキー */
        public static final String URL = "url";

        /** ステータス */
        public static final String STATUS = "status";

        /** ユーザID */
        public static final String USER_ID = "userId";

        /** ユーザ名 */
        public static final String USER_NAME = "userName";

        /** ユーザパスワード */
        public static final String USER_PASSWORD = "userPassword";

        /** ログイン済みフラグ */
        public static final String USER_LOGGED = "userLogged";

        /** ユーザ更新日時 */
        public static final String USER_UPDATE_TIME = "userUpdateTime";

        /** ルームID */
        public static final String ROOM_ID = "roomId";

        /** ルーム名 */
        public static final String ROOM_NAME = "roomName";

        /** ルームキー */
        public static final String ROOM_KEY = "roomKey";

        /** オーナーID */
        public static final String OWNER_ID = "ownerId";

        /** オーナー名 */
        public static final String OWNER_NAME = "ownerName";

        /** ルーム更新日時 */
        public static final String ROOM_UPDATE_TIME = "roomUpdateTime";

        /** 位置更新日時 */
        public static final String LOCATION_UPDATE_TIME = "locationUpdateTime";

        /** 緯度 */
        public static final String LATITUDE = "latitude";

        /** 軽度 */
        public static final String LONGITUDE = "longitude";

        /** メモタイトル */
        public static final String MEMO_TITLE = "memoTitle";

        /** メモ内容 */
        public static final String MEMO_CONTENT = "memoContent";

        /** メモ公開範囲 */
        public static final String MEMO_RANGE = "memoRange";

        /** メモ更新日時 */
        public static final String MEMO_UPDATE_TIME = "memoUpdateTime";

        /** ルームデータ配列 */
        public static final String ROOM_DATAS = "roomDatas";

        /** ルームデータ数 */
        public static final String ROOM_DATA_NUM = "roomDataNum";

        /** 位置データ配列 */
        public static final String LOCATION_DATAS = "locationDatas";

        /** 位置データ数 */
        public static final String LOCATION_DATA_NUM = "locationDataNum";

        /** メモデータ配列 */
        public static final String MEMO_DATAS = "memoDatas";

        /** メモデータ数 */
        public static final String MEMO_DATA_NUM = "memoDataNum";
    }

    /**
     * ローダーID定数
     *
     */
    public class LoaderId {
        /** ユーザ登録ID */
        public static final int REGIST_USER = 1;

        /** ユーザ削除ID */
        public static final int DELETE_USER = 2;

        /** ログインID */
        public static final int LOGIN = 3;

        /** ログアウトID */
        public static final int LOGOUT = 4;

        /** 新規ルームID */
        public static final int NEW_ROOM = 5;

        /** 入室ID */
        public static final int ENTER_ROOM = 6;

        /** 退室ID */
        public static final int EXIT_ROOM = 7;

        /** ルームリストID */
        public static final int ROOM_LIST = 8;

        /** 位置情報送信ID */
        public static final int SEND_LOCATION = 9;

        /** メモ編集ID */
        public static final int EDIT_MEMO = 10;

        /** メモ削除 */
        public static final int DELETE_MEMO = 11;
    }

    /**
     * Extraキー定数
     *
     */
    public class ExtraKey {
        /** ユーザデータ */
        public static final String USER_DATA = "userData";

        /** ルームデータ */
        public static final String ROOM_DATA = "roomData";

        /** 進捗ダイアログタイトル */
        public static final String PROGRESS_TITLE = "progressTitle";

        /** 進捗ダイアログメッセージ */
        public static final String PROGRESS_MESSAGE = "progressMessage";

        /** ユーザ緯度 */
        public static final String USER_LATITUDE = "userLatutide";

        /** ユーザ経度 */
        public static final String USER_LONGITUDE = "userLongitude";

        /** メモ緯度 */
        public static final String MEMO_LATITUDE = "memoLatitude";

        /** メモ経度 */
        public static final String MEMO_LONGITUDE = "memoLongitude";
    }

    /**
     * エンコーディング
     *
     */
    public class Encoding {
        /** UTF-8 */
        public static final String UTF_8 = "UTF-8";
    }

    /**
     * 要求コード定数
     *
     */
    public class RequestCode {
        /** 設定画面 */
        public static final int SETTINGS = 1;

        /** 接続エラー解決要求 */
        public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    }

    public class Action {
        public static final String ACTION_LOCATION = "info.paveway.hereclient.ACTION_LOCATION";

        public static final String ACTION_LOCATION_FAILED = "info.paveway.hereclient.ACTION_LOCATION_FAILED";
    }

    public class MemoRangeValue {
        public static final int SELF = 1;
        public static final int MEMBER = 2;
        public static final int ALL = 3;
    }
}

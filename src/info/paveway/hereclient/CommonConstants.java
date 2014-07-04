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

        /** 緯度 */
        public static final String LATITUDE = "latitude";

        /** 軽度 */
        public static final String LONGITUDE = "longitude";

        /** ルームデータ配列 */
        public static final String ROOM_DATAS = "roomDatas";

        /** ルームデータ数 */
        public static final String ROOM_DATA_NUM = "roomDataNum";

        /** 位置データ配列 */
        public static final String LOCATION_DATAS = "locationDatas";

        /** 位置データ数 */
        public static final String LOCATION_DATA_NUM = "locationDataNum";
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

        /** マップ画面 */
        public static final int MAP = 2;

        /** 接続エラー解決要求 */
        public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    }
}

package info.paveway.hereclient;

/**
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

        /** 初期化URL */
        public static final String INIT = BASE + "init";

        /** 入室URL */
        public static final String ENTER = BASE + "enter";

        /** 退室URL */
        public static final String EXIT = BASE + "exit";

        /** 位置情報設定 */
        public static final String SET_LOCATION = BASE + "setlocation";

    }

    /**
     * キー定数
     *
     */
    public class Key {
        /** URLキー */
        public static final String URL = "url";

        /** ステータスキー */
        public static final String STATUS = "status";

        /** 部屋データリストキー */
        public static final String ROOMS = "rooms";

        /** 部屋番号キー */
        public static final String ROOM_NO = "roomNo";

        /** 使用中フラグキー */
        public static final String USED = "used";

        /** パスワードキー */
        public static final String PASSWORD = "password";

        /** ユーザーIDキー */
        public static final String USER_ID = "userId";

        /** ニックネームキー */
        public static final String NICKNAME = "nickname";

        /** 更新日キー */
        public static final String UPDATE = "update";

        /** Eメールキー */
        public static final String EMAIL = "email";

        /** 緯度キー */
        public static final String LATITUDE = "latitude";

        /** 経度キー */
        public static final String LONGITUDE = "longitude";

        /** 位置情報リストキー */
        public static final String LOCATIONS = "locations";
    }

    /**
     * エンコーディング定数
     *
     */
    public class Encoding {
        /** UTF-8 */
        public static final String UTF_8 = "UTF-8";
    }

    /**
     * ローダーID定数
     *
     */
    public class LoaderId {
        /** 初期設定ID */
        public static final int INIT = 0;

        /** 入室ID */
        public static final int ENTER = 1;

        /** 退室ID */
        public static final int EXIT = 2;

        /** 位置情報設定ID */
        public static final int SET_LOCATION = 3;
    }

    /**
     * インターフェース名定数
     *
     */
    public class IntefaceName {
        /** WLAN0 */
        public static final String WLAN0 = "wlan0";
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

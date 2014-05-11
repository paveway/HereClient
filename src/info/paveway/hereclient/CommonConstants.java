package info.paveway.hereclient;

public class CommonConstants {

    public class Url {
        public static final String INIT = "http://here-paveway-info3.appspot.com/init";
    }

    /**
     * キー定数
     *
     */
    public class Key {
        /** URL */
        public static final String URL = "url";

        public static final String ROOM_NO = "roomNo";

        public static final String ROOM_NAME = "roomName";

        public static final String PASSWORD = "password";

        /** ニックネーム */
        public static final String NICKNAME = "nickname";

        /** ID */
        public static final String ID = "id";

        /** Eメール */
        public static final String EMAIL = "email";

        /** 緯度 */
        public static final String LATITUDE = "latitude";

        /** 経度 */
        public static final String LONGITUDE = "longitude";

        /** ロケーション情報 */
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
        /** 初期設定 */
        public static final int INIT = 0;

        /** 入室 */
        public static final int ENTER = 1;

        /** ロケーション設定 */
        public static final int SET_LOCATION = 2;
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

        /** 接続エラー解決要求 */
        public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    }
}

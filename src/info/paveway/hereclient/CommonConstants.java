package info.paveway.hereclient;

public class CommonConstants {

    /**
     * キー定数
     *
     */
    public class Key {
        /** URL */
        public static final String URL = "url";

        /** ID */
        public static final String ID = "id";

        /** Eメール */
        public static final String EMAIL = "email";

        /** ニックネーム */
        public static final String NICKNAME = "nickname";

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
    public class ENCODING {
        /** UTF-8 */
        public static final String UTF_8 = "UTF-8";
    }

    /**
     * ローダーID定数
     *
     */
    public class LOADER_ID {
        /** ロケーション設定 */
        public static final int SET_LOCATION = 0;
    }

    /**
     * インターフェース名定数
     *
     */
    public class INTERFACE_NAME {
        /** WLAN0 */
        public static final String WLAN0 = "wlan0";
    }

    /**
     * 要求コード定数
     *
     */
    public class REQUEST_CODE {
        /** 設定画面 */
        public static final int REQUEST_CODE_SETTINGS = 1;

        /** 接続エラー解決要求 */
        public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    }
}

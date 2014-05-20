package info.paveway.hereclient;

import info.paveway.hereclient.CommonConstants.Key;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Bundle;

/**
 * 入室ローダークラス
 *
 * @version 1.0 新規作成
 *
 */
public class EnterLoader extends AbstractHttpPostLoader {

    /** ロガー */
    private Logger mLogger = new Logger(EnterLoader.class);

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param params パラメータ
     */
    public EnterLoader(Context context, Bundle params) {
        // スーパークラスのコンストラクタを呼び出す。
        super(context, params);
    }

    /**
     * HTTP POSTパラメータを設定する。
     *
     * @return HTTP POSTパラメータリスト
     */
    @Override
    protected List<NameValuePair> setEntities() {
        mLogger.d("IN");

        List<NameValuePair> entities = new ArrayList<NameValuePair>();

        entities.add(new BasicNameValuePair(Key.ROOM_NO,  mParams.getString(Key.ROOM_NO)));
        entities.add(new BasicNameValuePair(Key.PASSWORD, mParams.getString(Key.PASSWORD)));
        entities.add(new BasicNameValuePair(Key.USER_ID,  mParams.getString(Key.USER_ID)));
        entities.add(new BasicNameValuePair(Key.NICKNAME, mParams.getString(Key.NICKNAME)));

        mLogger.d("OUT(OK)");
        return entities;
    }
}

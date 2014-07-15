package info.paveway.hereclient.loader;


import info.paveway.log.Logger;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import android.util.Xml.Encoding;

/**
 * HTTPレスポンスハンドラークラス
 *
 * @version 1.0 新規作成
 *
 */
public class HttpResponseHandler implements ResponseHandler<String> {

    /** ロガー */
    private Logger mLogger = new Logger(HttpResponseHandler.class);

    /**
     * レスポンスをハンドリングする。
     *
     * @param response HTTPレスポンス
     * @return レスポンス文字列
     * @throws IOException IO例外
     * @throws ClientProtocolException クライアントプロトコル例外
     */
    @Override
    public String handleResponse(HttpResponse response) throws IOException, ClientProtocolException {
        mLogger.i("IN");

        // 正常終了の場合
        int statusCode = response.getStatusLine().getStatusCode();
        if (HttpStatus.SC_OK == statusCode) {
            String result = EntityUtils.toString(response.getEntity(), Encoding.UTF_8.toString());
            mLogger.i("OUT(OK) result=[" + result + "]");
            return result;

        // 正常終了以外
        } else {
            mLogger.i("OUT(NG) statusCode=[" + statusCode + "]");
            return null;
        }
    }
}

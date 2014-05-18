package info.paveway.hereclient;

import info.paveway.hereclient.CommonConstants.Key;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

public class InitLoader extends AsyncTaskLoader<String> {

    private Bundle mParams;

    public InitLoader(Context context, Bundle params) {
        super(context);
        mParams = params;
    }

    @Override
    public String loadInBackground() {
        String result = null;

        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet httpGet = new HttpGet(mParams.getString(Key.URL));
            result = httpClient.execute(httpGet, new HttpResponseHandler());
        } catch (Exception e) {

        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }
}

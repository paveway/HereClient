package info.paveway.hereclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SelectRoomNoActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_room_no_activity);

        Intent intent = getIntent();
        if (null == intent) {
            finish();
            return;
        }
    }
}

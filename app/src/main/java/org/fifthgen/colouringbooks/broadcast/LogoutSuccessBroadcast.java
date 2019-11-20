package org.fifthgen.colouringbooks.broadcast;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Swifty on 2015/10/4.
 */
@SuppressWarnings("unused")
public class LogoutSuccessBroadcast {
    private static LogoutSuccessBroadcast ourInstance = new LogoutSuccessBroadcast();

    private LogoutSuccessBroadcast() {
    }

    public static LogoutSuccessBroadcast getInstance() {
        return ourInstance;
    }

    public void sendBroadcast(Context context) {
        Intent intent = new Intent();
        intent.setAction("userLoginAction");
        intent.putExtra("msg", "logoutsuccess");
        context.sendBroadcast(intent);
    }
}

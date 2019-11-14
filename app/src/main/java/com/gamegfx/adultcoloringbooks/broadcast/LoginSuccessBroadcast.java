package com.gamegfx.adultcoloringbooks.broadcast;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Swifty on 2015/10/3.
 */
public class LoginSuccessBroadcast {
    private static LoginSuccessBroadcast ourInstance = new LoginSuccessBroadcast();

    private LoginSuccessBroadcast() {
    }

    public static LoginSuccessBroadcast getInstance() {
        return ourInstance;
    }

    public void sendBroadcast(Context context) {
        Intent intent = new Intent();
        intent.setAction("userLoginAction");
        intent.putExtra("msg", "loginsuccess");
        context.sendBroadcast(intent);
    }
}

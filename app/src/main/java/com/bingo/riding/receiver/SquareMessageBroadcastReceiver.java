package com.bingo.riding.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.avos.avoscloud.LogUtil;

public class SquareMessageBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "Square_Message_Broadcast_Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.log.d(TAG, "Get broadcast message");

        String action = intent.getAction();
    }
}

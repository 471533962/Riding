package com.bingo.riding.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.bingo.riding.CustomActivity;
import com.bingo.riding.MainActivity;
import com.bingo.riding.utils.AVImClientManager;
import com.bingo.riding.utils.MessageHandler;

/**
 * Created by bingo on 15/12/17.
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AVImClientManager.getInstance().getClient() == null) {
            gotoLoginActivity(context);
        } else {
            String conversationId = intent.getStringExtra(MessageHandler.CONVERSATION_ID);
            if (!TextUtils.isEmpty(conversationId)) {
//                if (Constants.SQUARE_CONVERSATION_ID.equals(conversationId)) {
//                    gotoSquareActivity(context, intent);
//                } else {
                    gotoSingleChatActivity(context, intent);
//                }
            }
        }
    }

    /**
     * 如果 app 上下文已经缺失，则跳转到登陆页面，走重新登陆的流程
     * @param context
     */
    private void gotoLoginActivity(Context context) {
        Intent startActivityIntent = new Intent(context, CustomActivity.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startActivityIntent);
    }

    /**
     * 跳转至广场页面
     * @param context
     * @param intent
     */
    private void gotoSquareActivity(Context context, Intent intent) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivityIntent.putExtra(Constants.CONVERSATION_ID, intent.getStringExtra(Constants.CONVERSATION_ID));
        context.startActivity(startActivityIntent);
    }

    /**
     * 跳转至单聊页面
     * @param context
     * @param intent
     */
    private void gotoSingleChatActivity(Context context, Intent intent) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivityIntent.putExtra(Constants.MEMBER_ID, intent.getStringExtra(Constants.MEMBER_ID));
        context.startActivity(startActivityIntent);
    }
}

package com.bingo.riding.utils;

import android.content.Context;
import android.content.Intent;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.bingo.riding.R;
import com.bingo.riding.receiver.NotificationBroadcastReceiver;

/**
 * Created by bingo on 15/12/17.
 */
public class MessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {
    public static String CONVERSATION_ID = "conversation_id";
    public static String MEMBER_ID = "member_id";


    private Context mContext;

    public MessageHandler(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        String clientId = "";
        try{
            clientId = AVImClientManager.getInstance().getClientId();
            if (client.getClientId().equals(clientId)){
                if (message.getFrom().equals(clientId)){

                    if (NotificationUtils.isShowNotification(conversation.getConversationId())){

                    }

                }
            } else {
                client.close(null);
            }
        }catch (IllegalStateException e){
            client.close(null);
        }
    }

    private void sendNotification(AVIMTypedMessage message, AVIMConversation conversation) {
        String notificationContent = message instanceof AVIMTextMessage ?
                ((AVIMTextMessage)message).getText() : mContext.getString(R.string.unspport_message_type);

        Intent intent = new Intent(mContext, NotificationBroadcastReceiver.class);
        intent.putExtra(CONVERSATION_ID, conversation.getConversationId());
        intent.putExtra(MEMBER_ID, message.getFrom());
        NotificationUtils.showNotification(mContext, "", notificationContent, null, intent);
    }
}

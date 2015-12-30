package com.bingo.riding.utils;

import android.content.Context;
import android.content.Intent;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.bingo.riding.R;
import com.bingo.riding.event.ImTypeMessageEvent;
import com.bingo.riding.receiver.NotificationBroadcastReceiver;

import de.greenrobot.event.EventBus;

/**
 * Created by bingo on 15/12/17.
 */
public class MessageHandler extends AVIMMessageHandler{
    public static String CONVERSATION_ID = "conversation_id";
    public static String MEMBER_ID = "member_id";

    private DaoUtils daoUtils;

    private Context mContext;

    public MessageHandler(Context mContext) {
        this.mContext = mContext;

        daoUtils = DaoUtils.getInstance(mContext);
    }

    @Override
    public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
        String clientId = "";
        try{
            clientId = AVImClientManager.getInstance().getClientId();
            if (client.getClientId().equals(clientId)){
                if (!message.getFrom().equals(clientId)){
                    sendEvent(message, conversation);
                    if (NotificationUtils.isShowNotification(conversation.getConversationId())){
                        sendNotification(message, conversation);
                    }
                }
            } else {
                client.close(null);
            }
        }catch (IllegalStateException e){
            client.close(null);
        }
    }

    /**
     * 因为没有 db，所以暂时先把消息广播出去，由接收方自己处理
     * 稍后应该加入 db
     * @param message
     * @param conversation
     */
    private void sendEvent(AVIMMessage message, AVIMConversation conversation) {
        ImTypeMessageEvent event = new ImTypeMessageEvent();
        event.message = message;
        event.conversation = conversation;
        EventBus.getDefault().post(event);
    }


    private void sendNotification(AVIMMessage message, AVIMConversation conversation) {
        String notificationContent = message instanceof AVIMTextMessage ?
                ((AVIMTextMessage)message).getText() : mContext.getString(R.string.unspport_message_type);

        Intent intent = new Intent(mContext, NotificationBroadcastReceiver.class);
        intent.putExtra(CONVERSATION_ID, conversation.getConversationId());
        intent.putExtra(MEMBER_ID, message.getFrom());
        NotificationUtils.showNotification(mContext, mContext.getString(R.string.application_name), notificationContent, null, intent);
    }
}

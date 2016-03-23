package com.bingo.riding.utils;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.bingo.riding.ChatActivity;
import com.bingo.riding.dao.Conversation;
import com.bingo.riding.event.ImTypeMessageEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by bingo on 15/12/30.
 */
public class ChatMessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage>{

    private AVIMConversation avimConversation;
    private Context mContext;
    private OnChatMessageCallback onChatMessageCallback;
    private DaoUtils daoUtils;

    public interface OnChatMessageCallback{
        void onChatMessage(AVIMTextMessage message, AVIMConversation conversation, AVIMClient client);
    }

    public ChatMessageHandler(Context mContext, OnChatMessageCallback onChatMessageCallback) {
        this.mContext = mContext;
        this.onChatMessageCallback = onChatMessageCallback;

        daoUtils = DaoUtils.getInstance(mContext);
    }

    public void setAvimConversation(AVIMConversation avimConversation) {
        this.avimConversation = avimConversation;
    }

    @Override
    public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        LogUtil.log.e("ChatMessageHandler---Message Content : " + message.toString());
        if (message == null || message.getMessageId() == null) {
            LogUtil.log.e("may be SDK Bug, message or message id is null");
            return;
        }
        if(conversation == null){
            LogUtil.log.e("receive msg from invalid conversation");
            return;
        }

        String clientId = "";
        try{
            clientId = AVImClientManager.getInstance().getClientId();
            if (client.getClientId().equals(clientId)){
                if (!message.getFrom().equals(clientId)){
                    if (conversation != null && avimConversation != null && avimConversation.getConversationId().equals(conversation.getConversationId())){
                        String messageContent = "";
                        if (message instanceof AVIMTextMessage){
                            AVIMTextMessage avimTextMessage = (AVIMTextMessage) message;
                            //先保存到数据库
                            saveToDB(avimTextMessage, conversation);

                            onChatMessageCallback.onChatMessage(avimTextMessage, conversation, client);
                        }
                    } else {
                        //存进数据库，发送广播出去
                        AVIMTextMessage avimTextMessage = (AVIMTextMessage) message;
                        saveToDB(avimTextMessage, conversation);
                        sendEvent(message, conversation);
                        if (NotificationUtils.isShowNotification(conversation.getConversationId())){
                            sendNotification(message, conversation);
                        }
                    }
                }
            } else {
                client.close(null);
            }
        }catch (IllegalStateException e){
            client.close(null);
        }
    }

    private void sendEvent(AVIMTypedMessage message, AVIMConversation conversation) {
        //发送事物
        ImTypeMessageEvent event = new ImTypeMessageEvent();
        event.message = message;
        event.conversation = conversation;
        EventBus.getDefault().post(event);
    }

    private void saveToDB(AVIMTextMessage message, AVIMConversation conversation){
        daoUtils.insertChatMessage(DataTools.getChatMessageFromAVIMTextMessage(message, false));

        Conversation insertConversation = DataTools.getConversationFromAVIMConversation(conversation);
        daoUtils.insertConversation(insertConversation);

        JSONObject jsonObject = JSON.parseObject(message.getText());
        try {
            AVUser avUser = (AVUser) AVUser.parseAVObject(jsonObject.getString("sendUser"));
            daoUtils.insertUser(DataTools.getUserFromAVUser(avUser));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void sendNotification(AVIMTypedMessage message, AVIMConversation conversation) {
        try {
            String notificationContent = message.getContent();

            LogUtil.log.e(notificationContent);

            JSONObject messageObject = JSON.parseObject(notificationContent);
            String messageContent = messageObject.getString("messageContent");
            String aa = messageObject.getString("sendUser");
            AVUser avUser = (AVUser) AVObject.parseAVObject(aa);
            String sendUserName = avUser.getString("nikeName");

            Intent intent = new Intent(mContext, ChatActivity.class);

            intent.putExtra("user", avUser);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            NotificationUtils.showNotification(mContext, sendUserName, messageContent, null, intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

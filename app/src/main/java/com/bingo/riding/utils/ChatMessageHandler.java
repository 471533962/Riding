package com.bingo.riding.utils;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;

/**
 * Created by bingo on 15/12/30.
 */
public class ChatMessageHandler extends AVIMMessageHandler{

    private AVIMConversation avimConversation;
    private Context mContext;
    private OnChatMessageCallback onChatMessageCallback;

    public interface OnChatMessageCallback{
        void onChatMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client);
    }

    public ChatMessageHandler(AVIMConversation avimConversation, Context mContext, OnChatMessageCallback onChatMessageCallback) {
        this.avimConversation = avimConversation;
        this.mContext = mContext;
        this.onChatMessageCallback = onChatMessageCallback;
    }

    @Override
    public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
        LogUtils.e("ChatMessageHandler---Message Content : " + message.toString());
        if (message == null || message.getMessageId() == null) {
            LogUtils.d("may be SDK Bug, message or message id is null");
            return;
        }
        if(conversation == null){
            LogUtils.d("receive msg from invalid conversation");
            return;
        }

        String clientId = "";
        try{
            clientId = AVImClientManager.getInstance().getClientId();
            if (client.getClientId().equals(clientId)){
                if (!message.getFrom().equals(clientId)){
                    if (conversation != null && avimConversation != null && avimConversation.getConversationId().equals(conversation.getConversationId())){
                        String messageContent = JSON.parseObject(message.getContent()).getString("messageContent");
                        message.setContent(messageContent);
                        onChatMessageCallback.onChatMessage(message, conversation, client);
                    } else {
                        //存进数据库，发送广播出去
                    }
                }
            } else {
                client.close(null);
            }
        }catch (IllegalStateException e){
            client.close(null);
        }
    }
}

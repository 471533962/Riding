package com.bingo.riding.event;

import com.bingo.riding.bean.Message;

/**
 * Created by bingo on 15/10/21.
 */
public class PublishMessageEvent {
    private Message message;
    private boolean isSuccess;

    public PublishMessageEvent(Message message, boolean isSuccess){
        this.message = message;
        this.isSuccess = isSuccess;
    }

    public Message getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}

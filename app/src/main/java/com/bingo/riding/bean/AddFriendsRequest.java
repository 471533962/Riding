package com.bingo.riding.bean;

import com.avos.avoscloud.AVUser;

/**
 * Created by bingo on 16/1/3.
 */
public class AddFriendsRequest {
    private AVUser fromUser;
    private AVUser toUser;
    private int status;
    private boolean isRead;

    public AVUser getFromUser() {
        return fromUser;
    }

    public void setFromUser(AVUser fromUser) {
        this.fromUser = fromUser;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public AVUser getToUser() {
        return toUser;
    }

    public void setToUser(AVUser toUser) {
        this.toUser = toUser;
    }
}

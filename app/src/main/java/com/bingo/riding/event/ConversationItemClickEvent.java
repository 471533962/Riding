package com.bingo.riding.event;

/**
 * Created by bingo on 16/1/5.
 */
public class ConversationItemClickEvent {
    public String memberId;
    public ConversationItemClickEvent(String memberId) {
        this.memberId = memberId;
    }
}

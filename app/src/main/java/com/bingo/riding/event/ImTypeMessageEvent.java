package com.bingo.riding.event;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;

/**
 * Created by wli on 15/8/23.
 */
public class ImTypeMessageEvent {
    public AVIMMessage message;
    public AVIMConversation conversation;
}
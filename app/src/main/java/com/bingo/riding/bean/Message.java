package com.bingo.riding.bean;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

import java.util.ArrayList;

/**
 * 这个类是java bean类，表示广场上发出的信息
 * Created by bingo on 15/10/12.
 */
public class Message{
    String content;
    ArrayList<String> photoList;
    AVUser poster;
    AVObject messageObject;

    public Message() {
    }



    public Message(String content, AVObject messageObject, ArrayList<String> photoList, AVUser poster) {
        this.content = content;
        this.messageObject = messageObject;
        this.photoList = photoList;
        this.poster = poster;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AVObject getMessageObject() {
        return messageObject;
    }

    public void setMessageObject(AVObject messageObject) {
        this.messageObject = messageObject;
    }

    public ArrayList<String> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(ArrayList<String> photoList) {
        this.photoList = photoList;
    }

    public AVUser getPoster() {
        return poster;
    }

    public void setPoster(AVUser poster) {
        this.poster = poster;
    }

}

package com.bingo.riding.bean;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bingo on 15/12/16.
 */
public class Discussion {
    private String discussionContent;
    private AVUser poster;
    private AVUser replier;
    private AVObject discussionObject;
    private List<Discussion> children = new ArrayList<>();

    public String getDiscussionContent() {
        return discussionContent;
    }

    public void setDiscussionContent(String discussionContent) {
        this.discussionContent = discussionContent;
    }

    public AVObject getDiscussionObject() {
        return discussionObject;
    }

    public void setDiscussionObject(AVObject discussionObject) {
        this.discussionObject = discussionObject;
    }

    public AVUser getPoster() {
        return poster;
    }

    public void setPoster(AVUser poster) {
        this.poster = poster;
    }

    public AVUser getReplier() {
        return replier;
    }

    public void setReplier(AVUser replier) {
        this.replier = replier;
    }

    public List<Discussion> getChildren() {
        return children;
    }

    public void setChildren(List<Discussion> children) {
        this.children = children;
    }
}

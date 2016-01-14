package com.bingo.riding.utils;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.bingo.riding.bean.Discussion;
import com.bingo.riding.bean.Message;
import com.bingo.riding.dao.ChatMessage;
import com.bingo.riding.dao.Conversation;
import com.bingo.riding.dao.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bingo on 15/10/9.
 * 这个类主要的作用是完成各个数据的转化
 */
public class DataTools {

    public static AVObject getSquareMessage(Context mContext, String content, List<String> imageList) throws IOException,AVException{
        AVObject squareMessage = new AVObject("squareMessage");

        List<AVFile> imageAVFileList = new LinkedList<>();
        for (String path : imageList){
//            try {
                File imageFile = new File(Utils.getThumbUploadPath(mContext, path, 480));
                AVFile file = AVFile.withFile(imageFile.getName(), imageFile);
                file.save();
                imageAVFileList.add(file);
//            }catch (IOException e){
//                e.printStackTrace();
//            }catch (AVException ave){
//                ave.printStackTrace();
//            }
        }
        squareMessage.put("content", content);
        squareMessage.addAll("images", imageAVFileList);
        squareMessage.put("poster", AVUser.getCurrentUser());

        return squareMessage;
    }

    public static Message getMessageFromAVObject(AVObject avObject){
        Message message = new Message();
        message.setMessageObject(avObject);

        message.setContent(avObject.getString("content"));
        message.setPoster(avObject.getAVUser("poster"));
        List<AVFile> fileList = avObject.getList("images");
        ArrayList<String> photoList = new ArrayList<>();
        if (fileList != null){
            for (AVFile avFile : fileList){
                photoList.add(avFile.getUrl());
            }
        }

        message.setPhotoList(photoList);

        return message;
    }

    public static Message getMessageFromJSONString(String jsonStr) throws Exception{
        AVObject avObject = AVObject.parseAVObject(jsonStr);
        return DataTools.getMessageFromAVObject(avObject);
    }

    public static Discussion getDiscussionFromAVObject(AVObject avObject) {
        Discussion discussion = new Discussion();
        discussion.setPoster(avObject.getAVUser("poster"));
        discussion.setReplier(avObject.getAVUser("replier"));
        discussion.setDiscussionContent(avObject.getString("content"));
        discussion.setDiscussionObject(avObject);

        return discussion;
    }

    public static ChatMessage getChatMessageFromAVIMTextMessage(AVIMTextMessage avimTextMessage, boolean isRead){
        ChatMessage chatMessage = new ChatMessage();
        JSONObject jsonObject = JSON.parseObject(avimTextMessage.getText());

        chatMessage.setContent(jsonObject.getString("messageContent"));
        chatMessage.setConversationId(avimTextMessage.getConversationId());
        chatMessage.setTimestamp(avimTextMessage.getTimestamp());
        chatMessage.setStatus(avimTextMessage.getMessageStatus().getStatusCode());
        chatMessage.setMessageId(avimTextMessage.getMessageId());
        chatMessage.setReceiptTimestamp(avimTextMessage.getReceiptTimestamp());
        chatMessage.setIoType(avimTextMessage.getMessageIOType().getIOType());
        chatMessage.setIsRead(isRead);

        return chatMessage;
    }

    public static ChatMessage getChatMessageFromAVIMMessage (AVIMMessage avimMessage, boolean isRead){
        ChatMessage chatMessage = new ChatMessage();
        JSONObject jsonObject = JSON.parseObject(avimMessage.getContent());

        chatMessage.setContent(jsonObject.getString("messageContent"));
        chatMessage.setConversationId(avimMessage.getConversationId());
        chatMessage.setTimestamp(avimMessage.getTimestamp());
        chatMessage.setStatus(avimMessage.getMessageStatus().getStatusCode());
        chatMessage.setMessageId(avimMessage.getMessageId());
        chatMessage.setReceiptTimestamp(avimMessage.getReceiptTimestamp());
        chatMessage.setIoType(avimMessage.getMessageIOType().getIOType());
        chatMessage.setIsRead(isRead);

        return chatMessage;
    }

    public static ChatMessage getChatMessageFromAVIMTypedMessage (AVIMTypedMessage avimMessage, boolean isRead){
        ChatMessage chatMessage = new ChatMessage();
        JSONObject jsonObject = JSON.parseObject(avimMessage.getContent());

        chatMessage.setContent(jsonObject.getString("messageContent"));
        chatMessage.setConversationId(avimMessage.getConversationId());
        chatMessage.setTimestamp(avimMessage.getTimestamp());
        chatMessage.setStatus(avimMessage.getMessageStatus().getStatusCode());
        chatMessage.setMessageId(avimMessage.getMessageId());
        chatMessage.setReceiptTimestamp(avimMessage.getReceiptTimestamp());
        chatMessage.setIoType(avimMessage.getMessageIOType().getIOType());
        chatMessage.setIsRead(isRead);

        return chatMessage;
    }

    public static String timeLogic(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.get(Calendar.DAY_OF_MONTH);
        long now = calendar.getTimeInMillis();
        calendar.setTime(date);
        long past = calendar.getTimeInMillis();

        // 相差的秒数
        long time = (now - past) / 1000;

        StringBuffer sb = new StringBuffer();
        if (time > 0 && time < 60) { // 1小时内
            return sb.append(time + "秒前").toString();
        } else if (time > 60 && time < 3600) {
            return sb.append(time / 60+"分钟前").toString();
        } else if (time >= 3600 && time < 3600 * 24) {
            return sb.append(time / 3600 +"小时前").toString();
        }else if (time >= 3600 * 24 && time < 3600 * 48) {
            return sb.append("昨天").toString();
        }else if (time >= 3600 * 48 && time < 3600 * 72) {
            return sb.append("前天").toString();
        }else if (time >= 3600 * 72) {
            long days = time / (3600 * 24) + 1;
            return sb.append(days + "天前").toString();
        }

        return sb.append("刚刚").toString();
    }

    public static JSONObject getJSONObjectFromUser(AVUser avUser){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nikeName", avUser.getString("nikeName"));
        jsonObject.put("userPhoto", avUser.getAVFile("userPhoto").getUrl());
        jsonObject.put("objectId", avUser.getObjectId());

        return jsonObject;
    }

    public static User getUserFromAVUser(AVUser avUser){
        User user = new User();
        user.setNikeName(avUser.getString("nikeName"));
        user.setMessage(avUser.getString("message"));
        user.setIsMale(avUser.getBoolean("isMale"));
        user.setObjectId(avUser.getObjectId());
        AVFile userPhoto = avUser.getAVFile("userPhoto");
        if (userPhoto != null){
            user.setUserPhoto(userPhoto.getUrl());
        }else{
            user.setUserPhoto(null);
        }

        return user;
    }

    public static User getUserFromAVObject(AVObject avObject){
        User user = new User();

        user.setNikeName(avObject.getString("nikeName"));
        user.setMessage(avObject.getString("message"));
        user.setIsMale(avObject.getBoolean("isMale"));
        user.setObjectId(avObject.getObjectId());
        AVFile userPhoto = avObject.getAVFile("userPhoto");
        if (userPhoto != null){
            user.setUserPhoto(userPhoto.getUrl());
        }else{
            user.setUserPhoto(null);
        }
        return user;
    }

    public static Conversation getConversationFromAVIMConversation(AVIMConversation avimConversation){
        Conversation conversation = new Conversation();

        conversation.setConversationId(avimConversation.getConversationId());
        conversation.setName(avimConversation.getName());
        conversation.setCreator(avimConversation.getCreator());

        JSONArray memberArray = new JSONArray();
        for (String user: avimConversation.getMembers()) {
            memberArray.add(user);
        }
        conversation.setMembers(memberArray.toJSONString());
        conversation.setUnReadNum(Long.getLong("0"));

        return conversation;
    }
}

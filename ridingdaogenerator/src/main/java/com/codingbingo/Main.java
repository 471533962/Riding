package com.codingbingo;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class Main {
    public static void main(String[] args) {

        Schema schema = new Schema(15, "com.bingo.riding.dao");

//        addChatMessage(schema);
        addConversation(schema);
//        addDiscussion(schema);
        addUser(schema);

        try {
            new DaoGenerator().generateAll(schema, "/Users/bingo/AndroidStudioWorkspace/Riding/app/src/main/java");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void addConversation(Schema schema){
        Entity conversation = schema.addEntity("Conversation");

        conversation.addStringProperty("conversationId").primaryKey();
        conversation.addStringProperty("name");
        conversation.addStringProperty("members");
        conversation.addStringProperty("creator");
        conversation.addStringProperty("attributes");
        conversation.addStringProperty("lastMessageAt");
//        conversation.addLongProperty("unReadNum");


        //聊天消息
        Entity chatMessage = schema.addEntity("ChatMessage");

        chatMessage.addIdProperty().autoincrement();
        chatMessage.addBooleanProperty("isRead").notNull();
        chatMessage.addStringProperty("content").notNull();
        chatMessage.addStringProperty("clientId");
        chatMessage.addStringProperty("messageId");
        chatMessage.addLongProperty("timestamp").notNull();
        chatMessage.addLongProperty("receiptTimestamp");
        chatMessage.addIntProperty("status");
        chatMessage.addIntProperty("ioType").notNull();
        Property conversationId = chatMessage.addStringProperty("conversationId").getProperty();
        chatMessage.addToOne(conversation, conversationId);
    }

    private static void addDiscussion(Schema schema){
        Entity discussion = schema.addEntity("Discussion");

        discussion.addIdProperty().autoincrement();
        discussion.addStringProperty("content");
        discussion.addStringProperty("poster");
        discussion.addStringProperty("replier");
        discussion.addDateProperty("updatedAt");
        discussion.addBooleanProperty("isRead");
    }

    private static void addUser(Schema schema){
        Entity user = schema.addEntity("User");

        user.addStringProperty("objectId").primaryKey();
        user.addStringProperty("userPhoto");
        user.addStringProperty("nikeName");
        user.addBooleanProperty("isMale");
        user.addStringProperty("message");
    }
}

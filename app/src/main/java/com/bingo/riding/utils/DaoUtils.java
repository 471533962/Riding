package com.bingo.riding.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bingo.riding.dao.ChatMessage;
import com.bingo.riding.dao.ChatMessageDao;
import com.bingo.riding.dao.DaoMaster;
import com.bingo.riding.dao.DaoSession;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by bingo on 15/12/17.
 */
public class DaoUtils {
    private final String DBNAME = "chatmessage-db";
    private static DaoUtils daoUtils;

    private ChatMessageDao chatMessageDao;

    private DaoUtils(Context context) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, DBNAME, null);
        SQLiteDatabase sqLiteDatabase = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(sqLiteDatabase);
        DaoSession daoSession = daoMaster.newSession();

        chatMessageDao = daoSession.getChatMessageDao();
    }

    public static DaoUtils getInstance(Context context){
        if (daoUtils == null){
            daoUtils = new DaoUtils(context);
        }

        return daoUtils;
    }

    public void insertChatMessage(ChatMessage chatMessage){
        chatMessageDao.insert(chatMessage);
    }

    public void updateReadChatMessage(ChatMessage chatMessage){
        chatMessageDao.update(chatMessage);
    }

    public List<ChatMessage> getMessagesAccordingToConversationId(String conversationId){
        QueryBuilder queryBuilder = chatMessageDao.queryBuilder();
        queryBuilder.orderAsc(ChatMessageDao.Properties.Timestamp);
        queryBuilder.limit(20);
        queryBuilder.where(ChatMessageDao.Properties.ConversationId.eq(conversationId));
        return queryBuilder.list();
    }

}

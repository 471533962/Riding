package com.bingo.riding.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TableRow;

import com.bingo.riding.dao.ChatMessage;
import com.bingo.riding.dao.ChatMessageDao;
import com.bingo.riding.dao.Conversation;
import com.bingo.riding.dao.ConversationDao;
import com.bingo.riding.dao.DaoMaster;
import com.bingo.riding.dao.DaoSession;
import com.bingo.riding.dao.User;
import com.bingo.riding.dao.UserDao;
import com.bingo.riding.interfaces.OnConversationChangeListener;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by bingo on 15/12/17.
 */
public class DaoUtils {
    private final String DBNAME = "chatmessage-db";
    private static DaoUtils daoUtils;

    private ChatMessageDao chatMessageDao;
    private UserDao userDao;
    private ConversationDao conversationDao;
    private List<OnConversationChangeListener> onConversationChangeListenerList = new ArrayList<>();

    private DaoUtils(Context context) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, DBNAME, null);
        SQLiteDatabase sqLiteDatabase = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(sqLiteDatabase);
        DaoSession daoSession = daoMaster.newSession();

        chatMessageDao = daoSession.getChatMessageDao();
        userDao = daoSession.getUserDao();
        conversationDao = daoSession.getConversationDao();

    }

    public static DaoUtils getInstance(Context context){
        if (daoUtils == null){
            daoUtils = new DaoUtils(context);
        }

        return daoUtils;
    }

    public void insertChatMessage(ChatMessage chatMessage){
        chatMessageDao.insertOrReplace(chatMessage);

        notifyConversationChange(true, false);
    }

    public List<ChatMessage> getMessagesAccordingToConversationId(String conversationId){
        QueryBuilder queryBuilder = chatMessageDao.queryBuilder();
        queryBuilder.orderDesc(ChatMessageDao.Properties.Timestamp);
        queryBuilder.limit(20);
        queryBuilder.where(ChatMessageDao.Properties.ConversationId.eq(conversationId));
        return queryBuilder.list();
    }

    public void updateUnreadAccordingToConversationId(String conversationId){
        QueryBuilder queryBuilder = chatMessageDao.queryBuilder();
        queryBuilder.where(ChatMessageDao.Properties.ConversationId.eq(conversationId));
        queryBuilder.where(ChatMessageDao.Properties.IsRead.eq(false));
        List<ChatMessage> chatMessageList = queryBuilder.list();
        for (ChatMessage chatMessage : chatMessageList){
            chatMessage.setIsRead(true);
            chatMessageDao.insertOrReplace(chatMessage);
        }

        notifyConversationChange(true, false);
    }

    public void insertUser(User user){
        userDao.insertOrReplace(user);
    }

    public void deleteUsers(){
        userDao.deleteAll();
    }

    public void deleteChatMessages(){
        chatMessageDao.deleteAll();

        notifyConversationChange(true, false);
    }

    public void deleteConversations(){
        conversationDao.deleteAll();

        notifyConversationChange(false, true);
    }

    /**
     * 如果不存在则返回空的List
     * @param userId user主键objectId
     * @return user的list列表
     */
    public List<User> getUserById(String userId){
        QueryBuilder queryBuilder = userDao.queryBuilder();
        queryBuilder.where(UserDao.Properties.ObjectId.eq(userId));
        queryBuilder.limit(1);
        return queryBuilder.list();
    }

    public void insertConversation(Conversation conversation){
        conversationDao.insertOrReplace(conversation);

        notifyConversationChange(false, true);
    }

    /**
     * 如果不存在则返回空的List
     * @param conversationId
     * @return
     */
    public List<Conversation> getConversationById(String conversationId){
        QueryBuilder queryBuilder = conversationDao.queryBuilder();
        queryBuilder.where(ConversationDao.Properties.ConversationId.eq(conversationId));
        queryBuilder.limit(1);
        return queryBuilder.list();
    }

    public List<Conversation> getAllConversations(){
        return conversationDao.loadAll();
    }

    public ChatMessage getConversationLastMessage(String conversationId){
        QueryBuilder queryBuilder = chatMessageDao.queryBuilder();
        queryBuilder.where(ChatMessageDao.Properties.ConversationId.eq(conversationId));
        queryBuilder.orderDesc(ChatMessageDao.Properties.Timestamp);
        queryBuilder.limit(1);
        List<ChatMessage> chatMessageList = queryBuilder.list();
        if (chatMessageList.isEmpty()){
            return null;
        }

        return chatMessageList.get(0);
    }

    public long getUnreadChatMessageNumber(String conversationId){
        QueryBuilder chatMessageQueryBuilder = chatMessageDao.queryBuilder();
        chatMessageQueryBuilder.where(ChatMessageDao.Properties.ConversationId.eq(conversationId));
        chatMessageQueryBuilder.where(ChatMessageDao.Properties.IsRead.eq(false));
        return chatMessageQueryBuilder.count();
    }

    public void updateConversationChatMessage(String conversationId){
        QueryBuilder chatMessageQueryBuilder = chatMessageDao.queryBuilder();
        chatMessageQueryBuilder.where(ChatMessageDao.Properties.ConversationId.eq(conversationId));
        chatMessageQueryBuilder.where(ChatMessageDao.Properties.IsRead.eq(false));
        for (ChatMessage chatmessage: (List<ChatMessage>)chatMessageQueryBuilder.list()) {
            chatmessage.setIsRead(true);
            chatMessageDao.update(chatmessage);
        }

        notifyConversationChange(true, false);
    }

    private void notifyConversationChange(boolean isChatMessageChange, boolean isConversationChange){
        for (OnConversationChangeListener conversationChangeListener: onConversationChangeListenerList){
            if (isChatMessageChange) {
                conversationChangeListener.onChatMessageChange();
            }

            if(isConversationChange){
                conversationChangeListener.onConversationChange();
            }
        }
    }

    public void addNotifyConversationListener(OnConversationChangeListener onConversationChangeListener){
        if (onConversationChangeListenerList.contains(onConversationChangeListener) == false){
            onConversationChangeListenerList.add(onConversationChangeListener);
        }
    }
}

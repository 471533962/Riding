package com.bingo.riding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.bingo.riding.adapter.ChatMessageListAdapter;
import com.bingo.riding.dao.ChatMessage;
import com.bingo.riding.event.ImTypeMessageEvent;
import com.bingo.riding.event.InputBottomBarTextEvent;
import com.bingo.riding.utils.AVImClientManager;
import com.bingo.riding.utils.DaoUtils;
import com.bingo.riding.utils.DataTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ChatActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView chat_message_list;
    private LinearLayoutManager layoutManager;

    private DaoUtils daoUtils;
    private AVUser avUser;
    private AVIMConversation avimConversation;

    private List<ChatMessage> chatMessageList = new ArrayList<>();
    private ChatMessageListAdapter chatMessageListAdapter;
    private HashMap<String, String> userPic = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        avUser = intent.getParcelableExtra("user");
        EventBus.getDefault().register(this);

        userPic.put(ChatMessageListAdapter.OTHER_USER_PIC, avUser.getAVFile("userPhoto").getUrl());
        userPic.put(ChatMessageListAdapter.SELF_USER_PIC, AVUser.getCurrentUser().getAVFile("userPhoto").getUrl());

        daoUtils = DaoUtils.getInstance(getApplicationContext());

        init();
        initView();
    }

    private void init(){
        AVImClientManager
                .getInstance()
                .getClient()
                .createConversation(
                        Arrays.asList(avUser.getObjectId()),
                        "",
                        null,
                        false,
                        true,
                        new AVIMConversationCreatedCallback() {
                            @Override
                            public void done(AVIMConversation avimConversation, AVIMException e) {
                                if (e != null){
                                    e.printStackTrace();
                                    finish();
                                    return;
                                } else {
                                    ChatActivity.this.avimConversation = avimConversation;
                                    chatMessageList.addAll(DaoUtils
                                            .getInstance(getApplicationContext())
                                            .getMessagesAccordingToConversationId(avimConversation.getConversationId()));
                                    chatMessageListAdapter.notifyDataSetChanged();
                                }
                            }
                        });
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(avUser.getString("nikeName"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layoutManager = new LinearLayoutManager(this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        chat_message_list = (RecyclerView) findViewById(R.id.chat_message_list);

        chatMessageListAdapter = new ChatMessageListAdapter(getApplicationContext(), chatMessageList, userPic);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chat_message_list.setLayoutManager(layoutManager);
        chat_message_list.setAdapter(chatMessageListAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {

    }

    /**
     * 输入事件处理，接收后构造成 AVIMTextMessage 然后发送
     * 因为不排除某些特殊情况会受到其他页面过来的无效消息，所以此处加了 tag 判断
     */
    public void onEvent(InputBottomBarTextEvent textEvent) {
        if (null != avimConversation && null != textEvent) {
            if (!TextUtils.isEmpty(textEvent.sendContent) && avimConversation.getConversationId().equals(textEvent.tag)) {
                final AVIMTextMessage message = new AVIMTextMessage();
                message.setText(textEvent.sendContent);
                final ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent(textEvent.sendContent);
                chatMessage.setIsSendByUser(true);
                chatMessageList.add(chatMessage);
                chatMessageListAdapter.notifyDataSetChanged();
                scrollToBottom();

                avimConversation.sendMessage(message, new AVIMConversationCallback() {
                    @Override
                    public void done(AVIMException e) {
                        chatMessageListAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    /**
     * 处理推送过来的消息
     * 同理，避免无效消息，此处加了 conversation id 判断
     */
    public void onEvent(ImTypeMessageEvent event) {
        if (null != avimConversation && null != event &&
                avimConversation.getConversationId().equals(event.conversation.getConversationId())) {
            chatMessageList.add(DataTools.getChatMessageFromAVIMTypedMessage(event.message, true));
            chatMessageListAdapter.notifyDataSetChanged();
            scrollToBottom();
        }
    }

    private void scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(chatMessageListAdapter.getItemCount() - 1, 0);
    }

    public String getConversationId(){
        return avimConversation.getConversationId();
    }

}

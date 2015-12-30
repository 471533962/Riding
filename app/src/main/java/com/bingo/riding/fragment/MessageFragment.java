package com.bingo.riding.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.Conversation;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.bingo.riding.R;
import com.bingo.riding.adapter.PersonalMessageAdapter;
import com.bingo.riding.dao.ChatMessage;
import com.bingo.riding.utils.AVImClientManager;
import com.bingo.riding.utils.DaoUtils;

import java.util.HashMap;
import java.util.List;

public class MessageFragment extends Fragment {

    private View view;
    private ListView fragment_message_message_list;

    private DaoUtils daoUtils;
    private HashMap<Conversation, ChatMessage> conversationChatMessageHashMap = new HashMap<>();
    private PersonalMessageAdapter personalMessageAdapter;


    public MessageFragment() {
        AVIMConversationQuery query = AVImClientManager.getInstance().getClient().getQuery();
        query.limit(20);
        query.findInBackground(new AVIMConversationQueryCallback(){
            @Override
            public void done(List<AVIMConversation> convs, AVIMException e){
                if(e==null){
                    //convs就是获取到的conversation列表
                    //注意：按每个对话的最后更新日期（收到最后一条消息的时间）倒序排列
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        daoUtils = DaoUtils.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);


        init();
        initView();
        return view;
    }

    private void init(){

    }

    private void initView(){
        fragment_message_message_list = (ListView) view.findViewById(R.id.fragment_message_message_list);
        personalMessageAdapter = new PersonalMessageAdapter();
    }
}

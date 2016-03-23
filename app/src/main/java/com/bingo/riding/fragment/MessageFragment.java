package com.bingo.riding.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bingo.riding.ChatActivity;
import com.bingo.riding.R;
import com.bingo.riding.adapter.ConversationListAdapter;
import com.bingo.riding.adapter.PersonalMessageAdapter;
import com.bingo.riding.dao.ChatMessage;
import com.bingo.riding.dao.Conversation;
import com.bingo.riding.event.ConversationItemClickEvent;
import com.bingo.riding.event.ImTypeMessageEvent;
import com.bingo.riding.utils.Constants;
import com.bingo.riding.utils.DaoUtils;
import com.bingo.riding.utils.DividerItemDecoration;

import java.util.List;

import de.greenrobot.event.EventBus;

public class MessageFragment extends Fragment {

    private View view;
    private RecyclerView fragment_message_message_list;

    private DaoUtils daoUtils;
    private List<Conversation> conversationList;
    private ConversationListAdapter conversationListAdapter;


    public MessageFragment() {

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
    }

    private void init(){
        conversationList = daoUtils.getAllConversations();
    }

    private void initView(){
        fragment_message_message_list = (RecyclerView) view.findViewById(R.id.fragment_message_message_list);
        fragment_message_message_list.setLayoutManager(new LinearLayoutManager(getActivity()));

        conversationListAdapter = new ConversationListAdapter(conversationList);
        fragment_message_message_list.setAdapter(conversationListAdapter);
        fragment_message_message_list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
    }

    public void onEvent(ConversationItemClickEvent event) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.MEMBER_ID, event.memberId);
        startActivity(intent);
    }

    public void onEvent(ImTypeMessageEvent imTypeMessageEvent){
        List<Conversation> list = daoUtils.getAllConversations();
        conversationList.clear();
        conversationList.addAll(list);
        conversationListAdapter.notifyDataSetChanged();
    }
}

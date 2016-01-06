package com.bingo.riding.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bingo.riding.R;
import com.bingo.riding.dao.Conversation;
import com.bingo.riding.viewholder.ConversationViewHolder;

import java.util.List;

/**
 * Created by bingo on 16/1/5.
 */
public class ConversationListAdapter extends RecyclerView.Adapter<ConversationViewHolder> {

    private List<Conversation> conversationList;

    public ConversationListAdapter(List<Conversation> conversationList) {
        this.conversationList = conversationList;
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_item, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder holder, int position) {
        if (position >= 0 && position < conversationList.size()) {
            holder.bindData(conversationList.get(position));
        }
    }
}

package com.bingo.riding.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bingo.riding.R;
import com.bingo.riding.bean.AddFriendsRequest;
import com.bingo.riding.viewholder.NewFriendViewHolder;

import java.util.List;

/**
 * Created by bingo on 16/1/7.
 */
public class NewFriendsListAdapter extends RecyclerView.Adapter<NewFriendViewHolder>{

    private List<AddFriendsRequest> addFriendsRequestList;

    public NewFriendsListAdapter(List<AddFriendsRequest> addFriendsRequestList) {
        this.addFriendsRequestList = addFriendsRequestList;
    }

    @Override
    public int getItemCount() {
        return addFriendsRequestList.size();
    }

    @Override
    public NewFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_add_friend_item, parent, false);

        NewFriendViewHolder newFriendViewHolder = new NewFriendViewHolder(view);

        return newFriendViewHolder;
    }

    @Override
    public void onBindViewHolder(NewFriendViewHolder holder, int position) {
        AddFriendsRequest addFriendsRequest = addFriendsRequestList.get(position);

        holder.bindData(addFriendsRequest);
    }


}

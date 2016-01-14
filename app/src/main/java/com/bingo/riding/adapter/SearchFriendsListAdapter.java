package com.bingo.riding.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bingo.riding.R;
import com.bingo.riding.dao.User;
import com.bingo.riding.viewholder.SearchFriendViewHolder;

import java.util.List;

/**
 * Created by bingo on 16/1/14.
 */
public class SearchFriendsListAdapter extends RecyclerView.Adapter<SearchFriendViewHolder> {

    private List<User> searchFriendsList;

    public SearchFriendsListAdapter(List<User> searchFriendsList) {
        this.searchFriendsList = searchFriendsList;
    }

    @Override
    public int getItemCount() {
        return searchFriendsList.size();
    }

    @Override
    public SearchFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_friends_list_item, parent, false);

        return new SearchFriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchFriendViewHolder holder, int position) {
        holder.bindData(searchFriendsList.get(position));
    }
}

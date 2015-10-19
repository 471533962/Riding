package com.bingo.riding.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bingo.riding.R;
import com.bingo.riding.bean.Message;

import java.util.List;

/**
 * Created by bingo on 15/10/9.
 */
public class SquareListAdapter extends RecyclerView.Adapter<SquareListAdapter.ViewHolder> {
    private List<Message> messageList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView message_publisher_name;
        TextView message_content;
        TextView message_publish_time;
        GridView message_photos;
        ImageView message_publisher_photo;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_square_message_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }
}



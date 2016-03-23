package com.bingo.riding.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.bingo.riding.MessageDetailActivity;
import com.bingo.riding.R;
import com.bingo.riding.bean.Discussion;
import com.bingo.riding.bean.Message;
import com.bingo.riding.utils.DataTools;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.util.List;

/**
 * Created by bingo on 15/12/30.
 * MessageManagerActivity里面评论列表的适配器
 */
public class DiscussionListAdapter extends RecyclerView.Adapter<DiscussionListAdapter.ViewHolder> {
    private Context mContext;
    private List<Discussion> discussionList;

    public DiscussionListAdapter(List<Discussion> discussionList, Context mContext) {
        this.discussionList = discussionList;
        this.mContext = mContext;
    }

    @Override
    public int getItemCount() {
        return discussionList.size();
    }

    @Override
    public DiscussionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_manger_discuss_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DiscussionListAdapter.ViewHolder holder, int position) {
        Discussion discussion = discussionList.get(position);

        holder.message_publisher_content.setText(discussion.getDiscussionContent());
        holder.message_publisher_name.setText(discussion.getPoster().getString("nikeName"));
        holder.message_publisher_time.setText(DataTools.timeLogic(discussion.getDiscussionObject().getCreatedAt()));
        AVFile userPhoto = discussion.getPoster().getAVFile("userPhoto");
        if (userPhoto != null){
            Glide.with(mContext.getApplicationContext())
                    .load(userPhoto.getUrl())
                    .signature(new StringSignature(userPhoto.getUrl()))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.loaderror)
                    .centerCrop()
                    .into(holder.message_publisher_photo);
        } else {
            holder.message_publisher_photo.setImageResource(R.drawable.default_photo);
        }

        Message message = DataTools.getMessageFromAVObject(discussion.getDiscussionObject().getAVObject("centerMessage"));
        if (message.getPhotoList().size() > 0){
            holder.message_content.setVisibility(View.GONE);
            holder.message_photo.setVisibility(View.VISIBLE);

            Glide.with(mContext.getApplicationContext())
                    .load(message.getPhotoList().get(0))
                    .signature(new StringSignature(message.getPhotoList().get(0)))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.loaderror)
                    .override(250, 250)
                    .centerCrop()
                    .into(holder.message_photo);
        }else{
            holder.message_photo.setVisibility(View.GONE);
            holder.message_content.setVisibility(View.VISIBLE);

            holder.message_content.setText(message.getContent());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView message_content;
        private ImageView message_photo;
        private ImageView message_publisher_photo;
        private TextView message_publisher_name;
        private TextView message_publisher_content;
        private TextView message_publisher_time;

        public ViewHolder(View itemView) {
            super(itemView);

            message_content = (TextView)itemView.findViewById(R.id.message_content);
            message_publisher_name = (TextView)itemView.findViewById(R.id.message_publisher_name);
            message_publisher_content = (TextView)itemView.findViewById(R.id.message_publisher_content);
            message_publisher_time = (TextView)itemView.findViewById(R.id.message_publisher_time);
            message_photo = (ImageView)itemView.findViewById(R.id.message_photo);
            message_publisher_photo = (ImageView)itemView.findViewById(R.id.message_publisher_photo);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, MessageDetailActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("message", discussionList.get(getAdapterPosition()).getDiscussionObject().getAVObject("centerMessage").toString());

            intent.putExtra("bundle", bundle);

            mContext.startActivity(intent);
        }
    }
}

package com.bingo.riding.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.bingo.riding.R;
import com.bingo.riding.dao.ChatMessage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.util.HashMap;
import java.util.List;

/**
 * Created by bingo on 15/12/17.
 */
public class ChatMessageListAdapter extends RecyclerView.Adapter<ChatMessageListAdapter.ViewHolder> {
    public static String SELF_USER_PIC = "self_pic";
    public static String OTHER_USER_PIC = "user_pic";

    private List<ChatMessage> messageList;
    private Context mContext;
    private HashMap<String, String> userPicHash;

    public ChatMessageListAdapter(Context mContext, List<ChatMessage> messageList, HashMap<String, String> userPicHash) {
        this.mContext = mContext;
        this.messageList = messageList;
        this.userPicHash = userPicHash;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView chat_item_me_content;
        private TextView chat_item_user_content;
        private ImageView chat_item_user_pic;
        private ImageView chat_item_me_pic;

        public ViewHolder(View itemView) {
            super(itemView);

            chat_item_me_content = (TextView) itemView.findViewById(R.id.chat_item_me_content);
            chat_item_user_content = (TextView) itemView.findViewById(R.id.chat_item_user_content);
            chat_item_user_pic = (ImageView) itemView.findViewById(R.id.chat_item_user_pic);
            chat_item_me_pic = (ImageView) itemView.findViewById(R.id.chat_item_me_pic);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMessage chatMessage = messageList.get(position);

        if (chatMessage.getIoType() == AVIMMessage.AVIMMessageIOType.AVIMMessageIOTypeIn.getIOType()) {
            //收到的
            holder.chat_item_me_content.setText(chatMessage.getContent());
            if (userPicHash.get(OTHER_USER_PIC) != null){
                Glide.with(mContext.getApplicationContext())
                        .load(userPicHash.get(OTHER_USER_PIC))
                        .signature(new StringSignature(userPicHash.get(SELF_USER_PIC)))
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .placeholder(R.drawable.a0c)
                        .error(R.drawable.default_error)
                        .override(150, 150)
                        .centerCrop()
                        .into(holder.chat_item_me_pic);
            }else{
                holder.chat_item_me_pic.setImageResource(R.drawable.test_user_pic);
            }

            holder.chat_item_me_pic.setVisibility(View.VISIBLE);
            holder.chat_item_me_content.setVisibility(View.VISIBLE);
            holder.chat_item_user_content.setVisibility(View.GONE);
            holder.chat_item_user_pic.setVisibility(View.GONE);
        } else {
            //自己发的
            holder.chat_item_user_content.setText(chatMessage.getContent());
            if (userPicHash.get(SELF_USER_PIC) != null){
                Glide.with(mContext.getApplicationContext())
                        .load(userPicHash.get(SELF_USER_PIC))
                        .signature(new StringSignature(userPicHash.get(OTHER_USER_PIC)))
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .placeholder(R.drawable.a0c)
                        .error(R.drawable.default_error)
                        .override(150, 150)
                        .centerCrop()
                        .into(holder.chat_item_user_pic);
            }else{
                holder.chat_item_user_pic.setImageResource(R.drawable.test_user_pic);
            }

            holder.chat_item_me_pic.setVisibility(View.GONE);
            holder.chat_item_me_content.setVisibility(View.GONE);
            holder.chat_item_user_content.setVisibility(View.VISIBLE);
            holder.chat_item_user_pic.setVisibility(View.VISIBLE);
        }
    }
}

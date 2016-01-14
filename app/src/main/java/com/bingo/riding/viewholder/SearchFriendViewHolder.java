package com.bingo.riding.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bingo.riding.R;
import com.bingo.riding.dao.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

/**
 * Created by bingo on 16/1/14.
 */
public class SearchFriendViewHolder extends RecyclerView.ViewHolder {
    ImageView avatarView;
    TextView unreadView;
    TextView messageView;
    TextView nameView;
    RelativeLayout avatarLayout;
    LinearLayout contentLayout;

    private Context mContext;

    public SearchFriendViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();

        initView();
    }

    private void initView(){
        avatarView = (ImageView)itemView.findViewById(R.id.search_friends_item_iv_avatar);
        nameView = (TextView)itemView.findViewById(R.id.search_friends_item_tv_name);
        unreadView = (TextView)itemView.findViewById(R.id.search_friends_item_tv_unread);
        messageView = (TextView)itemView.findViewById(R.id.search_friends_item_tv_message);
        avatarLayout = (RelativeLayout)itemView.findViewById(R.id.search_friends_item_layout_avatar);
        contentLayout = (LinearLayout)itemView.findViewById(R.id.search_friends_item_layout_content);
    }

    public void bindData(User user){
        if (user.getUserPhoto() != null && user.getUserPhoto().length() != 0){
            Glide.with(mContext.getApplicationContext())
                    .load(user.getUserPhoto())
                    .signature(new StringSignature(user.getUserPhoto()))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.a0c)
                    .error(R.drawable.default_error)
                    .into(avatarView);
        }
        nameView.setText(user.getNikeName());
        messageView.setText(user.getMessage());
    }
}

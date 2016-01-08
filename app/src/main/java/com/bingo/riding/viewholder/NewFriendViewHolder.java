package com.bingo.riding.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.bingo.riding.R;
import com.bingo.riding.bean.AddFriendsRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

/**
 * Created by bingo on 16/1/7.
 */
public class NewFriendViewHolder extends RecyclerView.ViewHolder {
    TextView nameView;
    ImageView avatarView;
    Button addBtn;
    TextView agreedView;
    private AddFriendsRequest addFriendsRequest;
    private Context mContext;

    public NewFriendViewHolder(View itemView) {
        super(itemView);

        mContext = itemView.getContext();
        nameView = (TextView)itemView.findViewById(R.id.name);
        avatarView = (ImageView)itemView.findViewById(R.id.avatar);
        addBtn = (Button)itemView.findViewById(R.id.add);
        agreedView = (TextView) itemView.findViewById(R.id.agreedView);
    }

    public void bindData(AddFriendsRequest addFriendsRequest){
        this.addFriendsRequest = addFriendsRequest;

        AVFile userPhoto = addFriendsRequest.getFromUser().getAVFile("userPhoto");
        if (userPhoto != null){
            Glide.with(mContext.getApplicationContext())
                    .load(userPhoto.getUrl())
                    .signature(new StringSignature(userPhoto.getUrl()))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.a0c)
                    .error(R.drawable.default_error)
                    .into(avatarView);
        }else{
            avatarView.setImageResource(R.drawable.default_photo);
        }

        int status = addFriendsRequest.getStatus();
        if (status == AddFriendsRequest.STATUS_WAIT) {
            addBtn.setVisibility(View.VISIBLE);
            agreedView.setVisibility(View.GONE);
        } else {
            addBtn.setVisibility(View.GONE);
            agreedView.setVisibility(View.VISIBLE);

            if (status == AddFriendsRequest.STATUS_ACCEPT){
                agreedView.setText("已同意");
            } else if(status == AddFriendsRequest.STATUS_REFUSE){
                agreedView.setText("已拒绝");
            }
        }
    }
}

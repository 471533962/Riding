package com.bingo.riding.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMSingleMessageQueryCallback;
import com.bingo.riding.R;
import com.bingo.riding.dao.ChatMessage;
import com.bingo.riding.dao.Conversation;
import com.bingo.riding.dao.User;
import com.bingo.riding.event.ConversationItemClickEvent;
import com.bingo.riding.utils.DaoUtils;
import com.bingo.riding.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by bingo on 16/1/5.
 */
public class ConversationViewHolder extends RecyclerView.ViewHolder{
    ImageView avatarView;
    TextView unreadView;
    TextView messageView;
    TextView timeView;
    TextView nameView;
    RelativeLayout avatarLayout;
    LinearLayout contentLayout;

    Conversation conversation;
    private DaoUtils daoUtils;
    private Context mContext;

    public ConversationViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();

        initView();
        daoUtils = DaoUtils.getInstance(mContext);
    }

    public void initView() {
        avatarView = (ImageView)itemView.findViewById(R.id.conversation_item_iv_avatar);
        nameView = (TextView)itemView.findViewById(R.id.conversation_item_tv_name);
        timeView = (TextView)itemView.findViewById(R.id.conversation_item_tv_time);
        unreadView = (TextView)itemView.findViewById(R.id.conversation_item_tv_unread);
        messageView = (TextView)itemView.findViewById(R.id.conversation_item_tv_message);
        avatarLayout = (RelativeLayout)itemView.findViewById(R.id.conversation_item_layout_avatar);
        contentLayout = (LinearLayout)itemView.findViewById(R.id.conversation_item_layout_content);
    }

    public void bindData(final Conversation conversation){
        this.conversation = conversation;

        if (null != conversation) {
            String userId = Utils.otherIdOfConversation(conversation);
            if (userId == null){
                //出问题了
                return;
            }
            List<User> users = daoUtils.getUserById(userId);
            if (users.isEmpty()){
                //说明还没有缓存到数据库中
            }else{
                //已经存在于数据空中
                User user = users.get(0);
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
                if (conversation.getUnReadNum() != null){
                    unreadView.setText(conversation.getUnReadNum() + "");
                    unreadView.setVisibility(conversation.getUnReadNum() > 0 ? View.VISIBLE : View.GONE);
                }else {
                    unreadView.setVisibility(View.GONE);
                }
            }


            ChatMessage lastMessage = daoUtils.getConversationLastMessage(conversation.getConversationId());
            if (lastMessage != null){
                Date date = new Date(lastMessage.getTimestamp());
                SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
                timeView.setText(format.format(date));
                messageView.setText(lastMessage.getContent());
            }else{
                timeView.setText("");
                messageView.setText("");
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(
                            new ConversationItemClickEvent(
                                    Utils.otherIdOfConversation(conversation)
                            ));
                }
            });
        }
    }
}

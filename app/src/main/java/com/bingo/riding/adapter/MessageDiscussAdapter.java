package com.bingo.riding.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.model.Text;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.bingo.riding.PersonalIndexActivity;
import com.bingo.riding.R;
import com.bingo.riding.bean.Discussion;
import com.bingo.riding.interfaces.OnDiscussionContentClickListener;
import com.bingo.riding.utils.DataTools;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.linearlistview.LinearListView;

import java.util.List;

/**
 * Created by bingo on 12/5/15.
 */
public class MessageDiscussAdapter extends BaseAdapter {
    private List<Discussion> discussions;
    private Context context;
    private OnDiscussionContentClickListener onDiscussionContentClickListener;

    public MessageDiscussAdapter(Context context, List<Discussion> discussions, OnDiscussionContentClickListener onDiscussionContentClickListener) {
        this.context = context;
        this.discussions = discussions;
        this.onDiscussionContentClickListener = onDiscussionContentClickListener;
    }

    @Override
    public int getCount() {
        return discussions.size();
    }

    @Override
    public Object getItem(int position) {
        return discussions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final Discussion discussion = (Discussion) getItem(position);
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.message_discuss_item, parent, false);

            viewHolder.message_publisher_time = (TextView) convertView.findViewById(R.id.message_publisher_time);
            viewHolder.message_publisher_name = (TextView) convertView.findViewById(R.id.message_publisher_name);
            viewHolder.message_publisher_photo = (ImageView) convertView.findViewById(R.id.message_publisher_photo);
            viewHolder.message_publisher_content = (TextView) convertView.findViewById(R.id.message_publisher_content);
            viewHolder.list = (LinearListView) convertView.findViewById(R.id.list);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.message_publisher_time.setText(DataTools.timeLogic(discussion.getDiscussionObject().getCreatedAt()));
        viewHolder.message_publisher_name.setText(discussion.getPoster().getString("nikeName"));
        viewHolder.message_publisher_content.setText(discussion.getDiscussionContent());
        AVFile userPhoto = discussion.getPoster().getAVFile("userPhoto");
        if (userPhoto != null){
            Glide.with(context.getApplicationContext())
                    .load(userPhoto.getUrl())
                    .signature(new StringSignature(userPhoto.getUrl()))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.a0c)
                    .error(R.drawable.default_error)
                    .centerCrop()
                    .into(viewHolder.message_publisher_photo);
        } else {
            viewHolder.message_publisher_photo.setImageResource(R.drawable.default_photo);
        }

        viewHolder.message_publisher_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDiscussionContentClickListener.discussionClickListener(discussion);
            }
        });

        if (discussion.getChildren() != null && discussion.getChildren().isEmpty() == false){
            ChildrenDiscussionAdapter childrenDiscussionAdapter = new ChildrenDiscussionAdapter(discussion, context, onDiscussionContentClickListener);
            viewHolder.list.setVisibility(View.VISIBLE);
            viewHolder.list.setAdapter(childrenDiscussionAdapter);
        }

        return convertView;
    }

    private class ViewHolder{
        private ImageView message_publisher_photo;
        private TextView message_publisher_name;
        private TextView message_publisher_time;
        private TextView message_publisher_content;
        private LinearListView list;
    }

    private class ChildrenDiscussionAdapter extends BaseAdapter{
        private Discussion discussion;
        private Context mContext;
        private OnDiscussionContentClickListener onDiscussionContentClickListener;

        private LayoutInflater layoutInflater;

        public ChildrenDiscussionAdapter(Discussion discussion, Context mContext, OnDiscussionContentClickListener onDiscussionContentClickListener) {
            this.discussion = discussion;
            this.mContext = mContext;
            this.onDiscussionContentClickListener = onDiscussionContentClickListener;

            layoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return discussion.getChildren().size();
        }

        @Override
        public Object getItem(int position) {
            return discussion.getChildren().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Discussion discuss = (Discussion) getItem(position);

            ChildViewHolder childViewHolder;
            if (convertView == null){
                childViewHolder = new ChildViewHolder();
                convertView = layoutInflater.inflate(R.layout.children_discussion_item, parent, false);

                childViewHolder.poster_name = (TextView) convertView.findViewById(R.id.poster_name);
                childViewHolder.replier_name = (TextView) convertView.findViewById(R.id.replier_name);
                childViewHolder.reply_content = (TextView) convertView.findViewById(R.id.reply_content);

                convertView.setTag(childViewHolder);
            }else{
                childViewHolder = (ChildViewHolder) convertView.getTag();
            }

            childViewHolder.poster_name.setText(discuss.getPoster().getString("nikeName"));
            childViewHolder.replier_name.setText(discuss.getReplier().getString("nikeName") + ":");
            childViewHolder.reply_content.setText(discuss.getDiscussionContent());

            childViewHolder.reply_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDiscussionContentClickListener.discussionClickListener(discussion);
                }
            });
            childViewHolder.poster_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AVUser.getCurrentUser().getObjectId().equals(discuss.getPoster().getObjectId())){
                        return;
                    }
                    Intent intent = new Intent(mContext, PersonalIndexActivity.class);
                    intent.putExtra("user", discuss.getPoster());
                    mContext.startActivity(intent);
                }
            });
            childViewHolder.replier_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AVUser.getCurrentUser().getObjectId().equals(discuss.getReplier().getObjectId())){
                        return;
                    }
                    Intent intent = new Intent(mContext, PersonalIndexActivity.class);
                    intent.putExtra("user", discuss.getReplier());
                    mContext.startActivity(intent);
                }
            });

            return convertView;
        }

        private class ChildViewHolder{
            TextView poster_name;
            TextView replier_name;
            TextView reply_content;
        }
    }
}

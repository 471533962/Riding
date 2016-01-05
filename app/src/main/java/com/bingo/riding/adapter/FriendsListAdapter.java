package com.bingo.riding.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bingo.riding.R;
import com.bingo.riding.dao.User;
import com.bingo.riding.event.FriendItemClickEvent;
import com.bingo.riding.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by bingo on 16/1/4.
 */
public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendViewHolder> {

    /**
     * 在有序 friendItemList 第一次出现时的字母与位置的 map
     */
    private Map<Character, Integer> indexMap = new HashMap<Character, Integer>();

    private Context mContext;
    private List<FriendItem> friendItemList = new ArrayList<>();

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView alpha;
        TextView nameView;
        ImageView avatarView;
        RelativeLayout user_content;
        FriendItem friendItem;

        public FriendViewHolder(View itemView) {
            super(itemView);

            alpha = (TextView)itemView.findViewById(R.id.alpha);
            nameView = (TextView)itemView.findViewById(R.id.tv_friend_name);
            avatarView = (ImageView)itemView.findViewById(R.id.img_friend_avatar);
            user_content = (RelativeLayout)itemView.findViewById(R.id.user_content);

            user_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new FriendItemClickEvent(friendItem.user.getObjectId()));
                }
            });
        }

        public void bindData(FriendItem friendItem) {
            this.friendItem = friendItem;

            alpha.setVisibility(friendItem.initialVisible ? View.VISIBLE : View.GONE);
            alpha.setText(String.valueOf(Utils.chineneToSpell(friendItem.user.getNikeName()).charAt(0)));
            if (friendItem.user.getUserPhoto() != null){
                Glide.with(mContext.getApplicationContext())
                        .load(friendItem.user.getUserPhoto())
                        .signature(new StringSignature(friendItem.user.getUserPhoto()))
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .placeholder(R.drawable.a0c)
                        .error(R.drawable.default_error)
                        .into(avatarView);
            }
            nameView.setText(friendItem.user.getNikeName());
        }
    }

    public class FriendItem{
        public boolean initialVisible;
        public User user;

        public FriendItem(boolean initialVisible, User user) {
            this.initialVisible = initialVisible;
            this.user = user;
        }
    }

    public FriendsListAdapter(Context mContext, List<User> friendsList) {
        this.mContext = mContext;

        String lastHeader = "";
        for (int i = 0; i < friendsList.size(); i++) {
            FriendItem friendItem = new FriendItem(false, friendsList.get(i));
            String header = friendsList.get(i).getNikeName().substring(0, 1);
            if (!TextUtils.equals(lastHeader, header)) {
                // Insert new header view and update section data.
                lastHeader = header;

                friendItem.initialVisible = true;

                indexMap.put(header.charAt(0), i);
            }

            friendItemList.add(friendItem);
        }
    }

    @Override
    public int getItemCount() {
        return friendItemList.size();
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_user_item, parent, false);

        FriendViewHolder friendViewHolder = new FriendViewHolder(view);

        return friendViewHolder;
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        holder.bindData(friendItemList.get(position));
    }

    public Map<Character, Integer> getIndexMap() {
        return indexMap;
    }

    public void updateIndexMap(List<User> friendsList){
        friendItemList.clear();

        String lastHeader = "";
        for (int i = 0; i < friendsList.size(); i++) {
            FriendItem friendItem = new FriendItem(false, friendsList.get(i));
            String header = friendsList.get(i).getNikeName().substring(0, 1);
            if (!TextUtils.equals(lastHeader, header)) {
                // Insert new header view and update section data.
                lastHeader = header;

                friendItem.initialVisible = true;

                indexMap.put(header.charAt(0), i);
            }

            friendItemList.add(friendItem);
        }
    }
}

package com.bingo.riding.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.bingo.riding.ImageGalleryActivity;
import com.bingo.riding.R;
import com.bingo.riding.bean.Message;
import com.bingo.riding.interfaces.SquareItemClickListener;
import com.bingo.riding.interfaces.SquareItemLongClickListener;
import com.bingo.riding.ui.SquarePhotosGridView;
import com.bingo.riding.utils.DataTools;
import com.bingo.riding.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by bingo on 15/10/9.
 */
public class SquareListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static int TYPE_FOOT = 0;
    private static int TYPE_CONTENT = 1;

    private List<Message> messageList;
    private Context mContext;
    private SquareItemClickListener squareItemClickListener;
    private SquareItemLongClickListener squareItemLongClickListener;

    public SquareListAdapter(Context mContext,
                             List<Message> messageList,
                             @Nullable SquareItemClickListener squareItemClickListener,
                             @Nullable SquareItemLongClickListener squareItemLongClickListener) {
        this.mContext = mContext;
        this.messageList = messageList;
        this.squareItemClickListener = squareItemClickListener;
        this.squareItemLongClickListener = squareItemLongClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView message_publisher_name;
        TextView message_content;
        TextView message_publish_time;
        SquarePhotosGridView message_photos;
        ImageView message_publisher_photo;

        SquareItemClickListener squareItemClickListener;
        SquareItemLongClickListener squareItemLongClickListener;

        public ViewHolder(View itemView,
                          @Nullable SquareItemClickListener squareItemClickListener,
                          @Nullable SquareItemLongClickListener squareItemLongClickListener) {
            super(itemView);

            message_publisher_name = (TextView) itemView.findViewById(R.id.message_publisher_name);
            message_content = (TextView) itemView.findViewById(R.id.message_content);
            message_publish_time = (TextView) itemView.findViewById(R.id.publish_time);
            message_photos = (SquarePhotosGridView) itemView.findViewById(R.id.community_image_grid_view);
            message_publisher_photo = (ImageView) itemView.findViewById(R.id.message_publisher_photo);

            this.squareItemClickListener = squareItemClickListener;
            this.squareItemLongClickListener = squareItemLongClickListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (squareItemClickListener != null){
                squareItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (squareItemLongClickListener != null){
                squareItemLongClickListener.onLongClick(v, getAdapterPosition());
            }
            return true;
        }
    }

    public static class FootViewHolder extends RecyclerView.ViewHolder{

        Button loadMoreBtn;
        ContentLoadingProgressBar contentLoadingProgressBar;

        public FootViewHolder(View itemView) {
            super(itemView);

            loadMoreBtn = (Button) itemView.findViewById(R.id.loadMoreBtn);
            contentLoadingProgressBar = (ContentLoadingProgressBar) itemView.findViewById(R.id.loadingView);

            loadMoreBtn.setVisibility(View.VISIBLE);
            contentLoadingProgressBar.setVisibility(View.GONE);

            loadMoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadMoreBtn.setVisibility(View.INVISIBLE);
                    contentLoadingProgressBar.setVisibility(View.VISIBLE);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (messageList.size() <= 15){
            return messageList.size();
        }else{
            return messageList.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if ( (messageList.size() != getItemCount()) && (position == (getItemCount() - 1) )){
            return TYPE_FOOT;
        }
        return TYPE_CONTENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == TYPE_CONTENT) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_square_message_item, parent, false);
            viewHolder = new ViewHolder(itemView, squareItemClickListener, squareItemLongClickListener);
        }else if (viewType == TYPE_FOOT){
            View footView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bottom_view, parent, false);
            viewHolder = new FootViewHolder(footView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder){
            ViewHolder viewHolder = (ViewHolder) holder;
            final Message message = messageList.get(position);
            viewHolder.message_publisher_name.setText(message.getPoster().getString("nikeName"));
            viewHolder.message_content.setText(message.getContent());
            viewHolder.message_publish_time.setText(DataTools.timeLogic(message.getMessageObject().getCreatedAt()));

            AVFile userPhoto = message.getPoster().getAVFile("userPhoto");
            if (userPhoto != null){
                Glide.with(mContext.getApplicationContext())
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

            if (message.getPhotoList().size() > 0){
                viewHolder.message_photos.setVisibility(View.VISIBLE);
                viewHolder.message_photos.setAdapter(new GridPhotosAdapter(mContext, message.getPhotoList()));
                viewHolder.message_photos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(mContext, ImageGalleryActivity.class);
                        intent.putStringArrayListExtra("photos", (ArrayList<String>) message.getPhotoList());
                        intent.putExtra("photoManager", false);
                        intent.putExtra("startPosition", position);
                        intent.putExtra("isFile", false);
                        mContext.startActivity(intent);
                    }
                });
            }else{
                viewHolder.message_photos.setVisibility(View.GONE);
            }
        }else if (holder instanceof FootViewHolder){
            FootViewHolder footViewHolder = (FootViewHolder) holder;
        }
    }

}



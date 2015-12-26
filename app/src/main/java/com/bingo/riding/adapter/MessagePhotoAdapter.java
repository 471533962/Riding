package com.bingo.riding.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bingo.riding.PublishActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by bingo on 15/10/12.
 */
public class MessagePhotoAdapter extends BaseAdapter {
    private ArrayList<String> messagePhotoList = new ArrayList<>();
    private Context mContext;

    public MessagePhotoAdapter(Context mContext, ArrayList<String> messagePhotoList) {
        this.mContext = mContext;
        this.messagePhotoList = messagePhotoList;
    }

    @Override
    public int getCount() {
        if (messagePhotoList.size() < 9){
            return messagePhotoList.size();
        }else{
            return 9;
        }
    }

    @Override
    public Object getItem(int position) {
        return messagePhotoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null){
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER);
        }else{
            imageView = (ImageView) convertView;
        }
        String imagePath = messagePhotoList.get(position);
        if (imagePath.equals(PublishActivity.ADDNEWIMAGE) == false){
            Glide.with(mContext.getApplicationContext())
                    .load(new File(imagePath))
                    .signature(new StringSignature(imagePath))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .centerCrop()
                    .into(imageView);
        }else{
            Glide.with(mContext.getApplicationContext())
                    .load(Integer.parseInt(imagePath))
                    .signature(new StringSignature(imagePath))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .centerCrop()
                    .into(imageView);
        }

        return imageView;
    }
}

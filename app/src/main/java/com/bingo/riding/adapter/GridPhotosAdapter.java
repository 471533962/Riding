package com.bingo.riding.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bingo.riding.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.util.List;

/**
 * Created by bingo on 12/5/15.
 */
public class GridPhotosAdapter extends BaseAdapter {
    private List<String> photoList;
    private Context mContext;

    public GridPhotosAdapter(Context mContext, List<String> photoList) {
        this.mContext = mContext;
        this.photoList = photoList;
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public Object getItem(int position) {
        return photoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.photo_item, parent, false);
            imageView = (ImageView) convertView.findViewById(R.id.photo);

            convertView.setTag(imageView);
        }else{
            imageView = (ImageView) convertView.getTag();
        }

        Glide.with(mContext.getApplicationContext())
                .load(photoList.get(position))
                .signature(new StringSignature(photoList.get(position)))
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.loaderror)
                .override(250, 250)
                .centerCrop()
                .into(imageView);

        return convertView;
    }
}

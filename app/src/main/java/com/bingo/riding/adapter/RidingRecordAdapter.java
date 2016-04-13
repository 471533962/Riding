package com.bingo.riding.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.bingo.riding.R;
import com.bingo.riding.bean.RidingRecordBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: CodingBingo.
 * Email: codingbingo@gmail.com
 * Blog: codingbingo.github.io
 * Github: https://github.com/CodingBingo
 * Created at 16/4/13
 */
public class RidingRecordAdapter extends RecyclerView.Adapter<RidingRecordAdapter.ViewHolder> {

    private List<RidingRecordBean> mRidingRecordBeanList = new ArrayList<>();
    private Context mContext;
    DecimalFormat fnum = new DecimalFormat("##0.00");

    public RidingRecordAdapter(Context context, List<RidingRecordBean> ridingRecordBeanList) {
        mContext = context;
        mRidingRecordBeanList = ridingRecordBeanList;
    }

    @Override
    public int getItemCount() {
        return mRidingRecordBeanList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.riding_record_list_item, null);
        viewHolder = new ViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof ViewHolder){
            RidingRecordBean ridingRecordBean = mRidingRecordBeanList.get(position);

            String averageText = "平均速度: " + "<font color=\"#aea744\">" + fnum.format(ridingRecordBean.getAverageSpeed()) + "</front>" + "公里/小时";
            holder.riding_average_speed.setText(Html.fromHtml(averageText));
            String distanceText = "距离: " + "<font color=\"#aea744\">" + fnum.format(ridingRecordBean.getRidingDistance() / 1000) + "</front>" + "公里";
            holder.riding_distance.setText(Html.fromHtml(distanceText));

            long time = ridingRecordBean.getRidingTime();
            int hour = (int) ((time / 60) / 60) % 60;
            int minute = (int) (time / 60) % 60;
            int second = (int) (time % 60);

            String out_time = hour < 10 ? "0" + hour : "" + hour;
            out_time += ":";
            out_time += minute < 10 ? "0" + minute : "" + minute;
            out_time += ":";
            out_time += second < 10 ? "0" + second : "" + second;

            holder.riding_time.setText(Html.fromHtml("时间: " + "<font color=\"#aea744\">" + out_time + "</front>"));

            AVFile screenShotAVFile = ridingRecordBean.getScreenShot();
            if (screenShotAVFile != null){
                Glide.with(mContext.getApplicationContext())
                        .load(screenShotAVFile.getUrl())
                        .signature(new StringSignature(screenShotAVFile.getUrl()))
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.default_error)
                        .override(250, 250)
                        .centerCrop()
                        .into(holder.riding_screenshot);
            } else {
                Glide.with(mContext.getApplicationContext())
                        .load(R.drawable.placeholder)
                        .override(250, 250)
                        .centerCrop()
                        .into(holder.riding_screenshot);
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView riding_distance;
        TextView riding_time;
        TextView riding_average_speed;
        ImageView riding_screenshot;

        public ViewHolder(View itemView) {
            super(itemView);

            riding_distance = (TextView) itemView.findViewById(R.id.riding_distance);
            riding_time = (TextView) itemView.findViewById(R.id.riding_time);
            riding_average_speed = (TextView) itemView.findViewById(R.id.riding_average_speed);
            riding_screenshot = (ImageView) itemView.findViewById(R.id.riding_screenshot);
        }
    }
}

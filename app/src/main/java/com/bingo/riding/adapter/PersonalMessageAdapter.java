package com.bingo.riding.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bingo.riding.R;

public class PersonalMessageAdapter extends BaseAdapter{
	
	private Context mContext;
	private LayoutInflater inflate;
	private HolderView holder;

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return 0;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = inflate.inflate(R.layout.fragment_message_list_item, parent, false);

			holder = new HolderView();
			holder.personal_lettle_content = (TextView) convertView.findViewById(R.id.personal_lettle_content);
			holder.personal_lettle_num = (TextView) convertView.findViewById(R.id.personal_lettle_num);
			holder.personal_lettle_time = (TextView) convertView.findViewById(R.id.personal_lettle_time);
			holder.personal_lettle_user = (TextView) convertView.findViewById(R.id.personal_lettle_user);
			holder.personal_lettle_user_pic = (ImageView) convertView.findViewById(R.id.personal_lettle_user_pic);

			convertView.setTag(holder);
		}else{
			holder = (HolderView) convertView.getTag();
		}


		return convertView;
	}

	class HolderView{
		TextView personal_lettle_num;
		TextView personal_lettle_user;
		TextView personal_lettle_content;
		TextView personal_lettle_time;
		ImageView personal_lettle_user_pic;
	}
	
}

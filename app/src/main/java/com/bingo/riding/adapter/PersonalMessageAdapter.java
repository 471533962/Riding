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
	public Object getItem(int arg0) {
		return 0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if(arg1 == null){
			arg1 = inflate.inflate(R.layout.fragment_message_list_item, null);
			
			holder = new HolderView();
			holder.personal_lettle_content = (TextView) arg1.findViewById(R.id.personal_lettle_content);
			holder.personal_lettle_num = (TextView) arg1.findViewById(R.id.personal_lettle_num);
			holder.personal_lettle_time = (TextView) arg1.findViewById(R.id.personal_lettle_time);
			holder.personal_lettle_user = (TextView) arg1.findViewById(R.id.personal_lettle_user);
			holder.personal_lettle_user_pic = (ImageView) arg1.findViewById(R.id.personal_lettle_user_pic);
			
			arg1.setTag(holder);
		}else{
			
			holder = (HolderView) arg1.getTag();
			
		}
		
		return arg1;
	}

	class HolderView{
		TextView personal_lettle_num;
		TextView personal_lettle_user;
		TextView personal_lettle_content;
		TextView personal_lettle_time;
		ImageView personal_lettle_user_pic;
	}
	
}

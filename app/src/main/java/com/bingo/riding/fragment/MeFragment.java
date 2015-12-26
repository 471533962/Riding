package com.bingo.riding.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.bingo.riding.MessageMangerActivity;
import com.bingo.riding.PersonalInfoActivity;
import com.bingo.riding.R;
import com.bingo.riding.SettingsActivity;
import com.bingo.riding.bean.Message;
import com.bingo.riding.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import me.nereo.multi_image_selector.bean.Image;

/**
 * Created by bingo on 15/10/9.
 */
public class MeFragment extends Fragment implements View.OnClickListener{

    private ImageView userPhoto;
    private TextView userName;
    private TextView userEmail;

    private RelativeLayout personalInfo;
    private RelativeLayout setting;
    private RelativeLayout ridingData;
    private RelativeLayout myMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        initView(view);
        return view;
    }

    private void initView(View view){
        userPhoto = (ImageView) view.findViewById(R.id.userPhoto);
        userEmail = (TextView) view.findViewById(R.id.userEmail);
        userName = (TextView) view.findViewById(R.id.userName);

        personalInfo = (RelativeLayout) view.findViewById(R.id.personal_info);
        setting = (RelativeLayout) view.findViewById(R.id.setting);
        ridingData = (RelativeLayout) view.findViewById(R.id.riding_data);
        myMessage = (RelativeLayout) view.findViewById(R.id.my_message);

        initViewListener();
        initData();
    }

    private void initViewListener(){
        personalInfo.setOnClickListener(this);
        setting.setOnClickListener(this);
        ridingData.setOnClickListener(this);
        myMessage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.personal_info:
                Utils.startActivity(getActivity(), PersonalInfoActivity.class);
                break;
            case R.id.setting:
                Utils.startActivity(getActivity(), SettingsActivity.class);
                break;
            case R.id.riding_data:
                break;
            case R.id.my_message:
                Utils.startActivity(getActivity(), MessageMangerActivity.class);
                break;
        }
    }

    private void initData(){
        AVFile userPhotoFile = AVUser.getCurrentUser().getAVFile("userPhoto");
        if (userPhotoFile != null) {
            Glide.with(getActivity().getApplicationContext())
                    .load(userPhotoFile.getUrl())
                    .signature(new StringSignature(userPhotoFile.getUrl()))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(userPhoto);
        }

        userEmail.setText(AVUser.getCurrentUser().getEmail());
        userName.setText(AVUser.getCurrentUser().getString("nikeName"));
    }
}

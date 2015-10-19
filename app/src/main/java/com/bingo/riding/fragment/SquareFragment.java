package com.bingo.riding.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bingo.riding.PublishActivity;
import com.bingo.riding.R;
import com.bingo.riding.utils.Utils;

/**
 * Created by bingo on 15/10/8.
 */
public class SquareFragment extends Fragment {

    private RecyclerView fragment_square_recyclerview;
    private FloatingActionButton publish_new_message;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_square, container, false);

        initView(view);
        return view;
    }

    private void initView(View view){
        publish_new_message = (FloatingActionButton) view.findViewById(R.id.publish_new_message);
        fragment_square_recyclerview = (RecyclerView)view.findViewById(R.id.fragment_square_recyclerview);

        initListener();
    }

    private void initListener(){
        publish_new_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startActivity(getActivity(), PublishActivity.class);
            }
        });
    }
}

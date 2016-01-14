package com.bingo.riding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.bingo.riding.adapter.NewFriendsListAdapter;
import com.bingo.riding.bean.AddFriendsRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bingo on 16/1/7.
 */
public class NewFriendsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView new_friends_list;
    private LinearLayoutManager linearLayoutManager;

    private List<AddFriendsRequest> addFriendsRequestList;
    private NewFriendsListAdapter newFriendsListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_friends);

        init();
        initView();
    }

    private void init() {
        AVQuery<AVObject> query = new AVQuery("addFriendsRequest");
        query.setLimit(20);
        query.include("fromUser");
        query.orderByDescending("createdAt");
        query.whereEqualTo("toUser", AVUser.getCurrentUser());

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {

                } else {
                    Toast.makeText(NewFriendsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("新朋友");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new_friends_list = (RecyclerView) findViewById(R.id.new_friends_list);
        linearLayoutManager = new LinearLayoutManager(this);
        new_friends_list.setLayoutManager(linearLayoutManager);
        addFriendsRequestList = new ArrayList<>();
        newFriendsListAdapter = new NewFriendsListAdapter(addFriendsRequestList);
        new_friends_list.setAdapter(newFriendsListAdapter);
    }
}

package com.bingo.riding;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.bingo.riding.adapter.DiscussionListAdapter;
import com.bingo.riding.bean.Discussion;
import com.bingo.riding.utils.DataTools;

import java.util.ArrayList;
import java.util.List;

public class MessageMangerActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private RecyclerView discussList;
    private DiscussionListAdapter discussionListAdapter;
    private List<Discussion> discussionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_manger);

        init();
        initView();
    }

    private void init() {
        AVQuery<AVObject> query = new AVQuery<>("discuss");
        query.whereEqualTo("replier", AVUser.getCurrentUser());
        query.orderByDescending("createdAt");
        query.include("poster");
        query.include("poster.userPhoto");
        query.include("centerMessage");
        query.include("centerMessage.images");
        query.include("centerMessage.poster");
        query.include("centerMessage.poster.userPhoto");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){
                    discussionList.clear();
                    for (int i = 0; i < list.size(); i++) {
                        discussionList.add(DataTools.getDiscussionFromAVObject(list.get(i)));
                    }

                    discussionListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("评论消息");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        discussList = (RecyclerView) findViewById(R.id.discussList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        discussList.setLayoutManager(linearLayoutManager);
        discussionListAdapter = new DiscussionListAdapter(discussionList, this);
        discussList.setAdapter(discussionListAdapter);

    }
}

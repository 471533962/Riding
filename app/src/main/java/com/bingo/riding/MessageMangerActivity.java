package com.bingo.riding;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.bingo.riding.adapter.SquareListAdapter;
import com.bingo.riding.bean.Message;
import com.bingo.riding.utils.DataTools;

import java.util.List;

public class MessageMangerActivity extends AppCompatActivity {

    private RecyclerView mine_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_manger);

        init();
        initView();
    }

    private void init() {
        AVQuery<AVObject> query = new AVQuery("squareMessage");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.include("images");
        query.whereEqualTo("poster", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {

                }
            }
        });
    }

    private void initView() {
        mine_message = (RecyclerView) findViewById(R.id.mine_message);
    }
}

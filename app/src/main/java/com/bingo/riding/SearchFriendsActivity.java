package com.bingo.riding;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.bingo.riding.adapter.SearchFriendsListAdapter;
import com.bingo.riding.dao.User;
import com.bingo.riding.utils.DataTools;
import com.bingo.riding.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bingo on 16/1/8.
 */
public class SearchFriendsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView search_friends_list;

    private List<User> searchFriendsList = new ArrayList<>();
    private SearchFriendsListAdapter searchFriendsListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        initView();
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            LogUtils.e("query : " + query);
            AVQuery<AVObject> avObjectAVQuery = new AVQuery("_User");
            avObjectAVQuery.whereContains("nikeName", query);
            avObjectAVQuery.include("userPhoto");

            avObjectAVQuery.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null) {
                        for (AVObject avObject : list) {
                            User user = DataTools.getUserFromAVObject(avObject);
                            searchFriendsList.add(user);
                        }
                        searchFriendsListAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(SearchFriendsActivity.this, "查询用户失败，请稍后重试。", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("查找好友");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        search_friends_list = (RecyclerView) findViewById(R.id.search_friends_list);
        search_friends_list.setLayoutManager(new LinearLayoutManager(this));
        searchFriendsListAdapter = new SearchFriendsListAdapter(searchFriendsList);
        search_friends_list.setAdapter(searchFriendsListAdapter);
    }
}

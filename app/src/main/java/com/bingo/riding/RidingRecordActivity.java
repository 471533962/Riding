package com.bingo.riding;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.bingo.riding.adapter.RidingRecordAdapter;
import com.bingo.riding.bean.RidingRecordBean;
import com.bingo.riding.utils.DataTools;

import java.util.ArrayList;
import java.util.List;

public class RidingRecordActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RidingRecordAdapter mRidingRecordAdapter;
    private List<RidingRecordBean> mRidingRecordBeanList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riding_record);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("骑行数据");
        }

        initView();
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.riding_list);
        mRidingRecordAdapter = new RidingRecordAdapter(this, mRidingRecordBeanList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRidingRecordAdapter);

    }

    private void init(){
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>("ridingRoute");
        avQuery.include("screenShot");
        avQuery.whereEqualTo("belongUser", AVUser.getCurrentUser());
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){
                    for (int i = 0; i < list.size(); i++) {
                        mRidingRecordBeanList.add(DataTools.getRidingRecordBeanFromAVObject(list.get(i)));
                    }

                    mRidingRecordAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(RidingRecordActivity.this, "获取数据失败，请稍后重试。", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

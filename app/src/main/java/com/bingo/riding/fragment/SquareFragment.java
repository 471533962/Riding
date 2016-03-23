package com.bingo.riding.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.bingo.riding.MessageDetailActivity;
import com.bingo.riding.PublishActivity;
import com.bingo.riding.R;
import com.bingo.riding.adapter.SquareListAdapter;
import com.bingo.riding.bean.Message;
import com.bingo.riding.event.PublishMessageEvent;
import com.bingo.riding.event.SquareMessageLoadMoreEvent;
import com.bingo.riding.interfaces.SquareItemClickListener;
import com.bingo.riding.utils.DataTools;
import com.bingo.riding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by bingo on 15/10/8.
 */
public class SquareFragment extends Fragment implements SquareItemClickListener{

    private List<Message> messageList = new ArrayList<>();

    private RecyclerView fragment_square_recyclerview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SquareListAdapter squareListAdapter;

    private FloatingActionButton publish_new_message;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void loadData(){
        AVQuery<AVObject> query = new AVQuery("squareMessage");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.include("images");
        query.include("poster");
        query.include("poster.userPhoto");
        query.limit(20);
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){
                    for (AVObject avObject : list){
                        Message message = DataTools.getMessageFromAVObject(avObject);
                        messageList.add(message);
                    }
                    if (squareListAdapter != null){
                        squareListAdapter.notifyDataSetChanged();
                    }else{
                        squareListAdapter = new SquareListAdapter(getActivity(), messageList, SquareFragment.this);
                    }
                }else{
                    e.printStackTrace();
                }
            }
        });

    }

    public void onEventMainThread(PublishMessageEvent publishMessageEvent){
        if (publishMessageEvent.isSuccess() == true){
            messageList.add(0, publishMessageEvent.getMessage());
            squareListAdapter.notifyDataSetChanged();

            Toast.makeText(getActivity(), "发表成功", Toast.LENGTH_SHORT).show();
            Log.e("bingo", "成功了啦");

        }else{
            Toast.makeText(getActivity(), "发表失败", Toast.LENGTH_SHORT).show();
            Log.e("bingo", "发表失败");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_square, container, false);

        initView(view);
        return view;
    }

    private void initView(View view){
        publish_new_message = (FloatingActionButton) view.findViewById(R.id.publish_new_message);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.squareRefreshLayout);
        fragment_square_recyclerview = (RecyclerView)view.findViewById(R.id.fragment_square_recyclerview);

        initData();
        initListener();
    }

    private void initData(){
        if (squareListAdapter == null){
            squareListAdapter = new SquareListAdapter(getActivity(), messageList, SquareFragment.this);
        }
        fragment_square_recyclerview.setAdapter(squareListAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        fragment_square_recyclerview.setLayoutManager(layoutManager);

    }

    private void initListener(){
        publish_new_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startActivity(getActivity(), PublishActivity.class);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AVQuery<AVObject> query = new AVQuery("squareMessage");
                query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
                query.include("images");
                query.include("poster");
                query.include("poster.userPhoto");
                query.limit(20);
                query.orderByDescending("updatedAt");
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null){
                            messageList.clear();
                            for (AVObject avObject : list){
                                Message message = DataTools.getMessageFromAVObject(avObject);
                                messageList.add(message);
                            }
                            if (squareListAdapter != null){
                                squareListAdapter.notifyDataSetChanged();
                            }
                        }else{
                            e.printStackTrace();
                        }

                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), MessageDetailActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("message", messageList.get(position).getMessageObject().toString());

        intent.putExtra("bundle", bundle);

        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_left_in,
                R.anim.push_left_out);
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    public void onEvent(SquareMessageLoadMoreEvent squareMessageLoadMoreEvent){
        AVQuery<AVObject> query = new AVQuery("squareMessage");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.include("images");
        query.include("poster");
        query.include("poster.userPhoto");
        query.limit(20);
        query.orderByDescending("updatedAt");
        query.whereLessThan("createdAt", messageList.get(messageList.size() - 1).getMessageObject().getCreatedAt());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){
                    for (AVObject avObject : list){
                        Message message = DataTools.getMessageFromAVObject(avObject);
                        messageList.add(message);
                    }
                    if (squareListAdapter != null){
                        squareListAdapter.notifyDataSetChanged();
                    }
                }else{
                    e.printStackTrace();
                }

                squareListAdapter.setNeedToReset(true);
            }
        });

    }
}

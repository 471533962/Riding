package com.bingo.riding.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.bingo.riding.MessageDetailActivity;
import com.bingo.riding.PublishActivity;
import com.bingo.riding.R;
import com.bingo.riding.adapter.SquareListAdapter;
import com.bingo.riding.bean.Message;
import com.bingo.riding.event.PublishMessageEvent;
import com.bingo.riding.interfaces.SquareItemClickListener;
import com.bingo.riding.interfaces.SquareItemLongClickListener;
import com.bingo.riding.utils.DataTools;
import com.bingo.riding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by bingo on 15/10/8.
 */
public class SquareFragment extends Fragment implements SquareItemClickListener, SquareItemLongClickListener{

    private List<Message> messageList = new ArrayList<>();

    private RecyclerView fragment_square_recyclerview;
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

        query.doCloudQueryInBackground("select include images,include poster,include poster.userPhoto, * from squareMessage order by updatedAt desc", new CloudQueryCallback<AVCloudQueryResult>() {
            @Override
            public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                if (e == null){
                    List<AVObject> resultList = (List<AVObject>) avCloudQueryResult.getResults();
                    for (AVObject avObject : resultList){
                        Message message = DataTools.getMessageFromAVObject(avObject);
                        messageList.add(message);
                    }
                    if (squareListAdapter != null){
                        squareListAdapter.notifyDataSetChanged();
                    }else{
                        squareListAdapter = new SquareListAdapter(getActivity(), messageList, SquareFragment.this, SquareFragment.this);
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
        fragment_square_recyclerview = (RecyclerView)view.findViewById(R.id.fragment_square_recyclerview);

        initData();
        initListener();
    }

    private void initData(){
        if (squareListAdapter == null){
            squareListAdapter = new SquareListAdapter(getActivity(), messageList, SquareFragment.this, SquareFragment.this);
        }
        fragment_square_recyclerview.setAdapter(squareListAdapter);
        fragment_square_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

    }

    private void initListener(){
        publish_new_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startActivity(getActivity(), PublishActivity.class);
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
}

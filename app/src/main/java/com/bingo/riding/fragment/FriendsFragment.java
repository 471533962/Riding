package com.bingo.riding.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.bingo.riding.ChatActivity;
import com.bingo.riding.R;
import com.bingo.riding.adapter.FriendsListAdapter;
import com.bingo.riding.dao.User;
import com.bingo.riding.event.FriendItemClickEvent;
import com.bingo.riding.interfaces.OnLetterViewClickListener;
import com.bingo.riding.ui.LetterView;
import com.bingo.riding.utils.Constants;
import com.bingo.riding.utils.DaoUtils;
import com.bingo.riding.utils.DataTools;
import com.bingo.riding.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.event.EventBus;

public class FriendsFragment extends Fragment implements OnLetterViewClickListener{
    private View view;
    private RecyclerView friendsList;
    private LetterView letterView;
    private LinearLayout layout_new;

    private FriendsListAdapter friendsListAdapter;
    private FriendsListAdapter.FriendViewHolder viewHolder;
    private LinearLayoutManager linearLayoutManager;

    private List<User> userList = new ArrayList<>();

    private DaoUtils daoUtils;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        daoUtils = DaoUtils.getInstance(getActivity().getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);

        view = inflater.inflate(R.layout.fragment_friends, container, false);

        initData();
        initView();
        initViewListener();
        return view;
    }

    private void initData(){
        AVRelation<AVUser> friendsRelation = AVUser.getCurrentUser().getRelation("friends");
        AVQuery avQuery = friendsRelation.getQuery();
        avQuery.include("userPhoto");
        friendsRelation.getQuery().findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (e == null){
                    for (AVUser avUser: list) {
                        User user = DataTools.getUserFromAVUser(avUser);
                        userList.add(user);
                        daoUtils.insertUser(user);
                    }
                    Collections.sort(userList, new FriendsComparator());
                    friendsListAdapter.updateIndexMap(userList);
                    friendsListAdapter.notifyDataSetChanged();
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView(){
        letterView = (LetterView) view.findViewById(R.id.friends_letter_view);
        friendsList = (RecyclerView) view.findViewById(R.id.friends_list);
        layout_new = (LinearLayout) view.findViewById(R.id.layout_new);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        friendsList.setLayoutManager(linearLayoutManager);
        Collections.sort(userList, new FriendsComparator());
        friendsListAdapter = new FriendsListAdapter(getActivity(), userList);
        friendsList.setAdapter(friendsListAdapter);
    }


    public void initViewListener(){
        letterView.setOnLetterViewClickListener(this);
        layout_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void OnLetterClick(char letter) {
        Character targetChar = Character.toLowerCase(letter);
        if (friendsListAdapter.getIndexMap().containsKey(targetChar)) {
            int index = friendsListAdapter.getIndexMap().get(targetChar);
            if (index >= 0 && index < friendsListAdapter.getItemCount()) {
                linearLayoutManager.scrollToPositionWithOffset(index, 0);
            }
        }
    }

    class FriendsComparator implements Comparator<User> {
        @Override
        public int compare(User lhs, User rhs) {
            return Utils.chineneToSpell(lhs.getNikeName()).compareTo(Utils.chineneToSpell(rhs.getNikeName()));
        }
    }

    public void onEvent(FriendItemClickEvent friendItemClickEvent){
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.MEMBER_ID, friendItemClickEvent.memberId);
        startActivity(intent);
    }
}

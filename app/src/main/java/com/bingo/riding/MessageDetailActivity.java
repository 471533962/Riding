package com.bingo.riding;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.bingo.riding.adapter.GridPhotosAdapter;
import com.bingo.riding.adapter.MessageDiscussAdapter;
import com.bingo.riding.bean.Discussion;
import com.bingo.riding.bean.Message;
import com.bingo.riding.interfaces.OnDiscussionContentClickListener;
import com.bingo.riding.ui.SquarePhotosGridView;
import com.bingo.riding.utils.DataTools;
import com.bingo.riding.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.linearlistview.LinearListView;
import com.lsjwzh.loadingeverywhere.LoadingLayout;

import java.util.ArrayList;
import java.util.List;

public class MessageDetailActivity extends AppCompatActivity implements View.OnClickListener, OnDiscussionContentClickListener{

    private Toolbar toolbar;
    private Message message;

    private ImageView message_photo;
    private TextView publish_time;
    private TextView message_publisher_name;
    private TextView message_content;
    private SquarePhotosGridView community_image_grid_view;
    private Button sendBtn;
    private EditText commentEditText;

    private LinearListView discussList;
    private MessageDiscussAdapter messageDiscussAdapter;
    private LoadingLayout loadingLayout;

    private Discussion pretendReply = null;
    private List<Discussion> discussionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        try {
            Intent intent = getIntent();
            if (intent != null){
                Bundle bundle = intent.getBundleExtra("bundle");
                message = DataTools.getMessageFromJSONString(bundle.getString("message"));
            }

            //Transparent Status Bar
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                getWindow().setStatusBarColor(Color.parseColor("#3F51B5"));
            }

            initView();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("状态");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        message_photo = (ImageView) findViewById(R.id.message_photo);
        publish_time = (TextView) findViewById(R.id.publish_time);
        message_publisher_name = (TextView) findViewById(R.id.message_publisher_name);
        message_content = (TextView) findViewById(R.id.message_content);
        community_image_grid_view = (SquarePhotosGridView) findViewById(R.id.community_image_grid_view);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);
        commentEditText = (EditText) findViewById(R.id.commentEditText);
        discussList = (LinearListView) findViewById(R.id.discussList);
        loadingLayout = LoadingLayout.wrap(discussList);

        initData();
    }

    private void initData(){
        AVFile userPhoto = message.getPoster().getAVFile("userPhoto");
        if (userPhoto != null){
            Glide.with(getApplicationContext())
                    .load(userPhoto.getUrl())
                    .signature(new StringSignature(userPhoto.getUrl()))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.a0c)
                    .error(R.drawable.default_error)
                    .centerCrop()
                    .into(message_photo);
        } else {
            message_photo.setImageResource(R.drawable.default_photo);
        }

        message_content.setText(message.getContent());
        message_publisher_name.setText(message.getPoster().getString("nikeName"));
        publish_time.setText(DataTools.timeLogic(message.getMessageObject().getCreatedAt()));

        if (message.getPhotoList().size() > 0){
            community_image_grid_view.setVisibility(View.VISIBLE);
            community_image_grid_view.setAdapter(new GridPhotosAdapter(this, message.getPhotoList()));
            community_image_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MessageDetailActivity.this, ImageGalleryActivity.class);
                    intent.putStringArrayListExtra("photos", (ArrayList<String>) message.getPhotoList());
                    intent.putExtra("photoManager", false);
                    intent.putExtra("startPosition", position);
                    intent.putExtra("isFile", false);
                    MessageDetailActivity.this.startActivity(intent);
                }
            });
        }else{
            community_image_grid_view.setVisibility(View.GONE);
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    List<AVObject> discussionObject = message.getMessageObject()
                            .getRelation("discussion")
                            .getQuery()
                            .include("poster")
                            .include("replier")
                            .find();
                    for (AVObject avObject : discussionObject){
                        Discussion discussion = DataTools.getDiscussionFromAVObject(avObject);


                        List<AVObject> childrenDiscussionList = discussion.getDiscussionObject()
                                .getRelation("childrenDiscussion")
                                .getQuery()
                                .include("poster")
                                .include("replier")
                                .find();

                        List<Discussion> childrenDiscussions= new ArrayList<>();
                        for(AVObject children : childrenDiscussionList){
                            Discussion childrenDiscussion = DataTools.getDiscussionFromAVObject(children);

                            childrenDiscussions.add(childrenDiscussion);
                        }

                        discussion.setChildren(childrenDiscussions);
                        discussionList.add(discussion);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageDiscussAdapter = new MessageDiscussAdapter(MessageDetailActivity.this, discussionList, MessageDetailActivity.this);

                            discussList.setAdapter(messageDiscussAdapter);
                        }
                    });

                }catch (AVException ave){
                    ave.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingLayout.hideLoading();
                    }
                });
            }
        });
        thread.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendBtn:
                final String commentContent = commentEditText.getText().toString();
                if (commentContent.length() == 0){
                    Toast.makeText(MessageDetailActivity.this, "请输入评论内容", Toast.LENGTH_LONG).show();
                    return;
                }

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            final AVObject discuss = new AVObject("discuss");
                            discuss.put("poster", AVUser.getCurrentUser());
                            if (pretendReply == null){
                                discuss.put("replier", message.getPoster());
                            }else{
                                discuss.put("replier", pretendReply.getPoster());
                            }
                            discuss.put("content", commentContent);
                            discuss.put("centerMessage", message.getMessageObject());
                            discuss.setFetchWhenSave(true);
                            discuss.save();

                            if (pretendReply == null){
                                message.getMessageObject()
                                        .getRelation("discussion")
                                        .add(discuss);

                                message.getMessageObject()
                                        .setFetchWhenSave(true);

                                message.getMessageObject().save();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MessageDetailActivity.this, "Add discuss Success", Toast.LENGTH_SHORT).show();

                                        Discussion dis = DataTools.getDiscussionFromAVObject(discuss);
                                        dis.setPoster(AVUser.getCurrentUser());
                                        dis.setReplier(message.getPoster());

                                        discussionList.add(dis);
                                        messageDiscussAdapter.notifyDataSetChanged();

                                        commentEditText.setText("");
                                        Utils.closeSoftInput(MessageDetailActivity.this, commentEditText);
                                    }
                                });
                            }else{
                                final int location = discussionList.indexOf(pretendReply);

                                pretendReply.getDiscussionObject()
                                        .getRelation("childrenDiscussion")
                                        .add(discuss);
                                pretendReply.getDiscussionObject()
                                        .setFetchWhenSave(true);
                                pretendReply.getDiscussionObject().save();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MessageDetailActivity.this, "Add discuss success", Toast.LENGTH_SHORT).show();

                                        Discussion dis = DataTools.getDiscussionFromAVObject(discuss);
                                        dis.setPoster(AVUser.getCurrentUser());
                                        dis.setReplier(pretendReply.getPoster());

                                        discussionList.get(location).getChildren().add(dis);
                                        messageDiscussAdapter.notifyDataSetChanged();

                                        commentEditText.setText("");
                                        Utils.closeSoftInput(MessageDetailActivity.this, commentEditText);

                                    }
                                });
                            }
                        }catch (AVException e){
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MessageDetailActivity.this, "Add discuss failed", Toast.LENGTH_SHORT).show();

                                    commentEditText.setText("");
                                    Utils.closeSoftInput(MessageDetailActivity.this, commentEditText);
                                }
                            });
                        }
                    }
                });
                thread.start();
                break;
        }
    }

    @Override
    public void discussionClickListener(Discussion discussion) {
        commentEditText.setHint("回复" + discussion.getPoster().getString("nikeName"));
        pretendReply = discussion;
    }
}

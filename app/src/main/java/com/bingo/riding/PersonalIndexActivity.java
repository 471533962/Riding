package com.bingo.riding;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.bingo.riding.utils.AVImClientManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.mingle.widget.ShapeLoadingDialog;
import com.mingle.widget.ShapeLoadingView;

import java.lang.reflect.Array;
import java.util.Arrays;

public class PersonalIndexActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView user_photo;
    private TextView user_name;
    private TextView user_sex;
    private TextView user_message;
    private Button add_friends;

    private AVUser avUser;
    private ShapeLoadingDialog shapeLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_index);

        Intent intent = getIntent();
        avUser = intent.getParcelableExtra("user");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("详细资料");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shapeLoadingDialog = new ShapeLoadingDialog(this);
        shapeLoadingDialog.setCanceledOnTouchOutside(true);

        initView();
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

    private void initView() {
        user_photo = (ImageView) findViewById(R.id.user_photo);
        user_name = (TextView) findViewById(R.id.user_name);
        user_sex = (TextView) findViewById(R.id.user_sex);
        user_message = (TextView) findViewById(R.id.user_message);
        add_friends = (Button) findViewById(R.id.add_friends);


        initData();
        initViewListener();
    }

    private void initData(){
        user_name.setText(avUser.getString("nikeName"));
        user_message.setText(avUser.getString("message"));
        user_sex.setText(avUser.getBoolean("isMale") == true ? "男" : "女");

        AVFile avFile = avUser.getAVFile("userPhoto");
        if (avFile != null){
            Glide.with(getApplicationContext())
                    .load(avFile.getUrl())
                    .signature(new StringSignature(avUser.getAVFile("userPhoto").getUrl()))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.a0c)
                    .error(R.drawable.default_error)
                    .centerCrop()
                    .into(user_photo);
        }else {
            user_photo.setImageResource(R.drawable.default_photo);
        }

    }

    private void initViewListener() {
        add_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shapeLoadingDialog.setLoadingText("正在发送好友请求，请稍候......");
                shapeLoadingDialog.show();

                AVObject avObject = new AVObject("addFriendsRequest");
                avObject.put("fromUser", AVUser.getCurrentUser());
                avObject.put("toUser", avUser);
                avObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null){
                            Toast.makeText(getApplicationContext(), "添加好友请求已发送", Toast.LENGTH_SHORT).show();
                        }else{
                            if (e.getCode() == 137){
                                Toast.makeText(getApplicationContext(), "你已经发送过好友请求", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                            e.printStackTrace();
                        }
                        shapeLoadingDialog.dismiss();
                    }
                });
            }
        });
    }
}

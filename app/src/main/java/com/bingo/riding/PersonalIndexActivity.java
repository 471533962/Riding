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

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.bingo.riding.utils.AVImClientManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.lang.reflect.Array;
import java.util.Arrays;

public class PersonalIndexActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView user_photo;
    private TextView user_name;
    private TextView user_sex;
    private TextView user_message;
    private Button have_chat;

    private AVUser avUser;

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
        have_chat = (Button) findViewById(R.id.have_chat);


        initData();
        initViewListener();
    }

    private void initData(){
        user_name.setText(avUser.getString("nikeName"));
        user_message.setText(avUser.getString("message"));
        user_sex.setText(avUser.getBoolean("isMale") == true ? "男" : "女");

        Glide.with(getApplicationContext())
                .load(avUser.getAVFile("userPhoto").getUrl())
                .signature(new StringSignature(avUser.getAVFile("userPhoto").getUrl()))
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.a0c)
                .error(R.drawable.default_error)
                .centerCrop()
                .into(user_photo);
    }

    private void initViewListener() {
        have_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalIndexActivity.this, ChatActivity.class);
                intent.putExtra("user", avUser);
                startActivity(intent);
            }
        });
    }
}

package com.bingo.riding;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

public class PersonalInfoActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int REQUEST_NIKENAME = 0;
    public static final int REQUEST_EMAIL = 1;
    public static final int REQUEST_MESSAGE = 2;

    private RelativeLayout personalImage;
    private RelativeLayout personalNikeName;
    private RelativeLayout personalEmail;
    private RelativeLayout personalSex;
    private RelativeLayout personalMessage;

    private ImageView personalImage_imageView;
    private TextView personalNikeName_textView;
    private TextView personalEmail_textView;
    private TextView personalSex_textView;
    private TextView personalMessage_textView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        initView();
    }

    private void initView(){

        personalEmail = (RelativeLayout) findViewById(R.id.personalEmail);
        personalImage = (RelativeLayout) findViewById(R.id.personalImage);
        personalNikeName = (RelativeLayout) findViewById(R.id.personalNikeName);
        personalSex = (RelativeLayout) findViewById(R.id.personalSex);
        personalMessage = (RelativeLayout) findViewById(R.id.personalMessage);

        personalImage_imageView = (ImageView) findViewById(R.id.personalImage_imageView);
        personalEmail_textView = (TextView) findViewById(R.id.personalEmail_textView);
        personalMessage_textView = (TextView) findViewById(R.id.personalMessage_textView);
        personalNikeName_textView = (TextView) findViewById(R.id.personalNikeName_textView);
        personalSex_textView = (TextView) findViewById(R.id.personalSex_textView);

        initData();
        initViewListener();
    }

    private void initData(){
        AVUser avUser = AVUser.getCurrentUser();

        personalEmail_textView.setText(avUser.getEmail());
        personalSex_textView.setText(avUser.getBoolean("isMale") == true ? "男" : "女");
        personalNikeName_textView.setText(avUser.getString("nikeName"));
        personalMessage_textView.setText(avUser.getString("message"));

        Glide.with(getApplicationContext())
                .load(avUser.getAVFile("userPhoto").getUrl())
                .signature(new StringSignature(avUser.getAVFile("userPhoto").getUrl()))
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.a0c)
                .error(R.drawable.default_error)
                .centerCrop()
                .into(personalImage_imageView);
    }

    private void initViewListener(){
        personalEmail.setOnClickListener(this);
        personalImage.setOnClickListener(this);
        personalMessage.setOnClickListener(this);
        personalNikeName.setOnClickListener(this);
        personalSex.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.personalEmail:
                Intent intentEmail = new Intent(PersonalInfoActivity.this, ModifyActivity.class);
                intentEmail.putExtra("limit", 0);//代表没有限制
                intentEmail.putExtra("request", REQUEST_EMAIL);
                startActivityForResult(intentEmail, REQUEST_EMAIL);
                break;
            case R.id.personalImage:
                break;
            case R.id.personalNikeName:
                Intent intentNikeName = new Intent(PersonalInfoActivity.this, ModifyActivity.class);
                intentNikeName.putExtra("limit", 0);//代表没有限制
                intentNikeName.putExtra("request", REQUEST_NIKENAME);
                startActivityForResult(intentNikeName, REQUEST_NIKENAME);
                break;
            case R.id.personalSex:
                break;
            case R.id.personalMessage:
                Intent intentMessage = new Intent(PersonalInfoActivity.this, ModifyActivity.class);
                intentMessage.putExtra("limit", 20);
                intentMessage.putExtra("request", REQUEST_MESSAGE);
                startActivityForResult(intentMessage, REQUEST_MESSAGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_EMAIL:
                break;
            case REQUEST_MESSAGE:
                break;
            case REQUEST_NIKENAME:
                break;
        }
    }
}

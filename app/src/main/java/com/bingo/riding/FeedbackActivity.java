package com.bingo.riding;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.bingo.riding.utils.Utils;

public class FeedbackActivity extends AppCompatActivity {

    private Button submit_feedback;
    private EditText feedBack_edit_text;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        //Transparent Status Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.parseColor("#3F51B5"));
        }

        initView();
    }

    private void initView(){
        /* setup toolbar */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("发表状态");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submit_feedback = (Button) findViewById(R.id.submit_feedback);
        feedBack_edit_text = (EditText) findViewById(R.id.feedBack_edit_text);


        initListener();
    }


    private void initListener(){
        submit_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String feedBackContent = feedBack_edit_text.getText().toString().trim();
                if (feedBackContent.length() == 0){
                    Toast.makeText(FeedbackActivity.this, "请输入反馈内容", Toast.LENGTH_SHORT).show();
                }else{
                    AVObject feedbackAvObject = new AVObject("feedback");
                    feedbackAvObject.put("sender", AVUser.getCurrentUser());
                    feedbackAvObject.put("content", feedBackContent);
                    feedbackAvObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null){
                                Toast.makeText(FeedbackActivity.this, "发送反馈成功，谢谢您的建议......", Toast.LENGTH_SHORT).show();

                                Utils.closeSoftInput(FeedbackActivity.this, feedBack_edit_text);

                                FeedbackActivity.this.finish();
                            }else {
                                Toast.makeText(FeedbackActivity.this, "发送反馈失败，请稍后重试......", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}

package com.bingo.riding;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ModifyActivity extends AppCompatActivity {

    private int limit = 0;
    private int request = -1;

    private EditText modifyInfo;
    private TextView numberLimit;
    private TextView hintTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        Intent intent = getIntent();
        if (intent == null){
            this.finish();
            return;
        }

        numberLimit = (TextView) findViewById(R.id.numberLimit);
        hintTextView = (TextView) findViewById(R.id.hintTextView);
        modifyInfo = (EditText) findViewById(R.id.modifyInfo);

        limit = intent.getIntExtra("limit", 0);
        request = intent.getIntExtra("request", request);
        switch (request){
            case PersonalInfoActivity.REQUEST_NIKENAME:
                setTitle("修改昵称");
                hintTextView.setVisibility(View.VISIBLE);
                hintTextView.setText("好名字可以让你的朋友更容易记住你。");
                break;
            case PersonalInfoActivity.REQUEST_EMAIL:
                setTitle("修改邮箱");
                hintTextView.setVisibility(View.GONE);
                break;
            case PersonalInfoActivity.REQUEST_MESSAGE:
                setTitle("修改个性签名");
                hintTextView.setVisibility(View.GONE);
                break;
        }



        if (limit == 0){
            numberLimit.setVisibility(View.INVISIBLE);
        }else{
            numberLimit.setText(limit + "");
            modifyInfo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(limit)});
            modifyInfo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (limit != 0) {
                        if (s.length() < limit) {
                            numberLimit.setText(limit - modifyInfo.getText().length() + "");
                        } else {
                            numberLimit.setText("0");
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

    }
}

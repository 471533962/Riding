package com.bingo.riding.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.bingo.riding.CustomActivity;
import com.bingo.riding.MainActivity;
import com.bingo.riding.R;
import com.bingo.riding.utils.Utils;

/**
 * Created by bingo on 15/10/12.
 */
public class RegisterFragment extends Fragment {

    private Button registerBtn;
    private EditText userEmail;
    private EditText userName;
    private EditText password;
    private EditText re_password;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        initView(view);
        return view;
    }

    private void initView(View view){
        registerBtn = (Button) view.findViewById(R.id.register);
        userEmail = (EditText) view.findViewById(R.id.userEmail);
        userName = (EditText) view.findViewById(R.id.username);
        password = (EditText) view.findViewById(R.id.password);
        re_password = (EditText) view.findViewById(R.id.re_password);


        initViewListener();
    }

    private void initViewListener(){
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString();
                String username = userName.getText().toString();
                String pwd = password.getText().toString();
                String re_pwd = re_password.getText().toString();

                if (email.length() == 0 || username.length() == 0 || pwd.length() == 0 || re_pwd.length() == 0){
                    Toast.makeText(getActivity(), "邮箱、用户名、密码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }

                if (Utils.isEmail(email) == false){
                    Toast.makeText(getActivity(), "请确认邮箱是否正确", Toast.LENGTH_LONG).show();
                    return;
                }

                if (pwd.equals(re_pwd) == false){
                    Toast.makeText(getActivity(), "前后输入密码不统一", Toast.LENGTH_LONG).show();
                    return;
                }

                AVUser avUser = new AVUser();
                avUser.setEmail(email);
                avUser.setUsername(email);
                avUser.setPassword(pwd);
                avUser.put("nikeName", username);
                avUser.setFetchWhenSave(true);

                ((CustomActivity)getActivity()).showLoadingDialog("注册中...");
                avUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(AVException e) {
                        ((CustomActivity)getActivity()).dismissLoadingDialog();
                        if (e == null){
                            Toast.makeText(getActivity(), "注册成功", Toast.LENGTH_LONG).show();

                            Utils.startActivity(getActivity(), MainActivity.class);
                            Utils.finish(getActivity());
                        }else {
                            Toast.makeText(getActivity(), "注册失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}

package com.bingo.riding.fragment;

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
import com.avos.avoscloud.LogInCallback;
import com.bingo.riding.CustomActivity;
import com.bingo.riding.MainActivity;
import com.bingo.riding.R;
import com.bingo.riding.utils.Utils;

/**
 * Created by bingo on 15/10/12.
 */
public class LoginFragment extends Fragment {

    private Button loginBtn;
    private EditText userEmail;
    private EditText pwd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        initView(view);
        return view;
    }

    private void initView(View view){
        loginBtn = (Button) view.findViewById(R.id.login);
        userEmail = (EditText) view.findViewById(R.id.userEmail);
        pwd = (EditText) view.findViewById(R.id.password);

        initViewListenr();
    }

    private void initViewListenr(){
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString();
                String password = pwd.getText().toString();

                if (email.length() == 0 || password.length() == 0 ){
                    Toast.makeText(getActivity(), "邮箱、密码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }

                if (Utils.isEmail(email) == false){
                    Toast.makeText(getActivity(), "请确认邮箱是否正确", Toast.LENGTH_LONG).show();
                    return;
                }


                ((CustomActivity)getActivity()).showLoadingDialog("登录中...");
                AVUser.logInInBackground(email, password, new LogInCallback<AVUser>() {
                    @Override
                    public void done(AVUser avUser, AVException e) {
                        ((CustomActivity)getActivity()).dismissLoadingDialog();
                        if (e == null){
                            Utils.startActivity(getActivity(), MainActivity.class);
                            Utils.finish(getActivity());
                        } else{
                            Toast.makeText(getActivity(), "登录失败，请检查后重试。", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}

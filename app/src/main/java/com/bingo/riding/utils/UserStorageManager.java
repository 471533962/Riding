package com.bingo.riding.utils;

import android.content.Context;

/**
 * Created by bingo on 16/1/4.
 */
public class UserStorageManager {

    private Context mContext;
    private UserStorageManager userStorageManager;

    private UserStorageManager(){
    }

    public UserStorageManager getInstance(Context context){
        mContext = context;
        if (userStorageManager == null){
            userStorageManager = new UserStorageManager();
        }
        return userStorageManager;
    }

}

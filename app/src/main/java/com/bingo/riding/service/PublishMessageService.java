package com.bingo.riding.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.bingo.riding.bean.Message;
import com.bingo.riding.event.PublishMessageEvent;
import com.bingo.riding.utils.DataTools;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class PublishMessageService extends Service {
    public PublishMessageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            String content = intent.getStringExtra("content");
            ArrayList<String> photoList = intent.getStringArrayListExtra("photoList");

            addPublishMessage(content, photoList);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void addPublishMessage(final String content, final ArrayList<String> photoList){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AVObject squareMessage = DataTools.getSquareMessage(PublishMessageService.this, content, photoList);
                    Log.e("bingo", "开始保存");
                    squareMessage.setFetchWhenSave(true);
                    squareMessage.save();
                    Log.e("bingo", "结束保存");
                    EventBus.getDefault().post(new PublishMessageEvent(new Message(content, squareMessage, photoList, AVUser.getCurrentUser()), true));
                }catch (Exception e){
                    EventBus.getDefault().post(new PublishMessageEvent(null, false));
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

package com.bingo.riding;

import android.app.Application;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.bingo.riding.utils.AVImClientManager;
import com.bingo.riding.utils.MessageHandler;

/**
 * Created by bingo on 15/10/21.
 */
public class BaseApplication extends Application {

    private final String APP_ID = "htPLJhdgSHxlqSDerk4wu4x7";
    private final String APP_KEY = "G0yV0g45lXjnUeVUncnvWlxS";

    @Override
    public void onCreate() {
        super.onCreate();
//        AVOSCloud.useAVCloudUS();
        AVOSCloud.initialize(this, APP_ID, APP_KEY);
//        .initialize(getApplicationContext());
        AVIMMessageManager.registerDefaultMessageHandler(new MessageHandler(this));

        AVOSCloud.setDebugLogEnabled(true);

        //注册设备到服务端
        PushService.setDefaultPushCallback(this, MessageMangerActivity.class);
        PushService.subscribe(this, "Discussion", MessageMangerActivity.class);
        AVInstallation.getCurrentInstallation().saveInBackground();
        //测试环境云代码
        AVCloud.setProductionMode(false);
    }
}

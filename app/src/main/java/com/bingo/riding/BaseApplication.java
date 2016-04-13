package com.bingo.riding;

import android.app.Application;
import android.os.Process;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.bingo.riding.utils.MessageHandler;
import com.facebook.stetho.Stetho;

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

        //调试数据库
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());


        //添加崩溃反馈
//        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
    }

    class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            ex.printStackTrace();
            //do some thing

            Process.killProcess(Process.myPid());
            System.exit(1);
        }
    }
}

package com.xmq.bind;

import android.app.Application;

import com.xmq.xbind.core.XBinder;

/**
 * @author xmqyeah
 * @CreateDate 2021/8/23 22:30
 */
public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        XBinder.init();
    }
}

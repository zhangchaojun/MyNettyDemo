package com.example.myclient.application;

import android.app.Application;

/**
 * @author zcj
 * @date 2019/8/22
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }

    public static MyApplication getInstance() {
        return instance;
    }
}

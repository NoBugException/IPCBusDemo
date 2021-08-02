package com.ipcbus.demo;

import android.app.Application;

import com.noexception.ipcbus.IPCBus;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 注册接收类
        IPCBus.getDefault().regist(MyReceive.class);
    }
}

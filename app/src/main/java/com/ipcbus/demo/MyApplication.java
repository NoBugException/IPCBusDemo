package com.ipcbus.demo;

import android.app.Application;

import com.noexception.ipcbus.IPCBus;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化
        IPCBus.getDefault().init(getApplicationContext(), "com.ipcbus.ipcbusservice");
        IPCBus.getDefault().init(getApplicationContext(), "com.nobug.ipcbusservice2");
        IPCBus.getDefault().init(getApplicationContext(), getPackageName());
        // 注册接收类
        IPCBus.getDefault().regist(MyReceive.class);
    }
}

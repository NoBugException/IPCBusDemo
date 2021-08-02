package com.noexception.ipcbus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.noexception.ipcbus.inter.ICPReceive;
import com.noexception.ipcbus.inter.IIPCBusConnected;
import com.noexception.ipcbus.message.IIPCBusService;
import com.noexception.ipcbus.message.Request;
import com.noexception.ipcbus.message.Responce;
import com.noexception.ipcbus.message.ResultCode;
import com.noexception.ipcbus.service.IPCBusService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class IPCBus {

    // 客户端是否连接服务器
    private boolean isConnect = false;

    private Context mContext;

    // 响应对象
    private Responce responce = null;

    // 接收类
     private Class<?> receiveClass;

    // 服务缓存
    private IIPCBusService iipcBusService;

    private IPCBus() {}

    /**
     * 注册接收类
     *
     * @param receiveClass receiveClass
     */
    public void regist(Class<?> receiveClass) {
        this.receiveClass = receiveClass;
    }

    /**
     * 获取接收类
     *
     * @return getReceiveClass
     */
    public Class<?> getReceiveClass () {
        return receiveClass;
    }

    private static class IPCBusHolder {
        public static IPCBus instance = new IPCBus();
    }

    // 静态内部类单例
    public static IPCBus getDefault() {
        return IPCBusHolder.instance;
    }

    /**
     *  初始化
     * @param mContext mContext
     */
    public void init(Context mContext) {
        this.mContext = mContext;
        // 连接
        connect(null);
    }

    /**
     * 自定义ServiceConnection
     */
    private class IPCBusServiceConnection implements ServiceConnection {

        private IIPCBusConnected iipcBusConnected;

        public IPCBusServiceConnection(IIPCBusConnected iipcBusConnected){
            this.iipcBusConnected = iipcBusConnected;
        }


        /**
         * Service连接成功
         *
         * @param name name
         * @param service service
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (iipcBusConnected != null) {
                iipcBusConnected.success();
            }
            isConnect = true;
            iipcBusService = IIPCBusService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (iipcBusConnected != null) {
                iipcBusConnected.failed();
            }
            isConnect = false;
            iipcBusService = null;
        }
    }

    /**
     * 客户端进程请求服务器
     *
     * @param request request
     */
    public Responce send (final Request request) {
        if (request == null) {
            responce = nullRequest();
            return responce;
        }
        if (isConnect) {
            // 发送消息
            responce = sendMessage(request);
            return responce;
        } else {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            // 重新连接
            connect(new IIPCBusConnected() {
                @Override
                public void success() {
                    responce = sendMessage(request);
                    countDownLatch.countDown();
                }

                @Override
                public void failed() {
                    responce = responceIfNotConnect();
                    countDownLatch.countDown();
                }
            });
            try {
                // 等待最多两秒
                countDownLatch.await(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (responce == null) {
                responce = responceIfNotConnect();
                return responce;
            }
        }
        return responce;
    }

    /**
     * 发送消息
     *
     * @param request request
     * @return sendMessage
     */
    private Responce sendMessage(Request request) {

        if (iipcBusService == null) {
            // 服务没有连接
            return responceIfNotConnect();
        }
        Responce responce = null;
        try {
            responce = iipcBusService.send(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (responce == null) {
            // 空返回异常
            return nullResponce();
        }
        return responce;
    }

    /**
     * 空请求
     *
     * @return nullRequest
     */
    private Responce nullRequest(){
        Responce responce = new Responce();
        responce.setResultCode(ResultCode.ERROR_REQUEST_NULL);
        responce.setData("");
        responce.setErrorMsg("request is null");
        return responce;
    }

    /**
     * 远程没有注册接收类
     *
     * @return notRegisterReceiveRequest
     */
    public Responce notRegisterReceiveRequest(){
        Responce responce = new Responce();
        responce.setData("");
        responce.setErrorMsg("the remote does not have a receive class registered");
        responce.setResultCode(ResultCode.ERROR_REMOTE_NOT_REGISTER_RECEIVE);
        return responce;
    }

    /**
     * 获取不到远程代理对象
     *
     * @return getProxynullRequest
     */
    public Responce getProxynullRequest(){
        Responce responce = new Responce();
        responce.setData("");
        responce.setErrorMsg("failed to get remote proxy object");
        responce.setResultCode(ResultCode.ERROR_GET_PROXY_OBJECT);
        return responce;
    }

    /**
     * 服务器进程没有任何返回
     *
     * @return nullResponce
     */
    private Responce nullResponce(){
        Responce responce = new Responce();
        responce.setResultCode(ResultCode.ERROR_RESPONCE_NULL);
        responce.setData("");
        responce.setErrorMsg("responce is null");
        return responce;
    }

    /**
     * 客户端进程没有和服务端进程连接
     *
     * @return responceIfNotConnect
     */
    private Responce responceIfNotConnect(){
        Responce responce = new Responce();
        responce.setResultCode(ResultCode.ERROR_REQUEST_SERVICE_NO_CONNECTED);
        responce.setData("");
        responce.setErrorMsg("service is not connected");
        return responce;
    }

    /**
     * 客户端进程，连接服务器进程
     */
    private void connect(IIPCBusConnected iipcBusConnected) {
        if (mContext == null) {
            throw new RuntimeException("context is null");
        }
        Intent intent = new Intent(mContext, IPCBusService.class);
        mContext.startService(intent);
        mContext.bindService(intent, new IPCBusServiceConnection(iipcBusConnected), Context.BIND_AUTO_CREATE);
    }

    /**
     * 断开连接
     */
    public void unConnected() {
        isConnect = false;
        iipcBusService = null;
        responce = null;
    }
}

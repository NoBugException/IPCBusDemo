package com.noexception.ipcbus.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.Nullable;

import com.noexception.ipcbus.IPCBus;
import com.noexception.ipcbus.handler.MessageHandler;
import com.noexception.ipcbus.inter.ICPReceive;
import com.noexception.ipcbus.message.IIPCBusService;
import com.noexception.ipcbus.message.Request;
import com.noexception.ipcbus.message.Responce;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

public class IPCBusService extends Service {

    private String TAG = IPCBusService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new IPCBusBinder();
    }

    class IPCBusBinder extends IIPCBusService.Stub{

        /**
         * 客户端进程将数据发送给服务器进程
         *
         * @param request request
         * @return send
         * @throws RemoteException RemoteException
         */
        @Override
        public Responce send(Request request) throws RemoteException {
            if(request == null) {
                Log.d(TAG, "request is null");
                return null;
            }
            Class<?> receiveClass = IPCBus.getDefault().getReceiveClass();
            if (receiveClass == null) {
                return IPCBus.getDefault().notRegisterReceiveRequest();
            }
            Object object = null;
            try {
                Constructor constructor = receiveClass.getDeclaredConstructor();
                object = constructor.newInstance();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            if (object == null) {
                return IPCBus.getDefault().getProxynullRequest();
            }
            MessageHandler handler = new MessageHandler(object);
            ICPReceive receive = (ICPReceive) Proxy.newProxyInstance(receiveClass.getClassLoader(), receiveClass.getInterfaces(), handler);
            return receive.receive(request);
        }
    }
}

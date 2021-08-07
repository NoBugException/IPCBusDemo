package com.noexception.ipcbus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import com.noexception.ipcbus.error.ErrorConstant;
import com.noexception.ipcbus.error.ErrorData;
import com.noexception.ipcbus.inter.IIPCBusConnected;
import com.noexception.ipcbus.message.IIPCBusService;
import com.noexception.ipcbus.message.Request;
import com.noexception.ipcbus.message.Responce;
import com.noexception.ipcbus.message.ResultCode;
import com.noexception.ipcbus.service.IPCBusService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IPCBus {

    private static final String TAG = IPCBus.class.getSimpleName();
    private Context mContext;

    // 响应对象
    private Responce responce = null;

    // 接收类
     private Class<?> receiveClass;

    // 根据不同的包名来管理服务代理
    private Map<String, IIPCBusService> ipcBusServiceMap = new ConcurrentHashMap();

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
    public void init(Context mContext, String servicePackageName) {
        this.mContext = mContext;
        // 连接
        connect(null, servicePackageName);
    }

    /**
     * 填充错误数据
     *
     * @param errorCode 错误码
     * @param description 错误描述
     * @return fillErrorData
     */
    private ErrorData fillErrorData(int errorCode, String description) {
        ErrorData errorData = new ErrorData();
        errorData.setErrorCode(errorCode);
        errorData.setDescription(description);
        return errorData;
    }

    /**
     * 意外断开重连
     */
    private class DeathRecipient implements IBinder.DeathRecipient {

        private IIPCBusConnected iipcBusConnected;

        // 服务对应的包名
        private String servicePackageName;

        public DeathRecipient(IIPCBusConnected iipcBusConnected, String servicePackageName){
            this.iipcBusConnected = iipcBusConnected;
            this.servicePackageName = servicePackageName;
        }

        @Override
        public void binderDied() {
            // 重连
            connect(iipcBusConnected, servicePackageName);
        }
    }

    /**
     * 自定义ServiceConnection
     */
    private class IPCBusServiceConnection implements ServiceConnection {

        private IIPCBusConnected iipcBusConnected;

        // 服务对应的包名
        private String servicePackageName;

        public IPCBusServiceConnection(IIPCBusConnected iipcBusConnected, String servicePackageName){
            this.iipcBusConnected = iipcBusConnected;
            this.servicePackageName = servicePackageName;
        }

        /**
         * Service连接成功
         *
         * @param name name
         * @param service service
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                service.linkToDeath(new DeathRecipient(iipcBusConnected, servicePackageName), 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(servicePackageName)) {
                if (iipcBusConnected != null) {
                    ErrorData errorData = fillErrorData(ErrorConstant.SERVICE_PACKAGE_EMPTY.ERRORCODE, ErrorConstant.SERVICE_PACKAGE_EMPTY.DESCRIPTION);
                    iipcBusConnected.failed(errorData);
                }
                return;
            }
            IIPCBusService iipcBusService = IIPCBusService.Stub.asInterface(service);
            if (iipcBusService == null) { // 如果对应的服务代理对象为空
                if (iipcBusConnected != null) {
                    ErrorData errorData = fillErrorData(ErrorConstant.CONNECT_PROXY_EMPTY_CONSTANT.ERRORCODE, ErrorConstant.CONNECT_PROXY_EMPTY_CONSTANT.DESCRIPTION);
                    iipcBusConnected.failed(errorData);
                }
                // 移除空数据
                ipcBusServiceMap.remove(servicePackageName);
                return;
            }
            // 将当前代理对象添加到map集合来统一管理
            ipcBusServiceMap.put(servicePackageName, iipcBusService);
            if (iipcBusConnected != null) {
                iipcBusConnected.success();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (iipcBusConnected != null) {
                ErrorData errorData = fillErrorData(ErrorConstant.SERVICE_CONNECT_FAILED.ERRORCODE, ErrorConstant.SERVICE_CONNECT_FAILED.DESCRIPTION);
                iipcBusConnected.failed(errorData);
            }
            // 移除空数据
            ipcBusServiceMap.remove(servicePackageName);
        }
    }

    /**
     * 客户端进程请求服务器
     *
     * @param request 请求对象
     */
    public Responce send (Request request) {
        if (mContext == null) {
            // 保证上下文存在，即保证执行了init方法
            throw new RuntimeException("context is null, please init context");
        }
        return send(request, mContext.getPackageName());
    }
    /**
     * 客户端进程请求服务器
     *
     * @param request 请求对象
     * @param servicePackageName 服务端包名
     */
    public Responce send (final Request request, final String servicePackageName) {
        if (request == null) {
            responce = nullRequest();
            return responce;
        }
        IIPCBusService iipcBusService = ipcBusServiceMap.get(servicePackageName);
        if (iipcBusService != null) {
            // 发送消息
            responce = sendMessage(request, servicePackageName);
            return responce;
        } else {
            // 重新连接
            connect(null, servicePackageName);
            // 返回未连接的错误
            responce = responceIfNotConnect();
        }
        return responce;
    }

    /**
     * 发送消息
     *
     * @param request 请求对象
     * @param servicePackageName 服务包名
     * @return sendMessage
     */
    private Responce sendMessage(Request request, String servicePackageName) {
        IIPCBusService iipcBusService = ipcBusServiceMap.get(servicePackageName);
        if (iipcBusService == null) {
            // 移除空数据
            ipcBusServiceMap.remove(servicePackageName);
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
     *
     * @param iipcBusConnected iipcBusConnected
     * @param servicePackageName servicePackageName
     */
    private void connect(IIPCBusConnected iipcBusConnected, String servicePackageName) {
        if (mContext == null) {
            // 保证上下文存在，即保证执行了init方法
            throw new RuntimeException("context is null, please init context");
        }
        if (TextUtils.isEmpty(servicePackageName)) {
            // 启动服务（如果没有执行包名，那么默认是应用本身）
            startIPCService(iipcBusConnected);
        } else {
            // 启动服务
            startIPCService(iipcBusConnected, servicePackageName);
        }
    }

    /**
     * 启动服务（同一个包之间的跨进程）
     *
     * @param iipcBusConnected iipcBusConnected
     */
    private void startIPCService(IIPCBusConnected iipcBusConnected) {
        if (mContext == null) {
            throw new RuntimeException("context is null");
        }
        Intent intent = new Intent(mContext, IPCBusService.class);
        mContext.bindService(intent, new IPCBusServiceConnection(iipcBusConnected, mContext.getPackageName()), Context.BIND_AUTO_CREATE);
    }

    /**
     * 启动服务（不同包之间的跨进程）
     *
     * @param iipcBusConnected iipcBusConnected
     * @param packageName packageName
     */
    private void startIPCService(final IIPCBusConnected iipcBusConnected, String packageName) {
        if (mContext == null) {
            throw new RuntimeException("context is null");
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, IPCBusService.class.getName()));
        mContext.bindService(intent, new IPCBusServiceConnection(iipcBusConnected, packageName), Context.BIND_AUTO_CREATE);
    }

    /**
     * 断开连接
     */
    public void unConnected(String servicePackageName) {
        responce = null;
        if (!TextUtils.isEmpty(servicePackageName)) {
            ipcBusServiceMap.remove(servicePackageName);
        }
    }
}

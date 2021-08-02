package com.noexception.ipcbus.message;

/**
 * 返回码
 */
public class ResultCode {

    // 返回成功
    public static final int SUCCESS_CODE = 0;

    // 请求体为空
    public static final int ERROR_REQUEST_NULL = -1;

    // 服务未连接
    public static final int ERROR_REQUEST_SERVICE_NO_CONNECTED = -2;

    // 响应对象为空
    public static final int ERROR_RESPONCE_NULL = -3;

    // 远程没有注册接收类
    public static final int ERROR_REMOTE_NOT_REGISTER_RECEIVE = -4;

    // 获取不到远程代理对象
    public static final int ERROR_GET_PROXY_OBJECT = -5;
}

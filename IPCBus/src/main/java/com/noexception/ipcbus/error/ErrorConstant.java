package com.noexception.ipcbus.error;

/**
 * 错误常量
 */
public class ErrorConstant {

    // 连接Service之后，代理对象为空错误
    private static final int CONNECT_PROXY_EMPTY_ERROR = 0x01;

    // 服务包名为空
    private static final int SERVICE_PACKAGE_EMPTY_ERROR = 0x02;

    // 连接服务失败
    private static final int SERVICE_CONNECT_FAILED_ERROR = 0x03;

    // 连接Service之后，代理对象为空错误
    public interface CONNECT_PROXY_EMPTY_CONSTANT {
        int ERRORCODE = CONNECT_PROXY_EMPTY_ERROR;
        String DESCRIPTION = "after connecting to the service, the proxy object is empty";
    }

    // 服务包名为空
    public interface SERVICE_PACKAGE_EMPTY {
        int ERRORCODE = SERVICE_PACKAGE_EMPTY_ERROR;
        String DESCRIPTION = "the service package name is empty, please ensure that the service package name cannot be empty";
    }

    // 连接服务失败
    public interface SERVICE_CONNECT_FAILED {
        int ERRORCODE = SERVICE_CONNECT_FAILED_ERROR;
        String DESCRIPTION = "failed to connect to the service";
    }
}

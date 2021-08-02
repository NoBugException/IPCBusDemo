package com.noexception.ipcbus.inter;

public interface IIPCBusConnected {

    /**
     * 连接服务成功
     */
    void success();

    /**
     * 连接服务失败
     */
    void failed();

}

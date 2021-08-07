package com.noexception.ipcbus.inter;

import com.noexception.ipcbus.error.ErrorData;

public interface IIPCBusConnected {

    /**
     * 连接服务成功
     */
    void success();

    /**
     * 连接服务失败
     * @param errorData 错误数据
     */
    void failed(ErrorData errorData);

}

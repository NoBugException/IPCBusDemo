package com.noexception.ipcbus.message;

import com.noexception.ipcbus.message.Request;
import com.noexception.ipcbus.message.Responce;

interface IIPCBusService {

    // 发送
    Responce send(inout Request request);
}

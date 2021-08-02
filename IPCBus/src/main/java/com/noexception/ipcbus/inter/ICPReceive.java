package com.noexception.ipcbus.inter;

import com.noexception.ipcbus.message.Request;
import com.noexception.ipcbus.message.Responce;

public interface ICPReceive {

    /**
     * 接收消息
     *
     * @param request request
     * @return receive
     */
    Responce receive(Request request);
}

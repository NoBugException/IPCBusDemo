package com.noexception.ipcbus.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MessageHandler implements InvocationHandler {

    private Object object;

    public MessageHandler(Object object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(object, args);
    }
}

package com.ipcbus.demo;

import android.util.Log;

import com.noexception.ipcbus.inter.ICPReceive;
import com.noexception.ipcbus.message.Request;
import com.noexception.ipcbus.message.Responce;
import com.noexception.ipcbus.message.ResultCode;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 服务端侧申明接口的地方
 */
public class MyReceive implements ICPReceive {

    @Override
    public Responce receive(Request request) {
        Responce responce = null;
        if ("addition".equals(request.getRequestName())) { // 加法运算
            responce = new Responce();
            responce.setResultCode(ResultCode.SUCCESS_CODE);
            try {
                JSONObject jsonObject = new JSONObject(request.getParamJson());
                int parameter1 = jsonObject.optInt("parameter1");
                int parameter2 = jsonObject.optInt("parameter2");
                responce.setData(String.valueOf(parameter1 + parameter2));
            } catch (JSONException e) {
                responce.setData("数据异常");
            }
        } else if ("subtraction".equals(request.getRequestName())) { // 减法运算
            responce = new Responce();
            responce.setResultCode(ResultCode.SUCCESS_CODE);
            try {
                JSONObject jsonObject = new JSONObject(request.getParamJson());
                int parameter1 = jsonObject.optInt("parameter1");
                int parameter2 = jsonObject.optInt("parameter2");
                responce.setData(String.valueOf(parameter1 - parameter2));
            } catch (JSONException e) {
                responce.setData("数据异常");
            }
        } else {
            // 其它
        }
        return responce;
    }
}

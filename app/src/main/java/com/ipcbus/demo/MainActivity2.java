package com.ipcbus.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.noexception.ipcbus.IPCBus;
import com.noexception.ipcbus.message.Request;
import com.noexception.ipcbus.message.Responce;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // 与应用A连接
        final String servicePackageName = "com.ipcbus.ipcbusservice";
        // 与应用B连接
        final String servicePackageName2 = "com.nobug.ipcbusservice2";

        Button addition = findViewById(R.id.addition);
        addition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request request = new Request();
                request.setRequestName("addition");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("parameter1", 5);
                    jsonObject.put("parameter2", 4);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                request.setParamJson(jsonObject.toString());
                // 发送消息
                Responce responce = IPCBus.getDefault().send(request, servicePackageName);
                if (responce != null) {
                    Toast.makeText(MainActivity2.this, "加法结果:" + responce.getData(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 减法
        Button subtraction = findViewById(R.id.subtraction);
        subtraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request request = new Request();
                request.setRequestName("subtraction");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("parameter1", 5);
                    jsonObject.put("parameter2", 4);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                request.setParamJson(jsonObject.toString());
                // 发送消息
                Responce responce = IPCBus.getDefault().send(request);
                if (responce != null) {
                    Toast.makeText(MainActivity2.this, "减法结果:" + responce.getData(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 断开连接
        IPCBus.getDefault().unConnected(getPackageName());
    }
}

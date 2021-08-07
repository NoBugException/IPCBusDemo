# IPCBus ：跨进程通信架构

IPCBus是一个支持多通道、跨进程的通信架构，用起来比AIDL简单。

`[第一步]` 在项目根目录的build.gradle中引入jitpack maven

    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
    
    
`[第二步]` 引入依赖

    implementation 'com.github.NoBugException:IPCBusDemo:1.0.1'
    
`[第四步]` 在A进程初始化上下文

在Application的onCreate方法中初始化上下文

        // 初始化
        IPCBus.getDefault().init(getApplicationContext(), "com.ipcbus.ipcbusservice");
        IPCBus.getDefault().init(getApplicationContext(), "com.nobug.ipcbusservice2");
        IPCBus.getDefault().init(getApplicationContext(), getPackageName());
        
可以指定多个服务包名，也就是指定多个通信的通道。

`[第五步]` A进程发送请求

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
                
Request相当于网络请求的请求体，Responce相当于响应体。


`[第六步]` B进程注册服务

        <!--定义服务-->
        <service android:name="com.noexception.ipcbus.service.IPCBusService"
            android:exported="true"/>
            
如果是同一个应用之间的多进程通信，不需要将exported指定为true， 如果是不同应用的通信，必须将exported置为true。
exported置为true，可能涉及到安全性问题，可以在service中指明自定义权限。
该service的name为固定写法，它被封装在依赖中。

`[第七步]` B进程注册接收类

        // 注册接收类
        IPCBus.getDefault().regist(MyReceive.class);
        
在自定义Application中注册接收类MyReceive， MyReceive的代码举例：

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
    
    用于给发送方响应数据。

            

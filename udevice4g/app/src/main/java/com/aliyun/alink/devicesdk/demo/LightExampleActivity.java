package com.aliyun.alink.devicesdk.demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linksdk.tmp.api.MapInputParams;
import com.aliyun.alink.linksdk.tmp.api.OutputParams;
import com.aliyun.alink.linksdk.tmp.device.payload.ValueWrapper;
import com.aliyun.alink.linksdk.tmp.devicemodel.Service;
import com.aliyun.alink.linksdk.tmp.listener.IPublishResourceListener;
import com.aliyun.alink.linksdk.tmp.listener.ITResRequestHandler;
import com.aliyun.alink.linksdk.tmp.listener.ITResResponseCallback;
import com.aliyun.alink.linksdk.tmp.utils.ErrorInfo;
import com.aliyun.alink.linksdk.tools.AError;
import com.aliyun.alink.linksdk.tools.ALog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright (c) 2014-2016 Alibaba Group. All rights reserved.
 * License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/**
 * 注意！！！！
 * 1.该示例只共快速接入使用，只适用于有 Status、data属性的快速接入测试设备；
 * 2.真实设备可以参考 ControlPanelActivity 里面有数据上下行示例；
 */
public class LightExampleActivity extends BaseActivity {

    private final static int REPORT_MSG = 0x100;

    TextView consoleTV;
    String consoleStr;
    private InternalHandler mHandler = new InternalHandler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在初始化的时候可以设置 灯的初始状态，或者等初始化完成之后 上报一次设备所有属性的状态
        // 注意在调用云端接口之前确保初始化完成了
        setContentView(R.layout.activity_light_example);
        consoleTV = (TextView) findViewById(R.id.textview_console);
        setServiceHandler();
        showToast("已启动每5秒上报一次状态");
        log("已启动每5秒上报一次状态");
        mHandler.sendEmptyMessageDelayed(REPORT_MSG, 2 * 1000);
    }

    /**
     * 数据上行
     * 上报灯的状态
     */
    public void reportHelloWorld() {
        log("上报 Hello World！");
        try {
            Map<String, ValueWrapper> reportData = new HashMap<>();
            reportData.put("Status", new ValueWrapper.BooleanValueWrapper(1)); // 1开 0 关
            reportData.put("data", new ValueWrapper.StringValueWrapper("hello,world")); //
            LinkKit.getInstance().getDeviceThing().thingPropertyPost(reportData, new IPublishResourceListener() {
                @Override
                public void onSuccess(String s, Object o) {
                    Log.d(TAG, "onSuccess() called with: s = [" + s + "], o = [" + o + "]");
                    showToast("设备上报状态成功");
                    log("上报 Hello World 成功。");
                }

                @Override
                public void onError(String s, AError aError) {
                    Log.d(TAG, "onError() called with: s = [" + s + "], aError = [" + aError + "]");
                    showToast("设备上报状态失败");
                    log("上报 Hello World 失败。");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 物模型数据下行    需要远程触发如云端、或其他控制APP
     * 云端调用设备的某项服务的时候，设备端需要响应该服务并回复。
     * 设备端事件触发的时候需要调用这个接口上报事件，如事件告警等
     * 需要用户在云端定义不同的 Error 的类型
     */
    private void setServiceHandler() {
        Log.d(TAG, "setServiceHandler() called");
        List<Service> srviceList = LinkKit.getInstance().getDeviceThing().getServices();
        for (int i = 0; srviceList != null && i < srviceList.size(); i++) {
            Service service = srviceList.get(i);
            LinkKit.getInstance().getDeviceThing().setServiceHandler(service.getIdentifier(), mCommonHandler);
        }
        //
    }

    private ITResRequestHandler mCommonHandler = new ITResRequestHandler() {
        @Override
        public void onProcess(String identify, Object result, ITResResponseCallback itResResponseCallback) {
            Log.d(TAG, "onProcess() called with: s = [" + identify + "], o = [" + result + "], itResResponseCallback = [" + itResResponseCallback + "]");
            showToast("收到云端异步服务调用 " + identify);
            String data= null;
            try {
                if (result instanceof MapInputParams){
                    data = JSONObject.toJSONString(((MapInputParams) result).getData());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            log("收到云端异步服务调用 " + identify + " " + data);
            try {
                if ("set".equals(identify)) {
                    // TODO  用户按照真实设备的接口调用  设置设备的属性
                    // 设置完真实设备属性之后，上报设置完成的属性值
                    // 用户根据实际情况判断属性是否设置成功 这里测试直接返回成功
                    boolean isSetPropertySuccess = true;
                    if (isSetPropertySuccess) { // 这里假设用户调用设备真实的开关成功
                        // 设置成功之后，需要上报成功的状态给到云端，直接传null即可，物模型内部有缓存，会上报最新的值到云端
                        itResResponseCallback.onComplete(identify, null, null); // 收到云端下行之后 回复云端
                    } else {
                        AError error = new AError();
                        error.setCode(100);
                        error.setMsg("setPropertyFailed.");
                        itResResponseCallback.onComplete(identify, new ErrorInfo(error), null);
                    }

                } else if ("get".equals(identify)) {
                    //  初始化的时候将默认值初始化传进来，物模型内部会直接返回云端缓存的值

                } else {
                    // 根据不同的服务做不同的处理，跟具体的服务有关系
//                    OutputParams outputParams = new OutputParams();
//                    outputParams.put("Status", new ValueWrapper.IntValueWrapper(20));
//                    itResResponseCallback.onComplete(identify,null, outputParams);
                    // TODO replace   用户需要修改 这里默认返回空
//                    itResResponseCallback.onComplete(identify, null, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast("TMP 返回数据格式异常");
            }
        }

        @Override
        public void onSuccess(Object o, OutputParams outputParams) {
            Log.d(TAG, "onSuccess() called with: o = [" + o + "], outputParams = [" + outputParams + "]");
            showToast("注册服务成功");
            log("下行控制指令监听器注册成功");
        }

        @Override
        public void onFail(Object o, ErrorInfo errorInfo) {
            Log.d(TAG, "onFail() called with: o = [" + o + "], errorInfo = [" + errorInfo + "]");
            showToast("注册服务失败");
            log("下行控制指令监听器注册失败");
        }
    };

    private void log(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ALog.d(TAG, "log(), " + str);
                if (TextUtils.isEmpty(str))
                    return;
                consoleStr = consoleStr + "\n \n" + (getTime()) + " " + str;
                consoleTV.setText(consoleStr);
            }
        });

    }

    private void clearMsg() {
        consoleStr = "";
        consoleTV.setText(consoleStr);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHandler != null) {
            mHandler.removeMessages(REPORT_MSG);
            mHandler.removeCallbacksAndMessages(null);
            showToast("停止定时上报");
        }
        clearMsg();
    }

    private class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg == null) {
                return;
            }
            int what = msg.what;
            switch (what) {
                case REPORT_MSG:
                    reportHelloWorld();
                    mHandler.sendEmptyMessageDelayed(REPORT_MSG, 5*1000);
                    break;
            }

        }
    }

}

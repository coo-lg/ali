package com.utrons.ulink.ali.device;

import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.log.ALog;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceFilterHelper {
    public static void filter(String productKey, String deviceName, final DeviceFilterCallBack callback) {
        List<Map<String, String>> devices = new ArrayList<>();
        Map<String, String> device = new HashMap<>(2);
        device.put("productKey", productKey);
        device.put("deviceName", deviceName);
        devices.add(device);

        IoTRequest request = new IoTRequestBuilder()
                .setPath("/awss/enrollee/product/filter")
                .setApiVersion("1.0.2")
                .addParam("iotDevices", devices)
                .setAuthType("iotAuth")
                .build();

        new IoTAPIClientFactory().getClient().send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                if (200 != ioTResponse.getCode()) {
                    return;
                }

                if (!(ioTResponse.getData() instanceof JSONArray)) {
                    return;
                }

                JSONArray items = (JSONArray) ioTResponse.getData();
                //有返回数据，表示服务端支持此pk，dn
                if (null != items && items.length() > 0) {
                    callback.onExist();
                }else {
                    callback.onNotExist();
                }
            }
        });
    }
}

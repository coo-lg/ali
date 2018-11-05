package com.utrons.ulink.ali;

import com.aliyun.alink.linksdk.tmp.TmpSdk;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;

import java.util.HashMap;
import java.util.Map;

public class IoTAPIClientHelper {
    public static void send(String path, String version, Map<String, Object> params, IoTCallback callback) {
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath(path)
                .setScheme(Scheme.HTTPS)
                .setApiVersion(version)
                .setAuthType("iotAuth")
                .setParams(params);

        IoTRequest request = builder.build();
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, callback);
    }

    public static void networkChanged() {
        TmpSdk.getDeviceManager().discoverDevices(null,5000,null); // 设备模型
    }
}

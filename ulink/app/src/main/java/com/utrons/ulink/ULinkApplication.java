package com.utrons.ulink;

import com.aliyun.iot.aep.sdk.IoTSmart;
import com.aliyun.iot.aep.sdk.framework.AApplication;

public class ULinkApplication extends AApplication {
    private static final String TAG = "ULINK";

    @Override
    public void onCreate() {
        super.onCreate();
        IoTSmart.init(this); //初始化，App须继承自AApplication，否则会报错
    }
}

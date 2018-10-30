package com.utrons.ulink.page.device;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.alink.business.devicecenter.api.add.DeviceInfo;
import com.aliyun.alink.business.devicecenter.api.discovery.IDiscoveryListener;
import com.aliyun.alink.business.devicecenter.api.discovery.IOnDeviceTokenGetListener;
import com.aliyun.alink.business.devicecenter.api.discovery.LocalDeviceMgr;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.utrons.ulink.R;
import com.utrons.ulink.ali.IoTAPIClientHelper;
import com.utrons.ulink.ali.device.DeviceFilterCallBack;
import com.utrons.ulink.ali.device.DeviceFilterHelper;
import com.utrons.ulink.page.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddDeviceActivity extends BaseActivity {

    private LinearLayout mLayoutFoundDevice;

    private Handler mHandler = new Handler();
    private List<String> mBoundDevices; // 已绑定的设备信息
    private ArrayList<DeviceInfo> mFoundDevices = new ArrayList<>(); // 已配置设备
    private ArrayList<DeviceInfo> mFoundEnrolleeDevices = new ArrayList<>(); // 待配网设备

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            this.mBoundDevices = bundle.getStringArrayList("boundDevices");
        }

        setContentView(R.layout.activity_add_device);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mFoundDevices.clear();
        this.mFoundEnrolleeDevices.clear();

        LocalDeviceMgr.getInstance().startDiscovery(getApplicationContext(), new IDiscoveryListener() {
            @Override
            public void onLocalDeviceFound(DeviceInfo device) {// 已配网设备
                if (isDeviceBound(device.productKey + device.deviceName)) {
                    return;
                }

                DeviceFilterHelper.filter(device.productKey, device.deviceName, new DeviceFilterCallBack() {
                    @Override
                    public void onExist() {
                        mFoundDevices.add(device);
                        fillFoundDevices();
                    }

                    @Override
                    public void onNotExist() {
                        fillFoundDevices();
                    }
                });
            }

            @Override
            public void onEnrolleeDeviceFound(List<DeviceInfo> list) {//待配网设备
                mLayoutFoundDevice.removeAllViews();
                for (DeviceInfo device : list) {
                    if (isDeviceBound(device.productKey + device.deviceName)) {
                        continue;
                    }

                    DeviceFilterHelper.filter(device.productKey, device.deviceName, new DeviceFilterCallBack() {
                        @Override
                        public void onExist() {
                            mFoundEnrolleeDevices.add(device);
                            fillFoundDevices();
                        }

                        @Override
                        public void onNotExist() {
                            fillFoundDevices();
                        }
                    });
                }
            }
        });

    }

    private void initView() {
        this.mLayoutFoundDevice = findViewById(R.id.layout_found_device);
    }

    /**
     * 检查指定设备是否已经被绑定
     * @param deviceId 由productKey+deviceName组成
     * @return
     */
    private boolean isDeviceBound(String deviceId){
        if (null == this.mBoundDevices){
            return false;
        }

        return this.mBoundDevices.contains(deviceId) ? true : false;
    }

    private void fillFoundDevices() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                mLayoutFoundDevice.removeAllViews();
                for (DeviceInfo device : mFoundDevices) {
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.fragment_found_device, null);
                    TextView tvDeviceName = view.findViewById(R.id.tv_device_name);
                    tvDeviceName.setText("已配网" + device.deviceName);
                    Button btnBindDevice = view.findViewById(R.id.btn_bind_device);

                    final String productKey = device.productKey;
                    final String deviceName = device.deviceName;
                    btnBindDevice.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bindWithWifi(productKey, deviceName);
                        }
                    });
                    mLayoutFoundDevice.addView(view);
                }

                for (DeviceInfo device : mFoundEnrolleeDevices) {
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.fragment_found_device, null);
                    TextView tvDeviceName = (TextView) view.findViewById(R.id.tv_device_name);
                    tvDeviceName.setText("待配网" + device.deviceName);
                    mLayoutFoundDevice.addView(view);
                }
            }
        });
    }

    private void bindWithWifi(final String productKey, final String deviceName) {
        LocalDeviceMgr.getInstance().getDeviceToken(productKey, deviceName, 2*1000, new IOnDeviceTokenGetListener() {
            @Override
            public void onSuccess(String token) {
                Map<String, Object> params = new HashMap<>();
                params.put("productKey", productKey);
                params.put("deviceName", deviceName);
                params.put("token", token);
                IoTAPIClientHelper.send("/awss/enrollee/user/bind", "1.0.2", params, new IoTCallback() {
                    @Override
                    public void onFailure(IoTRequest ioTRequest, Exception e) {
                        Toast.makeText(getApplicationContext(), "绑定失败: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                        final int code = ioTResponse.getCode();
                        final String localizeMsg = ioTResponse.getLocalizedMsg();
                        if (code != 200) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AddDeviceActivity.this, localizeMsg, Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                DeviceInfo dev = null;
                                for (DeviceInfo device : mFoundDevices) {
                                    if (device.productKey.equals(productKey) && device.deviceName.equals(deviceName)) {
                                        dev = device;
                                    }
                                }
                                if (null != dev) {
                                    mFoundDevices.remove(dev);
                                }
                                fillFoundDevices();
                                Toast.makeText(AddDeviceActivity.this, "绑定成功: ", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onFail(final String s) {
                Toast.makeText(getApplicationContext(), "绑定失败: " + s, Toast.LENGTH_LONG).show();
            }
        });
    }
}

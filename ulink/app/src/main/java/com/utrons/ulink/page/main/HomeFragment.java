package com.utrons.ulink.page.main;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.utrons.ulink.R;
import com.utrons.ulink.ali.AccountDevDTO;
import com.utrons.ulink.ali.IoTAPIClientHelper;
import com.utrons.ulink.page.device.AddDeviceActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private ImageButton mIbtnAddDevice;
    private LinearLayout mLayoutMyDevices;

    private Handler mHandler = new Handler();
    private Bundle mBundle = new Bundle();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

        this.mIbtnAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddDeviceActivity.class);
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.layout_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginBusiness.isLogin()) {
                    initBoundDevices();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (LoginBusiness.isLogin()) {
            initBoundDevices();
        }
    }

    private void initView(View view) {
        this.mIbtnAddDevice = view.findViewById(R.id.ibtn_add_device);
        this.mLayoutMyDevices = view.findViewById(R.id.layout_my_devices);
    }

    private void initBoundDevices() {
        Map<String, Object> params = new HashMap<>();
        IoTAPIClientHelper.send("/uc/listBindingByAccount", "1.0.2", params, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                ALog.d(TAG, "onFailure");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                final int code = response.getCode();
                if (code != 200) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), response.getLocalizedMsg(), Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "刷新成功 ", Toast.LENGTH_SHORT).show();
                    }
                });

                Object data = response.getData();
                if (null == data) {
                    return;
                }
                if (!(data instanceof JSONObject)) {
                    return;
                }
                try {
                    JSONObject jsonObject = (JSONObject) data;
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    final List<AccountDevDTO> devices = JSON.parseArray(jsonArray.toString(), AccountDevDTO.class);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mLayoutMyDevices.removeAllViews();
                            if (null == devices) {
                                return;
                            }

                            String status = null;
                            for (AccountDevDTO device : devices) {
                                LayoutInflater inflater = getLayoutInflater();
                                View view = inflater.inflate(R.layout.fragment_my_devices, null);
                                TextView tvDeviceName = (TextView) view.findViewById(R.id.tv_device_name);
                                TextView tvDeviceStatus = (TextView) view.findViewById(R.id.tv_device_status);

                                switch(device.getStatus()) {
                                    case 0:
                                        status = "未激活";
                                        view.setBackgroundColor(getResources().getColor(R.color.possible_result_points));
                                        break;
                                    case 1:
                                        status = "在线";
                                        view.setBackgroundColor(getResources().getColor(R.color.color_1FC88B));
                                        break;
                                    case 3:
                                        status = "离线";
                                        view.setBackgroundColor(getResources().getColor(R.color.color_CCCCCC));
                                        break;
                                    case 8:
                                        status = "禁用";
                                        view.setBackgroundColor(getResources().getColor(R.color.color_CCCCCC));
                                        break;
                                    default:
                                        status = "未知";
                                        view.setBackgroundColor(getResources().getColor(R.color.possible_result_points));
                                }
                                tvDeviceStatus.setTextColor(getResources().getColor(R.color.contents_text));
                                tvDeviceStatus.setText(status);
                                tvDeviceName.setText(device.getDeviceName());

                                final String productKey = device.getProductKey();
                                final String iotId = device.getIotId();
                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String code = "link://router/" + productKey;
                                        Bundle bundle = new Bundle();
                                        bundle.putString("iotId", iotId); // 传入插件参数，没有参数则不需要这一行
                                        Router.getInstance().toUrlForResult(getActivity(), code, 1, bundle);
                                    }
                                });
                                mLayoutMyDevices.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
                            }
                        }
                    });
                    if (null == devices) {
                        return;
                    }
                    //注册虚拟设备
                    ArrayList<String> deviceStrList = new ArrayList<>();
                    for (AccountDevDTO deviceInfoBean : devices) {
                        deviceStrList.add(deviceInfoBean.getProductKey() + deviceInfoBean.getDeviceName());
                    }
                    mBundle.putStringArrayList("boundDevices", deviceStrList);
                } catch (Exception e) {
                    Log.d(TAG,e.getLocalizedMessage());
                }
            }
        });
    }

}

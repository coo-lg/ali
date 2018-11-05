package com.aliyun.alink.devicesdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.aliyun.alink.devicesdk.app.DemoApplication;


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

public class DemoActivity extends BaseActivity {
    private static final String TAG = "DemoActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void startLPTest(View view) {
        if (!DemoApplication.isInitDone){
            showToast("初始化尚未成功，请稍后点击");
            return;
        }
        Intent intent = new Intent(this, ControlPannelActivity.class);
        startActivity(intent);
    }

    public void startLabelTest(View view) {
        if (!DemoApplication.isInitDone){
            showToast("初始化尚未成功，请稍后点击");
            return;
        }
        Intent intent = new Intent(this, LabelActivity.class);
        startActivity(intent);
    }

    public void startCOTATest(View view) {
        if (!DemoApplication.isInitDone){
            showToast("初始化尚未成功，请稍后点击");
            return;
        }
        Intent intent = new Intent(this, COTAActivity.class);
        startActivity(intent);
    }

    public void startLightExample(View view) {
        if (!DemoApplication.isInitDone){
            showToast("初始化尚未成功，请稍后点击");
            return;
        }
        showToast("该示例只适用于快速接入，真实产品无对应的属性，真实产品使用可进一步参考 LP通用示例");
        Intent intent = new Intent(this, LightExampleActivity.class);
        startActivity(intent);
    }

    public void startShadowTest(View view) {
        if (!DemoApplication.isInitDone){
            showToast("初始化尚未成功，请稍后点击");
            return;
        }
        showToast("基础版设备影子功能，高级版不适用，需要设备的三元组是基础版才可以测试使用");
        Intent intent = new Intent(this, ShadowActivity.class);
        startActivity(intent);
    }

    public void startGatewayTest(View view) {
        if (!DemoApplication.isInitDone){
            showToast("初始化尚未成功，请稍后点击");
            return;
        }
        Intent intent = new Intent(this, GatewayActivity.class);
        startActivity(intent);
    }
}

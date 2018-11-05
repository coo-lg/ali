package com.utrons.ulink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.utrons.ulink.page.BaseActivity;
import com.utrons.ulink.page.main.MainActivity;

public class StartActivity extends BaseActivity {
    private Handler handle = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (LoginBusiness.isLogin()) {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            LoginBusiness.login(new ILoginCallback() {
                @Override
                public void onLoginSuccess() {
                    handle.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(StartActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                        }
                    }, 0);
                }

                @Override
                public void onLoginFailed(int code, String error) {
                    Toast.makeText(getApplicationContext(), String.format("登录失败[%s: %s]", code, error), Toast.LENGTH_LONG).show();
                }
            });
            finish();
        }
    }

}

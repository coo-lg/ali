package com.aliyun.alink.devicesdk.demo;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aliyun.alink.devicesdk.app.DeviceInfoData;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class TriadSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UDEVICE";
    private EditText etProductKey;
    private EditText etProductSecret;
    private EditText etDeviceName;
    private EditText etDeviceSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triad_setting);

        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);
        Button btnClear = findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(this);

        this.etProductKey = findViewById(R.id.et_productKey);
        this.etProductSecret = findViewById(R.id.et_ProductSecret);
        this.etDeviceName = findViewById(R.id.et_DeviceName);
        this.etDeviceSecret = findViewById(R.id.et_Device_Secret);

        this.readTraid();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                writeTraid();
                break;
            case R.id.btn_clear:
                clear();
                break;
        }
    }

    private void writeTraid() {
        String productKey = this.etProductKey.getText().toString().trim();
        String productSecret = this.etProductSecret.getText().toString().trim();
        String deviceName = this.etDeviceName.getText().toString().trim();
        String deviceSecret = this.etDeviceSecret.getText().toString().trim();

        try {
            SharedPreferences userSettings = getSharedPreferences("setting", 0);
            SharedPreferences.Editor editor = userSettings.edit();
            editor.putString("ProductKey", productKey);
            editor.putString("ProductSecret", productSecret);
            editor.putString("DeviceName", deviceName);
            editor.putString("DeviceSecret", deviceSecret);
            editor.commit();

            Toast.makeText(getApplicationContext(), "修改成功，请重新打开APP以使配置生效", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "getDeviceInfoFrom: e", e);
        }
    }

    private void clear() {
        this.etProductKey.setText("");
        this.etProductSecret.setText("");
        this.etDeviceName.setText("");
        this.etDeviceSecret.setText("");
    }

    private void readTraid() {
        SharedPreferences settings = getSharedPreferences("setting", 0);
        String productKey = settings.getString("ProductKey","");
        String productSecret = settings.getString("ProductSecret","");
        String deviceName = settings.getString("DeviceName","");
        String deviceSecret = settings.getString("DeviceSecret","");

        this.etProductKey.setText(productKey);
        this.etProductSecret.setText(productSecret);
        this.etDeviceName.setText(deviceName);
        this.etDeviceSecret.setText(deviceSecret);
    }
}

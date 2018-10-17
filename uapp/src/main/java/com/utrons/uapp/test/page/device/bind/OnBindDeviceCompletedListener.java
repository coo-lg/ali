package com.utrons.uapp.test.page.device.bind;

public interface OnBindDeviceCompletedListener {

    void onSuccess(String iotId);

    void onFailed(Exception e);

    void onFailed(int code, String message, String localizedMsg);
}

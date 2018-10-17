package com.utrons.uapp.test.page.ota.executor;

import com.utrons.uapp.test.page.ota.business.listener.IOTAQueryStatusCallback;
import com.utrons.uapp.test.page.ota.business.listener.IOTAStartUpgradeCallback;
import com.utrons.uapp.test.page.ota.business.listener.IOTAStopUpgradeCallback;
import com.utrons.uapp.test.page.ota.interfaces.IOTAExecutor;
import com.utrons.uapp.test.page.ota.interfaces.IOTAStatusChangeListener;

/**
 * Created by david on 2018/4/13.
 *
 * @author david
 * @date 2018/04/13
 *
 * 蓝牙 ota
 */
public class BreezeOTAExecutor implements IOTAExecutor {
    public BreezeOTAExecutor(IOTAStatusChangeListener listener) {

    }

    @Override
    public void queryOTAStatus(String iotId, IOTAQueryStatusCallback callback) {

    }

    @Override
    public void startUpgrade(String iotId, IOTAStartUpgradeCallback callback) {

    }

    @Override
    public void stopUpgrade(String iotId, IOTAStopUpgradeCallback callback) {

    }

    @Override
    public void destroy() {

    }
}

package com.utrons.ulink;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.alibaba.wireless.security.jaq.JAQException;
import com.alibaba.wireless.security.jaq.SecurityInit;
import com.aliyun.alink.alirn.RNGlobalConfig;
import com.aliyun.alink.business.devicecenter.extbone.BoneAddDeviceBiz;
import com.aliyun.alink.business.devicecenter.extbone.BoneHotspotHelper;
import com.aliyun.alink.business.devicecenter.extbone.BoneLocalDeviceMgr;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileConnectListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectConfig;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectState;
import com.aliyun.alink.linksdk.tmp.TmpSdk;
import com.aliyun.alink.linksdk.tmp.api.TmpInitConfig;
import com.aliyun.alink.linksdk.tmp.extbone.BoneThing;
import com.aliyun.alink.linksdk.tools.ALog;
import com.aliyun.alink.page.rn.InitializationHelper;
import com.aliyun.alink.sdk.jsbridge.BonePluginRegistry;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientImpl;
import com.aliyun.iot.aep.sdk.apiclient.adapter.APIGatewayHttpAdapterImpl;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Env;
import com.aliyun.iot.aep.sdk.apiclient.hook.IoTAuthProvider;
import com.aliyun.iot.aep.sdk.connectchannel.BoneChannel;
import com.aliyun.iot.aep.sdk.credential.IoTCredentialProviderImpl;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.oa.OALoginAdapter;
import com.aliyun.iot.aep.sdk.login.plugin.BoneUserAccountPlugin;
import com.facebook.react.FrescoPackage;
import com.utrons.ulink.ali.IoTAPIClientHelper;
import com.utrons.ulink.config.EnvConfig;

public class ULinkApplication extends MultiDexApplication {
    private static final String TAG = "ULINK";

    @Override
    public void onCreate() {
        super.onCreate();
        initBaseSDK();
        initSDK();
    }

    private void initBaseSDK() {
        String appKey = APIGatewayHttpAdapterImpl.getAppKey(this,  "114d");
        EnvConfig.ENV.put(EnvConfig.KEY_APPKEY, appKey);
    }

    private void initSDK() {
        initAPISDK();
        initAccountSDK();
        initAuthSDK();
        initConnectSDK();
        initThingModelSDK();
        initBoneMobileSDK();
    }

    private void initAPISDK() {
        // 初始化无线保镖
        try {
            SecurityInit.Initialize(this);
        } catch (JAQException ex) {
            Log.e(TAG, "security-sdk-initialize-failed");
        } catch (Exception ex) {
            Log.e(TAG, "security-sdk-initialize-failed");
        }

        // 初始化 IoTAPIClient
        IoTAPIClientImpl.InitializeConfig config = new IoTAPIClientImpl.InitializeConfig();
        // 国内环境
        config.host = "api.link.aliyun.com";
        // 海外环境，请参考如下设置
        //config.host = “api-iot.ap-southeast-1.aliyuncs.com”;
        config.apiEnv = Env.RELEASE; //只支持RELEASE

        IoTAPIClientImpl impl = IoTAPIClientImpl.getInstance();
        impl.init(this, config);
    }

    private void initAccountSDK() {
        //如果com.aliyun.iot.aep.sdk:account 为0.0.2以及以上，请使用如下代码完成初始化
        OALoginAdapter adapter = new OALoginAdapter(this); // 内置账号

        //UOALoginAdapter adapter = new UOALoginAdapter(this); // 自有账号
        //如果需要切换到海外环境，请执行下面setDefaultOAHost方法，默认为大陆环境
        //adapter.setDefaultOAHost("sgp-sdk.openaccount.aliyun.com");

        //        adapter.setDefaultOAHost(host);
        //        adapter.setDefaultLoginClass(OALoginActivity.class);
        adapter.init("online", "114d");
        LoginBusiness.init(this, adapter, "online");
    }

    private void initAuthSDK() {
        //务必注意在调用之前，保证完成了用户和账号SDK的初始化
        IoTCredentialManageImpl.init(EnvConfig.ENV.get(EnvConfig.KEY_APPKEY));

        // 初始化IoTCredentialProviderImpl模块
        IoTAuthProvider provider = new IoTCredentialProviderImpl(IoTCredentialManageImpl.getInstance(this));
        IoTAPIClientImpl.getInstance().registerIoTAuthProvider("iotAuth", provider);
    }

    private void initBoneMobileSDK() {
        String serverEnv = "production";//仅支持"production",即生产环境
        String pluginEnv = "release";//仅支持“release”
        String language = "zh-CN";//语言环境，目前仅支持“zh-CN”，“en-US”

        // 初始化 BoneMobile RN 容器
        InitializationHelper.initialize(this, pluginEnv, serverEnv, language);
        // 添加基于 Fresco 的图片组件支持
        RNGlobalConfig.addBizPackage(new FrescoPackage());

        // 集成账号能力
        BonePluginRegistry.register(BoneUserAccountPlugin.API_NAME, BoneUserAccountPlugin.class);

        // 集成配网能力
        BonePluginRegistry.register("BoneAddDeviceBiz",BoneAddDeviceBiz.class);
        BonePluginRegistry.register("BoneLocalDeviceMgr",BoneLocalDeviceMgr.class);
        BonePluginRegistry.register("BoneHotspotHelper",BoneHotspotHelper.class);
        // 集成设备模型能力
        BonePluginRegistry.register("BoneThing", BoneThing.class);

        // 集成长连接能力
        BonePluginRegistry.register("BoneChannel", BoneChannel.class);
    }

    private void initConnectSDK() {
        //打开Log 输出
        ALog.setLevel(ALog.LEVEL_DEBUG);

        MobileConnectConfig config = new MobileConnectConfig();
        // 设置 appKey 和 authCode(必填)
        config.appkey = EnvConfig.ENV.get(EnvConfig.KEY_APPKEY);;
        config.securityGuardAuthcode = "114d";


        // 设置验证服务器（默认不填，SDK会自动使用“API通道SDK“的Host设定）
        // config.authServer = "";

        // 指定长连接服务器地址。 （默认不填，SDK会使用默认的地址及端口。默认为国内华东节点。）
        // config.channelHost = "{长连接服务器域名}";

        // 开启动态选择Host功能。 (默认false，海外环境建议设置为true。此功能前提为ChannelHost 不特殊指定。）
        config.autoSelectChannelHost = false;

        MobileChannel.getInstance().startConnect(this, config, new IMobileConnectListener() {
            @Override
            public void onConnectStateChange(MobileConnectState state) {
                ALog.d(TAG,"onConnectStateChange(), state = "+state.toString());
            }
        });
    }

    private void initThingModelSDK() { // 设备模型SDK
        TmpSdk.init(getBaseContext(), new TmpInitConfig(TmpInitConfig.ONLINE));
        IoTAPIClientHelper.networkChanged();
    }
}

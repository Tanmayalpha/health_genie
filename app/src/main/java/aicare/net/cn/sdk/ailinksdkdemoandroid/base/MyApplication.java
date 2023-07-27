package aicare.net.cn.sdk.ailinksdkdemoandroid.base;

import android.app.Application;

import com.pingwang.bluetoothlib.AILinkSDK;

public class MyApplication extends Application {

    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        AILinkSDK.getInstance().init(this);
    }

    public static MyApplication getInstance(){
        return sInstance;
    }
}

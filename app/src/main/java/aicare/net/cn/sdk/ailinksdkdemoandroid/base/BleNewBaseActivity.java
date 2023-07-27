package aicare.net.cn.sdk.ailinksdkdemoandroid.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.pingwang.bluetoothlib.AILinkBleManager;
import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.utils.BleLog;
import aicare.net.cn.sdk.ailinksdkdemoandroid.BuildConfig;
import aicare.net.cn.sdk.ailinksdkdemoandroid.R;

/**
 * xing<br>
 * 2019/4/25<br>
 * 显示数据
 */
public abstract class BleNewBaseActivity extends AppCompatActivity {

    private static String TAG = BleNewBaseActivity.class.getName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.setTitle(getString(R.string.app_name) + BuildConfig.VERSION_NAME);
        init();
    }

    private void init() {
        bindService();

    }

    /**
     * 搜索蓝牙(默认通过uuid过滤)
     * @param timeOut 超时,小于等于0代表永不超时
     */
    protected void startScanBle(long timeOut){
        if (AILinkBleManager.getInstance()!=null){
            AILinkBleManager.getInstance().startScan(timeOut,BleConfig.UUID_SERVER_AILINK);
        }
    }

    /**
     * 主动停止搜索
     */
    protected void stopScanBle(){
        if (AILinkBleManager.getInstance()!=null){
            AILinkBleManager.getInstance().stopScan();
        }
    }




    /**
     * 连接设备
     * @param bleValueBean 搜索到的地址
     */
    protected void connectBle(BleValueBean bleValueBean){
        if (AILinkBleManager.getInstance()!=null){
            AILinkBleManager.getInstance().stopScan();
            AILinkBleManager.getInstance().connectDevice(bleValueBean.getMac());
        }
    }

    /**
     * 连接设备
     * @param mac 设备的地址
     */
    protected void connectBle(String mac){
        if (AILinkBleManager.getInstance()!=null){
            AILinkBleManager.getInstance().stopScan();
            AILinkBleManager.getInstance().connectDevice(mac);
        }
    }

    //---------------------------------服务---------------------------------------------------

    private void bindService() {
        if (!AILinkBleManager.getInstance().isInitOk()) {
            AILinkBleManager.getInstance().init(this, new AILinkBleManager.onInitListener() {
                @Override
                public void onInitSuccess() {
                    onServiceSuccess();
                }

                @Override
                public void onInitFailure() {
                    onServiceErr();
                }
            });
        }else {
            onServiceSuccess();
        }

    }

    private void unbindService() {
        unbindServices();
    }


    /**
     * 绑定服务成功
     */
    public abstract void onServiceSuccess();

    /**
     * 绑定服务失败
     */
    public abstract void onServiceErr();

    /**
     * 解绑服务,去掉与服务相关的操作,接口等
     */
    public abstract void unbindServices();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleLog.i(TAG, "onDestroy");
        unbindService();
    }
}

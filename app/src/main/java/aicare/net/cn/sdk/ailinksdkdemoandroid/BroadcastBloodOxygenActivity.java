package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.listener.OnCallbackDis;
import com.pingwang.bluetoothlib.listener.OnScanFilterListener;
import com.pingwang.bluetoothlib.utils.BleDensityUtil;
import com.pingwang.bluetoothlib.utils.BleLog;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.BloodOxygen.BroadcastBloodOxygenBleConfig;
import cn.net.aicare.modulelibrary.module.BloodOxygen.BroadcastBloodOxygenDeviceData;


/**
 * xing<br>
 * 2020/09/09<br>
 * 广播血氧仪
 */
public class BroadcastBloodOxygenActivity extends BleBaseActivity implements OnCallbackDis, BroadcastBloodOxygenDeviceData.onNotifyData, OnScanFilterListener, View.OnClickListener {

    private static String TAG = BroadcastBloodOxygenActivity.class.getName();
    private final int REFRESH_DATA = 3;

    private TextView tv_broadcast_blood_oxygen, tv_broadcast_mac;

    private List<String> mList;
    private ArrayAdapter listAdapter;
    private Context mContext;
    private BroadcastBloodOxygenDeviceData mDevice;
    private String mAddress = "";

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {


                case REFRESH_DATA:
                    if (listAdapter != null)
                        listAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_blood_oxygen);
        mContext = this;
        init();

    }

    private void init() {
        mList = new ArrayList<>();
        ListView listView = findViewById(R.id.listview);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        listView.setAdapter(listAdapter);

        findViewById(R.id.clear).setOnClickListener(this);
        findViewById(R.id.open).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);

        tv_broadcast_blood_oxygen = findViewById(R.id.tv_broadcast_blood_oxygen);

        tv_broadcast_mac = findViewById(R.id.tv_broadcast_mac);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear:
                if (mList != null)
                    mList.clear();
                mHandler.sendEmptyMessage(REFRESH_DATA);
                break;
            case R.id.open:
                if (mBluetoothService != null) {
                    mBluetoothService.scanLeDevice(0, UUID.fromString("0000F0A0-0000-1000-8000-00805F9B34FB"));
                }
                break;
            case R.id.stop:
                if (mBluetoothService != null) {
                    mBluetoothService.stopScan();
                }
                break;
        }
    }


    //---------------------------------服务---------------------------------------------------


    @Override
    public void onServiceSuccess() {
        BleLog.i(TAG, "服务与界面建立连接成功");
        //与服务建立连接
        if (mBluetoothService != null) {
            mDevice = BroadcastBloodOxygenDeviceData.getInstance();
            mDevice.setOnNotifyData(this);
            mBluetoothService.setOnScanFilterListener(this);
            mBluetoothService.scanLeDevice(0, BleConfig.UUID_SERVER_BROADCAST_AILINK);
        }
    }

    @Override
    public void onServiceErr() {
        BleLog.i(TAG, "服务与界面连接断开");
        //与服务断开连接
        mBluetoothService = null;
    }

    @Override
    public void unbindServices() {
        if (mDevice != null) {
            mDevice.clear();
            mDevice = null;
        }

    }

    //-----------------状态-------------------


    @Override
    public void bleOpen() {
        mList.add(TimeUtils.getTime() + "蓝牙打开");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void bleClose() {
        BleLog.i(TAG, "蓝牙未开启,可请求开启");
        mList.add(TimeUtils.getTime() + "蓝牙关闭");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    //-----------------通知-------------------


    @Override
    public void getBloodOxygenData(int status, int bloodOxygen, int pulseRate, int plethysmogram, int power) {
        String statusStr = "状态=";

        switch (status) {
            case BroadcastBloodOxygenBleConfig.START_TESTING:
                statusStr += "开始测试";
                break;
            case BroadcastBloodOxygenBleConfig.TESTING:
                statusStr += "正在测试";
                break;

            case BroadcastBloodOxygenBleConfig.STOP_TESTING:
                statusStr += "测试完成";
                break;

        }

        String bloodOxygenStr = "--%";
        if (bloodOxygen != 0xFF) {
            bloodOxygenStr = bloodOxygen + "%";
        }
        String pulseRateStr = "--%";
        if (pulseRate != 0xFF) {
            pulseRateStr = pulseRate + "%";
        }
        String plethysmogramStr = BleDensityUtil.getInstance().holdDecimals(plethysmogram, 1);
        String showData = TimeUtils.getTime();
        showData += statusStr;
        showData += "\n血氧=" + bloodOxygenStr + ";脉率=" + pulseRateStr + ";PI=" + plethysmogramStr + ";电量=" + power + "%";

        tv_broadcast_blood_oxygen.setText(bloodOxygenStr);

        mList.add(showData);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void OnDID(int cid, int vid, int pid) {
        String didStr = "cid:" + cid + "||vid:" + vid + "||pid:" + pid;
        mList.add(TimeUtils.getTime() + "ID:" + didStr);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleLog.i(TAG, "onDestroy");
    }


    @Override
    public boolean onFilter(BleValueBean bleValueBean) {
        return true;
    }

    @Override
    public void onScanRecord(BleValueBean bleValueBean) {
        if (TextUtils.isEmpty(mAddress) && bleValueBean.isBroadcastModule()) {
            mAddress = bleValueBean.getMac();
            if (tv_broadcast_mac != null) {
                tv_broadcast_mac.setText(mAddress);
            }
        }
        //地址相同,并且是广播秤
        if (mAddress.equalsIgnoreCase(bleValueBean.getMac()) && bleValueBean.isBroadcastModule()) {
            byte[] manufacturerData = bleValueBean.getManufacturerData();
            int cid = bleValueBean.getCid();
            int vid = bleValueBean.getVid();
            int pid = bleValueBean.getPid();
            if (mDevice != null)
                mDevice.onNotifyData(manufacturerData, cid, vid, pid);
        }
    }


}

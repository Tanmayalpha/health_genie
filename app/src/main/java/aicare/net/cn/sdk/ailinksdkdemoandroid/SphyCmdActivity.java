package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.pingwang.bluetoothlib.BleBaseActivity;
import com.pingwang.bluetoothlib.config.CmdConfig;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.listener.CallbackDisIm;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnCallbackDis;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleDensityUtil;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.sphygmomanometer.SphyBleConfig;
import cn.net.aicare.modulelibrary.module.sphygmomanometer.SphyDeviceData;


/**
 * xing<br>
 * 2019/4/25<br>
 * 血压计
 */
public class SphyCmdActivity extends BleBaseActivity implements OnCallbackDis, OnBleVersionListener
        , OnMcuParameterListener, OnBleCompanyListener, View.OnClickListener {

    private static String TAG = SphyCmdActivity.class.getName();
    private final int REFRESH_DATA = 3;
    private List<String> mList;
    private ArrayAdapter listAdapter;

    private Context mContext;
    private SphyDeviceData mBleDevice;
    private String mAddress;
    private BleSendCmdUtil mBleSendCmdUtil;
    private int type;
    private byte unit = 0;
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
        setContentView(R.layout.activity_sphy);
        mContext = this;
        mAddress = getIntent().getStringExtra("mac");
        type = getIntent().getIntExtra("type", -1);
        mBleSendCmdUtil = BleSendCmdUtil.getInstance();
        init();
    }

    private void init() {
        mList = new ArrayList<>();
        ListView listView = findViewById(R.id.listview);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        listView.setAdapter(listAdapter);

        Button clear = findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList != null)
                    mList.clear();
                mHandler.sendEmptyMessage(REFRESH_DATA);
            }
        });


        findViewById(R.id.btnVersion).setOnClickListener(this);
        findViewById(R.id.btnBattery).setOnClickListener(this);
        findViewById(R.id.btn_get_did).setOnClickListener(this);
        findViewById(R.id.clear).setOnClickListener(this);

        cmdBtn();
    }


    private void cmdBtn() {
        Button btn_set_unit = findViewById(R.id.btn_set_unit);
        btn_set_unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unit == SphyBleConfig.SPHY_UNIT_MMHG) {
                    unit = SphyBleConfig.SPHY_UNIT_KPA;
                    mBleDevice.setUnit(unit);
                } else {
                    unit = SphyBleConfig.SPHY_UNIT_MMHG;
                    mBleDevice.setUnit(unit);
                }
            }
        });

        Button btn_start = findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBleDevice.startMeasuring();
            }
        });

        Button btn_stop = findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBleDevice.stopMeasuring();
            }
        });
    }


    @Override
    public void onClick(View v) {
        SendBleBean sendBleBean = new SendBleBean();
        switch (v.getId()) {
            case R.id.btnVersion:
                sendBleBean.setHex(mBleSendCmdUtil.getBleVersion());
                mBleDevice.sendData(sendBleBean);
                break;
            case R.id.btnBattery:
                sendBleBean.setHex(mBleSendCmdUtil.getMcuBatteryStatus());
                mBleDevice.sendData(sendBleBean);
                break;
            case R.id.btn_get_did:
                sendBleBean.setHex(mBleSendCmdUtil.getDid());
                mBleDevice.sendData(sendBleBean);
                break;

            case R.id.clear:
                if (mList != null)
                    mList.clear();
                mHandler.sendEmptyMessage(REFRESH_DATA);
                break;
        }
    }

    //---------------------------------服务---------------------------------------------------


    @Override
    public void onServiceSuccess() {
        CallbackDisIm.getInstance().addListListener(this);
        BleLog.i(TAG, "服务与界面建立连接成功");
        //与服务建立连接
        if (mBluetoothService != null) {
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                mBleDevice = SphyDeviceData.getInstance(bleDevice);
                mBleDevice.setOnNotifyData(new NotifyData());
                mBleDevice.setOnBleVersionListener(SphyCmdActivity.this);
                mBleDevice.setOnMcuParameterListener(SphyCmdActivity.this);
                mBleDevice.setOnCompanyListener(SphyCmdActivity.this);
            }
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
        CallbackDisIm.getInstance().removeListener(this);
        if (mBleDevice != null) {
            mBleDevice.disconnect();
            mBleDevice.clear();
            mBleDevice = null;
        }
    }


    //-----------------状态-------------------


    @Override
    public void onConnecting(@NonNull String mac) {
        //TODO 连接中
        if (mAddress.equals(mac))
            BleLog.i(TAG, "连接中");
    }

    @Override
    public void onDisConnected(@NonNull String mac, int code) {
        //TODO 连接断开
        if (mAddress.equals(mac)){
            BleLog.i(TAG, "连接断开");
            finish();
        }
    }

    @Override
    public void onServicesDiscovered(@NonNull String mac) {
        //TODO 连接成功(获取服务成功)
        if (mAddress.equals(mac))
            BleLog.i(TAG, "连接成功(获取服务成功)");
    }


    @Override
    public void bleOpen() {

    }

    @Override
    public void bleClose() {
        BleLog.i(TAG, "蓝牙未开启,可请求开启");
        finish();
    }


    //-----------------通知-------------------

    private class NotifyData implements SphyDeviceData.onNotifyData {
        @Override
        public void onData(byte[] status, int type) {
            String data = "";
            if (status != null)
                data = BleStrUtils.byte2HexStr(status);
            if (type == 100) {
                mList.add(TimeUtils.getTime() + "send->" + data);
            } else {
                mList.add(TimeUtils.getTime() + "notify->" + data);
            }
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void getSphyCmd(byte cmd) {
            mList.add(TimeUtils.getTime() + "cmd:" + cmd);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void sphyDataNow(int dia, int sys, int decimal, int pul, int unit) {
            String diaStr = BleDensityUtil.getInstance().holdDecimals(dia, decimal);
            String sysStr = BleDensityUtil.getInstance().holdDecimals(sys, decimal);
            mList.add(TimeUtils.getTime() + "current:dia=" + diaStr + " sys=" + sysStr + " pul=" + pul + " unit=" + unit);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void sphyData(int dia, int sys, int decimal, int pul, int unit) {
            String diaStr = BleDensityUtil.getInstance().holdDecimals(dia, decimal);
            String sysStr = BleDensityUtil.getInstance().holdDecimals(sys, decimal);
            mList.add(TimeUtils.getTime() + "result:dia=" + diaStr + " sys=" + sysStr + " pul=" + pul + " unit=" + unit);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void getUnit(int unit) {
            String showData = "";
            switch (unit) {
                case CmdConfig.SETTING_SUCCESS:
                    showData = getString(R.string.set_success);
                    break;
                case CmdConfig.SETTING_FAILURE:
                    showData = getString(R.string.set_failure);

                    break;
                case CmdConfig.SETTING_ERR:

                    showData = getString(R.string.set_err);
                    break;
            }
            mList.add(TimeUtils.getTime() + showData+" unit="+SphyCmdActivity.this.unit);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void getErr(byte status) {
            mList.add(TimeUtils.getTime() + "Err:" + status);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }
    }

    @Override
    public void onBmVersion(String version) {
        mList.add(TimeUtils.getTime() + "Version:" + version);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void OnDID(int cid, int vid, int pid) {
        String didStr = "cid:" + cid + "||vid:" + vid + "||pid:" + pid;
        mList.add(TimeUtils.getTime() + "ID:" + didStr);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onMcuBatteryStatus(int status, int battery) {
        mList.add(TimeUtils.getTime() + getString(R.string.power) + battery + "%");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onSysTime(int status, int[] times) {
        String time =
                times[0] + "-" + times[1] + "-" + times[2] + "  " + times[3] + ":" + times[4] +
                        ":" + times[5];
        mList.add(TimeUtils.getTime() + "system Time:" + time);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleLog.i(TAG, "onDestroy");
    }
}

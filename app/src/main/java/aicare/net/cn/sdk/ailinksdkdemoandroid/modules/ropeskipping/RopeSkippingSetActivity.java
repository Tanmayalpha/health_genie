package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.ropeskipping;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;

import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.OnBleDeviceDataListener;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;

/**
 * 跳绳设置模式
 */
public class RopeSkippingSetActivity extends BleBaseActivity implements View.OnClickListener, OnCallbackBle, OnBleDeviceDataListener {


    private final int CID = 0x002F;
    private List<String> logList;
    private ArrayAdapter mArrayAdapter;
    private ListView listviw;
    private EditText et_rssi, et_set_mode;
    private Set<String> mSet = new ArraySet<>();
    private String mAddress;
    private boolean isPauseLog = false;
    private int mMode = 3;
    private int mRssi = -60;
    private boolean mSetMode = true;
    private int mSetModeNumber=0;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {

                case 1:
                    refreshLog("正在设置下一个...");
                    if (mBluetoothService != null) {
                        mBluetoothService.startScan(0, BleConfig.UUID_BROADCAST_AILINK);
                    }
                    break;

            }
        }
    };

    @Override
    public void onServiceSuccess() {
        refreshLog("绑定服务成功");
        if (mBluetoothService != null) {
            mBluetoothService.setOnCallbackBle(this);

        }

    }

    @Override
    public void onServiceErr() {
        if (mArrayAdapter != null && logList != null) {
            refreshLog("绑定服务失败");
        }
    }

    @Override
    public void unbindServices() {
        if (mArrayAdapter != null && logList != null) {
            refreshLog("解除绑定服务");
        }
        if (mBluetoothService!=null) {
            mBluetoothService.removeOnCallbackBle(this);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rope_skipping_set_mode);
        listviw = findViewById(R.id.listview);
        et_rssi = findViewById(R.id.et_rssi);
        et_set_mode = findViewById(R.id.et_set_mode);


        findViewById(R.id.tv_test_mode).setOnClickListener(this);
        findViewById(R.id.btn_start_set).setOnClickListener(this);
        findViewById(R.id.btn_start_read).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);


        logList = new ArrayList<>();
        mArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, logList);
        listviw.setAdapter(mArrayAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_test_mode) {
//            提示模式
            String show = "1=随机2~8次/S\n2=2次/S\n3=3次/S\n4=4次/S\n5=5次/S";
            refreshLog(show);

        } else if (v.getId() == R.id.btn_start_set) {
            //开始设置
            refreshLog("开始设置...");
            mMode = Integer.parseInt(et_set_mode.getText().toString().trim());
            mRssi = Integer.parseInt(et_rssi.getText().toString().trim());
            et_set_mode.setEnabled(false);
            et_rssi.setEnabled(false);
            mSetMode = true;
            if (mBluetoothService != null) {
                mBluetoothService.startScan(0, BleConfig.UUID_BROADCAST_AILINK);
            }
        } else if (v.getId() == R.id.btn_start_read) {
            //开始读取
            mMode = Integer.parseInt(et_set_mode.getText().toString().trim());
            mRssi = Integer.parseInt(et_rssi.getText().toString().trim());
            mSetMode = false;

        } else if (v.getId() == R.id.btn_stop) {
            //停止
            et_set_mode.setEnabled(true);
            et_rssi.setEnabled(true);
            refreshLog("停止设置");
            mBluetoothService.stopScan();
            mBluetoothService.disconnectAll();

        }
    }


    private void refreshLog(String content) {
        if (!isPauseLog) {
            content=TimeUtils.getTime(System.currentTimeMillis())+content;
            logList.add( content);
            mArrayAdapter.notifyDataSetChanged();
        }
    }


    //-------------开始----------------------------

    private void setMode(BleDevice bleDevice) {
        byte[] cmd = new byte[2];
        cmd[0] = 0x40;
        cmd[1] = (byte) mMode;
        SendMcuBean sendMcuBean = new SendMcuBean();
        sendMcuBean.setHex(CID, cmd);
        bleDevice.sendData(sendMcuBean);
    }


    @Override
    public void onStartScan() {

    }

    @Override
    public void onScanning(BleValueBean data) {
        if (!mSet.contains(data.getMac()) && data.getCid() == 0x002F && Math.abs(mRssi) > Math.abs(data.getRssi())) {
            mBluetoothService.stopScan();
            mBluetoothService.connectDevice(data.getMac());
            refreshLog("正在连接:" + data.getMac());
        }

    }

    @Override
    public void onScanErr(long time) {
        mHandler.sendEmptyMessageDelayed(1, time);
    }

    @Override
    public void onDisConnected(String mac, int code) {

    }

    @Override
    public void onServicesDiscovered(String mac) {
        if (mBluetoothService != null) {
            mSet.add(mac);
            BleDevice bleDevice = mBluetoothService.getBleDevice(mac);
            bleDevice.setOnBleDeviceDataListener(this);
            if (mSetMode) {
                setMode(bleDevice);
                refreshLog("连接并设置:" + mac);
            }
            mHandler.sendEmptyMessageDelayed(1, 2000);

        }
    }

    @Override
    public void onNotifyData(String uuid, byte[] hex, int type) {
        if (type == 0x002F) {
            if ((hex[0] & 0xFF) == 0x40) {

                mHandler.removeMessages(1);
                mHandler.sendEmptyMessageDelayed(1, 1000);
                switch (hex[1] & 0xFF) {

                    case 0:
                        mSetModeNumber++;
                        refreshLog("设置成功:"+mSetModeNumber);
                        break;
                    case 1:
                        refreshLog("设置失败:"+mSetModeNumber);

                        break;
                    case 2:
                        refreshLog("不支持:"+mSetModeNumber);
                        break;

                }
                if (mBluetoothService!=null){
                    mBluetoothService.disconnectAll();
                }
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (mBluetoothService!=null) {
            mBluetoothService.disconnectAll();
            mBluetoothService.removeOnCallbackBle(this);
        }
    }
}

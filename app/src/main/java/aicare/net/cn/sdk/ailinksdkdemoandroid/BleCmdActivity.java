package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.config.CmdConfig;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.listener.CallbackDisIm;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleDeviceDataListener;
import com.pingwang.bluetoothlib.listener.OnBleErrListener;
import com.pingwang.bluetoothlib.listener.OnBleHandshakeListener;
import com.pingwang.bluetoothlib.listener.OnBleInfoListener;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.listener.OnBleParameterListener;
import com.pingwang.bluetoothlib.listener.OnBleRssiListener;
import com.pingwang.bluetoothlib.listener.OnBleSettingListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnCallbackDis;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleDataUtils;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * xing<br>
 * 2019/4/25<br>
 * 基础指令信息数据显示
 */
public class BleCmdActivity extends BleBaseActivity implements OnCallbackDis, OnBleDeviceDataListener, OnBleVersionListener, OnMcuParameterListener, OnBleErrListener, OnBleInfoListener, OnBleParameterListener, OnBleCompanyListener, OnBleSettingListener, OnBleHandshakeListener, View.OnClickListener, OnBleOtherDataListener, OnBleRssiListener {

    private static String TAG = BleCmdActivity.class.getName();
    private final int REFRESH_DATA = 3;
    private List<String> mList;
    private ArrayAdapter listAdapter;
    private EditText etName, etMacType, etCid, etVid, etPid, etBroadcastTime, etMcuType, etSleepTime;
    private Context mContext;
    private String mAddress;
    private BleSendCmdUtil mBleSendCmdUtil;
    private BleDataUtils bleDataUtils;
    private BleDevice mBleDevice;
    private int type;
    /**
     * 是否暂停显示数据
     */
    private boolean mPauseShowCmd = false;
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
        setContentView(R.layout.activity_ble);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        bleDataUtils = BleDataUtils.getInstance();
        findViewById(R.id.btnClear).setOnClickListener(this);
        findViewById(R.id.btnPause).setOnClickListener(this);
        findViewById(R.id.btnHandshake).setOnClickListener(this);
        findViewById(R.id.btnVersion).setOnClickListener(this);
        findViewById(R.id.btnBattery).setOnClickListener(this);
        findViewById(R.id.btnTimeRead).setOnClickListener(this);
        findViewById(R.id.btnTimeWrite).setOnClickListener(this);
        findViewById(R.id.btnNameRead).setOnClickListener(this);
        findViewById(R.id.btnMacRead).setOnClickListener(this);
        findViewById(R.id.btnMacTypeRead).setOnClickListener(this);
        findViewById(R.id.btnNameWrite).setOnClickListener(this);
        findViewById(R.id.btnMacTypeWrite).setOnClickListener(this);
        findViewById(R.id.btnDidWrite).setOnClickListener(this);
        findViewById(R.id.btnDidRead).setOnClickListener(this);
        findViewById(R.id.btnBroadcastTimeWrite).setOnClickListener(this);
        findViewById(R.id.btnBroadcastTimeRead).setOnClickListener(this);
        findViewById(R.id.btnBmRestart).setOnClickListener(this);
        findViewById(R.id.btnBmReset).setOnClickListener(this);
        findViewById(R.id.btnMcuType).setOnClickListener(this);
        findViewById(R.id.btnUnits).setOnClickListener(this);
        findViewById(R.id.btnSleepTimeWrite).setOnClickListener(this);
        findViewById(R.id.btnSleepTimeRead).setOnClickListener(this);
        findViewById(R.id.btn_start_ble).setOnClickListener(this);
        findViewById(R.id.btnNameRssi).setOnClickListener(this);
        etName = findViewById(R.id.etName);
        etMacType = findViewById(R.id.etMacType);
        etCid = findViewById(R.id.etCid);
        etVid = findViewById(R.id.etVid);
        etPid = findViewById(R.id.etPid);
        etBroadcastTime = findViewById(R.id.etBroadcastTime);
        etMcuType = findViewById(R.id.etMcuType);
        etSleepTime = findViewById(R.id.etSleepTime);
    }

    @Override
    public void onClick(View v) {
        SendBleBean sendBleBean;
        switch (v.getId()) {
            case R.id.btnClear:
                if (mList != null)
                    mList.clear();
                mHandler.sendEmptyMessage(REFRESH_DATA);
                break;
            case R.id.btnPause:
                mPauseShowCmd = !mPauseShowCmd;
                break;
            case R.id.btnHandshake:
                if (mBleDevice != null) {
                    mBleDevice.setHandshakeStatus(true);
//                    mBleDevice.sendHandshake();
                    mList.add(TimeUtils.getTime() + "发送握手");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btnVersion:
//                sendBleBean = new SendBleBean();
//                sendBleBean.setHex(mBleSendCmdUtil.getBleVersion());
//                sendData(sendBleBean);
                if (mBleDevice != null) {
                    String version = mBleDevice.getVersion();
                    mList.add(TimeUtils.getTime() + "版本号:" + version);
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btnBattery:
                sendBleBean = new SendBleBean();
                sendBleBean.setHex(mBleSendCmdUtil.getMcuBatteryStatus());
                sendData(sendBleBean);
                break;
            case R.id.btnTimeRead:
                sendBleBean = new SendBleBean();
                sendBleBean.setHex(mBleSendCmdUtil.getSysTime());
                sendData(sendBleBean);
                break;
            case R.id.btnTimeWrite:
                sendBleBean = new SendBleBean();
                sendBleBean.setHex(mBleSendCmdUtil.setSysTime(bleDataUtils.getCurrentTime(), true));
                sendData(sendBleBean);
                break;
            case R.id.btnNameRead:
                sendBleBean = new SendBleBean();
                sendBleBean.setHex(mBleSendCmdUtil.getBleName());
                sendData(sendBleBean);
                break;
            case R.id.btnMacRead:
                sendBleBean = new SendBleBean();
                sendBleBean.setHex(mBleSendCmdUtil.getBleMac());
                sendData(sendBleBean);
                break;
            case R.id.btnMacTypeRead:
                sendBleBean = new SendBleBean();
                sendBleBean.setHex(mBleSendCmdUtil.getBroadcastDataType());
                sendData(sendBleBean);
                break;
            case R.id.btnMacTypeWrite:
                byte macType = Byte.valueOf(etMacType.getText().toString().trim());
                sendBleBean = new SendBleBean();
                sendBleBean.setHex(mBleSendCmdUtil.setBroadcastDataType(macType));
                sendData(sendBleBean);
                break;
            case R.id.btnNameWrite:
                byte[] names = bleDataUtils.getBleName(etName.getText().toString().trim());
                sendBleBean = new SendBleBean();
                sendBleBean.setHex(mBleSendCmdUtil.setBleName(names));
                sendData(sendBleBean);
                break;
            case R.id.btnBroadcastTimeRead:
                sendBleBean = new SendBleBean();
                sendBleBean.setHex(mBleSendCmdUtil.getBleBroadcastTime());
                sendData(sendBleBean);
                break;
            case R.id.btnBroadcastTimeWrite:
                int time = Integer.parseInt(etBroadcastTime.getText().toString().trim());
                byte[] broadcastTime = bleDataUtils.getBroadcastTime(time);
                sendBleBean = new SendBleBean();
                sendBleBean.setHex(mBleSendCmdUtil.setBleBroadcastTime(broadcastTime));
                sendData(sendBleBean);
                break;
            case R.id.btnDidWrite:
                try {
                    String cidS = etCid.getText().toString().trim().toLowerCase(Locale.US);
                    String vidS = etVid.getText().toString().trim().toLowerCase(Locale.US);
                    String pidS = etPid.getText().toString().trim().toLowerCase(Locale.US);
                    int cid = 0;
                    int vid = 0;
                    int pid = 0;
                    if (!TextUtils.isEmpty(cidS)) {
                        cid = Integer.parseInt(cidS);
                    }
                    if (!TextUtils.isEmpty(vidS)) {
                        vid = Integer.parseInt(vidS);
                    }
                    if (!TextUtils.isEmpty(pidS)) {
                        pid = Integer.parseInt(pidS);
                    }

                    byte[] did = bleDataUtils.getDid(1, 1, 1, cid, vid, pid);
                    sendBleBean = new SendBleBean();
                    sendBleBean.setHex(mBleSendCmdUtil.setDid(did));
                    sendData(sendBleBean);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnDidRead:
                sendBleBean = new SendBleBean();
                sendBleBean.setHex(mBleSendCmdUtil.getDid());
                sendData(sendBleBean);
                break;
            case R.id.btnBmRestart:
                sendBleBean = new SendBleBean();
                sendBleBean.setHex(mBleSendCmdUtil.setBleRestart());
                sendData(sendBleBean);
                break;
            case R.id.btnBmReset:
                sendBleBean = new SendBleBean();
                sendBleBean.setHex(mBleSendCmdUtil.setBleReset());
                sendData(sendBleBean);
                break;
            case R.id.btnMcuType:
                byte mode = (byte) Integer.parseInt(etMcuType.getText().toString().trim());
                sendBleBean = new SendBleBean();
                sendBleBean.setHex(mBleSendCmdUtil.setPortI2cSpiMode(mode));
                sendData(sendBleBean);
                break;

            case R.id.btnUnits:
                sendBleBean = new SendBleBean();
                sendBleBean.setHex(mBleSendCmdUtil.getSupportUnit());
                sendData(sendBleBean);
                break;
            //写自动休眠
            case R.id.btnSleepTimeWrite:
                String sleepStr = etSleepTime.getText().toString().trim().toLowerCase(Locale.US);
                if (sleepStr.contains(",")) {
                    String[] didStrS = sleepStr.split(",");
                    if (didStrS.length > 3) {
                        int switchStatus = Integer.parseInt(didStrS[0]);
                        int toSleepTimeS = Integer.parseInt(didStrS[1]);
                        int sleepSwitchCmd = Integer.parseInt(didStrS[2]);
                        int broadcastTimeMS = Integer.parseInt(didStrS[3]);
                        sendBleBean = new SendBleBean();
                        sendBleBean.setHex(mBleSendCmdUtil.setNoConnectSleep(switchStatus, toSleepTimeS, sleepSwitchCmd, broadcastTimeMS));
                        sendData(sendBleBean);
                    }
                }
                break;
            case R.id.btnSleepTimeRead:
                sendBleBean = new SendBleBean();
                sendBleBean.setHex(mBleSendCmdUtil.getNoConnectSleep());
                sendData(sendBleBean);
                break;

            case R.id.btn_start_ble:
                sendBleBean = new SendBleBean();
                sendBleBean.setHex(mBleSendCmdUtil.setToWake());
                sendData(sendBleBean);
                break;
            case R.id.btnNameRssi:
                if (mBleDevice != null) {
                    mBleDevice.readRssi();
                }
                break;


        }
    }

    private void sendData(SendBleBean sendBleBean) {
        if (mBleDevice != null) {
            mBleDevice.sendData(sendBleBean);
        }
    }


    //---------------------------------服务---------------------------------------------------


    @Override
    public void onServiceSuccess() {
        mList.add(TimeUtils.getTime() + "服务与界面建立连接成功");
        mHandler.sendEmptyMessage(REFRESH_DATA);
        CallbackDisIm.getInstance().addListListener(this);
        if (mBluetoothService != null) {
            mBleDevice = mBluetoothService.getBleDevice(mAddress);
            if (mBleDevice == null) {
                finish();
                BleLog.i(TAG, "mBleDevice==null");
                return;
            }
            mBleDevice.setOnBleVersionListener(this);
            mBleDevice.setOnBleDeviceDataListener(this);
            mBleDevice.setOnBleErrListener(this);
            mBleDevice.setOnBleInfoListener(this);
            mBleDevice.setOnMcuParameterListener(this);
            mBleDevice.setOnBleSettingListener(this);
            mBleDevice.setOnBleCompanyListener(this);
            mBleDevice.setOnBleParameterListener(this);
            mBleDevice.setOnBleHandshakeListener(this);
            mBleDevice.setOnBleOtherDataListener(this);
            mBleDevice.setOnBleRssiListener(this);
        }
    }


    @Override
    public void onServiceErr() {
        mList.add(TimeUtils.getTime() + "服务与界面连接断开");
        mHandler.sendEmptyMessage(REFRESH_DATA);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
        //与服务断开连接
        mBluetoothService = null;
    }

    @Override
    public void unbindServices() {
        CallbackDisIm.getInstance().removeListener(this);
        BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
        if (bleDevice != null) {
            BleLog.i(TAG, "unbindService,断开连接");
            bleDevice.disconnect();
        }
    }


    //-----------------状态-------------------


    @Override
    public void onConnecting(@NonNull String mac) {
        //TODO 连接中
        BleLog.i(TAG, "连接中");
    }

    @Override
    public void onDisConnected(@NonNull String mac, int code) {
        //TODO 连接断开
        BleLog.i(TAG, "连接断开");
        if (mAddress.equals(mac)) {
            Toast.makeText(mContext, "连接断开:" + code, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onServicesDiscovered(@NonNull String mac) {
        //TODO 连接成功(获取服务成功)
        BleLog.i(TAG, "连接成功(获取服务成功)");

    }


    @Override
    public void bleOpen() {

    }

    @Override
    public void bleClose() {
        BleLog.i(TAG, "蓝牙未开启,可请求开启");
    }


    //---------------------------------


    @Override
    public void onNotifyOtherData(byte[] data) {
        if (mPauseShowCmd) {
            return;
        }
        mList.add(TimeUtils.getTime() + "透传数据:" + BleStrUtils.byte2HexStr(data));
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onNotifyDataA6(byte[] hex) {

    }

    @Override
    public void onNotifyData(byte[] hex, int type) {
        if (mPauseShowCmd) {
            return;
        }
        String data = "";
        if (hex != null)
            data = BleStrUtils.byte2HexStr(hex);
        if (type == 100) {
            mList.add(TimeUtils.getTime() + "send->" + data);
        } else {
            mList.add(TimeUtils.getTime() + "notify->" + data);
        }
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onHandshake(boolean status) {
        mList.add(TimeUtils.getTime() + "握手:" + status);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onBmVersion(String version) {
        mList.add(TimeUtils.getTime() + "版本号:" + version);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onSupportUnit(List<SupportUnitBean> list) {
        StringBuilder unitStr = new StringBuilder();
        unitStr.append(TimeUtils.getTime());

        for (SupportUnitBean supportUnitBean : list) {
            unitStr.append("单位类型:").append(supportUnitBean.getType());
            StringBuilder units = new StringBuilder();
            units.append("[");
            for (Integer integer1 : supportUnitBean.getSupportUnit()) {
                units.append(integer1).append(",");
            }
            units.append("]");
            unitStr.append("单位列表:").append(units);
            unitStr.append("\n");
        }


        mList.add(unitStr.toString());
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    public void onMcuBatteryStatus(int status, int battery) {
        mList.add(TimeUtils.getTime() + "电量:" + battery + "%" + "||状态:" + status);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onSysTime(int status, int[] times) {
        String timeStr = times[0] + "-" + times[1] + "-" + times[2] + "  " + times[3] + ":" + times[4] + ":" + times[5];
        mList.add(TimeUtils.getTime() + "时间:" + timeStr + "||是否有效:" + status);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onErr(int cmdType) {
        mList.add(TimeUtils.getTime() + "错误:" + cmdType);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void OnSettingReturn(byte cmdType, byte cmdData) {
        if (CmdConfig.SET_TO_SLEEP == cmdType && cmdData == CmdConfig.SETTING_SUCCESS) {
            //进入睡眠
        }
        mList.add(TimeUtils.getTime() + "设置指令:" + cmdType + "||结果:" + cmdData);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onBleName(String name) {
        mList.add(TimeUtils.getTime() + "名称:" + name);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onBleMac(String mac) {
        mList.add(TimeUtils.getTime() + "Mac:" + mac);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onNoConnectSleep(int sleepSwitch, int sleepTime, int sleepBroadcastSwitch, int sleepBroadcastTime) {

        mList.add(TimeUtils.getTime() + "sleepSwitch:" + sleepSwitch + " ||sleepTime:" + sleepTime + " ||sleepBroadcastSwitch:" + sleepBroadcastSwitch + " ||sleepBroadcastTime:" + sleepBroadcastTime);
        mHandler.sendEmptyMessage(REFRESH_DATA);

    }

    @Override
    public void OnDID(int cid, int vid, int pid) {
        mList.add(TimeUtils.getTime() + "cid:" + cid + "||vid:" + vid + "||pid:" + pid);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    public void onBleBroadcastTime(int time) {
        mList.add(TimeUtils.getTime() + "广播间隔:" + time);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onConnectTime(int time, int status, int timeOut) {
        mList.add(TimeUtils.getTime() + "连接:time:" + time + "||status:" + status + "||timeOut:" + timeOut);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onBlePower(int power) {
        mList.add(TimeUtils.getTime() + "功率:" + power);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onPortRate(int rate) {
        mList.add(TimeUtils.getTime() + "串口波特率:" + rate);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onBroadcastDataType(int type) {
        mList.add(TimeUtils.getTime() + "广播大小端:" + type);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onModuleUUID(int length, String serverUUID, String featureUUID1, String featureUUID2) {
        mList.add(TimeUtils.getTime() + "UUID:length:" + length + "||serverUUID:" + serverUUID + "||featureUUID1:" + featureUUID1 + "||featureUUID2" + featureUUID2);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onBleMode(int mode) {
        mList.add(TimeUtils.getTime() + "模式:" + mode);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void OnRssi(int rssi) {
        String name = "";
        if (mBleDevice != null) {
            name = mBleDevice.getName();
        }
        mList.add(TimeUtils.getTime() + "名称:" + name + " ,||信号:" + rssi);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleLog.i(TAG, "onDestroy");
    }
}

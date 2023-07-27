package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;
import cn.net.aicare.modulelibrary.module.sphygmomanometer.SphyBleConfig;
import cn.net.aicare.modulelibrary.module.sphygmomanometer.SphyDeviceData;


/**
 * xing<br>
 * 2019/4/25<br>
 * 血压计
 */
public class SphyCmdActivity extends BleBaseActivity implements OnCallbackDis, OnBleVersionListener, OnMcuParameterListener, OnBleCompanyListener, View.OnClickListener {

    private static String TAG = SphyCmdActivity.class.getName();
    private final int REFRESH_DATA = 3;
    private List<String> mList;
    private ArrayAdapter listAdapter;

    private Context mContext;
    private EditText et_type;
    private SphyDeviceData mBleDevice;
    private String mAddress;
    private BleSendCmdUtil mBleSendCmdUtil;
    private int mType;
    private int mCid;
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
        mType = getIntent().getIntExtra("type", -1);
        mCid = getIntent().getIntExtra("cid", -1);
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
        findViewById(R.id.btn_voice).setOnClickListener(this);
        findViewById(R.id.clear).setOnClickListener(this);
        et_type = findViewById(R.id.et_type);

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


    private int mVoiceStatus = 0;

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
            case R.id.btn_voice:
                if (mVoiceStatus == 0) {
                    mVoiceStatus = 1;
                }else {
                    mVoiceStatus=0;

                }
                mBleDevice.setSphyVoice(mVoiceStatus);
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
                mBleDevice.setType(mCid);
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
        if (mAddress.equals(mac)) {
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
            String cmdStr = "";
            switch (cmd) {
                case SphyBleConfig.SHPY_CMD_START:
                    cmdStr += "开始测量";
                    break;
                case SphyBleConfig.SHPY_CMD_STOP:
                    cmdStr += "停止测试";
                    break;
                case SphyBleConfig.SHPY_CMD_MCU_START:
                    cmdStr += "mcu 开机";
                    break;
                case SphyBleConfig.SHPY_CMD_MCU_STOP:
                    cmdStr += "mcu 关机";
                    break;
            }
            mList.add(TimeUtils.getTime() + "指令:" + cmdStr);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void getSphyVoice(byte cmd) {
            String cmdStr = "";
            switch (cmd) {
                case 0x00:
                    cmdStr += "打开语音";
                    break;
                case 0x01:
                    cmdStr += "关闭语音";
                    break;
            }
            mList.add(TimeUtils.getTime() + "指令:" + cmdStr);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void sphyDataNow(int dia, int sys, int decimal, int pul, int unit) {
            String diaStr = BleDensityUtil.getInstance().holdDecimals(dia, decimal);
            String sysStr = BleDensityUtil.getInstance().holdDecimals(sys, decimal);
            mList.add(TimeUtils.getTime() + "实时:舒张压=" + diaStr + "  收缩压=" + sysStr + "  心率=" + pul + "  单位=" + (unit == 0 ? "mmhg" : "kPa"));
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void sphyData(int dia, int sys, int decimal, int pul, int unit) {
            String diaStr = BleDensityUtil.getInstance().holdDecimals(dia, decimal);
            String sysStr = BleDensityUtil.getInstance().holdDecimals(sys, decimal);
            mList.add(TimeUtils.getTime() + "稳定:舒张压=" + diaStr + "  收缩压=" + sysStr + "  心率=" + pul + "  单位=" + (unit == 0 ? "mmhg" : "kPa"));
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void getUnit(int unit) {
            String showData = "";
            switch (unit) {
                case CmdConfig.SETTING_SUCCESS:
                    showData = "设置单位成功";
                    break;
                case CmdConfig.SETTING_FAILURE:
                    showData = "设置单位失败";

                    break;
                case CmdConfig.SETTING_ERR:

                    showData = "设置单位错误";
                    break;
            }
            mList.add(TimeUtils.getTime() + showData);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void getErr(byte status) {
            String errStr = "";
            switch ((int) status) {

                case 0:
                    errStr += "未找到高压";
                    break;
                case 1:
                    errStr += "无法正常加压，请检查是否插入袖带，或者重新插拔袖带气 管";
                    break;
                case 2:
                    errStr += "电量低";
                    break;
                case 3:
                    errStr += "传感器信号异常";
                    break;
                case 4:
                    errStr += "测量结果异常";
                    break;
                case 5:
                    errStr += "腕带过紧或气路堵塞";
                    break;
                case 60:
                    errStr += "测量中压力干扰严重";
                    break;
                case 7:
                    errStr += "压力超 290";
                    break;
                case 8:
                    errStr += "标定数据异常或存储 IC 异常";
                    break;

            }
            mList.add(TimeUtils.getTime() + "错误:" + errStr);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }
    }

    @Override
    public void onBmVersion(String version) {
        mList.add(TimeUtils.getTime() + "版本号:" + version);
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
        mList.add(TimeUtils.getTime() + "电量:" + battery + "%");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onSysTime(int status, int[] times) {
        String time = times[0] + "-" + times[1] + "-" + times[2] + "  " + times[3] + ":" + times[4] + ":" + times[5];
        mList.add(TimeUtils.getTime() + "系统时间:" + time);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleLog.i(TAG, "onDestroy");
    }
}

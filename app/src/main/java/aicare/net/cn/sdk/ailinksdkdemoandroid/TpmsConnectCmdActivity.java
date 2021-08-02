package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.CallbackDisIm;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnCallbackDis;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.tpms.TpmsDeviceData;


/**
 * xing<br>
 * 2019/9/2<br>
 * tpms连接版
 */
public class TpmsConnectCmdActivity extends BleBaseActivity implements OnCallbackDis,
        TpmsDeviceData.onNotifyData, TpmsDeviceData.onTpmsSetting, TpmsDeviceData.onTpmsInfo, OnBleVersionListener, OnMcuParameterListener, OnBleCompanyListener, View.OnClickListener {

    private static String TAG = TpmsConnectCmdActivity.class.getName();
    private final int REFRESH_DATA = 3;
    private List<String> mList;
    private ArrayAdapter listAdapter;

    /**
     * 服务Intent
     */
    private Context mContext;
    private EditText et_type;
    private TpmsDeviceData mBleDevice;
    private String mAddress;
    private BleSendCmdUtil mBleSendCmdUtil;
    private int type;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁止横屏
        setContentView(R.layout.activity_height);
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


        findViewById(R.id.clear).setOnClickListener(this);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btnVersion).setOnClickListener(this);
        findViewById(R.id.btnBattery).setOnClickListener(this);
        findViewById(R.id.btn_get_did).setOnClickListener(this);
        et_type = findViewById(R.id.et_type);

        cmdBtn();
    }


    private void cmdBtn() {
        EditText et_unit_weight = findViewById(R.id.et_unit_weight);
        EditText et_unit_height = findViewById(R.id.et_unit_height);
        Button btn_set_unit = findViewById(R.id.btn_set_unit);
        btn_set_unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weightStr = et_unit_weight.getText().toString();
                String heightStr = et_unit_height.getText().toString();
                if (!weightStr.equals("") && !heightStr.equals("")) {
                    int weight = Integer.valueOf(weightStr);
                    int height = Integer.valueOf(heightStr);
                    mBleDevice.setUnit((byte) height, (byte) weight);
                }
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
            case R.id.btn1:
                String cmd = et_type.getText().toString().trim();
                SendMcuBean sendDataBean = new SendMcuBean();
                sendDataBean.setHex(type,cmd.getBytes());
                mBleDevice.sendData(sendDataBean);
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
        BleLog.i(TAG, "服务与界面建立连接成功");
        CallbackDisIm.getInstance().addListListener(this);
        if (mBluetoothService != null) {
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                mBleDevice = TpmsDeviceData.getInstance(bleDevice);
                mBleDevice.setOnNotifyData(TpmsConnectCmdActivity.this);
                mBleDevice.setOnBleVersionListener(TpmsConnectCmdActivity.this);
                mBleDevice.setOnMcuParameterListener(TpmsConnectCmdActivity.this);
                mBleDevice.setOnCompanyListener(TpmsConnectCmdActivity.this);
                mBleDevice.setOnTpmsSetting(TpmsConnectCmdActivity.this);
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
        BleLog.i(TAG, "连接中");
    }

    @Override
    public void onDisConnected(@NonNull String mac, int code) {
        //TODO 连接断开
        BleLog.i(TAG, "连接断开");
        finish();
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
        finish();
    }

    //-----------------通知-------------------

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
    public void onTpmsData(int index, int pressure, int pressureUnit, int pressureDecimal,
                           float battery, int tem, int temUnit, int temDecimal, int status) {


    }

    @Override
    public void onTpmsType(int type) {

    }

    @Override
    public void getUnit(byte status) {
        mList.add(TimeUtils.getTime() + "单位结果:" + status);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void getErr(byte status) {
        mList.add(TimeUtils.getTime() + "错误:" + status);
        mHandler.sendEmptyMessage(REFRESH_DATA);
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
        String time =
                times[0] + "-" + times[1] + "-" + times[2] + "  " + times[3] + ":" + times[4] +
                        ":" + times[5];
        mList.add(TimeUtils.getTime() + "系统时间:" + time);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleLog.i(TAG, "onDestroy");
    }
}

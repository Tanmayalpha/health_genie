package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.pingwang.bluetoothlib.BleBaseActivity;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.CallbackDisIm;
import com.pingwang.bluetoothlib.listener.OnBleDeviceDataListener;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnCallbackDis;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.sphygmomanometer.SphyBleConfig;


/**
 * xing<br>
 * 2019/4/25<br>
 * 验证不握手不加密模块
 */
public class ClearShakeHandsActivity extends BleBaseActivity implements OnCallbackDis, OnBleVersionListener, OnMcuParameterListener,
        View.OnClickListener, OnBleOtherDataListener, OnBleDeviceDataListener {

    private static String TAG = ClearShakeHandsActivity.class.getName();
    private final int REFRESH_DATA = 3;
    private List<String> mList;
    private ArrayAdapter listAdapter;

    private Context mContext;
    private EditText et_type;
    private BleDevice mBleDevice;
    private String mAddress;
    private BleSendCmdUtil mBleSendCmdUtil;
    private int type=0x01;
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
        setContentView(R.layout.activity_clear_shake_hands);
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

        findViewById(R.id.btn_set_unit).setOnClickListener(this);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.clear).setOnClickListener(this);
        et_type = findViewById(R.id.et_type);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_set_unit:
                if (unit == SphyBleConfig.SPHY_UNIT_MMHG) {
                    unit = SphyBleConfig.SPHY_UNIT_KPA;
                    setUnit(unit);
                } else {
                    unit = SphyBleConfig.SPHY_UNIT_MMHG;
                    setUnit(unit);
                }
                break;

            case R.id.btn1:
                String cmd = et_type.getText().toString().trim();
                byte[] bytes=new byte[cmd.length()/2];
                for (int i=0;i<=cmd.length()-2;i+=2){
                    int i1 = Integer.parseInt(cmd.substring(i, i + 2), 16);
                    bytes[i>>1]=(byte)i1;
                }
                SendMcuBean sendDataBean = new SendMcuBean();
                sendDataBean.setHex(type,bytes);
                mBleDevice.sendData(sendDataBean);
                mList.add(TimeUtils.getTime() + "发送->" + BleStrUtils.byte2HexStr(bytes));
                mHandler.sendEmptyMessage(REFRESH_DATA);
                break;
            case R.id.clear:
                if (mList != null)
                    mList.clear();
                mHandler.sendEmptyMessage(REFRESH_DATA);
                break;
        }
    }


    /**
     * 设置单位(Set unit)
     *
     * @param unit
     */
    public void setUnit(byte unit) {
        SendMcuBean sendMcuBean = new SendMcuBean();
        byte[] data = new byte[2];
        data[0] = SphyBleConfig.SET_UNIT;
        data[1] = unit;
        sendMcuBean.setHex(0x01, data);
        mBleDevice.sendData(sendMcuBean);
        mList.add(TimeUtils.getTime() + "发送->" + BleStrUtils.byte2HexStr(data));
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    //---------------------------------服务---------------------------------------------------


    @Override
    public void onServiceSuccess() {
        CallbackDisIm.getInstance().addListListener(this);
        BleLog.i(TAG, "服务与界面建立连接成功");
        //与服务建立连接
        if (mBluetoothService != null) {
            mBleDevice = mBluetoothService.getBleDevice(mAddress);
            mBleDevice.setA7Encryption(false);
            mBleDevice.setOnBleDeviceDataListener(ClearShakeHandsActivity.this);
            mBleDevice.setOnBleVersionListener(ClearShakeHandsActivity.this);
            mBleDevice.setOnBleOtherDataListener(ClearShakeHandsActivity.this);
            byte[] bytes = mBleSendCmdUtil.setClearShakeHands();
            SendBleBean sendBleBean=new SendBleBean();
            sendBleBean.setHex(bytes);
            mBleDevice.sendData(sendBleBean);
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


    @Override
    public void onNotifyData(byte[] hex, int type) {
        mList.add(TimeUtils.getTime() + "notify->" + BleStrUtils.byte2HexStr(hex));
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onNotifyDataA6(byte[] hex) {
        mList.add(TimeUtils.getTime() + "notify->" + BleStrUtils.byte2HexStr(hex));
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onNotifyOtherData(byte[] data) {
        mList.add(TimeUtils.getTime() + "notify->" + BleStrUtils.byte2HexStr(data));
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onBmVersion(String version) {
        mList.add(TimeUtils.getTime() + "版本号:" + version);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleLog.i(TAG, "onDestroy");
    }
}

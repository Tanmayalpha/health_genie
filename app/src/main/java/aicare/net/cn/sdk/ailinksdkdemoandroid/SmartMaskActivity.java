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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnCallbackDis;
import com.pingwang.bluetoothlib.listener.OnScanFilterListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.net.aicare.modulelibrary.module.SmartMask.SmartMaskBleConfig;
import cn.net.aicare.modulelibrary.module.SmartMask.SmartMaskDeviceData;


/**
 * xing<br>
 * 2020/09/15<br>
 * 智能口罩
 */
public class SmartMaskActivity extends BleBaseActivity implements OnCallbackDis, SmartMaskDeviceData.onNotifyData, OnBleVersionListener, OnScanFilterListener, View.OnClickListener {

    private static String TAG = SmartMaskActivity.class.getName();
    private final int REFRESH_DATA = 3;
    @BindView(R.id.clear)
    Button mClear;
    @BindView(R.id.set_status)
    Button mSetStatus;
    @BindView(R.id.set_filter)
    Button mSetFilter;
    @BindView(R.id.set_fan)
    Button mSetFan;
    @BindView(R.id.listview)
    ListView mListview;
    @BindView(R.id.get_version)
    Button mGetVersion;
    @BindView(R.id.get_AIQ)
    Button mGetAIQ;
    @BindView(R.id.close_power)
    Button closePower;
    @BindView(R.id.set_mode)
    Button setMode;
    @BindView(R.id.set_payload)
    Button setPayload;
    private List<String> mList;
    private ArrayAdapter listAdapter;
    private Context mContext;
    private SmartMaskDeviceData mDevice;
    private String mAddress = "";
    private BleSendCmdUtil mBleSendCmdUtil;
    private int type = SmartMaskBleConfig.SMART_MASK;
    private int mFanStatus = 0;
    private boolean showPayload;


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
        setContentView(R.layout.activity_smart_mask);
        ButterKnife.bind(this);
        mContext = this;
        mAddress = getIntent().getStringExtra("mac");
        type = getIntent().getIntExtra("type", -1);
        mBleSendCmdUtil = BleSendCmdUtil.getInstance();
        init();

    }

    private void init() {
        mList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        mListview.setAdapter(listAdapter);

        mClear.setOnClickListener(this);
        mSetStatus.setOnClickListener(this);
        mSetFilter.setOnClickListener(this);
        mSetFan.setOnClickListener(this);
        mGetVersion.setOnClickListener(this);

        setPayload.setOnClickListener(this);
        setMode.setOnClickListener(this);
        closePower.setOnClickListener(this);
        mGetAIQ.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear:
                if (mList != null)
                    mList.clear();
                mHandler.sendEmptyMessage(REFRESH_DATA);
                break;
            case R.id.get_version:
                if (mDevice != null) {
                    SendBleBean sendBleBean = new SendBleBean();
                    sendBleBean.setHex(mBleSendCmdUtil.getBleVersion());
                    mDevice.sendData(sendBleBean);
                }
                break;
            case R.id.set_status:
                if (mDevice != null) {
                    mDevice.setStatus();
                }
                break;
            case R.id.set_filter:
                if (mDevice != null) {
                    mDevice.setReplaceFilter();
                }
                break;
            case R.id.set_fan:
                if (mDevice != null) {
                    if (mFanStatus < 2) {
                        mFanStatus++;
                    } else if (mFanStatus == 2) {
                        mFanStatus = 0;
                    }
                    mDevice.setFanStatus(mFanStatus);
                }
                break;
            case R.id.get_AIQ:
                if (mDevice != null) mDevice.getIAQData();
                break;
            case R.id.set_mode:
                if (mDevice != null) mDevice.setTestMode();
                break;
            case R.id.close_power:
                if (mDevice != null) mDevice.closePower();
                break;
            case R.id.set_payload:
                showPayload = true;
                break;
        }
    }


    //---------------------------------服务---------------------------------------------------


    @Override
    public void onServiceSuccess() {
        BleLog.i(TAG, "服务与界面建立连接成功");
        //与服务建立连接
        if (mBluetoothService != null) {
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                mDevice = SmartMaskDeviceData.getInstance(bleDevice);
                mDevice.setOnNotifyData(this);
                mDevice.setOnBleVersionListener(this);
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
        if (mDevice != null) {
            mDevice.disconnect();
            mDevice.clear();
            mDevice = null;
        }

    }


    //-----------------通知-------------------


    @Override
    public void onData(byte[] hex, int type) {
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
    public void onConnecting(@NonNull String mac) {
        //TODO 连接中
        BleLog.i(TAG, "连接中");
    }

    @Override
    public void onDisConnected(@NonNull String mac, int code) {
        //TODO 连接断开
        BleLog.i(TAG, "连接断开");
        finish();
        Toast.makeText(mContext, "连接断开", Toast.LENGTH_SHORT).show();
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

    //-----------------通知-------------------


    @Override
    public void onPayloadData(byte[] data) {

        if (showPayload) {
            mList.add(BleStrUtils.byte2HexStr(data));
            mHandler.sendEmptyMessage(REFRESH_DATA);

        }
    }

    @Override
    public void onSetStatus(int type,int status) {
        String data = "";
        if (type==SmartMaskBleConfig.GET_TEST_MODE){
            data="进入测试模式:";
        }else if (type==SmartMaskBleConfig.GET_POWER){
            data="关机:";
        }
        switch (status) {
            case 0:
                data += "成功";
                break;
            case 1:
                data += "失败";
                break;


        }
        mList.add(TimeUtils.getTime() + data);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onStatus(int airIndex, int fanStatus, int power, int powerStatus, int batteryRemaining, int breathRate, int breathState, int filterDuration) {

        String data = "口罩状态:";
        String fanStatusStr = "风扇状态:";
        switch (fanStatus) {
            case 0:
                fanStatusStr += "关闭";
                break;
            case 1:
                fanStatusStr += "1 档";
                break;
            case 2:
                fanStatusStr += "2 档";
                break;
        }

        String powerStatusStr = "电池状态:";
        switch (powerStatus) {
            case 0:
                powerStatusStr += "充电";
                break;
            case 1:
                powerStatusStr += "非充电";
                break;
        }

        String breathStateStr = "呼吸状态:";
        switch (breathState) {
            case 1:
                breathStateStr += "呼气";
                break;
            case 2:
                breathStateStr += "吸气";
                break;
        }


        data += "空气指数:" + airIndex + ";" + fanStatusStr + "\n";
        data += "电量:" + power + "%;" + powerStatusStr + "\n";
        data += "呼吸频率:" + breathRate + "次/min;" + breathStateStr + "\n";
        data += "电池续航:" + batteryRemaining + "min;滤网的总工作时长:" + filterDuration;
        mList.add(TimeUtils.getTime() + data);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onFilter(int status) {
        String data = "更换滤网:";
        switch (status) {
            case 0:
                data += "成功";
                break;
            case 1:
                data += "失败";
                break;
        }
        mList.add(TimeUtils.getTime() + data);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onFan(int status) {
        String data = "";
        switch (status) {

            case 0:
                data = "关闭风扇";
                break;
            case 1:
                data = "1 档风扇";
                break;
            case 2:
                data = "2 档风扇";
                break;
        }
        mList.add(TimeUtils.getTime() + data);
        mHandler.sendEmptyMessage(REFRESH_DATA);

    }


    @Override
    public void onIAQData(int status, int eCo2, int TvOc, int hcho) {
        String data = "";
        switch (status) {
            case 0:
                data = "OK";
                break;
            case 1:
                data = "Heating";
                break;
            case 2:
                data = "Error";
                break;
        }
        mList.add(TimeUtils.getTime() + data + "\n ECO2:" + eCo2 + "\n TVOC:" + TvOc + "\n HCHO:" + hcho);
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

package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.pingwang.bluetoothlib.BleBaseActivity;
import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.config.CmdConfig;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleDensityUtil;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.babyBodyFat.BabyBodyFatBleConfig;
import cn.net.aicare.modulelibrary.module.babyBodyFat.BabyBodyFatDeviceData;
import cn.net.aicare.modulelibrary.module.babyscale.BabyBleConfig;


/**
 * xing<br>
 * 2020/08/10<br>
 * 显示数据
 */
public class BabyBodyFatCmdActivity extends BleBaseActivity implements OnCallbackBle, OnBleVersionListener, OnMcuParameterListener, OnBleCompanyListener, BabyBodyFatDeviceData.onNotifyData,
        View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static String TAG = BabyBodyFatCmdActivity.class.getName();
    private final int REFRESH_DATA = 3;
    private List<String> mList;
    private ArrayAdapter listAdapter;
    private Context mContext;
    private BabyBodyFatDeviceData mDevice;
    private String mAddress;
    private BleSendCmdUtil mBleSendCmdUtil;
    private int type;
    private int weightUnit = 0;
    private int heightUnit = 0;
    private RadioButton mRadioButtonKg, mRadioButtonLb, mRadioButtonLbLb, mRadioButtonG, mRadioButtonOz, mRadioButtonStLb, mRadioButtonJin;
    private RadioButton mRadioButtonCm, mRadioButtonInch, mRadioButtonFoot;

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
        setContentView(R.layout.activity_baby_body_fat_cmd);
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

        findViewById(R.id.btnVersion).setOnClickListener(this);
        findViewById(R.id.clear).setOnClickListener(this);
        findViewById(R.id.btnDis).setOnClickListener(this);
        findViewById(R.id.btnConnect).setOnClickListener(this);
        findViewById(R.id.btn_set_tare).setOnClickListener(this);
        findViewById(R.id.btn_set_hold).setOnClickListener(this);
        ((RadioGroup) findViewById(R.id.radio_weight)).setOnCheckedChangeListener(this);
        ((RadioGroup) findViewById(R.id.radio_height)).setOnCheckedChangeListener(this);


        mRadioButtonKg = findViewById(R.id.radio_weight_kg);
        mRadioButtonJin = findViewById(R.id.radio_weight_jin);
        mRadioButtonStLb = findViewById(R.id.radio_weight_st_lb);
        mRadioButtonLb = findViewById(R.id.radio_weight_lb);
        mRadioButtonOz = findViewById(R.id.radio_weight_oz);
        mRadioButtonG = findViewById(R.id.radio_weight_g);
        mRadioButtonLbLb = findViewById(R.id.radio_weight_lb_lb);

        mRadioButtonCm = findViewById(R.id.radio_height_cm);
        mRadioButtonInch = findViewById(R.id.radio_height_inch);
        mRadioButtonFoot = findViewById(R.id.radio_height_foot);


    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == -1)
            return;//不是人为点击不触发
        switch (group.getCheckedRadioButtonId()) {
            case R.id.radio_weight_kg:
                weightUnit = BabyBleConfig.BABY_KG;
                break;
            case R.id.radio_weight_jin:
                weightUnit = BabyBleConfig.BABY_FG;
                break;
            case R.id.radio_weight_lb:
                weightUnit = BabyBleConfig.BABY_LB;
                break;
            case R.id.radio_weight_oz:
                weightUnit = BabyBleConfig.BABY_OZ;
                break;
            case R.id.radio_weight_st_lb:
                weightUnit = BabyBleConfig.BABY_ST;
                break;
            case R.id.radio_weight_g:
                weightUnit = BabyBleConfig.BABY_G;
                break;
            case R.id.radio_weight_lb_lb:
                weightUnit = BabyBleConfig.BABY_LB_LB;
                break;

            case R.id.radio_height_cm:
                heightUnit = BabyBleConfig.BABY_CM;
                break;
            case R.id.radio_height_inch:
                heightUnit = BabyBleConfig.BABY_INCH;
                break;
            case R.id.radio_height_foot:
                heightUnit = BabyBleConfig.BABY_FEET;
                break;
        }
        BleLog.i(TAG, "weightUnit:" + weightUnit + " ,||heightUnit:" + heightUnit);
        mDevice.setUnit(weightUnit, heightUnit);
    }


    private void showWeightUnit(int unit) {
        switch (unit) {
            case BabyBleConfig.BABY_KG:
                mRadioButtonKg.setChecked(true);
                break;
            case BabyBleConfig.BABY_FG:
                mRadioButtonJin.setChecked(true);
                break;
            case BabyBleConfig.BABY_LB:
                mRadioButtonLb.setChecked(true);
                break;
            case BabyBleConfig.BABY_OZ:
                mRadioButtonOz.setChecked(true);
                break;
            case BabyBleConfig.BABY_ST:
                mRadioButtonStLb.setChecked(true);
                break;
            case BabyBleConfig.BABY_G:
                mRadioButtonG.setChecked(true);
                break;
            case BabyBleConfig.BABY_LB_LB:
                mRadioButtonLbLb.setChecked(true);
                break;
        }
    }

    private void showHeightUnit(int unit) {

        switch (unit) {
            case BabyBleConfig.BABY_CM:
                mRadioButtonCm.setChecked(true);
                break;
            case BabyBleConfig.BABY_INCH:
                mRadioButtonInch.setChecked(true);
                break;
            case BabyBleConfig.BABY_FEET:
                mRadioButtonFoot.setChecked(true);
                break;

        }
    }


    @Override
    public void onClick(View v) {
        SendBleBean sendBleBean = new SendBleBean();
        switch (v.getId()) {
            case R.id.btnVersion:
                sendBleBean.setHex(mBleSendCmdUtil.getBleVersion());
                mDevice.sendData(sendBleBean);
                break;


            case R.id.clear:
                if (mList != null)
                    mList.clear();
                mHandler.sendEmptyMessage(REFRESH_DATA);
                break;


            case R.id.btnDis:
                if (mDevice != null) {
                    mDevice.disconnect();
                }
                break;
            case R.id.btnConnect:
                startScanBle(0);
                break;
            case R.id.btn_set_tare:
                if (mDevice != null) {
                    mDevice.setTare();
                }
                break;
            case R.id.btn_set_hold:
                if (mDevice != null) {
                    mDevice.setHold();
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
            mBluetoothService.setOnCallback(this);
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                mDevice = BabyBodyFatDeviceData.getInstance(bleDevice);
                mDevice.setOnNotifyData(this);
                mDevice.setOnBleVersionListener(BabyBodyFatCmdActivity.this);
                mDevice.setOnMcuParameterListener(BabyBodyFatCmdActivity.this);
                mDevice.setOnCompanyListener(BabyBodyFatCmdActivity.this);
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

    //-----------------状态-------------------


    @Override
    public void onScanning(BleValueBean data) {
        if (data.getMac().equals(mAddress)) {
            connectBle(mAddress);
            mList.add(TimeUtils.getTime() + "开始连接");
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }
    }

    @Override
    public void onConnecting(@NonNull String mac) {
        //TODO 连接中
        BleLog.i(TAG, "连接中");
        mList.add(TimeUtils.getTime() + "连接中");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onDisConnected(@NonNull String mac, int code) {
        //TODO 连接断开
        BleLog.i(TAG, "连接断开");
        mList.add(TimeUtils.getTime() + "连接断开");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onServicesDiscovered(@NonNull String mac) {
        //TODO 连接成功(获取服务成功)
        BleLog.i(TAG, "连接成功(获取服务成功)");
        mList.add(TimeUtils.getTime() + "连接成功");
        mHandler.sendEmptyMessage(REFRESH_DATA);
        onServiceSuccess();
    }


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
    public void onWeight(int weight, int decimal, byte unit, boolean stableStatus) {

        String unitStr = "kg";
        switch (unit) {
            case BabyBodyFatBleConfig.BABY_KG:
                unitStr = "kg";
                break;
            case BabyBodyFatBleConfig.BABY_FG:
                unitStr = "斤";
                break;
            case BabyBodyFatBleConfig.BABY_LB:
                unitStr = "lb:oz";
                break;
            case BabyBodyFatBleConfig.BABY_OZ:
                unitStr = "oz";
                break;
            case BabyBodyFatBleConfig.BABY_ST:
                unitStr = "st:lb";
                break;
            case BabyBodyFatBleConfig.BABY_G:
                unitStr = "g";
                break;
            case BabyBodyFatBleConfig.BABY_LB_LB:
                unitStr = "LB";
                break;

        }

        String weightStr = BleDensityUtil.getInstance().holdDecimals(weight, decimal);

        if (stableStatus) {
            mList.add(TimeUtils.getTime() + "稳定体重=" + weightStr + ";小数位=" + decimal + ";单位=" + unit + ";" + unitStr);
        } else {
            mList.add(TimeUtils.getTime() + "实时体重=" + weightStr + ";小数位=" + decimal + ";单位=" + unit + ";" + unitStr);
        }
        if (weightUnit != unit) {
            weightUnit = unit;
            showWeightUnit(weightUnit);
        }
        mHandler.sendEmptyMessage(REFRESH_DATA);

    }

    @Override
    public void onHeight(int height, int decimal, byte unit, boolean stableStatus) {
        String heightStr = BleDensityUtil.getInstance().holdDecimals(height, decimal);
        String unitStr = "cm";
        switch (unit) {
            case BabyBodyFatBleConfig.BABY_CM:
                unitStr = "cm";
                break;
            case BabyBodyFatBleConfig.BABY_INCH:
                unitStr = "inch";
                break;
            case BabyBodyFatBleConfig.BABY_FEET:
                unitStr = "foot";
                break;
        }

        if (stableStatus) {
            mList.add(TimeUtils.getTime() + "稳定身高=" + heightStr + ";小数位=" + decimal + ";单位=" + unit + ";" + unitStr);
        } else {
            mList.add(TimeUtils.getTime() + "实时身高=" + heightStr + ";小数位=" + decimal + ";单位=" + unit + ";" + unitStr);
        }
        if (heightUnit != unit) {
            heightUnit = unit;
            showHeightUnit(heightUnit);
        }
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onImpedanceTesting() {
        BleLog.i(TAG, "测阻抗中");
        mList.add(TimeUtils.getTime() + "测阻抗中...");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onImpedanceSuccess(boolean appAlgorithm,int adc, int algorithmId) {
        BleLog.i(TAG, "测阻抗成功");
        mList.add(TimeUtils.getTime() + "测阻抗成功,阻抗值:" + adc + " ,算法ID:" + algorithmId+" ;"+(appAlgorithm?"使用app算法":"使用秤算法"));
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onImpedanceFailure() {
        BleLog.i(TAG, "测阻抗失败");
        mList.add(TimeUtils.getTime() + "测阻抗失败...");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onMeasurementCompleted() {
        mList.add(TimeUtils.getTime() + "测量完成");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    public void getTare(byte status) {
        String data = "";
        switch (status) {
            case 0:
                data = "去皮成功";
                break;
            case 1:
                data = "去皮失败";
                break;
            case 2:
                data = "不支持";
                break;
        }
        mList.add(TimeUtils.getTime() + data);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void getHold(byte status) {
        String data = "";
        switch (status) {
            case 0:
                data = "锁定成功";
                break;
            case 1:
                data = "锁定失败";
                break;
            case 2:
                data = "不支持";
                break;
        }
        mList.add(TimeUtils.getTime() + data);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    public void getUnit(byte status) {
        String msg = "";
        switch (status) {
            case CmdConfig.SETTING_SUCCESS:
                msg = "设置单位成功";
                break;
            case CmdConfig.SETTING_FAILURE:
                msg = "设置单位失败";

                break;
            case CmdConfig.SETTING_ERR:
                msg = "设置单位错误";

                break;
        }
        mList.add(TimeUtils.getTime() + msg);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    public void getErr(byte status) {

        String statusStr = "";
        if (status == 0) {
            statusStr = "超重";
        } else if (status == 1) {
            statusStr = "称重期间，重量不稳定";
        } else if (status == 2) {
            statusStr = "称重失败";
        }

        mList.add(TimeUtils.getTime() + "错误指令:" + status+" ;"+statusStr);
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

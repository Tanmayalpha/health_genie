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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.listener.OnCallbackDis;
import com.pingwang.bluetoothlib.listener.OnScanFilterListener;
import com.pingwang.bluetoothlib.utils.BleDensityUtil;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.BroadcastScale.BroadcastScaleBleConfig;
import cn.net.aicare.modulelibrary.module.BroadcastScale.BroadcastScaleDeviceData;
import cn.net.aicare.modulelibrary.module.babyscale.BabyBleConfig;


/**
 * xing<br>
 * 2020/08/10<br>
 * 广播秤
 */
public class BroadcastScaleActivity extends BleBaseActivity implements OnCallbackDis, BroadcastScaleDeviceData.onNotifyData, OnScanFilterListener, View.OnClickListener,
        RadioGroup.OnCheckedChangeListener {

    private static String TAG = BroadcastScaleActivity.class.getName();
    private final int REFRESH_DATA = 3;

    private RadioButton mRadioButtonKg, mRadioButtonLb, mRadioButtonLbLb, mRadioButtonG, mRadioButtonOz, mRadioButtonStLb, mRadioButtonJin;
    private TextView tv_broadcast_temp, tv_broadcast_mac,tv_broadcast_did;

    private List<String> mList;
    private ArrayAdapter listAdapter;
    private Context mContext;
    private BroadcastScaleDeviceData mDevice;
    private String mAddress = "";
    private BleSendCmdUtil mBleSendCmdUtil;
    private int type = BroadcastScaleBleConfig.BROADCAST_SCALE;
    private int mWeightUnit = 0;
    private int mTemp;

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
        setContentView(R.layout.activity_broadcast_scale);
        mContext = this;
//        mAddress = getIntent().getStringExtra("mac");
        mBleSendCmdUtil = BleSendCmdUtil.getInstance();
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
        ((RadioGroup) findViewById(R.id.radio_weight)).setOnCheckedChangeListener(this);

        tv_broadcast_temp = findViewById(R.id.tv_broadcast_temp);

        mRadioButtonKg = findViewById(R.id.radio_weight_kg);
        mRadioButtonJin = findViewById(R.id.radio_weight_jin);
        mRadioButtonStLb = findViewById(R.id.radio_weight_st_lb);
        mRadioButtonLb = findViewById(R.id.radio_weight_lb);
        mRadioButtonOz = findViewById(R.id.radio_weight_oz);
        mRadioButtonG = findViewById(R.id.radio_weight_g);
        mRadioButtonLbLb = findViewById(R.id.radio_weight_lb_lb);

        tv_broadcast_mac = findViewById(R.id.tv_broadcast_mac);
        tv_broadcast_did = findViewById(R.id.tv_broadcast_did);

    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == -1)
            return;//不是人为点击不触发
        switch (group.getCheckedRadioButtonId()) {
            case R.id.radio_weight_kg:
                mWeightUnit = BabyBleConfig.BABY_KG;
                break;
            case R.id.radio_weight_jin:
                mWeightUnit = BabyBleConfig.BABY_FG;
                break;
            case R.id.radio_weight_lb:
                mWeightUnit = BabyBleConfig.BABY_LB;
                break;
            case R.id.radio_weight_oz:
                mWeightUnit = BabyBleConfig.BABY_OZ;
                break;
            case R.id.radio_weight_st_lb:
                mWeightUnit = BabyBleConfig.BABY_ST;
                break;
            case R.id.radio_weight_g:
                mWeightUnit = BabyBleConfig.BABY_G;
                break;
            case R.id.radio_weight_lb_lb:
                mWeightUnit = BabyBleConfig.BABY_LB_LB;
                break;

        }
        BleLog.i(TAG, "weightUnit:" + mWeightUnit);
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
            mDevice = BroadcastScaleDeviceData.getInstance();
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

    private String mOldData="";
    @Override
    public void onData(byte[] dataOriginal,byte[] hex, int type) {
        String data = "";
        if (hex != null)
            data = BleStrUtils.byte2HexStr(hex);
        if (mOldData.equals(data)){
            return;
        }
        mOldData=data;
        mList.add(TimeUtils.getTime() + "数据ID" + type+" ,||解密数据:"+data+" ,||原始数据:"+BleStrUtils.byte2HexStr(dataOriginal));
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    public void getWeightData(int status, int tempUnit, int weightUnit, int weightDecimal, int weightStatus, int weightNegative, int weight, int adc, int algorithmId, int tempNegative, int temp) {
        String weightUnitStr = "kg";
        switch (weightUnit) {
            case BroadcastScaleBleConfig.UNIT_KG:
                weightUnitStr = "kg";
                break;
            case BroadcastScaleBleConfig.UNIT_FG:
                weightUnitStr = "斤";
                break;
            case BroadcastScaleBleConfig.UNIT_LB:
                weightUnitStr = "lb:oz";
                break;
            case BroadcastScaleBleConfig.UNIT_OZ:
                weightUnitStr = "oz";
                break;
            case BroadcastScaleBleConfig.UNIT_ST:
                weightUnitStr = "st:lb";
                break;
            case BroadcastScaleBleConfig.UNIT_G:
                weightUnitStr = "g";
                break;
            case BroadcastScaleBleConfig.UNIT_LB_LB:
                weightUnitStr = "LB";
                break;
        }
        String tempUnitStr = "℃";//℉
        switch (tempUnit) {
            case BroadcastScaleBleConfig.UNIT_C:
                tempUnitStr = "℃";
                break;
            case BroadcastScaleBleConfig.UNIT_F:
                tempUnitStr = "℉";
                break;
        }
        String statusStr = "状态=";
        switch (status) {

            case BroadcastScaleBleConfig.GET_WEIGHT_TESTING:
                statusStr += "正在测量体重";
                break;
            case BroadcastScaleBleConfig.GET_IMPEDANCE_TESTING:
                statusStr += "测阻抗中";
                break;
            case BroadcastScaleBleConfig.GET_IMPEDANCE_SUCCESS:
                statusStr+= "测阻抗成功";
                break;
            case BroadcastScaleBleConfig.GET_IMPEDANCE_FAIL:
                statusStr += "测阻抗失败";
                break;
            case BroadcastScaleBleConfig.GET_TEST_FINISH:
                statusStr += "测量完成";
                break;
            default:
                statusStr+=Integer.toHexString(status);
                break;

        }
        String weightStr = BleDensityUtil.getInstance().holdDecimals(weight, weightDecimal);
        if (weightNegative == 1) {
            weightStr = "-" + weightStr;
        }
        String showData = TimeUtils.getTime();
        showData += statusStr;
        if (weightStatus == 1) {
            showData += "\n稳定体重=" + weightStr + ";小数位=" + weightDecimal + ";单位=" + weightUnit + ";" + weightUnitStr;
        } else {
            showData += "\n实时体重=" + weightStr + ";小数位=" + weightDecimal + ";单位=" + weightUnit + ";" + weightUnitStr;
        }
        showData += "\n阻抗=" + adc;
        if (temp == 65535) {
            //不支持温度
            showData += "\n温度=暂不支持";
        } else {
            if (tempNegative == 1) {
                showData += "\n温度=" + (-temp / 10F) + tempUnitStr;
            } else {
                showData += "\n温度=" + (temp / 10F) + tempUnitStr;
            }
            if (mTemp != temp) {
                mTemp = temp;
                tv_broadcast_temp.setText((mTemp / 10F) + tempUnitStr);
            }
        }

        showData += "\n算法ID=" + algorithmId;

        if (mWeightUnit != weightUnit) {
            mWeightUnit = weightUnit;
            showWeightUnit(mWeightUnit);
        }


        mList.add(showData);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void OnDID(int cid, int vid, int pid) {
        String didStr = "cid:" + cid + "||vid:" + vid + "||pid:" + pid;
        if (tv_broadcast_did!=null){
            tv_broadcast_did.setText(didStr);
        }
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

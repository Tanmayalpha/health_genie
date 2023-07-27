package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.meat_probe;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.CallbackDisIm;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import cn.net.aicare.modulelibrary.module.meatprobe.MeatProbeBleData;
import cn.net.aicare.modulelibrary.module.meatprobe.MeatProbeSendCmdUtil;
import cn.net.aicare.modulelibrary.module.meatprobe.OnMeatProbeDataListener;
import cn.net.aicare.modulelibrary.module.meatprobe.ProbeBean;
import cn.net.aicare.modulelibrary.module.meatprobe.ProbeNowBean;

/**
 * @author ljl
 * on 2023/6/6
 */
public class MeatProbeActivity extends BleBaseActivity implements OnCallbackBle, OnMeatProbeDataListener, View.OnClickListener {
    private Button btn_meat_probe_connect, btn_meat_probe_disconnect;
    private Button btn_meat_probe_version, btn_meat_probe_battery;
    private Button btn_meat_probe_switch_unit, btn_meat_probe_get_info;
    private Button btn_meat_probe_start, btn_meat_probe_end;
    private TextView tv_meat_probe_ambient, tv_meat_probe_internal, tv_meat_probe_target;
    private TextView tv_meat_probe_battery, tv_meat_probe_version;
    private TextView tv_meat_probe_ambient_unit, tv_meat_probe_internal_unit, tv_meat_probe_target_unit;
    ListView list_view_meat_probe;
    private List<String> mList;
    private ArrayAdapter mListAdapter;

    private String mMac;
    private int mCid;
    private int mVid;
    private int mPid;
    private BleDevice mBleDevice;
    private MeatProbeBleData mMeatProbeBleData;
    private long mCookingId;
    private double percent = 0.8;
    private int foodType = 0;
    private int foodRawness = 2;
    private int unit = 0;
    private ProbeBean mProbeBean;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meat_probe);
        initView();
        initData();
    }

    /**
     * 初始化控件
     */

    private void initView() {
        btn_meat_probe_connect = findViewById(R.id.btn_meat_probe_connect);
        btn_meat_probe_disconnect = findViewById(R.id.btn_meat_probe_disconnect);
        btn_meat_probe_version = findViewById(R.id.btn_meat_probe_version);
        btn_meat_probe_battery = findViewById(R.id.btn_meat_probe_battery);
        btn_meat_probe_switch_unit = findViewById(R.id.btn_meat_probe_switch_unit);
        btn_meat_probe_get_info = findViewById(R.id.btn_meat_probe_get_info);
        btn_meat_probe_start = findViewById(R.id.btn_meat_probe_start);
        btn_meat_probe_end = findViewById(R.id.btn_meat_probe_end);
        tv_meat_probe_ambient = findViewById(R.id.tv_meat_probe_ambient);
        tv_meat_probe_internal = findViewById(R.id.tv_meat_probe_internal);

        tv_meat_probe_battery = findViewById(R.id.tv_meat_probe_battery);
        tv_meat_probe_version = findViewById(R.id.tv_meat_probe_version);

        tv_meat_probe_target = findViewById(R.id.tv_meat_probe_target);
        tv_meat_probe_ambient_unit = findViewById(R.id.tv_meat_probe_ambient_unit);
        tv_meat_probe_internal_unit = findViewById(R.id.tv_meat_probe_internal_unit);
        tv_meat_probe_target_unit = findViewById(R.id.tv_meat_probe_target_unit);

        list_view_meat_probe = findViewById(R.id.list_view_meat_probe);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (getIntent() != null) {
            mMac = getIntent().getStringExtra("mac");
            mCid = getIntent().getIntExtra("type", 0);
            mVid = getIntent().getIntExtra("vid", 0);
            mPid = getIntent().getIntExtra("pid", 0);
        }
        CallbackDisIm.getInstance().addListListener(this);
        btn_meat_probe_connect.setOnClickListener(this);
        btn_meat_probe_disconnect.setOnClickListener(this);
        btn_meat_probe_version.setOnClickListener(this);
        btn_meat_probe_battery.setOnClickListener(this);
        btn_meat_probe_switch_unit.setOnClickListener(this);
        btn_meat_probe_get_info.setOnClickListener(this);
        btn_meat_probe_start.setOnClickListener(this);
        btn_meat_probe_end.setOnClickListener(this);

        mProbeBean = new ProbeBean(mMac);
        mProbeBean.setAlarmTemperaturePercent(0.8);
        mProbeBean.setFoodType(foodType);
        mProbeBean.setFoodRawness(foodRawness);
        mProbeBean.setTargetTemperature_C(65);
        mProbeBean.setTargetTemperature_F(149);
        mProbeBean.setAmbientMinTemperature_C(0);
        mProbeBean.setAmbientMinTemperature_F(32);
        mProbeBean.setAmbientMaxTemperature_C(100);
        mProbeBean.setAmbientMaxTemperature_F(212);
        mProbeBean.setTimerStart(0);
        mProbeBean.setTimerEnd(0);

        //初始化列表
        mList = new ArrayList<>();
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        list_view_meat_probe.setAdapter(mListAdapter);

    }

    /**
     * 添加一条文本
     *
     * @param text 文本
     */
    private void addText(String text) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
        mList.add(sdf.format(System.currentTimeMillis()) + "：\n" + text);
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        }
        if (list_view_meat_probe != null) {
            list_view_meat_probe.smoothScrollToPosition(mList.size() - 1);
        }
    }

    @Override
    public void onServiceSuccess() {
        if (mBluetoothService != null) {
            mBluetoothService.setOnCallbackBle(this);
            BleDevice bleDevice = mBluetoothService.getBleDevice(mMac);
            MeatProbeBleData.init(bleDevice);
            mMeatProbeBleData = MeatProbeBleData.getInstance();
            mMeatProbeBleData.addOnMeatProbeDataListener(this);
            MeatProbeSendCmdUtil.getInstance().setListeners(this);
        }
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {
        if (mBluetoothService!=null) {
            mBluetoothService.removeOnCallbackBle(this);
        }
    }

    @Override
    public void onServicesDiscovered(String mac) {
        //连接成功
        Log.e("ljl", "onServicesDiscovered: mac is " + mac);
        if (mBluetoothService != null) {
            mBluetoothService.setOnCallbackBle(this);
            BleDevice bleDevice = mBluetoothService.getBleDevice(mMac);
            MeatProbeBleData.init(bleDevice);
            mMeatProbeBleData = MeatProbeBleData.getInstance();
            mMeatProbeBleData.addOnMeatProbeDataListener(this);
            MeatProbeSendCmdUtil.getInstance().setListeners(this);
        }
    }

    @Override
    public void onDisConnected(String mac, int code) {
        //断开连接
        Toast.makeText(this, "断开连接", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_meat_probe_connect:
                if (mBluetoothService != null) {
                    mBluetoothService.connectDevice(mMac);
                }
                break;
            case R.id.btn_meat_probe_disconnect:
                if (mBluetoothService != null) {
                    mBluetoothService.disconnect(mMac);
                }
                break;
            case R.id.btn_meat_probe_version:
                if (mMeatProbeBleData != null) {
                    mMeatProbeBleData.getVersionInfo();
                }
                break;
            case R.id.btn_meat_probe_battery:
                if (mMeatProbeBleData != null) {
                    mMeatProbeBleData.getBattery();
                }
                break;
            case R.id.btn_meat_probe_switch_unit:
                if (mMeatProbeBleData != null) {
                    //0-摄氏度 1-华氏度
                    if (unit == 0) {
                        mMeatProbeBleData.sendSwitchUnit(1);
                    } else {
                        mMeatProbeBleData.sendSwitchUnit(0);
                    }

                }
                break;
            case R.id.btn_meat_probe_get_info:
                if (mMeatProbeBleData != null) {
                    mMeatProbeBleData.appGetDeviceInfo();
                }
                break;
            case R.id.btn_meat_probe_start:
                if (mMeatProbeBleData != null) {
                    mProbeBean.setCookingId(System.currentTimeMillis());
                    mProbeBean.setCurrentUnit(unit);
                    mMeatProbeBleData.appSetDeviceInfo(mProbeBean);
                }
                break;
            case R.id.btn_meat_probe_end:
                if (mMeatProbeBleData != null) {
                    mMeatProbeBleData.endWork();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null) {
            mBluetoothService.disconnect(mMac);
        }
    }

    @Override
    public void onBleNowData(String mac, ProbeNowBean probeNowBean) {

        //当前单位取实时温度的单位
        unit = probeNowBean.getRealTimeUnit();
        //环境温度
        tv_meat_probe_ambient.setText("环境温度:" + (probeNowBean.getAmbientPositive() == 0 ? probeNowBean.getAmbientTemp() : -probeNowBean.getAmbientTemp()));
        //食物温度
        tv_meat_probe_internal.setText("食物温度:" + (probeNowBean.getRealTimePositive() == 0 ? probeNowBean.getRealTimeTemp() : -probeNowBean.getRealTimeTemp()));

        //环境温度单位
        tv_meat_probe_ambient_unit.setText("环境温度单位:" + (probeNowBean.getAmbientUnit() == 0 ? "0(℃)" : "1(℉)"));
        //食物温度单位
        tv_meat_probe_internal_unit.setText("食物温度单位:" + (probeNowBean.getRealTimeUnit() == 0 ? "0(℃)" : "1(℉)"));

//        addText("环境温度:" + (probeNowBean.getAmbientPositive() == 0 ? probeNowBean.getAmbientTemp() : -probeNowBean.getAmbientTemp()) +
//                "食物温度:" + (probeNowBean.getRealTimePositive() == 0 ? probeNowBean.getRealTimeTemp() : -probeNowBean.getRealTimeTemp()) +
//                "目标温度:" + (probeNowBean.getTargetPositive() == 0 ? probeNowBean.getTargetTemp() : -probeNowBean.getTargetTemp()) +
//                "环境温度单位:" + (probeNowBean.getAmbientUnit() == 0 ? "0(℃)" : "1(℉)") +
//                "食物温度单位:" + (probeNowBean.getRealTimeUnit() == 0 ? "0(℃)" : "1(℉)") +
//                "目标温度单位:" + (probeNowBean.getTargetUnit() == 0 ? "0(℃)" : "1(℉)"));
    }

    @Override
    public void onBatteryState(String mac, int status, int battery) {
        Log.e("ljl", "onBatteryState: mac is " + mac + "   status is " + status + "   battery is " + battery);
        //当前电量
        tv_meat_probe_battery.setText("当前电量:" + battery);
        addText("当前电量:" + battery);
    }

    @Override
    public void onMcuVersionInfo(String mac, String versionInfo) {
        Log.e("ljl", "onMcuVersionInfo: mac is " + mac + "   versionInfo is " + versionInfo);
        //版本号
        tv_meat_probe_version.setText("模块版本号:" + versionInfo);
        addText("模块版本号:" + versionInfo);
    }

    @Override
    public void getDeviceInfo(String mac, ProbeBean probeBean) {
        //目标温度
        tv_meat_probe_target.setText("目标温度:" + probeBean.getTargetTemperature_C());
        //目标温度单位
        tv_meat_probe_target_unit.setText("目标温度单位:" + (probeBean.getCurrentUnit() == 0 ? "0(℃)" : "1(℉)"));
    }

    @Override
    public void getInfoFailed(String mac) {
        Log.e("ljl", "getInfoFailed: mac is " + mac);
    }

    @Override
    public void getInfoSuccess(String mac) {
        Log.e("ljl", "getInfoSuccess: mac is " + mac);
    }

    @Override
    public void onDataNotifyA7(String mac, byte[] dataA7) {
        //接收到的A7 payload数据
        addText("接收的A7 payload[" + BleStrUtils.byte2HexStr(dataA7) + "]");
    }

    @Override
    public void onDataNotifyA6(String mac, byte[] dataA6) {
        //接收到的A6 payload数据
        addText("接收的A6 payload[" + BleStrUtils.byte2HexStr(dataA6) + "]");
    }

    @Override
    public void onDataA6(byte[] dataA6) {
        //发出的A6 payload数据

        addText("发送的A6 payload[" + BleStrUtils.byte2HexStr(dataA6) + "]");
    }

    @Override
    public void onDataA7(byte[] dataA7) {
        //发出的A7 payload数据
        addText("发送的A7 payload[" + BleStrUtils.byte2HexStr(dataA7) + "]");
    }
}

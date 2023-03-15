package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.noise_meter;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.NoiseMeter.BleNoiseTLVBean;
import cn.net.aicare.modulelibrary.module.NoiseMeter.NoiseMeterHistoryBean;
import cn.net.aicare.modulelibrary.module.NoiseMeter.NoiseMeterWifiBleDevice;
import cn.net.aicare.modulelibrary.module.NoiseMeter.NoiseParseUtils;
import cn.net.aicare.modulelibrary.module.NoiseMeter.NoiseSendUtils;

/**
 * 噪音计(wifi+ble)
 *
 * @author xing
 */
public class WifiBleNoiseMeterActivity extends BleBaseActivity implements View.OnClickListener, NoiseParseUtils.OnDeviceSupportList, NoiseParseUtils.OnSetDeviceInfo, NoiseParseUtils.OnSynDeviceState, OnBleOtherDataListener {

    private ListView list_view;

    private List<String> mList;
    private ArrayAdapter mListAdapter;

    private String mMac;
    private BleDevice mBleDevice;
    private NoiseMeterWifiBleDevice mNoiseMeterBle;
    private NoiseSendUtils mNoiseSendUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_ble_noise_meter);

        findViewById(R.id.btn_get_device_state).setOnClickListener(this);
        findViewById(R.id.btn_get_support_list).setOnClickListener(this);
        findViewById(R.id.btn_noise_ac).setOnClickListener(this);
        findViewById(R.id.btn_noise_min_max).setOnClickListener(this);
        findViewById(R.id.btn_noise_hold).setOnClickListener(this);
        findViewById(R.id.btn_noise_log).setOnClickListener(this);

        list_view = findViewById(R.id.list_view);


        // 获取Mac
        mMac = getIntent().getStringExtra("mac");

        // 初始化列表
        mList = new ArrayList<>();
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        list_view.setAdapter(mListAdapter);
    }

    int deviceAc = 1;
    int deviceMinMax = 1;
    int deviceHold = 1;
    long mTime=0;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {

            case R.id.btn_get_device_state:
                sendData(mNoiseSendUtils.sendSetDeviceState());
                addText("获取设备状态");
                break;

            case R.id.btn_get_support_list:
                sendData(mNoiseSendUtils.sendSetSupportList());
                addText("获取设备支持的功能列表");
                break;
            case R.id.btn_noise_ac:
                if (deviceAc == 1) {
                    deviceAc++;
                } else {
                    deviceAc--;
                }
                sendData(mNoiseSendUtils.sendAc(deviceAc));
                break;
            case R.id.btn_noise_min_max:
                deviceMinMax++;
                sendData(mNoiseSendUtils.sendMinMaxMode(deviceMinMax%3));
                break;
            case R.id.btn_noise_hold:
                if (deviceHold == 0) {
                    deviceHold++;
                } else {
                    deviceHold--;
                }
                sendData(mNoiseSendUtils.sendHoldMode(deviceHold,100));
                break;

            case R.id.btn_noise_log:
                sendData(mNoiseSendUtils.sendGetHistory(mTime));
                addText("获取历史记录:时间="+mTime);
                break;

            default:

                break;

        }


    }


    private void sendData(BleNoiseTLVBean bleNoiseTLVBean) {
        if (mNoiseMeterBle != null) {
            mNoiseMeterBle.sendTLVData(bleNoiseTLVBean);
        }
    }

    @Override
    protected void onDestroy() {
        if (mNoiseMeterBle != null) {
            mNoiseMeterBle = null;
        }
        if (mBluetoothService != null) {
            mBluetoothService.disconnectAll();
        }
        super.onDestroy();
    }

    @Override
    public void onServiceSuccess() {
        mBleDevice = mBluetoothService.getBleDevice(mMac);
        if (mBleDevice != null) {
            if (mNoiseMeterBle == null) {
                NoiseMeterWifiBleDevice.init(mBleDevice);
                mNoiseMeterBle = NoiseMeterWifiBleDevice.getInstance();
                mNoiseSendUtils = new NoiseSendUtils();
                mNoiseMeterBle.setOnDeviceSupportList(this);
                mNoiseMeterBle.setOnSetDeviceInfo(this);
                mNoiseMeterBle.setOnSynDeviceState(this);
                mNoiseMeterBle.setOnBleOtherDataListener(this);
            }

            addText("设备连接成功：" + mMac);
        }
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }


    // 添加一条文本
    private void addText(String text) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
        mList.add(sdf.format(System.currentTimeMillis()) + "：\n" + text);
        mListAdapter.notifyDataSetChanged();
        list_view.smoothScrollToPosition(mList.size() - 1);
    }

    private String getSupportStr(boolean state) {
        return (state ? "支持" : "不支持");
    }

    //-----------------BLE-------------------


    @Override
    public void onProtocolVersion(String version) {
        addText("设备版本：" + version);
    }

    @Override
    public void onSupportFrequencyAc(boolean supportA, boolean supportC) {
        addText("设备频率计权A：" + getSupportStr(supportA) + "\n" + "设备频率计权C：" + getSupportStr(supportC));
    }

    @Override
    public void onSupportTestRange(int point, int min, int max) {
        addText("测量总范围：小数点位数=" + point + " \n最大值:" + max + " \n最小值:" + min);
    }

    @Override
    public void onSupportTestLevel(boolean supportMode) {
        addText("测量等级切换：" + getSupportStr(supportMode));
    }

    @Override
    public void onSupportMaxMinMode(boolean supportMode) {
        addText("Max/Min 模式：" + getSupportStr(supportMode));
    }

    @Override
    public void onSupportFatSlow(boolean fastType, boolean slowType) {
        addText("时间加权(Fast/Slow)：Fast=" + getSupportStr(fastType) + "  \nSlow=" + getSupportStr(slowType));
    }

    @Override
    public void onSupportHold(boolean supportMode) {
        addText("数值保持(hold)：" + getSupportStr(supportMode));
    }

    @Override
    public void onSupportAlarm(boolean supportMode, int min, int max) {
        addText("报警功能：" + getSupportStr(supportMode) + "  \n最小值:" + min + "  \n最大值:" + max);
    }

    @Override
    public void onSupportBackLight(boolean supportMode) {
        addText("背光：" + getSupportStr(supportMode));
    }

    @Override
    public void onSupportNoiseValuePoint(int valuePoint) {
        addText("噪音支持的小数点位数：" + valuePoint);
    }

    @Override
    public void onSupportHistory(boolean supportMode) {
        addText("设备是否支持保存历史数据：" + getSupportStr(supportMode));
    }

    @Override
    public void onSupportPowered(int type) {
        String showData;
        switch (type) {

            case 1:
                showData = "锂电池供电";
                break;
            case 2:
                showData = "干电池供电";
                break;
            case 3:
                showData = "市电供电";
                break;

            default:
                showData = "未知";
                break;

        }
        addText("供电：" + showData);
    }

    //------------------


    @Override
    public void onSetFrequencyAc(int value) {
        addText("设备：" + (value == 1 ? "使用 A 权" : "使用 C 权"));
    }

    @Override
    public void onSetTestLevel(int state, int level, int minLevel, int maxLevel) {
        addText("设备等级及范围：" + (state == 1 ? "等级+" : "等级-"));
        addText("设备：当前的等级=" + level + " \n量程最小值=" + minLevel + " \n量程最大值=" + maxLevel);
    }

    @Override
    public void onSetMaxMinMode(int mode) {
        onSynMaxMinMode(mode);
    }

    @Override
    public void onSetFatSlow(int mode) {
        onSynFatSlow(mode);
    }

    @Override
    public void onSetHold(boolean holdState, int holdValue) {
        onSynHold(holdState, holdValue);
    }

    @Override
    public void onSetAlarm(boolean allSwitch, boolean currentSwitch, int alarmValue) {
        addText("报警:"+(allSwitch?"打开报警":"关闭报警")+"\n当前:"+(currentSwitch?"关闭当前报警":"none")+"\n报警值:"+alarmValue);
    }

    @Override
    public void onSetBackLight(int switchStatus) {
        onSynBackLight(switchStatus);
    }

    @Override
    public void onSetNoiseHistory(List<NoiseMeterHistoryBean> list) {
        addText("设备返回的历史记录:"+list.size()+"条");
        if (list.size() > 0) {
            NoiseMeterHistoryBean bean = list.get(list.size() - 1);
            String show="时间:"+ TimeUtils.getTime(bean.getTime()*1000);
            show+="噪音值:"+bean.getValue();
            show+="\n状态:"+(bean.getAc()==1?"A权":"C权");
            show+="\n状态:"+(bean.isAlarmState()?"已报警":"未报警");
            show+="\n状态:"+(bean.getState()==1?"噪音值低于量程":"噪音值高于量程");
            addText("设备返回的最近一条历史记录:"+show);
            mTime=bean.getTime();
        }
    }

    @Override
    public void onSetBindDevice(int state) {
        String show = "";
        switch (state) {
            case 1:
                show = "APP 首次连接设备绑定时,会通过指令请求用户确认";
                break;
            case 2:
                show = "MCU 返回,MCU 等待用户按键";
                break;
            case 3:
                show = "MCU 返回,用户已按按键";
                break;
            case 4:
                show = "MCU 返回,用户超时(30s)没按按键";
                break;
            case 5:
                show = "APP 发起,APP 取消绑定";
                break;
            default:
        }


        addText("设备绑定：" + show);
    }

    //-------------

    @Override
    public void onSynFrequencyAc(int value) {
        addText("设备：" + (value == 1 ? "使用 A 权" : "使用 C 权"));
    }

    @Override
    public void onSynTestLevel(int level, int minLevel, int maxLevel) {
        addText("设备：当前的等级=" + level + " \n量程最小值=" + minLevel + " \n量程最大值=" + maxLevel);
    }

    @Override
    public void onSynMaxMinMode(int mode) {
        String show = "正常模式";
        if (mode == 1) {
            show = "最小值测量模式 Min";
        } else if (mode == 2) {
            show = "最大值测量模式 Max";
        }
        addText("Max/Min 模式:当前为" + show);
    }

    @Override
    public void onSynFatSlow(int mode) {
        String show = "正常模式";
        if (mode == 1) {
            show = "Fast 模式";
        } else if (mode == 2) {
            show = "Slow 模式";
        }
        addText("时间加权:当前为" + show);
    }

    @Override
    public void onSynHold(boolean holdMode, int holdValue) {
        String show = "正常模式";
        if (holdMode) {
            show = "hold 模式";
        }
        addText("数值保持:当前为" + show + "   内容=" + holdValue);
    }

    @Override
    public void onSynAlarm(boolean alarmStatus, boolean noiseAlarm) {
        String show = "未打开";
        if (alarmStatus) {
            show = "已打开";
        }
        addText("报警功能:当前为" + show + "   内容=" + (noiseAlarm ? "报警" : "未报警"));
    }

    @Override
    public void onSynBackLight(int switchStatus) {
        String show = "未打开";
        if (switchStatus==1) {
            show = "已打开";
        }
        addText("背光:当前为" + show);
    }

    @Override
    public void onSynNoiseValuePoint(int noiseState, int noiseValue) {
        String show = "数值有效";
        if (noiseState == 1) {
            show = "数值低于量程,显示 under";
        } else if (noiseValue == 2) {
            show = "数值高于量程,显示 over";
        }
        addText("噪音值:当前为" + show + "   内容=" + noiseValue);
    }

    @Override
    public void onSynPowered(boolean chargeState, boolean lowerState, int batter) {
        String show = "充电状态:";
        if (chargeState) {
            show += "在充电";
        } else {
            show += "未充电";
        }
        if (lowerState) {
            show += "低电";
        } else {
            show += "电压正常";
        }
        addText("供电:当前为" + show + "   电量百分比=" + batter);
    }

    @Override
    public void onNotifyOtherData(String uuid, byte[] hex) {
        String data = BleStrUtils.byte2HexStr(hex);
        addText("接收到未知指令：" + data);
    }
}

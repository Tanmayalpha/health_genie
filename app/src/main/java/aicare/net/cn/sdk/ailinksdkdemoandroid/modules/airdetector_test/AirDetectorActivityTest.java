package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.airdetector_test;

import android.app.AlertDialog;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.Toast;

import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.pingwang.bluetoothlib.utils.BleLog;

import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleAppBaseActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.airdetector.AirUtil;
import cn.net.aicare.modulelibrary.module.airDetector.AirConst;
import cn.net.aicare.modulelibrary.module.airDetector.AirDetectorWifeBleData;
import cn.net.aicare.modulelibrary.module.airDetector.AirSendUtil;
import cn.net.aicare.modulelibrary.module.airDetector.AlarmClockStatement;
import cn.net.aicare.modulelibrary.module.airDetector.StatusBean;
import cn.net.aicare.modulelibrary.module.airDetector.SupportBean;

/**
 * @author yesp
 */
public class AirDetectorActivityTest extends BleAppBaseActivity implements AirDetectorWifeBleData.AirDataInterface, OnCallbackBle,
        AirDetectorTestAdapter.OnSelectListener, SettingResultInterface {

    private static final String TAG = "AirDetectorActivityTest";
    private static final int STEP_BLE_NAME = 0; // 蓝牙名称
    private static final int STEP_VID_PID_CID = 1; // vid、pid、cid
    private static final int STEP_SUPPORT_FEATURES = 2;// 支持功能列表
    private static final int STEP_REALTIME_STATUS = 3;// 实时状态
    private static final int STEP_GET_SETTING_PARAMS = 4;// 实时设置状态
    private static final int STEP_WARM_HCHO = 5; // 甲醛报警
    private static final int STEP_WARM_TEMP = 6; // 温度报警
    private static final int STEP_WARM_HUMIDITY = 7; // 湿度报警
    private static final int STEP_WARM_PM2_5 = 8; // PM2.5报警
    private static final int STEP_WARM_PM1_0 = 9; // PM1.0报警
    private static final int STEP_WARM_PM10 = 10; // PM10报警
    private static final int STEP_WARM_VOC = 11; // VOC报警
    private static final int STEP_WARM_CO2 = 12; // CO2报警
    private static final int STEP_WARM_AQI = 13; // AQI报警
    private static final int STEP_WARM_TVOC = 14; // TVOC报警
    private static final int STEP_WARM_CO = 15; // CO报警
    private static final int STEP_VOICE = 16; // 音量
    private static final int STEP_WARM_DURATION = 17; // 报警时长
    private static final int STEP_WARM_VOICE_LEVEL = 18; // 报警铃声值
    private static final int STEP_UNIT_CHANGE = 19; // 温度单位切换
    private static final int STEP_TIME_FORMAT = 20; // 时间格式设置
    private static final int STEP_BRIGHTNESS_EQUIPMENT = 21; // 设备亮度
    private static final int STEP_KEY_SOUND = 22; // 按键音效
    private static final int STEP_ALARM_SOUND_EFFECT = 23; // 报警音效
    private static final int STEP_ICON_DISPLAY = 24; // 图标显示
    private static final int STEP_MONITORING_DISPLAY_DATA = 25; // 监控显示
    private static final int STEP_DATA_DISPLAY_MODE = 26; // 数据显示模式
    private static final int STEP_CAL_HCHO = 27; // 甲醛校准
    private static final int STEP_CAL_TEMP = 28; // 温度校准
    private static final int STEP_CAL_HUMIDITY = 29; // 湿度校准
    private static final int STEP_CAL_PM2_5 = 30; // PM2.5校准
    private static final int STEP_CAL_PM1_0 = 31; // PM1.0校准
    private static final int STEP_CAL_PM10 = 32; // PM10校准
    private static final int STEP_CAL_VOC = 33; // VOC校准
    private static final int STEP_CAL_CO2 = 34; // CO2校准
    private static final int STEP_CAL_AQI = 35; // AQI校准
    private static final int STEP_CAL_TVOC = 36; // TVOC校准
    private static final int STEP_CAL_CO = 37; // CO校准
    private static final int STEP_SETTING_ALARM = 38; // 闹钟
    private static final int STEP_SETTING_WARN_SWITCH = 39; // 报警总开关
    //    private static final int STEP_DEVICE_SELF_TEST = 18; // 设备自检
//    private static final int STEP_DEVICE_BIND = 18; // 设备绑定
//    private static final int STEP_RESTORE_FACTORY_SETTINGS = 18; // 恢复出厂设置
    private static final int STEP_DONE = 40;// 结束

    private static final int RESULT_NULL = 0;
    private static final int RESULT_SUCCESS = 1;
    private static final int RESULT_FAIL = 2;
    /**
     * 每个测试项等待多久
     */
    private static final long DELAY_FAIL = 3000;
    /**
     * 进入下一个测试需要等多久
     */
    private static final long DELAY_NEXT = 500;
    /**
     * 超时
     */
    private static final int MSG_FAIL = 100;
    /**
     * 下一步
     */
    private static final int MSG_NEXT = 101;

    private String mAddress;
    private RecyclerView recycler_view;
    private Button btn_floating;

    private List<AirDetectorTestBean> mList;
    private AirDetectorTestAdapter mAdapter;


    private AirDetectorWifeBleData mAirDetectorWifeBleData;

    private SparseArray<SupportBean> deviceSupportList = new SparseArray<>();
    /**
     * 当前正在进行哪个步骤
     */
    private int mStep;
    /**
     * 开关状态
     */
    private int switchOpenFlag = 1;
    /**
     * 测试次数
     */
    private int testCount = 0;

    @Override
    protected void uiHandlerMessage(Message msg) {
        switch (msg.what) {
            case MSG_FAIL:
                // 超时就是失败
                setTestResultStr(null);
                setTestResult(RESULT_FAIL);
                // 获取支持功能失败，结束流程
                if (mStep == STEP_SUPPORT_FEATURES) {
                    endCheck();
                    return;
                }
                // 然后开启下一组测试
                mStep = getNextStep();
                mHandler.sendEmptyMessageDelayed(MSG_NEXT, DELAY_NEXT);
                break;
            case MSG_NEXT:
                // 开始下一组测试
                if (mAirDetectorWifeBleData == null) {
                    Toast.makeText(this, "初始化失败，请重新进入", Toast.LENGTH_SHORT).show();
                } else {
                    test();
                }
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_air_detector_test;
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
    }

    private void startCheck() {
        if (mAirDetectorWifeBleData != null) {
            mAddress = getIntent().getStringExtra("mac");
            int cid = getIntent().getIntExtra("cid", 0);
            int vid = getIntent().getIntExtra("vid", 0);
            int pid = getIntent().getIntExtra("pid", 0);
            String bleName = getIntent().getStringExtra("bleName");
            String idsStr = "16进制：V:" + Integer.toHexString(vid) + "、C:" + Integer.toHexString(cid) + "、P:" + Integer.toHexString(pid);
            mList.clear();
            mAdapter.notifyDataSetChanged();
            deviceSupportList.clear();
            mList.add(new AirDetectorTestBean(mStep, "蓝牙名称", RESULT_SUCCESS, bleName));
            mList.add(new AirDetectorTestBean(mStep, "VID、CID、PID", RESULT_SUCCESS, idsStr));
            addTestTitle(1);
            mStep = STEP_SUPPORT_FEATURES;
            test();
        }
    }

    private void endCheck() {
        if (mStep != STEP_DONE) {
            mHandler.removeMessages(MSG_FAIL);
            mStep = STEP_DONE;
            mHandler.sendEmptyMessageDelayed(MSG_NEXT, DELAY_NEXT);
        }
    }

    @Override
    protected void initView() {
        recycler_view = findViewById(R.id.recycler_view);
        // 初始化列表
        mList = new ArrayList<>();
        mAdapter = new AirDetectorTestAdapter(this, mList);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setAdapter(mAdapter);
        mAdapter.setOnSelectListener(this);

        btn_floating = findViewById(R.id.btn_floating);
        btn_floating.setOnClickListener(v -> {
            if (btn_floating.getText().toString().contains("开始")) {
                btn_floating.setText("结束\n测试");
                if (testCount % 2 == 0) {
                    switchOpenFlag = 1;
                } else {
                    switchOpenFlag = 0;
                }
                testCount++;
                startCheck();
            } else {
                endCheck();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAirDetectorWifeBleData != null) {
            mAirDetectorWifeBleData.setCurKey(TAG);
        }
    }

    @Override
    public void onServiceSuccess() {
        mAddress = getIntent().getStringExtra("mac");
        BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
        if (bleDevice == null) {
            finish();
            return;
        }
        mBluetoothService.setOnCallback(this);
        if (mAirDetectorWifeBleData == null) {
//            AirDetectorWifeBleData.init(bleDevice);
            mAirDetectorWifeBleData = AirDetectorWifeBleData.getInstance();
            mAirDetectorWifeBleData.setDataInterface(TAG, this);
        }
    }

    @Override
    public void onServiceErr() {
        Log.i(TAG, "onServiceErr");
    }

    @Override
    public void unbindServices() {
        Log.i(TAG, "unbindServices");
    }

    @Override
    public void onDisConnected(String mac, int code) {
        if (mac.equals(mAddress) && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            BleLog.i(TAG, "设备断开连接，请退出重新连接");
            new AlertDialog.Builder(this)
                    .setTitle("ERROR：")
                    .setMessage("\n" + "设备断开连接，请退出重新连接")
                    .setPositiveButton("确认", (dialog, which) -> {
                        setResult(2001);
                        finish();
                    })
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSupportedList(SparseArray<SupportBean> supportList) {
        deviceSupportList = supportList;
        if (mStep == STEP_SUPPORT_FEATURES) {
            stepSuccess(AirDetectorTestShowUtil.showTextSupport(supportList));
        }

    }

    @Override
    public void onStatusList(SparseArray<StatusBean> statusList) {
        if (mStep == STEP_REALTIME_STATUS) {
            stepSuccess(AirDetectorTestShowUtil.showTextStatus(statusList, deviceSupportList));
        } else if (mStep == STEP_CAL_HCHO || mStep == STEP_CAL_TEMP || mStep == STEP_CAL_HUMIDITY || mStep == STEP_CAL_PM2_5
                || mStep == STEP_CAL_PM1_0 || mStep == STEP_CAL_VOC || mStep == STEP_CAL_CO2 || mStep == STEP_CAL_AQI
                || mStep == STEP_CAL_TVOC || mStep == STEP_CAL_CO) {
            if (statusList.get(AirConst.AIR_CALIBRATION_PARAMETERS) != null) {
                stepSuccess(AirDetectorTestShowUtil.showCalSettingAfterTextStatus(statusList, deviceSupportList));
            }
        }
    }

    @Override
    public void onSettingList(SparseArray<StatusBean> settingList) {
        if (mStep == STEP_GET_SETTING_PARAMS) {
            stepSuccess(AirDetectorTestShowUtil.showGetResultSettings(settingList, deviceSupportList));
        } else {
            AirDetectorTestShowUtil.showResultSettings(settingList, deviceSupportList, this);
        }
    }

    /**
     * 进行测试
     */
    private void test() {
        int type = 0;
        switch (mStep) {
            case STEP_SUPPORT_FEATURES:
                addTest("支持指标");
                mAirDetectorWifeBleData.sendData(AirSendUtil.getSupportList());
                break;
            case STEP_REALTIME_STATUS:
                addTest("实时数据");
                mAirDetectorWifeBleData.sendData(AirSendUtil.getDeviceState());
                break;
            case STEP_GET_SETTING_PARAMS:
                addTest("获取参数");
                mAirDetectorWifeBleData.sendData(AirSendUtil.getSettingState());
                break;
            case STEP_WARM_HCHO:
                type = AirConst.AIR_TYPE_FORMALDEHYDE;
                if (isSupportWarmType(type)) {
                    addTest("设置甲醛报警");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setWarmMaxByType(type, switchOpenFlag, 0.10f, deviceSupportList.get(type).getPoint()));
                }
                break;
            case STEP_WARM_TEMP:
                type = AirConst.AIR_TYPE_TEMP;
                if (isSupportWarmType(type)) {
                    addTest("设置温度报警");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setWarmTemp(0, AirConst.UNIT_C, 30f, 0f, 1));
                }
                break;
            case STEP_WARM_HUMIDITY:
                type = AirConst.AIR_TYPE_HUMIDITY;
                if (isSupportWarmType(type)) {
                    addTest("设置湿度报警");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setWarmHumidity(0, 60f, 7f, 1));
                }
                break;
            case STEP_WARM_PM2_5:
                type = AirConst.AIR_TYPE_PM_2_5;
                if (isSupportWarmType(type)) {
                    addTest("设置 PM2.5 报警");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setWarmMaxByType(type, switchOpenFlag, 75f, deviceSupportList.get(type).getPoint()));
                }
                break;
            case STEP_WARM_PM1_0:
                type = AirConst.AIR_TYPE_PM_1;
                if (isSupportWarmType(type)) {
                    addTest("设置 PM1.0 报警");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setWarmMaxByType(type, switchOpenFlag, 150f, deviceSupportList.get(type).getPoint()));
                }
                break;
            case STEP_WARM_PM10:
                type = AirConst.AIR_TYPE_PM_10;
                if (isSupportWarmType(type)) {
                    addTest("设置 PM10 报警");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setWarmMaxByType(type, switchOpenFlag, 150f, deviceSupportList.get(type).getPoint()));
                }
                break;
            case STEP_WARM_VOC:
                type = AirConst.AIR_TYPE_VOC;
                if (isSupportWarmType(type)) {
                    addTest("设置 VOC 报警");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setWarmMaxByType(type, switchOpenFlag, 0.5f, deviceSupportList.get(type).getPoint()));
                }
                break;
            case STEP_WARM_CO2:
                type = AirConst.AIR_TYPE_CO2;
                if (isSupportWarmType(type)) {
                    addTest("设置二氧化碳报警");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setWarmMaxByType(type, switchOpenFlag, 600f, deviceSupportList.get(type).getPoint()));
                }
                break;
            case STEP_WARM_AQI:
                type = AirConst.AIR_TYPE_AQI;
                if (isSupportWarmType(type)) {
                    addTest("设置空气质量报警");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setWarmMaxByType(type, switchOpenFlag, 100f, deviceSupportList.get(type).getPoint()));
                }
                break;
            case STEP_WARM_TVOC:
                type = AirConst.AIR_TYPE_TVOC;
                if (isSupportWarmType(type)) {
                    addTest("设置 TVOC 报警");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setWarmMaxByType(type, switchOpenFlag, 0.5f, deviceSupportList.get(type).getPoint()));
                }
                break;
            case STEP_WARM_CO:
                type = AirConst.AIR_TYPE_CO;
                if (isSupportWarmType(type)) {
                    addTest("设置一氧化碳报警");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setWarmMaxByType(type, switchOpenFlag, 50f, deviceSupportList.get(type).getPoint()));
                }
                break;
            case STEP_VOICE:
                type = AirConst.AIR_SETTING_VOICE;
                if (isSupportType(type)) {
                    addTest("设置音量");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setVoice(switchOpenFlag, 1));
                }
                break;
            case STEP_WARM_DURATION:
                type = AirConst.AIR_SETTING_WARM_DURATION;
                if (isSupportType(type)) {
                    addTest("设置报警时长");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setWarmDuration(5));
                }
                break;
            case STEP_WARM_VOICE_LEVEL:
                type = AirConst.AIR_SETTING_WARM_VOICE;
                if (isSupportType(type)) {
                    addTest("设置报警铃声值");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setTypeAndLengthOne(type, 1));
                }
                break;
            case STEP_UNIT_CHANGE:
                addTest("单位切换");
                mAirDetectorWifeBleData.sendData(AirSendUtil.setUnit(0));
                break;
            case STEP_TIME_FORMAT:
                type = AirConst.AIR_TIME_FORMAT;
                if (isSupportType(type)) {
                    addTest("设置切换时间格式");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setTypeAndLengthOne(type, 1));
                }
                break;
            case STEP_BRIGHTNESS_EQUIPMENT:
                type = AirConst.AIR_BRIGHTNESS_EQUIPMENT;
                if (isSupportType(type)) {
                    addTest("设置设备亮度");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setBrightnessEquipment(0, 2));
                }
                break;
            case STEP_KEY_SOUND:
                type = AirConst.AIR_KEY_SOUND;
                if (isSupportType(type)) {
                    addTest("设置按键音效开关");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setTypeAndSwitch(type, switchOpenFlag));
                }
                break;
            case STEP_ALARM_SOUND_EFFECT:
                type = AirConst.AIR_ALARM_SOUND_EFFECT;
                if (isSupportType(type)) {
                    addTest("设置报警音效开关");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setTypeAndSwitch(type, switchOpenFlag));
                }
                break;
            case STEP_ICON_DISPLAY:
                type = AirConst.AIR_ICON_DISPLAY;
                if (isSupportType(type)) {
                    addTest("设置图标显示开关");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setTypeAndSwitch(type, switchOpenFlag));
                }
                break;
            case STEP_MONITORING_DISPLAY_DATA:
                type = AirConst.AIR_MONITORING_DISPLAY_DATA;
                if (isSupportType(type)) {
                    addTest("设置监控显示开关");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setTypeAndSwitch(type, switchOpenFlag));
                }
                break;
            case STEP_DATA_DISPLAY_MODE:
                type = AirConst.AIR_DATA_DISPLAY_MODE;
                if (isSupportType(type)) {
                    addTest("设置显示模式");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setTypeAndLengthOne(type, 1));
                }
                break;
            case STEP_CAL_HCHO:
                type = AirConst.AIR_TYPE_FORMALDEHYDE;
                if (isSupportCalType(type)) {
                    addTest("设置甲醛校准, 单位校准值加1");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setCalibrationParam(type, 0));
                }
                break;
            case STEP_CAL_TEMP:
                type = AirConst.AIR_TYPE_TEMP;
                if (isSupportCalType(type)) {
                    addTest("设置温度校准, 单位校准值加1");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setCalibrationParam(type, 0));
                }
                break;
            case STEP_CAL_HUMIDITY:
                type = AirConst.AIR_TYPE_HUMIDITY;
                if (isSupportCalType(type)) {
                    addTest("设置湿度校准, 单位校准值加1");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setCalibrationParam(type, 0));
                }
                break;
            case STEP_CAL_PM2_5:
                type = AirConst.AIR_TYPE_PM_2_5;
                if (isSupportCalType(type)) {
                    addTest("设置PM2.5校准, 单位校准值加1");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setCalibrationParam(type, 0));
                }
                break;
            case STEP_CAL_PM1_0:
                type = AirConst.AIR_TYPE_PM_1;
                if (isSupportCalType(type)) {
                    addTest("设置PM1.0校准, 单位校准值加1");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setCalibrationParam(type, 0));
                }
                break;
            case STEP_CAL_PM10:
                type = AirConst.AIR_TYPE_PM_10;
                if (isSupportCalType(type)) {
                    addTest("设置PM10校准, 单位校准值加1");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setCalibrationParam(type, 0));
                }
                break;
            case STEP_CAL_VOC:
                type = AirConst.AIR_TYPE_VOC;
                if (isSupportCalType(type)) {
                    addTest("设置VOC校准, 单位校准值加1");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setCalibrationParam(type, 0));
                }
                break;
            case STEP_CAL_CO2:
                type = AirConst.AIR_TYPE_CO2;
                if (isSupportCalType(type)) {
                    addTest("设置二氧化碳校准, 单位校准值加1");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setCalibrationParam(type, 0));
                }
                break;
            case STEP_CAL_AQI:
                type = AirConst.AIR_TYPE_AQI;
                if (isSupportCalType(type)) {
                    addTest("设置空气质量校准, 单位校准值加1");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setCalibrationParam(type, 0));
                }
                break;
            case STEP_CAL_TVOC:
                type = AirConst.AIR_TYPE_TVOC;
                if (isSupportCalType(type)) {
                    addTest("设置TVOC校准, 单位校准值加1");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setCalibrationParam(type, 0));
                }
                break;
            case STEP_CAL_CO:
                type = AirConst.AIR_TYPE_CO;
                if (isSupportCalType(type)) {
                    addTest("设置一氧化碳校准, 单位校准值加1");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setCalibrationParam(type, 0));
                }
                break;
            case STEP_SETTING_ALARM:
                type = AirConst.AIR_ALARM_CLOCK;
                if (isSupportType(type)) {
                    addTest("设置闹钟");
                    SupportBean supportBean = deviceSupportList.get(AirConst.AIR_ALARM_CLOCK);
                    if (supportBean.getExtentObject() != null) {
                        AlarmClockStatement alarmClockStatement = (AlarmClockStatement) supportBean.getExtentObject();
                        int mode = 0;
                        if (alarmClockStatement.isMode4()) {
                            mode = 4;
                        } else if (alarmClockStatement.isMode0()) {
                            mode = 0;
                        } else if (alarmClockStatement.isMode1()) {
                            mode = 1;
                        } else if (alarmClockStatement.isMode2()) {
                            mode = 2;
                        } else if (alarmClockStatement.isMode3()) {
                            mode = 3;
                        }
                        int[] days = AirUtil.getAlarmClockDayByMode(mode);
                        mAirDetectorWifeBleData.sendData(AirSendUtil.setAlarm(1, 14, 30, days, 4, 0, false));
                    }
                }
                break;
            case STEP_SETTING_WARN_SWITCH:
                type = AirConst.AIR_SETTING_WARM;
                if (isSupportType(type) && deviceSupportList.get(type).getCurValue() == 0x02) {
                    addTest("设置指标报警总开关");
                    mAirDetectorWifeBleData.sendData(AirSendUtil.setMasterWarnSwitch(1));
                } else {
                    mStep = getNextStep();
                    test();
                }
                break;
            case STEP_DONE:
                addTest("测试结束。以上出现NG的项说明没有回复应该回的指令(超过2秒)。如果设备不支持，请忽略");
                mHandler.removeMessages(MSG_FAIL);
                if (btn_floating != null) {
                    btn_floating.setText("开始\n测试");
                }
                break;
        }
    }

    /**
     * 判断是否支持该报警功能
     *
     * @param type type
     * @return
     */
    private boolean isSupportWarmType(int type) {
        if (deviceSupportList != null && deviceSupportList.get(AirConst.AIR_SETTING_WARM) != null && deviceSupportList.get(type) != null) {
            return true;
        } else {
            mStep = getNextStep();
            test();
        }
        return false;
    }

    /**
     * 判断是否支持该功能
     *
     * @param type type
     * @return
     */
    private boolean isSupportType(int type) {
        if (deviceSupportList != null && deviceSupportList.get(type) != null) {
            return true;
        } else {
            mStep = getNextStep();
            test();
        }
        return false;
    }

    /**
     * 判断是否支持该类型的校验功能
     *
     * @param type type
     * @return
     */
    private boolean isSupportCalType(int type) {
        if (deviceSupportList != null && deviceSupportList.get(type) != null) {
            SupportBean supportBean = deviceSupportList.get(AirConst.AIR_CALIBRATION_PARAMETERS);
            if (supportBean != null && supportBean.getExtentObject() != null) {
                // 支持的校验类型包含本类型
                int[] calTypes = (int[]) supportBean.getExtentObject();
                for (int calType : calTypes) {
                    if (type == calType) {
                        return true;
                    }
                }
            }
        }
        // 不支持，继续下一项检查
        mStep = getNextStep();
        mHandler.sendEmptyMessageDelayed(MSG_NEXT, 100);
        return false;
    }

    /**
     * 添加测试项
     *
     * @param title 测试项标题
     */
    private void addTest(String title) {
        mList.add(new AirDetectorTestBean(mStep, title, RESULT_NULL));
        mAdapter.notifyItemInserted(mList.size() - 1);
        recycler_view.smoothScrollToPosition(mList.size() - 1);
        addTimeout();
    }

    /**
     * 添加测试项标题
     *
     * @param type 1：自动测试；2：手动测试
     */
    private void addTestTitle(int type) {
        mList.add(new AirDetectorTestBean(type));
        mAdapter.notifyItemInserted(mList.size() - 1);
        recycler_view.smoothScrollToPosition(mList.size() - 1);
    }

    /**
     * 设置测试结果
     *
     * @param result 0 无，1 成功，2 失败
     */
    private void setTestResult(int result) {
        setTestResult(result, "");
    }

    /**
     * 设置测试结果
     *
     * @param result  0 无，1 成功，2 失败
     * @param content content
     */
    private void setTestResult(int result, String content) {
        int position = mList.size() - 1;
        mList.get(position).setResult(result);
        mList.get(position).setContentStr(content);
        mAdapter.notifyItemChanged(position);
    }

    /**
     * 设置测试结果字符串
     *
     * @param resultStr 字符串
     */
    private void setTestResultStr(String resultStr) {
        mList.get(mList.size() - 1).setResultStr(resultStr);
        mAdapter.notifyItemChanged(mList.size() - 1);
    }

    /**
     * 设置测试标题字符串
     *
     * @param titleStr 字符串
     */
    private void setTestTitleStr(String titleStr) {
        mList.get(mList.size() - 1).setTitle(titleStr);
        mAdapter.notifyItemChanged(mList.size() - 1);
    }

    /**
     * 增加超时检测
     */
    private void addTimeout() {
        mHandler.removeMessages(MSG_FAIL);
        mHandler.sendEmptyMessageDelayed(MSG_FAIL, DELAY_FAIL);
    }

    /**
     * 这一步测试成功
     */
    private void stepSuccess(String content) {
        setTestResultStr(null);
        setTestResult(RESULT_SUCCESS, content);
        mHandler.removeMessages(MSG_FAIL);
        mHandler.removeMessages(MSG_NEXT);
        mHandler.sendEmptyMessageDelayed(MSG_NEXT, DELAY_NEXT);
        mStep = getNextStep();
    }

//    private int testFinalStep = STEP_SETTING_ALARM;

    /**
     * 获取下一步
     *
     * @return nextStep
     */
    private int getNextStep() {
//        if (mStep == testFinalStep) {
//            return STEP_DONE;
//        }
        return mStep + 1;
    }

    /**
     * 弹框提示
     *
     * @param str str
     */
    private void showNgDialog(String str) {
        new AlertDialog.Builder(this)
                .setTitle("请按照协议回复：")
                .setMessage("\n" + str)
                .setPositiveButton("确认", null)
                .show();
    }

    @Override
    public void onSelect(int pos) {
        AirDetectorTestBean bean = mList.get(pos);
        if (TextUtils.isEmpty(bean.getResultStr()) && bean.getResult() == RESULT_FAIL) {
            switch (bean.getStep()) {
                case STEP_SUPPORT_FEATURES:
                    showNgDialog("请参考 7.4.2 APP 获取设备功能列表的数据格式");
                    break;
                case STEP_REALTIME_STATUS:
                    showNgDialog("请参考 7.4.3 APP 获取设备状态");
                    break;
                case STEP_GET_SETTING_PARAMS:
                    showNgDialog("请参考 7.4.4 APP 获取参数");
                    break;
                case STEP_WARM_HCHO:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x01:甲醛 TLV 的格式声明");
                    break;
                case STEP_WARM_TEMP:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x02:温度 TLV 的格式声明");
                    break;
                case STEP_WARM_HUMIDITY:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x03:湿度 TLV 的格式声明");
                    break;
                case STEP_WARM_PM2_5:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x04:PM2.5 TLV 的格式声明");
                    break;
                case STEP_WARM_PM1_0:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x05:PM1.0 TLV 的格式声明");
                    break;
                case STEP_WARM_PM10:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x06:PM10 TLV 的格式声明");
                    break;
                case STEP_WARM_VOC:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x07:VOC TLV 的格式声明");
                    break;
                case STEP_WARM_CO2:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x08:CO2 TLV 的格式声明");
                    break;
                case STEP_WARM_AQI:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x09:AQI TLV 的格式声明");
                    break;
                case STEP_WARM_TVOC:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x10:TVOC TLV 的格式声明");
                    break;
                case STEP_WARM_CO:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x15:CO TLV 的格式声明");
                    break;
                case STEP_VOICE:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x0B:音量状态 TLV 的格式声明");
                    break;
                case STEP_WARM_DURATION:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x0C:报警时长 TLV 的格式声明");
                    break;
                case STEP_WARM_VOICE_LEVEL:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x0D:报警铃声 TLV 的格式声明");
                    break;
                case STEP_UNIT_CHANGE:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x11:单位切换 TLV 的格式声明");
                    break;
                case STEP_TIME_FORMAT:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x19:时间格式设置 TLV 的格式声明");
                    break;
                case STEP_BRIGHTNESS_EQUIPMENT:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x1A:设备亮度设置 TLV 的格式声明");
                    break;
                case STEP_KEY_SOUND:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x1B:按键音效设置 TLV 的格式声明");
                    break;
                case STEP_ALARM_SOUND_EFFECT:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x1C:报警音效设置 TLV 的格式声明");
                    break;
                case STEP_ICON_DISPLAY:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x1D:图标显示设置 TLV 的格式声明");
                    break;
                case STEP_MONITORING_DISPLAY_DATA:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x1E:监控显示数据 TLV 的格式声明");
                    break;
                case STEP_DATA_DISPLAY_MODE:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x1F:数据显示模式设置 TLV 的格式声明");
                    break;
                case STEP_CAL_HCHO:
                case STEP_CAL_TEMP:
                case STEP_CAL_HUMIDITY:
                case STEP_CAL_PM2_5:
                case STEP_CAL_PM1_0:
                case STEP_CAL_PM10:
                case STEP_CAL_VOC:
                case STEP_CAL_CO2:
                case STEP_CAL_AQI:
                case STEP_CAL_TVOC:
                case STEP_CAL_CO:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x18:参数校准设置 TLV 的格式声明");
                    break;
                case STEP_SETTING_ALARM:
                    showNgDialog("请参考 7.4.4 APP 设置参数，7.4.1 APP 参数设置 0x16:闹钟设置 TLV 的格式声明");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onWarmResultHCHO(String content) {
        if (mStep == STEP_WARM_HCHO) {
            stepSuccess(content);
        }
    }

    @Override
    public void onWarmResultTemp(String content) {
        if (mStep == STEP_WARM_TEMP) {
            stepSuccess(content);
        }
    }

    @Override
    public void onWarmResultHumidity(String content) {
        if (mStep == STEP_WARM_HUMIDITY) {
            stepSuccess(content);
        }
    }

    @Override
    public void onWarmResultPM2_5(String content) {
        if (mStep == STEP_WARM_PM2_5) {
            stepSuccess(content);
        }
    }

    @Override
    public void onWarmResultPM1_0(String content) {
        if (mStep == STEP_WARM_PM1_0) {
            stepSuccess(content);
        }
    }

    @Override
    public void onWarmResultPM10(String content) {
        if (mStep == STEP_WARM_PM10) {
            stepSuccess(content);
        }
    }

    @Override
    public void onWarmResultVOC(String content) {
        if (mStep == STEP_WARM_VOC) {
            stepSuccess(content);
        }
    }

    @Override
    public void onWarmResultCO2(String content) {
        if (mStep == STEP_WARM_CO2) {
            stepSuccess(content);
        }
    }

    @Override
    public void onWarmResultAQI(String content) {
        if (mStep == STEP_WARM_AQI) {
            stepSuccess(content);
        }
    }

    @Override
    public void onWarmResultTVOC(String content) {
        if (mStep == STEP_WARM_TVOC) {
            stepSuccess(content);
        }
    }

    @Override
    public void onWarmResultCO(String content) {
        if (mStep == STEP_WARM_CO) {
            stepSuccess(content);
        }
    }

    @Override
    public void onResultVoice(String content) {
        if (mStep == STEP_VOICE) {
            stepSuccess(content);
        }
    }

    @Override
    public void onResultWarmDuration(String content) {
        if (mStep == STEP_WARM_DURATION) {
            stepSuccess(content);
        }
    }

    @Override
    public void onResultWarmVoiceLevel(String content) {
        if (mStep == STEP_WARM_VOICE_LEVEL) {
            stepSuccess(content);
        }
    }

    @Override
    public void onResultUnitChange(String content) {
        if (mStep == STEP_UNIT_CHANGE) {
            stepSuccess(content);
        }
    }

    @Override
    public void onResultTimeFormat(String content) {
        if (mStep == STEP_TIME_FORMAT) {
            stepSuccess(content);
        }
    }

    @Override
    public void onResultBrightnessEquipment(String content) {
        if (mStep == STEP_BRIGHTNESS_EQUIPMENT) {
            stepSuccess(content);
        }
    }

    @Override
    public void onResultKeySound(String content) {
        if (mStep == STEP_KEY_SOUND) {
            stepSuccess(content);
        }
    }

    @Override
    public void onResultAlarmSoundEffect(String content) {
        if (mStep == STEP_ALARM_SOUND_EFFECT) {
            stepSuccess(content);
        }
    }

    @Override
    public void onResultIconDisplay(String content) {
        if (mStep == STEP_ICON_DISPLAY) {
            stepSuccess(content);
        }
    }

    @Override
    public void onResultMonitoringDisplayData(String content) {
        if (mStep == STEP_MONITORING_DISPLAY_DATA) {
            stepSuccess(content);
        }
    }

    @Override
    public void onResultDataDisplayMode(String content) {
        if (mStep == STEP_DATA_DISPLAY_MODE) {
            stepSuccess(content);
        }
    }

    @Override
    public void onResultMasterWarnSwitch(String content) {
        if (mStep == STEP_SETTING_WARN_SWITCH) {
            stepSuccess(content);
        }
    }

    @Override
    public void onCalResultHCHO(String content) {
        if (mStep == STEP_CAL_HCHO) {
            stepSuccess(content);
        }
    }

    @Override
    public void onCalResultTemp(String content) {
        if (mStep == STEP_CAL_TEMP) {
            stepSuccess(content);
        }
    }

    @Override
    public void onCalResultHumidity(String content) {
        if (mStep == STEP_CAL_HUMIDITY) {
            stepSuccess(content);
        }
    }

    @Override
    public void onCalResultPM2_5(String content) {
        if (mStep == STEP_CAL_PM2_5) {
            stepSuccess(content);
        }
    }

    @Override
    public void onCalResultPM1_0(String content) {
        if (mStep == STEP_CAL_PM1_0) {
            stepSuccess(content);
        }
    }

    @Override
    public void onCalResultPM10(String content) {
        if (mStep == STEP_CAL_PM10) {
            stepSuccess(content);
        }
    }

    @Override
    public void onCalResultVOC(String content) {
        if (mStep == STEP_CAL_VOC) {
            stepSuccess(content);
        }
    }

    @Override
    public void onCalResultCO2(String content) {
        if (mStep == STEP_CAL_CO2) {
            stepSuccess(content);
        }
    }

    @Override
    public void onCalResultAQI(String content) {
        if (mStep == STEP_CAL_AQI) {
            stepSuccess(content);
        }
    }

    @Override
    public void onCalResultTVOC(String content) {
        if (mStep == STEP_CAL_TVOC) {
            stepSuccess(content);
        }
    }

    @Override
    public void onCalResultCO(String content) {
        if (mStep == STEP_CAL_CO) {
            stepSuccess(content);
        }
    }

    @Override
    public void onSettingAlarmResult(String content) {
        if (mStep == STEP_SETTING_ALARM) {
            stepSuccess(content);
        }
    }
}

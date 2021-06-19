package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.elinkthings.bleotalibrary.rtk.RtkOtaManager;
import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnBleHandshakeListener;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.realsil.sdk.core.RtkConfigure;
import com.realsil.sdk.core.RtkCore;
import com.realsil.sdk.core.logger.ZLogger;
import com.realsil.sdk.dfu.DfuConstants;
import com.realsil.sdk.dfu.RtkDfu;

import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleAppBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.dialog.DialogStringImageAdapter;
import aicare.net.cn.sdk.ailinksdkdemoandroid.dialog.ShowListDialogFragment;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.FileUtils;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.L;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.MyBleStrUtils;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;
import butterknife.BindView;
import cn.net.aicare.modulelibrary.module.scooter.BleWeatherBean;
import cn.net.aicare.modulelibrary.module.scooter.SkateboardBleConfig;
import cn.net.aicare.modulelibrary.module.scooter.SkateboardDevice;

/**
 * xing<br>
 * 2020/6/28<br>
 * 滑板车
 */
public class SkateboardDataActivity extends BleAppBaseActivity implements OnCallbackBle, View.OnClickListener, SkateboardDevice.onNotifyData,
        RtkOtaManager.OnRtkOtaInfoListener, ShowListDialogFragment.onDialogListener, OnBleHandshakeListener {

    private final int REFRESH_DATA = 1;

    @BindView(R.id.listviewData)
    ListView mListviewData;
    @BindView(R.id.btn_clear)
    Button mBtnClear;
    @BindView(R.id.btn_open_light)
    Button mBtnOpenLight;
    @BindView(R.id.btn_close_light)
    Button mBtnCloseLight;
    @BindView(R.id.btn_time)
    Button mBtnTime;
    @BindView(R.id.btn_start_boost)
    Button mBtnStartBoost;
    @BindView(R.id.btn_start_no_boost)
    Button mBtnStartNoBoost;
    @BindView(R.id.btn_set_unit_km)
    Button mBtnSetUnitKm;
    @BindView(R.id.btn_set_unit_mi)
    Button mBtnSetUnitMi;
    @BindView(R.id.btn_zero_start)
    Button btn_zero_start;
    @BindView(R.id.btn_no_zero_start)
    Button btn_no_zero_start;
    @BindView(R.id.btn_constant_speed)
    Button mBtnConstantSpeed;
    @BindView(R.id.btn_no_constant_speed)
    Button mBtnNoConstantSpeed;
    @BindView(R.id.btn_reset)
    Button mBtnReset;
    @BindView(R.id.btn_heartbeat)
    Button btn_heartbeat;
    @BindView(R.id.btn_reset_device)
    Button btn_reset_device;
    @BindView(R.id.btn_reset_mileage)
    Button btn_reset_mileage;
    @BindView(R.id.btn_reset_time)
    Button btn_reset_time;
    @BindView(R.id.btn_connect)
    Button btn_connect;
    @BindView(R.id.btn_disconnect)
    Button btn_disconnect;

    @BindView(R.id.btn_set_auto_shutdown_time)
    Button btn_set_auto_shutdown_time;
    @BindView(R.id.btn_read_auto_shutdown_time)
    Button btn_read_auto_shutdown_time;
    @BindView(R.id.btn_gear_speed)
    Button btn_gear_speed;
    @BindView(R.id.btn_setControllerTime)
    Button btn_setControllerTime;
    @BindView(R.id.btn_readControllerTime)
    Button btn_readControllerTime;
    @BindView(R.id.btn_readControllerSingleMileage)
    Button btn_readControllerSingleMileage;
    @BindView(R.id.btn_readControllerAllMileage)
    Button btn_readControllerAllMileage;
    @BindView(R.id.btn_readControllerTemp)
    Button btn_readControllerTemp;
    @BindView(R.id.btn_readControllerDrivingCurrent)
    Button btn_setControllerDrivingCurrent;
    @BindView(R.id.btn_readControllerVoltage)
    Button btn_readControllerVoltage;
    @BindView(R.id.et_old_pwd)
    EditText et_old_pwd;
    @BindView(R.id.et_new_pwd)
    EditText et_new_pwd;
    @BindView(R.id.btn_setLockCarPassword)
    Button btn_setLockCarPassword;
    @BindView(R.id.btn_readAllPower)
    Button btn_readAllPower;
    @BindView(R.id.btn_readLastPower)
    Button btn_readLastPower;
    @BindView(R.id.btn_readBatteryDischargeNumber)
    Button btn_readBatteryDischargeNumber;
    @BindView(R.id.btn_readBatteryCode)
    Button btn_readBatteryCode;
    @BindView(R.id.btn_readCompanyVersion)
    Button btn_readCompanyVersion;
    @BindView(R.id.btn_readMotorPulse)
    Button btn_readMotorPulse;
    @BindView(R.id.btn_readMotorPulseInterval)
    Button btn_readMotorPulseInterval;
    @BindView(R.id.btn_readMotorParameter)
    Button btn_readMotorParameter;
    @BindView(R.id.btn_readWheelSize)
    Button btn_readWheelSize;
    @BindView(R.id.btn_readFlashDataVersion)
    Button btn_readFlashDataVersion;
    @BindView(R.id.btn_readBootloaderVersion)
    Button btn_readBootloaderVersion;
    @BindView(R.id.btn_readMeterBackLight)
    Button btn_readMeterBackLight;
    @BindView(R.id.btn_setMeterBackLight)
    Button btn_setMeterBackLight;
    @BindView(R.id.btn_setAutoNightMode)
    Button btn_setAutoNightMode;
    @BindView(R.id.btn_readNightMode)
    Button btn_readNightMode;
    @BindView(R.id.btn_setCallPhone)
    Button btn_setCallPhone;
    @BindView(R.id.btn_setSMSData)
    Button btn_setSMSData;
    @BindView(R.id.btn_setSoftwareData)
    Button btn_setSoftwareData;
    @BindView(R.id.btn_readBlePassword)
    Button btn_readBlePassword;
    @BindView(R.id.btn_setNavigationMessage)
    Button btn_setNavigationMessage;
    @BindView(R.id.btn_setResetCmd)
    Button btn_setResetCmd;
    @BindView(R.id.btn_setLockCar)
    Button btn_setLockCar;
    @BindView(R.id.btn_readServicePassword)
    Button btn_readServicePassword;
    @BindView(R.id.btn_setWeather)
    Button btn_setWeather;
    @BindView(R.id.btn_ota_boot)
    Button btn_ota_boot;
    @BindView(R.id.btn_ota_app)
    Button btn_ota1;
    @BindView(R.id.btn_ota_flash)
    Button btn_flash1;
    @BindView(R.id.btn_ble)
    Button btn_ble;
    @BindView(R.id.btn_update_conditions)
    Button btn_update_conditions;
    @BindView(R.id.btn_ble_version)
    Button btn_ble_version;
    @BindView(R.id.btn_ota_type)
    Button btn_ota_type;
    @BindView(R.id.btn_setDeviceLanguage)
    Button btn_setDeviceLanguage;
    @BindView(R.id.btn_getDeviceImageFontVersion)
    Button btn_getDeviceImageFontVersion;
    @BindView(R.id.btn_go_setProductionTest)
    Button btn_go_setProductionTest;
    @BindView(R.id.btn_exit_setProductionTest)
    Button btn_exit_setProductionTest;

    @BindView(R.id.btn_ota_app_controller)
    Button btn_ota_app_controller;
    @BindView(R.id.et_ble_ota_step)
    EditText et_ble_ota_step;
    @BindView(R.id.btn_getControllerVersion)
    Button btn_getControllerVersion;
    @BindView(R.id.btn_getTpVersion)
    Button btn_getTpVersion;
    @BindView(R.id.btn_setPair)
    Button btn_setPair;
    @BindView(R.id.btn_checkPwd)
    Button btn_checkPwd;
    @BindView(R.id.btn_get_test_status)
    Button btn_get_test_status;


    private List<String> mList;
    private ArrayAdapter listAdapter;

    private String mMac;

    private SkateboardDevice mDevice;

    private int mOtaUpdateStatus;
    private ArrayList<DialogStringImageAdapter.DialogStringImageBean> mDialogList;
    /**
     * OTA类型,0=BOOTLOADER,1=APP,2=FLASH,3=ble
     */
    @SkateboardBleConfig.OTAType
    private int mOTAType;

    /**
     * rtk升级方式
     * 默认静默升级
     */
    private int mRtkOtaType = DfuConstants.OTA_MODE_SILENT_FUNCTION;
    /**
     * ota作用域
     */
    @SkateboardBleConfig.ScopeType
    private int mOTAScope = SkateboardBleConfig.HOST_WRITE_RETURN_METER;


    @Override
    protected void uiHandlerMessage(Message msg) {

        switch (msg.what) {

            case REFRESH_DATA:
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
                break;

        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_skateboard_data;
    }

    @Override
    protected void initListener() {
        mBtnClear.setOnClickListener(this);
        mBtnOpenLight.setOnClickListener(this);
        mBtnCloseLight.setOnClickListener(this);
        mBtnTime.setOnClickListener(this);
        mBtnStartBoost.setOnClickListener(this);
        mBtnStartNoBoost.setOnClickListener(this);
        mBtnSetUnitKm.setOnClickListener(this);
        mBtnSetUnitMi.setOnClickListener(this);
        btn_zero_start.setOnClickListener(this);
        btn_no_zero_start.setOnClickListener(this);
        mBtnConstantSpeed.setOnClickListener(this);
        mBtnNoConstantSpeed.setOnClickListener(this);
        mBtnReset.setOnClickListener(this);
        btn_heartbeat.setOnClickListener(this);
        btn_reset_device.setOnClickListener(this);
        btn_reset_mileage.setOnClickListener(this);
        btn_reset_time.setOnClickListener(this);
        btn_connect.setOnClickListener(this);
        btn_disconnect.setOnClickListener(this);

        btn_set_auto_shutdown_time.setOnClickListener(this);
        btn_read_auto_shutdown_time.setOnClickListener(this);
        btn_gear_speed.setOnClickListener(this);
        btn_setControllerTime.setOnClickListener(this);
        btn_readControllerTime.setOnClickListener(this);
        btn_readControllerSingleMileage.setOnClickListener(this);
        btn_readControllerAllMileage.setOnClickListener(this);
        btn_readControllerTemp.setOnClickListener(this);
        btn_setControllerDrivingCurrent.setOnClickListener(this);
        btn_readControllerVoltage.setOnClickListener(this);

        btn_setLockCarPassword.setOnClickListener(this);
        btn_readAllPower.setOnClickListener(this);
        btn_readLastPower.setOnClickListener(this);
        btn_readBatteryDischargeNumber.setOnClickListener(this);
        btn_readBatteryCode.setOnClickListener(this);
        btn_readCompanyVersion.setOnClickListener(this);
        btn_readMotorPulse.setOnClickListener(this);
        btn_readMotorPulseInterval.setOnClickListener(this);
        btn_readMotorParameter.setOnClickListener(this);
        btn_readWheelSize.setOnClickListener(this);
        btn_readFlashDataVersion.setOnClickListener(this);
        btn_readBootloaderVersion.setOnClickListener(this);
        btn_readMeterBackLight.setOnClickListener(this);
        btn_setMeterBackLight.setOnClickListener(this);
        btn_setAutoNightMode.setOnClickListener(this);
        btn_readNightMode.setOnClickListener(this);
        btn_setCallPhone.setOnClickListener(this);
        btn_setSMSData.setOnClickListener(this);
        btn_setSoftwareData.setOnClickListener(this);
        btn_readBlePassword.setOnClickListener(this);
        btn_setNavigationMessage.setOnClickListener(this);
        btn_setResetCmd.setOnClickListener(this);
        btn_setLockCar.setOnClickListener(this);
        btn_readServicePassword.setOnClickListener(this);
        btn_setWeather.setOnClickListener(this);
        btn_ota_boot.setOnClickListener(this);
        btn_ota1.setOnClickListener(this);
        btn_flash1.setOnClickListener(this);
        btn_update_conditions.setOnClickListener(this);
        btn_ble.setOnClickListener(this);
        btn_ble_version.setOnClickListener(this);
        btn_ota_type.setOnClickListener(this);
        btn_setDeviceLanguage.setOnClickListener(this);
        btn_getDeviceImageFontVersion.setOnClickListener(this);
        btn_go_setProductionTest.setOnClickListener(this);
        btn_exit_setProductionTest.setOnClickListener(this);
        btn_ota_app_controller.setOnClickListener(this);
        btn_getControllerVersion.setOnClickListener(this);
        btn_getTpVersion.setOnClickListener(this);
        btn_setPair.setOnClickListener(this);
        btn_checkPwd.setOnClickListener(this);
        btn_get_test_status.setOnClickListener(this);

    }

    private int min = 0;
    private boolean mLockCar = false;
    private int phoneNumber = 1;
    private int smsNumber = 1;
    private int mNavigationCode = 1;
    private int mDeviceLanguage = 0;
    private int mBackLightPercentage = 10;
    private int mNightMode = 0;
    private int mWeatherCode = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_clear:
                if (mList != null) {
                    mList.clear();
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_connect:
//                mBluetoothService.scanLeDevice(0, SkateboardBleConfig.UUID_BROADCAST);
                mBluetoothService.connectDevice(mMac);
                break;
            case R.id.btn_disconnect:
                if (mDevice != null) {
                    mDevice.disconnect();
                    mList.add("断开连接");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;

            case R.id.btn_heartbeat:
                //心跳
                if (mDevice != null) {
                    mDevice.getHeartbeat();
                    mList.add("查询心跳");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_open_light:
                //开灯
                if (mDevice != null) {
                    mDevice.setOpenLight();
                    mList.add("开灯");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;

            case R.id.btn_close_light:
                //关灯
                if (mDevice != null) {
                    mDevice.setCloseLight();
                    mList.add("关灯");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_time:
                //设置当前时间
                if (mDevice != null) {
                    mDevice.setTime(min);
                    min++;
                    mList.add("时间");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_start_boost:
                //助力模式
                if (mDevice != null) {
                    mDevice.setBoost();
                    mList.add("助力模式");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_start_no_boost:
                //非助力
                if (mDevice != null) {
                    mDevice.setNoBoost();
                    mList.add("非助力模式");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_zero_start:
                //0启动
                if (mDevice != null) {
                    mDevice.setZeroStart();
                    mList.add("零启动");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_no_zero_start:
                //非0启动
                if (mDevice != null) {
                    mDevice.setNoZeroStart();
                    mList.add("非零启动");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_set_unit_km:
                //公里单位
                if (mDevice != null) {
                    mDevice.setUnitKm();
                    mList.add("公里单位");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_set_unit_mi:
                //英里单位
                if (mDevice != null) {
                    mDevice.setUnitMi();
                    mList.add("英里单位");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_constant_speed:
                //定速
                if (mDevice != null) {
                    mDevice.setConstantSpeed();
                    mList.add("定速巡航");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_no_constant_speed:
                //不定速
                if (mDevice != null) {
                    mDevice.setNoConstantSpeed();
                    mList.add("取消定速巡航");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_reset:
                //重置
                if (mDevice != null) {
                    mDevice.setClearAll();
                    mList.add("清除所有数据");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;

            case R.id.btn_reset_device:
                //恢复出厂设置
                if (mDevice != null) {
                    mDevice.setReset();
                    mList.add("恢复出厂设置");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_reset_mileage:
                if (mDevice != null) {
                    mDevice.setClearMileage();
                    mList.add("清除总里程");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_reset_time:
                if (mDevice != null) {
                    mDevice.setClearCyclingTime();
                    mList.add("清除骑行时间");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;

            case R.id.btn_set_auto_shutdown_time:
                if (mDevice != null) {
                    mDevice.setAutoShutdownTime(1200);
                    mList.add("设置自动关机时间:600S");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_read_auto_shutdown_time:
                if (mDevice != null) {
                    mDevice.readAutoShutdownTime();
                    mList.add("读自动关机时间");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_gear_speed:
                if (mDevice != null) {
                    mDevice.setGearSpeed(1, 10);
                    mList.add("设置档位速度");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_setControllerTime:
                if (mDevice != null) {
                    mDevice.setControllerTime(TimeUtils.getHour(), TimeUtils.getMinute());
                    mList.add("设置控制器的实时时间");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readControllerTime:
                if (mDevice != null) {
                    mDevice.readControllerTime();
                    mList.add("读取控制器的时间");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readControllerSingleMileage:
                if (mDevice != null) {
                    mDevice.readControllerSingleMileage();
                    mList.add("读取控制器单次行驶里程");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readControllerAllMileage:
                if (mDevice != null) {
                    mDevice.readControllerAllMileage();
                    mList.add("读取控制器总行驶里程");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readControllerTemp:
                if (mDevice != null) {
                    mDevice.readControllerTemp();
                    mList.add("读取控制器温度");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readControllerDrivingCurrent:
                if (mDevice != null) {
                    mDevice.readControllerDrivingCurrent();
                    mList.add("读取控制器行驶电流");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;

            case R.id.btn_readControllerVoltage:
                if (mDevice != null) {
                    mDevice.readControllerVoltage();
                    mList.add("读电压");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readAllPower:
                if (mDevice != null) {
                    mDevice.readAllPower();
                    mList.add("读总容量");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readLastPower:
                if (mDevice != null) {
                    mDevice.readLastPower();
                    mList.add("读剩余容量");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readBatteryDischargeNumber:
                if (mDevice != null) {
                    mDevice.readBatteryDischargeNumber();
                    mList.add("读充放电次数");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readBatteryCode:
                if (mDevice != null) {
                    mDevice.readBatteryCode();
                    mList.add("读电池信息");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readCompanyVersion:
                if (mDevice != null) {
                    mDevice.readCompanyVersion();
                    mList.add("读版本");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_getControllerVersion:
                if (mDevice != null) {
                    mDevice.readControllerVersion();
                    mList.add("读控制器版本");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_getTpVersion:
                if (mDevice != null) {
                    mDevice.readTpVersion();
                    mList.add("读TP屏版本");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readMotorPulse:
                if (mDevice != null) {
                    mDevice.readMotorPulse();
                    mList.add("读电机脉冲");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readMotorPulseInterval:
                if (mDevice != null) {
                    mDevice.readMotorPulseInterval();
                    mList.add("读电机脉冲间隔");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readMotorParameter:
                if (mDevice != null) {
                    mDevice.readMotorParameter();
                    mList.add("读电机参数");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readWheelSize:
                if (mDevice != null) {
                    mDevice.readWheelSize();
                    mList.add("读车轮尺寸");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readFlashDataVersion:
                if (mDevice != null) {
                    mDevice.readFlashDataVersion();
                    mList.add("读FLASH版本");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readBootloaderVersion:
                if (mDevice != null) {
                    mDevice.readBootloaderVersion();
                    mList.add("读Boot版本");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_setMeterBackLight:
                if (mDevice != null) {
                    mDevice.setMeterBackLight(mBackLightPercentage);
                    mList.add("设置背光:" + mBackLightPercentage);
                    mBackLightPercentage += 10;
                    if (mBackLightPercentage > 100) {
                        mBackLightPercentage = 10;
                    }
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readMeterBackLight:
                if (mDevice != null) {
                    mDevice.readMeterBackLight();
                    mList.add("读背光亮度");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_setAutoNightMode:
                if (mDevice != null) {
                    mDevice.setAutoNightMode(mNightMode, 0, 0, 23, 59);
                    mList.add("夜间模式:" + mNightMode);
                    mNightMode++;
                    if (mNightMode > 3) {
                        mNightMode = 0;
                    }
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readNightMode:
                if (mDevice != null) {
                    mDevice.readNightMode();
                    mList.add("读夜间模式");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_setCallPhone:
                if (mDevice != null) {
                    mDevice.setCallPhone("123456789");
                    mDevice.setSoftwareData(1, phoneNumber);
                    mList.add("来电");
                    phoneNumber++;
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_setSMSData:
                if (mDevice != null) {
                    mDevice.setSMSData("abcdefg");
                    mDevice.setSoftwareData(2, smsNumber);
                    mList.add("短信");
                    smsNumber++;
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_setSoftwareData:
                if (mDevice != null) {
                    for (int i = 0; i < 11; i++) {
                        mDevice.setSoftwareData(i, 0);
                    }
                    mList.add("清空消息");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readBlePassword:
                if (mDevice != null) {
                    mDevice.readBlePassword();
                    mList.add("密码查询");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_setNavigationMessage:
                if (mDevice != null) {
                    mDevice.setNavigationMessage(true, mNavigationCode, 100 * mNavigationCode, 1000 * mNavigationCode);
                    mList.add("导航推送:" + mNavigationCode);
                    mNavigationCode++;
                    if (mNavigationCode > 7) {
                        mNavigationCode = 1;
                    }
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_setResetCmd:
                if (mDevice != null) {
                    mDevice.setResetCmd();
                    mList.add("重置指令");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_setLockCar:
                if (mDevice != null) {
                    mLockCar = !mLockCar;
                    String newPwd = et_new_pwd.getText().toString().trim();
                    char[] chars = newPwd.toCharArray();
                    StringBuilder newP = new StringBuilder();
                    for (int i = chars.length - 1; i >= 0; i--) {
                        newP.append(chars[i]);
                    }
                    mDevice.setLockCar(mLockCar, newP.toString());
                    mList.add("锁车/解锁");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_readServicePassword:
                if (mDevice != null) {
                    mDevice.readServicePassword();
                    mList.add("售后密码");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_setWeather:
                if (mDevice != null) {
                    List<BleWeatherBean> list = new ArrayList<>();
                    BleWeatherBean bleWeatherBean = new BleWeatherBean(mWeatherCode, 2, 3, 4, 5, 6, 7, 8);
                    list.add(bleWeatherBean);
                    list.add(bleWeatherBean);
                    list.add(bleWeatherBean);
                    list.add(bleWeatherBean);
                    list.add(bleWeatherBean);
                    mDevice.setWeather(list);
                    mList.add("天气信息推送:" + mWeatherCode);
                    mWeatherCode++;
                    if (mWeatherCode > 40) {
                        mWeatherCode = 0;
                    }
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_setLockCarPassword:
                if (mDevice != null) {
                    String oldPwd = et_old_pwd.getText().toString().trim();
                    String newPwd = et_new_pwd.getText().toString().trim();
                    char[] charsOld = oldPwd.toCharArray();
                    char[] charsNew = newPwd.toCharArray();
                    StringBuilder oldP = new StringBuilder();
                    for (int i = charsOld.length - 1; i >= 0; i--) {
                        oldP.append(charsOld[i]);
                    }
                    StringBuilder newP = new StringBuilder();
                    for (int i = charsNew.length - 1; i >= 0; i--) {
                        newP.append(charsNew[i]);
                    }
                    mDevice.setLockCarPassword(oldP.toString(), newP.toString());
                    mList.add("修改密码");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;

            case R.id.btn_ota_boot:

                selectOta(SkateboardBleConfig.OTAType.OTA_BOOTLOADER, SkateboardBleConfig.HOST_WRITE_RETURN_METER);

                break;
            case R.id.btn_ota_app:

                selectOta(SkateboardBleConfig.OTAType.OTA_APPLICATION, SkateboardBleConfig.HOST_WRITE_RETURN_METER);
                break;

            case R.id.btn_ota_flash:

                selectOta(SkateboardBleConfig.OTAType.OTA_FLASH, SkateboardBleConfig.HOST_WRITE_RETURN_METER);
                break;


            case R.id.btn_ota_app_controller:

                selectOta(SkateboardBleConfig.OTAType.OTA_APPLICATION, SkateboardBleConfig.HOST_WRITE_RETURN_CONTROLLER);
                break;


            case R.id.btn_ble:
                mDialogList.clear();
                for (String s : FileUtils.list()) {
                    mDialogList.add(new DialogStringImageAdapter.DialogStringImageBean(s, 0));
                }
                //btn_ble
                mOTAType = 3;
                ShowListDialogFragment.newInstance().setTitle("").setCancel("", 0).setCancelBlank(true).setBackground(true).setBottom(false)
                        .setList(mDialogList).setOnDialogListener(this).show(getSupportFragmentManager());
                break;
            //OTA类型
            case R.id.btn_ota_type:
                List<DialogStringImageAdapter.DialogStringImageBean> list = new ArrayList<>();
                list.add(new DialogStringImageAdapter.DialogStringImageBean("静默升级", DfuConstants.OTA_MODE_SILENT_FUNCTION));
                list.add(new DialogStringImageAdapter.DialogStringImageBean("普通升级", DfuConstants.OTA_MODE_NORMAL_FUNCTION));
                ShowListDialogFragment.newInstance().setTitle("").setCancel("", 0).setCancelBlank(true).setBackground(true).setBottom(false)
                        .setList(list).setOnDialogListener(new ShowListDialogFragment.onDialogListener() {
                    @Override
                    public void onItemListener(int position) {
                        DialogStringImageAdapter.DialogStringImageBean dialogStringImageBean = list.get(position);
                        mRtkOtaType = (int) dialogStringImageBean.getType();
                        btn_ota_type.setText(dialogStringImageBean.getName());
                    }
                }).show(getSupportFragmentManager());
                break;


            case R.id.btn_update_conditions:
                if (mDevice != null) {
                    switch (mOtaUpdateStatus) {
                        case 0:
                            mDevice.getUpdateReadyState(SkateboardBleConfig.OTAType.OTA_BOOTLOADER, SkateboardBleConfig.HOST_READ_ONLY_METER);
                            break;
                        case 1:
                            mDevice.getUpdateReadyState(SkateboardBleConfig.OTAType.OTA_APPLICATION, SkateboardBleConfig.HOST_READ_ONLY_METER);
                            break;
                        case 2:
                            mDevice.getUpdateReadyState(SkateboardBleConfig.OTAType.OTA_FLASH, SkateboardBleConfig.HOST_READ_ONLY_METER);
                            break;
                        case 3:
                            mDevice.getUpdateReadyState(SkateboardBleConfig.OTAType.OTA_BOOTLOADER, SkateboardBleConfig.HOST_READ_ONLY_CONTROLLER);
                            break;
                        case 4:
                            mDevice.getUpdateReadyState(SkateboardBleConfig.OTAType.OTA_APPLICATION, SkateboardBleConfig.HOST_READ_ONLY_CONTROLLER);
                            break;
                        case 5:
                            mDevice.getUpdateReadyState(SkateboardBleConfig.OTAType.OTA_FLASH, SkateboardBleConfig.HOST_READ_ONLY_CONTROLLER);
                            break;
                        default:
                            mOtaUpdateStatus = -1;
                            break;
                    }
                    mOtaUpdateStatus++;


                }
                break;


            case R.id.btn_ble_version:
                if (mDevice != null) {
                    mDevice.getBleVersion();
                }
                break;
            //设置语言
            case R.id.btn_setDeviceLanguage:
                if (mDevice != null) {
                    mDevice.setDeviceLanguage(mDeviceLanguage);
                    mList.add("设置语言:" + mDeviceLanguage);
                    mDeviceLanguage++;
                    if (mDeviceLanguage > 3) {
                        if (mDeviceLanguage == 4) {
                            mDeviceLanguage = 0xFF;
                        } else {
                            mDeviceLanguage = 0;
                        }
                    }
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_getDeviceImageFontVersion:
                if (mDevice != null) {
                    mDevice.getDeviceImageFontVersion();
                    mList.add("设置语言:" + mDeviceLanguage);
                    mDeviceLanguage++;
                    if (mDeviceLanguage > 3) {
                        if (mDeviceLanguage == 4) {
                            mDeviceLanguage = 0xFF;
                        } else {
                            mDeviceLanguage = 0;
                        }
                    }
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;

            case R.id.btn_get_test_status:
                if (mDevice != null) {
                    mDevice.readProductionTest();
                    mList.add("读测试模式");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;

            case R.id.btn_go_setProductionTest:
                if (mDevice != null) {
                    mDevice.setProductionTest(2);
                    mList.add("进入整机测试模式");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;

            case R.id.btn_exit_setProductionTest:
                if (mDevice != null) {
                    mDevice.setProductionTest(3);
                    mList.add("退出整机测试模式");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_setPair:
                if (mDevice != null) {
                    mDevice.setPair();
                    mList.add("请求配对");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;
            case R.id.btn_checkPwd:
                if (mDevice != null) {
                    String newPwd = et_new_pwd.getText().toString().trim();
                    char[] chars = newPwd.toCharArray();
                    StringBuilder newP = new StringBuilder();
                    for (int i = chars.length - 1; i >= 0; i--) {
                        newP.append(chars[i]);
                    }
                    mDevice.checkPwd(newP.toString());
                    mList.add("验证密码");
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                }
                break;


        }

    }


    /**
     * 选择OTA
     *
     * @param OTAType
     * @param OTAScope
     */
    private void selectOta(int OTAType, int OTAScope) {
        mDialogList.clear();
        for (String s : FileUtils.list()) {
            mDialogList.add(new DialogStringImageAdapter.DialogStringImageBean(s, 0));
        }
        mOTAType = OTAType;
        mOTAScope = OTAScope;
        ShowListDialogFragment.newInstance().setTitle("").setCancel("", 0).setCancelBlank(true).setBackground(true).setBottom(false)
                .setList(mDialogList).setOnDialogListener(this).show(getSupportFragmentManager());
    }

    private RtkOtaManager mRtkOtaManager;

    @Override
    public void onItemListener(int position) {
        if (mDialogList.size() > position) {
            DialogStringImageAdapter.DialogStringImageBean dialogStringImageBean = mDialogList.get(position);
            String name = dialogStringImageBean.getName();
            String byFileName = FileUtils.getByFileName() + name;
            if (mOTAType == 3) {
                //ble升级
                mRtkOtaManager = RtkOtaManager.newInstance(mContext).setOtaPathSdcard(mMac, byFileName).setOnBleOTAListener(this).build();
                int stepSize = mRtkOtaManager.getStepSize();
                if (mDevice != null) {
                    mDevice.setOtaStepSize(stepSize);
                }
                int step = -1;
                try {
                    String trim = et_ble_ota_step.getText().toString().trim();
                    step = Integer.parseInt(trim);
                    if (step >= stepSize) {
                        step = -1;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                mList.add("OTA包中包含:" + stepSize + "个小包\n现在升级" + ((step >= 0) ? step + "包" : "全部包"));
                mHandler.sendEmptyMessage(REFRESH_DATA);

                mRtkOtaManager.startOta(mRtkOtaType, step);
            } else if (mDevice != null) {

                mDevice.setOnBleOTAListener(SkateboardDataActivity.this);
                mDevice.setUpdateData(byFileName, mOTAType, mOTAScope);
            }
        }
    }


    @Override
    protected void initData() {
        mMac = getIntent().getStringExtra("mac");
        mList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        mListviewData.setAdapter(listAdapter);
        FileUtils.init();
        mDialogList = new ArrayList<>();
        RtkConfigure configure = new RtkConfigure.Builder().debugEnabled(BuildConfig.DEBUG).printLog(true).logTag("OTA").build();
        RtkCore.initialize(this, configure);
        RtkDfu.initialize(this, BuildConfig.DEBUG);
        ZLogger.initialize("AILink", BuildConfig.DEBUG);
    }

    @Override
    protected void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onServiceSuccess() {
        if (mBluetoothService != null) {
            mBluetoothService.setOnCallback(this);
            mBluetoothService.setOnScanFilterListener(null);
            BleDevice bleDevice = mBluetoothService.getBleDevice(mMac);
            if (bleDevice == null) {
                mBluetoothService.scanLeDevice(0, SkateboardBleConfig.UUID_BROADCAST, BleConfig.UUID_SERVER_AILINK);
            } else {
                onServicesDiscovered(mMac);
            }
        }

    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }

    @Override
    public void onStartScan() {
        mList.add("开始搜索连接");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    public void onScanning(BleValueBean data) {
        if (data.getMac().equalsIgnoreCase(mMac)) {
            connectBle(data.getMac());
        }
    }


    @Override
    public void onServicesDiscovered(String mac) {
        if (mBluetoothService != null) {
            BleDevice bleDevice = mBluetoothService.getBleDevice(mac);
            mDevice = SkateboardDevice.getInstance(bleDevice);
            bleDevice.setOnBleHandshakeListener(this);
            mDevice.setOnNotifyData(this);
            mList.add("连接成功:" + mac);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

    }


    @Override
    public void onScanTimeOut() {

    }


    @Override
    public void bleClose() {
        mList.add("蓝牙已关闭");
        mHandler.sendEmptyMessage(REFRESH_DATA);
        mDevice = null;

    }

    @Override
    public void onDisConnected(String mac, int code) {
        mList.add("断开连接:" + mac + " ||" + code);
        mHandler.sendEmptyMessage(REFRESH_DATA);
        if (mOTAType != SkateboardBleConfig.OTAType.OTA_BLE) {
            if (code != 0) {
                if (mDevice != null) {
                    mHandler.postDelayed(() -> {
                        mBluetoothService.connectDevice(mMac);
                    }, 1000);
                }
            }
        } else {
            mBluetoothService.deviceConnectListener(mMac, true);
        }
        mDevice = null;
    }

    @Override
    public void bleOpen() {
        mList.add("蓝牙已开启");
        mHandler.sendEmptyMessage(REFRESH_DATA);
        if (mBluetoothService != null) {
            mBluetoothService.scanLeDevice(0, SkateboardBleConfig.UUID_BROADCAST, BleConfig.UUID_SERVER_AILINK);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDevice != null) {
            mDevice.disconnect();
        }

    }


    @Override
    public void onData(byte[] data) {
        String dataStr = MyBleStrUtils.byte2HexStr(data);
        mList.add("接收到的数据:" + dataStr);

        if (data.length > 5) {
            int two = data[2] & 0xFF;
            int three = data[3] & 0xFF;
            if ((two == 0x01 && three == 0x01)) {
                int voltage = data[5] & 0xFF;
                mList.add("电量:" + voltage + "%");
            } else if ((two == 0x11 && three == 0x11)) {
                byte[] bytes = new byte[4];
                if (data.length > 9 + bytes.length) {
                    System.arraycopy(data, 9, bytes, 0, bytes.length);
                    float hardwareMax = ((bytes[0] & 0xFF) * 100 + (bytes[1] & 0xFF)) / 100F;
                    float softwareMax = ((bytes[2] & 0xFF) * 100 + (bytes[3] & 0xFF)) / 100F;
                    mList.add("软硬件版本:" + (hardwareMax) + "_" + (softwareMax));
                }
            } else if ((two == 0x16 && three == 0x16)) {
                byte[] flashByte = new byte[4];
                if (data.length > 9 + flashByte.length) {
                    System.arraycopy(data, 9, flashByte, 0, flashByte.length);
                    String year = String.valueOf(((flashByte[0] & 0xFF)) + 2000);
                    String month = String.valueOf((flashByte[1] & 0xFF));
                    String day = String.valueOf((flashByte[2] & 0xFF));
                    int version = flashByte[3] & 0xFF;
                    mList.add("FLASH版本号:" + year + "_" + month + "_" + day + "_" + version);
                }
            } else if ((two == 0x17 && three == 0x17)) {
                byte[] bytes = new byte[4];
                if (data.length > 9 + bytes.length) {
                    System.arraycopy(data, 9, bytes, 0, bytes.length);
                    String versionHardware = String.valueOf(((bytes[0] & 0xFF) * 100 + (bytes[1] & 0xFF)) / 100F);
                    String versionSoftware = String.valueOf(((bytes[2] & 0xFF) * 100 + (bytes[3] & 0xFF)) / 100F);
                    mList.add("硬件版本:" + versionHardware + "\n软件版本:" + versionSoftware);
                }
            } else if ((two == 0xA9 && three == 0xA9)) {
                byte[] bytes = new byte[6];
                System.arraycopy(data, 5, bytes, 0, bytes.length);
                String decrypt = decrypt(bytes);
                mList.add("售后密码:" + MyBleStrUtils.byte2HexStr(bytes));
                mList.add("原始密码:" + decrypt);
            } else if ((two == 0x33 && three == 0x33)) {
                byte[] bytes = new byte[8];
                System.arraycopy(data, 5, bytes, 0, bytes.length);
                String imageV = (bytes[0] & 0xFF) + "." + (bytes[1] & 0xFF) + "." + (bytes[2] & 0xFF) + "." + (bytes[3] & 0xFF);
                String fontV = (bytes[4] & 0xFF) + "." + (bytes[5] & 0xFF) + "." + (bytes[6] & 0xFF) + "." + (bytes[7] & 0xFF);
                mList.add("图片V:" + imageV + "\n字库V:" + fontV);
            } else if ((two == 0x36 && three == 0x36)) {
                byte[] bytes = new byte[5];
                System.arraycopy(data, 5, bytes, 0, bytes.length);
                String tpV =
                        (bytes[0] & 0xFF) + "." + (bytes[1] & 0xFF) + "." + (bytes[2] & 0xFF) + "." + (bytes[3] & 0xFF) + "." + (bytes[4] & 0xFF);
                mList.add("TP_V:" + tpV);
            } else if ((two == 0x32 && three == 0x32)) {
                byte datum = data[5];
                int start = datum & 0x07;
                int result = (datum >> 3) & 0x01;
                int status = (datum >> 4) & 0x03;
                String showData = "";
                switch (start) {
                    case 0:
                        showData += "无";
                        break;
                    case 1:
                        showData += "进入生产测试模式";
                        break;
                    case 2:
                        showData += "进入整机测试模式";
                        break;
                    case 3:
                        showData += "退出测试模式";
                        break;
                }
                if (result == 0) {
                    showData += "\nPCBA未通过";
                } else {
                    showData += "\nPCBA已通过";
                }
                switch (status) {
                    case 0:
                        showData += "\n正在测试";
                        break;
                    case 1:
                        showData += "\n测试成功";
                        break;


                }
                mList.add("生产测试:" + showData);
            }
        }
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    public void onOtaSuccess() {
        mList.add("OTA进度:100%");
        mList.add("OTA成功");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onOtaFailure(int cmd, String err) {
        mList.add("OTA失败");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    private int mProgress = -1;

    @Override
    public void onOtaProgress(float progress, int currentCount, int maxCount) {
        if (mProgress != progress) {
            mProgress = (int) progress;
            mList.add("OTA进度:" + progress + "%   " + currentCount + "/" + maxCount);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }
    }

    @Override
    public void onOtaStatus(int status) {
        String message = getString(getProgressStateResId(status));
        mList.add("OTA状态:" + message + "  ||state:" + status);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    public static int getProgressStateResId(int var0) {
        if (var0 != 527) {
            switch (var0) {
                case 257:
                    return R.string.rtk_dfu_progress_state_origin;
                case 258:
                    return R.string.rtk_dfu_state_image_active_success;
                case 259:
                    return R.string.rtk_dfu_state_aborted;
                case 260:
                    return R.string.rtk_dfu_state_error_processing;
                default:
                    switch (var0) {
                        case 513:
                            return R.string.rtk_dfu_state_initialize;
                        case 514:
                            return R.string.rtk_dfu_state_start;
                        case 515:
                        case 519:
                            return R.string.rtk_dfu_state_find_ota_remote;
                        case 516:
                        case 520:
                            return R.string.rtk_dfu_state_connect_ota_remote;
                        case 517:
                            return R.string.rtk_dfu_state_prepare_dfu_processing;
                        case 518:
                            return R.string.rtk_dfu_state_remote_enter_ota;
                        case 521:
                            return R.string.rtk_dfu_state_start_ota_processing;
                        case 522:
                            return R.string.rtk_dfu_state_hand_over_processing;
                        case 523:
                            return R.string.rtk_dfu_state_pending_active_image;
                        case 524:
                            return R.string.rtk_dfu_state_start_active_image;
                        case 525:
                            return R.string.rtk_dfu_state_abort_processing;
                        default:
                            return R.string.rtk_dfu_state_known;
                    }
            }
        } else {
            return R.string.rtk_dfu_state_scan_secondary_bud;
        }
    }


    @Override
    public void onWriteData(byte[] data) {
        String dataStr = MyBleStrUtils.byte2HexStr(data);
        L.i("写入的数据:" + dataStr);
        mList.add("写入的数据:" + dataStr);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onHandshake(boolean status) {
        mList.add(TimeUtils.getTime() + "握手:" + status);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    private static String decrypt(byte[] src) {
        final int[] k = {0x6E616863, 0x6F687A67, 0x61745F75, 0x6F61746F};
        char[] dst = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        int[] TmpBuf = new int[src.length];
        int[] TmpBuf2 = new int[TmpBuf.length];
        for (int i = 0; i < src.length; i++) {
            TmpBuf[i]=src[i];
        }
        int n = 0;
        while (n < 32) {
            for (int i = 0; i < TmpBuf.length; i++) {
                TmpBuf[i] = (TmpBuf[i] - ((k[(n + 2) % 4] >> ((n + 5) % 24)) & 0xFF)) & 0xFF;
                TmpBuf[i] = (TmpBuf[i] ^ ((k[n % 4] >> (n % 24)) & 0xFF)) & 0xFF;
                TmpBuf[i] = (TmpBuf[i] - i) & 0xFF;
                TmpBuf[i] = (TmpBuf[i] ^ n) & 0xFF;
            }
            n++;
        }

        int a = 0x5a;
        for (int i = 0; i < TmpBuf.length; i++) {
            TmpBuf2[i] = (TmpBuf[i] ^ a) & 0xFF;
            a = (a + TmpBuf2[i]) & 0xFF;
        }
        //因为协议售后密码是按照6-1顺序传过来，所以解密后的密码要反一下
        for (int i = 0; i < TmpBuf2.length; i++) {
            dst[i] = (char) TmpBuf2[5 - i];
        }

        return String.copyValueOf(dst);

    }


}

package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.ailinkScooter.AilinkScooterBleData;

public class AiLinkScooterActivity extends BleBaseActivity implements View.OnClickListener, OnCallbackBle, AilinkScooterBleData.ScooterListener {
    private Button btn_support_function, btn_hear, btn_unit_kg, btn_unit_mi, btn_add_gear, btn_cut_gear, btn_zero_start, btn_set_charge_time,
            btn_un_zero_start, btn_cruise_open, btn_cruise_close, btn_clear_total_mileage, btn_clear_single_mileage_time, btn_reset, btn_clear_other, btn_left_light, btn_right_light,
            btn_atmosphere_light, btn_day_light, btn_brake_light, btn_head_light, btn_set_password, btn_unlock, btn_lock, btn_check_password, btn_boot, btn_shutdown, btn_after_password, btn_auto_bg_light, btn_bg_light_100, btn_bg_light_0, btn_shutdown_unlock,
            btn_check_shutdown_time, btn_check_single_use_time, btn_check_battery_info, btn_check_total_mileage, btn_check_battery_no, btn_check_controller_no, btn_check_meter_version, btn_check_charge_time, btn_set_charge_capacity, btn_read_charge_capacity, btn_find_car_open, btn_find_car_close, btn_auto_lock_open, btn_auto_lock_close, btn_auto_unlock_open, btn_auto_unlock_close,
            btn_move_warm_open, btn_move_warm_close, btn_collision_warm_open, btn_collision_warm_close, btn_read_warm_auto_lock, btn_find_car, btn_navigation_open, btn_navigation_close;

    private ListView lv_log;
    private List<String> logList;
    private ArrayAdapter listAdapter;
    private String mAddress;
    private EditText et_password, et_charge_start_time, et_charge_end_time, et_charge_capacity;

    private int currentGear;

    private int unlockShutTime = 300;
    private int lockShutTime = 180;

    private CheckBox cb_monday, cb_tuesday, cb_wednesday, cb_thursday, cb_friday, cb_saturday, cb_sunday;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAddress = getIntent().getStringExtra("mac");
        setContentView(R.layout.activity_ailink_scooter);
        findView();
        setListener();
        logList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, logList);
        lv_log.setAdapter(listAdapter);
    }

    private void findView() {
        lv_log = findViewById(R.id.lv_log);
        btn_support_function = findViewById(R.id.btn_support_function);
        btn_hear = findViewById(R.id.btn_hear);
        btn_unit_kg = findViewById(R.id.btn_unit_kg);
        btn_unit_mi = findViewById(R.id.btn_unit_mi);
        btn_add_gear = findViewById(R.id.btn_add_gear);
        btn_cut_gear = findViewById(R.id.btn_cut_gear);
        btn_zero_start = findViewById(R.id.btn_zero_start);
        btn_un_zero_start = findViewById(R.id.btn_un_zero_start);
        btn_cruise_open = findViewById(R.id.btn_cruise_open);
        btn_cruise_close = findViewById(R.id.btn_cruise_close);
        btn_clear_total_mileage = findViewById(R.id.btn_clear_total_mileage);
        btn_clear_single_mileage_time = findViewById(R.id.btn_clear_single_mileage_time);
        btn_reset = findViewById(R.id.btn_reset);
        btn_clear_other = findViewById(R.id.btn_clear_other);
        btn_left_light = findViewById(R.id.btn_left_light);
        btn_right_light = findViewById(R.id.btn_right_light);
        btn_atmosphere_light = findViewById(R.id.btn_atmosphere_light);
        btn_day_light = findViewById(R.id.btn_day_light);
        btn_brake_light = findViewById(R.id.btn_brake_light);
        btn_head_light = findViewById(R.id.btn_head_light);
        btn_set_password = findViewById(R.id.btn_set_password);
        btn_unlock = findViewById(R.id.btn_unlock);
        btn_lock = findViewById(R.id.btn_lock);
        btn_check_password = findViewById(R.id.btn_check_password);
        btn_boot = findViewById(R.id.btn_boot);
        btn_shutdown = findViewById(R.id.btn_shutdown);
        btn_after_password = findViewById(R.id.btn_after_password);
        btn_auto_bg_light = findViewById(R.id.btn_auto_bg_light);
        btn_bg_light_100 = findViewById(R.id.btn_bg_light_100);
        btn_bg_light_0 = findViewById(R.id.btn_bg_light_0);
        btn_shutdown_unlock = findViewById(R.id.btn_shutdown_unlock);
        btn_check_shutdown_time = findViewById(R.id.btn_check_shutdown_time);
        btn_check_single_use_time = findViewById(R.id.btn_check_single_use_time);
        btn_check_battery_info = findViewById(R.id.btn_check_battery_info);
        btn_check_total_mileage = findViewById(R.id.btn_check_total_mileage);
        btn_check_battery_no = findViewById(R.id.btn_check_battery_no);
        btn_check_controller_no = findViewById(R.id.btn_check_controller_no);
        btn_check_meter_version = findViewById(R.id.btn_check_meter_version);
        btn_check_charge_time = findViewById(R.id.btn_check_charge_time);
        btn_set_charge_capacity = findViewById(R.id.btn_set_charge_capacity);
        btn_read_charge_capacity = findViewById(R.id.btn_read_charge_capacity);
        btn_find_car_open = findViewById(R.id.btn_find_car_open);
        btn_find_car_close = findViewById(R.id.btn_find_car_close);
        btn_auto_lock_open = findViewById(R.id.btn_auto_lock_open);
        btn_auto_lock_close = findViewById(R.id.btn_auto_lock_close);
        btn_auto_unlock_open = findViewById(R.id.btn_auto_unlock_open);
        btn_auto_unlock_close = findViewById(R.id.btn_auto_unlock_close);
        btn_move_warm_open = findViewById(R.id.btn_move_warm_open);
        btn_move_warm_close = findViewById(R.id.btn_move_warm_close);
        btn_collision_warm_open = findViewById(R.id.btn_collision_warm_open);
        btn_collision_warm_close = findViewById(R.id.btn_collision_warm_close);
        btn_read_warm_auto_lock = findViewById(R.id.btn_read_warm_auto_lock);
        btn_find_car = findViewById(R.id.btn_find_car);
        btn_navigation_open = findViewById(R.id.btn_navigation_open);
        btn_navigation_close = findViewById(R.id.btn_navigation_close);
        et_password = findViewById(R.id.et_password);
        cb_monday = findViewById(R.id.cb_monday);
        cb_tuesday = findViewById(R.id.cb_tuesday);
        cb_wednesday = findViewById(R.id.cb_wednesday);
        cb_thursday = findViewById(R.id.cb_thursday);
        cb_friday = findViewById(R.id.cb_friday);
        cb_saturday = findViewById(R.id.cb_saturday);
        cb_sunday = findViewById(R.id.cb_sunday);
        et_charge_start_time = findViewById(R.id.et_charge_start_time);
        et_charge_end_time = findViewById(R.id.et_charge_end_time);
        btn_set_charge_time = findViewById(R.id.btn_set_charge_time);
        et_charge_capacity = findViewById(R.id.et_charge_capacity);
    }

    private void setListener() {
        btn_support_function.setOnClickListener(this);
        btn_hear.setOnClickListener(this);
        btn_unit_kg.setOnClickListener(this);
        btn_unit_mi.setOnClickListener(this);
        btn_add_gear.setOnClickListener(this);
        btn_cut_gear.setOnClickListener(this);
        btn_zero_start.setOnClickListener(this);
        btn_un_zero_start.setOnClickListener(this);
        btn_cruise_open.setOnClickListener(this);
        btn_cruise_close.setOnClickListener(this);
        btn_clear_total_mileage.setOnClickListener(this);
        btn_clear_single_mileage_time.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        btn_clear_other.setOnClickListener(this);
        btn_left_light.setOnClickListener(this);
        btn_right_light.setOnClickListener(this);
        btn_atmosphere_light.setOnClickListener(this);
        btn_day_light.setOnClickListener(this);
        btn_brake_light.setOnClickListener(this);
        btn_head_light.setOnClickListener(this);
        btn_set_password.setOnClickListener(this);
        btn_unlock.setOnClickListener(this);
        btn_lock.setOnClickListener(this);
        btn_check_password.setOnClickListener(this);
        btn_boot.setOnClickListener(this);
        btn_shutdown.setOnClickListener(this);
        btn_after_password.setOnClickListener(this);
        btn_auto_bg_light.setOnClickListener(this);
        btn_bg_light_100.setOnClickListener(this);
        btn_bg_light_0.setOnClickListener(this);
        btn_shutdown_unlock.setOnClickListener(this);

        btn_check_shutdown_time.setOnClickListener(this);
        btn_check_single_use_time.setOnClickListener(this);
        btn_check_battery_info.setOnClickListener(this);
        btn_check_total_mileage.setOnClickListener(this);
        btn_check_battery_no.setOnClickListener(this);
        btn_check_controller_no.setOnClickListener(this);
        btn_check_meter_version.setOnClickListener(this);
        btn_check_charge_time.setOnClickListener(this);
        btn_set_charge_capacity.setOnClickListener(this);
        btn_read_charge_capacity.setOnClickListener(this);
        btn_find_car_open.setOnClickListener(this);
        btn_find_car_close.setOnClickListener(this);
        btn_auto_lock_open.setOnClickListener(this);
        btn_auto_lock_close.setOnClickListener(this);
        btn_auto_unlock_open.setOnClickListener(this);
        btn_auto_unlock_close.setOnClickListener(this);
        btn_move_warm_open.setOnClickListener(this);
        btn_move_warm_close.setOnClickListener(this);
        btn_collision_warm_open.setOnClickListener(this);
        btn_collision_warm_close.setOnClickListener(this);
        btn_read_warm_auto_lock.setOnClickListener(this);
        btn_find_car.setOnClickListener(this);
        btn_navigation_open.setOnClickListener(this);
        btn_navigation_close.setOnClickListener(this);
        btn_set_charge_time.setOnClickListener(this);
    }

    @Override
    public void onServiceSuccess() {
        mBluetoothService.setOnCallback(this);
        logList.add(0,"绑定服务成功");
        if (mBluetoothService != null) {
            mBluetoothService.setOnCallback(this);
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                AilinkScooterBleData.init(bleDevice);
                AilinkScooterBleData.getInstance().setScooterListener(this);

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
    public void onDisConnected(String mac, int code) {
        if (mac.equalsIgnoreCase(mAddress)) {
            logList.add(0,"连接断开");
            listAdapter.notifyDataSetChanged();
        }
    }

    private boolean isOpenR;
    private boolean isOpenL;
    private boolean isAtmosphere;
    private boolean isDay;
    private boolean isBrake;
    private boolean isHead;

    @Override
    public void onClick(View v) {
        if(AilinkScooterBleData.getInstance()==null)return;
        switch (v.getId()) {
            case R.id.btn_support_function:
                logList.add(0,"查询支持的功能");
                listAdapter.notifyDataSetChanged();
                AilinkScooterBleData.getInstance().getSupportedFunction();
                break;
            case R.id.btn_hear:
                logList.add(0,"心跳数据交互");
                listAdapter.notifyDataSetChanged();
                AilinkScooterBleData.getInstance().sendHearData();
                break;
            case R.id.btn_unit_kg:
                logList.add(0,"设置公制单位(结果在心跳包中看)");
                listAdapter.notifyDataSetChanged();
                AilinkScooterBleData.getInstance().sendUnitData(0);
                break;
            case R.id.btn_unit_mi:
                logList.add(0,"设置英制单位(结果在心跳包中看)");
                listAdapter.notifyDataSetChanged();
                AilinkScooterBleData.getInstance().sendUnitData(1);
                break;
            case R.id.btn_add_gear:
                currentGear++;
                if (currentGear > 7) currentGear = 7;
                logList.add(0,"设置档位(结果在心跳包中看)：" + currentGear);
                listAdapter.notifyDataSetChanged();
                AilinkScooterBleData.getInstance().sendGearData(currentGear);
                break;
            case R.id.btn_cut_gear:
                currentGear--;
                if (currentGear < 0) currentGear = 0;
                logList.add(0,"设置档位(结果在心跳包中看)：" + currentGear);
                listAdapter.notifyDataSetChanged();
                AilinkScooterBleData.getInstance().sendGearData(currentGear);
                break;
            case R.id.btn_zero_start:
                logList.add(0,"零启动(结果在心跳包中看)");
                listAdapter.notifyDataSetChanged();
                AilinkScooterBleData.getInstance().sendStartModeData(0);
                break;
            case R.id.btn_un_zero_start:
                logList.add(0,"非零启动(结果在心跳包中看)");
                listAdapter.notifyDataSetChanged();
                AilinkScooterBleData.getInstance().sendStartModeData(1);
                break;
            case R.id.btn_cruise_open:
                logList.add(0,"开启巡航(结果在心跳包中看)");
                listAdapter.notifyDataSetChanged();
                AilinkScooterBleData.getInstance().sendCruiseControlFunctionData(1);

                break;
            case R.id.btn_cruise_close:
                logList.add(0,"关闭巡航(结果在心跳包中看)");
                listAdapter.notifyDataSetChanged();
                AilinkScooterBleData.getInstance().sendCruiseControlFunctionData(0);
                break;
            case R.id.btn_clear_total_mileage:
                logList.add(0,"清除总里程(结果在心跳包中看)");
                listAdapter.notifyDataSetChanged();
                AilinkScooterBleData.getInstance().sendClearData(0x02);


                break;
            case R.id.btn_clear_single_mileage_time:
                logList.add(0,"清除单次里程时间(结果在心跳包中看)");
                listAdapter.notifyDataSetChanged();
                AilinkScooterBleData.getInstance().sendClearData(0x01);

                break;
            case R.id.btn_reset:
                logList.add(0,"清除单次里程时间(结果在心跳包中看)");
                listAdapter.notifyDataSetChanged();
                AilinkScooterBleData.getInstance().sendClearData(0x80);
                break;
            case R.id.btn_clear_other:
                logList.add(0,"清除除里程和时间外的其它控制器及仪表参数");
                listAdapter.notifyDataSetChanged();
                AilinkScooterBleData.getInstance().sendClearData(0x04);
                break;
            case R.id.btn_left_light:
                if (isOpenR) {
                    isOpenR = false;
                    logList.add(0,"右转灯开");
                    AilinkScooterBleData.getInstance().sendLightOpenData(0x20);
                } else {
                    isOpenR = true;
                    logList.add(0,"右转灯关");
                    AilinkScooterBleData.getInstance().sendLightCloseData(0x20);
                }
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_right_light:
                if (isOpenL) {
                    isOpenL = false;
                    logList.add(0,"左转灯开");
                    AilinkScooterBleData.getInstance().sendLightOpenData(0x10);
                } else {
                    isOpenL = true;
                    logList.add(0,"左转灯关");
                    AilinkScooterBleData.getInstance().sendLightCloseData(0x10);
                }
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_atmosphere_light:
                if (isAtmosphere) {
                    isAtmosphere = false;
                    logList.add(0,"氛围灯开");
                    AilinkScooterBleData.getInstance().sendLightOpenData(0x08);
                } else {
                    isAtmosphere = true;
                    logList.add(0,"氛围灯关");
                    AilinkScooterBleData.getInstance().sendLightCloseData(0x08);
                }
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_day_light:
                if (isDay) {
                    isDay = false;
                    logList.add(0,"日行灯开");
                    AilinkScooterBleData.getInstance().sendLightOpenData(0x04);
                } else {
                    isDay = true;
                    logList.add(0,"日行灯关");
                    AilinkScooterBleData.getInstance().sendLightCloseData(0x04);
                }
                listAdapter.notifyDataSetChanged();

                break;
            case R.id.btn_brake_light:
                if (isBrake) {
                    isBrake = false;
                    logList.add(0,"刹车灯开");
                    AilinkScooterBleData.getInstance().sendLightOpenData(0x02);
                } else {
                    isBrake = true;
                    logList.add(0,"刹车灯关");
                    AilinkScooterBleData.getInstance().sendLightCloseData(0x02);
                }
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_head_light:
                if (isHead) {
                    isHead = false;
                    logList.add(0,"前车灯开");
                    AilinkScooterBleData.getInstance().sendLightOpenData(0x01);
                } else {
                    isHead = true;
                    logList.add(0,"前车灯关");
                    AilinkScooterBleData.getInstance().sendLightCloseData(0x01);
                }
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_set_password:
                String oldPassword = et_password.getText().toString();
                if (oldPassword.isEmpty()) {
                    logList.add(0,"请输入原始密码");
                } else {
                    logList.add(0,"修改密码");
                    AilinkScooterBleData.getInstance().changeLockCarPassword(oldPassword, "123456");
                }
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_unlock:
                String password = et_password.getText().toString();
                if (password.isEmpty()) {
                    logList.add(0,"请输入密码");
                } else {
                    logList.add(0,"解锁");
                    AilinkScooterBleData.getInstance().setLockCarPassword(0, password);
                }
                listAdapter.notifyDataSetChanged();

                break;
            case R.id.btn_lock:
                logList.add(0,"锁车");
                AilinkScooterBleData.getInstance().setLockCarPassword(1, "000000");
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_check_password:
                String checkPassword = et_password.getText().toString();
                if (checkPassword.isEmpty()) {
                    logList.add(0,"请输入密码");
                } else {
                    logList.add(0,"验证密码");
                    AilinkScooterBleData.getInstance().setLockCarPassword(2, checkPassword);
                }

                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_boot:
                String boot = et_password.getText().toString();
                if (boot.isEmpty()) {
                    logList.add(0,"请输入密码");
                } else {
                    logList.add(0,"验证密码");
                    AilinkScooterBleData.getInstance().setLockCarPassword(3, boot);
                }

                listAdapter.notifyDataSetChanged();

                break;
            case R.id.btn_shutdown:
                logList.add(0,"锁车");
                AilinkScooterBleData.getInstance().setLockCarPassword(4, "000000");
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_after_password:
                logList.add(0,"获取售后密码");
                AilinkScooterBleData.getInstance().readServicePassword();
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_auto_bg_light:
                logList.add(0,"开启自动背光");
                AilinkScooterBleData.getInstance().sendBgAuto();
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_bg_light_100:
                logList.add(0,"开启手动背光：100");
                AilinkScooterBleData.getInstance().sendBgUnAuto(100);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_bg_light_0:
                logList.add(0,"开启手动背光：0");
                AilinkScooterBleData.getInstance().sendBgUnAuto(0);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_shutdown_unlock:
                logList.add(0,"设置自动关机");
                AilinkScooterBleData.getInstance().sendAutoShutDownTime(unlockShutTime, lockShutTime);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_check_shutdown_time:
                logList.add(0,"查询自动关机时间");
                AilinkScooterBleData.getInstance().sendGetShutDownTime();
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_check_single_use_time:

                logList.add(0,"查询单次行驶里程时间");
                AilinkScooterBleData.getInstance().sendSingleMileageTime();
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_check_battery_info:
                logList.add(0,"查询总里程");
                AilinkScooterBleData.getInstance().sendTotalMileage();
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_check_total_mileage:
                logList.add(0,"查询电池信息");
                AilinkScooterBleData.getInstance().sendBatteryInfo();
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_check_battery_no:
                logList.add(0,"查询电池厂商代码及编号");
                AilinkScooterBleData.getInstance().sendBatteryNo();
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_check_controller_no:
                logList.add(0,"查询控制器厂商代码及编号");
                AilinkScooterBleData.getInstance().sendControllerNo();
                listAdapter.notifyDataSetChanged();

                break;
            case R.id.btn_check_meter_version:
                logList.add(0,"查询仪表固件版本");
                AilinkScooterBleData.getInstance().sendMeterVersion();
                listAdapter.notifyDataSetChanged();

                break;
            case R.id.btn_check_charge_time:
                logList.add(0,"读取充电时间");
                AilinkScooterBleData.getInstance().sendCheckChargeTime();
                listAdapter.notifyDataSetChanged();
                break;

            case R.id.btn_set_charge_time:
                String startTime = et_charge_start_time.getText().toString().trim();
                String endTime = et_charge_end_time.getText().toString().trim();
                if (startTime.isEmpty() || endTime.isEmpty() || startTime.contains(":") || endTime.contains(":")) {
                    logList.add(0,"输入正确的时间和格式");
                    return;
                }
                int monday = cb_monday.isChecked() ? 1 : 0;
                int tuesday = cb_tuesday.isChecked() ? 1 : 0;
                int wednesday = cb_wednesday.isChecked() ? 1 : 0;
                int thursday = cb_thursday.isChecked() ? 1 : 0;
                int friday = cb_friday.isChecked() ? 1 : 0;
                int saturday = cb_saturday.isChecked() ? 1 : 0;
                int sunday = cb_sunday.isChecked() ? 1 : 0;

                int repeat = 1;
                StringBuffer stringBuffer = new StringBuffer();
                if (monday == 1 || tuesday == 1 || wednesday == 1 || thursday == 1 || friday == 1 || saturday == 1 || sunday == 1) {
                    repeat = 0;
                    stringBuffer.append("设置充电时间:重复:");
                    stringBuffer.append(monday == 1 ? "周一 " : "");
                    stringBuffer.append(tuesday == 1 ? "周二 " : "");
                    stringBuffer.append(wednesday == 1 ? "周三 " : "");
                    stringBuffer.append(thursday == 1 ? "周四 " : "");
                    stringBuffer.append(friday == 1 ? "周五 " : "");
                    stringBuffer.append(saturday == 1 ? "周六 " : "");
                    stringBuffer.append(sunday == 1 ? "周日 " : "");
                } else {
                    stringBuffer.append("设置充电时间:不重复");
                }
                String[] startTimes = startTime.split(":");
                String[] endTimes = startTime.split(":");
                logList.add(0,stringBuffer.toString());
                try {
                    AilinkScooterBleData.getInstance().sendChargeTime(repeat, monday, tuesday, wednesday, thursday, friday, saturday, sunday,
                            Integer.parseInt(startTimes[0]), Integer.parseInt(startTimes[1]), Integer.parseInt(endTimes[0]), Integer.parseInt(endTimes[1]));
                } catch (NumberFormatException e) {
                    logList.add(0,"出现异常。时间格式解析错误");
                    e.printStackTrace();
                }
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_set_charge_capacity:
                String capacity = et_charge_capacity.getText().toString().trim();
                if (capacity.isEmpty()) {
                    logList.add(0,"请填写容量");
                    return;
                }
                int percentage = 0;
                try {
                    percentage = Integer.parseInt(capacity);
                    logList.add(0,"充电容量:" + percentage);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    logList.add(0,"请填数据啊");
                }
                AilinkScooterBleData.getInstance().sendSetChargeCapacity(percentage);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_read_charge_capacity:
                logList.add(0,"读取充电容量");
                AilinkScooterBleData.getInstance().sendGetChargeCapacity();
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_find_car_open:
                logList.add(0,"打开寻车功能");
                AilinkScooterBleData.getInstance().sendFindCarFunction(1);
                listAdapter.notifyDataSetChanged();

                break;
            case R.id.btn_find_car_close:
                logList.add(0,"关闭寻车功能");
                AilinkScooterBleData.getInstance().sendFindCarFunction(0);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_auto_lock_open:

                logList.add(0,"打开自动锁车");
                AilinkScooterBleData.getInstance().sendAutoLockCar(1);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_auto_lock_close:
                logList.add(0,"关闭自动锁车");
                AilinkScooterBleData.getInstance().sendAutoLockCar(0);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_auto_unlock_open:
                logList.add(0,"打开自动解锁");
                AilinkScooterBleData.getInstance().sendAutoUnLockCar(1);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_auto_unlock_close:
                logList.add(0,"关闭自动解锁");
                AilinkScooterBleData.getInstance().sendAutoUnLockCar(0);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_move_warm_open:
                logList.add(0,"打开移位报警");
                AilinkScooterBleData.getInstance().sendMoveWarm(1);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_move_warm_close:
                logList.add(0,"关闭移位报警");
                AilinkScooterBleData.getInstance().sendMoveWarm(0);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_collision_warm_open:
                logList.add(0,"打开碰撞报警");
                AilinkScooterBleData.getInstance().sendCollisionWarm(1);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_collision_warm_close:
                logList.add(0,"关闭碰撞报警");
                AilinkScooterBleData.getInstance().sendCollisionWarm(0);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_read_warm_auto_lock:
                logList.add(0,"读取车辆报警、自动锁");
                AilinkScooterBleData.getInstance().sendCarWarmAndLock();
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_find_car:

                logList.add(0,"寻车");
                AilinkScooterBleData.getInstance().sendFindCar();
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_navigation_open:
                logList.add(0,"导航:向左转弯,当前方向剩余500米，总剩余路程1000米");
                AilinkScooterBleData.getInstance().sendNavigation(1,2,500,1000);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_navigation_close:
                logList.add(0,"关闭导航");
                AilinkScooterBleData.getInstance().sendNavigation(0,0,0,0);
                listAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onSupportFunction(byte[] payload, int MeterBridgeOrBlePayload, int autoBgLight, int unAutoBgLight,
                                  int chargeManage, int chargeCapacityManage, int navigation, int weatherPush,
                                  int msgPush, int cruiseControl, int cruiseControlSwitch, int zeroOrUnzeroStart,
                                  int boostMode, int appSwitchMachine, int controllerNo,
                                  int batteryInfo, int batteryNo, int rightLight, int leftLight,
                                  int ambientLight, int dayLight, int brakeLight, int headingLight,
                                  int findCar, int autoLock, int autoUnLock, int moveWarm, int collisionWarm) {
        logList.add(0,"payload收：" + BleStrUtils.byte2HexStr(payload));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(MeterBridgeOrBlePayload == 0 ? "仪表桥接" : "BLE透传");
        stringBuffer.append("  自动背光:");
        stringBuffer.append(supportOrUnSupport(autoBgLight));
        stringBuffer.append("  手动背光:");
        stringBuffer.append(supportOrUnSupport(unAutoBgLight));
        stringBuffer.append("  充电时间管理:");
        stringBuffer.append(supportOrUnSupport(chargeManage));
        stringBuffer.append("  充电容量管理:");
        stringBuffer.append(supportOrUnSupport(chargeCapacityManage));
        stringBuffer.append("  导航数据:");
        stringBuffer.append(supportOrUnSupport(navigation));
        stringBuffer.append("  天气推送:");
        stringBuffer.append(supportOrUnSupport(weatherPush));
        stringBuffer.append("  消息推送:");
        stringBuffer.append(supportOrUnSupport(msgPush));
        stringBuffer.append("  定速巡航功能:");
        stringBuffer.append(supportOrUnSupport(cruiseControl));
        stringBuffer.append("  定速巡航开关:");
        stringBuffer.append(supportOrUnSupport(cruiseControlSwitch));
        stringBuffer.append("  零启动、非零启动切换:");
        stringBuffer.append(supportOrUnSupport(zeroOrUnzeroStart));
        stringBuffer.append("  助力模式:");
        stringBuffer.append(supportOrUnSupport(boostMode));
        stringBuffer.append("  APP开关机:");
        stringBuffer.append(supportOrUnSupport(appSwitchMachine));
        stringBuffer.append("  控制器厂商代码编号:");
        stringBuffer.append(supportOrUnSupport(controllerNo));
        stringBuffer.append("  电池信息:");
        stringBuffer.append(supportOrUnSupport(batteryInfo));
        stringBuffer.append("  电池厂商代码编号:");
        stringBuffer.append(supportOrUnSupport(batteryNo));
        stringBuffer.append("  右转灯开关:");
        stringBuffer.append(supportOrUnSupport(rightLight));
        stringBuffer.append("  左转灯开关:");
        stringBuffer.append(supportOrUnSupport(leftLight));
        stringBuffer.append("  氛围灯开关:");
        stringBuffer.append(supportOrUnSupport(ambientLight));
        stringBuffer.append("  日行灯开关:");
        stringBuffer.append(supportOrUnSupport(dayLight));
        stringBuffer.append("  刹车灯开关:");
        stringBuffer.append(supportOrUnSupport(brakeLight));
        stringBuffer.append("  前车灯开关:");
        stringBuffer.append(supportOrUnSupport(headingLight));
        stringBuffer.append("  寻车功能:");
        stringBuffer.append(supportOrUnSupport(findCar));
        stringBuffer.append("  自动锁车:");
        stringBuffer.append(supportOrUnSupport(autoLock));
        stringBuffer.append("  自动解锁:");
        stringBuffer.append(supportOrUnSupport(autoUnLock));
        stringBuffer.append("  车辆移位报警:");
        stringBuffer.append(supportOrUnSupport(moveWarm));
        stringBuffer.append("  车辆碰撞报警:");
        stringBuffer.append(supportOrUnSupport(collisionWarm));

        logList.add(0,stringBuffer.toString());
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHeartData(byte[] payload, int batteryState, int battery, float currentSpeed, int deviceState, int cruiseControlState, int unit, int cruiseControlSwitchState, int startMode, int lockState, int maxSpeed, int minGearStartZero, int currentGear, int supportGear, int handlerbarState, int electronicBrakeState, int mechanicalBrakeState, int motorState, int rightLight, int leftLight, int ambientLight, int dayLight, int brakeLight, int headingLight, int errorBattery, int errorControl, int errorMotor, int errorMotorHall, int errorBrake, int errorHandlerBar, int errorCommunication, float singleMileage, int singleTime, int updateState) {
        this.currentGear = currentGear;
        logList.add(0,"payload收：" + BleStrUtils.byte2HexStr(payload));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("心跳：充电状态:");
        stringBuffer.append(batteryState == 0 ? "未充电" : "充电中");
        stringBuffer.append("  电池电量百分比:");
        stringBuffer.append(battery);
        stringBuffer.append("  车辆实时速度:");
        stringBuffer.append(currentSpeed);
        stringBuffer.append("  开关机状态:");
        stringBuffer.append(deviceState == 0 ? "开机" : "关机");
        stringBuffer.append("  巡航状态:");
        stringBuffer.append(cruiseControlState == 0 ? "未巡航" : "巡航中");
        stringBuffer.append("  当前单位:");
        stringBuffer.append(unit == 0 ? "公里制" : "英里制");
        stringBuffer.append("  巡航功能开关状态:");
        stringBuffer.append(cruiseControlSwitchState == 0 ? "巡航关闭" : "巡航开启");
        stringBuffer.append("  启动方式:");
        stringBuffer.append(startMode == 0 ? "零启动" : "非零启动");
        stringBuffer.append("  锁车状态:");
        stringBuffer.append(lockState == 0 ? "解锁" : "锁车");
        stringBuffer.append("  车辆支持的最高速度(km/h):");
        stringBuffer.append(maxSpeed);
        stringBuffer.append("  最低档位是否从0开始:");
        stringBuffer.append(minGearStartZero == 0 ? "从1开始" : "从0开始");
        stringBuffer.append("  当前档位:");
        stringBuffer.append(currentGear);
        stringBuffer.append("  支持的档位个数:");
        stringBuffer.append(supportGear);
        stringBuffer.append("  转把有效状态:");
        stringBuffer.append(handlerbarState == 0 ? "无效" : "有效");
        stringBuffer.append("  电子刹车状态:");
        stringBuffer.append(electronicBrakeState == 0 ? "未刹车" : "刹车中");
        stringBuffer.append("  机械刹车状态:");
        stringBuffer.append(mechanicalBrakeState == 0 ? "未刹车" : "刹车中");
        stringBuffer.append("  电机运行状态:");
        stringBuffer.append(motorState == 0 ? "电机未运行" : "电机运行");
        stringBuffer.append("  右转灯开关:");
        stringBuffer.append(openOrClose(rightLight));
        stringBuffer.append("  左转灯开关:");
        stringBuffer.append(openOrClose(leftLight));
        stringBuffer.append("  氛围灯开关:");
        stringBuffer.append(openOrClose(ambientLight));
        stringBuffer.append("  日行灯开关:");
        stringBuffer.append(openOrClose(dayLight));
        stringBuffer.append("  刹车灯开关:");
        stringBuffer.append(openOrClose(brakeLight));
        stringBuffer.append("  前车灯开关:");
        stringBuffer.append(openOrClose(headingLight));
        stringBuffer.append("  电池故障:");
        stringBuffer.append(unFaultOrFault(errorBattery));
        stringBuffer.append("  控制器故障:");
        stringBuffer.append(unFaultOrFault(errorControl));
        stringBuffer.append("  电机相线或者MOS管短路故障:");
        stringBuffer.append(unFaultOrFault(errorMotor));
        stringBuffer.append("  电机霍尔故障:");
        stringBuffer.append(unFaultOrFault(errorMotorHall));
        stringBuffer.append("  刹车故障:");
        stringBuffer.append(unFaultOrFault(errorBrake));
        stringBuffer.append("  转把故障:");
        stringBuffer.append(unFaultOrFault(errorHandlerBar));
        stringBuffer.append("  通讯故障:");
        stringBuffer.append(unFaultOrFault(errorCommunication));
        stringBuffer.append("  单次行驶里程:");
        stringBuffer.append(singleMileage);
        stringBuffer.append("  单次行驶时间:");
        stringBuffer.append(singleTime);
        stringBuffer.append("  升级状态:");
        stringBuffer.append(updateState == 0 ? "未升级1" : updateState == 1 ? "仪表升级中" : "控制器升级中");
        logList.add(0,stringBuffer.toString());
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChangePassword(byte[] payload, int result) {
        logList.add(0,"payload收：" + BleStrUtils.byte2HexStr(payload));
        logList.add(0,"修改密码" + (result == 0 ? "成功" : result == 1 ? "失败" : "不支持"));
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLockState(byte[] payload, int result, int passwordResult) {
        logList.add(0,"payload收：" + BleStrUtils.byte2HexStr(payload));
        logList.add(0,(result == 0 ? "解锁：" : result == 1 ? "上锁" : "验证密码：") + (passwordResult == 0 ? "密码正确" : "密码错误") + " （锁车时，不校验密码.该结果无效）");
        listAdapter.notifyDataSetChanged();
    }


    @Override
    public void onAfterPassword(byte[] payload, String afterPassword) {
        logList.add(0,"payload收：" + BleStrUtils.byte2HexStr(payload));
        logList.add(0,"售后密码: " + afterPassword);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAutoShutDownTime(byte[] payload, int unlockTime, int lockTime) {
        logList.add(0,"payload收：" + BleStrUtils.byte2HexStr(payload));
        logList.add(0,"自动关机时间: 开锁状态下，自动关机" + unlockTime + "  锁车状态下，自动关机:" + lockTime);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSingleMileageAndTime(byte[] payload, float mileage, int second) {
        logList.add(0,"payload收：" + BleStrUtils.byte2HexStr(payload));
        logList.add(0,"单次行驶里程" + mileage + "  时间:" + second);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTotalMileage(byte[] payload, float mileage) {
        logList.add(0,"payload收：" + BleStrUtils.byte2HexStr(payload));
        logList.add(0,"行驶总里程" + mileage);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBatteryInfo(byte[] payload, int temp, int currentType, float current, float voltage, int totalCapacity, int chargeAndDischargeTimes) {
        logList.add(0,"payload收：" + BleStrUtils.byte2HexStr(payload));
        logList.add(0,"电池温度:" + temp + (currentType == 0 ? "  实时电流" : "  电池限流") + " 电流:" + current + " 电压:" + voltage + " 总容量:" + totalCapacity + " 充放电次数:" + chargeAndDischargeTimes);
        listAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBatteryNo(byte[] payload, String no, String hardwareVersion, String softwareVersion) {
        logList.add(0,"payload收：" + BleStrUtils.byte2HexStr(payload));
        logList.add(0,"厂商代码编号:" + no + "  硬件版本:" + hardwareVersion + "  软件版本:" + softwareVersion);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onControllerVersion(byte[] payload, String no, String hardwareVersion, String softwareVersion) {
        logList.add(0,"payload收：" + BleStrUtils.byte2HexStr(payload));
        logList.add(0,"控制器厂商代码:" + no + "  硬件版本:" + hardwareVersion + "  软件版本:" + softwareVersion);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMeterVersion(byte[] payload, String no, String hardwareVersion, String softwareVersion) {
        logList.add(0,"payload收：" + BleStrUtils.byte2HexStr(payload));
        logList.add(0,"仪表固件版本:" + no + "  硬件版本:" + hardwareVersion + "  软件版本:" + softwareVersion);
        listAdapter.notifyDataSetChanged();
    }


    @Override
    public void onChargeTime(byte[] payload, int repeat, int monday, int tuesday, int wednesday, int thursday, int friday, int saturday, int sunday, int startHour, int startMin, int endHour, int endMin) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("设置充电时间:重复:");
        stringBuffer.append(repeat == 0 ? "重复：" : "不重复");
        stringBuffer.append(monday == 1 ? "周一 " : "");
        stringBuffer.append(tuesday == 1 ? "周二 " : "");
        stringBuffer.append(wednesday == 1 ? "周三 " : "");
        stringBuffer.append(thursday == 1 ? "周四 " : "");
        stringBuffer.append(friday == 1 ? "周五 " : "");
        stringBuffer.append(saturday == 1 ? "周六 " : "");
        stringBuffer.append(sunday == 1 ? "周日 " : "");
        stringBuffer.append(" 开始时间：");
        stringBuffer.append(startHour);
        stringBuffer.append(":");
        stringBuffer.append(startMin);
        stringBuffer.append(" 结束时间：");
        stringBuffer.append(endHour);
        stringBuffer.append(":");
        stringBuffer.append(endMin);
        logList.add(0,"payload收：" + BleStrUtils.byte2HexStr(payload));
        logList.add(0,stringBuffer.toString());
        listAdapter.notifyDataSetChanged();

    }

    @Override
    public void onChargeCapacity(byte[] payload, int capacity) {
        logList.add(0,"payload收：" + BleStrUtils.byte2HexStr(payload));
        logList.add(0,"充电容量：" + capacity);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCarWarmAndAutoLock(byte[] payload, int findCar, int autoLock, int autoUnlock, int moveCar, int collisionWarm) {
        logList.add(0,"payload收：" + BleStrUtils.byte2HexStr(payload));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("寻车功能");
        stringBuffer.append(openOrClose(findCar));
        stringBuffer.append("自动锁车");
        stringBuffer.append(openOrClose(autoLock));
        stringBuffer.append("自动解锁");
        stringBuffer.append(openOrClose(autoUnlock));
        stringBuffer.append("移位报警");
        stringBuffer.append(openOrClose(moveCar));
        stringBuffer.append("碰撞报警");
        stringBuffer.append(openOrClose(collisionWarm));
        logList.add(0,stringBuffer.toString());
        listAdapter.notifyDataSetChanged();
    }

    private String supportOrUnSupport(int type) {
        return type == 0 ? "不支持" : "支持";
    }

    private String openOrClose(int type) {
        return type == 0 ? "关闭" : "开启";
    }

    private String unFaultOrFault(int type) {
        return type == 0 ? "无故障" : "有故障";
    }

}

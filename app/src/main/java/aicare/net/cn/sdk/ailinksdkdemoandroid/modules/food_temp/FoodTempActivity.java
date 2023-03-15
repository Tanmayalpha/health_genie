package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.food_temp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.FoodTemp.FoodTempData;

/**
 * 食品温度计界面
 */
public class FoodTempActivity extends BleBaseActivity implements View.OnClickListener, FoodTempData.FoodTempCallback {

    private static final String TAG = "Tag1";

    private Button btn_clear;
    private TextView tv_device_battery;
    private Button btn_get_device;
    private Button btn_set_temp_unit;
    private RadioButton rb_set_c;
    private RadioButton rb_set_f;
    private Button btn_stop_alert;
    private Spinner sp_stop_alert;
    private Button btn_set_target;
    private Spinner sp_set_target;
    private EditText et_set_target;
    private RadioButton rb_set_target_c;
    private RadioButton rb_set_target_f;
    private Button btn_set_timing;
    private Spinner sp_set_timing;
    private EditText et_set_timing;
    private Button btn_open_close;
    private RadioButton rb_open;
    private RadioButton rb_close;
    private Button btn_sync_time;
    private EditText et_year;
    private EditText et_month;
    private EditText et_day;
    private EditText et_hour;
    private EditText et_minute;
    private EditText et_second;
    private EditText et_week;
    private Button btn_open_close_probe;
    private Spinner sp_open_close_probe;
    private RadioButton rb_open_probe;
    private RadioButton rb_close_probe;

    private ListView list_view;

    private List<String> mList;
    private ArrayAdapter mListAdapter;

    private String mMac;
    private BleDevice mBleDevice;
    private FoodTempData mFoodTempData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_temp);

        btn_clear = findViewById(R.id.btn_clear);
        btn_get_device = findViewById(R.id.btn_get_device);
        btn_set_temp_unit = findViewById(R.id.btn_set_temp_unit);
        rb_set_c = findViewById(R.id.rb_set_c);
        rb_set_f = findViewById(R.id.rb_set_f);
        btn_stop_alert = findViewById(R.id.btn_stop_alert);
        sp_stop_alert = findViewById(R.id.sp_stop_alert);
        btn_set_target = findViewById(R.id.btn_set_target);
        sp_set_target = findViewById(R.id.sp_set_target);
        et_set_target = findViewById(R.id.et_set_target);
        rb_set_target_c = findViewById(R.id.rb_set_target_c);
        rb_set_target_f = findViewById(R.id.rb_set_target_f);
        btn_set_timing = findViewById(R.id.btn_set_timing);
        sp_set_timing = findViewById(R.id.sp_set_timing);
        et_set_timing = findViewById(R.id.et_set_timing);
        btn_open_close = findViewById(R.id.btn_open_close);
        rb_open = findViewById(R.id.rb_open);
        rb_close = findViewById(R.id.rb_close);
        btn_sync_time = findViewById(R.id.btn_sync_time);
        et_year = findViewById(R.id.et_year);
        et_month = findViewById(R.id.et_month);
        et_day = findViewById(R.id.et_day);
        et_hour = findViewById(R.id.et_hour);
        et_minute = findViewById(R.id.et_minute);
        et_second = findViewById(R.id.et_second);
        et_week = findViewById(R.id.et_week);
        btn_open_close_probe = findViewById(R.id.btn_open_close_probe);
        sp_open_close_probe = findViewById(R.id.sp_open_close_probe);
        rb_open_probe = findViewById(R.id.rb_open_probe);
        rb_close_probe = findViewById(R.id.rb_close_probe);
        list_view = findViewById(R.id.list_view);
        tv_device_battery = findViewById(R.id.tv_device_battery);

        btn_clear.setOnClickListener(this);
        btn_get_device.setOnClickListener(this);
        btn_set_temp_unit.setOnClickListener(this);
        btn_stop_alert.setOnClickListener(this);
        btn_set_target.setOnClickListener(this);
        btn_set_timing.setOnClickListener(this);
        btn_open_close.setOnClickListener(this);
        btn_sync_time.setOnClickListener(this);
        btn_open_close_probe.setOnClickListener(this);

        // 获取Mac
        mMac = getIntent().getStringExtra("mac");

        // 初始化列表
        mList = new ArrayList<>();
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        list_view.setAdapter(mListAdapter);

        // 默认时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        et_year.setText("" + calendar.get(Calendar.YEAR));
        et_month.setText("" + (calendar.get(Calendar.MONTH) + 1));
        et_day.setText("" + calendar.get(Calendar.DAY_OF_MONTH));
        et_hour.setText("" + calendar.get(Calendar.HOUR_OF_DAY));
        et_minute.setText("" + calendar.get(Calendar.MINUTE));
        et_second.setText("" + calendar.get(Calendar.SECOND));
        int week = (calendar.get(Calendar.DAY_OF_WEEK) - 1);
        if (week <= 0) {
            week = 7;
        }
        et_week.setText("" + week);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_clear) {
            clearText();
        } else if (id == R.id.btn_get_device) {
            appGetDevice();
        } else if (id == R.id.btn_set_temp_unit) {
            appSetTempUnit();
        } else if (id == R.id.btn_stop_alert) {
            appStopAlert();
        } else if (id == R.id.btn_set_target) {
            appSetTargetTemp();
        } else if (id == R.id.btn_set_timing) {
            appSetTiming();
        } else if (id == R.id.btn_open_close) {
            appOpenClose();
        } else if (id == R.id.btn_sync_time) {
            appSyncTime();
        } else if (id == R.id.btn_open_close_probe) {
            appOpenCloseProbe();
        }
    }


    public void getBattery(View view) {
        if (mBleDevice != null) {
            byte[] blePower = BleSendCmdUtil.getInstance().getMcuBatteryStatus();
            SendBleBean sendBleBean = new SendBleBean();
            sendBleBean.setHex(blePower);
            mBleDevice.sendData(sendBleBean);
        }
    }

    public void onStopLog(View view) {
        mRefreshLog = !mRefreshLog;
    }

    @Override
    protected void onDestroy() {
        if (mBluetoothService != null) {
            mBluetoothService.disconnectAll();
        }
        super.onDestroy();
    }

    @Override
    public void onServiceSuccess() {
        mBleDevice = mBluetoothService.getBleDevice(mMac);
        if (mBleDevice != null) {
            mFoodTempData = new FoodTempData(mBleDevice);
            mFoodTempData.setFoodTempCallback(this);
            mBleDevice.setOnMcuParameterListener(new OnMcuParameterListener() {
                @Override
                public void onMcuBatteryStatus(int status, int battery) {
                    if (tv_device_battery != null) {
                        tv_device_battery.setText("电量:" + battery + "%");
                    }
                }
            });
        }
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }

    @Override
    public void mcuDevice(int probeNum, int chargerState, int battery, int tempUnit, int alertType) {
        addText("MCU上发设备信息：探针数量：" + probeNum + "，充电状态：" + chargerState + "，电量：" + battery + "，温度单位：" + (tempUnit == 0 ? "C" : "F") + "，警报类型：" + alertType);
    }

    @Override
    public void mcuResult(int id, int inDevice, int curTemp, int curTempUnit, int ambienceTemp, int ambienceTempUnit, int targetTemp, int targetTempUnit, int inMeat, int enableAlert, int mode,
                          int timing, int alertType) {
        addText("MCU上发数据：探针编号：" + id + "，探针插入设备状态：" + inDevice + "，当前温度：" + curTemp + "，当前温度单位：" + (curTempUnit == 0 ? "C" : "F") + "，环境温度：" + ambienceTemp + "，环境温度单位：" + (ambienceTempUnit == 0 ?
                "C" : "F") + "，目标温度：" + targetTemp + "，目标温度单位：" + (targetTempUnit == 0 ? "C" : "F") + "，探针插入肉状态：" + inMeat + "，警报启动状态：" + enableAlert + "，模式：" + mode + "，定时：" + timing + "，警报类型：" + alertType);
    }

    @Override
    public void mcuSetTempUnitResult(int result) {
        addText("MCU回复设置温度单位结果：" + result);
    }

    @Override
    public void mcuStopAlertResult(int result) {
        addText("MCU回复停止警报结果：" + result);
    }

    @Override
    public void mcuSetTargetTempResult(int result) {
        addText("MCU回复设置目标温度结果：" + result);
    }

    @Override
    public void mcuSetTimingResult(int result) {
        addText("MCU回复设置定时结果：" + result);
    }

    @Override
    public void mcuOpenCloseResult(int result) {
        addText("MCU回复开关机结果：" + result);
    }

    @Override
    public void mcuSyncTimeResult(int result) {
        addText("MCU回复同步时间结果：" + result);
    }

    @Override
    public void mcuOpenCloseProbeResult(int result) {
        addText("MCU回复开关探针结果：" + result);
    }

    /**
     * APP获取设备信息
     */
    private void appGetDevice() {
        mFoodTempData.appGetDevice();
        addText("APP请求获取设备信息");
    }

    /**
     * APP设置温度单位
     */
    private void appSetTempUnit() {
        int unit = rb_set_c.isChecked() ? 0 : 1;
        mFoodTempData.appSetTempUnit(unit);
        addText("APP设置温度单位：" + unit);
    }

    /**
     * APP停止设备报警
     */
    private void appStopAlert() {
        int id = sp_stop_alert.getSelectedItemPosition();
        mFoodTempData.appStopAlert(id);
        addText("APP停止设备报警：" + id);
    }

    /**
     * APP设置目标温度
     */
    private void appSetTargetTemp() {
        int id = sp_set_target.getSelectedItemPosition();
        int temp = 0;
        int tempUnit = rb_set_target_c.isChecked() ? 0 : 1;
        try {
            temp = Integer.parseInt(et_set_target.getText().toString());
        } catch (Exception ignored) {
            addText("目标温度必须是整数");
            return;
        }
        mFoodTempData.appSetTargetTemp(id, temp, tempUnit);
        addText("APP设置目标温度：编号：" + id + "，温度：" + temp + "，温度单位：" + tempUnit);
    }

    /**
     * APP设置定时
     */
    private void appSetTiming() {
        int id = sp_set_timing.getSelectedItemPosition();
        int minute = 0;
        try {
            minute = Integer.parseInt(et_set_timing.getText().toString());
        } catch (Exception ignored) {
            addText("定时必须是整数");
            return;
        }
        mFoodTempData.appSetTiming(id, minute);
        addText("APP设置定时：编号：" + id + "，分钟：" + minute);
    }

    /**
     * APP开关设备
     */
    private void appOpenClose() {
        boolean isOpen = rb_open.isChecked();
        mFoodTempData.appOpenClose(isOpen);
        addText("APP开关设备：" + isOpen);
    }

    /**
     * APP同步时间
     */
    private void appSyncTime() {
        int year = 0;
        int month = 0;
        int day = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;
        int week = 0;
        try {
            year = Integer.parseInt(et_year.getText().toString());
            month = Integer.parseInt(et_month.getText().toString());
            day = Integer.parseInt(et_day.getText().toString());
            hour = Integer.parseInt(et_hour.getText().toString());
            minute = Integer.parseInt(et_minute.getText().toString());
            second = Integer.parseInt(et_second.getText().toString());
            week = Integer.parseInt(et_week.getText().toString());
        } catch (Exception ignored) {
            addText("年月日时分秒必须是整数");
            return;
        }
        mFoodTempData.appSyncTime(year, month, day, hour, minute, second, week);
        addText("APP同步时间：年：" + year + "，月：" + month + "，日：" + day + "，时：" + hour + "，分：" + minute + "，秒：" + second + "，一周第几天：" + week);
    }

    /**
     * APP开关探针
     */
    private void appOpenCloseProbe() {
        int id = sp_open_close_probe.getSelectedItemPosition();
        boolean isOpen = rb_open_probe.isChecked();
        mFoodTempData.appOpenCloseProbe(id, isOpen);
        addText("APP开关探针：编号：" + id + "，" + isOpen);
    }

    private boolean mRefreshLog = true;

    SimpleDateFormat sdf;

    // 添加一条文本
    private void addText(String text) {
        if (!mRefreshLog) {
            return;
        }
        if (sdf == null) {
            sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
        }
        mList.add(sdf.format(System.currentTimeMillis()) + "：\n" + text);
        mListAdapter.notifyDataSetChanged();
        list_view.smoothScrollToPosition(mList.size() - 1);
    }

    // 清空文本
    private void clearText() {
        mList.clear();
        mListAdapter.notifyDataSetChanged();
    }
}

package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.coffee_scale;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import com.pingwang.bluetoothlib.BleBaseActivity;
import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BleDevice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.CoffeeScale.CoffeeScaleData;

/**
 * 咖啡秤
 */
public class CoffeeScaleActivity extends BleBaseActivity implements View.OnClickListener, CoffeeScaleData.CoffeeScaleCallback {

    private static final String TAG = "Tag1";

    private Button btn_clear;
    private Button btn_zero;
    private Button btn_set_weight_unit;
    private Button btn_set_temp_unit;
    private Button btn_set_auto_shutdown;
    private Button btn_set_timing;
    private Button btn_callback_timing;
    private Button btn_set_alert;
    private Button btn_stop_alert;
    private Button btn_callback_alert;
    private Button btn_callback_stop_alert;
    private Button btn_brew_mode;
    private RadioButton rb_kg;
    private RadioButton rb_jin;
    private RadioButton rb_lb_oz;
    private RadioButton rb_oz;
    private RadioButton rb_st_lb;
    private RadioButton rb_g;
    private RadioButton rb_lb;
    private RadioButton rb_c;
    private RadioButton rb_f;
    private RadioButton rb_positive_timing;
    private RadioButton rb_negative_timing;
    private RadioButton rb_timing_start;
    private RadioButton rb_timing_pause;
    private RadioButton rb_timing_reset;
    private RadioButton rb_callback_timing_success;
    private RadioButton rb_callback_timing_fail;
    private RadioButton rb_callback_timing_not_support;
    private RadioButton rb_alert_close;
    private RadioButton rb_alert_open;
    private RadioButton rb_callback_alert_success;
    private RadioButton rb_callback_alert_fail;
    private RadioButton rb_callback_alert_not_support;
    private RadioButton rb_callback_stop_alert_success;
    private RadioButton rb_callback_stop_alert_fail;
    private RadioButton rb_callback_stop_alert_not_support;
    private RadioButton rb_brew_mode_enter;
    private RadioButton rb_brew_mode_exit;
    private EditText et_auto_shutdown;
    private EditText et_timing;
    private EditText et_alert;
    private ListView list_view;

    private List<String> mList;
    private ArrayAdapter mListAdapter;

    private String mMac;
    private BleDevice mBleDevice;
    private CoffeeScaleData mCoffeeScaleData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_scale);

        btn_clear = findViewById(R.id.btn_clear);
        btn_zero = findViewById(R.id.btn_zero);
        btn_set_weight_unit = findViewById(R.id.btn_set_weight_unit);
        btn_set_temp_unit = findViewById(R.id.btn_set_temp_unit);
        btn_set_auto_shutdown = findViewById(R.id.btn_set_auto_shutdown);
        btn_set_timing = findViewById(R.id.btn_set_timing);
        btn_callback_timing = findViewById(R.id.btn_callback_timing);
        btn_set_alert = findViewById(R.id.btn_set_alert);
        btn_stop_alert = findViewById(R.id.btn_stop_alert);
        btn_callback_alert = findViewById(R.id.btn_callback_alert);
        btn_callback_stop_alert = findViewById(R.id.btn_callback_stop_alert);
        btn_brew_mode = findViewById(R.id.btn_brew_mode);
        rb_kg = findViewById(R.id.rb_kg);
        rb_jin = findViewById(R.id.rb_jin);
        rb_lb_oz = findViewById(R.id.rb_lb_oz);
        rb_oz = findViewById(R.id.rb_oz);
        rb_st_lb = findViewById(R.id.rb_st_lb);
        rb_g = findViewById(R.id.rb_g);
        rb_lb = findViewById(R.id.rb_lb);
        rb_c = findViewById(R.id.rb_c);
        rb_f = findViewById(R.id.rb_f);
        rb_positive_timing = findViewById(R.id.rb_positive_timing);
        rb_negative_timing = findViewById(R.id.rb_negative_timing);
        rb_timing_start = findViewById(R.id.rb_timing_start);
        rb_timing_pause = findViewById(R.id.rb_timing_pause);
        rb_timing_reset = findViewById(R.id.rb_timing_reset);
        rb_callback_timing_success = findViewById(R.id.rb_callback_timing_success);
        rb_callback_timing_fail = findViewById(R.id.rb_callback_timing_fail);
        rb_callback_timing_not_support = findViewById(R.id.rb_callback_timing_not_support);
        rb_alert_close = findViewById(R.id.rb_alert_close);
        rb_alert_open = findViewById(R.id.rb_alert_open);
        rb_callback_alert_success = findViewById(R.id.rb_callback_alert_success);
        rb_callback_alert_fail = findViewById(R.id.rb_callback_alert_fail);
        rb_callback_alert_not_support = findViewById(R.id.rb_callback_alert_not_support);
        rb_callback_stop_alert_success = findViewById(R.id.rb_callback_stop_alert_success);
        rb_callback_stop_alert_fail = findViewById(R.id.rb_callback_stop_alert_fail);
        rb_callback_stop_alert_not_support = findViewById(R.id.rb_callback_stop_alert_not_support);
        rb_brew_mode_enter = findViewById(R.id.rb_brew_mode_enter);
        rb_brew_mode_exit = findViewById(R.id.rb_brew_mode_exit);
        et_auto_shutdown = findViewById(R.id.et_auto_shutdown);
        et_timing = findViewById(R.id.et_timing);
        et_alert = findViewById(R.id.et_alert);
        list_view = findViewById(R.id.list_view);

        btn_clear.setOnClickListener(this);
        btn_zero.setOnClickListener(this);
        btn_set_weight_unit.setOnClickListener(this);
        btn_set_temp_unit.setOnClickListener(this);
        btn_set_auto_shutdown.setOnClickListener(this);
        btn_set_timing.setOnClickListener(this);
        btn_callback_timing.setOnClickListener(this);
        btn_set_alert.setOnClickListener(this);
        btn_stop_alert.setOnClickListener(this);
        btn_callback_alert.setOnClickListener(this);
        btn_callback_stop_alert.setOnClickListener(this);
        btn_brew_mode.setOnClickListener(this);

        // 获取Mac
        mMac = getIntent().getStringExtra("mac");

        // 初始化列表
        mList = new ArrayList<>();
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        list_view.setAdapter(mListAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear:
                // 清空文本框
                clearText();
                break;
            case R.id.btn_zero:
                // APP下发去皮功能（归零功能）
                appSetZero();
                break;
            case R.id.btn_set_weight_unit:
                // APP下发重量单位
                appSetWeightUnit();
                break;
            case R.id.btn_set_temp_unit:
                // APP下发温度单位
                appSetTempUnit();
                break;
            case R.id.btn_set_auto_shutdown:
                // APP下发自动关机
                appSetAutoShutdown();
                break;
            case R.id.btn_set_timing:
                // APP下发计时功能控制
                appSetTiming();
                break;
            case R.id.btn_callback_timing:
                // APP回复计时功能
                appCallbackTiming();
                break;
            case R.id.btn_set_alert:
                // APP下发报警设置
                appSetAlert();
                break;
            case R.id.btn_stop_alert:
                // APP停止报警指令
                appStopAlert();
                break;
            case R.id.btn_callback_alert:
                // APP回复报警设置
                appCallbackAlert();
                break;
            case R.id.btn_callback_stop_alert:
                // APP回复停止报警
                appCallbackStopAlert();
                break;
            case R.id.btn_brew_mode:
                // APP设置冲煮模式
                appBrewMode();
                break;
        }
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
            mCoffeeScaleData = new CoffeeScaleData(mBleDevice);
            mCoffeeScaleData.setCoffeeScaleCallback(this);
        }
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }

    @Override
    public void mcuResult(int weightType, int weightUnit, int weightDecimal, int weightSource, float weight, int tempUnit, int tempDecimal, int tempSource, float temp, int err) {
        Log.i(TAG, "onResult");
        String text = "MCU上发重量：" + (weightType == 1 ? "稳定重量" : "实时重量");
        text += "；重量小数点：" + weightDecimal;
        text += "；重量原始值：" + weightSource;
        text += "；重量：" + (weightDecimal > 0 ? weight : (int) weight) + getWeightUnitStr(weightUnit);
        if (tempUnit != 0xff) {
            text += "；温度小数点：" + tempDecimal;
            text += "；温度原始值：" + tempSource;
            text += "；温度：" + (tempDecimal > 0 ? temp : (int) temp) + getTempUnitStr(tempUnit);
        } else {
            text += "；温度：不支持";
        }
        if (err != 0xff) {
            text += "；异常：" + getErrStr(err);
        } else {
            text += "；异常：不支持";
        }
        addText(text);
    }

    @Override
    public void mcuPower(int status, int power) {
        Log.i(TAG, "onPower：" + status + "，" + power);
        String text = "MCU上发充电状态：" + getPowerStatusStr(status);
        text += "；电量：" + power + "%";
        addText(text);
    }

    @Override
    public void mcuTiming(int seconds, int type, int op) {
        Log.i(TAG, "onTiming：" + seconds + "，type：" + type + "，op：" + op);
        String text = "MCU上发计时：" + seconds + "秒";
        text += "；" + getTimingStr(type) + "；" + getTimingOpStr(op);
        addText(text);
    }

    @Override
    public void mcuAlert(int seconds, int op) {
        Log.i(TAG, "onAlert：" + seconds + "，" + op);
        String text = "MCU上发报警：" + seconds + "秒；" + getAlertOpStr(op);
        addText(text);
    }

    @Override
    public void mcuStopAlert() {
        Log.i(TAG, "onStopAlert");
        String text = "MCU停止报警";
        addText(text);
    }

    @Override
    public void mcuCallbackSetZero(int status) {
        Log.i(TAG, "onSetZero：" + status);
        String text = "MCU回复去皮（归零）：" + getStatusStr(status);
        addText(text);
    }

    @Override
    public void mcuCallbackSetWeightUnit(int status) {
        Log.i(TAG, "onSetWeightUnit：" + status);
        String text = "MCU回复设置重量单位：" + getStatusStr(status);
        addText(text);
    }

    @Override
    public void mcuCallbackSetTempUnit(int status) {
        Log.i(TAG, "onSetTempUnit：" + status);
        String text = "MCU回复设置温度单位：" + getStatusStr(status);
        addText(text);
    }

    @Override
    public void mcuCallbackSetAutoShutdown(int status) {
        Log.i(TAG, "onSetAutoShutdown：" + status);
        String text = "MCU回复设置自动关机：" + getStatusStr(status);
        addText(text);
    }

    @Override
    public void mcuCallbackSetTiming(int status) {
        Log.i(TAG, "onSetTiming：" + status);
        String text = "MCU回复设置计时功能：" + getStatusStr(status);
        addText(text);
    }

    @Override
    public void mcuCallbackSetAlert(int status) {
        Log.i(TAG, "onSetAlert：" + status);
        String text = "MCU回复设置警报功能：" + getStatusStr(status);
        addText(text);
    }

    @Override
    public void mcuCallbackStopAlert(int status) {
        Log.i(TAG, "onStopAlert：" + status);
        String text = "MCU回复停止警报功能：" + getStatusStr(status);
        addText(text);
    }

    @Override
    public void mcuBrewMode(int status) {
        Log.i(TAG, "onBrewMode：" + status);
        String text = "MCU冲煮模式：" + getBrewMode(status);
        addText(text);
    }

    @Override
    public void mcuSupportUnit(List<SupportUnitBean> list) {

    }

    // APP下发去皮功能（归零功能）
    private void appSetZero() {
        Log.i(TAG, "去皮功能（归零功能）");
        if (mCoffeeScaleData != null) {
            addText("APP下发归零");
            mCoffeeScaleData.setZero();
        }
    }

    // APP设置重量单位
    private void appSetWeightUnit() {
        Log.i(TAG, "设置重量单位");
        int weightUnit = rb_kg.isChecked() ? 0 : rb_jin.isChecked() ? 1 : rb_lb_oz.isChecked() ? 2 : rb_oz.isChecked() ? 3 : rb_st_lb.isChecked() ? 4 : rb_g.isChecked() ? 5 : rb_lb.isChecked() ? 6 : 0;
        if (mCoffeeScaleData != null) {
            addText("APP下发重量单位：" + weightUnit + "；" + getWeightUnitStr(weightUnit));
            mCoffeeScaleData.setWeightUnit(weightUnit);
        }
    }

    // APP设置温度单位
    private void appSetTempUnit() {
        Log.i(TAG, "设置温度单位");
        int tempUnit = rb_c.isChecked() ? 0 : rb_f.isChecked() ? 1 : 0;
        if (mCoffeeScaleData != null) {
            addText("APP下发温度单位：" + tempUnit + "；" + getTempUnitStr(tempUnit));
            mCoffeeScaleData.setTempUnit(tempUnit);
        }
    }

    // APP设置自动关机
    private void appSetAutoShutdown() {
        Log.i(TAG, "设置自动关机");
        String str = et_auto_shutdown.getText().toString();
        if (!TextUtils.isEmpty(str)) {
            int num = -1;
            try {
                num = Integer.parseInt(str);
            } catch (Exception e) {
                addText("APP下发自动关机：必须为整数");
            }
            if (num >= 0xffff) {
                addText("APP下发自动关机：不能≥65535");
            } else if (num < 0) {
                addText("APP下发自动关机：必须为正整数");
            } else {
                if (mCoffeeScaleData != null) {
                    addText("APP下发自动关机：" + num + "秒");
                    mCoffeeScaleData.setAutoShutdown(num);
                }
            }
        } else {
            addText("APP下发自动关机：不能为空");
        }
    }

    // APP计时功能控制
    private void appSetTiming() {
        Log.i(TAG, "计时功能控制");
        String str = et_timing.getText().toString();
        if (!TextUtils.isEmpty(str)) {
            int num = -1;
            try {
                num = Integer.parseInt(str);
            } catch (Exception e) {
                addText("APP下发计时功能：必须为整数");
            }
            if (num >= 0xffff) {
                addText("APP下发计时功能：不能≥65535");
            } else if (num < 0) {
                addText("APP下发计时功能：必须为正整数");
            } else {
                int type = rb_positive_timing.isChecked() ? 0 : rb_negative_timing.isChecked() ? 1 : 0;
                int op = rb_timing_start.isChecked() ? 1 : rb_timing_pause.isChecked() ? 2 : rb_timing_reset.isChecked() ? 3 : 0;
                if (mCoffeeScaleData != null) {
                    addText("APP下发计时功能：" + num + "秒；" + getTimingStr(type) + "；" + getTimingOpStr(op));
                    mCoffeeScaleData.setTiming(num, type, op);
                }
            }
        } else {
            addText("APP下发计时功能：不能为空");
        }
    }

    // APP回复计时功能
    private void appCallbackTiming() {
        Log.i(TAG, "回复计时功能");
        int status = rb_callback_timing_success.isChecked() ? 0 : rb_callback_timing_fail.isChecked() ? 1 : rb_callback_timing_not_support.isChecked() ? 2 : 0;
        if (mCoffeeScaleData != null) {
            addText("APP回复计时功能：" + status + "；" + getStatusStr(status));
            mCoffeeScaleData.callbackTiming(status);
        }
    }

    // APP报警设置指令
    private void appSetAlert() {
        Log.i(TAG, "报警设置指令");
        String str = et_alert.getText().toString();
        if (!TextUtils.isEmpty(str)) {
            int num = -1;
            try {
                num = Integer.parseInt(str);
            } catch (Exception e) {
                addText("APP下发报警设置：必须为整数");
            }
            if (num >= 0xffff) {
                addText("APP下发报警设置：不能≥65535");
            } else if (num < 0) {
                addText("APP下发报警设置：必须为正整数");
            } else {
                int op = rb_alert_close.isChecked() ? 0 : rb_alert_open.isChecked() ? 1 : 0;
                if (mCoffeeScaleData != null) {
                    addText("APP下发报警设置：" + num + "秒；" + getAlertOpStr(op));
                    mCoffeeScaleData.setAlert(num, op);
                }
            }
        } else {
            addText("APP下发报警设置：不能为空");
        }
    }

    // APP停止报警指令
    private void appStopAlert() {
        Log.i(TAG, "停止报警指令");
        if (mCoffeeScaleData != null) {
            addText("APP下发停止报警");
            mCoffeeScaleData.stopAlert();
        }
    }

    // APP回复报警设置
    private void appCallbackAlert() {
        Log.i(TAG, "回复报警设置");
        int status = rb_callback_alert_success.isChecked() ? 0 : rb_callback_alert_fail.isChecked() ? 1 : rb_callback_alert_not_support.isChecked() ? 2 : 0;
        if (mCoffeeScaleData != null) {
            addText("APP回复报警设置：" + status + "；" + getStatusStr(status));
            mCoffeeScaleData.callbackAlert(status);
        }
    }

    // APP回复停止报警
    private void appCallbackStopAlert() {
        Log.i(TAG, "回复停止报警");
        int status = rb_callback_stop_alert_success.isChecked() ? 0 : rb_callback_stop_alert_fail.isChecked() ? 1 : rb_callback_stop_alert_not_support.isChecked() ? 2 : 0;
        if (mCoffeeScaleData != null) {
            addText("APP回复停止报警：" + status + "；" + getStatusStr(status));
            mCoffeeScaleData.callbackStopAlert(status);
        }
    }

    // APP设置冲煮模式
    private void appBrewMode() {
        Log.i(TAG, "设置冲煮模式");
        int status = rb_brew_mode_enter.isChecked() ? 1 : 0;
        if (mCoffeeScaleData != null) {
            addText("APP设置冲煮模式：" + status + "；" + getBrewMode(status));
            mCoffeeScaleData.brewMode(status);
        }
    }

    SimpleDateFormat sdf;

    // 添加一条文本
    private void addText(String text) {
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

    private String getWeightUnitStr(int weightUnit) {
        switch (weightUnit) {
            case 0:
                return "kg";
            case 1:
                return "斤";
            case 2:
                return "lb:oz";
            case 3:
                return "oz";
            case 4:
                return "st:lb";
            case 5:
                return "g";
            case 6:
                return "lb";
        }
        return "";
    }

    private String getTempUnitStr(int tempUnit) {
        switch (tempUnit) {
            case 0:
                return "℃";
            case 1:
                return "℉";
        }
        return "";
    }

    private String getErrStr(int err) {
        switch (err) {
            case 0:
                return "无异常";
            case 1:
                return "超重";
        }
        return "";
    }

    private String getStatusStr(int status) {
        switch (status) {
            case 0:
                return "成功";
            case 1:
                return "失败";
            case 2:
                return "不支持";
        }
        return "";
    }

    private String getPowerStatusStr(int status) {
        switch (status) {
            case 0:
                return "没有充电";
            case 1:
                return "充电中";
            case 2:
                return "充满电";
            case 3:
                return "充电异常";
        }
        return "";
    }

    private String getTimingStr(int type) {
        switch (type) {
            case 0:
                return "正计时";
            case 1:
                return "倒计时";
        }
        return "";
    }

    private String getTimingOpStr(int op) {
        switch (op) {
            case 1:
                return "计时";
            case 2:
                return "暂停";
            case 3:
                return "重置";
        }
        return "";
    }
    
    private String getAlertOpStr(int op) {
        switch (op) {
            case 0:
                return "关闭";
            case 1:
                return "打开";
        }
        return "";
    }

    private String getBrewMode(int status) {
        switch (status) {
            case 0:
                return "退出";
            case 1:
                return "进入";
        }
        return "";
    }
}

package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.fascia_gun;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.pingwang.bluetoothlib.device.BleDevice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import cn.net.aicare.modulelibrary.module.FasciaGun.FasciaGunData;

/**
 * 筋膜枪
 */
public class FasciaGunActivity extends BleBaseActivity implements View.OnClickListener, FasciaGunData.FasciaGunCallback {

    private Button btn_device;
    private Spinner sp_device_gear;
    private RadioButton rb_device_start;
    private RadioButton rb_device_stop;
    private Button btn_set_gear;
    private Spinner sp_set_gear;
    private Button btn_set_time;
    private EditText et_time;
    private RadioButton rb_time_stop;
    private RadioButton rb_time_start;
    private ListView list_view;

    private List<String> mList;
    private ArrayAdapter mListAdapter;

    private String mMac;
    private BleDevice mBleDevice;
    private FasciaGunData mFasciaGunData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fascia_gun);

        btn_device = findViewById(R.id.btn_device);
        sp_device_gear = findViewById(R.id.sp_device_gear);
        rb_device_start = findViewById(R.id.rb_device_start);
        rb_device_stop = findViewById(R.id.rb_device_stop);
        btn_set_gear = findViewById(R.id.btn_set_gear);
        sp_set_gear = findViewById(R.id.sp_set_gear);
        btn_set_time = findViewById(R.id.btn_set_time);
        et_time = findViewById(R.id.et_time);
        rb_time_stop = findViewById(R.id.rb_time_stop);
        rb_time_start = findViewById(R.id.rb_time_start);
        list_view = findViewById(R.id.list_view);

        btn_device.setOnClickListener(this);
        btn_set_gear.setOnClickListener(this);
        btn_set_time.setOnClickListener(this);

        // 获取Mac
        mMac = getIntent().getStringExtra("mac");

        // 初始化列表
        mList = new ArrayList<>();
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        list_view.setAdapter(mListAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_device) {
            appDevice();
        } else if (id == R.id.btn_set_gear) {
            appSetGear();
        } else if (id == R.id.btn_set_time) {
            appSetTime();
        }
    }

    @Override
    protected void onDestroy() {
        if (mFasciaGunData != null) {
            mFasciaGunData.setFasciaGunCallback(null);
            mFasciaGunData = null;
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
            mFasciaGunData = new FasciaGunData(mBleDevice);
            mFasciaGunData.setFasciaGunCallback(this);

            addText("设备连接成功：" + mMac);
        }
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }

    @Override
    public void mcuDevice(int mode, int gear) {
        if (mode == 1) {
            addText("MCU回复启动/停止设备结果：启动；挡位：" + gear);
        } else {
            addText("MCU回复启动/停止设备结果：停止；挡位：" + gear);
        }
    }

    @Override
    public void mcuSetGear(int gear) {
        addText("MCU回复设置挡位结果：当前挡位：" + gear);
    }

    @Override
    public void mcuSetTime(int mode, int second) {
        if (mode == 1) {
            addText("MCU回复设置倒计时结果：启动；时间：" + second + "秒");
        } else {
            addText("MCU回复设置倒计时结果：停止；时间：" + second + "秒");
        }
    }

    @Override
    public void mcuStatus(int workStatus, int useTime, int curGear, int timeStatus, int timeSecond, int pressure, int batteryStatus, int batteryNum, int supportGear) {
        String workStatusStr;
        if (workStatus == 1) {
            workStatusStr = "启动";
        } else {
            workStatusStr = "停止";
        }
        String timeStatusStr;
        if (timeStatus == 1) {
            timeStatusStr = "启动";
        } else {
            timeStatusStr = "关闭";
        }
        String pressureStr;
        if (pressure != 0xFF) {
            pressureStr = String.valueOf(pressure);
        } else {
            pressureStr = "不支持";
        }
        String batteryStatusStr;
        switch (batteryStatus) {
            default:
            case 0:
                batteryStatusStr = "未充电";
                break;
            case 1:
                batteryStatusStr = "在充电";
                break;
            case 2:
                batteryStatusStr = "充满电";
                break;
            case 0xFF:
                batteryStatusStr = "不支持";
                break;
        }

        List<String> list = new ArrayList<>();
        for (int i = 0; i <= supportGear; i++) {
            list.add("挡位" + i);
        }
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_device_gear.setAdapter(arrayAdapter1);
        sp_set_gear.setAdapter(arrayAdapter2);

        addText("MCU上报实时状态：工作状态：" + workStatusStr + "；使用时长：" + useTime + "秒；当前挡位：" + curGear + "；是否倒计时：" + timeStatusStr + "；倒计时时长：" + timeSecond + "；压力值：" + pressureStr + "；电池状态：" + batteryStatusStr + "；电量：" + batteryNum + "；支持挡位：" + supportGear);
    }

    /**
     * APP 启动/停止设备
     */
    private void appDevice() {
        int mode = rb_device_start.isChecked() ? 1 : 2;
        int gear = sp_device_gear.getSelectedItemPosition();

        if (gear == -1) {
            addText("请先用设备回复支持的挡位数量");
            return;
        }

        if (mode == 1) {
            addText("APP启动设备；挡位：" + gear);
        } else {
            addText("APP停止设备；挡位：" + gear);
        }
        mFasciaGunData.appDevice(mode, gear);
    }

    /**
     * APP 设置挡位
     */
    private void appSetGear() {
        int gear = sp_set_gear.getSelectedItemPosition();

        if (gear == -1) {
            addText("请先用设备回复支持的挡位数量");
            return;
        }

        addText("APP设置挡位：" + gear);
        mFasciaGunData.appSetGear(gear);
    }

    /**
     * APP 设置倒计时
     */
    private void appSetTime() {
        int mode = rb_time_start.isChecked() ? 1 : 0;
        int second = 0;
        try {
            second = Integer.parseInt(et_time.getText().toString());
        } catch (Exception e) {
            second = 0;
        }

        if (mode == 1) {
            addText("APP设置倒计时：启动；时间：" + second + "秒");
        } else {
            addText("APP设置倒计时：停止；时间：" + second + "秒");
        }
        mFasciaGunData.appSetTime(mode, second);
    }

    // 添加一条文本
    private void addText(String text) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
        mList.add(sdf.format(System.currentTimeMillis()) + "：\n" + text);
        mListAdapter.notifyDataSetChanged();
        list_view.smoothScrollToPosition(mList.size() - 1);
    }
}

package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.net.aicare.modulelibrary.module.HeightWeightScale.HeightBodyFatBleData;
import cn.net.aicare.modulelibrary.module.HeightWeightScale.HeightBodyFatBleUntils;

/**
 * 身高体脂秤
 */
public class HeightWeightScaleActivity extends BleBaseActivity implements OnCallbackBle, HeightBodyFatBleData.OnHeightBodyFatDataCallback, View.OnClickListener {
    private String mAddress = "";
    private List<String> logList;
    private ListView listView;
    private ArrayAdapter listAdapter;
    private int selectWUnit;
    private int selectHUnit;
    private int currentWUnit = 0;
    private int currentHUnit = 0;
    private int currentMode = 1;
    private int selectMode = 1;
    private final int SETUNIT = 0;
    private final int SETUSER = 1;
    private final int SETMODE = 2;
    private final int ADC = 3;
    private final int FINISH = 4;
    private final int DEVICESTATUS = 5;
    private final int VOICE = 6;
    private final int VOICESTATUS = 7;
    private MHandler mMHandler;
    private TextView text;
    private int currentVoice = 1;
    private int selectVoice = 1;

    @Override
    public void onServiceSuccess() {
        mBluetoothService.setOnCallback(this);
        logList.add(0, "绑定服务成功");
        if (mBluetoothService != null) {
            mBluetoothService.setOnCallback(this);
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                HeightBodyFatBleData.init(bleDevice);
                HeightBodyFatBleData.getInstance().setOnHeightBodyFatDataCallback(this);
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height_bodyfat);
        mAddress = getIntent().getStringExtra("mac");
        logList = new ArrayList<>();

        listView = findViewById(R.id.listView);
        mMHandler = new WeakReference<>(new MHandler()).get();
        findViewById(R.id.kg).setOnClickListener(this);
        findViewById(R.id.jin).setOnClickListener(this);
        findViewById(R.id.st_lb).setOnClickListener(this);
        findViewById(R.id.lb).setOnClickListener(this);
        findViewById(R.id.girl).setOnClickListener(this);
        findViewById(R.id.man).setOnClickListener(this);
        findViewById(R.id.cm).setOnClickListener(this);
        findViewById(R.id.inch).setOnClickListener(this);
        findViewById(R.id.ft_in).setOnClickListener(this);
        findViewById(R.id.bodyfat).setOnClickListener(this);
        findViewById(R.id.weight).setOnClickListener(this);
        findViewById(R.id.baby).setOnClickListener(this);
        findViewById(R.id.deviceStatus).setOnClickListener(this);
        findViewById(R.id.voice_open).setOnClickListener(this);
        findViewById(R.id.voice_close).setOnClickListener(this);
        findViewById(R.id.voice_status).setOnClickListener(this);
        findViewById(R.id.finish).setOnClickListener(this);
        findViewById(R.id.height_weight).setOnClickListener(this);
        text = findViewById(R.id.text);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, logList);
        listView.setAdapter(listAdapter);
    }

    @Override
    public void onStartScan() {

    }

    @Override
    public void onScanning(BleValueBean data) {
        if (data.getMac().equalsIgnoreCase(mAddress)){
          connectBle(data.getMac());
        }

    }

    @Override
    public void onScanTimeOut() {

    }

    @Override
    public void onConnecting(String mac) {

    }

    @Override
    public void onDisConnected(String mac, int code) {
        if (mac.equals(mAddress)) {
            logList.add(0, "连接已经断开");
            listAdapter.notifyDataSetChanged();
           startScanBle(0);

        }

    }

    @Override
    public void onConnectionSuccess(String mac) {

    }

    @Override
    public void onServicesDiscovered(String mac) {
        BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
        if (bleDevice != null) {
            HeightBodyFatBleData.init(bleDevice);
            HeightBodyFatBleData.getInstance().setOnHeightBodyFatDataCallback(this);
        }

    }

    @Override
    public void bleOpen() {

    }

    @Override
    public void bleClose() {

    }

    @Override
    public void onVersion(String version) {
        logList.add(0, "版本号:" + version);

    }

    @Override
    public void onSupportUnitList(List<SupportUnitBean> list) {

    }

    @Override
    public void onMcuResult(int type, int result) {
        String resultStr = "";
        if (result == HeightBodyFatBleUntils.SET_SUCCESS) {
            resultStr = "成功";
        } else if (result == HeightBodyFatBleUntils.SET_FAIL) {
            resultStr = "失败";

        } else {
            resultStr = "不支持";
        }
        switch (type) {
            case HeightBodyFatBleUntils.MCU_SET_USER_RESULT:
                mMHandler.removeMessages(SETUSER);

                logList.add(0, "下发用户结果:" + resultStr);
                break;
            case HeightBodyFatBleUntils.MCU_SET_UNIT_RESULT:
                if (result == 0) {
                    currentHUnit = selectHUnit;
                    currentWUnit = selectWUnit;
                    if (currentHUnit == 0) {

                    }
                    text.setText("体重单位： " + currentWUnit +
                            "\n身高单位： " + currentHUnit +
                            "\n模式:" + currentMode + " 声音： " + currentVoice);
                }
                mMHandler.removeMessages(SETUNIT);
                logList.add(0, "设置单位结果:" + resultStr);
                break;
            case HeightBodyFatBleUntils.MCU_SET_WORK_MODE_RESULT:
                if (result == 0) {
                    currentMode = selectMode;
                    text.setText("体重单位： " + currentWUnit +
                            "\n身高单位： " + currentHUnit +
                            "\n模式:" + currentMode + " 声音： " + currentVoice);
                }
                mMHandler.removeMessages(SETMODE);
                logList.add(0, "设置工作模式:" + resultStr);
                break;
            case HeightBodyFatBleUntils.MUC_REQUEST_VOICE_SET_RESULT:
                if (result == 0) {
                    currentVoice = selectVoice;
                    text.setText("体重单位： " + currentWUnit +
                            "\n身高单位： " + currentHUnit +
                            "\n模式:" + currentMode + " 声音： " + currentVoice);
                }
                mMHandler.removeMessages(VOICE);
                logList.add(0, "设置声音结果:" + resultStr);
                break;

        }
        listAdapter.notifyDataSetChanged();


    }

    @Override
    public void onMcuRequestUser() {
        logList.add(0, "请求下发用户，点击下发用户");
        listAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDeviceStatus(int workMode, int battery, int batteryStatus, int weightUnit, int heightUnit, int voice) {
        mMHandler.removeMessages(DEVICESTATUS);
        currentMode = workMode;
        currentWUnit = weightUnit;
        currentHUnit = heightUnit;
        currentVoice = voice;
        text.setText("体重单位： " + currentWUnit +
                "\n身高单位： " + currentHUnit +
                "\n模式:" + currentMode + " 声音： " + currentVoice);
        logList.add(0, "体重状态 模式:" + workMode + " 电池电量: " + battery + " 电池状态: " + batteryStatus
                + "\n 体重单位: " + weightUnit + " 身高单位: " + heightUnit + " 声音状态: " + voice);
        listAdapter.notifyDataSetChanged();

    }

    @Override
    public void onWeightBodyFat(int workMode, int weightMode, int weight, int decimals, int unit) {
        logList.add(0, "体重体脂 模式:" + workMode + " 测量状态: " + weightMode + "\n 体重: " + weight
                + "  小数位: " + decimals + " 单位: " + unit);
        listAdapter.notifyDataSetChanged();
        if (weightMode == 2) {
            mMHandler.removeMessages(ADC);
            mMHandler.removeMessages(FINISH);
            mMHandler.sendEmptyMessageDelayed(ADC, 30000);
            mMHandler.sendEmptyMessageDelayed(FINISH, 40000);
        }

    }

    @Override
    public void onWeightBaby(int workMode, int weightMode, int adultWeight, int adultBabyWeight, int babyWeight, int decimals, int unit) {
        logList.add(0, "抱婴体重 模式：" + workMode + " 测量状态：" + weightMode + " 成人体重：" + adultWeight +
                "\n 抱婴体重：" + adultBabyWeight + " 婴儿体重：" + babyWeight + " 小数位：" + decimals + " 单位" + unit);
        listAdapter.notifyDataSetChanged();
        if (weightMode == 2) {
            mMHandler.removeMessages(FINISH);

            mMHandler.sendEmptyMessageDelayed(FINISH, 40000);
        }


    }

    @Override
    public void onWeight(int workMode, int weightMode, int weight, int decimals, int unit) {
        logList.add(0, "体重 模式:" + workMode + " 测量状态: " + weightMode + "\n 体重: " + weight
                + "  小数位: " + decimals + " 单位: " + unit);
        listAdapter.notifyDataSetChanged();
        if (weightMode == 2) {
            mMHandler.removeMessages(FINISH);
            mMHandler.sendEmptyMessageDelayed(FINISH, 40000);
        }


    }

    @Override
    public void onAdc(int workMode, int status, int adcType, int adc, int arithmetic) {
        mMHandler.removeMessages(ADC);
        logList.add(0, "阻抗数据 模式:" + workMode + " 测量状态: " + status + "\n 阻抗类型: " + adcType
                + "  阻抗: " + adc + " 算法位: " + arithmetic);
        listAdapter.notifyDataSetChanged();


    }

    @Override
    public void onHeart(int workMode, int status, int heart) {
        logList.add(0, "心率数据 模式:" + workMode + " 测量状态: " + status + " 心率: " + heart);
        listAdapter.notifyDataSetChanged();


    }

    @Override
    public void onTEMP(int workMode, int sign, int temp, int decimals, int unit) {
        logList.add(0, "温度数据 模式:" + workMode + " 温度: " + temp + " 正负: " + sign + "小数 : " + decimals + " 单位 : " + unit);
        listAdapter.notifyDataSetChanged();

    }

    @Override
    public void onHeight(int workMode, int height, int decimals, int unit) {
        logList.add(0, "身高数据 模式:" + workMode + " 身高: " + height + " 小数位: " + decimals + "单位 : " + unit);
        listAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBodyfat1(int workMode, int bfr, int sfr, int vfr, int rom, int bmr, int age) {
        logList.add(0, "体脂数据 模式:" + workMode + "\n 体脂率: " + bfr + " 皮下脂肪: " + sfr
                + "\n 内脏脂肪: " + vfr + " 肌肉路: " + rom + "\n 基础代谢率: " + bmr + " 年龄" + age);
        listAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBodyfat2(int workMode, int bm, int water, int pp, int bmi, int heart, int obesityLevels) {
        logList.add(0, "体脂数据 模式:" + workMode + " 骨量: " + bm + " 水分: " + water
                + "\n 蛋白率: " + pp + " bmi: " + bmi + " 心率: " + heart + " \n肥胖等级" + obesityLevels);
        listAdapter.notifyDataSetChanged();


    }

    @Override
    public void onVoice(int status) {
        mMHandler.removeMessages(VOICESTATUS);
        logList.add(0, "声音状态 模式:" + status);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFinish(int workMode) {
        logList.add(0, "测量完成 模式:" + workMode);
        listAdapter.notifyDataSetChanged();
        mMHandler.removeMessages(FINISH);

    }

    @Override
    public void onError(int error) {
        logList.add(0, "错误码: " + error);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (HeightBodyFatBleData.getInstance() != null) {

            switch (v.getId()) {
                case R.id.kg:
                    selectWUnit = 0;
                    logList.add(0, "下发体重单位 :" + selectWUnit);
                    listAdapter.notifyDataSetChanged();
                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.setUnit(currentHUnit, selectWUnit));
                    mMHandler.removeMessages(SETUNIT);
                    mMHandler.sendEmptyMessageDelayed(SETUNIT, 3000);
                    break;
                case R.id.jin:
                    selectWUnit = 1;
                    logList.add(0, "下发体重单位 :" + selectWUnit);
                    listAdapter.notifyDataSetChanged();
                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.setUnit(currentHUnit, selectWUnit));
                    mMHandler.removeMessages(SETUNIT);
                    mMHandler.sendEmptyMessageDelayed(SETUNIT, 3000);
                    break;
                case R.id.st_lb:
                    selectWUnit = 4;
                    logList.add(0, "下发体重单位 :" + selectWUnit);
                    listAdapter.notifyDataSetChanged();
                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.setUnit(currentHUnit, selectWUnit));
                    mMHandler.removeMessages(SETUNIT);
                    mMHandler.sendEmptyMessageDelayed(SETUNIT, 3000);
                    break;
                case R.id.lb:
                    selectWUnit = 6;
                    logList.add(0, "下发体重单位 :" + selectWUnit);
                    listAdapter.notifyDataSetChanged();
                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.setUnit(currentHUnit, selectWUnit));
                    mMHandler.removeMessages(SETUNIT);
                    mMHandler.sendEmptyMessageDelayed(SETUNIT, 3000);
                    break;
                case R.id.girl:
                    logList.add(0, "下用户 女 18岁 165cm :" + selectWUnit);
                    listAdapter.notifyDataSetChanged();
                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.setUser(0, 18, 165));
                    mMHandler.removeMessages(SETUSER);
                    mMHandler.sendEmptyMessageDelayed(SETUSER, 3000);
                    break;
                case R.id.man:
                    logList.add(0, "下用户 男 28岁 170cm :" + selectWUnit);
                    listAdapter.notifyDataSetChanged();

                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.setUser(1, 28, 170));
                    mMHandler.removeMessages(SETUSER);
                    mMHandler.sendEmptyMessageDelayed(SETUSER, 3000);

                    break;
                case R.id.cm:
                    selectHUnit = 0;
                    logList.add(0, "下发身高单位 :" + selectHUnit);
                    listAdapter.notifyDataSetChanged();
                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.setUnit(selectHUnit, currentWUnit));
                    mMHandler.removeMessages(SETUNIT);
                    mMHandler.sendEmptyMessageDelayed(SETUNIT, 3000);
                    break;
                case R.id.inch:
                    selectHUnit = 1;
                    logList.add(0, "下发身高单位 :" + selectHUnit);
                    listAdapter.notifyDataSetChanged();
                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.setUnit(selectHUnit, currentWUnit));
                    mMHandler.removeMessages(SETUNIT);
                    mMHandler.sendEmptyMessageDelayed(SETUNIT, 3000);
                    break;
                case R.id.ft_in:
                    selectHUnit = 2;
                    logList.add(0, "下发身高单位 :" + selectHUnit);
                    listAdapter.notifyDataSetChanged();
                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.setUnit(selectHUnit, currentWUnit));
                    mMHandler.removeMessages(SETUNIT);
                    mMHandler.sendEmptyMessageDelayed(SETUNIT, 3000);
                    break;
                case R.id.baby:
                    selectMode = 2;
                    logList.add(0, "下发工作模式 :" + selectMode);
                    listAdapter.notifyDataSetChanged();
                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.setWorkMode(selectMode));
                    mMHandler.removeMessages(SETMODE);
                    mMHandler.sendEmptyMessageDelayed(SETMODE, 3000);
                    break;
                case R.id.bodyfat:
                    selectMode = 1;
                    logList.add(0, "下发工作模式 :" + selectMode);
                    listAdapter.notifyDataSetChanged();
                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.setWorkMode(selectMode));
                    mMHandler.removeMessages(SETMODE);
                    mMHandler.sendEmptyMessageDelayed(SETMODE, 3000);
                    break;
                case R.id.weight:
                    selectMode = 3;
                    logList.add(0, "下发工作模式 :" + selectMode);
                    listAdapter.notifyDataSetChanged();
                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.setWorkMode(selectMode));
                    mMHandler.removeMessages(SETMODE);
                    mMHandler.sendEmptyMessageDelayed(SETMODE, 3000);
                    break;
                case R.id.height_weight:
                    selectMode = 4;
                    logList.add(0, "下发工作模式 :" + selectMode);
                    listAdapter.notifyDataSetChanged();
                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.setWorkMode(selectMode));
                    mMHandler.removeMessages(SETMODE);
                    mMHandler.sendEmptyMessageDelayed(SETMODE, 3000);
                    break;
                case R.id.deviceStatus:

                    logList.add(0, "请求设备状态 :" );
                    listAdapter.notifyDataSetChanged();
                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.getDeviceStatus());
                    mMHandler.removeMessages(DEVICESTATUS);
                    mMHandler.sendEmptyMessageDelayed(DEVICESTATUS, 3000);
                    break;
                case R.id.voice_open:
                    selectVoice = 1;
                    logList.add(0, "设置声音 :" + selectMode);
                    listAdapter.notifyDataSetChanged();
                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.setVoiceStatus(selectVoice));
                    mMHandler.removeMessages(VOICE);
                    mMHandler.sendEmptyMessageDelayed(VOICE, 3000);
                    break;
                case R.id.voice_close:
                    selectVoice = 2;
                    logList.add(0, "设置声音 :" + selectMode);
                    listAdapter.notifyDataSetChanged();
                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.setVoiceStatus(selectVoice));
                    mMHandler.removeMessages(VOICE);
                    mMHandler.sendEmptyMessageDelayed(VOICE, 3000);
                    break;
                case R.id.voice_status:

                    logList.add(0, "获取声音状态");
                    listAdapter.notifyDataSetChanged();
                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.getVoiceStatus());
                    mMHandler.removeMessages(VOICESTATUS);
                    mMHandler.sendEmptyMessageDelayed(VOICESTATUS, 3000);
                    break;
                case R.id.finish:
                    logList.add(0, "回复测量完成");
                    listAdapter.notifyDataSetChanged();
                    HeightBodyFatBleData.getInstance().sendData(HeightBodyFatBleUntils.replyTestFinish());
                    break;



            }
        }
    }

    public class MHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SETMODE:
                    logList.add(0, "下发模式没有回复");
                    listAdapter.notifyDataSetChanged();
                    break;
                case SETUNIT:
                    logList.add(0, "下发单位没有回复");
                    listAdapter.notifyDataSetChanged();
                    break;
                case SETUSER:
                    logList.add(0, "下发用户没有回复");
                    listAdapter.notifyDataSetChanged();
                    break;
                case FINISH:
                    logList.add(0, "没有回复测量完成指令(\n为了检测是否有回复。" +
                            "app会在接收到稳定体重的时候。开启定时器。去确认设备是否有回复测量完成。" +
                            "\n如果回复。此消息可以忽略)"+"\n 每次测量（母婴模式。的测量完成也只能发一次），测量完成的命令只能发一次！！！！。");
                    listAdapter.notifyDataSetChanged();
                    break;
                case ADC:
                    logList.add(0, "没有测量阻抗的指令(\n为了检测是否有回复。app会在接收到稳定体重的时候。开启定时器。去确认设备是否有回复测量阻抗的状态。\n如果回复。此消息可以忽略)");
                    listAdapter.notifyDataSetChanged();
                    break;
                case DEVICESTATUS:
                    logList.add(0, "没有接收到设备状态(\n为了检测是否有回复。app会在点击获取设备状态指令的时候。开启定时器。去确认设备是否有回复设备状态。\n如果回复。此消息可以忽略)");
                    listAdapter.notifyDataSetChanged();
                    break;
                case VOICE:
                    logList.add(0, "没有声音设置的回复(\n为了检测是否有回复。app会在点击设置声音的时候。开启定时器。去确认设备是否有回复设置声音。\n如果回复。此消息可以忽略)");
                    listAdapter.notifyDataSetChanged();
                    break;
                case VOICESTATUS:
                    logList.add(0, "没有声音状态的回复(\n为了检测是否有回复。app会在点击声音状态的时候。开启定时器。去确认设备是否有回复声音状态。\n如果回复。此消息可以忽略)");
                    listAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}

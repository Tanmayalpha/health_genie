package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.airdetector;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;

import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendDataBean;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleAppBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.airdetector_test.AirDetectorActivityTest;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;
import cn.net.aicare.modulelibrary.module.airDetector.AirConst;
import cn.net.aicare.modulelibrary.module.airDetector.AirDetectorWifeBleData;
import cn.net.aicare.modulelibrary.module.airDetector.AirSendUtil;
import cn.net.aicare.modulelibrary.module.airDetector.StatusBean;
import cn.net.aicare.modulelibrary.module.airDetector.SupportBean;

/**
 * @author yesp
 */
public class AirDetectorActivity extends BleAppBaseActivity implements AirDetectorWifeBleData.AirDataInterface, View.OnClickListener, OnCallbackBle , OnBleOtherDataListener {
    private String mAddress;

    private Button btn_auto_test_tools, btn_support_list, btn_device_state, btn_param, btn_set_param, btn_change_log;
    private EditText ed_type, ed_max, ed_min, ed_warm_state, ed_value;
    private ListView list_view;
    private ListView list_view_payload;
    private List<String> mList;
    private List<String> mListPayload;
    private ArrayAdapter listAdapter;
    private ArrayAdapter listAdapterPayload;
    private AirDetectorWifeBleData mAirDetectorWifeBleData;
    private SparseArray<SupportBean> supportList;
    private static final String TAG = "AirDetectorActivity";

    private ArrayList<String> typeNames = new ArrayList<>();
    LinkedHashMap<String, SetType> maps = new LinkedHashMap<>();
    private int selectType = -1;
    private AppCompatSpinner sp_set_type, sp_mode;
    private LinearLayout layout_type, layout_switch, layout_max, layout_min, layout_value;
    private RadioGroup group_cal, group_alarm;
    private CheckBox cb_day1, cb_day2, cb_day3, cb_day4, cb_day5, cb_day6, cb_day7;
    private EditText ed_time, ed_cid;
    private ConstraintLayout layout_alarm;
    private Button btn_stop;
    private boolean stopFlag = false;


    @Override
    protected void uiHandlerMessage(Message msg) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_air_detector;
    }

    @Override
    protected void initListener() {
        sp_set_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    SetType setType = maps.get(typeNames.get(position));
                    if (setType != null) {
                        selectType = setType.type;
                        showParams(setType.showType);
                    }
                } else {
                    selectType = -1;
                    showParams(-1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showParams(int showType) {
        switch (showType) {
            case SetType.TYPE_VALUE_SWITCH:
                layout_switch.setVisibility(View.VISIBLE);
                layout_value.setVisibility(View.VISIBLE);
                layout_type.setVisibility(View.GONE);
                layout_max.setVisibility(View.GONE);
                layout_min.setVisibility(View.GONE);
                group_cal.setVisibility(View.GONE);
                layout_alarm.setVisibility(View.GONE);
                break;
            case SetType.TYPE_MIN_MAX_SWITCH:
                layout_switch.setVisibility(View.VISIBLE);
                layout_max.setVisibility(View.VISIBLE);
                layout_min.setVisibility(View.VISIBLE);
                layout_type.setVisibility(View.GONE);
                layout_value.setVisibility(View.GONE);
                group_cal.setVisibility(View.GONE);
                layout_alarm.setVisibility(View.GONE);
                break;
            case SetType.TYPE_SWITCH:
                layout_switch.setVisibility(View.VISIBLE);
                layout_type.setVisibility(View.GONE);
                layout_max.setVisibility(View.GONE);
                layout_min.setVisibility(View.GONE);
                layout_value.setVisibility(View.GONE);
                group_cal.setVisibility(View.GONE);
                layout_alarm.setVisibility(View.GONE);
                break;
            case SetType.TYPE_CAL:
                group_cal.setVisibility(View.VISIBLE);
                layout_type.setVisibility(View.VISIBLE);
                layout_switch.setVisibility(View.GONE);
                layout_max.setVisibility(View.GONE);
                layout_min.setVisibility(View.GONE);
                layout_value.setVisibility(View.GONE);
                layout_alarm.setVisibility(View.GONE);
                break;
            case SetType.TYPE_VALUE:
                layout_value.setVisibility(View.VISIBLE);
                layout_switch.setVisibility(View.GONE);
                layout_type.setVisibility(View.GONE);
                layout_max.setVisibility(View.GONE);
                layout_min.setVisibility(View.GONE);
                group_cal.setVisibility(View.GONE);
                layout_alarm.setVisibility(View.GONE);
                break;
            case SetType.TYPE_ALARM:
                layout_alarm.setVisibility(View.VISIBLE);
                layout_switch.setVisibility(View.GONE);
                layout_type.setVisibility(View.GONE);
                layout_max.setVisibility(View.GONE);
                layout_min.setVisibility(View.GONE);
                layout_value.setVisibility(View.GONE);
                group_cal.setVisibility(View.GONE);
                break;
            default:
                layout_alarm.setVisibility(View.GONE);
                layout_switch.setVisibility(View.GONE);
                layout_type.setVisibility(View.GONE);
                layout_max.setVisibility(View.GONE);
                layout_min.setVisibility(View.GONE);
                layout_value.setVisibility(View.GONE);
                group_cal.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void initData() {
        AirConst.DEVICE_CID = getIntent().getIntExtra("cid", AirConst.NO_MQTT_DEVICE_CID);
        mList = new ArrayList<>();
        mListPayload = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        listAdapterPayload = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListPayload);
        list_view.setAdapter(listAdapter);
        list_view_payload.setAdapter(listAdapterPayload);
        initSettingTypes();
        initAlarmModeSP();
    }

    private void initAlarmModeSP() {
        ArrayList<String> modeList = new ArrayList<>();
        modeList.add("选择模式");
        modeList.add("0:一次性");
        modeList.add("1:每天都响");
        modeList.add("2:周一至周五");
        modeList.add("3:周一至周六");
        modeList.add("4:自定义");
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modeList);
        sp_mode.setAdapter(modeAdapter);
    }

    private void initSettingTypes() {
        maps = AirDetectorShowUtil.initSettingTypes();
        typeNames.clear();
        typeNames.add("请选择类型");
        for (Map.Entry<String, SetType> entry : maps.entrySet()) {
            typeNames.add(entry.getKey());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeNames);
        sp_set_type.setAdapter(spinnerAdapter);
    }

    @Override
    protected void initView() {
        btn_auto_test_tools = findViewById(R.id.btn_auto_test_tools);
        btn_support_list = findViewById(R.id.btn_support_list);
        btn_device_state = findViewById(R.id.btn_device_state);
        btn_param = findViewById(R.id.btn_param);
        ed_max = findViewById(R.id.ed_max);
        ed_type = findViewById(R.id.ed_type);
        ed_min = findViewById(R.id.ed_min);
        ed_value = findViewById(R.id.ed_value);
        ed_warm_state = findViewById(R.id.ed_warm_state);
        list_view = findViewById(R.id.list_view);
        btn_set_param = findViewById(R.id.btn_set_param);
        list_view_payload = findViewById(R.id.list_view_payload);
        btn_change_log = findViewById(R.id.btn_change_log);
        sp_set_type = findViewById(R.id.spinner);
        layout_type = findViewById(R.id.layout_type);
        layout_max = findViewById(R.id.layout_max);
        layout_min = findViewById(R.id.layout_min);
        layout_value = findViewById(R.id.layout_value);
        layout_switch = findViewById(R.id.layout_switch);
        group_cal = findViewById(R.id.group_cal);
        btn_stop = findViewById(R.id.btn_stop);
        // 闹钟
        layout_alarm = findViewById(R.id.layout_alarm);
        ed_time = findViewById(R.id.ed_time);
        ed_cid = findViewById(R.id.ed_cid);
        group_alarm = findViewById(R.id.group_alarm);
        sp_mode = findViewById(R.id.sp_mode);
        cb_day1 = findViewById(R.id.cb_day1);
        cb_day2 = findViewById(R.id.cb_day2);
        cb_day3 = findViewById(R.id.cb_day3);
        cb_day4 = findViewById(R.id.cb_day4);
        cb_day5 = findViewById(R.id.cb_day5);
        cb_day6 = findViewById(R.id.cb_day6);
        cb_day7 = findViewById(R.id.cb_day7);

        btn_auto_test_tools.setOnClickListener(this);
        btn_support_list.setOnClickListener(this);
        btn_device_state.setOnClickListener(this);
        btn_param.setOnClickListener(this);
        btn_set_param.setOnClickListener(this);
        btn_change_log.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
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
        mBluetoothService.setOnCallback(this);
        if (mAirDetectorWifeBleData == null) {
            AirDetectorWifeBleData.init(bleDevice);
            mAirDetectorWifeBleData = AirDetectorWifeBleData.getInstance();
            mAirDetectorWifeBleData.setDataInterface(TAG, this);
            mAirDetectorWifeBleData.setOnBleOtherDataListener(this);
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
        if (mac.equals(mAddress) && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            BleLog.i(TAG, "设备断开连接，请退出重新连接");
            new AlertDialog.Builder(this)
                    .setTitle("ERROR：")
                    .setMessage("\n" + "设备断开连接，请退出重新连接")
                    .setPositiveButton("确认", (dialog, which) -> finish())
                    .show();
        }
    }

    public void onDataPayload(String bytes) {
        mListPayload.add(0, TimeUtils.getTime() + bytes);
        listAdapterPayload.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_auto_test_tools) {
            Intent intent = new Intent(this, AirDetectorActivityTest.class);
            intent.putExtras(getIntent());
            startActivityForResult(intent, 100);
        } else if (v.getId() == R.id.btn_change_log) {
            if (list_view_payload.getVisibility() == View.GONE) {
                list_view_payload.setVisibility(View.VISIBLE);
                list_view.setVisibility(View.GONE);
            } else {
                list_view_payload.setVisibility(View.GONE);
                list_view.setVisibility(View.VISIBLE);
            }
        } else if (v.getId() == R.id.btn_support_list) {
            if (isStopping()) return;
            if (mAirDetectorWifeBleData != null) {
                addTextWithTime("获取支持列表------------");
                mAirDetectorWifeBleData.sendData(AirSendUtil.getSupportList());
            }
        } else if (v.getId() == R.id.btn_device_state) {
            if (isStopping()) return;
            if (mAirDetectorWifeBleData != null) {
                if (!checkHasGetSupport()) return;
                addTextWithTime("获取实时状态------------");
                mAirDetectorWifeBleData.sendData(AirSendUtil.getDeviceState());
            }
        } else if (v.getId() == R.id.btn_param) {
            if (isStopping()) return;
            if (mAirDetectorWifeBleData != null) {
                if (!checkHasGetSupport()) return;
                addTextWithTime("获取参数设置------------");
                mAirDetectorWifeBleData.sendData(AirSendUtil.getSettingState());
            }
        } else if (v.getId() == R.id.btn_set_param) {
            if (isStopping()) return;
            sendSettingParams();
        } else if(v.getId() == R.id.btn_stop){
            if (stopFlag) {
                btn_stop.setText("暂停");
            } else {
                btn_stop.setText("刷新");
            }
            stopFlag = !stopFlag;
        }
    }


    /**
     * 添加一条文本
     *
     * @param text
     */
    private void addTextWithTime(String text) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
        mList.add(sdf.format(System.currentTimeMillis()) + "：\n" + text);
        listAdapter.notifyDataSetChanged();
        list_view.smoothScrollToPosition(mList.size() - 1);
    }

    /**
     * 添加一条文本
     *
     * @param text
     */
    private void addText(String text) {
        if (mList.size() > 1000) {
            ArrayList<String> list = new ArrayList<>(mList.subList(500, mList.size()));
            mList.clear();
            mList.addAll(list);
        }
        mList.add(text);
        listAdapter.notifyDataSetChanged();
        list_view.smoothScrollToPosition(mList.size() - 1);
    }

    /**
     * 发送设置指令
     */
    private void sendSettingParams() {
        try {
            if (!checkHasGetSupport()) return;
            SupportBean supportBean = supportList.get(selectType);
            if (supportBean == null || selectType == -1) {
                Toast.makeText(this, "当前类型不支持", Toast.LENGTH_SHORT).show();
                return;
            }
            SendDataBean sendDataBean = null;
            switch (selectType) {
                case AirConst.AIR_TYPE_FORMALDEHYDE:
                case AirConst.AIR_TYPE_PM_2_5:
                case AirConst.AIR_TYPE_PM_1:
                case AirConst.AIR_TYPE_PM_10:
                case AirConst.AIR_TYPE_VOC:
                case AirConst.AIR_TYPE_CO2:
                case AirConst.AIR_TYPE_AQI:
                case AirConst.AIR_TYPE_TVOC:
                case AirConst.AIR_TYPE_CO:
                    int warmState = Integer.parseInt(ed_warm_state.getText().toString().trim());
                    String value = ed_value.getText().toString().trim();
                    float myValue = value.contains(".") ? Float.parseFloat(value) : Integer.parseInt(value);
                    sendDataBean = AirSendUtil.setWarmMaxByType(selectType, warmState, myValue, supportBean.getPoint());
                    break;
                case AirConst.AIR_TYPE_TEMP:
                    String valueMax = ed_max.getText().toString().trim();
                    String valueMin = ed_min.getText().toString().trim();
                    float myValueMax = valueMax.contains("\\.") ? Float.parseFloat(valueMax) : Integer.parseInt(valueMax);
                    float myValueMin = valueMin.contains("\\.") ? Float.parseFloat(valueMin) : Integer.parseInt(valueMin);
                    sendDataBean = AirSendUtil.setWarmTemp(supportBean.getPoint(), supportBean.getUnit(), myValueMax, myValueMin,1);
                    break;
                case AirConst.AIR_TYPE_HUMIDITY:
                    valueMax = ed_max.getText().toString().trim();
                    valueMin = ed_min.getText().toString().trim();
                    myValueMax = valueMax.contains("\\.") ? Float.parseFloat(valueMax) : Integer.parseInt(valueMax);
                    myValueMin = valueMin.contains("\\.") ? Float.parseFloat(valueMin) : Integer.parseInt(valueMin);
                    sendDataBean = AirSendUtil.setWarmHumidity(supportBean.getPoint(),
                            myValueMax, myValueMin,1);
                    break;
                case AirConst.AIR_SETTING_VOICE:
                    warmState = Integer.parseInt(ed_warm_state.getText().toString().trim());
                    value = ed_value.getText().toString().trim();
                    sendDataBean = AirSendUtil.setVoice(warmState, Integer.parseInt(value));
                    break;
                case AirConst.AIR_SETTING_WARM_DURATION:
                    value = ed_value.getText().toString().trim();
                    sendDataBean = AirSendUtil.setWarmDuration(Integer.parseInt(value));
                    break;
                case AirConst.AIR_SETTING_WARM_VOICE:
                case AirConst.AIR_SETTING_DEVICE_SELF_TEST:
                case AirConst.AIR_RESTORE_FACTORY_SETTINGS:
                case AirConst.AIR_TIME_FORMAT:
                case AirConst.AIR_DATA_DISPLAY_MODE:
                    value = ed_value.getText().toString().trim();
                    sendDataBean = AirSendUtil.setTypeAndLengthOne(selectType, Integer.parseInt(value));
                    break;
                case AirConst.AIR_SETTING_SWITCH_TEMP_UNIT:
                    value = ed_value.getText().toString().trim();
                    sendDataBean = AirSendUtil.setUnit(Integer.parseInt(value));
                    break;
                case AirConst.AIR_KEY_SOUND:
                case AirConst.AIR_ALARM_SOUND_EFFECT:
                case AirConst.AIR_ICON_DISPLAY:
                case AirConst.AIR_MONITORING_DISPLAY_DATA:
                    warmState = Integer.parseInt(ed_warm_state.getText().toString().trim());
                    sendDataBean = AirSendUtil.setTypeAndSwitch(selectType, warmState);
                    break;
                case AirConst.AIR_BRIGHTNESS_EQUIPMENT:
                    warmState = Integer.parseInt(ed_warm_state.getText().toString().trim());
                    value = ed_value.getText().toString().trim();
                    sendDataBean = AirSendUtil.setBrightnessEquipment(warmState, Integer.parseInt(value));
                    break;
                case AirConst.AIR_CALIBRATION_PARAMETERS:
                    int calType = Integer.parseInt(ed_type.getText().toString().trim());
                    int operate = group_cal.getCheckedRadioButtonId() == R.id.rb_add ? 0 : 1;
                    sendDataBean = AirSendUtil.setCalibrationParam(calType, operate);
                    break;
                case AirConst.AIR_ALARM_CLOCK:
                    sendDataBean = getAlarmSendDataBean();
                    break;
                default:
                    break;
            }
            if (mAirDetectorWifeBleData != null && sendDataBean != null) {
                mAirDetectorWifeBleData.sendData(sendDataBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            new AlertDialog.Builder(this)
                    .setTitle("ERROR：")
                    .setMessage("\n" + "设置参数错误")
                    .setPositiveButton("确认", null)
                    .show();
        }
    }

    @Nullable
    private SendDataBean getAlarmSendDataBean() {
        int cid = Integer.parseInt(ed_cid.getText().toString().trim());
        String time = ed_time.getText().toString().trim();
        if (time.contains(":")) {
            String[] hourMinute = time.split(":");
            int hour = Integer.parseInt(hourMinute[0].trim());
            int minute = Integer.parseInt(hourMinute[1].trim());
            int mode = sp_mode.getSelectedItemPosition() - 1;
            if (mode < 0) {
                Toast.makeText(this, "请选择模式", Toast.LENGTH_SHORT).show();
                return null;
            }
            int day1 = cb_day1.isChecked() ? 1 : 0;
            int day2 = cb_day2.isChecked() ? 1 : 0;
            int day3 = cb_day3.isChecked() ? 1 : 0;
            int day4 = cb_day4.isChecked() ? 1 : 0;
            int day5 = cb_day5.isChecked() ? 1 : 0;
            int day6 = cb_day6.isChecked() ? 1 : 0;
            int day7 = cb_day7.isChecked() ? 1 : 0;
            int openAlarm = group_alarm.getCheckedRadioButtonId() == R.id.rb_open ? 1 : 0;
            boolean isDeleted = group_alarm.getCheckedRadioButtonId() == R.id.rb_delete;
            int[] days;
            switch (mode) {
                case 1:
                    days = new int[]{0, 1, 1, 1, 1, 1, 1, 1};
                    break;
                case 2:
                    days = new int[]{0, 1, 1, 1, 1, 1, 0, 0};
                    break;
                case 3:
                    days = new int[]{0, 1, 1, 1, 1, 1, 1, 0};
                    break;
                case 4:
                    days = new int[]{0, day1, day2, day3, day4, day5, day6, day7};
                    break;
                case 0:
                default:
                    days = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
                    break;
            }
            return AirSendUtil.setAlarm(cid, hour, minute, days, mode, openAlarm, isDeleted);
        } else {
            Toast.makeText(this, "时间格式错误", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private boolean checkHasGetSupport() {
        if (supportList == null || supportList.size() == 0) {
            Toast.makeText(this, "请先获取支持列表", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (mAirDetectorWifeBleData != null) {
            mAirDetectorWifeBleData = null;
        }
        if (mBluetoothService != null) {
            mBluetoothService.disconnectAll();
        }
        super.onDestroy();
    }

    @Override
    public void onSupportedList(SparseArray<SupportBean> supportList) {
        if (stopFlag) {
            return;
        }
        this.supportList = supportList;
        for (int i = 0; i < supportList.size(); i++) {
            SupportBean supportBean = supportList.get(supportList.keyAt(i));
            addText(AirDetectorShowUtil.getTextSupportShow(supportBean));
        }
        addTextWithTime("返回支持列表----------结束");
    }

    @Override
    public void onStatusList(SparseArray<StatusBean> statusList) {
        if (stopFlag || !checkHasGetSupport()) {
            return;
        }
        for (int i = 0; i < statusList.size(); i++) {
            StatusBean statusBean = statusList.get(statusList.keyAt(i));
            addText(AirDetectorShowUtil.getTextStatusShow(statusBean, supportList));
        }
    }

    @Override
    public void onSettingList(SparseArray<StatusBean> settingList) {
        if (stopFlag || !checkHasGetSupport()) {
            return;
        }
        for (int i = 0; i < settingList.size(); i++) {
            StatusBean statusBean = settingList.get(settingList.keyAt(i));
            addText(AirDetectorShowUtil.getResultSettingsShow(statusBean, supportList));
        }
    }

    private boolean isStopping() {
        if (stopFlag) {
            Toast.makeText(this, "请先点击刷新", Toast.LENGTH_SHORT).show();
        }
        return stopFlag;
    }

    @Override
    public void onNotifyOtherData(String uuid, byte[] hex) {
        String data = BleStrUtils.byte2HexStr(hex);
        addTextWithTime("接收到未知指令：" + data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2001) {
            finish();
        }
    }
}

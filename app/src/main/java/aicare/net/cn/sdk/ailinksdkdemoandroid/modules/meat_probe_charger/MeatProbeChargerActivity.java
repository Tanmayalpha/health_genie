package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.meat_probe_charger;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.device.BleDevice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.dialog.DialogStringImageBean;
import aicare.net.cn.sdk.ailinksdkdemoandroid.dialog.ShowListDialogFragment;
import cn.net.aicare.modulelibrary.module.meatprobecharger.ChargerProbeBean;
import cn.net.aicare.modulelibrary.module.meatprobecharger.MeatProbeChargerBleDevice;

/**
 * @auther ljl
 * 食物探针充电盒ble
 * on 2023/2/24
 */
public class MeatProbeChargerActivity extends BleBaseActivity implements View.OnClickListener, MeatProbeChargerBleDevice.OnMeatProbeChargerDataListener {
    private String mMac;
    private int mVid;
    private BleDevice mBleDevice;

    private ListView list_view;

    private List<String> mList;
    private ArrayAdapter mListAdapter;
    private List<String> mMacList;
    private List<DialogStringImageBean> mDialogMacList;

    private MeatProbeChargerBleDevice mMeatProbeChargerBleDevice;

    private Button btn_probe_version, btn_probe_sync_time, btn_probe_info, btn_probe_clear;
    private Button btn_probe_set, btn_probe_get, btn_probe_unit, btn_probe_set_alarm;
    private Button btn_probe_clear_alarm, btn_probe_set_hand;
    private EditText et_probe_set_hand;
    private TextView tv_version, tv_other;
    private List<ChargerProbeBean> mChargerProbeBeanList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meat_probe_charger);

        initView();
        mMac = getIntent().getStringExtra("mac");
        mVid = getIntent().getIntExtra("vid", 0);
        // 初始化列表
        mList = new ArrayList<>();
        mMacList = new ArrayList<>();
//        mMacList.add("44:33:22:11:10:10");
//        mMacList.add("44:33:22:11:10:09");
//        mMacList.add("44:33:22:11:10:08");
//        mMacList.add("44:33:22:11:10:07");
        mDialogMacList = new ArrayList<>();
//        mDialogMacList.add(new DialogStringImageBean("44:33:22:11:10:10", 0));
//        mDialogMacList.add(new DialogStringImageBean("44:33:22:11:10:09", 0));
//        mDialogMacList.add(new DialogStringImageBean("44:33:22:11:10:08", 0));
//        mDialogMacList.add(new DialogStringImageBean("44:33:22:11:10:07", 0));
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        list_view.setAdapter(mListAdapter);
    }

    private void initView() {
        list_view = findViewById(R.id.list_view);
        btn_probe_version = findViewById(R.id.btn_probe_version);
        btn_probe_sync_time = findViewById(R.id.btn_probe_sync_time);
        btn_probe_info = findViewById(R.id.btn_probe_info);
        btn_probe_set = findViewById(R.id.btn_probe_set);
        btn_probe_get = findViewById(R.id.btn_probe_get);
        btn_probe_unit = findViewById(R.id.btn_probe_unit);
        btn_probe_set_alarm = findViewById(R.id.btn_probe_set_alarm);
        btn_probe_clear = findViewById(R.id.btn_probe_clear);
        btn_probe_clear_alarm = findViewById(R.id.btn_probe_clear_alarm);
        btn_probe_set_hand = findViewById(R.id.btn_probe_set_hand);
        et_probe_set_hand = findViewById(R.id.et_probe_set_hand);
        btn_probe_version.setOnClickListener(this);
        btn_probe_sync_time.setOnClickListener(this);
        btn_probe_info.setOnClickListener(this);
        btn_probe_set.setOnClickListener(this);
        btn_probe_get.setOnClickListener(this);
        btn_probe_unit.setOnClickListener(this);
        btn_probe_set_alarm.setOnClickListener(this);
        btn_probe_clear.setOnClickListener(this);
        btn_probe_clear_alarm.setOnClickListener(this);
        btn_probe_set_hand.setOnClickListener(this);

        tv_version = findViewById(R.id.tv_probe_version);
        tv_other = findViewById(R.id.tv_probe_other);

        et_probe_set_hand.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_probe_version:
                //获取版本号
                mMeatProbeChargerBleDevice.getVersion();
                break;
            case R.id.btn_probe_sync_time:
                //同步时间
                mMeatProbeChargerBleDevice.appSyncTime();
                break;
            case R.id.btn_probe_info:
                //获取设备状态
                mMeatProbeChargerBleDevice.getDeviceInfo();
                break;
            case R.id.btn_probe_set:
                //设置探针参数，需要选择探针
                if (mDialogMacList.size() > 0) {
                    setProbeDataDialog();
                } else {
                    Toast.makeText(MeatProbeChargerActivity.this, "请确认充电盒当前是否有连接的探针,点击获取设备状态按钮试试", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_probe_get:
                //获取探针参数,先判断是否有探针再选择探针获取数据，需要选择探针
                if (mDialogMacList.size() > 0) {
                    getProbeDataDialog();
                } else {
                    Toast.makeText(MeatProbeChargerActivity.this, "请确认充电盒当前是否有连接的探针,点击获取设备状态按钮试试", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_probe_clear:
                //清除探针数据,需要选择探针
                if (mDialogMacList.size() > 0) {
                    clearProbeDataDialog();
                } else {
                    Toast.makeText(MeatProbeChargerActivity.this, "请确认充电盒当前是否有连接的探针,点击获取设备状态按钮试试", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_probe_unit:
                //切换盒子单位
                if (("切换成℃").equals(btn_probe_unit.getText().toString())) {
                    btn_probe_unit.setText("切换成℉");
                    mMeatProbeChargerBleDevice.switchUnit(true);
                } else {
                    btn_probe_unit.setText("切换成℃");
                    mMeatProbeChargerBleDevice.switchUnit(false);
                }
                break;
            case R.id.btn_probe_set_alarm:
                //发送报警设置,需要选择探针
                if (mDialogMacList.size() > 0) {
                    setAlarmDialog();
                } else {
                    Toast.makeText(MeatProbeChargerActivity.this, "请确认充电盒当前是否有连接的探针,点击获取设备状态按钮试试", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_probe_clear_alarm:
                //清除报警设置,需要选择探针
                if (mDialogMacList.size() > 0) {
                    cancelDialog();
                } else {
                    Toast.makeText(MeatProbeChargerActivity.this, "请确认充电盒当前是否有连接的探针,点击获取设备状态按钮试试", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_probe_set_hand:
                //发送握手命令进行握手
                if (!TextUtils.isEmpty(et_probe_set_hand.getText().toString())) {
                    String cidvidpid = et_probe_set_hand.getText().toString().replaceAll("，", ",");
                    if (cidvidpid.contains(",")) {
                        String[] split = cidvidpid.split(",");
                        int cid = 0, vid = 0, pid = 0;
                        cid = Integer.parseInt(split[0], 16);
                        if (split.length > 1) {
                            vid = Integer.parseInt(split[1], 16);
                        }
                        if (split.length > 2) {
                            pid = Integer.parseInt(split[2], 16);
                        }
                        BleConfig.setHandshakeStatus(mMac, true, cid, vid, pid);
                    }
                } else {
                    Toast.makeText(MeatProbeChargerActivity.this, "请先输入CID,VID,PID", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 取消报警
     */
    private void cancelDialog() {
        ShowListDialogFragment.newInstance().setTitle("选择探针").setCancel("取消", 0).setCancelBlank(true).setBackground(true).setBottom(false).setList(mDialogMacList)
                .setOnDialogListener(new ShowListDialogFragment.onDialogListener() {
                    @Override
                    public void onItemListener(int position) {
                        Toast.makeText(MeatProbeChargerActivity.this, mDialogMacList.get(position).getName() + "设置该指令", Toast.LENGTH_SHORT).show();
                        mMeatProbeChargerBleDevice.cancelAlarm(mDialogMacList.get(position).getName());
                    }
                }).show(getSupportFragmentManager());
    }

    /**
     * 发送报警设置
     */
    private void setAlarmDialog() {
        ShowListDialogFragment.newInstance().setTitle("选择探针").setCancel("取消", 0).setCancelBlank(true).setBackground(true).setBottom(false).setList(mDialogMacList)
                .setOnDialogListener(new ShowListDialogFragment.onDialogListener() {
                    @Override
                    public void onItemListener(int position) {
                        Toast.makeText(MeatProbeChargerActivity.this, mDialogMacList.get(position).getName() + "设置该指令", Toast.LENGTH_SHORT).show();
                        mMeatProbeChargerBleDevice.setAlarmInfo(mDialogMacList.get(position).getName(), true, true, true);
                    }
                }).show(getSupportFragmentManager());
    }

    /**
     * 清除探针数据
     */
    private void clearProbeDataDialog() {
        ShowListDialogFragment.newInstance().setTitle("选择探针").setCancel("取消", 0).setCancelBlank(true).setBackground(true).setBottom(false).setList(mDialogMacList)
                .setOnDialogListener(new ShowListDialogFragment.onDialogListener() {
                    @Override
                    public void onItemListener(int position) {
                        Toast.makeText(MeatProbeChargerActivity.this, mDialogMacList.get(position).getName() + "设置该指令", Toast.LENGTH_SHORT).show();
                        mMeatProbeChargerBleDevice.setProbeData(0, mDialogMacList.get(position).getName());
                    }
                }).show(getSupportFragmentManager());
    }

    /**
     * 获取探针数据指令
     */
    private void getProbeDataDialog() {
        ShowListDialogFragment.newInstance().setTitle("选择探针").setCancel("取消", 0).setCancelBlank(true).setBackground(true).setBottom(false).setList(mDialogMacList)
                .setOnDialogListener(new ShowListDialogFragment.onDialogListener() {
                    @Override
                    public void onItemListener(int position) {
                        Toast.makeText(MeatProbeChargerActivity.this, mDialogMacList.get(position).getName() + "设置该指令", Toast.LENGTH_SHORT).show();
                        mMeatProbeChargerBleDevice.setProbeData(2, mDialogMacList.get(position).getName());
                    }
                }).show(getSupportFragmentManager());
    }

    /**
     * 设置探针数据
     */
    private void setProbeDataDialog() {
        ShowListDialogFragment.newInstance().setTitle("选择探针").setCancel("取消", 0).setCancelBlank(true).setBackground(true).setBottom(false).setList(mDialogMacList)
                .setOnDialogListener(new ShowListDialogFragment.onDialogListener() {
                    @Override
                    public void onItemListener(int position) {
                        Toast.makeText(MeatProbeChargerActivity.this, mDialogMacList.get(position).getName() + "设置该指令", Toast.LENGTH_SHORT).show();
                        mMeatProbeChargerBleDevice.setProbeData(mDialogMacList.get(position).getName(), 2, System.currentTimeMillis(), 0, 0, -15, 5, 0, 0, 0, 0, 0.8, 0, 0, 0, -15, 5);
                    }
                }).show(getSupportFragmentManager());
    }

    @Override
    public void onServiceSuccess() {
        mBleDevice = mBluetoothService.getBleDevice(mMac);
        if (mBleDevice != null) {
            if (mMeatProbeChargerBleDevice == null) {
                Log.e("ljl", "onServiceSuccess: 绑定设备，设置监听");
                MeatProbeChargerBleDevice.init(mBleDevice);
                mMeatProbeChargerBleDevice = MeatProbeChargerBleDevice.getInstance();
                mMeatProbeChargerBleDevice.setOnMeatProbeChargerDataListener(this);
                if (btn_probe_version != null) {
                    btn_probe_version.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBleDevice.setMtu(247);
                        }
                    }, 200);
                }
            }
        }
        addText("连接成功，充电盒Mac地址为：" + mMac);
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

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
        if (list_view != null) {
            list_view.smoothScrollToPosition(mList.size() - 1);
        }
    }

    @Override
    protected void onDestroy() {
        if (mMeatProbeChargerBleDevice != null) {
            mMeatProbeChargerBleDevice = null;
        }
        if (mBluetoothService != null) {
            mBluetoothService.disconnectAll();
        }
        super.onDestroy();
    }

    @Override
    public void setVersion() {
        if (mMeatProbeChargerBleDevice != null) {
            mMeatProbeChargerBleDevice.getVersion();
        }
    }

    /**
     * 设置mtu成功后可以设置设备信息
     */
    @Override
    public void setDeviceInfo() {
    }

    @Override
    public void onVersionInfo(String version) {
        tv_version.setText("固件版本号：" + version);
    }

    @Override
    public void appSyncTimeResult(int result) {
        addText("同步时间状态码：" + result);
    }

    @Override
    public void switchUnitResult(int result) {
        addText("切换单位状态码:" + result);
    }

    @Override
    public void setAlarmresult(String mac, int alarmResult) {
        addText("设置警报->探针Mac地址" + mac + "  状态码:" + alarmResult);
    }

    @Override
    public void cancelAlarmresult(String mac, int alarmResult) {
        addText("取消警报->探针Mac地址" + mac + "  状态码:" + alarmResult);
    }

    private int index = 0;

    @Override
    public void onDeviceInfo(int supportNum, int currentNum, int chargerState, int battery, int boxUnit, List<ChargerProbeBean> chargerProbeBeanList) {
        addText("设备支持探针数：" + supportNum + "   当前连接探针数：" + currentNum + "  充电状态：" + chargerState + "   电池电量：" + battery + "   充电盒单位：" + boxUnit);
        if (mChargerProbeBeanList == null) {
            mChargerProbeBeanList = new ArrayList<>();
            mChargerProbeBeanList.addAll(chargerProbeBeanList);
        } else {
            mChargerProbeBeanList.clear();
            mChargerProbeBeanList.addAll(chargerProbeBeanList);
        }
        if (mMacList.size() > 0) {
            mMacList.clear();
        }
        if (mDialogMacList.size() > 0) {
            index = 0;
            mDialogMacList.clear();
        }
        for (ChargerProbeBean chargerProbeBean : chargerProbeBeanList) {
            mMacList.add(chargerProbeBean.getMac());
            mDialogMacList.add(new DialogStringImageBean(chargerProbeBean.getMac(), ++index));
            addText("探针编号：" + chargerProbeBean.getNum() + " 探针mac地址:" + chargerProbeBean.getMac() + " 食物温度单位: " + chargerProbeBean.getFoodUnit() + " 正负: " + chargerProbeBean.getFoodPositive() + " " + "温度绝对值: " + chargerProbeBean.getFoodTemp() + " 环境温度单位: " + chargerProbeBean.getAmbientUnit() + " 正负: " + chargerProbeBean.getAmbientPositive() + " 温度绝对值: " + chargerProbeBean.getAmbientTemp() + " 探针充电状态:" + chargerProbeBean.getChargerState() + "  电量: " + chargerProbeBean.getBattery() + "   插入食物状态: " + chargerProbeBean.getInsertState());
        }
    }

    @Override
    public void onNoDeviceInfo(int supportNum, int currentNum, int chargingState, int battery, int boxUnit) {
        addText("设备支持探针数：" + supportNum + "   当前连接探针数：" + currentNum + "  充电状态：" + chargingState + "   电池电量：" + battery + "   充电盒单位：" + boxUnit);
    }

    @Override
    public void onBatteryStatus(int status, int battery) {
        if (btn_probe_version != null) {
            btn_probe_version.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mBleDevice != null) {
                        mBleDevice.setMtu(247);
                    }
                }
            }, 200);
        }
    }

    @Override
    public void onOtherData(String hexStr) {
        //其他数据
        addText("其他数据：[" + hexStr + "]");
    }

    @Override
    public void onDataStrA6(String hexStrA6) {
        addText("收到A6数据：[" + hexStrA6 + "]");
    }

    @Override
    public void onDataStrA7(String hexStrA7) {
        addText("收到A7数据：[" + hexStrA7 + "]");
    }

    @Override
    public void sendDataA6(String hexStrA6) {
        addText("发送A6数据：[" + hexStrA6 + "]");
    }

    @Override
    public void sendDataA7(String hexStrA7) {
        addText("发送A7数据：[" + hexStrA7 + "]");
    }

    @Override
    public void onHand(boolean status) {
        addText("握手" + (status ? "成功" : "失败"));
        if (!status) {
            Toast.makeText(MeatProbeChargerActivity.this, "握手失败，请退出界面重新搜索蓝牙连接设备", Toast.LENGTH_SHORT).show();
        }
    }
}

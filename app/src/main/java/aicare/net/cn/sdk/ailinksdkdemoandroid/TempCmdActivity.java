package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.CallbackDisIm;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnCallbackDis;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleDensityUtil;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.thermometer.TempDeviceData;


/**
 * xing<br>
 * 2019/4/25<br>
 * 体温计
 */
public class TempCmdActivity extends BleBaseActivity implements OnCallbackDis, OnBleVersionListener, OnMcuParameterListener, OnBleCompanyListener, View.OnClickListener {

    private static String TAG = TempCmdActivity.class.getName();
    private final int REFRESH_DATA = 3;
    private List<String> mList;
    private ArrayAdapter listAdapter;

    private Button btn_get_history;
    private Button btn_del_history;
    private Button btn_get_mode;
    private Button btn_get_temp;
    private Button btn_set_mode;
    private Button btn_set_temp;
    private RadioButton rb_single;
    private RadioButton rb_c;
    private EditText et_temp;
    private EditText et_history;

    private int stamp = 0;// 历史记录最新一条的时间戳

    /**
     * 服务Intent
     */
    private Context mContext;
    private EditText et_type;
    private TempDeviceData mBleDevice;
    private String mAddress;
    private BleSendCmdUtil mBleSendCmdUtil;
    private int type;
    private byte unit = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {


                case REFRESH_DATA:
                    if (listAdapter != null)
                        listAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        mContext = this;
        mAddress = getIntent().getStringExtra("mac");
        type = getIntent().getIntExtra("type", -1);
        mBleSendCmdUtil = BleSendCmdUtil.getInstance();
        init();

    }

    private void init() {
        mList = new ArrayList<>();
        ListView listView = findViewById(R.id.listview);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        listView.setAdapter(listAdapter);

        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btnVersion).setOnClickListener(this);
        findViewById(R.id.btnBattery).setOnClickListener(this);
        findViewById(R.id.btn_get_did).setOnClickListener(this);
        findViewById(R.id.clear).setOnClickListener(this);
        et_type = findViewById(R.id.et_type);

        btn_get_history = findViewById(R.id.btn_get_history);
        btn_get_mode = findViewById(R.id.btn_get_mode);
        btn_get_temp = findViewById(R.id.btn_get_temp);
        btn_set_mode = findViewById(R.id.btn_set_mode);
        btn_set_temp = findViewById(R.id.btn_set_temp);
        rb_single = findViewById(R.id.rb_single);
        rb_c = findViewById(R.id.rb_c);
        et_temp = findViewById(R.id.et_temp);
        et_history = findViewById(R.id.et_history);
        btn_del_history = findViewById(R.id.btn_del_history);

        btn_get_history.setOnClickListener(this);
        btn_del_history.setOnClickListener(this);
        btn_get_mode.setOnClickListener(this);
        btn_get_temp.setOnClickListener(this);
        btn_set_mode.setOnClickListener(this);
        btn_set_temp.setOnClickListener(this);

        cmdBtn();
    }


    private void cmdBtn() {
        Button btn_set_unit = findViewById(R.id.btn_set_unit);
        btn_set_unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unit == 0) {
                    unit = 1;
                    mBleDevice.setUnit((byte) 1);
                } else {
                    unit = 0;
                    mBleDevice.setUnit((byte) 0);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        SendBleBean sendBleBean = new SendBleBean();
        switch (v.getId()) {
            case R.id.btnVersion:
                sendBleBean.setHex(mBleSendCmdUtil.getBleVersion());
                mBleDevice.sendData(sendBleBean);
                break;
            case R.id.btnBattery:
                sendBleBean.setHex(mBleSendCmdUtil.getMcuBatteryStatus());
                mBleDevice.sendData(sendBleBean);
                break;
            case R.id.btn_get_did:
                sendBleBean.setHex(mBleSendCmdUtil.getDid());
                mBleDevice.sendData(sendBleBean);
                break;
            case R.id.btn1:
                String cmd = et_type.getText().toString().trim();
                SendMcuBean sendDataBean = new SendMcuBean();
                sendDataBean.setHex(type, cmd.getBytes());
                mBleDevice.sendData(sendDataBean);
                break;
            case R.id.clear:
                if (mList != null)
                    mList.clear();
                mHandler.sendEmptyMessage(REFRESH_DATA);
                break;
            case R.id.btn_get_history:
                // 读取历史记录
//                try {
//                    int size = Integer.parseInt(et_history.getText().toString());
//                    mBleDevice.getHistory(size, stamp);
//                } catch (Exception ignored) {}
                try {
                    String time = et_history.getText().toString();
                    if (time.isEmpty()) {
                        mBleDevice.getHistoryNew(0);
                    } else {
                        mBleDevice.getHistoryNew(Long.parseLong(time));
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "请按规则来", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_get_mode:
                // 获取设备测温模式
                mBleDevice.getMode();
                break;
            case R.id.btn_get_temp:
                // 获取高温报警值
                mBleDevice.getTemp();
                break;
            case R.id.btn_set_mode:
                // 设置设备测温模式
                if (rb_single.isChecked()) {
                    mBleDevice.setMode(0);
                } else {
                    mBleDevice.setMode(1);
                }
                break;
            case R.id.btn_set_temp:
                // 设置高温报警值
                String tempStr = et_temp.getText().toString();
                if (!tempStr.isEmpty()) {
                    if (rb_c.isChecked()) {
                        mBleDevice.setTemp(tempStr, 0);
                    } else {
                        mBleDevice.setTemp(tempStr, 1);
                    }
                }
                break;
            case R.id.btn_del_history:
                mBleDevice.delHistoryNew();
                break;
        }
    }

    //---------------------------------服务---------------------------------------------------


    @Override
    public void onServiceSuccess() {
        BleLog.i(TAG, "服务与界面建立连接成功");
        CallbackDisIm.getInstance().addListListener(this);
        if (mBluetoothService != null) {
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                mBleDevice = TempDeviceData.getInstance(bleDevice);
                mBleDevice.setOnNotifyData(new NotifyData());
                mBleDevice.setOnBleVersionListener(TempCmdActivity.this);
                mBleDevice.setOnMcuParameterListener(TempCmdActivity.this);
                mBleDevice.setOnCompanyListener(TempCmdActivity.this);

                mHandler.postDelayed(() -> {
                    if (isDestroyed() || isFinishing()) {
                        return;
                    }
                    int stamp = (int) (System.currentTimeMillis() / 1000);
                    mList.add("同步Unix时间戳：" + stamp);
                    mHandler.sendEmptyMessage(REFRESH_DATA);
                    mBleDevice.setUnixStampNew(stamp);
                }, 200);
            }
        }
    }

    @Override
    public void onServiceErr() {
        BleLog.i(TAG, "服务与界面连接断开");
        //与服务断开连接
        mBluetoothService = null;
    }

    @Override
    public void unbindServices() {
        CallbackDisIm.getInstance().removeListener(this);
        if (mBleDevice != null) {
            mBleDevice.disconnect();
            mBleDevice.clear();
            mBleDevice = null;
        }
    }


    //-----------------状态-------------------


    @Override
    public void onConnecting(@NonNull String mac) {
        //TODO 连接中
        BleLog.i(TAG, "连接中");
    }

    @Override
    public void onDisConnected(@NonNull String mac, int code) {
        //TODO 连接断开
        BleLog.i(TAG, "连接断开");
        finish();
    }

    @Override
    public void onServicesDiscovered(@NonNull String mac) {
        //TODO 连接成功(获取服务成功)
        BleLog.i(TAG, "连接成功(获取服务成功)");
    }


    @Override
    public void bleOpen() {

    }

    @Override
    public void bleClose() {
        BleLog.i(TAG, "蓝牙未开启,可请求开启");
        finish();
    }


    //-----------------通知-------------------

    private long lastTime;

    private class NotifyData implements TempDeviceData.onNotifyData {
        @Override
        public void onData(byte[] status, int type) {
            String data = "";
            if (status != null)
                data = BleStrUtils.byte2HexStr(status);
            if (type == 100) {
                mList.add(TimeUtils.getTime() + "send->" + data);
            } else {
                mList.add(TimeUtils.getTime() + "notify->" + data);
            }
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void temp(int temp, int decimal, byte tempUnit) {
            String tempStr = BleDensityUtil.getInstance().holdDecimals(temp, decimal);
            mList.add(TimeUtils.getTime() + "稳定:TEMP=" + tempStr + "，tempUnit=" + tempUnit);
            mHandler.sendEmptyMessage(REFRESH_DATA);

        }

        @Override
        public void tempNow(int temp, int decimal, byte tempUnit) {
            String tempStr = BleDensityUtil.getInstance().holdDecimals(temp, decimal);
            mList.add(TimeUtils.getTime() + "实时:TEMP=" + tempStr + "，tempUnit=" + tempUnit);
            mHandler.sendEmptyMessage(REFRESH_DATA);

        }

        @Override
        public void getUnit(byte status) {
            mList.add(TimeUtils.getTime() + "单位:" + status);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void getErr(byte status) {
            mList.add(TimeUtils.getTime() + "错误:" + status);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void mcuHistory(int maxSize, int curSize, List<TempDeviceData.HistoryBean> list) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String str = "MCU上发历史记录：\n总条数：" + maxSize + "；此次发送：" + curSize;
            for (int i = 0; i < list.size(); i++) {
                TempDeviceData.HistoryBean bean = list.get(i);
                str += "\n";
                str += i + "：" + sdf.format(bean.getStamp());
                str += "\n温度值：" + bean.getTemp() + "；单位：" + bean.getUnit() + "；小数点：" + bean.getDecimal();

                // 把最新的一条时间戳保存起来
                if (i == list.size() - 1) {
                    stamp = bean.getStamp();
                }
            }
            mList.add(str);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void mcuGetMode(int mode) {
            String str = "MCU回复当前测温模式：" + mode;
            mList.add(str);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void mcuSetMode(int status) {
            String str = "MCU回复设置当前测温模式结果：" + status;
            mList.add(str);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void mcuGetTemp(int temp, int unit, int decimal) {
            String str = "MCU回复高温报警值：";
            str += "\n温度值：" + temp + "；单位：" + unit + "；小数点：" + decimal;
            mList.add(str);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void mcuSetTemp(int status) {
            String str = "MCU回复设置高温报警值结果：" + status;
            mList.add(str);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void mcuSetUnixStamp(int status) {
            String str = "MCU回复设置Unix时间戳结果：" + status;
            mList.add(str);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }


        @Override
        public void onHistoryNum(long allNum, long sendNum) {
            if (allNum == sendNum) {
                //这里在前那一次数据
                mList.add(TimeUtils.getTime() + "历史记录获取完成");
            } else {
                mList.add(TimeUtils.getTime() + "历史记录获取未完成: 下次获取时间: " + lastTime);
                mBleDevice.getHistoryNew(lastTime);
            }
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void onHistory(long time, byte[] value) {
            mList.add(TimeUtils.getTime() + "历史记录: " + TimeUtils.getTime(time*1000) + "  数据: " + BleStrUtils.byte2HexStr(value));
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void onHistoryLast(long time) {
            lastTime = time;
            mList.add(TimeUtils.getTime() + "最新一条历史记录: 时间" + time);
            et_history.setText(lastTime+"");
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

        @Override
        public void onDelHistory(int result) {
            mList.add(TimeUtils.getTime() + "删除历史记录结果: " + (result == 0 ? "成功" : "失败"));
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }
    }

    @Override
    public void onBmVersion(String version) {
        mList.add(TimeUtils.getTime() + "版本号:" + version);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void OnDID(int cid, int vid, int pid) {
        String didStr = "cid:" + cid + "||vid:" + vid + "||pid:" + pid;
        mList.add(TimeUtils.getTime() + "ID:" + didStr);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onMcuBatteryStatus(int status, int battery) {
        mList.add(TimeUtils.getTime() + "电量:" + battery + "%");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onSysTime(int status, int[] times) {
        String time =
                times[0] + "-" + times[1] + "-" + times[2] + "  " + times[3] + ":" + times[4] +
                        ":" + times[5];
        mList.add(TimeUtils.getTime() + "系统时间:" + time);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleLog.i(TAG, "onDestroy");
    }
}

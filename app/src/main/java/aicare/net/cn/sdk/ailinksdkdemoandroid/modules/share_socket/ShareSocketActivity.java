package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.share_socket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.pingwang.bluetoothlib.listener.OnScanFilterListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.ShareSocket.ShareSocketData;

public class ShareSocketActivity extends BleBaseActivity implements View.OnClickListener, ShareSocketData.ShareSocketCallback, OnCallbackBle, OnScanFilterListener {

    private static final int MSG_TEST_START_SCAN = 100;

    private Button btn_set;
    private SeekBar seek_hour;
    private SeekBar seek_minute;
    private SeekBar seek_second;
    private TextView tv_hour;
    private TextView tv_minute;
    private TextView tv_second;
    private Button btn_get;
    private Button btn_switch;
    private RadioButton rb_open;
    private RadioButton rb_close;
    private ListView list_view;
    private Spinner sp;
    private TextView tv_timing_0;
    private TextView tv_timing_1;
    private TextView tv_test;

    private List<String> mList;
    private ArrayAdapter mListAdapter;

    private String mMac;
    private BleDevice mBleDevice;
    private ShareSocketData mShareSocketData;

    private Map<Integer, Long> mTimingMap;// 计时map；key：编号；value：结束的时间戳

    private SimpleDateFormat mSDF;

    private boolean mIsTest = false;// 是否在进行测试
    private long mTestAllStamp = 30 * 60 * 1000;// 完整测试时长
    private long mTestSingleStamp = 2 * 60 * 1000;// 单轮测试时长
    private long mTestStartStamp = 0;// 下发完整测试时长那一刻的时间戳

    private int mTest1SuccessCount = 0;// 成功次数
    private int mTest1AbnormalCount = 0;// 异常次数
    private int mTest1Abnormal10Count = 0;// 异常超过10秒的次数
    private int mTest1ResetCount = 0;// 复位次数

    private int mTest2SuccessCount = 0;// 成功次数
    private int mTest2AbnormalCount = 0;// 异常次数
    private int mTest2Abnormal10Count = 0;// 异常超过20秒的次数
    private int mTest2ResetCount = 0;// 复位次数

    private int mTestConnectSuccessCount = 0;// 测试时连接成功的次数
    private int mTestConnectFailCount = 0;// 测试时连接失败的次数

    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            long endStamp = mTimingMap.get(msg.what);
            int timeStamp = (int) (endStamp - System.currentTimeMillis()) / 1000;
            String text = "编号" + msg.what + "计时：" + timeStamp + "秒";
            switch (msg.what) {
                case 0:
                    tv_timing_0.setText(text);
                    break;
                case 1:
                    tv_timing_1.setText(text);
                    break;
            }
            // 判断倒计时结束了没有
            if (timeStamp > 0) {
                // 没结束，1秒后继续
                mHandler.sendEmptyMessageDelayed(msg.what, 1000);
            } else {
                // 结束了，gg
                mHandler.removeMessages(msg.what);
            }
        }
    };

    // 测试Handler
    private Handler mTestHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_TEST_START_SCAN:
                    // 开始扫描设备
                    addText("准备重连，开始扫描设备：" + mMac);
                    mBluetoothService.scanLeDevice(0);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_socket);

        btn_set = findViewById(R.id.btn_set);
        seek_hour = findViewById(R.id.seek_hour);
        seek_minute = findViewById(R.id.seek_minute);
        seek_second = findViewById(R.id.seek_second);
        tv_hour = findViewById(R.id.tv_hour);
        tv_minute = findViewById(R.id.tv_minute);
        tv_second = findViewById(R.id.tv_second);
        btn_get = findViewById(R.id.btn_get);
        btn_switch = findViewById(R.id.btn_switch);
        rb_open = findViewById(R.id.rb_open);
        rb_close = findViewById(R.id.rb_close);
        list_view = findViewById(R.id.list_view);
        sp = findViewById(R.id.sp);
        tv_timing_0 = findViewById(R.id.tv_timing_0);
        tv_timing_1 = findViewById(R.id.tv_timing_1);
        tv_test = findViewById(R.id.tv_test);

        btn_set.setOnClickListener(this);
        btn_get.setOnClickListener(this);
        btn_switch.setOnClickListener(this);

        // 滑动修改时间显示
        seek_hour.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_hour.setText(progress + "时");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seek_minute.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_minute.setText(progress + "分");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seek_second.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_second.setText((progress + 1) + "秒");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // 获取Mac
        mMac = getIntent().getStringExtra("mac");

        // 初始化列表
        mList = new ArrayList<>();
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        list_view.setAdapter(mListAdapter);

        mTimingMap = new HashMap<>();

        mSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_set) {
            int spId = sp.getSelectedItemPosition();
            int hour = seek_hour.getProgress();
            int minute = seek_minute.getProgress();
            int second = seek_second.getProgress() + 1;
            if (mShareSocketData != null) {
                addText("APP设置充电时间：" + getTimeStr(hour, minute, second));
                mShareSocketData.appSetSocketTime(spId, hour, minute, second);
                // 保存到map
                long endStamp = System.currentTimeMillis() + hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000 + 2000;// 最后包括这一秒，然后设备又多一秒
                mTimingMap.put(spId, endStamp);
                // Handler开始计时
                mHandler.removeMessages(spId);
                mHandler.sendEmptyMessageDelayed(spId, 1000);
            }
        } else if (id == R.id.btn_get) {
            int spId = sp.getSelectedItemPosition();
            if (mShareSocketData != null) {
                addText("APP获取剩余充电时间");
                mShareSocketData.appGetSocketTime(spId);
            }
        } else if (id == R.id.btn_switch) {
            int spId = sp.getSelectedItemPosition();
            boolean isOpen = rb_open.isChecked();
            if (mShareSocketData != null) {
                addText("APP切换开关：" + isOpen);
                mShareSocketData.appSwitch(spId, isOpen);
            }
        } else if (id == R.id.btn_version) {
            if (mShareSocketData != null) {
                addText("APP获取设备版本号");
                mShareSocketData.appGetVersion();
            }
        } else if (id == R.id.btn_test) {
            if (!mIsTest && mShareSocketData != null) {
                mIsTest = true;
                addText("开始性能测试");
                startTest();
            }
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
        mBluetoothService.setOnCallback(this);
        mBluetoothService.setOnScanFilterListener(this);
        mBleDevice = mBluetoothService.getBleDevice(mMac);
        if (mBleDevice != null) {
            mShareSocketData = new ShareSocketData(mBleDevice);
            mShareSocketData.setShareSocketCallback(this);
        }
    }

    @Override
    public void onScanning(BleValueBean data) {
        // 扫描到了设备
        if (mIsTest && !mBluetoothService.isConnectStatus()) {
            // 开始连接设备
            addText("扫描到设备，开始连接");
            mBluetoothService.connectDevice(mMac);
            // 停止扫描
            mBluetoothService.stopScan();
        }
    }

    @Override
    public boolean onFilter(BleValueBean bleValueBean) {
        // 只扫描相同的设备
        return bleValueBean.getMac().equals(mMac);
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }

    @Override
    public void mcuSetSocketTime(int id, int status) {
        addText("MCU回复设置充电时间：编号：" + id + "；" + getStatusStr(status));
    }

    private int mCha1 = -1;
    private int mCha2 = -1;
    @Override
    public void mcuGetSocketTime(int id, int status, int hour, int minute, int second) {
        if (mCha1 >= 0 && mCha2 >= 0) {
            // 已经回了2次了，多出来的回复不处理
            return;
        }

        addText("MCU回复查询剩余充电时间：编号：" + id + "；" + getStatusStr(status) + "；" + getTimeStr(hour, minute, second));
        long mcuStamp = hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000;
        long appStamp = mTestAllStamp - (System.currentTimeMillis() - mTestStartStamp);

        if (appStamp < 0) {
            appStamp = 0;
        }

        if (mIsTest) {
            int cha = Math.abs((int) (mcuStamp - appStamp));
            appStamp /= 1000;
            int h = (int) (appStamp / 60 / 60);
            int m = (int) (appStamp - hour * 60 * 60) / 60;
            int s = (int) appStamp % 60;
            addText("和APP的计时：" + getTimeStr(h, m, s) + "相差" + cha + "ms");

            cha /= 1000;
            if (id == 0) {
                mCha1 = cha;
            } else if (id == 1) {
                mCha2 = cha;
            }
            boolean needReset = false;
            if (mCha1 >= 0 && mCha2 >= 0) {
                // 两条数据都回来了
                if (mcuStamp == 0 && appStamp > 0) {
                    mTest1ResetCount++;
                    needReset = true;
                } else if (mCha1 < 5000) {
                    mTest1SuccessCount++;
                } else if (mCha1 < 10000) {
                    mTest1AbnormalCount++;
                    needReset = true;
                } else {
                    mTest1Abnormal10Count++;
                    needReset = true;
                }
                if (mcuStamp == 0 && appStamp > 0) {
                    mTest2ResetCount++;
                    needReset = true;
                } else if (mCha2 < 5000) {
                    mTest2SuccessCount++;
                } else if (mCha2 < 10000) {
                    mTest2AbnormalCount++;
                    needReset = true;
                } else {
                    mTest2Abnormal10Count++;
                    needReset = true;
                }
                // 刷新显示
                refreshTest();
                // 判断要不要重新发送充电时长
                if (needReset) {
                    // 有异常，需要重置
                    addText("出现异常，需要重置充电时间");
                    startTest();
                } else if (appStamp <= 0) {
                    // 几轮都走完了
                    addText("测试完成，重新设置充电时间");
                    startTest();
                } else {
                    // 没有问题
                    addText("本轮测试结束，总时长还没结束，直接断开，不重置充电时长");
                    if (mBleDevice != null) {
                        mBleDevice.disconnect();
                        mBleDevice = null;
                    }
                }
            }
        }
    }

    @Override
    public void mcuSwitch(int id, int status) {
        addText("MCU回复开关：编号：" + id + "；" + getStatusStr(status));
    }

    @Override
    public void mcuVersion(String version) {
        addText("MCU回复版本：" + version);
    }

    @Override
    public void onServicesDiscovered(String mac) {
        mBleDevice = mBluetoothService.getBleDevice(mMac);
        if (mIsTest && mBleDevice != null) {
            mTestConnectSuccessCount++;
            addText("连接成功：" + mMac + "，查询剩余充电时间");
            mShareSocketData = new ShareSocketData(mBleDevice);
            mShareSocketData.setShareSocketCallback(this);

            mCha1 = mCha2 = -1;
            mShareSocketData.appGetSocketTime(0);
            mShareSocketData.appGetSocketTime(1);
        }
    }

    @Override
    public void onDisConnected(String mac, int code) {
        if (mIsTest) {
            long stamp = mTestSingleStamp;
            if (stamp < 0) {
                stamp = 0;
            }
            mBleDevice = null;
            if (code != 0) {
                // 0是自己断开，不是0就说明是连接失败
                mTestConnectFailCount++;
                addText("设备断开连接：" + code + "。正在测试，等待" + getTimeStrByStamp(10000) + "后重连");
                mTestHandler.sendEmptyMessageDelayed(MSG_TEST_START_SCAN, 10000);
            } else {
                // 自己主动断开要等待
                addText("主动断开连接：" + code + "。正在测试，等待" + getTimeStrByStamp(stamp) + "后重连");
                mTestHandler.sendEmptyMessageDelayed(MSG_TEST_START_SCAN, stamp);
            }
        } else {
            addText("设备断开连接：" + code);
        }
    }

    // 开始插口1的测试
    private void startTest() {
        addText("设置充电总时长：" + getTimeStrByStamp(mTestAllStamp));
        mTestStartStamp = System.currentTimeMillis() + 2000L;// 1000毫秒是蓝牙的延迟，1000毫秒是MCU自己的延迟

        long stamp = mTestAllStamp;
        stamp /= 1000;
        int hour = (int) (stamp / 60 / 60);
        int minute = (int) (stamp - hour * 60 * 60) / 60;
        int second = (int) stamp % 60;

        mShareSocketData.appSetSocketTime(0, hour, minute, second);
        mShareSocketData.appSetSocketTime(1, hour, minute, second);
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (Exception ignored) {
            }
            if (mBleDevice != null && mBleDevice.isConnectSuccess()) {
                addText("设置充电总时长后断开连接");
                mBleDevice.disconnect();
                mBleDevice = null;
            }
        }).start();
    }

    // 添加一条文本
    private void addText(String text) {
        runOnUiThread(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
            mList.add(sdf.format(System.currentTimeMillis()) + "：\n" + text);
            mListAdapter.notifyDataSetChanged();
            list_view.smoothScrollToPosition(mList.size() - 1);
        });
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

    private String getTimeStr(int hour, int minute, int second) {
        String hourStr;
        if (hour >= 0) {
            hourStr = hour >= 10 ? "" + hour : "0" + hour;
        } else {
            hourStr = "NULL";
        }
        String minuteStr;
        if (minute >= 0) {
            minuteStr = minute >= 10 ? "" + minute : "0" + minute;
        } else {
            minuteStr = "NULL";
        }
        String secondStr;
        if (second >= 0) {
            secondStr = second >= 10 ? "" + second : "0" + second;
        } else {
            secondStr = "NULL";
        }
        return hourStr + ":" + minuteStr + ":" + secondStr;
    }

    /**
     * 根据毫秒数获取时间字符串
     *
     * @param stamp 毫秒
     * @return 时间
     */
    private String getTimeStrByStamp(long stamp) {
        String str = "";

        stamp /= 1000;
        int hour = (int) (stamp / 60 / 60);
        int minute = (int) (stamp - hour * 60 * 60) / 60;
        int second = (int) stamp % 60;

        if (hour > 0) {
            str += hour + "小时";
        }
        if (minute > 0) {
            str += minute + "分钟";
        }
        if (second > 0) {
            str += second + "秒";
        }

        return str;
    }

    /**
     * 刷新测试统计
     */
    private void refreshTest() {
        String str = "";
        
        str += "\n连接次数：" + (mTestConnectSuccessCount + mTestConnectFailCount);
        str += "\n连接成功次数：" + mTestConnectSuccessCount;
        str += "\n连接失败次数：" + mTestConnectFailCount;
        str += "\n连接成功率：" + (mTestConnectSuccessCount * 1.0f / (mTestConnectSuccessCount + mTestConnectFailCount) * 100) + "%";
        str += "\n\n插口1成功次数：" + mTest1SuccessCount;
        str += "\n插口1重置次数：" + mTest1ResetCount;
        str += "\n插口1异常不超过10秒次数：" + mTest1AbnormalCount;
        str += "\n插口1异常超过10秒次数：" + mTest1Abnormal10Count;
        str += "\n插口1成功率" + (mTest1SuccessCount * 1.0f / (mTest1SuccessCount + mTest1AbnormalCount + mTest1ResetCount + mTest1Abnormal10Count) * 100) + "%";
        str += "\n\n插口2成功次数：" + mTest2SuccessCount;
        str += "\n插口2重置次数：" + mTest2ResetCount;
        str += "\n插口2异常不超过10秒次数：" + mTest2AbnormalCount;
        str += "\n插口2异常超过10秒次数：" + mTest2Abnormal10Count;
        str += "\n插口2成功率" + (mTest2SuccessCount * 1.0f / (mTest2SuccessCount + mTest2AbnormalCount + mTest2ResetCount + mTest2Abnormal10Count) * 100) + "%";
        
        tv_test.setText(str);
    }
}

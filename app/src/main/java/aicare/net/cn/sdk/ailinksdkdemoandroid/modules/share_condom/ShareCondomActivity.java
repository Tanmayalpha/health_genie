package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.share_condom;

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
import android.widget.TextView;

import com.pingwang.bluetoothlib.BleBaseActivity;
import com.pingwang.bluetoothlib.device.BleDevice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.ShareCondom.ShareCondomData;

/**
 * 共享套套机
 */
public class ShareCondomActivity extends BleBaseActivity implements View.OnClickListener, ShareCondomData.ShareCondomCallback {

    private static final int MSG_TIMING = 100;

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
    private Button btn_out;
    private Button btn_recycle;
    private Button btn_open;
    private TextView tv_time;

    private List<String> mList;
    private ArrayAdapter mListAdapter;

    private String mMac;
    private BleDevice mBleDevice;
    private ShareCondomData mShareCondomData;

    private long mEndStamp = 0;// 剩余时间目标的结束时间

    /**
     * 计时
     */
    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_TIMING) {
                // 判断显示
                long stamp = mEndStamp - System.currentTimeMillis();
                int second = (int) stamp / 1000;
                if (second > 0) {
                    tv_time.setText("剩余时间：" + second + "秒");
                    // 1秒后重新刷新
                    sendEmptyMessageDelayed(MSG_TIMING, 1000);
                } else {
                    tv_time.setText("");
                    // 停止刷新
                    removeMessages(MSG_TIMING);
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_condom);

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
        btn_out = findViewById(R.id.btn_out);
        btn_recycle = findViewById(R.id.btn_recycle);
        btn_open = findViewById(R.id.btn_open);
        tv_time = findViewById(R.id.tv_time);

        btn_set.setOnClickListener(this);
        btn_get.setOnClickListener(this);
        btn_switch.setOnClickListener(this);
        btn_out.setOnClickListener(this);
        btn_recycle.setOnClickListener(this);
        btn_open.setOnClickListener(this);

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
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_set) {
            int hour = seek_hour.getProgress();
            int minute = seek_minute.getProgress();
            int second = seek_second.getProgress() + 1;
            if (mShareCondomData != null) {
                addText("APP设置充电时间：" + getTimeStr(hour, minute, second));
                mShareCondomData.appSetCondomTime(hour, minute, second);

                mEndStamp = System.currentTimeMillis() + (hour * 60 * 60 + minute * 60 + second) * 1000 + 1500;// 500是当前秒，1000是设备自己加的
                mHandler.removeMessages(MSG_TIMING);
                mHandler.sendEmptyMessageDelayed(MSG_TIMING, 1000);
            }
        } else if (id == R.id.btn_get) {
            if (mShareCondomData != null) {
                addText("APP获取剩余充电时间");
                mShareCondomData.appGetCondomTime();
            }
        } else if (id == R.id.btn_switch) {
            boolean isOpen = rb_open.isChecked();
            if (mShareCondomData != null) {
                addText("APP切换开关：" + isOpen);
                mShareCondomData.appSwitch(isOpen);
            }
        } else if (id == R.id.btn_out) {
            if (mShareCondomData != null) {
                addText("APP仓位出仓");
                mShareCondomData.appOut();
            }
        } else if (id == R.id.btn_recycle) {
            if (mShareCondomData != null) {
                addText("APP仓位回收");
                mShareCondomData.appRecycle();
            }
        } else if (id == R.id.btn_open) {
            if (mShareCondomData != null) {
                addText("APP打开补货门");
                mShareCondomData.appOpen();
            }
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;

        if (mBluetoothService != null) {
            mBluetoothService.disconnectAll();
        }
        super.onDestroy();
    }

    @Override
    public void onServiceSuccess() {
        mBleDevice = mBluetoothService.getBleDevice(mMac);
        if (mBleDevice != null) {
            mShareCondomData = new ShareCondomData(mBleDevice);
            mShareCondomData.setShareCondomCallback(this);
        }
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }

    @Override
    public void mcuSetCondomTime(int status) {
        addText("MCU回复设置充电时间：" + getStatusStr(status));
    }

    @Override
    public void mcuGetCondomTime(int status, int hour, int minute, int second) {
        addText("MCU回复查询剩余充电时间：" + getStatusStr(status) + "；" + getTimeStr(hour, minute, second));
    }

    @Override
    public void mcuSwitch(int status) {
        addText("MCU回复开关：" + getStatusStr(status));
    }

    @Override
    public void mcuOut(int status) {
        addText("MCU回复仓位出仓：" + getCondomOutStr(status));
    }

    @Override
    public void mcuRecycle(int status) {
        addText("MCU回复仓位回收：" + getCondomRecycleStr(status));
    }

    @Override
    public void mcuOpen(int status) {
        addText("MCU回复打开缺货仓：" + getCondomOutStr(status));
    }

    // 添加一条文本
    private void addText(String text) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
        mList.add(sdf.format(System.currentTimeMillis()) + "：\n" + text);
        mListAdapter.notifyDataSetChanged();
        list_view.smoothScrollToPosition(mList.size() - 1);
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

    private String getCondomOutStr(int status) {
        switch (status) {
            case 0:
                return "成功";
            case 1:
                return "不支持";
            case 2:
                return "电机或限位开关故障";
            case 3:
                return "仓位已出仓";
        }
        return "";
    }

    private String getCondomRecycleStr(int status) {
        switch (status) {
            case 0:
                return "成功";
            case 1:
                return "不支持";
            case 2:
                return "电机或限位开关故障";
            case 3:
                return "仓位未出仓";
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
}

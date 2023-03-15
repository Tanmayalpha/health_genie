package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.share_charger;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pingwang.bluetoothlib.device.BleDevice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.ShareCharger.ShareChargerData;

public class ShareChargerActivity extends BleBaseActivity implements View.OnClickListener, ShareChargerData.ShareChargerCallback {

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

    private List<String> mList;
    private ArrayAdapter mListAdapter;

    private String mMac;
    private BleDevice mBleDevice;
    private ShareChargerData mShareChargerData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_charger);

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_set:
                int hour = seek_hour.getProgress();
                int minute = seek_minute.getProgress();
                int second = seek_second.getProgress() + 1;
                if (mShareChargerData != null) {
                    addText("APP设置充电时间：" + getTimeStr(hour, minute, second));
                    mShareChargerData.appSetChargerTime(hour, minute, second);
                }
                break;
            case R.id.btn_get:
                if (mShareChargerData != null) {
                    addText("APP获取剩余充电时间");
                    mShareChargerData.appGetChargerTime();
                }
                break;
            case R.id.btn_switch:
                boolean isOpen = rb_open.isChecked();
                if (mShareChargerData != null) {
                    addText("APP切换开关：" + isOpen);
                    mShareChargerData.appSwitch(isOpen);
                }
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
            mShareChargerData = new ShareChargerData(mBleDevice);
            mShareChargerData.setShareChargerCallback(this);
        }
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }

    @Override
    public void mcuSetChargerTime(int status) {
        addText("MCU回复设置充电时间：" + getStatusStr(status));
    }

    @Override
    public void mcuGetChargerTime(int status, int hour, int minute, int second) {
        addText("MCU回复查询剩余充电时间：" + getStatusStr(status) + "；" + getTimeStr(hour, minute, second));
    }

    @Override
    public void mcuSwitch(int status) {
        addText("MCU回复开关：" + getStatusStr(status));
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

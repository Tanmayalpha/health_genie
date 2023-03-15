package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import cn.net.aicare.modulelibrary.module.BloodOxygen.BleBloodOxygenBleConfig;
import cn.net.aicare.modulelibrary.module.BloodOxygen.BleBloodOxygenDeviceData;

/**
 * 血氧仪
 */
public class BloodOxygenActivity extends BleBaseActivity implements OnCallbackBle, BleBloodOxygenDeviceData.DataCallback {

    private TextView tv_sp_max, tv_sp_min, tv_pr_max, tv_pr_min, tv_pl_max, tv_pl_min, tv_version;
    private AppCompatSeekBar sb_sp_max, sb_sp_min, sb_pr_max, sb_pr_min, sb_pl_max, sb_pl_min;
    private ListView mListView;
    private List<String> loglist;
    private ArrayAdapter listAdapter;
    private String mAddress;
    private boolean mPauseRefresh = false;
    private BleBloodOxygenDeviceData mBleBloodOxygenDeviceData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodoxygen);
        mAddress = getIntent().getStringExtra("mac");

        tv_sp_max = findViewById(R.id.tv_sp_max);
        tv_sp_min = findViewById(R.id.tv_sp_min);
        tv_pr_max = findViewById(R.id.tv_pr_max);
        tv_pr_min = findViewById(R.id.tv_pr_min);
        tv_pl_max = findViewById(R.id.tv_pl_max);
        tv_pl_min = findViewById(R.id.tv_pl_min);
        tv_version = findViewById(R.id.tv_version);
        sb_sp_max = findViewById(R.id.sb_sp_max);
        sb_sp_min = findViewById(R.id.sb_sp_min);
        sb_pr_max = findViewById(R.id.sb_pr_max);
        sb_pr_min = findViewById(R.id.sb_pr_min);
        sb_pl_max = findViewById(R.id.sb_pl_max);
        sb_pl_min = findViewById(R.id.sb_pl_min);
        sb_pl_min = findViewById(R.id.sb_pl_min);
        mListView = findViewById(R.id.lv_log);
        loglist = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, loglist);
        mListView.setAdapter(listAdapter);
        findViewById(R.id.bt_status).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBleBloodOxygenDeviceData != null)
                    mBleBloodOxygenDeviceData.getDeviceStatus();
            }
        });
        findViewById(R.id.bt_alarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sp_max = sb_sp_max.getProgress();
                int sp_min = sb_sp_min.getProgress();
                int pl_max = sb_pl_max.getProgress();
                int pl_min = sb_pl_min.getProgress();
                int pr_max = sb_pr_max.getProgress();
                int pr_min = sb_pr_min.getProgress();
                if (mBleBloodOxygenDeviceData != null)
                    mBleBloodOxygenDeviceData.setAlarm(sp_max, sp_min, pr_max, pr_min, pl_max, pl_min);


            }
        });
        findViewById(R.id.bt_version).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBleBloodOxygenDeviceData != null)
                    mBleBloodOxygenDeviceData.getVersion();
            }
        });
        findViewById(R.id.bt_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPauseRefresh=!mPauseRefresh;

            }
        });
        findViewById(R.id.bt_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPauseRefresh=false;
                loglist.clear();
                listAdapter.notifyDataSetChanged();
            }
        });
        sb_sp_max.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_sp_max.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_sp_min.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_sp_min.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_pr_max.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_pr_max.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_pr_min.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_pr_min.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_pl_max.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_pl_max.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_pl_min.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_pl_min.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public void onServiceSuccess() {
        loglist.add(0, "连接设备成功");
        mBluetoothService.setOnCallback(this);
        BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
        BleBloodOxygenDeviceData.init(bleDevice, this);
        mBleBloodOxygenDeviceData = BleBloodOxygenDeviceData.getInstance();
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }

    @Override
    protected void startScanBle(long timeOut) {
        super.startScanBle(timeOut);
    }

    @Override
    protected void stopScanBle() {
        super.stopScanBle();
    }

    @Override
    protected void connectBle(BleValueBean bleValueBean) {
        super.connectBle(bleValueBean);
    }

    @Override
    protected void connectBle(String mac) {
        super.connectBle(mac);
    }

    @Override
    public void onDisConnected(String mac, int code) {
        if (mAddress.equalsIgnoreCase(mac)) {
            Toast.makeText(this, "设备断开连接", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (mBleBloodOxygenDeviceData != null) {
            mBleBloodOxygenDeviceData.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onData(byte[] data, int type) {
        String dataStr = "收到的数据";
        if (type == 1) {
            dataStr = "收到透传数据:";
        }
        dataStr += BleStrUtils.byte2HexStr(data);
        loglist.add(0, dataStr);
        refreshData();
    }

    @Override
    public void onResult(int status, int spo2, int pr, float pi, int battery, float rr, int py, int pyTrough, int wearStatus) {

        String statusStr = "";
        switch (status) {
            case BleBloodOxygenBleConfig.Test_status_START:
                statusStr = "开始测试";
                break;
            case BleBloodOxygenBleConfig.Test_status_Finish:
                statusStr = "正在测试";
                break;
            case BleBloodOxygenBleConfig.Test_status_ERROR:
                statusStr = "测试结束";
                break;
        }
        String wearStatusStr = wearStatus == 1 ? "已佩戴" : "未佩戴";

        String showData =
                "测试状态:" + statusStr + "\n血氧饱和度:" + spo2 + "\n脉率:" + pr + "\n血流灌注指数:" + pi + "\n电池电量:" + battery + "\n呼吸频率(RR):" + rr + "\n脉率曲线值:" + py + "\n脉率曲线波谷:" + pyTrough + "\n佩戴状态:" + wearStatusStr;
        loglist.add(0, showData);

        refreshData();
    }

    @Override
    public void onSetResult(int result) {
        String resultStr = "";
        switch (result) {
            case BleBloodOxygenBleConfig.SET_RESULT_FAIL:
                resultStr = "设置失败";
                break;
            case BleBloodOxygenBleConfig.SET_RESULT_SUCCESS:
                resultStr = "设置成功";
                break;

        }
        loglist.add(0, "设置结果" + resultStr);
        refreshData();
    }

    @Override
    public void onErrorCode(int code) {
        String errorStr = "";
        switch (code) {

            case BleBloodOxygenBleConfig.ErrorCode_Saturation_Rate_Not_Stable:
                errorStr = "血氧饱和率不稳定";
                break;
            case BleBloodOxygenBleConfig.ErrorCode_Pulse_Rate_Unstable:
                errorStr = "脉率不稳定";
                break;
            case BleBloodOxygenBleConfig.ErrorCode_Test_Error:
                errorStr = "测量出错";
                break;
            case BleBloodOxygenBleConfig.ErrorCode_Low_Power:
                errorStr = "设备低电";
                break;

        }
        loglist.add(0, "设置结果" + errorStr);
        refreshData();
    }


    private void refreshData() {
        if (listAdapter != null && !mPauseRefresh) {
            listAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onBmVersion(String version) {
        tv_version.setText(version);
    }
}

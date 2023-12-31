package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.ropeskipping;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;

import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import cn.net.aicare.modulelibrary.module.RopeSkipping.OnRopeSkipCallBack;
import cn.net.aicare.modulelibrary.module.RopeSkipping.RopeSkipRecord;
import cn.net.aicare.modulelibrary.module.RopeSkipping.RopeSkippingBleData;

public class RopeSkippingActivity extends BleBaseActivity implements View.OnClickListener, OnCallbackBle, OnRopeSkipCallBack {


    private List<String> logList;
    private ArrayAdapter mArrayAdapter;
    private ListView listviw;


    private String mAddress;
    private boolean isPauseLog = false;

    @Override
    public void onServiceSuccess() {
        logList.add("绑定服务成功");
        if (mBluetoothService != null) {
            mBluetoothService.setOnCallbackBle(this);
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                RopeSkippingBleData.init(bleDevice);
                RopeSkippingBleData.getInstance().setOnRopeSkipCallBack(this);

            }
        }

    }

    @Override
    public void onServiceErr() {
        if (mArrayAdapter != null && logList != null) {
            refreshLog("绑定服务失败");
        }
    }

    @Override
    public void unbindServices() {
        if (mArrayAdapter != null && logList != null) {
            refreshLog("解除绑定服务");
        }
        if (mBluetoothService!=null) {
            mBluetoothService.removeOnCallbackBle(this);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAddress = getIntent().getStringExtra("mac");
        setContentView(R.layout.activity_rope_skipping);
        listviw = findViewById(R.id.listview);

        findViewById(R.id.btn_syn_time).setOnClickListener(this);
        findViewById(R.id.btn_free_jump).setOnClickListener(this);
        findViewById(R.id.btn_time_jump).setOnClickListener(this);
        findViewById(R.id.btn_num_jump).setOnClickListener(this);
        findViewById(R.id.btn_stop_free_jump).setOnClickListener(this);
        findViewById(R.id.btn_stop_time_jump).setOnClickListener(this);
        findViewById(R.id.btn_stop_num_jump).setOnClickListener(this);

        findViewById(R.id.btn_get_history).setOnClickListener(this);
        findViewById(R.id.btn_clear_log).setOnClickListener(this);
        findViewById(R.id.btn_default_timer).setOnClickListener(this);
        findViewById(R.id.btn_default_num).setOnClickListener(this);
        findViewById(R.id.btn_bind).setOnClickListener(this);


        logList = new ArrayList<>();
        mArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, logList);
        listviw.setAdapter(mArrayAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_syn_time) {

            if (RopeSkippingBleData.getInstance() != null) {
                RopeSkippingBleData.getInstance().synTime(System.currentTimeMillis());
            }
            refreshLog("同步时间搓");

        } else if (v.getId() == R.id.btn_free_jump) {

            if (RopeSkippingBleData.getInstance() != null) {
                RopeSkippingBleData.getInstance().startOrStopMode(1, 1);
            }
            refreshLog("启动自由跳绳");
        } else if (v.getId() == R.id.btn_time_jump) {

            if (RopeSkippingBleData.getInstance() != null) {
                RopeSkippingBleData.getInstance().startOrStopMode(2, 1);
            }
            refreshLog("启动倒计时跳绳");

        } else if (v.getId() == R.id.btn_num_jump) {

            if (RopeSkippingBleData.getInstance() != null) {
                RopeSkippingBleData.getInstance().startOrStopMode(3, 1);
            }
            refreshLog("启动倒计数跳绳绳");
        } else if (v.getId() == R.id.btn_clear_log) {
            if (!isPauseLog) {
                isPauseLog = true;
            } else {
                isPauseLog = false;
            }

        } else if (v.getId() == R.id.btn_stop_free_jump) {
            if (RopeSkippingBleData.getInstance() != null) {
                RopeSkippingBleData.getInstance().startOrStopMode(1, 0);
            }
            refreshLog("结束自由跳绳");
        } else if (v.getId() == R.id.btn_stop_time_jump) {
            if (RopeSkippingBleData.getInstance() != null) {
                RopeSkippingBleData.getInstance().startOrStopMode(2, 0);
            }
            refreshLog("结束倒计时跳绳");

        } else if (v.getId() == R.id.btn_stop_num_jump) {

            if (RopeSkippingBleData.getInstance() != null) {
                RopeSkippingBleData.getInstance().startOrStopMode(3, 0);
            }
            refreshLog("结束倒计数跳绳绳");
        } else if (v.getId() == R.id.btn_default_num) {

            if (RopeSkippingBleData.getInstance() != null) {
                RopeSkippingBleData.getInstance().setCountDownNum(50);
            }
            refreshLog("默认倒计数100");
        } else if (v.getId() == R.id.btn_default_timer) {

            if (RopeSkippingBleData.getInstance() != null) {
                RopeSkippingBleData.getInstance().setTimerNum(120);
            }
            refreshLog("默认倒计时60");
        } else if (v.getId() == R.id.btn_get_history) {

            if (RopeSkippingBleData.getInstance() != null) {
                RopeSkippingBleData.getInstance().offlineHistory(1);
            }
            refreshLog("获取离线记录");

        } else if (v.getId() == R.id.btn_bind) {

            if (RopeSkippingBleData.getInstance() != null) {
                RopeSkippingBleData.getInstance().questBind();
            }
            refreshLog("请按确认按钮");
        }
    }


    private void refreshLog(String content) {
        if (!isPauseLog) {
            logList.add(content);
            mArrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFinish(RopeSkipRecord ropeSkipBean) {

        refreshLog("跳绳结束" + "\n" + ropeSkipBean.toString() + " \n绊绳=" + new Gson().toJson(ropeSkipBean.getStopDetail()));
    }

    @Override
    public void onBattery(int battery) {

    }

    private String mOldData = "";
    private int mId = 0;

    @Override
    public void onCurrentData(int status, int mode, int defaultValue, int currentJumpNum, int currentJumpTime, int batter) {
        String data = "实时数据 \n" + "状态: " + status + " ( 0：准备 1：进行中 2：完成) \n" + "模式: " + mode + " (1：自由 2：倒计时 3：倒计数) \n" + "默认值: " + defaultValue + "  电量 " + batter + "\n 当前个数: " + currentJumpNum +
                " 时间 " + currentJumpTime;
        mId++;
        if (mOldData.equals(data)) {
            return;
        }
        mOldData = data;
        refreshLog(mId+data);
    }

    @Override
    public void onResultTimerAndCountDownNum(int mode, int timer) {
        if (mode == 2) {
            refreshLog("设置默认时间：" + timer);

        } else if (mode == 3) {
            refreshLog("设置默认个数：" + timer);

        }

    }

    @Override
    public void onResultStatus(int mode, int result) {
        refreshLog("模式: " + mode + " (1：自由 2：倒计时 3：倒计数)" + "结果: " + result + "0:成功 1:失败 2：不支持");


    }

    @Override
    public void onBindResult(int result) {
        refreshLog("确认绑定结果: " + result + "  0 : 成功 1 ：失败 2 不支持");
    }

    @Override
    public void onFinishOffHistory(List<RopeSkipRecord> list) {
        if (list == null) {
            refreshLog("没有离线记录");
        } else {

            refreshLog("离线记录：\n" + new Gson().toJson(list));
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (mBluetoothService != null){
            mBluetoothService.disconnectAll();
        }
    }
}

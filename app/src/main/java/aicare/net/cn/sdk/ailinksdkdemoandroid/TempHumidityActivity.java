package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import cn.net.aicare.modulelibrary.module.TempHumidity.TempHumidityBleUtils;

/**
 * 温湿度计
 */
public class TempHumidityActivity extends BleBaseActivity implements OnCallbackBle, TempHumidityBleUtils.BleDataCallBack, View.OnClickListener {
    private String mAddress = "";
    private List<String> logList;
    private List<String> historyLogList;
    private List<Long> historyLogListId;
    private ListView listView;
    private ListView list_view_history;
    private ArrayAdapter listAdapter;
    private ArrayAdapter historyListAdapter;
    private MHandler mMHandler;
    private EditText et_history;
    private boolean isHeart;
    private final int HEART = 1;
    private TextView tv_result;


    @Override
    public void onServiceSuccess() {
        logList.add(0, "绑定服务成功");
        if (mBluetoothService != null) {
            mBluetoothService.setOnCallbackBle(this);
            mBluetoothService.deviceConnectListener(mAddress, true);
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                TempHumidityBleUtils.init(bleDevice);
                TempHumidityBleUtils.getInstance().setBleDataCallBack(this);
            } else {
                mBluetoothService.disconnectAll();
                startScanBle(0);
            }
        }

    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {
        if (mBluetoothService!=null) {
            mBluetoothService.removeOnCallbackBle(this);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_humidity);
        mAddress = getIntent().getStringExtra("mac");
        logList = new ArrayList<>();
        historyLogList = new ArrayList<>();
        historyLogListId = new ArrayList<>();

        listView = findViewById(R.id.listView);
        list_view_history = findViewById(R.id.list_view_history);
        tv_result = findViewById(R.id.tv_result);
        et_history = findViewById(R.id.et_history);
        mMHandler = new WeakReference<>(new MHandler()).get();
        findViewById(R.id.btn_slow).setOnClickListener(this);
        findViewById(R.id.btn_fat).setOnClickListener(this);
        findViewById(R.id.btn_history).setOnClickListener(this);
        findViewById(R.id.btn_hear).setOnClickListener(this);
        findViewById(R.id.btn_show_history).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_interval_one).setOnClickListener(this);
        findViewById(R.id.btn_interval_three).setOnClickListener(this);
        findViewById(R.id.btn_interval_five).setOnClickListener(this);
        findViewById(R.id.btn_ota).setOnClickListener(this);


        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, logList);
        historyListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyLogList);
        listView.setAdapter(listAdapter);
        list_view_history.setAdapter(historyListAdapter);
    }

    @Override
    public void onStartScan() {

    }

    @Override
    public void onScanning(BleValueBean data) {
        if (data.getMac().equalsIgnoreCase(mAddress)) {
            stopScanBle();
            mBluetoothService.connectDevice(mAddress);
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

        }

    }

    @Override
    public void onConnectionSuccess(String mac) {
        stopScanBle();

        logList.add(0, "连接成功" + mac);

    }

    @Override
    public void onServicesDiscovered(String mac) {
        if (mac.equalsIgnoreCase(mAddress)) {
            stopScanBle();

            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                TempHumidityBleUtils.init(bleDevice);
                TempHumidityBleUtils.getInstance().setBleDataCallBack(this);

            }
        }
    }

    @Override
    public void bleOpen() {

    }

    @Override
    public void bleClose() {

    }


    @Override
    public void onClick(View v) {
        if (TempHumidityBleUtils.getInstance() != null) {

            switch (v.getId()) {
                case R.id.btn_slow:
                    if (TempHumidityBleUtils.getInstance() != null) {
                        TempHumidityBleUtils.getInstance().sendSlowData();
                        logList.add(0, "慢速保存数据");
                        listAdapter.notifyDataSetChanged();
                    }
                    break;
                case R.id.btn_fat:
                    if (TempHumidityBleUtils.getInstance() != null) {
                        TempHumidityBleUtils.getInstance().sendFatData();
                        logList.add(0, "快速速保存数据");
                        listAdapter.notifyDataSetChanged();

                    }
                    break;
                case R.id.btn_history:
                    if (TempHumidityBleUtils.getInstance() != null) {
                        String s = et_history.getText().toString().trim();
                        long maxId = 0;
                        if (!s.isEmpty()) {
                            maxId = Long.parseLong(s);
                        }
                        TempHumidityBleUtils.getInstance().getOffLineRecord(maxId);
                        logList.add(0, "获取历史记录 时间Id" + maxId);
                        tv_result.setText("");
                        historyLogListId.clear();
                        historyLogList.clear();
                        listAdapter.notifyDataSetChanged();

                    }
                    break;
                case R.id.btn_show_history:
                    if (listView.getVisibility() == View.VISIBLE) {
                        historyListAdapter.notifyDataSetChanged();
                        listView.setVisibility(View.GONE);
                        list_view_history.setVisibility(View.VISIBLE);
                    } else {
                        listView.setVisibility(View.VISIBLE);
                        list_view_history.setVisibility(View.GONE);
                        listAdapter.notifyDataSetChanged();

                    }
                case R.id.btn_hear:
                    if (!isHeart) {
                        isHeart = true;
                        mMHandler.sendEmptyMessage(HEART);
                        logList.add(0, "开启心跳");


                    } else {
                        isHeart = false;
                        mMHandler.removeMessages(HEART);
                        logList.add(0, "关闭心跳");
                    }
                    listAdapter.notifyDataSetChanged();

                    break;
                case R.id.btn_clear:
                    logList.clear();
                    historyLogListId.clear();
                    historyLogList.clear();
                    listAdapter.notifyDataSetChanged();
                    historyListAdapter.notifyDataSetChanged();
                    break;
                case R.id.btn_interval_one:
                    logList.add(0, "上报间隔1分钟");
                    TempHumidityBleUtils.getInstance().setReportTime(60);
                    listAdapter.notifyDataSetChanged();
                    break;
                case R.id.btn_interval_three:
                    logList.add(0, "上报间隔3分钟");
                    TempHumidityBleUtils.getInstance().setReportTime(180);
                    listAdapter.notifyDataSetChanged();
                    break;
                case R.id.btn_interval_five:
                    logList.add(0, "上报间隔5分钟");
                    TempHumidityBleUtils.getInstance().setReportTime(300);
                    listAdapter.notifyDataSetChanged();
                    break;
                case R.id.btn_ota:
                    logList.add(0, "请求ota");
                    TempHumidityBleUtils.getInstance().ota();
                    listAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    public void onDeviceStatus(int battery, long time, float temp, float humidity) {
        logList.add(0, "设备状态 电池电量：" + battery + "\n时间:" + time + " 温度: " + temp + " 湿度：" + humidity);
        listAdapter.notifyDataSetChanged();

    }

    @Override
    public void onOffLineRecordNum(long total, long sendNum) {
        logList.add(0, "历史记录 总量：" + total + "发送量:" + sendNum);

        if (total == sendNum) {
            if (historyLogListId.size() > 0) {
                Collections.sort(historyLogListId);
                tv_result.setText("总：" + historyLogListId.size() + "开：" + historyLogListId.get(0) + "结：" + historyLogListId.get(historyLogList.size() - 1));
            } else {
                tv_result.setText("");

            }
        }
        listAdapter.notifyDataSetChanged();

    }

    @Override
    public void onOffLineRecord(long time, float temp, float humidity) {
        historyLogList.add(0, "时间:" + time + " 温度: " + temp + " 湿度：" + humidity);
        historyLogListId.add(time);

    }

    public class MHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HEART:
                    if (TempHumidityBleUtils.getInstance() != null) {
                        TempHumidityBleUtils.getInstance().getDeviceStatus();
                        mMHandler.sendEmptyMessageDelayed(HEART, 5000);
                    }

                    break;

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMHandler.removeMessages(HEART);
        if (mBluetoothService != null)
            mBluetoothService.disconnectAll();
    }
}

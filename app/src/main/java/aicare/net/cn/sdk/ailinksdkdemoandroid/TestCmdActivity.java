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
import android.widget.TextView;

import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendDataBean;
import com.pingwang.bluetoothlib.listener.CallbackDisIm;
import com.pingwang.bluetoothlib.listener.OnBleDeviceDataListener;
import com.pingwang.bluetoothlib.listener.OnBleOtherDataListener;
import com.pingwang.bluetoothlib.listener.OnCallbackDis;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;
import com.pingwang.bluetoothlib.utils.UuidUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * xing<br>
 * 2019/4/25<br>
 * 测试指令界面
 */
public class TestCmdActivity extends BleBaseActivity implements OnCallbackDis, OnBleDeviceDataListener, View.OnClickListener, OnBleOtherDataListener {

    private TextView tv_receive_number;
    private TextView tv_send_number;

    private static String TAG = TestCmdActivity.class.getName();
    private final int REFRESH_DATA = 3;
    private final int SEND_DATA = 4;
    private List<String> mList;
    private ArrayAdapter listAdapter;
    private Context mContext;
    private EditText et_cmd, et_uuid, et_uuid_server, et_time, et_notify;
    private BleDevice bleDevice;
    private String mAddress;
    private int sendTime = 1000;
    private UUID sendUuid = null;
    private UUID sendUuidServer = null;
    private String uuidEnd = "-0000-1000-8000-00805F9B34FB";
    private String sendCmd;
    private long mSendNumber = 0;
    private long mReceiveNumber = 0;
    private boolean mShowLog = true;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {


                case REFRESH_DATA:
                    if (listAdapter != null)
                        listAdapter.notifyDataSetChanged();
                    break;

                case SEND_DATA:

                    if (sendUuid != null && sendUuidServer != null) {
                        SendDataBean sendDataBean = new SendDataBean(sendCmd.getBytes(), sendUuid, BleConfig.WRITE_DATA, sendUuidServer);
                        bleDevice.sendData(sendDataBean);
                        if (sendTime > 0) {
                            mHandler.sendEmptyMessageDelayed(SEND_DATA, sendTime);
                        }
                        mSendNumber += sendDataBean.getHex().length;
                        tv_send_number.setText(String.valueOf(mSendNumber));
                    }

                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_cmd);
        mContext = this;
        mAddress = getIntent().getStringExtra("mac");
        init();
    }

    private void init() {
        mList = new ArrayList<>();
        ListView listView = findViewById(R.id.listview);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        listView.setAdapter(listAdapter);

        findViewById(R.id.clear).setOnClickListener(this);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn_uuid).setOnClickListener(this);
        findViewById(R.id.btn_time).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_notify).setOnClickListener(this);
        findViewById(R.id.btn_notify_status).setOnClickListener(this);
        findViewById(R.id.btn_uuid_server).setOnClickListener(this);
        findViewById(R.id.btn_log).setOnClickListener(this);

        et_cmd = findViewById(R.id.et_cmd);
        et_uuid = findViewById(R.id.et_uuid);
        et_time = findViewById(R.id.et_time);
        et_notify = findViewById(R.id.et_notify);
        et_uuid_server = findViewById(R.id.et_uuid_server);
        tv_receive_number = findViewById(R.id.tv_receive_number);
        tv_send_number = findViewById(R.id.tv_send_number);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                sendCmd = et_cmd.getText().toString().trim();
                mHandler.removeMessages(SEND_DATA);
                mHandler.sendEmptyMessage(SEND_DATA);
                break;

            case R.id.btn_stop:
                mHandler.removeMessages(SEND_DATA);
                break;
            case R.id.clear:
                if (mList != null)
                    mList.clear();
                mSendNumber=0;
                mReceiveNumber = 0;
                tv_receive_number.setText("0");
                tv_send_number.setText("0");
                mHandler.sendEmptyMessage(REFRESH_DATA);
                break;

            case R.id.btn_uuid:
                String uuid = et_uuid.getText().toString().trim().toUpperCase(Locale.ENGLISH);
                sendUuid = UuidUtils.getUuid(uuid);
                break;
            case R.id.btn_uuid_server:
                String uuidServer = et_uuid_server.getText().toString().trim().toUpperCase(Locale.ENGLISH);
                sendUuidServer = UuidUtils.getUuid(uuidServer);
                break;
            case R.id.btn_time:
                String time = et_time.getText().toString().trim().toUpperCase(Locale.ENGLISH);
                sendTime = Integer.parseInt(time);
                break;

            case R.id.btn_notify:
                String notify = et_notify.getText().toString().trim().toUpperCase(Locale.ENGLISH);
                readNotify(notify, mNotify);
                break;
            case R.id.btn_notify_status:
                mNotify = !mNotify;
                ((Button) v).setText("" + mNotify);
                break;
            case R.id.btn_log:
                mShowLog = !mShowLog;
                ((Button) v).setText("log:" + (mShowLog ? "Y" : "N"));
                break;

        }
    }

    private boolean mNotify = true;

    /**
     * 设置通知
     */
    private void readNotify(String notify, boolean notifyOpen) {
        UUID uuidNotify = UuidUtils.getUuid(notify);
        if (bleDevice != null && sendUuidServer != null) {
            if (notifyOpen) {
                bleDevice.setNotify(sendUuidServer, uuidNotify);
            } else {
                bleDevice.setCloseNotify(sendUuidServer, uuidNotify);
            }

        } else {
            mList.add(TimeUtils.getTime() + "请先设置特征UUID和服务UUID");
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }
    }

    //---------------------------------服务---------------------------------------------------


    @Override
    public void onServiceSuccess() {
        BleLog.i(TAG, "服务与界面建立连接成功");
        //与服务建立连接
        if (mBluetoothService != null) {
            bleDevice = mBluetoothService.getBleDevice(mAddress);
            CallbackDisIm.getInstance().addListListener(this);
            if (bleDevice != null) {
                bleDevice.setOnBleOtherDataListener(this);
                bleDevice.setOnBleDeviceDataListener(this);

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
        if (bleDevice != null) {
            bleDevice.disconnect();
            bleDevice = null;
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
        mList.add(TimeUtils.getTime() + "连接断开");
        mHandler.sendEmptyMessage(REFRESH_DATA);
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
    }

    //-----------------通知-------------------



    @Override
    public void onNotifyData(byte[] hex, int type) {
        if (!mShowLog) {
            return;
        }
        String data = "";
        if (hex != null)
            data = BleStrUtils.byte2HexStr(hex);
        if (type == 100) {
            mList.add(TimeUtils.getTime() + "send->" + data);
        } else {
            mList.add(TimeUtils.getTime() + "notify->" + data);
            mReceiveNumber += hex.length;
            tv_receive_number.setText(String.valueOf(mReceiveNumber));
        }
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onNotifyOtherData(byte[] hex) {
        if (mShowLog) {
            String data = "";
            if (hex != null)
                data = BleStrUtils.byte2HexStr(hex);
            mList.add(TimeUtils.getTime() + "notify->" + data);
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }
        mReceiveNumber += hex.length;
        tv_receive_number.setText(String.valueOf(mReceiveNumber));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        BleLog.i(TAG, "onDestroy");
    }
}

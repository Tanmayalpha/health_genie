package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.pingwang.bluetoothlib.utils.BleDataUtils;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.Transmission.TransmissionDeviceData;

/**
 * 透传界面
 */
public class TransmissionActivity extends BleBaseActivity implements View.OnClickListener, OnCallbackBle, TransmissionDeviceData.MyBleCallback {

    private TextView tv_device_info;
    private EditText et, et_cid,et_a6,et_customize,et_name;
    private Button send,send_a6, bt_clear, bt_clear_log,bt_cid,send_customize;
    private ListView mListView;
    private List<String> mlogList;
    private final int ToRefreUi = 1;
    private MHandler mMHandler;
    private ArrayAdapter listAdapter;
    private String mAddress;
    private TransmissionDeviceData mTransmissionDeviceData;

    @Override
    public void onServiceSuccess() {
        mlogList.add(0, "服务与界面建立连接成功");
        mMHandler.sendEmptyMessage(ToRefreUi);
//        BleLog.i(TAG, "服务与界面建立连接成功");
        //与服务建立连接
        if (mBluetoothService != null) {
            mBluetoothService.setOnCallback(this);
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                if (tv_device_info!=null) {
                    tv_device_info.setText("MAC:"+bleDevice.getMac());
                }
                mTransmissionDeviceData = new TransmissionDeviceData(bleDevice);
                mTransmissionDeviceData.setMyBleCallback(this);


            }
        }
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //禁止横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_transmission);
        WeakReference weakReference = new WeakReference(new MHandler());
        mMHandler = (MHandler) weakReference.get();
        tv_device_info = findViewById(R.id.tv_device_info);
        et = findViewById(R.id.et);
        et_a6 = findViewById(R.id.et_a6);
        et_cid = findViewById(R.id.et_cid);
        bt_cid = findViewById(R.id.bt_cid);
        send = findViewById(R.id.send);
        send_a6 = findViewById(R.id.send_a6);
        et_customize = findViewById(R.id.et_customize);
        send_customize = findViewById(R.id.send_customize);
        et_name = findViewById(R.id.et_name);
        bt_clear = findViewById(R.id.bt_clear);
        bt_clear_log = findViewById(R.id.bt_clear_log);
        mListView = findViewById(R.id.listview);
        send.setOnClickListener(this);
        send_a6.setOnClickListener(this);
        send_customize.setOnClickListener(this);
        findViewById(R.id.send_name).setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        bt_cid.setOnClickListener(this);
        bt_clear_log.setOnClickListener(this);
        mAddress = getIntent().getStringExtra("mac");
        mlogList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mlogList);
        mListView.setAdapter(listAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.send) {
            if (!et.getText().toString().isEmpty() && !et_cid.getText().toString().isEmpty()) {
                String hex = et.getText().toString().toUpperCase().trim();
                byte[] hexStr = BleStrUtils.stringToByte(hex);
                String cid = et_cid.getText().toString().toUpperCase().trim();
                int hexStrCid = Integer.parseInt(cid, 16);
                if (mTransmissionDeviceData != null) {
                    mTransmissionDeviceData.setSendData(hexStrCid, hexStr);
                }
            }
        } else if (id == R.id.bt_clear) {
            et.setText("");
        } else if (id == R.id.bt_clear_log) {
            mlogList.clear();
            mMHandler.sendEmptyMessage(ToRefreUi);
        } else if (id == R.id.bt_cid) {

            if (mTransmissionDeviceData != null) {
                mTransmissionDeviceData.getCid();
            }
        }else if (id == R.id.send_a6) {
            String hex = et_a6.getText().toString().toUpperCase().trim();
            if (!TextUtils.isEmpty(hex)) {
                byte[] hexStr = BleStrUtils.stringToByte(hex);
                if (mTransmissionDeviceData != null) {
                    mTransmissionDeviceData.setSendDataA6( hexStr);
                }
            }
        }else if (id == R.id.send_customize) {
            String hex = et_customize.getText().toString().toUpperCase().trim();
            if (!TextUtils.isEmpty(hex)) {
                byte[] hexStr = BleStrUtils.stringToByte(hex);
                if (mTransmissionDeviceData != null) {
                    mTransmissionDeviceData.setSendDataCustomize( hexStr);
                }
            }
        }else if (id == R.id.send_name) {
            String name = et_name.getText().toString().trim();
            if (!TextUtils.isEmpty(name)) {
                byte[] names = BleDataUtils.getInstance().getBleName(name);
                mTransmissionDeviceData.setSendDataA6(BleSendCmdUtil.getInstance().setBleName(names));
            }
        }
    }

    @Override
    public void onVersion(String version) {

    }

    @Override
    public void showData(String data, int type) {
        mlogList.add(0, "收 payload数据" +  TimeUtils.getTime() +"cid=" + type + "\n" + data);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onSupportUnit(List<SupportUnitBean> list) {

    }

    @Override
    public void onCid(int cid, int vid, int pid) {
        mlogList.add(0, "收" + TimeUtils.getTime() + "cid:" + cid + "||vid:" + vid + "||pid:" + pid);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void otherData(byte[] hex, String data) {
        mlogList.add(0, "收 透传数据" + TimeUtils.getTime() + data);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void sendData(String data) {
        mlogList.add(0, "发 " + TimeUtils.getTime() + data);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }




    private class MHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ToRefreUi:
                    if (listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                    }
                    break;

                default:

                    break;

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService!=null) {
            mBluetoothService.disconnectAll();
        }
    }
}

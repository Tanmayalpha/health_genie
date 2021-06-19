package aicare.net.cn.sdk.ailinksdkdemoandroid.find;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.pingwang.bluetoothlib.BleBaseActivity;
import com.pingwang.bluetoothlib.device.BleDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.findDevice.FindConnectDeviceInfoBean;
import cn.net.aicare.modulelibrary.module.findDevice.FindDeviceData;

public class FindDeviceActivity extends BleBaseActivity implements View.OnClickListener, FindDeviceData.onNotifyData {

    private Button btn_device_id_0, btn_device_id_1, btn_device_id_2, btn_device_id_3, btn_device_id_4, btn_device_id_5, btn_device_id_6;
    private Button btn_clear, btn_get_connect_number, btn_connect_info_list,btn_scan_ble;
    private EditText et_send_data;
    private ListView list_view;

    private List<String> mList;
    private ArrayAdapter mListAdapter;
    private Context mContext;
    private String mMac;
    private BleDevice mBleDevice;
    private FindDeviceData mFindDeviceData;
    private Map<Integer, FindConnectDeviceInfoBean> mMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_device);
        mContext = this;
        btn_get_connect_number = findViewById(R.id.btn_get_connect_number);
        btn_connect_info_list = findViewById(R.id.btn_connect_info_list);
        btn_device_id_0 = findViewById(R.id.btn_device_id_0);
        btn_device_id_1 = findViewById(R.id.btn_device_id_1);
        btn_device_id_2 = findViewById(R.id.btn_device_id_2);
        btn_device_id_3 = findViewById(R.id.btn_device_id_3);
        btn_device_id_4 = findViewById(R.id.btn_device_id_4);
        btn_device_id_5 = findViewById(R.id.btn_device_id_5);
        btn_device_id_6 = findViewById(R.id.btn_device_id_6);
        et_send_data = findViewById(R.id.et_send_data);
        btn_clear = findViewById(R.id.btn_clear);
        list_view = findViewById(R.id.list_view);
        btn_scan_ble = findViewById(R.id.btn_scan_ble);

        btn_device_id_0.setOnClickListener(this);
        btn_device_id_1.setOnClickListener(this);
        btn_device_id_2.setOnClickListener(this);
        btn_device_id_3.setOnClickListener(this);
        btn_device_id_4.setOnClickListener(this);
        btn_device_id_5.setOnClickListener(this);
        btn_device_id_6.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_scan_ble.setOnClickListener(this);
        btn_get_connect_number.setOnClickListener(this);
        btn_connect_info_list.setOnClickListener(this);

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

            case R.id.btn_connect_info_list:
                if (mFindDeviceData!=null) {
                    mFindDeviceData.getConnectInfoList();
                }
                break;
            case R.id.btn_get_connect_number:
                if (mFindDeviceData!=null) {
                    mFindDeviceData.getConnectDeviceNumber();
                }
                break;
            case R.id.btn_device_id_0:
                sendCmd(0);
                break;
            case R.id.btn_device_id_1:
                sendCmd(1);
                break;
            case R.id.btn_device_id_2:
                sendCmd(2);
                break;
            case R.id.btn_device_id_3:
                sendCmd(3);
                break;
            case R.id.btn_device_id_4:
                sendCmd(4);
                break;
            case R.id.btn_device_id_5:
                sendCmd(5);
                break;
            case R.id.btn_device_id_6:
                sendCmd(6);
                break;
            case R.id.btn_clear:
                mList.clear();
                if (mListAdapter != null) {
                    mListAdapter.notifyDataSetChanged();
                }
                break;

        }
    }


    /**
     * 发送指令
     */
    private void sendCmd(int id) {
        if (mMap != null) {
            FindConnectDeviceInfoBean findConnectDeviceInfoBean = mMap.get(id);
            if (findConnectDeviceInfoBean != null) {
                String trim = et_send_data.getText().toString().trim();
                mFindDeviceData.setCmd(findConnectDeviceInfoBean.getDeviceId(), trim, findConnectDeviceInfoBean.getMac());
            } else {
                Toast.makeText(mContext, "当前ID没有设备", Toast.LENGTH_SHORT).show();
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
        mBleDevice = mBluetoothService.getBleDevice(mMac);
        if (mBleDevice != null) {
            mFindDeviceData = FindDeviceData.getInstance(mBleDevice);
            mFindDeviceData.setOnNotifyData(this);
        }
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }


    @Override
    public void onData(byte[] data, int type) {

    }

    @Override
    public void onConnectNumber(int number) {
        mList.add("当前连接的设备数量:" + number);
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onConnectDeviceInfoList(List<FindConnectDeviceInfoBean> list) {
        StringBuilder deviceInfo = new StringBuilder();
        for (FindConnectDeviceInfoBean findConnectDeviceInfoBean : list) {
            mMap.put(findConnectDeviceInfoBean.getDeviceId(), findConnectDeviceInfoBean);
            deviceInfo.append("设备ID").append(findConnectDeviceInfoBean.getDeviceId()).append(" || MAC地址:").append(findConnectDeviceInfoBean.getMac()).append("\n");
        }
        mList.add(deviceInfo.toString());
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onConnectDeviceStatus(int id, int status) {
        String statusStr = status == 0 ? "断开" : "连接";
        mList.add("设备ID:" + id + " || 状态:" + statusStr);
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        }
    }


    //---------------新--------------


    @Override
    public void onNearbyDeviceInfo(String mac, int rssi, byte[] broadcastData) {

    }

    @Override
    public void onConnectDeviceStatus(String mac, int status) {

    }

    @Override
    public void onConnectDeviceInfo(int status, String mac) {

    }
}

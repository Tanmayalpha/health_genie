package aicare.net.cn.sdk.ailinksdkdemoandroid.find;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.pingwang.bluetoothlib.AILinkSDK;
import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.pingwang.bluetoothlib.listener.OnScanFilterListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;
import aicare.net.cn.sdk.ailinksdkdemoandroid.BuildConfig;
import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleAppBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.SP;
import aicare.net.cn.sdk.ailinksdkdemoandroid.view.MyItemDecoration;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.net.aicare.modulelibrary.module.findDevice.FindDeviceData;

public class FindDeviceNewActivity extends BleAppBaseActivity implements View.OnClickListener, OnCallbackBle, FindDeviceData.onNotifyData, OnScanFilterListener, FindDeviceAdapter.OnItemClickListener {


    private final int REFRESH_DATA = 2;
    private final int START_SCAN_BLE =3;
    private final static String BLE_NAME_START = "FiLink";
    private final static String BLE_UUID_1 = "74278bdab64445208f0c720eaf00001a";
    private final static String BLE_UUID_2 = "74278bdab64445208f0c720eaf00001b";

    private Button btn_clear, btn_scan_ble;
    private TextView tv_status;
    private ListView list_view;
    private RecyclerView rv_device;

    private List<String> mList;
    private ArrayAdapter mListAdapter;
    private String mMac;
    private BleDevice mBleDevice;
    private FindDeviceData mFindDeviceData;
    private volatile ArrayList<FindDeviceBean> mFindDeviceBeanList;
    private FindDeviceAdapter mFindDeviceAdapter;

    @Override
    protected void uiHandlerMessage(Message msg) {

        switch (msg.what) {

            case REFRESH_DATA:
                if (mListAdapter != null) {
                    mListAdapter.notifyDataSetChanged();
                }
                break;

            case START_SCAN_BLE:
                bleOpen();
                break;

        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_device_new;
    }

    @Override
    protected void initListener() {
        btn_clear.setOnClickListener(this);
        btn_scan_ble.setOnClickListener(this);
    }

    @Override
    protected void initData() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name) + ":" + BuildConfig.VERSION_NAME);
        }
        SP.init(this);
        // 获取Mac
//        mMac = getIntent().getStringExtra("mac");

        // 初始化列表
        mList = new ArrayList<>();
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        list_view.setAdapter(mListAdapter);

        mFindDeviceBeanList = new ArrayList<>();
        String findDeviceMacList = SP.getInstance().getFindDeviceMacList();
        if (!TextUtils.isEmpty(findDeviceMacList) && findDeviceMacList.contains(";")) {
            String[] split = findDeviceMacList.split(";");
            for (int i = 0; i < split.length; i++) {
                String mac = split[i];
                if (!TextUtils.isEmpty(mac)) {
                    mFindDeviceBeanList.add(new FindDeviceBean(mac, String.valueOf(i), -60));
                }
            }
        }

        mFindDeviceAdapter = new FindDeviceAdapter(mContext, mFindDeviceBeanList, this);
        rv_device.setAdapter(mFindDeviceAdapter);


    }

    @Override
    protected void initView() {
        AILinkSDK.getInstance().init(getApplication());//sdk
        btn_clear = findViewById(R.id.btn_clear);
        tv_status = findViewById(R.id.tv_status);
        list_view = findViewById(R.id.list_view);
        btn_scan_ble = findViewById(R.id.btn_scan_ble);
        rv_device = findViewById(R.id.rv_device);
        rv_device.setLayoutManager(new GridLayoutManager(mContext, 2, LinearLayoutManager.VERTICAL, false));
        rv_device.addItemDecoration(new MyItemDecoration(mContext, LinearLayoutManager.VERTICAL, 1, mContext.getResources().getColor(R.color.public_white)));
//        rv_device.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onItemClick(int position) {

        if (mFindDeviceBeanList.size() > position) {
            FindDeviceBean findDeviceBean = mFindDeviceBeanList.get(position);
            if (mFindDeviceData != null) {
//                mFindDeviceData.setDisconnectDevice();
                mFindDeviceData.setConnectDevice(findDeviceBean.getMac());

            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_scan_ble:
                if (mFindDeviceData != null) {
//                    mFindDeviceData.setNearbyDevice(1, 10, new byte[]{});
//                    if (rv_device.getVisibility() != View.VISIBLE) {
//                        rv_device.setVisibility(View.VISIBLE);
//                    }
                    mFindDeviceBeanList.clear();
                    if (mFindDeviceAdapter != null) {
                        mFindDeviceAdapter.notifyDataSetChanged();
                    }
                    mFindDeviceData.getConnectInfoList();
                    mBluetoothService.scanLeDevice(30 * 1000);
                }
                break;

            case R.id.btn_clear:
                mList.clear();
                if (mListAdapter != null) {
                    mListAdapter.notifyDataSetChanged();
                }
                break;

        }
    }


    @Override
    protected void onDestroy() {
        StringBuilder mac = new StringBuilder();
        for (FindDeviceBean findDeviceBean : mFindDeviceBeanList) {
            mac.append(findDeviceBean.getMac()).append(";");
        }
        if (mac.length() > 0) {
            mac.deleteCharAt(mac.length() - 1);
        }
        SP.getInstance().putFindDeviceMacList(mac.toString());
        if (mBluetoothService != null) {
            mBluetoothService.disconnectAll();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    // ------------------- 权限 ------------------
    @Override
    protected void onPermissionsOk() {
        if (mBluetoothService != null) {
            mBluetoothService.setOnCallback(this);
            mBleDevice = mBluetoothService.getBleDevice(mMac);
            mBluetoothService.setOnScanFilterListener(this);
            if (mBleDevice != null) {
                mFindDeviceData = FindDeviceData.getInstance(mBleDevice);
                mFindDeviceData.setOnNotifyData(this);
                if (tv_status != null)
                    tv_status.setText("已连接:" + mMac);
                mList.add("连接设备成功!");
                mHandler.sendEmptyMessage(REFRESH_DATA);
            } else {
                mHandler.removeMessages(START_SCAN_BLE);
                mHandler.sendEmptyMessage(START_SCAN_BLE);
            }
        }
    }


    //--------------------------


    @Override
    public void onServiceSuccess() {
        if (mBluetoothService != null) {
            initPermissions();
        }


    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {
        if (mBluetoothService!=null){
            mBluetoothService.disconnectAll();
        }
        mFindDeviceData = null;
        mBluetoothService=null;
    }

    @Override
    public boolean onFilter(BleValueBean bleValueBean) {
        if (bleValueBean.getName() != null && bleValueBean.getName().trim().toUpperCase().startsWith(BLE_NAME_START.toUpperCase())) {
            return true;
        } else {
            byte[] data = bleValueBean.getManufacturerData();
            if (data != null && data.length > 20) {
                byte[] uuidData = new byte[16];
                System.arraycopy(data, 4, uuidData, 0, uuidData.length);
                String uuid = BleStrUtils.byte2HexStr_1(uuidData);
                if (uuid.equalsIgnoreCase(BLE_UUID_1) || uuid.equalsIgnoreCase(BLE_UUID_2)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onScanning(BleValueBean bleValueBean) {
        if (TextUtils.isEmpty(mMac) && bleValueBean.getName() != null && bleValueBean.getName().trim().toUpperCase().startsWith(BLE_NAME_START.toUpperCase())) {
            mMac = bleValueBean.getMac();
        } else if (bleValueBean.getMac().equalsIgnoreCase(mMac) && mFindDeviceData == null) {
            BleConfig.setHandshakeStatus(mMac, false);
            mBluetoothService.stopScan();
            mBluetoothService.connectDevice(mMac);
            mBluetoothService.setOnCallback(this);
        } else if (bleValueBean.getName() == null || !bleValueBean.getName().trim().toUpperCase().startsWith(BLE_NAME_START.toUpperCase())) {
            onNearbyDeviceInfo(bleValueBean.getMac(), bleValueBean.getRssi(), bleValueBean.getManufacturerData());
        }

    }

    @Override
    public void onDisConnected(String mac, int code) {
        if (mac.equalsIgnoreCase(mMac)) {
            mFindDeviceData = null;
            mList.add("连接断开:"+code);
            mHandler.sendEmptyMessage(REFRESH_DATA);
            mHandler.removeMessages(START_SCAN_BLE);
            mHandler.sendEmptyMessageDelayed(START_SCAN_BLE,2000);
        }
    }

    @Override
    public void onServicesDiscovered(String mac) {
        if (mac.equalsIgnoreCase(mMac)) {
            mBluetoothService.stopScan();
            onServiceSuccess();
        }
    }

    @Override
    public void bleOpen() {

        if (mBluetoothService != null) {
            mBluetoothService.stopScan();

            mList.add("正在扫描连接...");
            if (tv_status != null)
                tv_status.setText("正在连接...");
            mHandler.sendEmptyMessage(REFRESH_DATA);
            mBluetoothService.scanLeDevice(0, BleConfig.UUID_SERVER_AILINK);
        }

    }

    @Override
    public void bleClose() {
        mFindDeviceData = null;
        mList.add("蓝牙已关闭!");
        if (tv_status != null)
            tv_status.setText("蓝牙已关闭!");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onData(byte[] data, int type) {

    }


    //---------------新--------------


    @Override
    public void onNearbyDeviceInfo(String mac, int rssi, byte[] broadcastData) {
        for (FindDeviceBean findDeviceBean : mFindDeviceBeanList) {
            if (findDeviceBean.getMac().equalsIgnoreCase(mac)) {
                return;
            }
        }
        mFindDeviceBeanList.add(new FindDeviceBean(mac, rssi, broadcastData));
        if (mFindDeviceAdapter != null) {
            mFindDeviceAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onConnectDeviceStatus(String mac, int status) {
        String statusStr = "";
        switch (status) {

            case 0:
                statusStr = "连接成功";
                break;
            case 1:
                statusStr = "断开连接";
                break;
            case 2:
                statusStr = "响铃...";
                break;
            case 3:
                statusStr = "连接超时";
                break;

        }
        mList.add("设备:" + mac + " || 状态:" + statusStr);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    public void onConnectDeviceInfo(int status, String mac) {
        BleLog.i(TAG, "当前连接的设备:" + mac + " || status=" + status);
        if (status == 1) {

            mFindDeviceBeanList.add(new FindDeviceBean(mac, -60, new byte[]{}));
            if (mFindDeviceAdapter != null) {
                mFindDeviceAdapter.notifyDataSetChanged();
            }
        }

    }
}

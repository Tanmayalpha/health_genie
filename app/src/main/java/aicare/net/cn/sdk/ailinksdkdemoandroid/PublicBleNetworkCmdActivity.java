package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.pingwang.bluetoothlib.config.CmdConfig;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.listener.CallbackDisIm;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleConnectStatus;
import com.pingwang.bluetoothlib.listener.OnBleDeviceDataListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnCallbackDis;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.listener.OnWifiInfoListener;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.dialog.WifiDialog;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.BodyFatScale.BodyFatDataUtil;


/**
 * 通用的ble配网界面
 */
public class PublicBleNetworkCmdActivity extends BleBaseActivity implements OnCallbackDis, OnBleVersionListener, OnWifiInfoListener
        , OnMcuParameterListener, OnBleCompanyListener, View.OnClickListener, OnBleDeviceDataListener, OnBleConnectStatus {

    private static String TAG = PublicBleNetworkCmdActivity.class.getName();
    private final int REFRESH_DATA = 3;
    private List<String> mList;
    private ArrayAdapter listAdapter;

    private Context mContext;
    private BleDevice mBleDevice;
    private String mAddress;
    private BleSendCmdUtil mBleSendCmdUtil;
    private int type;
    private byte unit = 0;
    private HashMap<Integer, String> wifiMap;
    private HashMap<Integer, String> mMapWifiName;
    private EditText select_wifi_et, et_ip, et_port, et_url;
    private int no;
    private ListView listView;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {


                case REFRESH_DATA:
                    if (listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                        listView.smoothScrollToPosition(mList.size() - 1);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_ble_network);
        mContext = this;
        mAddress = getIntent().getStringExtra("mac");
        type = getIntent().getIntExtra("type", -1);
        wifiMap = new HashMap<>();
        mMapWifiName = new HashMap<>();
        mBleSendCmdUtil = BleSendCmdUtil.getInstance();
        init();
    }

    private void init() {
        mList = new ArrayList<>();
        listView = findViewById(R.id.listview);
        select_wifi_et = findViewById(R.id.select_wifi_et);
        et_ip = findViewById(R.id.et_ip);
        et_port = findViewById(R.id.et_port);
        et_url = findViewById(R.id.et_url);

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        listView.setAdapter(listAdapter);


        findViewById(R.id.clear).setOnClickListener(this);
        findViewById(R.id.scan_wifi).setOnClickListener(this);
        findViewById(R.id.check_wifi_state).setOnClickListener(this);
        findViewById(R.id.check_device_id).setOnClickListener(this);
        findViewById(R.id.connect_wifi).setOnClickListener(this);
        findViewById(R.id.disconnect).setOnClickListener(this);
        findViewById(R.id.get_set_name).setOnClickListener(this);
        findViewById(R.id.get_set_paw).setOnClickListener(this);
        findViewById(R.id.reset).setOnClickListener(this);
        findViewById(R.id.check_ip).setOnClickListener(this);
        findViewById(R.id.check_port).setOnClickListener(this);
        findViewById(R.id.check_url).setOnClickListener(this);
        findViewById(R.id.set_ip).setOnClickListener(this);
        findViewById(R.id.set_port).setOnClickListener(this);
        findViewById(R.id.set_url).setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.clear:
                if (mList != null)
                    mList.clear();

            case R.id.scan_wifi:
                wifiMap.clear();
                mMapWifiName.clear();
                mList.add("扫描热点");
                if (mBleDevice != null) mBleDevice.sendData(scanWifi());
                break;
            case R.id.check_wifi_state:
                mList.add("查看wifi当前状态");
                if (mBleDevice != null) mBleDevice.sendData(checkWiFiState());
                break;
            case R.id.check_device_id:
                mList.add("查看设备的ID");
                if (mBleDevice != null) mBleDevice.sendData(getSnDeviceId());
                break;
            case R.id.connect_wifi:
                try {
                    no = Integer.parseInt(select_wifi_et.getText().toString());
                    String wifiMac = wifiMap.get(no);
                    String wifiName = mMapWifiName.get(no);
                    WifiDialog.newInstance().setTitle(wifiName, wifiMac).setOnDialogListener(new WifiDialog.OnDialogListener() {
                        @Override
                        public void tvCancelListener(View v) {

                        }

                        @Override
                        public void tvSucceedListener(View v, String data) {
                            mBleDevice.sendData(setWifiMac(wifiMac));
                            if (data.equals("") || data.length() >= 8) {
                                setPaw(data);
                            } else {
                                mList.add("按照操作规则来，输入密码。");
                                mHandler.sendEmptyMessage(REFRESH_DATA);
                            }
                        }
                    }).show(getSupportFragmentManager());
                } catch (Exception e) {
                    e.printStackTrace();
                    //不输入编号，不输入数字，瞎搞的人。呸
                    mList.add("按照操作规则来，输入热点编号。");
                }

                break;
            case R.id.disconnect:
                mList.add("断开连接");
                if (mBleDevice != null) mBleDevice.sendData(setDisconnectWifi());
                break;
            case R.id.get_set_paw:
                mList.add("获取到设置的密码");
                if (mBleDevice != null) mBleDevice.sendData(getConnectWifiPwd());
                break;

            case R.id.get_set_name:
                mList.add("获取到设置的热点");
                if (mBleDevice != null) mBleDevice.sendData(getConnectWifiName());
                break;
            case R.id.reset:
                mList.add("回复出厂设置");
                if (mBleDevice != null) mBleDevice.sendData(reset());
                break;
            case R.id.check_ip:
                mList.add("查看Ip");
                if (mBleDevice != null) mBleDevice.sendData(checkIp());
                break;
            case R.id.check_port:
                mList.add("查看端口号");
                if (mBleDevice != null) mBleDevice.sendData(checkPort());
                break;
            case R.id.check_url:
                mList.add("查看URL");
                if (mBleDevice != null) mBleDevice.sendData(checkUrl());
                break;
            case R.id.set_ip:
                String ipStr = et_ip.getText().toString();
                if (!ipStr.isEmpty()) {
                    mList.add("设置Ip地址");
                    setIp(convertToASCII(ipStr));
                } else {
                    mList.add("最起码把Ip地址填一下吧");
                }
                break;
            case R.id.set_url:
                String urlStr = et_url.getText().toString();
                mList.add("设置路径地址");
                setIpUrl(convertToASCII(urlStr));
                break;
            case R.id.set_port:

                try {
                    int port = Integer.parseInt(et_port.getText().toString());
                    mList.add("设置Ip地址");
                    setPort(port);
                } catch (Exception e) {
                    e.printStackTrace();
                    mList.add("输入的数字");
                }
                break;

        }
        mHandler.sendEmptyMessage(REFRESH_DATA);

    }

    //---------------------------------服务---------------------------------------------------


    @Override
    public void onServiceSuccess() {
        CallbackDisIm.getInstance().addListListener(this);
        mList.add("服务与界面建立连接成功");
        //与服务建立连接
        if (mBluetoothService != null) {
            mBleDevice = mBluetoothService.getBleDevice(mAddress);
            if (mBleDevice != null) {
                mBleDevice.setOnBleVersionListener(PublicBleNetworkCmdActivity.this);
                mBleDevice.setOnMcuParameterListener(PublicBleNetworkCmdActivity.this);
                mBleDevice.setOnWifiInfoListener(PublicBleNetworkCmdActivity.this);
                mBleDevice.setOnBleConnectListener(this);
                mBleDevice.setOnBleDeviceDataListener(this);
            }
        }

        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onServiceErr() {
        mList.add("服务与界面连接断开");
        //与服务断开连接
        mBluetoothService = null;
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    public void unbindServices() {
        CallbackDisIm.getInstance().removeListener(this);
        if (mBleDevice != null) {
            mBleDevice.disconnect();
            mBleDevice = null;
        }
    }


    //-----------------状态-------------------


    @Override
    public void onConnecting(@NonNull String mac) {
        //TODO 连接中

    }

    @Override
    public void onDisConnected(@NonNull String mac, int code) {
        //TODO 连接断开
        if (mAddress.equals(mac)) {

            finish();
        }
    }

    @Override
    public void onServicesDiscovered(@NonNull String mac) {
        //TODO 连接成功(获取服务成功)
        if (mAddress.equals(mac)) {
            mList.add("连接成功(获取服务成功)");
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }
    }


    @Override
    public void bleOpen() {

    }

    @Override
    public void bleClose() {


        finish();
    }


    @Override
    public void onScanWiFiStatus(int status) {
        mList.add("扫描wifi状态: " + status);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onWifiListName(int no, String name) {
        mList.add("编号:" + no + "  热点：" + name);
        mMapWifiName.put(no, name);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onConnectedWifiName(String name) {
        mList.add("当前连接的热点名称：" + name);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onWifiListInfo(int no, String mac, int db, int type, int wifiStatus) {
        wifiMap.put(no, mac);

    }

    @Override
    public void onWifiScanFinish(int wifiNum) {
        mList.add("热点扫描完成,热点数：" + wifiNum);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    private boolean issetMac = false;

    @Override
    public void onSetWifiNameOrPawOrConnectCallback(int type, int status) {
        if (type == CmdConfig.SET_WIFI_MAC) {
            mList.add("设置的mac地址状态 " + status);
            if (status == BodyFatDataUtil.STATUS_SUCCESS) {
                issetMac = true;
            } else {

            }
            mHandler.sendEmptyMessage(REFRESH_DATA);

        }
        if (type == CmdConfig.SET_WIFI_PAW) {
            mList.add("设置的密码状态 " + status);
            mHandler.sendEmptyMessage(REFRESH_DATA);
            if (status == BodyFatDataUtil.STATUS_SUCCESS && issetMac) {
                mBleDevice.sendData(setConnectWifi());
            }
        }
        if (type == CmdConfig.DIS_OR_CON_WIFI) {
            mList.add("发起连接 " + status);
            mHandler.sendEmptyMessage(REFRESH_DATA);

        }
    }

    @Override
    public void getSelectWifiMac(String mac) {
        mList.add("获取到设置的wifi的mac地址 " + mac);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void getSelectWifiPaw(String pwd) {
        mList.add("获取到设置的wifi的密码 " + pwd);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void getSN(long sn) {
        mList.add("设备Id: " + sn);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onBleConnectStatus(int bleStatus, int wifiStatus, int workStatus) {
        String wifiStatusStr = "";
        switch (wifiStatus) {
            case 0:
                wifiStatusStr = "未配置AP";
                break;
            case 1:
                wifiStatusStr = "连接AP失败";
                break;
            case 2:
                wifiStatusStr = "连接的AP信号不好";
                break;
            case 3:
                wifiStatusStr = "成功连接上AP";
                break;
            case 4:
                wifiStatusStr = "正在连接AP";
                break;

        }
        mList.add("蓝牙状态：" + bleStatus + "\nwifi状态：" + wifiStatusStr + "\n工作状态：" + workStatus);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onNotifyDataA6(byte[] hex) {
        if ((hex[0] & 0xff) == 0x8b) {
            // 设置IP地址
            mList.add("设置的Ip地址状态" + (hex[1] & 0xff));
            mHandler.sendEmptyMessage(REFRESH_DATA);

        } else if ((hex[0] & 0xff) == 0x8d) {
            // 设置端口
            mList.add("设置的端口号的状态" + (hex[1] & 0xff));
            mHandler.sendEmptyMessage(REFRESH_DATA);

        } else if ((hex[0] & 0xff) == 0x96) {
            // 设置路径
            mList.add("设置的Url的状态" + (hex[1] & 0xff));
            mHandler.sendEmptyMessage(REFRESH_DATA);

        } else if ((hex[0] & 0xff) == 0x8c) {
            byte[] name = new byte[hex.length - 1];
            System.arraycopy(hex, 1, name, 0, name.length);
            mList.add("设置的Ip地址" + BleStrUtils.convertUTF8ToString(name));
            mHandler.sendEmptyMessage(REFRESH_DATA);

        } else if ((hex[0] & 0xff) == 0x8e) {
            int port = (((hex[1] & 0xff) << 8) | (hex[2] & 0xff));
            mList.add("设置的端口号: " + port);
            mHandler.sendEmptyMessage(REFRESH_DATA);

        } else if ((hex[0] & 0xff) == 0x97) {
            byte[] name = new byte[hex.length - 1];
            System.arraycopy(hex, 1, name, 0, name.length);
            mList.add("设置的Url:" + BleStrUtils.convertUTF8ToString(name));
            mHandler.sendEmptyMessage(REFRESH_DATA);

        }

    }


    //-----------------通知-------------------

    @Override
    public void onBmVersion(String version) {
        mList.add(TimeUtils.getTime() + "版本号:" + version);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void OnDID(int cid, int vid, int pid) {
        String didStr = "cid:" + cid + "||vid:" + vid + "||pid:" + pid;
        mList.add(TimeUtils.getTime() + "ID:" + didStr);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onMcuBatteryStatus(int status, int battery) {
        mList.add(TimeUtils.getTime() + "电量:" + battery + "%");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onSysTime(int status, int[] times) {
        String time =
                times[0] + "-" + times[1] + "-" + times[2] + "  " + times[3] + ":" + times[4] +
                        ":" + times[5];
        mList.add(TimeUtils.getTime() + "系统时间:" + time);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    public SendBleBean checkWiFiState() {
        byte[] bytes = new byte[1];
        bytes[0] = 0x26;
        return getSendBleBeam(bytes);
    }


    public SendBleBean scanWifi() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) CmdConfig.GET_SCAN_WIFI_RESULT;
        bytes[1] = 0x01;
        return getSendBleBeam(bytes);
    }

    public SendBleBean getSnDeviceId() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) 0x93;
        return getSendBleBeam(bytes);
    }

    public SendBleBean setWifiMac(String mac) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) CmdConfig.SET_WIFI_MAC;
        String[] s = mac.split(":");
        for (int i = 0; i < s.length; i++) {
            bytes[i + 1] = (byte) Integer.parseInt(s[i]);
        }
        return getSendBleBeam(bytes);

    }

    public SendBleBean setWifiPwd(int subpackage, byte[] password) {
        int length = 0;
        byte[] bytes1;
        if (password != null) {
            length = password.length + 1;
            bytes1 = new byte[length + 1];
            bytes1[0] = (byte) CmdConfig.SET_WIFI_PAW;
            bytes1[1] = (byte) subpackage;
            System.arraycopy(password, 0, bytes1, 2, password.length);

        } else {
            bytes1 = new byte[1];
            bytes1[0] = (byte) 0x86;
        }
        return getSendBleBeam(bytes1);
    }


    public SendBleBean setConnectWifi() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) CmdConfig.DIS_OR_CON_WIFI;
        bytes[1] = 0x01;
        return getSendBleBeam(bytes);
    }

    /**
     * 断开连接
     * Disconnect
     *
     * @return payload数据
     */
    public SendBleBean setDisconnectWifi() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) CmdConfig.DIS_OR_CON_WIFI;
        bytes[1] = 0x00;
        return getSendBleBeam(bytes);
    }

    /**
     * 获取当前连接的wifi的名字
     * Get the name of the currently connected wifi
     *
     * @return payload数据
     */
    public SendBleBean getConnectWifiName() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) CmdConfig.GET_WIFI_NAME;
        return getSendBleBeam(bytes);
    }

    /**
     * 获取连接的wifi密码
     *
     * @return payload数据
     */
    public SendBleBean getConnectWifiPwd() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) CmdConfig.GET_WIFI_PWD;
        return getSendBleBeam(bytes);
    }

    public SendBleBean reset() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x22;
        bytes[1] = 0x01;
        return getSendBleBeam(bytes);

    }


    public SendBleBean checkIp() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) 0x8c;
        return getSendBleBeam(bytes);
    }

    public SendBleBean checkPort() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) 0x8e;
        return getSendBleBeam(bytes);
    }

    public SendBleBean checkUrl() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) 0x97;
        return getSendBleBeam(bytes);
    }


    public SendBleBean environmentIp(int subpackage, byte[] bytesIp) {
        byte[] bytes1;
        if (bytesIp != null) {
            bytes1 = new byte[bytesIp.length + 2];
            bytes1[0] = (byte) 0x8b;
            bytes1[1] = (byte) subpackage;
            System.arraycopy(bytesIp, 0, bytes1, 2, bytesIp.length);
        } else {
            bytes1 = new byte[1];
            bytes1[0] = (byte) 0x8b;
        }

        return getSendBleBeam(bytes1);

    }

    public SendBleBean environmentPort(int port) {
        byte[] bytes1;
        bytes1 = new byte[3];
        bytes1[0] = (byte) 0x8d;
        bytes1[1] = (byte) (port >> 8);
        bytes1[2] = (byte) (port & 0xff);
        return getSendBleBeam(bytes1);
    }


    public SendBleBean environmentUrl(int subpackage, byte[] bytesIpUrl) {
        byte[] bytes1;
        if (bytesIpUrl != null) {
            bytes1 = new byte[bytesIpUrl.length + 2];
            bytes1[0] = (byte) 0x96;
            bytes1[1] = (byte) subpackage;
            System.arraycopy(bytesIpUrl, 0, bytes1, 2, bytesIpUrl.length);
        } else {
            bytes1 = new byte[1];
            bytes1[0] = (byte) 0x96;
        }

        return getSendBleBeam(bytes1);

    }


    private void setIp(byte[] ips) {
        if (ips.length <= 14) {
            if (mBleDevice != null) {
                mBleDevice.sendData(environmentIp(0, ips));
            }
        } else {
            boolean isend = false;
            int i = 0;
            byte[] byte1 = ips;
            while (!isend) {
                if (byte1.length > 14) {
                    byte[] bytes = new byte[14];
                    System.arraycopy(ips, i, bytes, 0, bytes.length);
                    if (mBleDevice != null) mBleDevice.sendData(environmentIp(1, bytes));
                    i = i + 14;
                    byte1 = Arrays.copyOf(ips, ips.length - i);
                } else {
                    isend = true;
                    byte[] bytes = new byte[ips.length - i];
                    System.arraycopy(ips, i, bytes, 0, bytes.length);
                    if (mBleDevice != null) mBleDevice.sendData(environmentIp(0, bytes));
                }

            }
        }

    }

    private void setPort(int port) {
        if (mBleDevice != null) mBleDevice.sendData(environmentPort(port));
    }

    private void setIpUrl(byte[] setIpUrl) {
        if (setIpUrl.length <= 14) {
            if (mBleDevice != null) mBleDevice.sendData(environmentUrl(0, setIpUrl));
        } else {
            boolean isend = false;
            int i = 0;
            byte[] byte1 = setIpUrl;
            while (!isend) {
                if (byte1.length > 14) {
                    byte[] bytes = new byte[14];
                    System.arraycopy(setIpUrl, i, bytes, 0, bytes.length);
                    if (mBleDevice != null) mBleDevice.sendData(environmentUrl(1, bytes));
                    i = i + 14;
                    byte1 = Arrays.copyOf(setIpUrl, setIpUrl.length - i);
                } else {
                    isend = true;
                    byte[] bytes = new byte[setIpUrl.length - i];
                    System.arraycopy(setIpUrl, i, bytes, 0, bytes.length);
                    if (mBleDevice != null) mBleDevice.sendData(environmentUrl(0, bytes));
                }

            }
        }

    }

    private byte[] convertToASCII(String string) {
        char[] ch = string.toCharArray();
        byte[] tmp = new byte[ch.length];
        for (int i = 0; i < ch.length; i++) {
            tmp[i] = (byte) Integer.valueOf(ch[i]).intValue();
        }
        return tmp;
    }

    private void setPaw(String paw) {
        if (paw.isEmpty()) {
            byte[] bytes = new byte[0];
            if (mBleDevice != null) mBleDevice.sendData(setWifiPwd(0, bytes));
        } else {
            byte[] password = BleStrUtils.stringToBytes(paw);
            if (password != null) {
                if (password.length < 14)
                    if (mBleDevice != null) mBleDevice.sendData(setWifiPwd(0, password));
                    else {
                        boolean isend = false;
                        int i = 0;
                        byte[] byte1 = password;
                        while (!isend) {
                            if (byte1.length > 14) {
                                byte[] bytes = new byte[14];
                                System.arraycopy(password, i, bytes, 0, bytes.length);

                                if (mBleDevice != null)
                                    mBleDevice.sendData(BodyFatDataUtil.getInstance().setWifiPwd(1, bytes));
                                i = i + 14;
                                byte1 = Arrays.copyOf(password, password.length - i);
                            } else {
                                isend = true;
                                byte[] bytes = new byte[password.length - i];
                                System.arraycopy(password, i, bytes, 0, bytes.length);

                                if (mBleDevice != null)
                                    mBleDevice.sendData(BodyFatDataUtil.getInstance().setWifiPwd(0, bytes));
                            }

                        }


                    }
            }
        }
    }

    private SendBleBean getSendBleBeam(byte[] bytes) {
        SendBleBean sendBleBean = new SendBleBean();
        sendBleBean.setHex(bytes);
        return sendBleBean;
    }


}

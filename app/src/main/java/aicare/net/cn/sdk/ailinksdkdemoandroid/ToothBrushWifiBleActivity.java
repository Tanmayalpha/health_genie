package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.config.CmdConfig;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.dialog.WifiDialog;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.ToothBrush.ToothBrushBleCmd;
import cn.net.aicare.modulelibrary.module.ToothBrush.ToothBrushWiFiBleUtilsData;

/**
 * Wifi+ble 牙刷
 */
public class ToothBrushWifiBleActivity extends BleBaseActivity implements View.OnClickListener, OnCallbackBle, ToothBrushWiFiBleUtilsData.BleToothBrushWiFiCallback, ToothBrushWiFiBleUtilsData.BleToothBrushCallback {
    private String TAG = ToothBrushWifiBleActivity.class.getName();
    private String mAddress;

    private List<String> mList;
    private ArrayAdapter listAdapter;

    private ToothBrushWiFiBleUtilsData mToothBrushWiFiBleUtilsData;
    private MHandler mMHandler;
    private EditText mEditText, select_gears_et;
    private String dataPaw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁止横屏
        setContentView(R.layout.activity_tooth_brush_wifi_ble);
        findViewById(R.id.wifistatus).setOnClickListener(this);
        findViewById(R.id.sn).setOnClickListener(this);
        findViewById(R.id.scan_wifi).setOnClickListener(this);
        findViewById(R.id.connect_wifi).setOnClickListener(this);
        findViewById(R.id.disconnect).setOnClickListener(this);
        findViewById(R.id.setedname).setOnClickListener(this);
        findViewById(R.id.support_unit).setOnClickListener(this);
        findViewById(R.id.default_try_out).setOnClickListener(this);
        findViewById(R.id.default_time_mode).setOnClickListener(this);
        findViewById(R.id.default_mode).setOnClickListener(this);
        findViewById(R.id.ota).setOnClickListener(this);

        mEditText = findViewById(R.id.select_wifi_et);
        select_gears_et = findViewById(R.id.select_gears_et);

        mAddress = getIntent().getStringExtra("mac");
        mList = new ArrayList<>();
        ListView listView = findViewById(R.id.log_list);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        listView.setAdapter(listAdapter);
        WeakReference weakReference = new WeakReference(new MHandler());
        mMHandler = (MHandler) weakReference.get();


    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onServiceSuccess() {

        //与服务建立连接
        mList.add(0, "服务与界面建立连接成功");
//        mList.add(0, "搜索设备");
        mMHandler.sendEmptyMessage(ToRefreUi);
        mBluetoothService.setOnCallback(this);
        mBluetoothService.scanLeDevice(30 * 1000);


    }


    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }

    @Override
    public void onStartScan() {

    }

    @Override
    public void onScanning(BleValueBean data) {
        BleLog.i(TAG, "MAC=" + mAddress + "||CID=" + data.getCid() + "||VID=" + data.getVid() + "||PID=" + data.getPid());
        if (data.getMac().equalsIgnoreCase(mAddress)) {
            mBluetoothService.connectDevice(data.getMac());
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
        mList.add(0, "蓝牙已断开");
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onServicesDiscovered(String mac) {
        mList.add(0, "蓝牙已连接");
        mMHandler.sendEmptyMessage(ToRefreUi);
        mBluetoothService.setOnCallback(this);
        BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
        if (bleDevice != null) {

            ToothBrushWiFiBleUtilsData.init(bleDevice, this, this);
            mToothBrushWiFiBleUtilsData = ToothBrushWiFiBleUtilsData.getInstance();
            mMHandler.sendEmptyMessageDelayed(ToRequestToken, 600);
            mMHandler.sendEmptyMessageDelayed(GETBATTERY, 800);
        }
    }

    @Override
    public void bleOpen() {

    }

    @Override
    public void bleClose() {

    }


    @Override
    public void OnBleAndWifiStatus(int blestatus, int wifistatus, int workstatus) {
        BleLog.e(TAG, "蓝牙状态：" + blestatus + " wifi状态：" + " 工作状态：" + workstatus);
        mList.add(0, "蓝牙状态：" + blestatus + " wifi状态：" + wifistatus + " 工作状态：" + workstatus);
        mMHandler.sendEmptyMessage(ToRefreUi);

    }

    @Override
    public void OnWifiScanStatus(int Status) {
        mList.add(0, "扫描wifi状态: " + Status);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    private HashMap<Integer, String> mHashMap = new HashMap();

    @Override
    public void OnWifiListName(int no, String name) {

        mHashMap.put(no, name);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    private HashMap<Integer, String> wifimacMap = new HashMap();

    @Override
    public void OnWifiListInfo(int no, String mac, int db, int type, int wifistatus) {

        wifimacMap.put(no, mac);
        mList.add(0, "WIFI序号: " + no + " WIFI名称：" + mHashMap.get(no) + " WIFImac: " + mac + " db: " + db + " type: " + type + " wifistata" + wifistatus);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void OnWifiCurrentConnect(String name) {
        mList.add(0, "当前连接wifi名称: " + name);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void OnWifiScanFinish(int wifiNum) {
        mList.add(0, "扫描结束 扫描的wifi个数 " + wifiNum);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    private boolean issetMac = false;

    /**
     * @param type   {@link CmdConfig#SET_WIFI_MAC}
     * @param status {@link ToothBrushBleCmd#STATUS_SUCCESS}
     */
    @Override
    public void OnSetWifiNameOrPwdOrConnectCallback(int type, int status) {
        if (type == CmdConfig.SET_WIFI_MAC) {
            mList.add(0, "获取到设置的mac地址状态 " + status);
            if (status == ToothBrushBleCmd.STATUS_SUCCESS) {
                issetMac = true;
                setPaw(dataPaw);
            }

        }
        if (type == CmdConfig.SET_WIFI_PWD) {
            mList.add(0, "获取到设置的密码状态 " + status);

            if (status == ToothBrushBleCmd.STATUS_SUCCESS && issetMac) {
                mMHandler.sendEmptyMessage(ConnectWifi);
            }
        }
        if (type == CmdConfig.DISCONNECT_WIFI) {
            mList.add(0, "发起连接 " + status);
            mMHandler.sendEmptyMessage(ToRefreUi);
        }

    }

    @Override
    public void getSelectWifiMac(String mac) {
        mList.add(0, "获取到设置的wifi的mac地址 " + mac);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void getSelectWifiPaw(String paw) {
        mList.add(0, "获取到设置的wifi的密码 " + paw);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }


    @Override
    public void getDid(long sn) {
        mList.add(0, "sn: " + sn);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (mToothBrushWiFiBleUtilsData != null)
            switch (id) {
                case R.id.wifistatus:

                    mToothBrushWiFiBleUtilsData.queryBleStatus();
                    break;
                case R.id.sn:
                    mToothBrushWiFiBleUtilsData.getDevicedid();
                    break;
                case R.id.scan_wifi:
                    mToothBrushWiFiBleUtilsData.scanWifi();
                    break;
                case R.id.connect_wifi:
                    try {
                        int selectWifi = Integer.valueOf(mEditText.getText().toString().trim());
                        if (mHashMap.get(selectWifi) != null && wifimacMap.get(selectWifi) != null) {
                            WifiDialog.newInstance().setTitle(mHashMap.get(selectWifi), wifimacMap.get(selectWifi)).setOnDialogListener(new WifiDialog.OnDialogListener() {
                                @Override
                                public void tvCancelListener(View v) {

                                }

                                @Override
                                public void tvSucceedListener(View v, String data) {

                                    if (data.equals("") || data.length() > 8) {
                                        dataPaw = data.trim();
                                        mToothBrushWiFiBleUtilsData.setWifimac(wifimacMap.get(selectWifi));
                                    } else {
                                        Toast.makeText(ToothBrushWifiBleActivity.this, "密码格式不对", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void etModifyName(EditText v) {

                                }
                            }).show(getSupportFragmentManager());
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        mMHandler.sendEmptyMessage(ConnectWifi);
                    }

                    break;

                case R.id.setedname:
                    mToothBrushWiFiBleUtilsData.getConnectWifiName();
                    break;
                case R.id.disconnect:
                    mToothBrushWiFiBleUtilsData.disconnectWifi();
                    break;
                case R.id.support_unit:
                    mToothBrushWiFiBleUtilsData.getSupportGears();
                    break;
                case R.id.default_mode:
                    String gear = select_gears_et.getText().toString().trim();
                    if (!gear.isEmpty()) {
                        String[] gears = null;
                        if (gear.contains(",")) {
                            gears = gear.split(",");
                        } else if (gear.contains("，")) {
                            gears = gear.split("，");
                        } else {
                            Toast.makeText(this, "请输入时长,档位,档位级别(数字加符号)", Toast.LENGTH_SHORT).show();
                        }
                        if (gears != null) {
                            try {
                                mToothBrushWiFiBleUtilsData.setDefault(Integer.parseInt(gears[0]), Integer.parseInt(gears[1]), Integer.parseInt(gears[2]));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "请输入时长,档位,档位级别(数字加符号)", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                    break;
                case R.id.default_try_out:
                    String gear1 = select_gears_et.getText().toString().trim();
                    if (!gear1.isEmpty()) {
                        String[] gears = null;
                        if (gear1.contains(",")) {
                            gears = gear1.split(",");
                        } else if (gear1.contains("，")) {
                            gears = gear1.split("，");
                        } else {
                            Toast.makeText(this, "请输入时长,档位,档位级别(数字加符号)", Toast.LENGTH_SHORT).show();
                        }
                        if (gears != null) {
                            try {
                                mToothBrushWiFiBleUtilsData.setTryOut(Integer.parseInt(gears[1]), Integer.parseInt(gears[2]), 0, 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(this, "请输入时长,档位,档位级别(数字加符号)", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    break;
                case R.id.default_time_mode:

                    mToothBrushWiFiBleUtilsData.getDefaultGearAndDuration();
                    break;

                case R.id.ota:

                    mToothBrushWiFiBleUtilsData.setOta();

                    break;
            }

    }

    private final int ToRefreUi = 300;
    private final int ConnectWifi = 400;
    private final int ToRequestToken = 500;
    private final int GETBATTERY = 600;


    @Override
    public void onVersion(String version) {
        mList.add(0, "版本号:" + version);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onGetSupportGears(List<Integer> staif, List<Integer> secondLevel) {
        mList.add(0, "牙刷支持的一级档位:" + Arrays.toString(staif.toArray()) + " 二级档位:" + Arrays.toString(secondLevel.toArray()));
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onGetBattery(int batteryStatus, int batteryQuantity) {
        mList.add(0, "电池状态:" + batteryStatus + " 电量:" + batteryQuantity);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onGetDefaultGearAndDuration(int time, int gear, int gearFrom) {
        mList.add(0, "获得到默认的刷牙档位和时长:" + time + " 档位:" + gear + " 档位级别" + gearFrom);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onGetTokenResult(int result) {
        String s = "";
        if (result == ToothBrushBleCmd.NO_TOKEN) {
            s = "没有";
        } else if (result == ToothBrushBleCmd.HAS_TOKEN) {
            s = "已经授权";
        } else if (result == ToothBrushBleCmd.WITHOUT_TOKEN) {
            s = "不需要授权";
        } else if (result == ToothBrushBleCmd.SUCCESSTOKEN) {
            s = "授权成功";
        }
        mList.add(0, "请求授权结果" + result + "  " + s);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onGetManualParameter(int time, int hz, int duty) {
        mList.add(0, " 获取手动档位的参数: 时长" + time + "  频率" + hz + "   占空比" + duty);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onSetDefaultModeAndManualModeResult(byte type, int result) {
        String s = "";
        if (result == 0) {
            s = "设置成功";
        } else if (result == 1) {
            s = "设置失败";
        } else if (result == 2) {
            s = "不支持设置";
        }
        if (type == ToothBrushBleCmd.SET_TOOTHBRUSH_TIME_GEARS) {
            mList.add(0, " 设置默认刷牙时长和工作档位: 结果" + result + "  " + s);
            mMHandler.sendEmptyMessage(ToRefreUi);
        } else {
            mList.add(0, "  设置手动设置（自定义）档位: 结果" + result + "  " + s);
            mMHandler.sendEmptyMessage(ToRefreUi);
        }
    }

    @Override
    public void onTestFinish(int totalTime, int leftTime, int rightTime, int mode, int battery) {
        mList.add(0, "刷牙完成: 总时长:" + totalTime + " 左时长:" + leftTime + " 右时长:" + rightTime + " 模式:" + mode + " 电量:" + battery);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onTryOutResult(int result) {
        mList.add(0, "设置使用结果:" + result + " ( 0：设置成功 1：设置失败，原因未知 2：不支持设置)");
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onTwoLevelModeDefault(int mode) {
        mList.add(0, "获取二级档位默认值:" + mode);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }


    @Override
    public void onOTA(int status) {
        String s = " ";
        switch (status) {
            case 0x00:
                s = "wifiOTA 成功";
                break;
            case 0x01:
                s = "wifiOTA 失败";
                break;
            case 0x02:
                s = "不支持 wifiOTA";
                break;
            case 0x03:
                s = "模块主动开始 wifiOTA（MCU 收到该指 令后不能断电，需要等待 OTA 成功或者失败）";
                break;
        }
        mList.add(0, s);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onShowData(String data) {

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
                case ConnectWifi:
                    mToothBrushWiFiBleUtilsData.connectWifi();
                    break;
                case ToRequestToken:
                    mList.add(0, "请求授权");
                    mToothBrushWiFiBleUtilsData.requestToken(System.currentTimeMillis());
                    if (listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                    }
                    break;
                case GETBATTERY:
                    mToothBrushWiFiBleUtilsData.getBattery();
                    break;
            }
        }
    }


    /**
     * wifi密码一次只能传14个byte
     * 如果密码长度超过14个byte 就需要分包传送
     * subpackage 为0 时，表示后面还有数据
     * subpackage 为1 时，表示数据小于或等于14个byte,后面没有数据
     *
     * @param paw
     */
    private void setPaw(String paw) {
        if (paw.isEmpty()) {
            byte[] bytes = new byte[0];
            mToothBrushWiFiBleUtilsData.setWifiPwd(0, bytes);
        } else {
            byte[] password = BleStrUtils.stringToBytes(paw);
            if (password != null) {
                if (password.length < 14)
                    mToothBrushWiFiBleUtilsData.setWifiPwd(0, password);

                else {
                    boolean isend = false;
                    int i = 0;
                    byte[] byte1 = password;
                    while (!isend) {
                        if (byte1.length > 14) {
                            byte[] bytes = new byte[14];
                            System.arraycopy(password, i, bytes, 0, bytes.length);

                            mToothBrushWiFiBleUtilsData.setWifiPwd(1, bytes);
                            i = i + 14;
                            byte1 = Arrays.copyOf(password, password.length - i);
                        } else {
                            isend = true;
                            byte[] bytes = new byte[password.length - i];
                            System.arraycopy(password, i, bytes, 0, bytes.length);
                            mToothBrushWiFiBleUtilsData.setWifiPwd(1, bytes);

                        }

                    }


                }
            }
        }
    }

}

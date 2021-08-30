package aicare.net.cn.sdk.ailinksdkdemoandroid;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.elinkthings.bleotalibrary.listener.OnBleOTAListener;
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
import aicare.net.cn.sdk.ailinksdkdemoandroid.config.BleDeviceConfig;
import aicare.net.cn.sdk.ailinksdkdemoandroid.dialog.WifiDialog;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.BodyFatScale.AppHistoryRecordBean;
import cn.net.aicare.modulelibrary.module.BodyFatScale.BodyFatBleUtilsData;
import cn.net.aicare.modulelibrary.module.BodyFatScale.BodyFatDataUtil;
import cn.net.aicare.modulelibrary.module.BodyFatScale.BodyFatRecord;
import cn.net.aicare.modulelibrary.module.BodyFatScale.McuHistoryRecordBean;

/**
 * wifi+ble体脂秤
 */
public class WeightScaleWifiBleActivity extends BleBaseActivity implements View.OnClickListener, OnCallbackBle, BodyFatBleUtilsData.BleBodyFatCallback, BodyFatBleUtilsData.BleBodyFatWiFiCallback {
    private String TAG = WeightScaleWifiBleActivity.class.getName();
    private String mAddress;
    private List<String> mList;
    private ArrayAdapter listAdapter;
    //    private Button wifiStatus_btn;
    private BodyFatBleUtilsData bodyFatBleUtilsData;
    private MHandler mMHandler;
    private EditText mEditText;
    private RadioButton kg, jing, stlb, lb;
    private byte[] testIp = new byte[]{0x74,
            0x65, 0x73, 0x74, 0x2e,
            0x61, 0x69, 0x6c, 0x69,
            0x6e, 0x6b, 0x2e, 0x72,
            0x65, 0x76, 0x69, 0x63,
            0x65, 0x2e, 0x61, 0x69,
            0x63, 0x61, 0x72, 0x65,
            0x2e, 0x6e, 0x65, 0x74,
            0x2e, 0x63, 0x6e};

    private byte[] productIp = new byte[]{
            0x61, 0x69, 0x6c, 0x69, 0x6e, 0x6b, 0x2e,
            0x69, 0x6f, 0x74, 0x2e, 0x61, 0x69, 0x63,
            0x61, 0x72, 0x65, 0x2e, 0x6e, 0x65, 0x74,
            0x2e, 0x63, 0x6e};

    private byte[] IpUrl = new byte[]{
            0x2f, 0x64, 0x65, 0x76, 0x69,
            0x76, 0x63, 0x64, 0x2f, 0x73,
            0x65, 0x72, 0x76, 0x64, 0x72,
            0x52, 0x65, 0x64, 0x69, 0x72,
            0x65, 0x63, 0x74, 0x2f};
    private boolean isTest = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁止横屏
        setContentView(R.layout.activity_weight_scale_wifi_ble);
        findViewById(R.id.wifistatus).setOnClickListener(this);
        findViewById(R.id.sn).setOnClickListener(this);
        findViewById(R.id.scan_wifi).setOnClickListener(this);
        findViewById(R.id.connect_wifi).setOnClickListener(this);
        findViewById(R.id.disconnect).setOnClickListener(this);
        findViewById(R.id.setedname).setOnClickListener(this);
        findViewById(R.id.setedpaw).setOnClickListener(this);
        findViewById(R.id.setedmac).setOnClickListener(this);
        findViewById(R.id.ota).setOnClickListener(this);
        findViewById(R.id.surroundings).setOnClickListener(this);
        mEditText = findViewById(R.id.select_wifi_et);
        kg = findViewById(R.id.kg);
        jing = findViewById(R.id.jin);
        stlb = findViewById(R.id.st_lb);
        lb = findViewById(R.id.lb);
        kg.setChecked(true);
        mAddress = getIntent().getStringExtra("mac");
        mList = new ArrayList<>();
        ListView listView = findViewById(R.id.log_list);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        listView.setAdapter(listAdapter);
        WeakReference weakReference = new WeakReference(new MHandler());
        mMHandler = (MHandler) weakReference.get();
        kg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().setWeightUnit(BodyFatDataUtil.KG, BleDeviceConfig.WEIGHT_BODY_FAT_SCALE_WIFI_BLE));
                }
            }
        });
        jing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().setWeightUnit(BodyFatDataUtil.JIN, BleDeviceConfig.WEIGHT_BODY_FAT_SCALE_WIFI_BLE));
                }
            }
        });
        stlb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().setWeightUnit(BodyFatDataUtil.ST, BleDeviceConfig.WEIGHT_BODY_FAT_SCALE_WIFI_BLE));
                }
            }
        });
        lb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().setWeightUnit(BodyFatDataUtil.LB, BleDeviceConfig.WEIGHT_BODY_FAT_SCALE_WIFI_BLE));
                }
            }
        });

    }


    @Override
    public void onServiceSuccess() {
        BleLog.i(TAG, "服务与界面建立连接成功");
        //与服务建立连接
        if (mBluetoothService != null) {
            mBluetoothService.setOnCallback(this);
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                BodyFatBleUtilsData.init(bleDevice, this, this);
                bodyFatBleUtilsData = BodyFatBleUtilsData.getInstance();
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
    public void onStartScan() {

    }

    @Override
    public void onScanning(BleValueBean data) {

    }

    @Override
    public void onScanTimeOut() {

    }

    @Override
    public void onConnecting(String mac) {

    }

    @Override
    public void onDisConnected(String mac, int code) {

    }

    @Override
    public void onServicesDiscovered(String mac) {

    }

    @Override
    public void bleOpen() {

    }

    @Override
    public void bleClose() {

    }

    @Override
    public void onWeightData(int status, float weight, int weightUnit, int decimals) {
        mList.add(0, "体重数据类型：" + status + " 体重: " + weight + " 单位：" + weightUnit + " 小数点位: " + decimals);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onStatus(int status) {

        switch (status) {
            case BodyFatDataUtil.WEIGHT_TESTING:
                mList.add(0, "测量状态：" + status + " 测量实时体重");
                break;
            case BodyFatDataUtil.WEIGHT_RESULT:
                mList.add(0, "测量状态：" + status + " 稳定体重");
                break;
            case BodyFatDataUtil.IMPEDANCE_TESTING:
                mList.add(0, "测量状态：" + status + " 阻抗测量中");
                break;
            case BodyFatDataUtil.IMPEDANCE_SUCCESS_DATA:
            case BodyFatDataUtil.IMPEDANCE_SUCCESS:
                mList.add(0, "测量状态：" + status + " 阻抗测量成功");
                break;
            case BodyFatDataUtil.IMPEDANCE_FAIL:
                mList.add(0, "测量状态：" + status + " 阻抗测量失败");
                break;
            case BodyFatDataUtil.HEART_TESTING:
                mList.add(0, "测量状态：" + status + " 心率测量中");
                break;
            case BodyFatDataUtil.HEART_SUCCESS:
                mList.add(0, "测量状态：" + status + " 心率测量成功");
                break;
            case BodyFatDataUtil.HEART_FAIL:
                mList.add(0, "测量状态：" + status + " 心率测量失败");
                break;
            case BodyFatDataUtil.TEST_FINISH:
                mList.add(0, "测量状态：" + status + " 测量完成");
                break;
            case BodyFatDataUtil.MUC_REQUEST_USER_INFO:
                mList.add(0, "测量状态：" + status + "请求用户信息");
                break;
            default:
                mList.add(0, "测量状态：" + status);

        }
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onOtaCallback(int status) {
        switch (status) {
            case 0x00:
                mList.add(0, "ota状态：" + status + " wifiOTA成功");
                break;
            case 0x01:
                mList.add(0, "ota状态：" + status + " wifiOTA失败");
                break;
            case 0x02:
                mList.add(0, "ota状态：" + status + " 不支持wifiOTA");
                break;
            case 0x03:
                mList.add(0, "ota状态：" + status + " 模块主动开始wifiOTA（MCU收到该指令后不能断电，需要等待OTA成功或者失败）");
                break;

        }
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onAdc(int adc, int algorithmic) {
        mList.add(0, "阻抗：" + adc + " 算法位：" + algorithmic);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onHeartRate(int heartrate) {
        mList.add(0, "心率：" + heartrate);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onBodyFat(BodyFatRecord bodyFatRecord) {
        mList.add(0, "体脂数据：" + bodyFatRecord.toString());
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onError(int code) {
        mList.add(0, "历史记录Mcu：" + code);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onHistoryMcu(McuHistoryRecordBean mcuHistoryRecordBean) {
        mList.add(0, "历史记录Mcu：" + mcuHistoryRecordBean.toString());
        mMHandler.sendEmptyMessage(ToRefreUi);
//        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onHistoryApp(AppHistoryRecordBean appHistoryRecordBean) {
        mList.add(0, "历史记录app：" + appHistoryRecordBean.toString());
        mMHandler.sendEmptyMessage(ToRefreUi);

    }

    @Override
    public void onVersion(String version) {
        mList.add(0, "版本号：" + version);
        mMHandler.sendEmptyMessage(ToRefreUi);

    }

    @Override
    public void onMcuBatteryStatus(int status, int battery) {
        mList.add(0, "电量状态" + status + " 电量：" + battery);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onSysTime(int status, int[] times) {
        mList.add(0, "时间状态" + status);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void requestSynTime() {
        mList.add(0, "同步时间");
        bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().synTime());
    }

    @Override
    public void setTimeCallback(int type, int status) {
        String msg = "";
        if (type == CmdConfig.SET_SYS_TIME) {
            msg = "设置系统当前时间：";
        } else if (type == CmdConfig.SET_DEVICE_TIME) {
            msg = "同步时间";
        }
        if (status == BodyFatDataUtil.STATUS_SUCCESS) {
            msg = msg + status + " 成功";
        } else if (status == BodyFatDataUtil.STATUS_FAIL) {
            msg = msg + status + " 失败";
        } else if (status == BodyFatDataUtil.STATUS_NOSUPORT) {
            msg = msg + status + " 不支持";
        }
        mList.add(0, msg);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void requestSynHistoryCallback(int status) {
        if (status == 0) {
            mList.add(0, "" + status + " 无历史记录");
        } else if (status == 1) {
            mList.add(0, "请求历史记录" + status + " 开始发送历史记录");
        } else {
            mList.add(0, "请求历史记录" + status + " 发送历史记录结束");
        }
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void updateUserCallback(int status) {
        if (status == 0) {
            mList.add(0, "更新用户或列表回调" + status + " 更新列表成功");
        } else if (status == 1) {
            mList.add(0, "更新用户或列表回调" + status + " 更新个人用户成功");
        } else if (status == 2) {
            mList.add(0, "更新用户或列表回调" + status + " 更新列表失败");
        } else {
            mList.add(0, "更新用户或列表回调" + status + " 更新个人用户失败");
        }
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void setUnitCallback(int status) {
        if (status == 0) {
            mList.add(0, "下发单位回调" + status + " 成功");
        } else if (status == 1) {
            mList.add(0, "下发单位回调" + status + " 失败");
        } else {
            mList.add(0, "下发单位回调" + status + " 不支持");
        }
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void requestUserData(int status) {
        if (status == 0x01) {
            mList.add(0, "下发用户信息 " + status);
        } else if (status == 0x03) {
            mList.add(0, "下发用户信息成功 " + status);
        } else {
            mList.add(0, "下发用户信息失败 " + status);
        }
        mMHandler.sendEmptyMessage(ToRefreUi);
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

//        mList.add(0,"WIFI序号: "+no+" WIFI名称: "+name);
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
     * @param type
     * @param status {@link BodyFatDataUtil#STATUS_SUCCESS}
     */
    @Override
    public void OnSetWifiNameOrPwdOrConnectCallback(int type, int status) {
        if (type == CmdConfig.SET_WIFI_MAC) {
            mList.add(0, "获取到设置的mac地址状态 " + status);
            if (status == BodyFatDataUtil.STATUS_SUCCESS) {
                issetMac = true;
            } else {

            }

        }
        if (type == CmdConfig.SET_WIFI_PAW) {
            mList.add(0, "获取到设置的密码状态 " + status);

            if (status == BodyFatDataUtil.STATUS_SUCCESS && issetMac) {
                mMHandler.sendEmptyMessage(ConnectWifi);
            }
        }
        if (type == CmdConfig.DIS_OR_CON_WIFI) {
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
    public void onSetIpStatus(int status) {
        if (status == 0) {
            if (isTest) {
                mList.add(0, "设置环境IP为生产环境成功");
                mList.add(0, "设置环境路径为生产环境");
            } else {
                mList.add(0, "设置环境IP为测试环境成功");
                mList.add(0, "设置环境路径为测试环境");
            }
            setIpUrl(IpUrl);
        } else {
            mList.add(0, "设置环境IP失败");
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSetPortStatus(int status) {

    }

    @Override
    public void onSetIpUrlStatus(int status) {
        if (status == 0) {
            if (isTest) {
                mList.add(0, "设置环境路径为生产环境成功");
                isTest = false;

            } else {
                mList.add(0, "设置环境路径为测试环境成功");
                isTest = true;

            }
        } else {
            mList.add(0, "设置环境路径失败");
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onIpData(String ip) {

    }

    @Override
    public void onPortData(int port) {

    }

    @Override
    public void onUrlData(String url) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.wifistatus:
                bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().queryBleStatus());
                break;
            case R.id.sn:
                bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().getSnDeviceDid());
                break;
            case R.id.scan_wifi:
                bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().scanWifi());
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
                                bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().setWifiMac(wifimacMap.get(selectWifi)));
                                if (data.equals("") || data.length() > 8) {
                                    setPaw(data);
                                } else {
                                    Toast.makeText(WeightScaleWifiBleActivity.this, "密码格式不对", Toast.LENGTH_SHORT).show();
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
            case R.id.setedmac:
                bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().getSelectWifiMac());
                break;
            case R.id.setedpaw:
                bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().getSelectWifiPwd());
                break;
            case R.id.setedname:
                bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().getConnectWifiName());
                break;
            case R.id.disconnect:
                bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().disconnectWifi());
                break;
            case R.id.ota:
//                bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().ota());
//                OtaUtil otaUtil=new OtaUtil(this,mBluetoothService.getBleDevice(mAddress));
                showFileChooser();
                break;
            case R.id.surroundings:
                if (isTest) {
                    setIp(productIp);
                    mList.add(0, "设置环境IP为生产环境");

                } else {
                    setIp(testIp);
                    mList.add(0, "设置环境IP为测试环境");


                }
                listAdapter.notifyDataSetChanged();

                break;


        }

    }


    private void setIp(byte[] ips) {
        if (ips.length <= 14)
            bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().environmentIp(0, ips));
        else {
            boolean isend = false;
            int i = 0;
            byte[] byte1 = ips;
            while (!isend) {
                if (byte1.length > 14) {
                    byte[] bytes = new byte[14];
                    System.arraycopy(ips, i, bytes, 0, bytes.length);
                    bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().environmentIp(1, bytes));
                    i = i + 14;
                    byte1 = Arrays.copyOf(ips, ips.length - i);
                } else {
                    isend = true;
                    byte[] bytes = new byte[ips.length - i];
                    System.arraycopy(ips, i, bytes, 0, bytes.length);
                    bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().environmentIp(0, bytes));
                }

            }
        }

    }

    private void setIpUrl(byte[] setIpUrl) {
        if (setIpUrl.length <= 14)
            bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().environmentUrl(0, setIpUrl));
        else {
            boolean isend = false;
            int i = 0;
            byte[] byte1 = setIpUrl;
            while (!isend) {
                if (byte1.length > 14) {
                    byte[] bytes = new byte[14];
                    System.arraycopy(setIpUrl, i, bytes, 0, bytes.length);
                    bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().environmentUrl(1, bytes));
                    i = i + 14;
                    byte1 = Arrays.copyOf(setIpUrl, setIpUrl.length - i);
                } else {
                    isend = true;
                    byte[] bytes = new byte[setIpUrl.length - i];
                    System.arraycopy(setIpUrl, i, bytes, 0, bytes.length);
                    bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().environmentUrl(0, bytes));
                }

            }
        }

    }

    private final int ToRefreUi = 300;
    private final int ConnectWifi = 400;

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
                    bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().connectWifi());
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
            bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().setWifiPwd(0, bytes));
        } else {
            byte[] password = BleStrUtils.stringToBytes(paw);
            if (password != null) {
                if (password.length < 14)
                    bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().setWifiPwd(0, password));
                else {
                    boolean isend = false;
                    int i = 0;
                    byte[] byte1 = password;
                    while (!isend) {
                        if (byte1.length > 14) {
                            byte[] bytes = new byte[14];
                            System.arraycopy(password, i, bytes, 0, bytes.length);

                            bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().setWifiPwd(1, bytes));
                            i = i + 14;
                            byte1 = Arrays.copyOf(password, password.length - i);
                        } else {
                            isend = true;
                            byte[] bytes = new byte[password.length - i];
                            System.arraycopy(password, i, bytes, 0, bytes.length);

                            bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().setWifiPwd(0, bytes));
                        }

                    }


                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String path = uri.getPath();
            mList.add(0, "ota准备就绪，请勿操作");
            listAdapter.notifyDataSetChanged();
            bodyFatBleUtilsData.initOtaUtil(this, uri, new OnBleOTAListener() {
                @Override
                public void onOtaSuccess() {
                    mList.add(0, "ota成功");
                    listAdapter.notifyDataSetChanged();

                }

                @Override
                public void onOtaFailure(int cmd, String err) {
                    mList.add(0, "失败");
                    listAdapter.notifyDataSetChanged();
                }

                @Override
                public void onOtaProgress(float progress, int currentCount, int maxCount) {
                    mList.add(0, "otaProgress:"+progress);
                    listAdapter.notifyDataSetChanged();
                }
            });
        } else {

        }
    }
    private static final int FILE_SELECT_CODE = 0x1002;
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

}
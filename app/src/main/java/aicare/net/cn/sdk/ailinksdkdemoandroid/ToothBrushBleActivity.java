package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.pingwang.bluetoothlib.utils.BleLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.config.BleDeviceConfig;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.ToothBrush.ToothBrushBleCmd;

/**
 * ble 牙刷
 */
public class ToothBrushBleActivity extends BleBaseActivity implements View.OnClickListener, OnCallbackBle,ToothBrushBleUtilsData.BleToothBrushCallback {
    private String TAG = ToothBrushBleActivity.class.getName();
    private String mAddress;
    private int type;
    private List<String> mList;
    private ArrayAdapter listAdapter;

    private MHandler mMHandler;
    private EditText select_gears_et;

    private ToothBrushBleUtilsData mToothBrushBleUtilsData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁止横屏
        setContentView(R.layout.activity_tooth_brush_ble);

        findViewById(R.id.support_unit).setOnClickListener(this);
        findViewById(R.id.default_try_out).setOnClickListener(this);
        findViewById(R.id.default_time_mode).setOnClickListener(this);
        findViewById(R.id.default_mode).setOnClickListener(this);


        select_gears_et = findViewById(R.id.select_gears_et);

        mAddress = getIntent().getStringExtra("mac");
        type = getIntent().getIntExtra("type", BleDeviceConfig.TOOTHBRUSH_TEST);
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
            mBluetoothService.stopScan();
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
        stopScanBle();
        BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
        if (bleDevice != null) {
            ToothBrushBleUtilsData.init(bleDevice,this);
            mToothBrushBleUtilsData =ToothBrushBleUtilsData.getInstance();

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
    public void onClick(View v) {
        int id = v.getId();
        if (mToothBrushBleUtilsData != null)
            switch (id) {

                case R.id.support_unit:
                    mToothBrushBleUtilsData.getSupportGears();
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
                                mToothBrushBleUtilsData.setDefault(Integer.parseInt(gears[0]), Integer.parseInt(gears[1]), Integer.parseInt(gears[2]));
                            } catch (Exception e) {
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
                                mToothBrushBleUtilsData.setTryOut(Integer.parseInt(gears[1]), Integer.parseInt(gears[2]), 0, 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(this, "请输入时长,档位,档位级别(数字加符号)", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    break;
                case R.id.default_time_mode:
                    mToothBrushBleUtilsData.getdefaultGearAndDuration();
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
    public void onShowData(String data) {
        Log.e("蓝牙牙刷",data);
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

                case ToRequestToken:
                    mList.add(0, "请求授权");
                    mToothBrushBleUtilsData.requestToken(System.currentTimeMillis());
                    if (listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                    }
                    break;
                case GETBATTERY:
                    mToothBrushBleUtilsData.getBattery();
                    break;
            }
        }
    }



}

package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.CallbackDisIm;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.pingwang.bluetoothlib.listener.OnScanFilterListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleAppBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.config.BleDeviceConfig;
import aicare.net.cn.sdk.ailinksdkdemoandroid.dialog.LoadingIosDialogFragment;
import aicare.net.cn.sdk.ailinksdkdemoandroid.find.FindDeviceNewActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.coffee_scale.CoffeeScaleActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.food_temp.FoodTempActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.share_charger.ShareChargerActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.share_condom.ShareCondomActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.share_socket.ShareSocketActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.CheckPermissionUtils;
import androidx.annotation.NonNull;
import cn.net.aicare.modulelibrary.module.RopeSkipping.RopeSkippingBleData;
import cn.net.aicare.modulelibrary.module.scooter.SkateboardBleConfig;


/**
 * xing<br>
 * 2019/3/6<br>
 * java类作用描述
 */
public class ShowBleActivity extends BleAppBaseActivity implements OnCallbackBle, OnScanFilterListener {


    private static String TAG = ShowBleActivity.class.getName();

    private final int BIND_SERVER_OK = 1;
    private final int BIND_SERVER_ERR = 2;
    private final int REFRESH_DATA = 3;
    private List<String> mList;
    private ArrayAdapter listAdapter;
    private int mType;
    private String mNoEncryptionMac = "";
    private String mFilterName = "";
    private String mFilterMac = "";
    private int mCid;
    private int mVid;
    private int mPid;

    @Override
    protected void uiHandlerMessage(Message msg) {
        switch (msg.what) {

            case BIND_SERVER_OK:

                break;

            case REFRESH_DATA:
                listAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_ble;
    }

    @Override
    protected void initData() {
        mType = getIntent().getIntExtra("type", 0);
        if (0 == mType) {
            finish();
        }
    }

    @Override
    protected void initView() {
        mList = new ArrayList<>();
        ListView listView = findViewById(R.id.listview);
        Button btn = findViewById(R.id.btn);
        Button btn1 = findViewById(R.id.btn1);
        Button clear = findViewById(R.id.clear);
        EditText et_filter_name = findViewById(R.id.et_filter_name);
        EditText et_filter_mac = findViewById(R.id.et_filter_mac);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothService != null) {
                    BleLog.i(TAG, "搜索设备");
                    mFilterName = et_filter_name.getText().toString().trim();
                    mFilterMac = et_filter_mac.getText().toString().trim();
                    if (mType == BleDeviceConfig.SMART_SCOOTER) {
                        Map<String, String> map = new HashMap<>();
                        map.put(SkateboardBleConfig.UUID_BROADCAST.toString(), "37,3,1");//37=0x0025=电滑板的cid
                        mBluetoothService.scanLeDevice(0, map, BleConfig.UUID_SERVER_AILINK, SkateboardBleConfig.UUID_BROADCAST);
                    } else {
                        //0000FEE7=手表
                        mBluetoothService.scanLeDevice(0, BleConfig.UUID_SERVER_AILINK, UUID.fromString("0000FEE7-0000-1000-8000-00805F9B34FB"), SkateboardBleConfig.UUID_BROADCAST);
                    }
                    mList.clear();
                    listAdapter.notifyDataSetChanged();
                }
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothService != null) {
                    mBluetoothService.stopScan();
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothService != null) {
                    mList.clear();
                    listAdapter.notifyDataSetChanged();
                }
            }
        });

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemStr = mList.get(position);
                String mac = itemStr.split("=")[0];
                String cidVidPid = itemStr.split("=")[2];
                mCid = Integer.parseInt(cidVidPid.split(";")[0]);
                mVid = Integer.parseInt(cidVidPid.split(";")[1]);
                mPid = Integer.parseInt(cidVidPid.split(";")[2]);
                if (mType == BleDeviceConfig.CLEAR_SHAKE_HANDS) {
                    //验证不握手,不加密的界面使用
                    BleConfig.setHandshakeStatus(mac, false);
                    if (mBluetoothService != null) {
                        mBluetoothService.stopScan();
                        mBluetoothService.connectDevice(mac);
                        showLoading();
                    }
                } else if (BleDeviceConfig.TOOTHBRUSH_WIFI_BLE == mType) {
                    mBluetoothService.stopScan();
                    Intent intent = new Intent();
                    intent.setClass(ShowBleActivity.this, ToothBrushWifiBleActivity.class);
                    intent.putExtra("type", mCid);
                    intent.putExtra("mac", mac);
                    startActivity(intent);
                    finish();

                } else if (BleDeviceConfig.TOOTHBRUSH_BLE == mType) {
                    mBluetoothService.stopScan();
                    Intent intent = new Intent();
                    intent.setClass(ShowBleActivity.this, ToothBrushBleActivity.class);
                    intent.putExtra("type", mCid);
                    intent.putExtra("mac", mac);
                    startActivity(intent);
                    finish();
                } else {
                    if (mCid == BleDeviceConfig.BLE_BOOLD_OXYGEN && mVid == 0x0012) {
                        //vid=12的不用握手校验,不加密
                        mNoEncryptionMac = mac;
                        BleConfig.setHandshakeStatus(mac, false);
                    } else if (mCid == BleDeviceConfig.SMART_SCOOTER) {
                        if (mPid == 0x0001) {
                            //电滑板pid=01的不用握手校验,不加密
                            BleConfig.setHandshakeStatus(mac, false);
                        }
                        mNoEncryptionMac = mac;
                    } else if (mCid == 0) {
                        //CID=0不需要握手
                        BleConfig.setHandshakeStatus(mac, false);
                    } else if (mCid == BleDeviceConfig.ROPE_SKIPPING) {
                        //要加密要握手。别人家的东西
                        if (mVid == 0x0027 && mPid == 0x0001) {
                            BleConfig.setHandshakeStatus(RopeSkippingBleData.LongXiang, mac, true);
                        }
                    }
                    if (mBluetoothService != null) {
                        mBluetoothService.stopScan();
                        mBluetoothService.connectDevice(mac);
                        showLoading();
                    }
                }
            }
        });


        // 延迟后请求权限
        btn.postDelayed(() -> {
            if (isDestroyed()) {
                return;
            }

            checkPermission();
        }, 500);
    }


    @Override
    protected void initListener() {

    }


    //---------------------------------服务---------------------------------------------------

    @Override
    public void onServiceSuccess() {
        if (mBluetoothService!=null) {
            mBluetoothService.setOnCallback(ShowBleActivity.this);
            mBluetoothService.setOnScanFilterListener(ShowBleActivity.this);
            mHandler.sendEmptyMessage(BIND_SERVER_OK);
        }
        CallbackDisIm.getInstance().addListListener(this);
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {
        CallbackDisIm.getInstance().removeListener(this);
    }


    @Override
    public void onStartScan() {

    }

    @Override
    public void onScanning(@NonNull BleValueBean data) {
        String mAddress = data.getMac();
        BleLog.i(TAG, "MAC=" + mAddress + "||CID=" + data.getCid() + "||VID=" + data.getVid() + "||PID=" + data.getPid());
        String dataStr = mAddress + "=" + data.getName() + "=" + data.getCid() + ";" + data.getVid() + ";" + data.getPid();
        if (!mList.contains(dataStr)) {
            String data1 = BleStrUtils.byte2HexStr(data.getScanRecord());
            String data2 = BleStrUtils.byte2HexStr(data.getManufacturerData());
            BleLog.i(TAG, "设备地址+广播数据:" + mAddress + "||" + data1 + "||" + data2);
            mList.add(dataStr);
            listAdapter.notifyDataSetChanged();
        }


    }


    @Override
    public void onConnecting(@NonNull String mac) {

    }

    @Override
    public void onDisConnected(@NonNull String mac, int code) {
        dismissLoading();
        Toast.makeText(mContext, "连接断开:" + code, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onServicesDiscovered(@NonNull String mac) {
        if (mac.equalsIgnoreCase(mNoEncryptionMac)) {
            BleDevice bleDevice = mBluetoothService.getBleDevice(mac);
            bleDevice.setA7Encryption(false);
            mNoEncryptionMac = "";

        }
        dismissLoading();
        Intent intent = new Intent();
        int type = mType;//默认婴儿秤
        switch (type) {
            case BleDeviceConfig.BABY_SCALE:
                intent.setClass(ShowBleActivity.this, BabyCmdActivity.class);
                break;
            case BleDeviceConfig.INFRARED_THERMOMETER:
                intent.setClass(ShowBleActivity.this, TempGunCmdActivity.class);
                break;
            case BleDeviceConfig.BLOOD_PRESSURE:
                intent.setClass(ShowBleActivity.this, SphyCmdActivity.class);
                break;
            case BleDeviceConfig.THERMOMETER:
                intent.setClass(ShowBleActivity.this, TempCmdActivity.class);
                break;
            case BleDeviceConfig.HEIGHT_METER:
                intent.setClass(ShowBleActivity.this, HeightCmdActivity.class);
                break;
            case BleDeviceConfig.WEIGHT_BODY_FAT_SCALE:
                intent.setClass(ShowBleActivity.this, WeightScaleBleActivity.class);
                break;

            case BleDeviceConfig.WEIGHT_BODY_FAT_SCALE_AD:
                intent.setClass(ShowBleActivity.this, ADWeightScaleCmdActivity.class);
                break;
            case BleDeviceConfig.WEIGHT_BODY_FAT_SCALE_WIFI_BLE:
                intent.setClass(ShowBleActivity.this, WeightScaleWifiBleActivity.class);
                break;
            case BleDeviceConfig.TOOTHBRUSH_WIFI_BLE:

                intent.setClass(ShowBleActivity.this, ToothBrushWifiBleActivity.class);
                break;
            case BleDeviceConfig.TOOTHBRUSH_BLE:
                intent.setClass(ShowBleActivity.this, ToothBrushBleActivity.class);
                break;
            case BleDeviceConfig.EIGHT_BODY_FAT_SCALE:
                intent.setClass(ShowBleActivity.this, EightBodyfatActivity.class);
                break;
            case BleDeviceConfig.BLOOD_GLUCOSE:
                intent.setClass(ShowBleActivity.this, BloodGlucoseActivity.class);
                break;
            case BleDeviceConfig.BABY_BODY_FAT:
                intent.setClass(ShowBleActivity.this, BabyBodyFatCmdActivity.class);
                break;
            case BleDeviceConfig.SMART_MASK:
                intent.setClass(ShowBleActivity.this, SmartMaskActivity.class);
                break;
            case BleDeviceConfig.SMART_SCOOTER:
                intent.setClass(ShowBleActivity.this, SkateboardDataActivity.class);
                break;
            case BleDeviceConfig.BLE_BOOLD_OXYGEN:
                intent.setClass(ShowBleActivity.this, BloodOxygenActivity.class);
                break;
            case BleDeviceConfig.COFFEE_SCALE:
                // 咖啡秤
                intent.setClass(ShowBleActivity.this, CoffeeScaleActivity.class);
                break;
            case BleDeviceConfig.SHARE_CHARGER:
                // 共享充电器
                intent.setClass(ShowBleActivity.this, ShareChargerActivity.class);
                break;
            case BleDeviceConfig.SHARE_SOCKET:
                // 共享插座
                intent.setClass(ShowBleActivity.this, ShareSocketActivity.class);
                break;
            case BleDeviceConfig.SHARE_CONDOM:
                // 共享套套机
                intent.setClass(ShowBleActivity.this, ShareCondomActivity.class);
                break;
            case BleDeviceConfig.FIND_DEVICE:
                // 寻物器
//                intent.setClass(ShowBleActivity.this, FindDeviceActivity.class);
                intent.setClass(ShowBleActivity.this, FindDeviceNewActivity.class);
                BleConfig.setHandshakeStatus(mac, false);
                break;
            case BleDeviceConfig.FOOD_TEMP:
                // 食品温度计
                intent.setClass(ShowBleActivity.this, FoodTempActivity.class);
                break;
            case BleDeviceConfig.HEIGHT_BODY_FAT:
                intent.setClass(ShowBleActivity.this, HeightWeightScaleActivity.class);
                break;
            case BleDeviceConfig.TEMP_Humidity:
                intent.setClass(ShowBleActivity.this, TempHumidityActivity.class);
                break;
            case BleDeviceConfig.ROPE_SKIPPING:
                intent.setClass(ShowBleActivity.this, RopeSkippingActivity.class);
                break;
//            case BleDeviceConfig.TOOTHBRUSH_TEST:
//                // 牙刷测试
//                intent.setClass(ShowBleActivity.this, ToothbrushTestActivity.class);
//                break;

            case BleDeviceConfig.BLE_NUTRITION_SCALE:
                // 蓝牙营养秤
//                intent.setClass(ShowBleActivity.this, BleNutritionActivity.class);
                break;
//            case BleDeviceConfig.BLD_WEIGHT:
//                intent.setClass(ShowBleActivity.this, BLDWeightScaleBle.class);
//                break;
            case -1:
                intent.setClass(ShowBleActivity.this, BleCmdActivity.class);
                break;
            case -2:
                intent.setClass(ShowBleActivity.this, TestCmdActivity.class);
                break;
            case -3:
                intent.setClass(ShowBleActivity.this, TestOtaActivity.class);
                break;
            case -4:
                intent.setClass(ShowBleActivity.this, TransmissionActivity.class);
                break;
            case BleDeviceConfig.CLEAR_SHAKE_HANDS:
                //验证不握手不加密的界面
                intent.setClass(ShowBleActivity.this, ClearShakeHandsActivity.class);
                break;


        }

        intent.putExtra("type", type);
        intent.putExtra("mac", mac);
        intent.putExtra("cid", mCid);
        intent.putExtra("vid", mVid);
        intent.putExtra("pid", mPid);
        startActivity(intent);

    }


    @Override
    public void bleOpen() {

    }

    @Override
    public void bleClose() {
        checkPermission();
    }

    @Override
    public boolean onFilter(BleValueBean bleValueBean) {
        int cid = bleValueBean.getCid();
        BleLog.i(TAG, "绑定设备广播类型:" + cid + "||添加的类型:" + mType);
        if (mType < 0) {
            if (TextUtils.isEmpty(mFilterName) && TextUtils.isEmpty(mFilterMac)) {
                return true;
            }
            if (!TextUtils.isEmpty(mFilterName)) {
                String name = bleValueBean.getName();
                if (name != null && name.toUpperCase().contains(mFilterName.toUpperCase())) {
                    return true;
                }
            }
            if (!TextUtils.isEmpty(mFilterMac)) {
                String mac = bleValueBean.getMac().replace(":", "").toUpperCase().trim();
                return mac.contains(mFilterMac.toUpperCase());
            }
            return false;
        } else {

            return mType == cid;
        }

    }

    @Override
    public void onScanRecord(BleValueBean mBle) {
        //TODO 过滤后的设备
    }


    //--------------------------start Loading--------------------------
    private LoadingIosDialogFragment mDialogFragment;

    /**
     * 显示加载
     */
    private void showLoading() {
        if (mDialogFragment == null) {
            mDialogFragment = new LoadingIosDialogFragment();
        }
        mDialogFragment.show(getSupportFragmentManager());
    }

    /**
     * 关闭加载
     */
    private void dismissLoading() {
        if (mDialogFragment != null) {
            mDialogFragment.dismiss();
        }
    }

    //--------------------------end Loading--------------------------


    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothService != null) {
            mBluetoothService.setOnCallback(ShowBleActivity.this);
            mBluetoothService.setOnScanFilterListener(ShowBleActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null) {
            mBluetoothService.stopScan();
        }
    }


    // ------------------- 权限 ------------------

    /**
     * 检查权限
     */
    private void checkPermission() {
        // 没有蓝牙权限就请求蓝牙权限
        if (!hasBluetooth()) {
            requestBluetooth();
            return;
        }
        new CheckPermissionUtils(this).checkPermissions(() -> {
            // 都有了，OK
            Toast.makeText(mContext, "权限都有，可以开始搜索", Toast.LENGTH_SHORT).show();

        });
    }


    /**
     * 蓝牙是否打开
     *
     * @return boolean
     */
    private boolean hasBluetooth() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }


    /**
     * 申请打开蓝牙
     */
    @SuppressLint("MissingPermission")
    private void requestBluetooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(intent);
    }


}

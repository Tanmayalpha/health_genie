package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.CallbackDisIm;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.pingwang.bluetoothlib.listener.OnScanFilterListener;
import com.pingwang.bluetoothlib.server.ELinkBleServer;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import aicare.net.cn.sdk.ailinksdkdemoandroid.adapter.StringAdapter;
import aicare.net.cn.sdk.ailinksdkdemoandroid.config.AppConfig;
import aicare.net.cn.sdk.ailinksdkdemoandroid.config.BleDeviceConfig;
import aicare.net.cn.sdk.ailinksdkdemoandroid.dialog.HintDataDialogFragment;
import aicare.net.cn.sdk.ailinksdkdemoandroid.dialog.LoadingIosDialogFragment;
import aicare.net.cn.sdk.ailinksdkdemoandroid.find.FindDeviceNewActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.TempInstrument.TempInstrumentActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.airdetector.AirDetectorActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.ble_nutrition.BleNutritionActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.blood_pressure_tc.BloodPressureTcActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.coffee_scale.CoffeeScaleActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.fascia_gun.FasciaGunActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.food_temp.FoodTempActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.noise_meter.BleNoiseMeterActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.noise_meter.WifiBleNoiseMeterActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.share_charger.ShareChargerActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.share_condom.ShareCondomActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.share_socket.ShareSocketActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.toothbrush_test.ToothbrushTestActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.weight_scale.WeightScaleActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.CheckPermissionUtils;
import aicare.net.cn.sdk.ailinksdkdemoandroid.view.MyItemDecoration;
import cn.net.aicare.modulelibrary.module.RopeSkipping.RopeSkippingBleData;
import cn.net.aicare.modulelibrary.module.scooter.SkateboardBleConfig;


/**
 * xing<br>
 * 2019/3/6<br>
 * java类作用描述
 */
public class ShowBleActivity extends AppCompatActivity implements OnCallbackBle, OnScanFilterListener {

    public static final int REQUEST_PERMISSION_CODE = 1500;

    private static String TAG = ShowBleActivity.class.getName();

    private final int BIND_SERVER_OK = 1;
    private final int BIND_SERVER_ERR = 2;
    private final int REFRESH_DATA = 3;
    private EditText et_cid;
    private List<BleValueBean> mBleValueList;
    private StringAdapter listAdapter;
    private ELinkBleServer mBluetoothService;
    /**
     * 服务Intent
     */
    private Intent bindIntent;
    private Context mContext;
    private int mType;
    private String mNoEncryptionMac = "";
    private String mFilterName = "";
    private String mFilterMac = "";
    private int mCid;
    private int mVid;
    private int mPid;
    private int mScanCid = 0;
    private String mBleName;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case BIND_SERVER_OK:

                    break;

                case REFRESH_DATA:
                    if (listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ble);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(getString(R.string.app_name) + BuildConfig.VERSION_NAME);
        }
        Intent mUserService = new Intent(this.getApplicationContext(), ELinkBleServer.class);
        //核心用户服务
        startService(mUserService);
        mContext = this;
        mType = getIntent().getIntExtra("type", 0);
        if (0 == mType) {
            finish();
            return;
        }
        mScanCid = mType;
        init();
        initData();

        // 如果是蓝牙牙刷，就弹个框提醒
        if (mType == BleDeviceConfig.TOOTHBRUSH_TEST) {
            new AlertDialog.Builder(mContext).setMessage("启动牙刷，工作10秒钟，然后关闭牙刷（让牙刷处于广播状态），然后点击搜索，选中列表中的牙刷，开始测试").setPositiveButton("确认", null).show();
        }
    }

    private void initData() {
        bindService();

    }


    private void init() {

        mBleValueList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.rv_data);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new MyItemDecoration(mContext, LinearLayoutManager.VERTICAL, 1, mContext.getResources().getColor(R.color.public_press_bg)));
        Button btn = findViewById(R.id.btn);
        Button btn1 = findViewById(R.id.btn1);
        Button clear = findViewById(R.id.clear);
        EditText et_filter_name = findViewById(R.id.et_filter_name);
        EditText et_filter_mac = findViewById(R.id.et_filter_mac);
        EditText et_cid = findViewById(R.id.et_cid);
        et_cid.setText(String.valueOf(mScanCid));


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothService != null) {
                    BleLog.i(TAG, "搜索设备");
                    try {
                        String cidStr = et_cid.getText().toString().trim();
                        if (cidStr.startsWith("0x")) {
                            mScanCid = Integer.parseInt(cidStr, 16);
                        } else {
                            mScanCid = Integer.parseInt(cidStr);
                        }
                    } catch (NumberFormatException e) {
                        mScanCid = -1;
                        e.printStackTrace();
                    }
                    mFilterName = et_filter_name.getText().toString().trim();
                    mFilterMac = et_filter_mac.getText().toString().trim();
                    if (mType == BleDeviceConfig.SMART_SCOOTER) {
                        Map<String, String> map = new HashMap<>();
                        map.put(SkateboardBleConfig.UUID_BROADCAST.toString(), "37,3,1");//37=0x0025=电滑板的cid
                        mBluetoothService.scanLeDevice(30 * 1000, map, BleConfig.UUID_SERVER_AILINK, SkateboardBleConfig.UUID_BROADCAST);
                    } else {
                        //0000FEE7=手表
                        mBluetoothService.scanLeDevice(1000, BleConfig.UUID_SERVER_AILINK, UUID.fromString("0000FEE7-0000-1000-8000-00805F9B34FB"), SkateboardBleConfig.UUID_BROADCAST);
                    }
                    mBleValueList.clear();
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
                    mBleValueList.clear();
                    listAdapter.notifyDataSetChanged();
                }
            }
        });

        listAdapter = new StringAdapter(this, mBleValueList, new StringAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                BleValueBean bleValueBean = mBleValueList.get(position);
                String mac = bleValueBean.getMac();
                mCid = bleValueBean.getCid();
                mVid = bleValueBean.getVid();
                mPid = bleValueBean.getPid();
                mBleName = bleValueBean.getName();
                if (mType == BleDeviceConfig.CLEAR_SHAKE_HANDS) {
                    //验证不握手,不加密的界面使用
                    BleConfig.setHandshakeStatus(mac, false);
                    if (mBluetoothService != null) {
                        mBluetoothService.stopScan();
                        mBluetoothService.connectDevice(mac);
                        showLoading();
                    }
                } else if (BleDeviceConfig.TOOTHBRUSH_WIFI_BLE == mType) {
                    Intent intent = new Intent();
                    intent.setClass(ShowBleActivity.this, ToothBrushWifiBleActivity.class);
                    intent.putExtra("type", mType);
                    intent.putExtra("mac", mac);
                    startActivity(intent);

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
                    } else if (mCid == BleDeviceConfig.LEAP_WATCH) {
                        // 手表不握手
                        BleConfig.setHandshakeStatus(mac, false);
                    }
                    if (mBluetoothService != null) {
                        mBluetoothService.stopScan();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mBluetoothService.connectDevice(mac, BluetoothDevice.TRANSPORT_LE);
                        } else {
                            mBluetoothService.connectDevice(mac);
                        }
                        showLoading();
                    }
                }
            }

            @Override
            public void onLongClick(int position) {
                BleValueBean bleValueBean = mBleValueList.get(position);
                if (bleValueBean == null) {
                    return;
                }
                StringBuilder showData = new StringBuilder();
                if (bleValueBean.getParcelUuids() != null) {
                    showData.append("UUID:");
                    for (ParcelUuid parcelUuid : bleValueBean.getParcelUuids()) {
                        showData.append(parcelUuid.toString().substring(0, 8)).append(";");
                    }
                    showData.append("\n");
                }
                String data2 = BleStrUtils.byte2HexStr(bleValueBean.getManufacturerData());
                showData.append(data2);
                HintDataDialogFragment.newInstance().setTitle("自定义厂商数据", 0).setContent(showData.toString(), false).setOk("", 0).show(getSupportFragmentManager());
            }
        });
        recyclerView.setAdapter(listAdapter);


        // 延迟后请求权限
        btn.postDelayed(() -> {
            if (isDestroyed()) {
                return;
            }

            checkPermission();
        }, 500);
    }


    //---------------------------------服务---------------------------------------------------

    private void bindService() {
        BleLog.i(TAG, "绑定服务");
        if (bindIntent == null) {
            bindIntent = new Intent(mContext, ELinkBleServer.class);
            if (mFhrSCon != null) {
                this.bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
            }
        }
    }


    private void unbindService() {
        if (mBluetoothService != null) {
            mBluetoothService.stopForeground();//停止前台服务
        }
        CallbackDisIm.getInstance().removeListener(this);
        if (mFhrSCon != null) {
            BleLog.i(TAG, "解绑服务");
            this.unbindService(mFhrSCon);
        }
        bindIntent = null;
    }


    /**
     * 服务连接与界面的连接
     */
    private ServiceConnection mFhrSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BleLog.i(TAG, "服务与界面建立连接成功");
            //与服务建立连接
            mBluetoothService = ((ELinkBleServer.BluetoothBinder) service).getService();
            if (mBluetoothService != null) {
                mBluetoothService.setOnCallback(ShowBleActivity.this);
                mBluetoothService.setOnScanFilterListener(ShowBleActivity.this);
                mBluetoothService.initForegroundService(1, R.mipmap.ic_launcher, "前台服务", MainActivity.class);
                mBluetoothService.startForeground();//启动前台服务

                mHandler.sendEmptyMessage(BIND_SERVER_OK);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            BleLog.i(TAG, "服务与界面连接断开");
            //与服务断开连接
            mBluetoothService = null;
        }
    };


    @Override
    public void onStartScan() {

    }

    private long mOldRefreshTime = 0;

    @Override
    public void onScanning(@NonNull BleValueBean data) {
        String mAddress = data.getMac();
        BleLog.i(TAG, "MAC=" + mAddress + "||CID=" + data.getCid() + "||VID=" + data.getVid() + "||PID=" + data.getPid());
        boolean oldData = false;
        for (int i = 0; i < mBleValueList.size(); i++) {
            BleValueBean bleValueBean = mBleValueList.get(i);
            if (bleValueBean.equals(data)) {
                bleValueBean.setRssi(data.getRssi());
                if (!oldData) {
                    oldData = true;
                }
                listAdapter.notifyItemChanged(i);
            }
        }
        if (!oldData) {
            //不是旧设备,是新的,添加
            mBleValueList.add(data);
            listAdapter.notifyDataSetChanged();
        } else {
//            if (System.currentTimeMillis()-mOldRefreshTime>500){
//                mOldRefreshTime=System.currentTimeMillis();
//                listAdapter.notifyDataSetChanged();
//            }
        }
//        String data1 = BleStrUtils.byte2HexStr(data.getScanRecord());
//        String data2 = BleStrUtils.byte2HexStr(data.getManufacturerData());
//        BleLog.i(TAG, "设备地址+广播数据:" + mAddress + "||" + data1 + "||" + data2);


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
                //婴儿秤
                intent.setClass(ShowBleActivity.this, BabyCmdActivity.class);
                break;
            case BleDeviceConfig.INFRARED_THERMOMETER:
                //额温枪
                intent.setClass(ShowBleActivity.this, TempGunCmdActivity.class);
                break;
            case BleDeviceConfig.BLOOD_PRESSURE:
            case BleDeviceConfig.SPHY_WIFI_BLE:
                //血压计
                intent.setClass(ShowBleActivity.this, SphyCmdActivity.class);
                break;
            case BleDeviceConfig.THERMOMETER:
                //体温计
                intent.setClass(ShowBleActivity.this, TempCmdActivity.class);
                break;
            case BleDeviceConfig.HEIGHT_METER:
                //身高仪
                intent.setClass(ShowBleActivity.this, HeightCmdActivity.class);
                break;
            case BleDeviceConfig.WEIGHT_BODY_FAT_SCALE:
                //体重体脂称
                intent.setClass(ShowBleActivity.this, WeightScaleBleActivity.class);
                break;
            case BleDeviceConfig.WEIGHT_BODY_FAT_SCALE_AD:
                //体重体脂称
                intent.setClass(ShowBleActivity.this, ADWeightScaleCmdActivity.class);
                break;
            case BleDeviceConfig.WEIGHT_BODY_FAT_SCALE_WIFI_BLE:
                intent.setClass(ShowBleActivity.this, WeightScaleWifiBleActivity.class);
                break;
            case BleDeviceConfig.TOOTHBRUSH_WIFI_BLE:
                intent.setClass(ShowBleActivity.this, ToothBrushWifiBleActivity.class);
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

            case BleDeviceConfig.SMART_SCOOTER_CM02:
                intent.setClass(ShowBleActivity.this, AiLinkScooterActivity.class);
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
                intent.setClass(ShowBleActivity.this, FindDeviceNewActivity.class);
                BleConfig.setHandshakeStatus(mac, false);
                break;
            case BleDeviceConfig.FOOD_TEMP:
                // 食品温度计
                intent.setClass(ShowBleActivity.this, FoodTempActivity.class);
                break;
            case BleDeviceConfig.HEIGHT_BODY_FAT:
                //身高体脂秤
                intent.setClass(ShowBleActivity.this, HeightWeightScaleActivity.class);
                break;
            case BleDeviceConfig.TEMP_Humidity:
            case BleDeviceConfig.TEMP_Humidity_wifi:
                intent.setClass(ShowBleActivity.this, TempHumidityActivity.class);
                break;
            case BleDeviceConfig.ROPE_SKIPPING:
                intent.setClass(ShowBleActivity.this, RopeSkippingActivity.class);
                break;
            case BleDeviceConfig.BLE_NUTRITION_SCALE:
                // 蓝牙营养秤
                intent.setClass(ShowBleActivity.this, BleNutritionActivity.class);
                break;
            case BleDeviceConfig.TOOTHBRUSH_TEST:
                // 牙刷测试
                intent.setClass(ShowBleActivity.this, ToothbrushTestActivity.class);
                break;
            case BleDeviceConfig.FASCIA_GUN:
                // 筋膜枪
                intent.setClass(ShowBleActivity.this, FasciaGunActivity.class);
                break;
            case BleDeviceConfig.TEMP_INSTRUMENT:
                // 测温仪
                intent.setClass(ShowBleActivity.this, TempInstrumentActivity.class);
                break;

            case BleDeviceConfig.PUBLIC_BLE_NETWORK:
                // 通用配网
                intent.setClass(ShowBleActivity.this, PublicBleNetworkCmdActivity.class);
                break;
            case BleDeviceConfig.AIR_DETECTOR:
            case BleDeviceConfig.MQTT_AIR_DETECTOR:
                intent.setClass(ShowBleActivity.this, AirDetectorActivity.class);
                break;
            //wifi+ble噪音计
            case BleDeviceConfig.WIFI_BLE_NOISE_METER:
                intent.setClass(ShowBleActivity.this, WifiBleNoiseMeterActivity.class);
                break;
            //ble噪音计
            case BleDeviceConfig.BLE_NOISE_METER:
                intent.setClass(ShowBleActivity.this, BleNoiseMeterActivity.class);
                break;

            //体脂秤
            case BleDeviceConfig.WEIGHT_SCALE:
                intent.setClass(ShowBleActivity.this, WeightScaleActivity.class);
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
            case -10:
                // 血压计透传
                intent.setClass(ShowBleActivity.this, BloodPressureTcActivity.class);
                break;
            case BleDeviceConfig.CLEAR_SHAKE_HANDS:
                //验证不握手不加密的界面
                intent.setClass(ShowBleActivity.this, ClearShakeHandsActivity.class);
                break;

            default:

                break;

        }


        intent.putExtra("type", type);
        intent.putExtra("mac", mac);
        intent.putExtra("cid", mCid);
        intent.putExtra("vid", mVid);
        intent.putExtra("pid", mPid);
        intent.putExtra("bleName", mBleName);
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
        boolean nameAndMac = false;
        if (TextUtils.isEmpty(mFilterName) && TextUtils.isEmpty(mFilterMac)) {
            nameAndMac = true;
        }
        if (!TextUtils.isEmpty(mFilterName)) {
            String name = bleValueBean.getName();
            if (name != null && name.toUpperCase().contains(mFilterName.toUpperCase())) {
                nameAndMac = true;
            }
        }
        if (!TextUtils.isEmpty(mFilterMac)) {
            String mac = bleValueBean.getMac().replace(":", "").toUpperCase().trim();
            nameAndMac = mac.contains(mFilterMac.toUpperCase());
        }

        if (mScanCid == BleDeviceConfig.TOOTHBRUSH_TEST && nameAndMac) {
            return cid == BleDeviceConfig.TOOTHBRUSH_TEST || cid == BleDeviceConfig.TOOTHBRUSH_WIFI_BLE;
        }
        if (mScanCid == BleDeviceConfig.TEMP_Humidity && nameAndMac) {
            return cid == BleDeviceConfig.TEMP_Humidity || cid == BleDeviceConfig.TEMP_Humidity_wifi;
        }
        if (mScanCid == BleDeviceConfig.FOOD_TEMP && nameAndMac) {
            return cid == BleDeviceConfig.FOOD_TEMP || cid == 63;
        }
        if (mScanCid < 0) {
            return nameAndMac;
        } else {
            return isCheckDevice(mScanCid, Math.abs(cid), nameAndMac);
        }

    }


    /**
     * 检查设备
     *
     * @param scanCid    扫描cid
     * @param cid        cid
     * @param nameAndMac 名字和mac过滤
     * @return boolean
     */
    private boolean isCheckDevice(int scanCid, int cid, boolean nameAndMac) {
        boolean okDevice = false;
        if (scanCid == BleDeviceConfig.BLOOD_PRESSURE) {
            //血压计包含wifi+bel
            if (nameAndMac) {
                okDevice = (scanCid == cid) || (cid == BleDeviceConfig.SPHY_WIFI_BLE);
            }
        } else {
            okDevice = scanCid == cid && nameAndMac;
        }
        return okDevice;
    }


    @Override
    public void onScanRecord(BleValueBean mBle) {
        //TODO 过滤后的设备
    }

    @Override
    public void onScanTimeOut() {

    }

    @Override
    public void onScanErr(int type, long time) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            checkPermission();
        }
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
        unbindService();
    }


    // ------------------- 权限 ------------------

    /**
     * 检查权限
     */
    private void checkPermission() {
        new CheckPermissionUtils(this).checkPermissions(new CheckPermissionUtils.OnPermissionListener() {
            @Override
            public void onPermissionsOk() {
                ShowBleActivity.this.onPermissionsOk();
            }
        });
        // 没有蓝牙权限就请求蓝牙权限
        if (!hasBluetooth()) {
            requestBluetooth();
            return;
        }
        // 没有定位权限就请求定位权限
        if (!hasLocationPermission()) {
            requestLocationPermission(this);
            return;
        }
        // 没有定位服务就请求定位服务
        if (!hasLocationService()) {
            requestLocationService();
            return;
        }
        // 都有了，OK
        Toast.makeText(mContext, "权限都有，可以开始搜索", Toast.LENGTH_SHORT).show();
    }

    /**
     * 权限ok
     */
    protected void onPermissionsOk() {

    }

    /**
     * 是否有定位权限
     *
     * @return boolean
     */
    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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
     * 定位服务是否打开
     *
     * @return boolean
     */
    private boolean hasLocationService() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return false;
        }
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * 申请定位权限
     */
    private void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_CODE);
    }

    /**
     * 申请打开蓝牙
     */
    @SuppressLint("MissingPermission")
    private void requestBluetooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_PERMISSION_CODE);
    }

    /**
     * 申请打开定位服务
     */
    private void requestLocationService() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, REQUEST_PERMISSION_CODE);
    }
}

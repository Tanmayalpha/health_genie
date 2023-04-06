package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.besthealth.bhBodyComposition120.BhBodyComposition;
import com.pingwang.bluetoothlib.AILinkSDK;
import com.pingwang.bluetoothlib.utils.BleLog;

import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.config.AppConfig;
import aicare.net.cn.sdk.ailinksdkdemoandroid.config.BleDeviceConfig;
import aicare.net.cn.sdk.ailinksdkdemoandroid.dialog.HintDataDialogFragment;
import aicare.net.cn.sdk.ailinksdkdemoandroid.find.FindDeviceNewActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.body_scale_4g.BodyScale4GActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.broadcast_height.BroadcastHeightActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.broadcast_nutrition.BroadNutritionActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.broadcast_weight_sacle.BroadcastWeightScaleActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.leaone_broadcast.LeaOneBroadcastActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.AppStart;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.L;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.SP;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {


    private static String TAG = MainActivity.class.getName();
    private List<View> mList = new ArrayList<>();
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
        initData();
        initListener();
    }


    protected void initListener() {
        String version = getString(R.string.version) + ":" + BuildConfig.VERSION_NAME;
        ((TextView) findViewById(R.id.tv_app_version)).setText(version);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(getString(R.string.app_name) + BuildConfig.VERSION_NAME);
        }
        findViewById(R.id.btn_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, AboutActivity.class));
            }
        });

        MyListener listener = new MyListener();
        mList.add(findViewById(R.id.btn_sphy));
        mList.add(findViewById(R.id.btn_tempgun));
        mList.add(findViewById(R.id.btn_temp));
        mList.add(findViewById(R.id.btn_baby));
        mList.add(findViewById(R.id.btn_height));
        mList.add(findViewById(R.id.btn_ble));
        mList.add(findViewById(R.id.btn_ble_test));
        mList.add(findViewById(R.id.btnConnectTest));
        mList.add(findViewById(R.id.btn_ad_weight));
        mList.add(findViewById(R.id.btn_ble_weight));
        mList.add(findViewById(R.id.btn_wifi_ble_tooth));
        mList.add(findViewById(R.id.wifi_config));
        mList.add(findViewById(R.id.eight_scale));
        mList.add(findViewById(R.id.btn_ota));
        mList.add(findViewById(R.id.glucometer));
        mList.add(findViewById(R.id.btn_broadcast_scale));
        mList.add(findViewById(R.id.btn_broadcast_blood_oxygen));
        mList.add(findViewById(R.id.btn_smart_mask));
        mList.add(findViewById(R.id.btn_bld));
        mList.add(findViewById(R.id.btn_bleBo));
        mList.add(findViewById(R.id.btn_coffeeScale));
        mList.add(findViewById(R.id.btn_scooter));
        mList.add(findViewById(R.id.btn_shareCharger));
        mList.add(findViewById(R.id.btn_share_socket));
        mList.add(findViewById(R.id.btn_transmission));
        mList.add(findViewById(R.id.btn_wifi_ble_weight));
        mList.add(findViewById(R.id.btn_baby_body_fat));
        mList.add(findViewById(R.id.btn_broadcast_height));
        mList.add(findViewById(R.id.btn_hbfs));
        mList.add(findViewById(R.id.btn_find));
        mList.add(findViewById(R.id.btn_4g_bs));
        mList.add(findViewById(R.id.btn_clear_shake_hands));
        mList.add(findViewById(R.id.btn_food_temp));
        mList.add(findViewById(R.id.btn_temp_humidity));
        mList.add(findViewById(R.id.btn_share_condom));
        mList.add(findViewById(R.id.btn_rope_skip));
        mList.add(findViewById(R.id.btn_broadcast_nutrition));
        mList.add(findViewById(R.id.btn_ble_nutrition));
        mList.add(findViewById(R.id.btn_toothbrush_test));
        mList.add(findViewById(R.id.btn_leaone_broadcast));
        mList.add(findViewById(R.id.btn_fascia_gun));
        mList.add(findViewById(R.id.btn_blood_pressure_tc));
        mList.add(findViewById(R.id.btn_body_scale_4g));
        mList.add(findViewById(R.id.btn_scooter_cm02));
        mList.add(findViewById(R.id.btn_temp_instrument));
        mList.add(findViewById(R.id.btn_leap_watch));
        mList.add(findViewById(R.id.btn_public_ble_network));
        mList.add(findViewById(R.id.btn_rope_skipping_set_mode));
        mList.add(findViewById(R.id.btn_air_detector));
        mList.add(findViewById(R.id.btn_mqtt_air_detector));
        mList.add(findViewById(R.id.btn_mqtt));
        mList.add(findViewById(R.id.btn_wifi_ble_noise_meter));
        mList.add(findViewById(R.id.btn_ble_noise_meter));
        mList.add(findViewById(R.id.btn_meat_probe_charger));
        mList.add(findViewById(R.id.btn_weight_scale));
        mList.add(findViewById(R.id.btn_broadcast_scale_weight));
        for (View view : mList) {
            view.setOnClickListener(listener);
        }
        List<Integer> showViewId = AppConfig.SHOW_VIEW_ID;
        if (showViewId != null) {
            for (View view : mList) {
                view.setVisibility(View.GONE);
            }
            for (Integer integer : showViewId) {
                findViewById(integer).setVisibility(View.VISIBLE);
            }
        }
        findViewById(R.id.btn_rope_skipping_set_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, RopeSkippingSetActivity.class));

            }
        });

//        AILinkBleManager.getInstance().init(mContext, new AILinkBleManager.onInitListener() {
//            @Override
//            public void onInitSuccess() {
//                L.i("初始化成功");
//                AILinkBleManager.getInstance().startScan(1000);
//                AILinkBleManager.getInstance().setOnCallbackBle(new OnCallbackBle() {
//                    @Override
//                    public void onScanning(BleValueBean data) {
//                        L.i("当前搜索到的设备:"+data.getName()+" mac="+data.getMac());
//                        AILinkBleManager.getInstance().stopScan();
//                        AILinkBleManager.getInstance().connectDevice(data);
//                    }
//
//                    @Override
//                    public void onServicesDiscovered(String mac) {
//                        L.i("连接成功:"+mac);
//                    }
//                });
//
//            }
//
//            @Override
//            public void onInitFailure() {
//                L.i("初始化失败");
//            }
//        });

    }

    protected void initData() {
        initPermissions();

    }

    protected void initView() {
        BleLog.init("", "", true);
        //connectDevice(BleValueBean bleValueBean);连接的时候需要传广播对象,否则返回的cid,vid,pid始终都是0
        //每次连接之前都要设置一次,设置一次之后就一直有效
        //sdk
//        AILinkBleManager.getInstance().init(mContext, new AILinkBleManager.onInitListener() {
//            @Override
//            public void onInitSuccess() {
//                AILinkBleManager.getInstance().startScan(0);
//            }
//
//            @Override
//            public void onInitFailure() {
//
//            }
//        });
//        BleConfig.addVendorID(0xac05);
        SP.init(this);
    }

    private class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
//          boolean onClick=  initPermissions();
//          if (!onClick){
//              return;
//          }
            int type = 0;
            switch (v.getId()) {

                case R.id.btn_clear_shake_hands:
                    type = BleDeviceConfig.CLEAR_SHAKE_HANDS;
                    break;

                case R.id.btn_sphy:
                    type = BleDeviceConfig.BLOOD_PRESSURE;
                    break;
                case R.id.btn_tempgun:
                    type = BleDeviceConfig.INFRARED_THERMOMETER;
                    break;
                case R.id.btn_temp:
                    type = BleDeviceConfig.THERMOMETER;
                    break;
                case R.id.btn_baby:
                    type = BleDeviceConfig.BABY_SCALE;
                    break;
                case R.id.btn_height:
                    type = BleDeviceConfig.HEIGHT_METER;
                    break;
                case R.id.btn_lock:
                    type = BleDeviceConfig.SMART_LOCK;
                    break;
                case R.id.btn_ad_weight:
                    type = BleDeviceConfig.WEIGHT_BODY_FAT_SCALE_AD;
                    break;
                case R.id.btn_wifi_ble_weight:
                    type = BleDeviceConfig.WEIGHT_BODY_FAT_SCALE_WIFI_BLE;
                    break;
                case R.id.btn_wifi_ble_tooth:
                    type = BleDeviceConfig.TOOTHBRUSH_WIFI_BLE;
                    break;
                case R.id.btn_ble_weight:
                    type = BleDeviceConfig.WEIGHT_BODY_FAT_SCALE;
                    break;
                case R.id.glucometer:
                    type = BleDeviceConfig.BLOOD_GLUCOSE;
                    break;
                case R.id.btn_baby_body_fat:
                    type = BleDeviceConfig.BABY_BODY_FAT;
                    break;
                case R.id.btn_temp_instrument:
                    type = BleDeviceConfig.TEMP_INSTRUMENT;
                    break;
                case R.id.btn_mqtt_air_detector:
                    type = BleDeviceConfig.MQTT_AIR_DETECTOR;
                    break;
                case R.id.btn_air_detector:
                    type = BleDeviceConfig.AIR_DETECTOR;
                    break;
                case R.id.btn_broadcast_scale:
                    Intent intentBroadcast = new Intent(MainActivity.this, BroadcastScaleActivity.class);
                    startActivity(intentBroadcast);
                    return;
                case R.id.btn_broadcast_scale_weight:
                    Intent intentBroadcastWeight = new Intent(MainActivity.this, BroadcastWeightScaleActivity.class);
                    startActivity(intentBroadcastWeight);
                    return;
                case R.id.btn_broadcast_blood_oxygen:
                    Intent intentBloodOxygen = new Intent(MainActivity.this, BroadcastBloodOxygenActivity.class);
                    startActivity(intentBloodOxygen);
                    return;

                case R.id.btn_smart_mask:
                    type = BleDeviceConfig.SMART_MASK;
                    break;
                case R.id.btn_bleBo:
                    type = BleDeviceConfig.BLE_BOOLD_OXYGEN;
                    break;
//                case R.id.btn_bld:
//                    type = BleDeviceConfig.BLD_WEIGHT;
//                    break;
                case R.id.btn_ble:
                    type = -1;
                    break;
                case R.id.btn_ble_test:
                    type = -2;
                    break;
                case R.id.btn_ota:
                    type = -3;
                    break;

                case R.id.btn_transmission:
                    type = -4;
                    break;
                case R.id.btn_blood_pressure_tc:
                    // 血压计透传
                    type = -10;
                    break;
                case R.id.eight_scale:
                    type = BleDeviceConfig.EIGHT_BODY_FAT_SCALE;
                    break;
                case R.id.btnConnectTest:
                    Intent intent = new Intent(MainActivity.this, ConnectBleTestActivity.class);
                    startActivity(intent);
                    return;
                case R.id.wifi_config:
                    Intent intent1 = new Intent(MainActivity.this, WifiConfigActivity.class);
                    startActivity(intent1);
                    return;
                case R.id.btn_coffeeScale:
                    // 咖啡秤
                    type = BleDeviceConfig.COFFEE_SCALE;
                    break;
                case R.id.btn_scooter:
                    type = BleDeviceConfig.SMART_SCOOTER;
                    break;
                case R.id.btn_shareCharger:
                    // 共享充电器
                    type = BleDeviceConfig.SHARE_CHARGER;
                    break;
                case R.id.btn_share_socket:
                    // 共享插座
                    type = BleDeviceConfig.SHARE_SOCKET;
                    break;
                case R.id.btn_share_condom:
                    // 共享插座
                    type = BleDeviceConfig.SHARE_CONDOM;
                    break;
                case R.id.btn_find:
                    // 寻物器
                    Intent findDeviceActivity = new Intent(MainActivity.this, FindDeviceNewActivity.class);
                    startActivity(findDeviceActivity);
                    return;

                case R.id.btn_broadcast_height:
                    // 广播身高仪
                    Intent intentBroadcastHeight = new Intent(MainActivity.this, BroadcastHeightActivity.class);
                    startActivity(intentBroadcastHeight);
                    return;
                case R.id.btn_4g_bs:
                    // 4G 血糖仪
                    Intent bloodSugar4GIntent = new Intent(MainActivity.this, BloodSugar4GActivity.class);
                    startActivity(bloodSugar4GIntent);
                    return;
                case R.id.btn_food_temp:
                    // 食品温度计
                    type = BleDeviceConfig.FOOD_TEMP;
                    break;
                case R.id.btn_hbfs:
                    type = BleDeviceConfig.HEIGHT_BODY_FAT;
                    break;
                case R.id.btn_temp_humidity:
                    type = BleDeviceConfig.TEMP_Humidity;
                    break;
                case R.id.btn_rope_skip:
                    type = BleDeviceConfig.ROPE_SKIPPING;
                    break;
                case R.id.btn_broadcast_nutrition:
                    // 广播营养秤
                    Intent broadcastNutritionIntent = new Intent(MainActivity.this, BroadNutritionActivity.class);
                    startActivity(broadcastNutritionIntent);
                    return;
                case R.id.btn_ble_nutrition:
                    // 蓝牙营养秤
                    type = BleDeviceConfig.BLE_NUTRITION_SCALE;
                    break;
                case R.id.btn_toothbrush_test:
                    // 牙刷测试
                    type = BleDeviceConfig.TOOTHBRUSH_TEST;
                    break;
                case R.id.btn_leaone_broadcast:
                    // LeaOne 广播秤
                    Intent broadcastLeaOneIntent = new Intent(MainActivity.this, LeaOneBroadcastActivity.class);
                    startActivity(broadcastLeaOneIntent);
                    return;
                case R.id.btn_fascia_gun:
                    // 筋膜枪
                    type = BleDeviceConfig.FASCIA_GUN;
                    break;
                case R.id.btn_scooter_cm02:
                    type = BleDeviceConfig.SMART_SCOOTER_CM02;
                    break;
                case R.id.btn_body_scale_4g:
                    // 4G 体脂秤
                    Intent bodyScaleIntent = new Intent(MainActivity.this, BodyScale4GActivity.class);
                    startActivity(bodyScaleIntent);
                    return;
                case R.id.btn_leap_watch:
                    // 芯一代手表
                    type = BleDeviceConfig.LEAP_WATCH;
                    break;
                case R.id.btn_public_ble_network:
                    // BLE通用配网
                    type = BleDeviceConfig.PUBLIC_BLE_NETWORK;
                    break;

                case R.id.btn_wifi_ble_noise_meter:
                    // wifi+ble噪音计
                    type = BleDeviceConfig.WIFI_BLE_NOISE_METER;
                    break;
                case R.id.btn_ble_noise_meter:
                    // ble噪音计
                    type = BleDeviceConfig.BLE_NOISE_METER;
                    break;
                case R.id.btn_meat_probe_charger:
                    //食物探针充电盒
                    type = BleDeviceConfig.MEAT_PROBE_CHARGER;
                    break;
                case R.id.btn_weight_scale:
                    //体重秤
                    type = BleDeviceConfig.WEIGHT_SCALE;
                    break;
                case R.id.btn_mqtt:

                    return;
                default:
                    return;
            }
            startActivity(type);
        }
    }


    private void startActivity(int type) {
        Intent intent = new Intent(this, ShowBleActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleLog.quit();
    }


//-----------------------权限----------------------------------------
    /**
     * 需要申请的权限
     */
    private String[] LOCATION_PERMISSION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * 权限请求返回
     */
    private final int PERMISSION = 101;
    /**
     * 定位服务返回
     */
    protected final int LOCATION_SERVER = 102;
    private HintDataDialogFragment mHintDataDialog = null;

    protected void initPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onPermissionsOk();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSION, PERMISSION);
        } else {
            boolean bleStatus = AppStart.isLocServiceEnable(mContext);
            if (!bleStatus) {
                //没有开启定位服务
                mHintDataDialog = HintDataDialogFragment.newInstance().setTitle("提示", 0).setCancel("取消", 0).setOk("确定", 0).setContent("请求开启定位服务", true)
                        .setOnDialogListener(new HintDataDialogFragment.onDialogListener() {
                            @Override
                            public void onSucceedListener(View v) {
                                startLocationActivity();
                            }
                        });
                mHintDataDialog.show(getSupportFragmentManager());


            } else {
                onPermissionsOk();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //请求权限被拒绝
        if (requestCode != PERMISSION) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initPermissions();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, LOCATION_PERMISSION[0])) {
                //权限请求失败，但未选中“不再提示”选项,再次请求
                ActivityCompat.requestPermissions(this, LOCATION_PERMISSION, PERMISSION);
            } else {
                //权限请求失败，选中“不再提示”选项
                mHintDataDialog = HintDataDialogFragment.newInstance().setTitle("提示", 0).setCancel("取消", 0).setOk("确定", 0).setContent("请求开启定位权限", true)
                        .setOnDialogListener(new HintDataDialogFragment.onDialogListener() {
                            @Override
                            public void onSucceedListener(View v) {
                                AppStart.startUseSetActivity(mContext);
                            }
                        });
                mHintDataDialog.show(getSupportFragmentManager());

            }

        }
    }


    /**
     * 启动去设置定位服务
     */
    protected void startLocationActivity() {

        AppStart.startLocationActivity(this, LOCATION_SERVER);

    }


    /**
     * 权限ok
     */
    protected void onPermissionsOk() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SERVER) {
            //定位服务页面返回
            initPermissions();
        }
    }

}

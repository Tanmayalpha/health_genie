package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.besthealth.bhBodyComposition120.BhBodyComposition;
import com.pingwang.bluetoothlib.AILinkSDK;
import com.pingwang.bluetoothlib.utils.BleLog;

import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleAppBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.config.AppConfig;
import aicare.net.cn.sdk.ailinksdkdemoandroid.config.BleDeviceConfig;
import aicare.net.cn.sdk.ailinksdkdemoandroid.find.FindDeviceNewActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.broadcast_height.BroadcastHeightActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.broadcast_nutrition.BroadNutritionActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.SP;


public class MainActivity extends BleAppBaseActivity {

    private static String TAG = MainActivity.class.getName();
    private List<View> mList = new ArrayList<>();

    @Override
    protected void uiHandlerMessage(Message msg) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    public void onServiceSuccess() {

    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }

    @Override
    protected void initListener() {
        MyListener listener = new MyListener();

//        Button btn_shpy = findViewById(R.id.btn_sphy);
//        Button btn_tempgun = findViewById(R.id.btn_tempgun);
//        Button btn_temp = findViewById(R.id.btn_temp);
//        Button btn_baby = findViewById(R.id.btn_baby);
//        Button btn_height = findViewById(R.id.btn_height);
//        Button btn_ble = findViewById(R.id.btn_ble);
//        Button btn_weightScale = findViewById(R.id.btn_lock);
//        Button btn_ble_test = findViewById(R.id.btn_ble_test);
//        Button btnConnectTest = findViewById(R.id.btnConnectTest);
//        Button btn_ad_weight = findViewById(R.id.btn_ad_weight);
//        Button btn_ble_weight = findViewById(R.id.btn_ble_weight);
//        Button btn_wifi_ble_tooth = findViewById(R.id.btn_wifi_ble_tooth);
//        Button wifi_config = findViewById(R.id.wifi_config);
//        Button eight_scale = findViewById(R.id.eight_scale);
//        Button btn_ota = findViewById(R.id.btn_ota);
//        Button btn_wristband = findViewById(R.id.btn_wristband);
//        Button glucometer = findViewById(R.id.glucometer);
//        Button btn_broadcast_scale = findViewById(R.id.btn_broadcast_scale);
//        Button btn_broadcast_blood_oxygen = findViewById(R.id.btn_broadcast_blood_oxygen);
//        Button btn_smart_mask = findViewById(R.id.btn_smart_mask);
//        Button btn_bld = findViewById(R.id.btn_bld);
//        Button btn_bleBo = findViewById(R.id.btn_bleBo);
//        Button btn_coffeeScale = findViewById(R.id.btn_coffeeScale);
//        Button btn_scooter = findViewById(R.id.btn_scooter);
//        Button btn_shareCharger = findViewById(R.id.btn_shareCharger);
//        Button btn_transmission = findViewById(R.id.btn_transmission);
//        Button btn_wifi_ble_weight = findViewById(R.id.btn_wifi_ble_weight);
//        Button btn_baby_body_fat = findViewById(R.id.btn_baby_body_fat);
        mList.add(findViewById(R.id.btn_sphy));
        mList.add(findViewById(R.id.btn_tempgun));
        mList.add(findViewById(R.id.btn_temp));
        mList.add(findViewById(R.id.btn_baby));
        mList.add(findViewById(R.id.btn_height));
        mList.add(findViewById(R.id.btn_ble));
//        mList.add(findViewById(R.id.btn_lock));
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

    @Override
    protected void initData() {
        initPermissions();


    }

    @Override
    protected void initView() {
        BleLog.init("", "", BuildConfig.DEBUG);
        String version = getString(R.string.version) + ":" + BuildConfig.VERSION_NAME;
        ((TextView) findViewById(R.id.tv_app_version)).setText(version);
        AILinkSDK.getInstance().init(getApplication());//sdk
//        AILinkBleManager.getInstance().init(getApplication());
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
                    type=BleDeviceConfig.CLEAR_SHAKE_HANDS;
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
                case R.id.btn_broadcast_scale:
                    Intent intentBroadcast = new Intent(MainActivity.this, BroadcastScaleActivity.class);
                    startActivity(intentBroadcast);
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

                    // 广播身高仪
                    Intent bloodSugar4GIntent = new Intent(MainActivity.this, BloodSugar4GActivity.class);
                    startActivity(bloodSugar4GIntent);
                    return;
                case R.id.btn_food_temp:
                    // 食品温度计
                    type = BleDeviceConfig.FOOD_TEMP;
                    break;
                case R.id.btn_hbfs:
                    type=BleDeviceConfig.HEIGHT_BODY_FAT;
                    break;
                case R.id.btn_temp_humidity:
                    type= BleDeviceConfig.TEMP_Humidity;
                    break;
                case R.id.btn_rope_skip:
                    type=BleDeviceConfig.ROPE_SKIPPING;
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


}

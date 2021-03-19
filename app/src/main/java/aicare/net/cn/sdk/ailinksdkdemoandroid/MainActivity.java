package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pingwang.bluetoothlib.AILinkSDK;
import com.pingwang.bluetoothlib.utils.BleLog;

import aicare.net.cn.sdk.ailinksdkdemoandroid.config.BleDeviceConfig;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.SP;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AILinkSDK.getInstance().init(this);
        setContentView(R.layout.activity_main);
        BleLog.init("", "", BuildConfig.DEBUG);
        String version = getString(R.string.version) + ":" + BuildConfig.VERSION_NAME;
        ((TextView) findViewById(R.id.tv_app_version)).setText(version);
        init();
        initPermissions();
        SP.init(this);
    }

    private void init() {
        MyListener listener = new MyListener();
        Button btn_shpy = findViewById(R.id.btn_sphy);
        Button btn_tempgun = findViewById(R.id.btn_tempgun);
        Button btn_temp = findViewById(R.id.btn_temp);
        Button btn_baby = findViewById(R.id.btn_baby);
        Button btn_height = findViewById(R.id.btn_height);
        Button btn_ble = findViewById(R.id.btn_ble);
        findViewById(R.id.btn_ble_weight).setOnClickListener(listener);
        findViewById(R.id.btn_wifi_ble_tooth).setOnClickListener(listener);
        findViewById(R.id.wifi_config).setOnClickListener(listener);
        findViewById(R.id.eight_scale).setOnClickListener(listener);
        findViewById(R.id.btn_ota).setOnClickListener(listener);
        findViewById(R.id.glucometer).setOnClickListener(listener);
        findViewById(R.id.btn_broadcast_scale).setOnClickListener(listener);
        findViewById(R.id.btn_broadcast_blood_oxygen).setOnClickListener(listener);
        findViewById(R.id.btn_smart_mask).setOnClickListener(listener);

        findViewById(R.id.btn_transmission).setOnClickListener(listener);
        btn_shpy.setOnClickListener(listener);
        btn_tempgun.setOnClickListener(listener);
        btn_temp.setOnClickListener(listener);
        btn_baby.setOnClickListener(listener);
        btn_height.setOnClickListener(listener);
        btn_ble.setOnClickListener(listener);
        findViewById(R.id.btn_wifi_ble_weight).setOnClickListener(listener);
        findViewById(R.id.btn_baby_body_fat).setOnClickListener(listener);
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
                    Intent intentBroadcast = new Intent(MainActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.BroadcastScaleActivity.class);
                    startActivity(intentBroadcast);
                    return;
                case R.id.btn_broadcast_blood_oxygen:
                    Intent intentBloodOxygen = new Intent(MainActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.BroadcastBloodOxygenActivity.class);
                    startActivity(intentBloodOxygen);
                    return;

                case R.id.btn_smart_mask:
                    type = BleDeviceConfig.SMART_MASK;
                    break;
                case R.id.btn_ble:
                    type = -1;
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
                case R.id.wifi_config:
                    Intent intent1 = new Intent(MainActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.WifiConfigActivity.class);
                    startActivity(intent1);
                    return;


            }
            startActivity(type);
        }
    }


    /**
     * 初始化请求权限
     */
    private void initPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat
                    .requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CHANGE_WIFI_MULTICAST_STATE, Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 1) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                //权限请求失败，但未选中“不再提示”选项
                new AlertDialog.Builder(this).setTitle("提示").setMessage("请求使用定位权限搜索蓝牙设备").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //引导用户至设置页手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.cancel();
                        }

                    }
                }).show();
            } else {
                //权限请求失败，选中“不再提示”选项
//                T.showShort(MainActivity.this, "获取权限失败");
                new AlertDialog.Builder(this).setTitle("提示").setMessage("请求使用定位权限搜索蓝牙设备").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //引导用户至设置页手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.cancel();
                        }

                    }
                }).show();
            }

        }

    }


    private void startActivity(int tyep) {
        Intent intent = new Intent(this, aicare.net.cn.sdk.ailinksdkdemoandroid.ShowBleActivity.class);
        intent.putExtra("type", tyep);
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

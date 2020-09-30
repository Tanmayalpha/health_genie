package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pingwang.bluetoothlib.BleBaseActivity;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnBleDeviceDataListener;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.pingwang.bluetoothlib.listener.OnDialogOTAListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import aicare.net.cn.sdk.ailinksdkdemoandroid.dialog.DialogStringImageAdapter;
import aicare.net.cn.sdk.ailinksdkdemoandroid.dialog.ShowListDialogFragment;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.FileUtils;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.SP;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;


/**
 * xing<br>
 * 2019/4/25<br>
 * 显示数据
 */
public class TestOtaActivity extends BleBaseActivity implements OnCallbackBle,
        OnBleDeviceDataListener, View.OnClickListener, OnDialogOTAListener, ShowListDialogFragment.onDialogListener {

    private static String TAG = TestOtaActivity.class.getName();
    private final int REFRESH_DATA = 3;
    private final int SEND_DATA = 4;

    private TextView mTvVersion;

    private List<String> mList;
    private ArrayAdapter listAdapter;
    private Context mContext;
    private BleDevice mBleDevice;
    private String mAddress;

    private ArrayList<DialogStringImageAdapter.DialogStringImageBean> mDialogList;
    private String mOTAFileName;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {


                case REFRESH_DATA:
                    if (listAdapter != null)
                        listAdapter.notifyDataSetChanged();
                    break;

                case SEND_DATA:


                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ota);
        mContext = this;
        mAddress = getIntent().getStringExtra("mac");
        initPermissions();
        init();
        FileUtils.init();
    }

    private void init() {
        mList = new ArrayList<>();
        ListView listView = findViewById(R.id.listview);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        listView.setAdapter(listAdapter);

        findViewById(R.id.btn_ota_connect).setOnClickListener(this);
        findViewById(R.id.btn_ota_dis).setOnClickListener(this);
        findViewById(R.id.btn_ota_file).setOnClickListener(this);
        findViewById(R.id.btn_ota_start).setOnClickListener(this);
        mTvVersion=findViewById(R.id.tv_version);

        mOTAFileName = SP.getInstance().getOtaFileName();
        if (mOTAFileName.isEmpty())
            mTvVersion.setText("xxxxxxxx");
        else
            mTvVersion.setText(mOTAFileName);
        mDialogList=new ArrayList<>();

    }



    @Override
    public void onItemListener(int position) {
        if (mDialogList.size() > position) {
            DialogStringImageAdapter.DialogStringImageBean dialogStringImageBean = mDialogList
                    .get(position);
            String name = dialogStringImageBean.getName();
            mOTAFileName = name;
            SP.getInstance().putOtaFileName(name);
            mTvVersion.setText(mOTAFileName);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ota_connect:
                if (mBleDevice==null&&!TextUtils.isEmpty(mAddress)){
                    connectBle(mAddress);
                }
                break;

            case R.id.btn_ota_dis:
                if (mBleDevice!=null){
                    mBleDevice.disconnect();
                    mBleDevice=null;
                }
                break;
            case R.id.btn_ota_file:
                mDialogList.clear();
                ArrayList<String> list = FileUtils.list();
                for (String s : list) {
                    mDialogList.add(new DialogStringImageAdapter.DialogStringImageBean(s, 0));
                }

                ShowListDialogFragment.newInstance().setTitle("").setCancel("",0).setCancelBlank(true)
                        .setBackground(true).setBottom(false).setList(mDialogList).setOnDialogListener(this)
                        .show(getSupportFragmentManager());
                break;

            case R.id.btn_ota_start:
                if (mOTAFileName.isEmpty()) {
                    Toast.makeText(mContext, "请先选择文件", Toast.LENGTH_SHORT).show();
                    return;
                }
                String byFileName = FileUtils.getByFileName()+mOTAFileName;
                mList.add(TimeUtils.getTime() + "OTA已开始,请耐心等待");
                mHandler.sendEmptyMessage(REFRESH_DATA);
                mBleDevice.setOnDialogOTAListener(this);
                mBleDevice.startDialogOta(byFileName);
                break;


        }
    }


    /**
     * 初始化请求权限
     */
    private void initPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat
                    .requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 1) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                //权限请求失败，但未选中“不再提示”选项
                new AlertDialog.Builder(this).setTitle("提示").setMessage("请求使用定位权限搜索蓝牙设备")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //引导用户至设置页手动授权
                                Intent intent =
                                        new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getApplicationContext()
                                        .getPackageName(), null);
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
                new AlertDialog.Builder(this).setTitle("提示").setMessage("请求使用定位权限搜索蓝牙设备")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //引导用户至设置页手动授权
                                Intent intent =
                                        new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getApplicationContext()
                                        .getPackageName(), null);
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

    //---------------------------------服务---------------------------------------------------


    @Override
    public void onServiceSuccess() {
        BleLog.i(TAG, "服务与界面建立连接成功");
        //与服务建立连接
        if (mBluetoothService != null) {
            mBleDevice = mBluetoothService.getBleDevice(mAddress);
            mBluetoothService.setOnCallback(this);
            if (mBleDevice != null) {
                mBleDevice.setOnDialogOTAListener(this);
            }
        }
    }

    @Override
    public void onServiceErr() {
        BleLog.i(TAG, "服务与界面连接断开");
        //与服务断开连接
        mBluetoothService = null;
    }

    @Override
    public void unbindServices() {
        if (mBleDevice != null) {
            mBleDevice.disconnect();
            mBleDevice = null;
        }

    }

    //-----------------状态-------------------


    @Override
    public void onConnecting(@NonNull String mac) {
        //TODO 连接中
        BleLog.i(TAG, "连接中");
        mList.add(TimeUtils.getTime() + "连接中");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onDisConnected(@NonNull String mac, int code) {
        //TODO 连接断开
        BleLog.i(TAG, "连接断开");
        mList.add(TimeUtils.getTime() + "连接断开");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onServicesDiscovered(@NonNull String mac) {
        //TODO 连接成功(获取服务成功)
        BleLog.i(TAG, "连接成功(获取服务成功)");
        mList.add(TimeUtils.getTime() + "连接成功");
        mHandler.sendEmptyMessage(REFRESH_DATA);
        onServiceSuccess();
    }


    @Override
    public void bleOpen() {

    }

    @Override
    public void bleClose() {
        BleLog.i(TAG, "蓝牙未开启,可请求开启");
    }

    //-----------------通知-------------------



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        BleLog.i(TAG, "onDestroy");
    }

    @Override
    public void onOtaSuccess() {
        mList.add(TimeUtils.getTime() + "OTA成功");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onOtaFailure(int cmd ,String err) {
        mList.add(TimeUtils.getTime() + "OTA失败:" + err);
        mHandler.sendEmptyMessage(REFRESH_DATA);
        if (mBleDevice != null) {
            mBleDevice.disconnect();
        }
    }

    private int progressOld;

    @Override
    public void onOtaProgress(float progress) {
        int progressInt = (int) progress;
        if (progressOld != progressInt) {
            progressOld = progressInt;
            mList.add(TimeUtils.getTime() + "OTA进度:" + progressInt + "%");
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }
    }
}

package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.listener.CallbackDisIm;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.pingwang.bluetoothlib.listener.OnScanFilterListener;
import com.pingwang.bluetoothlib.server.ELinkBleServer;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.config.BleDeviceConfig;
import aicare.net.cn.sdk.ailinksdkdemoandroid.dialog.LoadingIosDialogFragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * xing<br>
 * 2019/3/6<br>
 * java类作用描述
 */
public class ShowBleActivity extends AppCompatActivity implements OnCallbackBle, OnScanFilterListener {

    private static String TAG = ShowBleActivity.class.getName();

    private final int BIND_SERVER_OK = 1;
    private final int BIND_SERVER_ERR = 2;
    private final int REFRESH_DATA = 3;
    private List<String> mList;
    private ArrayAdapter listAdapter;
    private ELinkBleServer mBluetoothService;
    /**
     * 服务Intent
     */
    private Intent bindIntent;
    private Context mContext;
    private int mType;
    private boolean mFilter = true;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case BIND_SERVER_OK:

                    break;

                case REFRESH_DATA:
                    listAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ble);
        Intent mUserService = new Intent(this.getApplicationContext(), ELinkBleServer.class);
        //核心用户服务
        startService(mUserService);
        mContext = this;
        mType = getIntent().getIntExtra("type", 0);
        if (0 == mType) {
            finish();
            return;
        }
        init();
        initData();


    }

    private void initData() {
        bindService();

    }

    private void init() {

        mList = new ArrayList<>();
        ListView listView = findViewById(R.id.listview);
        Button btn = findViewById(R.id.btn);
        Button btn1 = findViewById(R.id.btn1);
        Button clear = findViewById(R.id.clear);
        final Button filter = findViewById(R.id.filter);
        filter.setTag(true);
        filter.setText(getString(R.string.filter)+"true");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothService != null) {
                    BleLog.i(TAG, "搜索设备");
                    if (mType == -3) {
                        mBluetoothService.scanLeDevice(0);

                    } else {
                        if (mFilter)
                            mBluetoothService.scanLeDevice(0, BleConfig.UUID_SERVER_AILINK);
                        else
                            mBluetoothService.scanLeDevice(0);
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

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean m = (Boolean) filter.getTag();
                filter.setTag(!m);
                mFilter = !m;
                filter.setText("过滤:" + mFilter);
            }
        });

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemStr = mList.get(position);
                String mac = itemStr.split("=")[0];
                if (BleDeviceConfig.TOOTHBRUSH_WIFI_BLE==mType){
                    Intent intent=new Intent();
                    intent.setClass(ShowBleActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.ToothBrushWifiBleActivity.class);
                    intent.putExtra("type", mType);
                    intent.putExtra("mac", mac);
                    startActivity(intent);
                    finish();

                }else {
                    if (mBluetoothService != null) {
                        mBluetoothService.stopScan();
                        mBluetoothService.connectDevice(mac);
                        showLoading();
                    }
                }
            }
        });


    }


    //---------------------------------服务---------------------------------------------------

    private void bindService() {
        BleLog.i(TAG, "绑定服务");
        if (bindIntent == null) {
            bindIntent = new Intent(mContext, ELinkBleServer.class);
            if (mFhrSCon != null)
                this.bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
        }
    }


    private void unbindService() {
        if (mBluetoothService != null)
            mBluetoothService.stopForeground();//停止前台服务
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

    @Override
    public void onScanning(@NonNull BleValueBean data) {
        String mAddress = data.getMac();
        BleLog.i(TAG, "MAC=" + mAddress + "||CID=" + data.getCid() + "||VID=" + data.getVid() + "||PID=" + data.getPid());
        if (!mList.contains(mAddress + "=" + data.getName())) {
            String data1 = BleStrUtils.byte2HexStr(data.getScanRecord());
            String data2 = BleStrUtils.byte2HexStr(data.getManufacturerData());
            BleLog.i(TAG, "MAC=" + mAddress + " ||" + data1 + "||" + data2);
            mList.add(mAddress + "=" + data.getName());
            listAdapter.notifyDataSetChanged();
        }


    }


    @Override
    public void onConnecting(@NonNull String mac) {

    }

    @Override
    public void onDisConnected(@NonNull String mac, int code) {
        dismissLoading();
        Toast.makeText(mContext, getString(R.string.disconnect) + code, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onServicesDiscovered(@NonNull String mac) {
        dismissLoading();
        Intent intent = new Intent();
        int type = mType;//默认婴儿秤
        switch (type) {
            case BleDeviceConfig.BABY_SCALE:
                intent.setClass(ShowBleActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.BabyCmdActivity.class);
                break;
            case BleDeviceConfig.INFRARED_THERMOMETER:
                intent.setClass(ShowBleActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.TempGunCmdActivity.class);
                break;
            case BleDeviceConfig.BLOOD_PRESSURE:
                intent.setClass(ShowBleActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.SphyCmdActivity.class);
                break;
            case BleDeviceConfig.THERMOMETER:
                intent.setClass(ShowBleActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.TempCmdActivity.class);
                break;
            case BleDeviceConfig.HEIGHT_METER:
                intent.setClass(ShowBleActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.HeightCmdActivity.class);
                break;
            case BleDeviceConfig.WEIGHT_BODY_FAT_SCALE:
                intent.setClass(ShowBleActivity.this, WeightScaleBleActivity.class);
                break;
            case BleDeviceConfig.WEIGHT_BODY_FAT_SCALE_AD:
                intent.setClass(ShowBleActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.ADWeightScaleCmdActivity.class);
                break;
            case BleDeviceConfig.WEIGHT_BODY_FAT_SCALE_WIFI_BLE:
                intent.setClass(ShowBleActivity.this, WeightScaleWifiBleActivity.class);
                break;
            case BleDeviceConfig.TOOTHBRUSH_WIFI_BLE:
                intent.setClass(ShowBleActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.ToothBrushWifiBleActivity.class);
                break;
            case BleDeviceConfig.EIGHT_BODY_FAT_SCALE:
                intent.setClass(ShowBleActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.EightBodyfatActivity.class);
                break;
            case BleDeviceConfig.BLOOD_GLUCOSE:
                intent.setClass(ShowBleActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.BloodGlucoseActivity.class);
                break;
            case BleDeviceConfig.BABY_BODY_FAT:
                intent.setClass(ShowBleActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.BabyBodyFatCmdActivity.class);
                break;
            case BleDeviceConfig.SMART_MASK:
                intent.setClass(ShowBleActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.SmartMaskActivity.class);
                break;
            case -1:
                intent.setClass(ShowBleActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.BleCmdActivity.class);
                break;
            case -2:
                intent.setClass(ShowBleActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.TestCmdActivity.class);
                break;
            case -3:
                intent.setClass(ShowBleActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.TestOtaActivity.class);
                break;
            case -4:
                intent.setClass(ShowBleActivity.this, aicare.net.cn.sdk.ailinksdkdemoandroid.TransmissionActivity.class);
                break;

        }


        intent.putExtra("type", type);
        intent.putExtra("mac", mac);
        startActivity(intent);

    }


    @Override
    public void bleOpen() {

    }

    @Override
    public void bleClose() {

    }

    @Override
    public boolean onFilter(BleValueBean bleValueBean) {
        int cid = bleValueBean.getCid();
        BleLog.i(TAG, "绑定设备广播类型:" + cid + "||添加的类型:" + mType);
        if (mType < 0 || mType > 0xFF)
            return true;
        else
            return mType == cid;
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
        if (mDialogFragment == null)
            mDialogFragment = new LoadingIosDialogFragment();
        mDialogFragment.show(getSupportFragmentManager());
    }

    /**
     * 关闭加载
     */
    private void dismissLoading() {
        if (mDialogFragment != null)
            mDialogFragment.dismiss();
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
        unbindService();
    }
}

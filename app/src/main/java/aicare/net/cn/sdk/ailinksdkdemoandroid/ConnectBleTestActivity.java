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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.listener.CallbackDisIm;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.pingwang.bluetoothlib.server.ELinkBleServer;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;
import aicare.net.cn.sdk.ailinksdkdemoandroid.dialog.LoadingIosDialogFragment;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * xing<br>
 * 2019/3/6<br>
 * 连接测试工具
 */
public class ConnectBleTestActivity extends AppCompatActivity implements OnCallbackBle {

    private static String TAG = ConnectBleTestActivity.class.getName();

    private final int BIND_SERVER_OK = 1;
    private final int BIND_SERVER_ERR = 2;
    private final int REFRESH_LIST = 3;
    private final int REFRESH_DATA = 4;
    private List<String> mList;
    private List<String> mListData;
    private ArrayAdapter listAdapter;
    private ArrayAdapter listDataAdapter;
    private ELinkBleServer mBluetoothService;
    /**
     * 服务Intent
     */
    private Intent bindIntent;
    private Context mContext;
    private boolean mFilter = true;
    private long mConnectTime = 0;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BIND_SERVER_OK:
                    break;
                case REFRESH_LIST:
                    listAdapter.notifyDataSetChanged();
                    break;
                case REFRESH_DATA:
                    listDataAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_ble_test);
        Intent mUserService = new Intent(this.getApplicationContext(), ELinkBleServer.class);
        //核心用户服务
        startService(mUserService);
        mContext = this;
        init();
        initData();


    }

    private void initData() {
        bindService();

    }

    private void init() {
        mList = new ArrayList<>();
        mListData = new ArrayList<>();
        ListView listView = findViewById(R.id.listview);
        ListView listviewData = findViewById(R.id.listviewData);
        Button btn = findViewById(R.id.btn);
        Button btn1 = findViewById(R.id.btn1);
        Button clear = findViewById(R.id.clear);
        findViewById(R.id.clearData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListData.clear();
                listDataAdapter.notifyDataSetChanged();
            }
        });
        final Button filter = findViewById(R.id.filter);
        filter.setTag(true);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothService != null) {
                    BleLog.i(TAG, "搜索设备");
                    mBluetoothService.scanLeDevice(30000);
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
                    mBluetoothService.disconnectAll();
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
        listDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListData);
        listviewData.setAdapter(listDataAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemStr = mList.get(position);
                String mac = itemStr.split("=")[0];
                if (mBluetoothService != null) {
                    mBluetoothService.stopScan();
                    mBluetoothService.disconnectAll();
                    mBluetoothService.connectDevice(mac);
                    mConnectTime = System.currentTimeMillis();
                    showLoading();
                }
            }
        });
        findViewById(R.id.跳过).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "跳过", Toast.LENGTH_SHORT).show();
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
        CallbackDisIm.getInstance().removeListener(this);
        if (mFhrSCon != null)
            this.unbindService(mFhrSCon);
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
                mBluetoothService.setOnCallback(ConnectBleTestActivity.this);
                mBluetoothService.setOnScanFilterListener(null);
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
        if (!mList.contains(mAddress + "=" + data.getName())) {
            String data1 = BleStrUtils.byte2HexStr(data.getScanRecord());
            BleLog.i(TAG, "设备地址+广播数据:" + mAddress + "||" + data1);
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
        Toast.makeText(mContext, "连接断开:" + code, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onServicesDiscovered(@NonNull String mac) {
        long time = System.currentTimeMillis() - mConnectTime;
        dismissLoading();
        mListData.add(TimeUtils.getTime() + "连接成功获取服务成功:" + time);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    public void bleOpen() {
    }

    @Override
    public void bleClose() {
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
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
    }
}

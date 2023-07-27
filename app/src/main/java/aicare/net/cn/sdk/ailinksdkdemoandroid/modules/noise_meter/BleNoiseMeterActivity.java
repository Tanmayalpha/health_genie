package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.noise_meter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.pingwang.bluetoothlib.device.BleDevice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import cn.net.aicare.modulelibrary.module.NoiseMeter.NoiseMeterBleDevice;

/**
 * 噪音计(ble)
 *
 * @author xing
 */
public class BleNoiseMeterActivity extends BleBaseActivity implements View.OnClickListener, NoiseMeterBleDevice.OnNoiseDataListener {

    private ListView list_view;

    private List<String> mList;
    private ArrayAdapter mListAdapter;
    private String mMac;
    private BleDevice mBleDevice;
    private NoiseMeterBleDevice mNoiseMeterBle;
    private Button btn_ac, btn_light, btn_fs, btn_max, btn_up, btn_down;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_noise_meter);

        list_view = findViewById(R.id.list_view);
        btn_ac = findViewById(R.id.btn_noise_ble_ac);
        btn_light = findViewById(R.id.btn_noise_ble_light);
        btn_fs = findViewById(R.id.btn_noise_ble_fs);
        btn_max = findViewById(R.id.btn_noise_ble_max);
        btn_up = findViewById(R.id.btn_noise_ble_up);
        btn_down = findViewById(R.id.btn_noise_ble_down);
        btn_ac.setOnClickListener(this);
        btn_light.setOnClickListener(this);
        btn_fs.setOnClickListener(this);
        btn_max.setOnClickListener(this);
        btn_up.setOnClickListener(this);
        btn_down.setOnClickListener(this);


        // 获取Mac
        mMac = getIntent().getStringExtra("mac");

        // 初始化列表
        mList = new ArrayList<>();
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        list_view.setAdapter(mListAdapter);

    }

    @Override
    protected void onDestroy() {
        if (mNoiseMeterBle != null) {
            mNoiseMeterBle = null;
        }
        if (mBleDevice != null) {
            mBleDevice.disconnect();
//            mBluetoothService.disconnectAll();
        }
        super.onDestroy();
    }

    @Override
    public void onServiceSuccess() {
        Log.e("ljl", "onServiceSuccess: 服务打开成功");
        mBleDevice = mBluetoothService.getBleDevice(mMac);
        if (mBleDevice != null) {
            if (mNoiseMeterBle == null) {
                Log.e("ljl", "NoiseMeterBleDevice绑定设备");
                NoiseMeterBleDevice.init(mBleDevice);
                mNoiseMeterBle = NoiseMeterBleDevice.getInstance();
                mNoiseMeterBle.setOnNoiseDataListener(this);
                //
            }
        }
        addText("设备连接成功" + mMac);
    }

    @Override
    public void onServiceErr() {
    }

    @Override
    public void unbindServices() {
    }


    /**
     * 添加一条文本
     *
     * @param text 文本
     */
    private void addText(String text) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
        mList.add(sdf.format(System.currentTimeMillis()) + "：\n" + text);
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        }
        if (list_view != null) {
            list_view.smoothScrollToPosition(mList.size() - 1);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_noise_ble_ac:
                //A/C键功能
                if (mNoiseMeterBle != null) {
                    mNoiseMeterBle.sendAC();
                }
                break;
            case R.id.btn_noise_ble_light:
                //LIGHT键
                if (mNoiseMeterBle != null) {
                    mNoiseMeterBle.sendLight();
                }
                break;
            case R.id.btn_noise_ble_fs:
                //F/S键
                if (mNoiseMeterBle != null) {
                    mNoiseMeterBle.sendFS();
                }
                break;
            case R.id.btn_noise_ble_max:
                //MAX键
                if (mNoiseMeterBle != null) {
                    mNoiseMeterBle.sendMax();
                }
                break;
            case R.id.btn_noise_ble_up:
                //UP键
                if (mNoiseMeterBle != null) {
                    mNoiseMeterBle.sendUp();
                }
                break;
            case R.id.btn_noise_ble_down:
                //DOWN键
                if (mNoiseMeterBle != null) {
                    mNoiseMeterBle.sendDown();
                }
                break;
            default:
                break;

        }
    }

    @Override
    public void onBleData(int noiseData, int over, int under, int fs, int ac, int backlight, int max, int lowBat, int range) {
        addText("噪音数据：" + noiseData + "   OVER指示：" + over + "--" + (+over == 0 ? "非OVER状态" : "OVER状态")
                + "   UNDER指示：" + under + "--" + (under == 0 ? "非UNDER状态" : "UNDER状态")
                + "   F/S模式：" + fs + "--" + (fs == 0 ? "Fast模式" : "Slow模式")
                + "   A/C加权：" + ac + "--" + (ac == 0 ? "A加权" : "C加权")
                + "   背光：" + backlight + "--" + (backlight == 0 ? "背光disable" : "背光enable")
                + "   最大值标志：" + max + "--" + (max == 0 ? "最大值disable" : "最大值enable")
                + "   低电指示：" + lowBat + "--" + (lowBat == 0 ? "非低电状态" : "低电状态")
                + "   量程状态：" + range + "--" + select(range));

    }

    private String select(int range) {
        String str = "";
        if (range == 0) {
            str = "30~130";
            return str;
        } else if (range == 1) {
            str = "30~80";
            return str;
        } else if (range == 2) {
            str = "50~100";
            return str;
        } else if (range == 3) {
            str = "60~110";
            return str;
        } else if (range == 4) {
            str = "80~130";
            return str;
        }
        return str;
    }
}

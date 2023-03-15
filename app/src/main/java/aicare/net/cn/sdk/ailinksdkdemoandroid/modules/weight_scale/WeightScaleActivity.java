package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.weight_scale;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;

import com.pingwang.bluetoothlib.device.BleDevice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.weightscale.WeightScaleDevice;

/**
 * @auther ljl
 * on 2023/3/3
 */
public class WeightScaleActivity extends BleBaseActivity implements WeightScaleDevice.OnWeightScaleDataListener, View.OnClickListener {
    private String mMac;
    private BleDevice mBleDevice;

    private ListView list_view;

    private List<String> mList;
    private ArrayAdapter mListAdapter;

    private WeightScaleDevice mWeightScaleDevice;

    private Button btn_weight_battery, btn_weight_sync_time,btn_weight_query_unit;

    private RadioButton kg, jing, stlb, lb;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_scale);
        initView();
        mMac = getIntent().getStringExtra("mac");
        mList = new ArrayList<>();
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        list_view.setAdapter(mListAdapter);
    }

    private void initView() {
        list_view = findViewById(R.id.list_view);
        btn_weight_battery = findViewById(R.id.btn_weight_battery);
        btn_weight_battery.setOnClickListener(this);
        btn_weight_sync_time = findViewById(R.id.btn_weight_sync_time);
        btn_weight_sync_time.setOnClickListener(this);
        btn_weight_query_unit = findViewById(R.id.btn_weight_query_unit);
        btn_weight_query_unit.setOnClickListener(this);

        kg = findViewById(R.id.kg);
        jing = findViewById(R.id.jin);
        stlb = findViewById(R.id.st_lb);
        lb = findViewById(R.id.lb);
        kg.setChecked(true);
        setUnitinit();
    }

    private void setUnitinit() {
        kg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mWeightScaleDevice != null) {
                        mWeightScaleDevice.setScaleUnit(0);
                    }
                }
            }
        });
        jing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mWeightScaleDevice != null) {
                        mWeightScaleDevice.setScaleUnit(1);
                    }
                }
            }
        });
        stlb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mWeightScaleDevice != null) {
                        mWeightScaleDevice.setScaleUnit(4);
                    }
                }
            }
        });
        lb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mWeightScaleDevice != null) {
                        mWeightScaleDevice.setScaleUnit(6);
                    }
                }
            }
        });
    }


    @Override
    public void onServiceSuccess() {
        mBleDevice = mBluetoothService.getBleDevice(mMac);
        if (mBleDevice != null) {
            if (mWeightScaleDevice == null) {
                WeightScaleDevice.init(mBleDevice);
                mWeightScaleDevice = WeightScaleDevice.getInstance();
                mWeightScaleDevice.setOnWeightScaleDataListener(this);
                mWeightScaleDevice.getBattery();
                mWeightScaleDevice.setScaleUnit(0);
            }

        }
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


    /**
     * 实时重量
     *
     * @param realStatus
     * @param realWeight
     * @param realPoint
     * @param realUnit
     */
    @Override
    public void onRealWeightData(int realStatus, int realWeight, int realPoint, int realUnit) {
        if (realUnit == 4) {
            addText("实时重量：" + (int) LbToSt(getWeight(realWeight, realPoint))[0] + ":" + LbToSt(getWeight(realWeight, realPoint))[1] + "  单位：" + getUnit(realUnit));
        } else {
            addText("实时重量：" + getWeight(realWeight, realPoint) + "  单位：" + getUnit(realUnit));
        }
    }

    private String getUnit(int unit) {
        switch (unit) {
            default:
            case 0:
                return "kg";
            case 1:
                return "斤";
            case 4:
                return "st:lb";
            case 6:
                return "lb";
        }
    }

    private float getWeight(int weight, int point) {
        return (float) (weight / Math.pow(10, point));
    }

    /**
     * lb转st:lb
     */
    public static float[] LbToSt(float lbSize) {
        float[] lbFloatS = new float[2];
        int st = (int) lbSize / 14;
        float lb = lbSize % 14f;
        lbFloatS[0] = st;
        lbFloatS[1] = lb;
        return lbFloatS;
    }

    /**
     * 稳定重量
     *
     * @param stableStatus
     * @param stableWeight
     * @param stablePoint
     * @param stableUnit
     */
    @Override
    public void onStableWeightData(int stableStatus, int stableWeight, int stablePoint, int stableUnit) {
        if (stableUnit == 4) {
            addText("稳定重量：" + (int) LbToSt(getWeight(stableWeight, stablePoint))[0] + ":" + LbToSt(getWeight(stableWeight, stablePoint))[1] + "  单位：" + getUnit(stableUnit));
        } else {
            addText("稳定重量：" + getWeight(stableWeight, stablePoint) + "  单位：" + getUnit(stableUnit));
        }
    }

    @Override
    public void onDataA6(String A6DataStr) {
        addText("收到的A6 PayLoad:[" + A6DataStr + "]");
    }

    @Override
    public void onDataA7(String A7DataStr) {
        addText("收到的A7 PayLoad:[" + A7DataStr + "]");
    }

    @Override
    public void onMeasureOk() {
        addText("测量完成");
    }

    /**
     * bmi值，需要除以10
     *
     * @param bmi
     */
    @Override
    public void onBmi(float bmi) {
        addText("BMI值为：" + bmi);
    }

    /**
     * 设置单位结果 0-成功 1-失败 2-不支持
     *
     * @param unitResult
     */
    @Override
    public void onUnitResult(int unitResult) {
        switch (unitResult) {
            case 0:
                addText("单位设置成功：" + unitResult);
                break;
            case 1:
                addText("单位设置失败：" + unitResult);
                break;
            case 2:
                addText("不支持单位设置：" + unitResult);
                break;
        }
    }

    /**
     * 设备上发的错误码
     *
     * @param errorCode
     */
    @Override
    public void onErrorCode(int errorCode) {
        addText("错误码：" + errorCode);
    }

    @Override
    public void onVersion(String version) {
        addText("版本号：" + version);
    }

    /**
     * 请求同步时间
     *
     * @param quest 1-请求同步时间
     */
    @Override
    public void onSyncTime(int quest) {
        if (quest == 1) {
            if (mWeightScaleDevice != null) {
                mWeightScaleDevice.appSyncTime();
            }
        }
    }

    @Override
    public void onBattery(int status, int battery) {
        addText("电量：" + battery + "%  " + "状态码：" + status);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_weight_battery) {
            if (mWeightScaleDevice != null) {
                mWeightScaleDevice.getBattery();
            }
        } else if (view.getId() == R.id.btn_weight_sync_time) {
            if (mWeightScaleDevice != null) {
                mWeightScaleDevice.appSyncTime();
            }
        }else if (view.getId()==R.id.btn_weight_query_unit){
            if (mWeightScaleDevice != null) {
                mWeightScaleDevice.queryUnit();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mWeightScaleDevice != null) {
            mWeightScaleDevice = null;
        }
        if (mBluetoothService != null) {
            mBluetoothService.disconnectAll();
        }
        super.onDestroy();
    }
}

package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.ble_nutrition;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BleDevice;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.BleNutrition.BleNutritionData;

public class BleNutritionActivity extends BleBaseActivity implements View.OnClickListener, BleNutritionData.BleNutritionCallback {

    private static final String TAG = "Tag1";

    private Button btn_clear;
    private ListView list_view;
    private Button btn_set_unit;
    private Button btn_set_zero;
    private List<RadioButton> rb_list;

    private List<String> mList;
    private ArrayAdapter mListAdapter;

    private String mMac;
    private BleDevice mBleDevice;

    private BleNutritionData mBleNutritionData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_nutrition);

        btn_clear = findViewById(R.id.btn_clear);
        list_view = findViewById(R.id.list_view);
        btn_set_unit = findViewById(R.id.btn_set_unit);
        btn_set_zero = findViewById(R.id.btn_set_zero);

        btn_clear.setOnClickListener(this);
        btn_set_unit.setOnClickListener(this);
        btn_set_zero.setOnClickListener(this);

        rb_list = new ArrayList<>();
        rb_list.add(findViewById(R.id.rb_g));
        rb_list.add(findViewById(R.id.rb_ml));
        rb_list.add(findViewById(R.id.rb_lb_oz));
        rb_list.add(findViewById(R.id.rb_oz));
        rb_list.add(findViewById(R.id.rb_kg));
        rb_list.add(findViewById(R.id.rb_jin));
        rb_list.add(findViewById(R.id.rb_milk_ml));
        rb_list.add(findViewById(R.id.rb_water_ml));
        rb_list.add(findViewById(R.id.rb_milk_fl_oz));
        rb_list.add(findViewById(R.id.rb_water_fl_oz));
        rb_list.add(findViewById(R.id.rb_lb));

        // 单位只能单选
        for (RadioButton radioButton : rb_list) {
            radioButton.setOnClickListener(v -> {
                for (RadioButton rb : rb_list) {
                    rb.setChecked(rb == v);
                }
            });
        }

        // 获取Mac
        mMac = getIntent().getStringExtra("mac");

        // 初始化列表
        mList = new ArrayList<>();
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        list_view.setAdapter(mListAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_clear) {
            // 清空文本框
            clearText();
        } else if (id == R.id.btn_set_unit) {
            // 设置单位
            setUnit();
        } else if (id == R.id.btn_set_zero) {
            // 去皮指令
            setZero();
        }
    }

    @Override
    protected void onDestroy() {
        if (mBluetoothService != null) {
            mBluetoothService.disconnectAll();
        }
        super.onDestroy();
    }

    @Override
    public void onServiceSuccess() {
        mBleDevice = mBluetoothService.getBleDevice(mMac);
        if (mBleDevice != null) {
            mBleNutritionData = new BleNutritionData(mBleDevice);
            mBleNutritionData.setBleNutritionCallback(this);
            addText("连接成功：" + mMac);
        }
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }

    @Override
    public void mcuBmVersion(String version) {
        addText("MCU上发BM版本：" + version);
    }

    @Override
    public void mcuSupportUnit(List<SupportUnitBean> list) {
        String str = "";
        for (SupportUnitBean supportUnitBean : list) {
            str += supportUnitBean.toString() + "；";
            if (supportUnitBean.getType() != null && supportUnitBean.getType().equals("8")) {
                // 遍历所有单位，有这个支持单位就显示
                for (int i = 0; i < rb_list.size(); i++) {
                    boolean hasUnit = false;
                    for (Integer integer : supportUnitBean.getSupportUnit()) {
                        if (integer != null && integer == i) {
                            hasUnit = true;
                            break;
                        }
                    }
                    rb_list.get(i).setEnabled(hasUnit);
                }
                // 再次遍历，如果选中了不支持的单位，就重新选择
                boolean unitDisable = false;
                for (int i = 0; i < rb_list.size(); i++) {
                    if (!rb_list.get(i).isEnabled() && rb_list.get(i).isChecked()) {
                        unitDisable = true;
                        rb_list.get(i).setChecked(false);
                    }
                    if (rb_list.get(i).isEnabled() && unitDisable) {
                        unitDisable = false;
                        rb_list.get(i).setChecked(true);
                    }
                }
            }
        }
        addText("MCU上发支持单位列表：\n" + str);
    }

    @Override
    public void mcuWeight(int no, int weight, int unit, int decimal, int symbol, int type) {

        float w = weight;
        // 正负
        if (symbol == 1) {
            w *= -1;
        }
        // 小数点
        w = (float) (w / Math.pow(10, decimal));
        // 保留小数位
        String weightStr = getPreFloatStr(w, decimal);

        String str = "MCU上发重量：" + weightStr + getUnitStr(unit) + "\n流水号：" + no + "，原始重量：" + weight + "，单位：" + unit + "，小数点：" + decimal + "，符号：" + symbol + "，重量类型：" + type;
        addText(str);
    }

    @Override
    public void mcuUnitResult(int status) {
        String statusStr = "";
        switch (status) {
            case 0:
                statusStr = "成功";
                break;
            case 1:
                statusStr = "失败";
                break;
            case 2:
                statusStr = "不支持";
                break;
        }
        String str = "MCU上发设置单位结果：" + statusStr;
        addText(str);
    }

    @Override
    public void mcuErr(int weightErr, int batteryErr) {
        String weightErrStr = "";
        switch (weightErr) {
            case 0:
                weightErrStr = "正常";
                break;
            case 1:
                weightErrStr = "超重";
                break;
        }
        String batteryErrStr = "";
        switch (batteryErr) {
            case 0:
                batteryErrStr = "正常";
                break;
            case 1:
                batteryErrStr = "低电";
                break;
        }
        String str = "MCU上发异常报警：\n重量状态：" + weightErrStr + "\n电池状态：" + batteryErrStr;
        addText(str);
    }

    private SimpleDateFormat sdf;

    // 添加一条文本
    private void addText(String text) {
        if (sdf == null) {
            sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
        }
        mList.add(sdf.format(System.currentTimeMillis()) + "：\n" + text);
        mListAdapter.notifyDataSetChanged();
        list_view.smoothScrollToPosition(mList.size() - 1);
    }

    // 清空文本
    private void clearText() {
        mList.clear();
        mListAdapter.notifyDataSetChanged();
    }

    // 保留小数位
    private String getPreFloatStr(float f, int decimal) {
        BigDecimal bigDecimal = new BigDecimal(f);
        return bigDecimal.setScale(decimal, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * APP 发送归零指令
     */
    private void setZero() {
        if (mBleNutritionData != null) {
            mBleNutritionData.setZero();
            addText("APP下发去皮指令");
        }
    }

    /**
     * APP 下发单位
     */
    private void setUnit() {
        if (mBleNutritionData != null) {
            int unit = -1;
            for (int i = 0; i < rb_list.size(); i++) {
                if (rb_list.get(i).isChecked()) {
                    unit = i;
                    break;
                }
            }
            if (unit == -1) {
                addText("APP下发单位：失败：未选择单位");
                return;
            }
            mBleNutritionData.setUnit(unit);
            addText("APP下发单位：" + unit + "：" + getUnitStr(unit));
        }
    }

    /**
     * 获取单位字符串
     *
     * @param unit 0
     * @return g
     */
    private String getUnitStr(int unit) {
        String unitStr = "";
        switch (unit) {
            case 0x00:
                unitStr = "g";
                break;
            case 0x01:
                unitStr = "ml";
                break;
            case 0x02:
                unitStr = "lb:oz";
                break;
            case 0x03:
                unitStr = "oz";
                break;
            case 0x04:
                unitStr = "kg";
                break;
            case 0x05:
                unitStr = "斤";
                break;
            case 0x06:
                unitStr = "牛奶ml";
                break;
            case 0x07:
                unitStr = "水ml";
                break;
            case 0x08:
                unitStr = "牛奶floz";
                break;
            case 0x09:
                unitStr = "水floz";
                break;
            case 0x0A:
                unitStr = "lb";
                break;
        }
        return unitStr;
    }
}

package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.blood_pressure_tc;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BleDevice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.Transmission.TransmissionDeviceData;

/**
 * 血压计透传测试
 */
public class BloodPressureTcActivity extends BleBaseActivity implements TransmissionDeviceData.MyBleCallback {

    private ListView list_view;

    private List<String> mList;
    private ArrayAdapter mListAdapter;

    private String mMac;
    private BleDevice mBleDevice;
    private TransmissionDeviceData mTransmissionDeviceData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure_tc);

        list_view = findViewById(R.id.list_view);

        mList = new ArrayList<>();
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        list_view.setAdapter(mListAdapter);

        // 获取Mac
        mMac = getIntent().getStringExtra("mac");
    }

    @Override
    public void onServiceSuccess() {
        mBleDevice = mBluetoothService.getBleDevice(mMac);
        if (mBleDevice != null) {
            mTransmissionDeviceData = new TransmissionDeviceData(mBleDevice);
            mTransmissionDeviceData.setMyBleCallback(this);
            addText("设备连接成功：" + mMac);
        }
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }

    @Override
    public void onVersion(String version) {

    }

    @Override
    public void showData(String data, int type) {
        addText("收到Payload数据：\nCID：" + type + "\n数据：" + data);
    }

    @Override
    public void onSupportUnit(List<SupportUnitBean> list) {

    }

    @Override
    public void onCid(int cid, int vid, int pid) {

    }

    @Override
    public void otherData(byte[] hex, String data) {
        addText("收到透传数据：\n" + data);

        if (hex != null && hex.length == 10 && (hex[0] & 0xFF) == 0xD6 && (hex[9] & 0xFF) == 0xEC) {
            int highPressure = ((hex[2] & 0xFF) << 8) + (hex[3] & 0xFF);
            int lowPressure = (hex[4] & 0xFF);
            int bp = (hex[5] & 0xFF);
            int status = (hex[6] & 0xFF);
            int batteryStatus = (hex[7] & 0xFF);

            String str = "高压：" + highPressure + "\n低压：" + lowPressure + "\n心率：" + bp + "\n状态：";
            switch (status) {
                case 1:
                    str += "正常";
                    break;
                case 2:
                    str += "偏高";
                    break;
                case 3:
                    str += "严重偏高";
                    break;
                case 4:
                    str += "严重错误";
                    break;
                default:
                    str += "未知：" + status;
                    break;
            }
            str += "\n电量状态：";
            switch (batteryStatus) {
                case 0:
                    str += "正常";
                    break;
                case 1:
                    str += "偏低";
                    break;
                default:
                    str += "未知：" + status;
                    break;
            }
            addText(str);
        }
    }

    @Override
    public void sendData(String data) {

    }

    private SimpleDateFormat mSdf;

    // 添加一条文本
    private void addText(String text) {
        if (mSdf == null) {
            mSdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
        }
        mList.add(mSdf.format(System.currentTimeMillis()) + "：\n" + text);
        mListAdapter.notifyDataSetChanged();
        list_view.smoothScrollToPosition(mList.size() - 1);
    }
}

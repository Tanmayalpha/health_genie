package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.broadcast_nutrition;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.listener.OnScanFilterListener;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;
import com.pinwang.ailinkble.AiLinkPwdUtil;
import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BroadNutritionActivity extends BleBaseActivity implements View.OnClickListener, OnScanFilterListener {

    private ListView list_view;
    private Button btn_start;
    private Button btn_stop;
    private Button btn_clear;

    private List<String> mList;
    private ArrayAdapter mListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_nutrition);

        list_view = findViewById(R.id.list_view);
        btn_start = findViewById(R.id.btn_start);
        btn_stop = findViewById(R.id.btn_stop);
        btn_clear = findViewById(R.id.btn_clear);

        btn_start.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_clear.setOnClickListener(this);

        mList = new ArrayList<>();
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        list_view.setAdapter(mListAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_start) {
            mBluetoothService.scanLeDevice(0, BleConfig.UUID_SERVER_BROADCAST_AILINK);
        } else if (id == R.id.btn_stop) {
            mBluetoothService.stopScan();
        } else if (id == R.id.btn_clear) {
            clearText();
        }
    }

    @Override
    protected void onDestroy() {
        if (mBluetoothService != null) {
            mBluetoothService.stopScan();
        }
        super.onDestroy();
    }

    @Override
    public void onServiceSuccess() {
        mBluetoothService.setOnScanFilterListener(this);
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }

    @Override
    public boolean onFilter(BleValueBean bleValueBean) {
        if (bleValueBean.getCid() == 4) {
            return true;
        }
        return false;
    }

    private String mMac;

    @Override
    public void onScanRecord(BleValueBean bleValueBean) {
        byte[] manufacturerData = bleValueBean.getManufacturerData();
        mMac = bleValueBean.getMac();
        onNotifyData(manufacturerData, bleValueBean.getCid(), bleValueBean.getVid(), bleValueBean.getPid());
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

    // 清空文本
    private void clearText() {
        mList.clear();
        mListAdapter.notifyDataSetChanged();
    }

    private long mOldNumberId;

    /**
     * @param manufacturerData 自定义厂商数据0xFF后面的数据
     * @param cid              cid 设备类型
     * @param vid              vid
     * @param pid              pid
     */
    public void onNotifyData(byte[] manufacturerData, int cid, int vid, int pid) {
        if (manufacturerData == null) {
            BleLog.i("Tag1", "接收到的数据:null");
            return;
        }
//        if (manufacturerData.length >= 20) {
        byte sum = manufacturerData[9];
        byte[] data = new byte[10];
        System.arraycopy(manufacturerData, 10, data, 0, data.length);
        byte newSum = cmdSum(data);
        if (newSum == sum) {
            int numberId = data[0] & 0xff;//数据ID
//                if (mOldNumberId == numberId) {
//                    //数据相同,已处理过了.不再处理
//                    return;
//                }
            mOldNumberId = numberId;
            byte[] bytes;
            if (cid != 0 || vid != 0 || pid != 0) {
                bytes = AiLinkPwdUtil.decryptBroadcast(cid, vid, pid, data);
            } else {
                bytes = data;
            }
            Log.i("Tag1", "接收到的数据:" + BleStrUtils.byte2HexStr(bytes));
            notifyData(bytes);
        } else {
            Log.i("Tag1", "校验和错误");
        }
//        }
    }

    /**
     * 校验累加,从1开始加
     */
    private byte cmdSum(byte[] data) {
        byte sum = 0;
        for (byte datum : data) {
            sum += datum;
        }
        return sum;
    }

    /**
     * 解析数据
     *
     * @param hex payloads
     */
    private void notifyData(byte[] hex) {
        if (hex.length >= 10) {
            int no = hex[0] & 0xff;
            int type = hex[1] & 0xff;
            int weight = ((hex[2] & 0xff) << 16) + ((hex[3] & 0xff) << 8) + (hex[4] & 0xff);
            int unit = (hex[5] & 0xff) & 0x0f;
            int decimal = ((hex[5] & 0xff) & 0x70) >> 4;
            int symbol = (hex[5] & 0xff) >> 7;
            int battery = hex[6] & 0xff;
            int err = hex[7] & 0xff;

            float weightValue = weight;
            if (symbol == 1) {
                weightValue *= -1;
            }
            weightValue /= Math.pow(10, decimal);
            String weightStr = getPreFloatStr(weightValue, decimal);
            switch (unit) {
                default:
                case 0:
                    weightStr += "g";
                    break;
                case 1:
                    weightStr += "ml";
                    break;
            }

            addText("Mac：" + mMac + "\n流水号：" + no + "\n测量标识符：" + type + "\n原始重量：" + weight + "，单位：" + unit + "，小数点：" + decimal + "，正负：" + symbol + "\n重量：" + weightStr + "\n电量：" + battery + "\n异常标志位：" + err);
        }
    }

    /**
     * 四舍五入
     *
     * @param f 小数
     * @return float
     */
    protected String getPreFloatStr(float f, int decimal) {
        BigDecimal dc = new BigDecimal(f);
        return dc.setScale(decimal, BigDecimal.ROUND_HALF_UP).toString();
    }
}

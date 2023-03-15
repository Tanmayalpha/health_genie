package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.leaone_broadcast;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.listener.OnScanFilterListener;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import androidx.annotation.Nullable;

public class LeaOneBroadcastActivity extends BleBaseActivity implements View.OnClickListener, OnScanFilterListener {

    private ListView list_view;
    private Button btn_start;
    private Button btn_stop;
    private Button btn_clear;
    private TextView tv_mac_system;
    private TextView tv_mac_broadcast;

    private List<String> mList;
    private ArrayAdapter mListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaone_broadcast);

        list_view = findViewById(R.id.list_view);
        btn_start = findViewById(R.id.btn_start);
        btn_stop = findViewById(R.id.btn_stop);
        btn_clear = findViewById(R.id.btn_clear);
        tv_mac_system = findViewById(R.id.tv_mac_system);
        tv_mac_broadcast = findViewById(R.id.tv_mac_broadcast);

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
            mBluetoothService.scanLeDevice(0);
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
        return true;
    }

    private String mMac;

    @Override
    public void onScanRecord(BleValueBean bleValueBean) {
        mMac = bleValueBean.getMac();
        byte[] manufacturerData = bleValueBean.getManufacturerData();
        onNotifyData(manufacturerData);
    }

    private SimpleDateFormat mSdf;

    // 添加一条文本
    private void addText(String text) {
        if (mSdf == null) {
            mSdf = new SimpleDateFormat("HH:mm:ss:SSS", Locale.US);
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

    /**
     * 解析数据
     *
     * @param hex hex
     */
    private void onNotifyData(byte[] hex) {
        // 长度为 15 个 byte，C0 开头
        if (hex == null || hex.length != 15 || (hex[0] & 0xFF) != 0xC0) {
            return;
        }
        byte[] macByte = new byte[6];
        System.arraycopy(hex, 9, macByte, 0, macByte.length);
        String macStr = BleStrUtils.byte2HexStr(macByte).trim().replace(" ", ":").toUpperCase();

        addText(macStr + "\n" + BleStrUtils.byte2HexStr(hex));

        tv_mac_system.setText("系统解析Mac：" + mMac.toUpperCase());
        tv_mac_broadcast.setText("广播解析Mac：" + macStr);
    }

}

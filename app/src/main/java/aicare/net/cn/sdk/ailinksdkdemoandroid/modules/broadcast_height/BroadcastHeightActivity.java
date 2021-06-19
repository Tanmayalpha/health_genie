package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.broadcast_height;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pingwang.bluetoothlib.BleBaseActivity;
import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.listener.OnCallbackDis;
import com.pingwang.bluetoothlib.listener.OnScanFilterListener;
import com.pingwang.bluetoothlib.utils.BleStrUtils;
import com.pinwang.ailinkble.AiLinkPwdUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import cn.net.aicare.modulelibrary.module.BroadcastScale.BroadcastScaleBleConfig;

/**
 * 广播身高仪
 */
public class BroadcastHeightActivity extends BleBaseActivity implements OnCallbackDis, OnScanFilterListener {

    private Context mContext;

    private ListView list_view;

    private List<String> mList;
    private ArrayAdapter mListAdapter;

    private String mMac;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_height);
        mContext = this;

        list_view = findViewById(R.id.list_view);

        // 初始化列表
        mList = new ArrayList<>();
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        list_view.setAdapter(mListAdapter);
    }

    @Override
    public void onServiceSuccess() {
        // 绑定服务成功，检查权限
        checkPermission();
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

    /**
     * 请求权限的Code
     */
    public static final int REQUEST_PERMISSION_CODE = 1500;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            checkPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            checkPermission();
        }
    }

    @Override
    public void onScanRecord(BleValueBean bleValueBean) {
        if (TextUtils.isEmpty(mMac) && bleValueBean.isBroadcastModule()) {
            // 是广播模块
            if (bleValueBean.getCid() == 0x03) {
                // 是身高仪，进行数据解析
                notifyData(bleValueBean.getManufacturerData(), bleValueBean.getCid(), bleValueBean.getVid(), bleValueBean.getPid(), bleValueBean.getMac());
            }
        }
    }

    /**
     * @param manufacturerData 自定义厂商数据0xFF后面的数据
     * @param cid              cid 设备类型
     * @param vid              vid
     * @param pid              pid
     */
    private void notifyData(byte[] manufacturerData, int cid, int vid, int pid, String mac) {
        if (manufacturerData == null) {
            addText("接收到的数据:null");
            return;
        }
        if (manufacturerData.length >= 20) {
            byte sum = manufacturerData[9];
            byte[] data = new byte[10];
            System.arraycopy(manufacturerData, 10, data, 0, data.length);
            byte newSum = cmdSum(data);
            if (newSum == sum) {
//                addText("接收到的数据:原始数据:" + BleStrUtils.byte2HexStr(data));
                byte[] bytes;
                byte[] dataOriginal = data.clone();
                if (cid != 0 || vid != 0 || pid != 0) {
                    //数据需要解密
                    if (cid == BroadcastScaleBleConfig.BROADCAST_SCALE_LING_YANG_CID) {
                        bytes = AiLinkPwdUtil.decryptLingYang(data);
                    } else {
                        bytes = AiLinkPwdUtil.decryptBroadcast(cid, vid, pid, data);
                    }
                } else {
                    bytes = data;
                }

//                addText("接收到的数据:" + BleStrUtils.byte2HexStr(bytes));
                checkData(bytes, cid, vid, pid, mac);
            } else {
                addText("校验和错误");
            }
        }
    }

    // 上一次的流水号
    private int mLastSerialNum = -1;

    /**
     * 解析数据
     *
     * @param hex 解密后的数据
     * @param cid cid
     * @param vid vid
     * @param pid pid
     */
    private void checkData(byte[] hex, int cid, int vid, int pid, String mac) {
        // 流水号
        int serialNum = hex[0];
        if (serialNum == mLastSerialNum) {
            return;
        }
        mLastSerialNum = serialNum;

        // 测量标识
        int flag = hex[1] & 0xff;
        // 身高原始数据
        int heightOrigin = (hex[2] & 0xff) << 8 | (hex[3] & 0xff);
        // 身高单位 0：cm；1：inch；2：ft-in
        int heightUnit = (hex[4] & 0xff) & 0x0f;
        // 身高小数点
        int heightDecimal = ((hex[4] & 0xff) & 0xf0) >> 4;
        // 体重原始数据
        int weightOrigin = (hex[5] & 0xff) << 8 | (hex[6] & 0xff);
        // 体重单位 0：kg；1：斤；2：lb:oz；3：oz；4：st:lb；5：g；6：lb
        int weightUnit = (hex[7] & 0xff) & 0x0f;
        // 体重正负号
        int weightSymbol = ((hex[7] & 0xff) & 0x10) >> 4;
        // 体重小数点
        int weightDecimal = ((hex[7] & 0xff) & 0xe0) >> 5;
        // 电量
        int battery = hex[8] & 0xff;

        // 输出
        String text = "";
        text += "MAC地址：" + mac;
        text += "\n数据解析：" + BleStrUtils.byte2HexStr(hex);
        text += "\n测量标识：" + flag + "：" + getFlagStr(flag);
        text += "\n身高原始数据：" + heightOrigin;
        text += "\n身高单位：" + heightUnit;
        text += "\n身高小数点：" + heightDecimal;
        text += "\n身高最终值：" + getHeightStr(heightOrigin, heightUnit, heightDecimal);

        if (weightOrigin != 0xffff) {
            text += "\n体重原始数据：" + weightOrigin;
            text += "\n体重单位：" + weightUnit;
            text += "\n体重正负号：" + weightSymbol;
            text += "\n体重小数点：" + weightDecimal;
            text += "\n体重最终值：" + getWeightStr(weightOrigin, weightUnit, weightDecimal, weightSymbol);
        } else {
            text += "\n体重：" + "不支持";
        }
        if (battery != 0xff) {
            text += "\n电量：" + battery;
        } else {
            text += "\n电量：" + "不支持";
        }
        addText(text);
    }

    private String getFlagStr(int flag) {
        String str = "";
        switch (flag) {
            case 0:
                str = "正在测量";
                break;
            case 1:
                str = "稳定身高体重";
                break;
            case 0xff:
                str = "测量失败";
                break;
        }
        return str;
    }

    private String getHeightStr(int heightOrigin, int heightUnit, int heightDecimal) {
        String str = "";
        str += getPreFloatStr((float) (heightOrigin * 1.0f / Math.pow(10, heightDecimal)), heightDecimal, 0);

        switch (heightUnit) {
            case 0:
                str += "cm";
                break;
            case 1:
                str += "inch";
                break;
            case 2:
                try {
                    int ft = Integer.parseInt(str);
                    int f = ft / 12;
                    int i = ft - f * 12;
                    str = f + "'" + i + "\"";
                } catch (Exception e) {
                    str = "解析异常，原始值不是int型";
                }
                break;
        }

        return str;
    }

    private String getWeightStr(int weightOrigin, int weightUnit, int weightDecimal, int weightSymbol) {
        String str = "";
        str += getPreFloatStr((float) (weightOrigin * 1.0f / Math.pow(10, weightDecimal)) * (weightSymbol == 1 ? -1 : 1), weightDecimal, 0);

        switch (weightUnit) {
            case 0:
                str += "kg";
                break;
            case 1:
                str += "斤";
                break;
            case 2:
                str += "lb:oz";
                break;
            case 3:
                str += "oz";
                break;
            case 4:
                str += "st:lb";
                break;
            case 5:
                str += "g";
                break;
            case 6:
                str += "lb";
                break;
        }

        return str;
    }

    /**
     * 四舍五入保留一位小数
     *
     * @param f 原来小数
     * @return 保留后的小数
     */
    public static float getPreFloat(float f) {
        return getPreFloat(f, 1, 0);
    }

    /**
     * 保留小数
     *
     * @param f    原始小数
     * @param bit  保留多少位
     * @param type 0 四舍五入，1 去尾，2 银行家， 3 进一
     * @return 保留后的小数
     */
    public static float getPreFloat(float f, int bit, int type) {
        return Float.parseFloat(getPreFloatStr(f, bit, type));
    }

    /**
     * 保留小数，获取字符串
     *
     * @param f    原始小数
     * @param bit  保留多少位
     * @param type 0 四舍五入，1 去尾，2 银行家， 3 进一
     * @return 保留后的小数
     */
    public static String getPreFloatStr(float f, int bit, int type) {
        BigDecimal dc = new BigDecimal(f);

        switch (type) {
            default:
            case 0:
                // 四舍五入
                return dc.setScale(bit, BigDecimal.ROUND_HALF_UP).toString();
            case 1:
                // 去尾
                return dc.setScale(bit, BigDecimal.ROUND_DOWN).toString();
            case 2:
                // 银行家
                return dc.setScale(bit, BigDecimal.ROUND_HALF_EVEN).toString();
            case 3:
                // 进一
                return dc.setScale(bit, BigDecimal.ROUND_UP).toString();
        }
    }

    /**
     * 校验累加,从1开始加
     */
    private static byte cmdSum(byte[] data) {
        byte sum = 0;
        for (byte datum : data) {
            sum += datum;
        }
        return sum;
    }

    /**
     * 检查权限，并开始扫描
     */
    private void checkPermission() {
        // 没有打开蓝牙就请求打开蓝牙
        if (!hasBluetooth()) {
            requestBluetooth();
            return;
        }
        // 没有定位权限就请求定位权限
        if (!hasLocationPermission()) {
            requestLocationPermission(this);
            return;
        }
        // 没有定位服务就请求定位服务
        if (!hasLocationService()) {
            requestLocationService();
            return;
        }
        // 权限都有了，OK
        addText("权限都有，开始接收广播数据");
        if (mBluetoothService != null) {
            mBluetoothService.setOnScanFilterListener(this);
            mBluetoothService.scanLeDevice(0, BleConfig.UUID_SERVER_BROADCAST_AILINK);
        }
    }

    /**
     * 是否有定位权限
     *
     * @return boolean
     */
    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 蓝牙是否打开
     *
     * @return boolean
     */
    private boolean hasBluetooth() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    /**
     * 定位服务是否打开
     *
     * @return boolean
     */
    private boolean hasLocationService() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return false;
        }
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * 申请定位权限
     */
    private void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_CODE);
    }

    /**
     * 申请打开蓝牙
     */
    private void requestBluetooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_PERMISSION_CODE);
    }

    /**
     * 申请打开定位服务
     */
    private void requestLocationService() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, REQUEST_PERMISSION_CODE);
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
}

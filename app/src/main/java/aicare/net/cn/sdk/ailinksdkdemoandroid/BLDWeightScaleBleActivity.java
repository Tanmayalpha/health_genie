package aicare.net.cn.sdk.ailinksdkdemoandroid;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;

import androidx.annotation.Nullable;

import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import cn.net.aicare.modulelibrary.module.BLDBodyfatScale.BLDBodyFatBleUtilsData;
import cn.net.aicare.modulelibrary.module.BLDBodyfatScale.BLDBodyFatDataUtil;
import cn.net.aicare.modulelibrary.module.BLDBodyfatScale.BLDUser;

public class BLDWeightScaleBleActivity extends BleBaseActivity implements View.OnClickListener, OnCallbackBle, BLDBodyFatBleUtilsData.BleBodyFatCallback {
    private String TAG = BLDWeightScaleBleActivity.class.getName();
    private String mAddress;
    private List<String> mlogList;

    private ArrayAdapter listAdapter;

    private BLDBodyFatBleUtilsData bodyFatBleUtilsData;
    private MHandler mMHandler;

    private RadioButton kg, jing, stlb, lb;

    private BLDUser selectUser;

    private ListView loglistView;
    private int weighunit = BLDBodyFatDataUtil.WeightUnit.KG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bld_weight_scale_ble);
        initView();
        setUnitinit();
        initdata();


    }

    private void initView() {

        kg = findViewById(R.id.kg);
        jing = findViewById(R.id.jin);
        stlb = findViewById(R.id.st_lb);
        lb = findViewById(R.id.lb);

        kg.setChecked(true);
        loglistView = findViewById(R.id.log_list);


    }

    private void initdata() {
        mAddress = getIntent().getStringExtra("mac");

        WeakReference weakReference = new WeakReference(new MHandler());
        mMHandler = (MHandler) weakReference.get();

        mlogList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mlogList);
        loglistView.setAdapter(listAdapter);


        selectUser = getdefault();


    }

    private BLDUser getdefault() {
        BLDUser user = new BLDUser();
        user.setModeType(BLDBodyFatDataUtil.UserEum.MODE_ORDINARY);
        user.setSex(BLDBodyFatDataUtil.UserEum.SEX_MAN);
        user.setAge(18);
        user.setHeight(170);
        user.setAdc(560);
        user.setWeight(50);
        user.setId(1);
        return user;
    }

    private void setUnitinit() {
        kg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    weighunit = BLDBodyFatDataUtil.WeightUnit.KG;
                    if (bodyFatBleUtilsData != null)
                        bodyFatBleUtilsData.sendData(BLDBodyFatDataUtil.getInstance().setWeightUnit(
                                BLDBodyFatDataUtil.WeightUnit.KG));
                }
            }
        });
        jing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    weighunit = BLDBodyFatDataUtil.WeightUnit.JIN;
                    if (bodyFatBleUtilsData != null)
                        bodyFatBleUtilsData.sendData(BLDBodyFatDataUtil.getInstance().setWeightUnit(BLDBodyFatDataUtil.WeightUnit.JIN));
                }
            }
        });
        stlb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    weighunit = BLDBodyFatDataUtil.WeightUnit.ST;
                    if (bodyFatBleUtilsData != null)
                        bodyFatBleUtilsData.sendData(BLDBodyFatDataUtil.getInstance().setWeightUnit(BLDBodyFatDataUtil.WeightUnit.ST));
                }
            }
        });
        lb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    weighunit = BLDBodyFatDataUtil.WeightUnit.LB;
                    if (bodyFatBleUtilsData != null)
                        bodyFatBleUtilsData.sendData(BLDBodyFatDataUtil.getInstance().setWeightUnit(BLDBodyFatDataUtil.WeightUnit.LB));
                }
            }
        });
    }

    @Override
    public void onServiceSuccess() {
        mlogList.add(0, "服务与界面建立连接成功");
        mMHandler.sendEmptyMessage(ToRefreUi);
//        BleLog.i(TAG, "服务与界面建立连接成功");
        //与服务建立连接
        if (mBluetoothService != null) {
            mBluetoothService.setOnCallbackBle(this);
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                BLDBodyFatBleUtilsData.init(bleDevice, this);
                bodyFatBleUtilsData = BLDBodyFatBleUtilsData.getInstance();
                bodyFatBleUtilsData.sendData(BLDBodyFatDataUtil.getInstance().getVersion());
                mMHandler.sendEmptyMessageDelayed(GETLIST, 200);
                mMHandler.sendEmptyMessageDelayed(GETLIST, 300);


            }
        }
    }


    @Override
    public void onServiceErr() {
        mlogList.add(0, "服务与界面建立连接出错");
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void unbindServices() {
        mlogList.add(0, "服务与界面建立断开连接成功");
        mMHandler.sendEmptyMessage(ToRefreUi);
        if (mBluetoothService!=null) {
            mBluetoothService.removeOnCallbackBle(this);
        }
    }

    @Override
    public void onStartScan() {
        mlogList.add(0, "开始扫描");
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onScanning(BleValueBean data) {
        mlogList.add(0, "扫描中");
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onScanTimeOut() {
        mlogList.add(0, "扫描超时");
        mMHandler.sendEmptyMessage(ToRefreUi);
    }


    @Override
    public void onConnecting(String mac) {
        mlogList.add(0, "正在连接" + mac);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onDisConnected(String mac, int code) {
        mlogList.add(0, "断开连接" + mac);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onServicesDiscovered(String mac) {
        mlogList.add(0, "发现蓝牙服务" + mac);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void bleOpen() {
        mlogList.add(0, "蓝牙打开");
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void bleClose() {
        mlogList.add(0, "蓝牙关闭");
        mMHandler.sendEmptyMessage(ToRefreUi);
    }


    @Override
    public void onWeightData(int status, int symbol, int weight, int weightUnit, int decimals) {
        String symbolStr = symbol == BLDBodyFatDataUtil.Symbol.MINUS ? "(-)" : "(+)";
        String unitStr = "";
        switch (weightUnit) {
            case BLDBodyFatDataUtil.WeightUnit.JIN:
                unitStr = "斤";
                break;
            case BLDBodyFatDataUtil.WeightUnit.KG:
                unitStr = "Kg";
                break;
            case BLDBodyFatDataUtil.WeightUnit.LB:
                unitStr = "lb";
                break;
            case BLDBodyFatDataUtil.WeightUnit.ST:
                unitStr = "ST:LB(  由于 st 与 lb 是标准整数换算，1st=14lb，故重量数据部分还是 lb 的重量)";
                break;
        }
        unitStr += "(" + weightUnit + ")";
        mlogList.add(0, "体重数据类型：" + status + "  符号:" + symbol + symbolStr
                + " 体重:" + weight + "  单位：" + unitStr + "  小数点位: " + decimals);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onTempData(int symhol, int temp) {
        mlogList.add(0, "温度  符号：" + symhol + " 温度: " + temp + "  (精度(precision) 0.1℃)");
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onStatus(int status) {

        switch (status) {
            case BLDBodyFatDataUtil.WEIGHT_TESTING:
                mlogList.add(0, "测量状态：" + status + " 测量实时体重");
                break;
            case BLDBodyFatDataUtil.WEIGHT_RESULT:
                mlogList.add(0, "测量状态：" + status + " 稳定体重");
                break;
            case BLDBodyFatDataUtil.IMPEDANCE_TESTING:
                mlogList.add(0, "测量状态：" + status + " 阻抗测量中");
                break;
            case BLDBodyFatDataUtil.IMPEDANCE_SUCCESS:
            case BLDBodyFatDataUtil.IMPEDANCE_SUCCESS_DATA:
                mlogList.add(0, "测量状态：" + status + " 阻抗测量成功");
                break;
            case BLDBodyFatDataUtil.IMPEDANCE_FAIL:
                mlogList.add(0, "测量状态：" + status + " 阻抗测量失败");
                break;

            case BLDBodyFatDataUtil.TEST_FINISH:
                mlogList.add(0, "测量状态：" + status + " 测量完成");
                break;
            case BLDBodyFatDataUtil.REQUESR_USER_INFO:
                mlogList.add(0, "测量状态：" + status + " 请求用户信息");
                break;
            case BLDBodyFatDataUtil.TEMPERATURE:
                mlogList.add(0, "测量状态：" + status + " 测量温度");
                break;
            case BLDBodyFatDataUtil.REQUEST_CURRENT_UNIT:
                mlogList.add(0, "测量状态：" + status + " 请求单位单位");
                mMHandler.sendEmptyMessage(SETUNIT);
                break;
            case BLDBodyFatDataUtil.ERROR_CODE:
                mlogList.add(0, "测量状态：" + status + " 错误");

                break;
            default:
                mlogList.add(0, "测量状态：" + status);

        }
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onAdc(int adc, int algorithmic) {
        mlogList.add(0, "阻抗：" + adc);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onBodyFat(int bf, int vfg, int muscleMassKg, int bmv, int Bmr, int age, int bfStandard, int vfgStandard, int muscleMassStandard, int bmvStandard) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("体脂: ");
        stringBuffer.append(bf);
        stringBuffer.append("   精度(precision) 0.1%，范围(scope) 5.0～75.0%");
        stringBuffer.append(" 标准:");
        stringBuffer.append(bfStandard);
        stringBuffer.append("\n内脏脂肪等级: ");
        stringBuffer.append(vfg);
        stringBuffer.append("    精度(precision) 0.5，范围(scope) 1.0～59.0");
        stringBuffer.append(" 标准:");
        stringBuffer.append(vfgStandard);
        stringBuffer.append("\n肌肉量: ");
        stringBuffer.append(muscleMassKg);
        stringBuffer.append("    精度(precision) 0.1kg");
        stringBuffer.append(" 标准:");
        stringBuffer.append(muscleMassStandard);
        stringBuffer.append("\n基础代谢量: ");
        stringBuffer.append(bmv);
        stringBuffer.append("     精度(precision) 1kcal");
        stringBuffer.append(" 标准:");
        stringBuffer.append(bmvStandard);
        stringBuffer.append("\n体水分率: ");
        stringBuffer.append(Bmr);
        stringBuffer.append("      精度(precision) 0.1%");
        stringBuffer.append("\n体内年龄: ");
        stringBuffer.append(age);
        stringBuffer.append("       范围(scope) 18-90");

        mlogList.add(0, stringBuffer.toString());
        mMHandler.sendEmptyMessage(ToRefreUi);
    }


    @Override
    public void onError(int code) {
        mlogList.add(0, "错误码:" + code);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }


    @Override
    public void onVersion(String version) {
        mlogList.add(0, "版本号：" + version);
        mMHandler.sendEmptyMessage(ToRefreUi);

    }

    @Override
    public void onSupportUnit(List<SupportUnitBean> list) {
        String listStr = "";
        for (SupportUnitBean supportUnitBean : list) {
            listStr = supportUnitBean.toString();
        }
        mlogList.add(0, "支持的单位列表：" + listStr);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onMcuBatteryStatus(int status, int battery) {
        mlogList.add(0, "电量状态" + status + " 电量：" + battery);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }


    /**
     * @param status {@link#Bod}
     */
    @Override
    public void setUnitCallback(int status) {
        if (status == BLDBodyFatDataUtil.UnitResult.SUCCESS) {
            mlogList.add(0, "下发单位回调" + status + " 成功");
        } else if (status == BLDBodyFatDataUtil.UnitResult.FAIL) {
            mlogList.add(0, "下发单位回调" + status + " 失败");
        } else {
            mlogList.add(0, "下发单位回调" + status + " 不支持");
        }
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void requestUserData(int status) {
        if (status == BLDBodyFatDataUtil.REQUEST_SEND_USER) {
            mlogList.add(0, "下发用户信息 " + status + " \n" + selectUser.toString());
            bodyFatBleUtilsData.sendData(BLDBodyFatDataUtil.getInstance().setUserInfo(selectUser));
        } else if (status == BLDBodyFatDataUtil.REQUEST_SEND_USER_RECEIVE_SUCESS) {
            mlogList.add(0, "下发用户信息成功 " + status);
        } else {
            mlogList.add(0, "下发用户信息失败 " + status);
        }
        mMHandler.sendEmptyMessage(ToRefreUi);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();


    }

    private final int ToRefreUi = 300;
    private final int GETLIST = 301;
    private final int SETUNIT = 302;


    private class MHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ToRefreUi:
                    if (listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                    }
                    break;
                case GETLIST:
                    if (bodyFatBleUtilsData != null)
                        bodyFatBleUtilsData.sendData(BLDBodyFatDataUtil.getInstance().getUnitSupportUnit());
                    break;

                case SETUNIT:
                    if (bodyFatBleUtilsData != null)
                        bodyFatBleUtilsData.sendData(BLDBodyFatDataUtil.getInstance().setWeightUnit(weighunit));
                    break;
            }
        }
    }


}
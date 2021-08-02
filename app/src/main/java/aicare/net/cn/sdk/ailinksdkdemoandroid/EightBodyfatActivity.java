package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;

import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.EightBodyfatscale.EightBodyFatBleDeviceData;
import cn.net.aicare.modulelibrary.module.EightBodyfatscale.EightBodyFatUtil;

/**
 * 八电极秤
 */
public class EightBodyfatActivity extends BleBaseActivity implements View.OnClickListener, OnCallbackBle, EightBodyFatBleDeviceData.EightBodyFatCallback {

    private List<String> loglist;
    private ListView log_list;
    private ArrayAdapter listAdapter;
    private String mAddress;
    private EightBodyFatBleDeviceData mEightBodyFatBleDeviceData;
    private RadioButton kg, jing, stlb, lb, C, F;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eight_body_fata);
        mAddress = getIntent().getStringExtra("mac");
        init();
        loglist = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, loglist);
        log_list.setAdapter(listAdapter);
    }


    private void init() {
        log_list = findViewById(R.id.log_list);
        kg = findViewById(R.id.kg);
        jing = findViewById(R.id.jin);
        stlb = findViewById(R.id.st_lb);
        lb = findViewById(R.id.lb);
        C = findViewById(R.id.c);
        F = findViewById(R.id.f);
        findViewById(R.id.support_unit).setOnClickListener(this);
        kg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mEightBodyFatBleDeviceData != null)
                        mEightBodyFatBleDeviceData.setWeightUnit(EightBodyFatUtil.KG);

                }
            }
        });
        jing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mEightBodyFatBleDeviceData != null)
                        mEightBodyFatBleDeviceData.setWeightUnit(EightBodyFatUtil.JIN);
                }
            }
        });
        stlb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mEightBodyFatBleDeviceData != null)
                        mEightBodyFatBleDeviceData.setWeightUnit(EightBodyFatUtil.ST);
                }
            }
        });
        lb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mEightBodyFatBleDeviceData != null)
                        mEightBodyFatBleDeviceData.setWeightUnit(EightBodyFatUtil.LB);
                }
            }
        });
        C.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mEightBodyFatBleDeviceData != null)
                        mEightBodyFatBleDeviceData.setTempUnit(EightBodyFatUtil.C);
                }
            }
        });
        F.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mEightBodyFatBleDeviceData != null)
                        mEightBodyFatBleDeviceData.setTempUnit(EightBodyFatUtil.F);
                }
            }
        });
        kg.setChecked(true);
        C.setChecked(true);
    }

    @Override
    public void onServiceSuccess() {
        loglist.add(0, "绑定服务成功");
        if (mBluetoothService != null) {
            mBluetoothService.setOnCallback(this);
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                mEightBodyFatBleDeviceData = new EightBodyFatBleDeviceData(bleDevice);
                mEightBodyFatBleDeviceData.setEightBodyFatCallback(this);
            }
        }

    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.support_unit) {
            if (mEightBodyFatBleDeviceData != null)
                mEightBodyFatBleDeviceData.getUnitList();
        }
    }

    @Override
    public void onState(int type, int typeState, int result) {
        switch (type) {
//            case EightBodyFatUtil.WEIGHING:
//                switch (typeState) {
//                    case EightBodyFatUtil.WEIGHT_REAL_TIME_WEIGH:
//                        loglist.add(0, "实时体重");
//                        break;
//                    case EightBodyFatUtil.WEIGHT_STABILIZATION_WEIGHT:
//                        loglist.add(0, "稳定体重");
//                }
//                break;
            case EightBodyFatUtil.IMPEDANCE:
                String adc = "";
                switch (typeState) {
                    case EightBodyFatUtil.IMPEDANCE_MEASUREMENT:

                        adc = "阻抗测量中";
                        break;
                    case EightBodyFatUtil.IMPEDANCE_SUCCESS:
                        //阻抗测量成功
                        adc = "阻抗测量成功";
                        break;
                    case EightBodyFatUtil.IMPEDANCE_FAILED:
                        adc = "阻抗测量失败";
                        //阻抗测量失败
                        break;
                    case EightBodyFatUtil.IMPEDANCE_FINISH:
                        //阻抗测量完成
                        adc = "阻抗测量完成";
                        break;
                }
                switch (result) {
                    case EightBodyFatUtil.IMPEDANCE_FOOT:
                        adc = adc + "双脚阻抗";
                        break;
                    case EightBodyFatUtil.IMPEDANCE_HAND:
                        adc = adc + "双手阻抗";
                        break;
                    case EightBodyFatUtil.IMPEDANCE_BODY:
                        adc = adc + "躯干阻抗";
                        break;
                    case EightBodyFatUtil.IMPEDANCE_L_HAND:
                        adc = adc + "左手阻抗";
                        break;
                    case EightBodyFatUtil.IMPEDANCE_R_HAND:
                        adc = adc + "右手阻抗";
                        break;
                    case EightBodyFatUtil.IMPEDANCE_L_FOOT:
                        adc = adc + "左脚阻抗";
                        break;
                    case EightBodyFatUtil.IMPEDANCE_R_FOOT:
                        adc = adc + "右脚阻抗";
                        break;
                    case EightBodyFatUtil.IMPEDANCE_L_BODY:
                        adc = adc + "左驱干";
                        break;
                    case EightBodyFatUtil.IMPEDANCE_R_BODY:
                        adc = adc + "右躯干";
                        break;
                    case EightBodyFatUtil.IMPEDANCE_R_HAND_L_FOOT:
                        adc = adc + "右手左脚";
                        break;
                    case EightBodyFatUtil.IMPEDANCE_L_HAND_R_FOOT:
                        adc = adc + "左手右脚";
                        break;
                    default:


                }

                loglist.add(0, adc);
                break;
            case EightBodyFatUtil.HEART_RATE:
                switch (typeState) {
                    case EightBodyFatUtil.HEART_RATE_MEASUREMENT:
                        //心率测量完成
                        loglist.add(0, "心率测量中");
                        break;
                    case EightBodyFatUtil.HEART_RATE_SUCCESS:
                        loglist.add(0, "心率测量成功");
                        //心率测量成功
                        break;
                    case EightBodyFatUtil.HEART_RATE_FAILED:
                        loglist.add(0, "心率测量失败");
                        //心率测量失败
                        break;
                }
                break;
            case EightBodyFatUtil.TEMP_MEASUREMENT:
                loglist.add(0, "测量温度");
                break;
            case EightBodyFatUtil.MEASUREMENT_END:
                loglist.add(0, "测量完成");
                //测量完成
                if (mEightBodyfatAdc!=null)kaimengJieMi(mEightBodyfatAdc);

                break;
            case EightBodyFatUtil.MUC_CALL_BACK_RESULT:
                switch (typeState) {
                    case EightBodyFatUtil.APP_CMD_CALIBRATION:
                        //校验
                        if (result == EightBodyFatUtil.SUCCESS) {
                            loglist.add(0, "校验成功");
                        } else if (result == EightBodyFatUtil.FAILED) {
                            loglist.add(0, "校验失败");
                        } else {
                            loglist.add(0, "校验中");
                        }
                        break;
                    case EightBodyFatUtil.APP_CMD_TEMP_UNIT:
                        if (result == EightBodyFatUtil.SUCCESS) {
                            loglist.add(0, "切换温度成功");
                        } else if (result == EightBodyFatUtil.FAILED) {
                            loglist.add(0, "切换温度失败");
                        } else {
                            loglist.add(0, "切换温度中");
                        }
                        //切换温度
                        break;
                    case EightBodyFatUtil.APP_CMD_WEIGHT_UNIT:
                        if (result == EightBodyFatUtil.SUCCESS) {
                            loglist.add(0, "切换体重单位成功");
                        } else if (result == EightBodyFatUtil.FAILED) {
                            loglist.add(0, "切换体重单位失败");
                        } else {
                            loglist.add(0, "切换体重单位中");
                        }
                        //切换温度
                        break;
                }
                break;
            case EightBodyFatUtil.ERROR_CODE:
                loglist.add(0, "错误码" + typeState);
                break;

        }
        listAdapter.notifyDataSetChanged();

    }

    @Override
    public void onWeight(int state, float weight, int unit, int decimal) {
        //测量状态 1实时体重 2稳定体重
        String stateStr = "";
        if (state == EightBodyFatUtil.WEIGHT_REAL_TIME_WEIGH) {
            stateStr = "实时体重";
        } else if (state == EightBodyFatUtil.WEIGHT_STABILIZATION_WEIGHT) {
            stateStr = "稳定体重";
        }

        if (unit == EightBodyFatUtil.ST) {
            loglist.add(0, "测量状态：" + state + stateStr + "  体重:" + EightBodyFatUtil.lbtostlb(weight) + "  体重单位:" + unit + " 小数点位" + decimal);
        } else {
            loglist.add(0, "测量状态：" + state + stateStr + "  体重:" + weight + "  体重单位:" + unit + " 小数点位" + decimal);
        }
    }

    @Override
    public void onImpedance(int adc, int part, int arithmetic) {
        loglist.add(0, "阻抗:" + adc + "  部位: " + part + "  算法" + arithmetic);
        kaimeng(part,adc);
    }

    @Override
    public void onHeartRate(int heartRate) {
        loglist.add(0, "  心率" + heartRate);
    }

    @Override
    public void onTemp(int sign, float temp, int unit, int decimal) {
        loglist.add(0, "  温度 正负" + sign + "   温度:" + temp + "   单位: " + unit + "  小数位" + decimal);
    }

    @Override
    public void onVersion(String version) {
        loglist.add(0, "当前版本：" + version);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSupportUnit(List<SupportUnitBean> list) {
        loglist.add(0, "支持单位:");
        for (SupportUnitBean supportUnitBean : list) {
            loglist.add(0, supportUnitBean.toString());
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void showData(String data) {
//        loglist.add(0, data);
    }


    private EightBodyfatAdc mEightBodyfatAdc;

    private void kaimeng(int part, int adc) {
        if (mEightBodyfatAdc == null) mEightBodyfatAdc = new EightBodyfatAdc();

        switch (part) {
            case EightBodyFatUtil.IMPEDANCE_FOOT:
                //双脚阻抗
                mEightBodyfatAdc.setAdcFoot(adc);
                break;
            case EightBodyFatUtil.IMPEDANCE_HAND:
                //双手阻抗
                mEightBodyfatAdc.setAdcHand(adc);

                break;
            case EightBodyFatUtil.IMPEDANCE_BODY:
                //躯干阻抗
                mEightBodyfatAdc.setAdcBody(adc);

                break;
            case EightBodyFatUtil.IMPEDANCE_L_HAND:
                //"左手阻抗";
                mEightBodyfatAdc.setAdcLeftHand(adc);
                break;
            case EightBodyFatUtil.IMPEDANCE_R_HAND:
//                adc = adc + "右手阻抗";
                mEightBodyfatAdc.setAdcRightHand(adc);
                break;
            case EightBodyFatUtil.IMPEDANCE_L_FOOT:
//                adc = adc + "左脚阻抗";
                mEightBodyfatAdc.setAdcLeftFoot(adc);

                break;
            case EightBodyFatUtil.IMPEDANCE_R_FOOT:
//                adc = adc + "右脚阻抗";
                mEightBodyfatAdc.setAdcRightFoot(adc);

                break;
            case EightBodyFatUtil.IMPEDANCE_L_BODY:
//                adc = adc + "左驱干";
                mEightBodyfatAdc.setAdcLeftBody(adc);

                break;
            case EightBodyFatUtil.IMPEDANCE_R_BODY:
//                adc = adc + "右躯干";
                mEightBodyfatAdc.setAdcRightBody(adc);

                break;
            case EightBodyFatUtil.IMPEDANCE_R_HAND_L_FOOT:
//                adc = adc + "右手左脚";
                mEightBodyfatAdc.setAdcRightHandLeftFoot(adc);
                break;
            case EightBodyFatUtil.IMPEDANCE_L_HAND_R_FOOT:
//                adc = adc + "左手右脚";
                mEightBodyfatAdc.setAdcLeftHandRightFoot(adc);

                break;
            default:


        }

    }

    private void kaimengJieMi(EightBodyfatAdc mEightBodyfatAdc) {
        loglist.add(0,mEightBodyfatAdc.toString());
        loglist.add(0, "默认传入用户: 性别:男,身高:170,体重 65kg 年龄25");
        EightBodyFatBean algorithmsData = EightBodyFatAlgorithms.getInstance().getAlgorithmsData(1, 1, 170, 65, 25, mEightBodyfatAdc);
        loglist.add(0, algorithmsData.toString());

    }



}

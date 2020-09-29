package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;

import androidx.annotation.Nullable;

import com.pingwang.bluetoothlib.BleBaseActivity;
import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;

import java.util.ArrayList;
import java.util.List;

import cn.net.aicare.modulelibrary.module.EightBodyfatscale.EightBodyFatBleDeviceData;
import cn.net.aicare.modulelibrary.module.EightBodyfatscale.EightBodyfatUtil;

public class EightBodyfatActivity extends BleBaseActivity implements View.OnClickListener, OnCallbackBle, EightBodyFatBleDeviceData.EightBodyFatCallback {

    private List<String> loglist;
    private ListView log_list;
    private ArrayAdapter listAdapter;
    private String mAddress;
    private EightBodyFatBleDeviceData mEightBodyFatBleDeviceData;
    private RadioButton kg, jing, stlb, lb,C,F;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eight_body_fat);
        mAddress = getIntent().getStringExtra("mac");
        init();
        loglist = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, loglist);
        log_list.setAdapter(listAdapter);
    }


    private void init(){
        log_list = findViewById(R.id.log_list);
        kg = findViewById(R.id.kg);
        jing = findViewById(R.id.jin);
        stlb = findViewById(R.id.st_lb);
        lb = findViewById(R.id.lb);
        C=findViewById(R.id.c);
        F=findViewById(R.id.f);
        findViewById(R.id.support_unit).setOnClickListener(this);
        kg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mEightBodyFatBleDeviceData!=null)mEightBodyFatBleDeviceData.setWeightUnit(EightBodyfatUtil.KG);

                }
            }
        });
        jing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mEightBodyFatBleDeviceData!=null)mEightBodyFatBleDeviceData.setWeightUnit(EightBodyfatUtil.JIN);
                }
            }
        });
        stlb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mEightBodyFatBleDeviceData!=null)mEightBodyFatBleDeviceData.setWeightUnit(EightBodyfatUtil.ST);
                }
            }
        });
        lb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mEightBodyFatBleDeviceData!=null)mEightBodyFatBleDeviceData.setWeightUnit(EightBodyfatUtil.LB);
                }
            }
        });
        C.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mEightBodyFatBleDeviceData!=null)mEightBodyFatBleDeviceData.setTempUnit(EightBodyfatUtil.C);
                }
            }
        });
        F.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mEightBodyFatBleDeviceData!=null)mEightBodyFatBleDeviceData.setTempUnit(EightBodyfatUtil.F);
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
        if (v.getId()==R.id.support_unit){
            if(mEightBodyFatBleDeviceData!=null)mEightBodyFatBleDeviceData.getUnitList();
        }
    }

    @Override
    public void onState(int type, int typestate, int result) {
        switch (type) {
            case EightBodyfatUtil.WEIGHING:
                switch (typestate) {
                    case EightBodyfatUtil.WEIGHT_REAL_TIME_WEIGH:
                        loglist.add(0, "实时体重");
                        break;
                    case EightBodyfatUtil.WEIGHT_STABILIZATION_WEIGHT:
                        loglist.add(0, "稳定体重");
                }
                break;
            case EightBodyfatUtil.IMPEDANCE:
                String adc = "";
                switch (typestate) {
                    case EightBodyfatUtil.IMPEDANCE_MEASUREMENT:

                        adc = "阻抗测量中";
                        break;
                    case EightBodyfatUtil.IMPEDANCE_SUCCESS:
                        //阻抗测量成功
                        adc = "阻抗测量成功";
                        break;
                    case EightBodyfatUtil.IMPEDANCE_FAILED:
                        adc = "阻抗测量失败";
                        //阻抗测量失败
                        break;
                    case EightBodyfatUtil.IMPEDANCE_FINISH:
                        //阻抗测量完成
                        adc = "阻抗测量完成";
                        break;
                }
                switch (result) {
                    case EightBodyfatUtil.IMPEDANCE_FOOT:
                        adc = adc + "双脚阻抗";
                        break;
                    case EightBodyfatUtil.IMPEDANCE_HAND:
                        adc = adc + "双手阻抗";
                        break;
                    case EightBodyfatUtil.IMPEDANCE_BODY:
                        adc = adc + "躯干阻抗";
                        break;
                    case EightBodyfatUtil.IMPEDANCE_L_HAND:
                        adc = adc + "左手阻抗";
                        break;
                    case EightBodyfatUtil.IMPEDANCE_R_HAND:
                        adc = adc + "右手阻抗";
                        break;
                    case EightBodyfatUtil.IMPEDANCE_L_FOOT:
                        adc = adc + "左脚阻抗";
                        break;
                    case EightBodyfatUtil.IMPEDANCE_R_FOOT:
                        adc = adc + "右脚阻抗";
                        break;
                    case EightBodyfatUtil.IMPEDANCE_L_BODY:
                        adc = adc + "左驱干";
                        break;
                    case EightBodyfatUtil.IMPEDANCE_R_BODY:
                        adc = adc + "右躯干";
                        break;
                    case EightBodyfatUtil.IMPEDANCE_R_HAND_L_FOOT:
                        adc = adc + "右手左脚";
                        break;
                    case EightBodyfatUtil.IMPEDANCE_L_HAND_R_FOOT:
                        adc = adc + "左手右脚";
                        break;
                    default:


                }

                loglist.add(0, adc);
                break;
            case EightBodyfatUtil.HEART_RATE:
                switch (typestate) {
                    case EightBodyfatUtil.HEART_RATE_MEASUREMENT:
                        //心率测量完成
                        loglist.add(0, "心率测量中");
                        break;
                    case EightBodyfatUtil.HEART_RATE_SUCCESS:
                        loglist.add(0, "心率测量成功");
                        //心率测量成功
                        break;
                    case EightBodyfatUtil.HEART_RATE_FAILED:
                        loglist.add(0, "心率测量失败");
                        //心率测量失败
                        break;
                }
                break;
            case EightBodyfatUtil.TEMP_MEASUREMENT:
                loglist.add(0, "测量温度");
                break;
            case EightBodyfatUtil.MEASUREMENT_END:
                loglist.add(0, "测量完成");
                //测量完成
                break;
            case EightBodyfatUtil.MUC_CALL_BACK_RESULT:
                switch (typestate) {
                    case EightBodyfatUtil.APP_CMD_CALIBRATION:
                        //校验
                        if (result == EightBodyfatUtil.SUCCESS) {
                            loglist.add(0, "校验成功");
                        } else if (result == EightBodyfatUtil.FAILED) {
                            loglist.add(0, "校验失败");
                        } else {
                            loglist.add(0, "校验中");
                        }
                        break;
                    case EightBodyfatUtil.APP_CMD_TEMP_UNIT:
                        if (result == EightBodyfatUtil.SUCCESS) {
                            loglist.add(0, "切换温度成功");
                        } else if (result == EightBodyfatUtil.FAILED) {
                            loglist.add(0, "切换温度失败");
                        } else {
                            loglist.add(0, "切换温度中");
                        }
                        //切换温度
                        break;
                    case EightBodyfatUtil.APP_CMD_WEIGHT_UNIT:
                        if (result == EightBodyfatUtil.SUCCESS) {
                            loglist.add(0, "切换体重单位成功");
                        } else if (result == EightBodyfatUtil.FAILED) {
                            loglist.add(0, "切换体重单位失败");
                        } else {
                            loglist.add(0, "切换体重单位中");
                        }
                        //切换温度
                        break;
                }
                break;
            case EightBodyfatUtil.ERROR_CODE:
                loglist.add(0, "错误码"+typestate);
                break;

        }
        listAdapter.notifyDataSetChanged();

    }

    @Override
    public void onWeight(int state, float weight, int unit, int decimal) {
        if (unit == EightBodyfatUtil.ST) {
            loglist.add(0, "测量状态：" + state + "  体重:" + EightBodyfatUtil.lbtostlb(weight) + "  体重单位:" +unit+ " 小数点位" + decimal);
        } else {
            loglist.add(0, "测量状态：" + state + "  体重:" + weight + "  体重单位:" +unit+ " 小数点位" + decimal);
        }
    }

    @Override
    public void onImpedance(int adc, int part, int arithmetic) {
        loglist.add(0, "阻抗:" + adc + "  部位: " + part + "  算法" + arithmetic);
    }



    @Override
    public void onHeartRate(int heartrate) {
        loglist.add(0, "  心率" + heartrate);
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
        for (SupportUnitBean supportUnitBean:list) {
            loglist.add(0, supportUnitBean.toString());
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void showData(String data) {

    }


}

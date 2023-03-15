package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.blood_glucose;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;

import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.BloodGlucose.BloodGlucoseBleDeviceData;
import cn.net.aicare.modulelibrary.module.BloodGlucose.BloodGlucoseUtil;

/**
 * 血糖仪
 */
public class BloodGlucoseActivity extends BleBaseActivity implements OnCallbackBle, BloodGlucoseBleDeviceData.BloodGlucoseCallback {

    private Button support_unit, quest_status, showdata, test;
    private RadioButton mmol, mg;
    private ListView log_list;

    private List<String> mLogList;
    private ArrayAdapter listAdapter;
    private String mAddress;
    private boolean isShowData=false;
    private BloodGlucoseBleDeviceData mBloodGlucoseBleDeviceData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_glucose);
        mAddress = getIntent().getStringExtra("mac");
        support_unit = findViewById(R.id.support_unit);
        quest_status = findViewById(R.id.quest_status);
        showdata=findViewById(R.id.showdata);
        test =  findViewById(R.id.test);
        mmol = findViewById(R.id.mmol);
        mg = findViewById(R.id.mg);
        log_list = findViewById(R.id.log_list);
        mLogList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mLogList);
         log_list.setAdapter(listAdapter);
        mmol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    if (mBloodGlucoseBleDeviceData != null)
                        mBloodGlucoseBleDeviceData.setUnit(BloodGlucoseUtil.UNIT_MMOL_L);
            }
        });
        mg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    if (mBloodGlucoseBleDeviceData != null)
                        mBloodGlucoseBleDeviceData.setUnit(BloodGlucoseUtil.UNIT_MG_DL);
            }
        });
        showdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShowData){
                    showdata.setText("隐藏Data数据");
                    isShowData=true;
                }else {
                    showdata.setText("显示Data数据");
                    isShowData=false;
                }
            }
        });
        quest_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBloodGlucoseBleDeviceData != null)
                        mBloodGlucoseBleDeviceData.queryStatus();
            }
        });
        // 流程测试
        test.setOnClickListener(v -> {
            Intent intent = new Intent(this, BloodGlucoseTestActivity.class);
            intent.putExtra("mac", mAddress);
            startActivity(intent);
        });
        // 查询设备支持单位
        support_unit.setOnClickListener(v -> {
            if (mBloodGlucoseBleDeviceData != null) {
                mBloodGlucoseBleDeviceData.getSupportUnit();
            }
        });
    }

    @Override
    public void onServiceSuccess() {
        mLogList.add(0, "绑定服务成功");
        listAdapter.notifyDataSetChanged();
        if (mBluetoothService != null) {
            mBluetoothService.setOnCallback(this);
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                mBloodGlucoseBleDeviceData = new BloodGlucoseBleDeviceData(bleDevice);
                mBloodGlucoseBleDeviceData.setBloodGlucoseCallback(this);
            }
        }
    }

    @Override
    public void onDisConnected(String mac, int code) {
        // 断开连接直接退出
        finish();
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }


    @Override
    public void onVersion(String version) {
        mLogList.add(0, "版本号:"+version);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSupportUnit(List<SupportUnitBean> list) {
        mLogList.add(0, "支持单位:");
        for (SupportUnitBean supportUnitBean:list) {
            mLogList.add(0, supportUnitBean.toString());
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeviceStatus(int status) {
          switch (status){
              case BloodGlucoseUtil.STATUS_STATELESS:
                  mLogList.add(0,status+" 无状态");
                  break;
              case BloodGlucoseUtil.STATUS_WAIT_TEST_PAPER:
                  mLogList.add(0,status+" 设备等待插入试纸");
                  break;
              case BloodGlucoseUtil.STATUS_WAIT_BLOOD_SAMPLES:
                  mLogList.add(0,status+" 设备已插入试纸，等待获取血样");
                  break;
              case BloodGlucoseUtil.STATUS_ANALYSIS_BLOOD_SAMPLES:
                  mLogList.add(0,status+" 血样已获取，分析血样中");
                  break;

              case BloodGlucoseUtil.STATUS_TEST_FINISH:
                  mLogList.add(0,status+" 上发数据完成，测量完成");
                  break;
          }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResult(int originalValue, float value, int unit, int decimal) {
        mLogList.add(0,"原始数据:"+originalValue+" 值:"+value+" 单位:"+unit+" 小数点位:"+decimal);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onErrorCode(int code) {
        switch (code){
            case BloodGlucoseUtil.ERROR_CODE_NO_ELECTRICITY:
                mLogList.add(0,code+" 电池没电");
                break;
            case BloodGlucoseUtil.ERROR_CODE_USED_TEST_PAPER:
                mLogList.add(0,code+" 已使用过的试纸");
                break;
            case BloodGlucoseUtil.ERROR_CODE_TEMPERATURE_OUT_OF_RANGE:
                mLogList.add(0,code+" 环境温度超出使用范围");
                break;
            case BloodGlucoseUtil.ERROR_CODE_WITHDRAWN_TEST_PAPER:
                mLogList.add(0,code+" 试纸施加血样后测试未完成，被退出试纸");
                break;
            case BloodGlucoseUtil.ERROR_CODE_SELF_CHECK_FAIL:
                mLogList.add(0,code+" 机器自检未通过");
                break;
            case BloodGlucoseUtil.ERROR_CODE_RESULT_LOWER:
                mLogList.add(0,code+" 测量结果过低，超出测量范围");
                break;
            case BloodGlucoseUtil.ERROR_CODE_RESULT_OVERTOP:
                mLogList.add(0,code+" 测量结果过高，超出测量范围");
                break;
            default:
                mLogList.add(0,"错误码:"+code);
                break;
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSetUnitResult(int result) {
        switch (result){
            case BloodGlucoseUtil.SUCCESS:
                mLogList.add(0,result+" 成功");
                break;
            case BloodGlucoseUtil.FAILED:
                mLogList.add(0,result+" 失败");
                break;
            case BloodGlucoseUtil.NONSUPPORT:
                mLogList.add(0,result+" 不支持");
                break;
        }
        listAdapter.notifyDataSetChanged();

    }

    @Override
    public void onData(String data) {
        if (isShowData)
            mLogList.add(0,data);
    }
}

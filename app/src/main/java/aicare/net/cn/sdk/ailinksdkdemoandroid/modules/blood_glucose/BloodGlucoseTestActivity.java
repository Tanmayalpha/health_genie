package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.blood_glucose;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.pingwang.bluetoothlib.bean.SupportUnitBean;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.BloodGlucose.BloodGlucoseBleDeviceData;
import cn.net.aicare.modulelibrary.module.BloodGlucose.BloodGlucoseUtil;

public class BloodGlucoseTestActivity extends BleBaseActivity implements OnCallbackBle, BloodGlucoseBleDeviceData.BloodGlucoseCallback, OnBleCompanyListener {

    private ListView list_view;
    private ArrayAdapter mAdapter;
    private List<String> mList;

    private String mAddress;
    private BloodGlucoseBleDeviceData mBleDevice;
    private List<Integer> mSupportUnit;// 设备支持的单位列表
    private int mCurTestUnit = 0;// 当前在测试第几个单位

    private int mCurStep = 1;// 当前流程进行到第几步

    private static final int STEP_END_FAIL = -1;// 测试结束 失败
    private static final int STEP_END_SUCCESS = -2;// 测试结束 成功

    private static final int STEP_CID = 1;// 查询设备CID VID PID
    private static final int STEP_UNIT = 2;// 查询设备支持的单位
    private static final int STEP_DEVICE_STATUS = 3;// 获取设备状态
    private static final int STEP_TEST_STATUS_1 = 4;// 测量状态1 插入试纸
    private static final int STEP_TEST_STATUS_2 = 5;// 测量状态2 获取血样
    private static final int STEP_TEST_STATUS_3 = 6;// 测量状态3 分析血样
    private static final int STEP_TEST_DATA = 7;// 测量数据
    private static final int STEP_TEST_STATUS_4 = 8;// 测量状态4 测量完成

    private static final int STEP_SET_UNIT = 9;// APP设置单位 需要设备支持该单位
    private static final int STEP_SET_UNIT_SUCCESS = 10;// APP设置单位 需要返回成功
    private static final int STEP_SET_UNIT_FAIL = 11;// APP设置单位 需要返回失败
    private static final int STEP_SET_UNIT_UNSUPPORT = 12;// APP设置单位 需要返回不支持

    private static final int STEP_ERROR_CODE_1 = 13;// 错误码 电池没电
    private static final int STEP_ERROR_CODE_2 = 14;// 错误码 已使用过的试纸
    private static final int STEP_ERROR_CODE_3 = 15;// 错误码 环境温度超出使用范围
    private static final int STEP_ERROR_CODE_4 = 16;// 错误码 试纸施加血样后测试未完成， 被退出试纸
    private static final int STEP_ERROR_CODE_5 = 17;// 错误码 机器自检未通过
    private static final int STEP_ERROR_CODE_6 = 18;// 错误码 测量结果过低， 超出测量范围
    private static final int STEP_ERROR_CODE_7 = 19;// 错误码 测量结果过高， 超出测量范围

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_glucose_test);

        list_view = findViewById(R.id.list_view);

        mAddress = getIntent().getStringExtra("mac");

        // 初始化列表
        mList = new ArrayList<>();
        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mList);
        list_view.setAdapter(mAdapter);

        setTitle("流程测试");
        list_view.postDelayed(this::nextStep, 500);
    }

    @Override
    public void onDisConnected(String mac, int code) {
        showToast("设备断开连接");
        finish();
    }

    @Override
    public void onServiceSuccess() {
        if (mBluetoothService != null) {
            mBluetoothService.setOnCallback(this);
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                mBleDevice = new BloodGlucoseBleDeviceData(bleDevice);
                mBleDevice.setBloodGlucoseCallback(this);
                bleDevice.setOnBleCompanyListener(this);
            } else {
                // 无法获取到设备
                showToast("无法获取到设备信息");
                finish();
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
    public void onVersion(String version) {

    }

    @Override
    public void onSupportUnit(List<SupportUnitBean> list) {
        // 获取到设备支持的单位
        if (mCurStep == STEP_UNIT) {
            String supportUnitStr = "设备支持的单位：";
            if (list != null && list.size() > 0) {
                boolean hasUnit = false;
                for (SupportUnitBean bean : list) {
                    // 6 是血糖单位
                    if (bean.getType().equals("6")) {
                        mSupportUnit = bean.getSupportUnit();
                        hasUnit = true;
                        for (int unit : bean.getSupportUnit()) {
//                            if (unit == BloodGlucoseUtil.UNIT_MMOL_L) {
                            if (unit == 0) {
                                supportUnitStr += "mmol/L,";
                            }
//                            if (unit == BloodGlucoseUtil.UNIT_MG_DL) {
                            if (unit == 1) {
                                supportUnitStr += "mg/DL,";
                            }
                        }
                    }
                }
                if (!hasUnit) {
                    supportUnitStr += "空";
                }
            } else {
                supportUnitStr += "空";
            }
            if (supportUnitStr.contains(",")) {
                supportUnitStr = supportUnitStr.substring(0, supportUnitStr.length() - 1);
            }
            addText(supportUnitStr);
            addText("测试通过");
            mCurStep = STEP_DEVICE_STATUS;
            nextStep();
        } else {
            stepError();
        }
    }

    @Override
    public void onDeviceStatus(int status) {
        if (mCurStep == STEP_DEVICE_STATUS) {
            addText("获取到设备状态：" + status);
            addText("测试通过");
            addText("下面进入血氧值测量流程测试");
            mCurStep = STEP_TEST_STATUS_1;
            nextStep();
        } else {
            switch (mCurStep) {
                case STEP_TEST_STATUS_1:
                    // 插入试纸
                    if (status == BloodGlucoseUtil.STATUS_WAIT_TEST_PAPER) {
                        addText("收到插入试纸，下一步");
                        mCurStep = STEP_TEST_STATUS_2;
                        nextStep();
                    } else {
                        stepError();
                    }
                    break;
                case STEP_TEST_STATUS_2:
                    // 获取血样
                    if (status == BloodGlucoseUtil.STATUS_WAIT_BLOOD_SAMPLES) {
                        addText("收到获取血样，下一步");
                        mCurStep = STEP_TEST_STATUS_3;
                        nextStep();
                    } else {
                        stepError();
                    }
                    break;
                case STEP_TEST_STATUS_3:
                    // 分析血样
                    if (status == BloodGlucoseUtil.STATUS_ANALYSIS_BLOOD_SAMPLES) {
                        addText("收到分析血样，下一步");
                        mCurStep = STEP_TEST_DATA;
                        nextStep();
                    } else {
                        stepError();
                    }
                    break;
                case STEP_TEST_STATUS_4:
                    // 测量完成
                    if (status == BloodGlucoseUtil.STATUS_TEST_FINISH) {
                        addText("收到测量完成");
                        mCurStep = STEP_SET_UNIT;
                        nextStep();
                    } else {
                        stepError();
                    }
                    break;
                default:
                    stepError();
                    break;
            }
        }

    }

    @Override
    public void onResult(int originalValue, float value, int unit, int decimal) {
        if (mCurStep == STEP_TEST_DATA) {
            addText("收到数据：originValue：" + originalValue + "，value：" + value + "，unit：" + unit + "，decimal：" + decimal);
            mCurStep = STEP_TEST_STATUS_4;
            nextStep();
        } else {
            stepError();
        }
    }

    @Override
    public void onErrorCode(int code) {
        if (mCurStep == STEP_ERROR_CODE_1) {
            addText("收到错误码：" + code + "，下一步");
            mCurStep = STEP_END_SUCCESS;
            nextStep();
        } else {
            stepError();
        }

//        switch (mCurStep) {
//            case STEP_ERROR_CODE_1:
//                if (code == 1) {
//                    addText("收到错误码：" + code + "，下一步");
//                    mCurStep = STEP_ERROR_CODE_2;
//                    nextStep();
//                } else {
//                    stepError();
//                }
//                break;
//            case STEP_ERROR_CODE_2:
//                if (code == 2) {
//                    addText("收到错误码：" + code + "，下一步");
//                    mCurStep = STEP_ERROR_CODE_3;
//                    nextStep();
//                } else {
//                    stepError();
//                }
//                break;
//            case STEP_ERROR_CODE_3:
//                if (code == 3) {
//                    addText("收到错误码：" + code + "，下一步");
//                    mCurStep = STEP_ERROR_CODE_4;
//                    nextStep();
//                } else {
//                    stepError();
//                }
//                break;
//            case STEP_ERROR_CODE_4:
//                if (code == 4) {
//                    addText("收到错误码：" + code + "，下一步");
//                    mCurStep = STEP_ERROR_CODE_5;
//                    nextStep();
//                } else {
//                    stepError();
//                }
//                break;
//            case STEP_ERROR_CODE_5:
//                if (code == 5) {
//                    addText("收到错误码：" + code + "，下一步");
//                    mCurStep = STEP_ERROR_CODE_6;
//                    nextStep();
//                } else {
//                    stepError();
//                }
//                break;
//            case STEP_ERROR_CODE_6:
//                if (code == 6) {
//                    addText("收到错误码：" + code + "，下一步");
//                    mCurStep = STEP_ERROR_CODE_7;
//                    nextStep();
//                } else {
//                    stepError();
//                }
//                break;
//            case STEP_ERROR_CODE_7:
//                if (code == 7) {
//                    addText("收到错误码：" + code + "，下一步");
//                    mCurStep = STEP_END_SUCCESS;
//                    nextStep();
//                } else {
//                    stepError();
//                }
//                break;
//        }
    }

    @Override
    public void onSetUnitResult(int result) {
        switch (mCurStep) {
            case STEP_SET_UNIT_SUCCESS:
                // 设置单位成功
//                if (result == 0) {
                if (result == 0 || result == 1 || result == 2) {
                    if (result == 0) {
                        addText("收到设置单位成功，下一步");
                    } else if (result == 1) {
                        addText("收到设置单位失败，下一步");
                    } else if (result == 2) {
                        addText("收到设置单位不支持，下一步");
                    }
//                    mCurStep = STEP_SET_UNIT_FAIL;
                    mCurStep = STEP_SET_UNIT;
                    nextStep();
                } else {
                    stepError();
                }
                break;
            case STEP_SET_UNIT_FAIL:
//                if (result == 1) {
                if (result == 0 || result == 1 || result == 2) {
//                    mCurStep = STEP_SET_UNIT_UNSUPPORT;
                    mCurStep = STEP_SET_UNIT;
                    nextStep();
                } else {
                    stepError();
                }
                break;
            case STEP_SET_UNIT_UNSUPPORT:
//                if (result == 2) {
                if (result == 0 || result == 1 || result == 2) {
                    mCurStep = STEP_SET_UNIT;
                    nextStep();
                } else {
                    stepError();
                }
                break;
            default:
                stepError();
                break;
        }
    }

    @Override
    public void onData(String data) {

    }

    @Override
    public void OnDID(int cid, int vid, int pid) {
        // 获取到设备的CID VID PID
        if (cid < 0) {
            cid = 0;
        }
        if (vid < 0) {
            vid = 0;
        }
        if (pid < 0) {
            pid = 0;
        }
        if (mCurStep == STEP_CID) {
            addText("CID：" + cid + "，VID：" + vid + "，PID：" + pid);
            if (cid > 0 && vid > 0 && pid > 0) {
                addText("测试通过");
                mCurStep = STEP_UNIT;
                nextStep();
            } else {
                addText("测试不通过，CID VID PID不能小于等于0");
                mCurStep = STEP_END_FAIL;
                nextStep();
            }
        } else {
            stepError();
        }
    }

    // 进行下一步
    private void nextStep() {
        if (this.isDestroyed()) {
            return;
        }
        String stepStr = "第" + mCurStep + "步：";

        switch (mCurStep) {
            case STEP_CID:
                // 查询CID VID PID
                addText(stepStr + "查询设备CID VID PID");
                mBleDevice.getCidVidPid();
                break;
            case STEP_UNIT:
                // 查询设备支持单位
                addText(stepStr + "查询设备支持的单位");
                mBleDevice.getSupportUnit();
                break;
            case STEP_DEVICE_STATUS:
                // 获取设备状态
                addText(stepStr + "APP下发查询状态指令，MCU接收到指令需要回复设备当前状态");
                mBleDevice.queryStatus();
                break;
            case STEP_TEST_STATUS_1:
                // 测量状态1
                addText(stepStr + "请发送插入试纸");
                break;
            case STEP_TEST_STATUS_2:
                // 测量状态2
                addText(stepStr + "请发送获取血样");
                break;
            case STEP_TEST_STATUS_3:
                // 测量状态3
                addText(stepStr + "请发送分析血样中");
                break;
            case STEP_TEST_DATA:
                // 测量数据
                addText(stepStr + "请发送测量数据");
                break;
            case STEP_TEST_STATUS_4:
                // 测量状态4
                addText(stepStr + "请发送测量完成");
                break;
            case STEP_SET_UNIT:
                // APP设置单位
                if (mSupportUnit.size() > mCurTestUnit) {
                    // 还有单位没测，进行测试
                    addText(stepStr + "APP下发单位：" + mSupportUnit.get(mCurTestUnit));
                    mCurTestUnit++;
                    mCurStep = STEP_SET_UNIT_SUCCESS;
                    nextStep();
                } else {
                    // 单位都测试完了
                    addText("单位设置测试完成");
                    mCurStep = STEP_ERROR_CODE_1;
                    nextStep();
                }
                break;
            case STEP_SET_UNIT_SUCCESS:
                // 单位设置成功
//                addText(stepStr + "请回复单位设置成功");
                addText(stepStr + "请回复单位设置结果");
                break;
            case STEP_SET_UNIT_FAIL:
                // 单位设置失败
//                addText(stepStr + "请回复单位设置失败");
                addText(stepStr + "请回复单位设置结果");
                break;
            case STEP_SET_UNIT_UNSUPPORT:
                // 单位设置不支持
//                addText(stepStr + "请回复单位设置不支持");
                addText(stepStr + "请回复单位设置结果");
                break;
            case STEP_ERROR_CODE_1:
                // 错误码 电池没电
//                addText(stepStr + "请发送错误码：电池没电");
                addText(stepStr + "目前血糖仪支持的错误码有：\n" +
                        "0x01：电池没电\n" +
                        "0x02：已使用过的试纸\n" +
                        "0x03：环境温度超出使用范围\n" +
                        "0x04：试纸施加血样后测试未完成，被退出试纸\n" +
                        "0x05：机器自检未通过\n" +
                        "0x06：测量结果过低，超出测量范围\n" +
                        "0x07：测量结果过高，超出测量范围\n\n" +
                        "请设备发送支持的其中一种错误码");
                break;
            case STEP_ERROR_CODE_2:
                // 错误码 已使用过的试纸
                addText(stepStr + "请发送错误码：已使用过的试纸");
                break;
            case STEP_ERROR_CODE_3:
                // 错误码 环境温度超出使用范围
                addText(stepStr + "请发送错误码：环境温度超出使用范围");
                break;
            case STEP_ERROR_CODE_4:
                // 错误码 试纸施加血样后测试未完成， 被退出试纸
                addText(stepStr + "请发送错误码：试纸施加血样后测试未完成， 被退出试纸");
                break;
            case STEP_ERROR_CODE_5:
                // 错误码 机器自检未通过
                addText(stepStr + "请发送错误码：机器自检未通过");
                break;
            case STEP_ERROR_CODE_6:
                // 错误码 测量结果过低， 超出测量范围
                addText(stepStr + "测量结果过低， 超出测量范围");
                break;
            case STEP_ERROR_CODE_7:
                // 错误码 测量结果过高， 超出测量范围
                addText(stepStr + "测量结果过高， 超出测量范围");
                break;
            case STEP_END_FAIL:
                // 测试结束
                addText("❌ 测试结束：失败");
                addText("❌ 请退出当前页面，重新进行流程测试");
                setTitle("❌ 测试结束：失败");
                break;
            case STEP_END_SUCCESS:
                // 测试结束
                addText("✔ 测试结束：成功");
                setTitle("✔ 测试结束：成功");
                break;
        }
    }

    // 显示流程错误
    private void stepError() {
        addText("❌ 第" + mCurStep + "步：" + "请严格按照流程进行测试！");
    }

    // 添加一条文本
    private void addText(String text) {
        mList.add(text);
        mAdapter.notifyDataSetChanged();
        list_view.smoothScrollToPosition(mList.size() - 1);
    }

    // 弹出Toast
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}

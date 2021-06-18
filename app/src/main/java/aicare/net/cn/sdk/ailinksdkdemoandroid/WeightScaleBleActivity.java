package aicare.net.cn.sdk.ailinksdkdemoandroid;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import com.pingwang.bluetoothlib.BleBaseActivity;
import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.config.CmdConfig;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.listener.OnCallbackBle;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.config.BleDeviceConfig;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.BodyFatScale.AppHistoryRecordBean;
import cn.net.aicare.modulelibrary.module.BodyFatScale.BodyFatBleUtilsData;
import cn.net.aicare.modulelibrary.module.BodyFatScale.BodyFatDataUtil;
import cn.net.aicare.modulelibrary.module.BodyFatScale.BodyFatRecord;
import cn.net.aicare.modulelibrary.module.BodyFatScale.McuHistoryRecordBean;
import cn.net.aicare.modulelibrary.module.BodyFatScale.User;

public class WeightScaleBleActivity extends BleBaseActivity implements View.OnClickListener, OnCallbackBle, BodyFatBleUtilsData.BleBodyFatCallback {
    private String TAG = WeightScaleBleActivity.class.getName();
    private String mAddress;
    private List<String> mlogList;
    private List<String> mUserlogList;
    private ArrayAdapter listAdapter;
    private ArrayAdapter userAdapter;
    private BodyFatBleUtilsData bodyFatBleUtilsData;
    private MHandler mMHandler;
    private EditText mEditText;
    private RadioButton kg, jing, stlb, lb;
    private List<User> mUsers;
    private User selectUser;
    private int weighunit = BodyFatDataUtil.KG;
    private  ListView loglistView;
    private ListView userlistView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁止横屏
        setContentView(R.layout.activity_weight_scale_ble);
        initView();
        setUnitinit();
        initdata();






    }

    private void initView(){
        findViewById(R.id.history).setOnClickListener(this);
        findViewById(R.id.userlist).setOnClickListener(this);
        findViewById(R.id.user).setOnClickListener(this);
        findViewById(R.id.adduser).setOnClickListener(this);
        findViewById(R.id.syntime).setOnClickListener(this);
        kg = findViewById(R.id.kg);
        jing = findViewById(R.id.jin);
        stlb = findViewById(R.id.st_lb);
        lb = findViewById(R.id.lb);
        mEditText = findViewById(R.id.edit_Userid);
        kg.setChecked(true);
        loglistView = findViewById(R.id.log_list);
        userlistView = findViewById(R.id.user_list);


    }
    private void initdata(){
        mAddress = getIntent().getStringExtra("mac");

        WeakReference weakReference = new WeakReference(new MHandler());
        mMHandler = (MHandler) weakReference.get();

        mlogList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mlogList);
        loglistView.setAdapter(listAdapter);

        User defaultUser=getdefault();
        selectUser = defaultUser;

        mUsers = new ArrayList<>();
        mUsers.add(defaultUser);

        mUserlogList = new ArrayList<>();
        mUserlogList.add(defaultUser.toString());

        userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mUserlogList);
        userlistView.setAdapter(userAdapter);
    }

    private User getdefault(){
        User user = new User();
        user.setModeType(BodyFatDataUtil.MODE_ORDINARY);
        user.setSex(BodyFatDataUtil.SEX_MAN);
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
                    weighunit = BodyFatDataUtil.KG;
                    if (bodyFatBleUtilsData != null)
                        bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().setWeightUnit(BodyFatDataUtil.KG, BleDeviceConfig.WEIGHT_BODY_FAT_SCALE));
                }
            }
        });
        jing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    weighunit = BodyFatDataUtil.JIN;
                    if (bodyFatBleUtilsData != null)
                        bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().setWeightUnit(BodyFatDataUtil.JIN, BleDeviceConfig.WEIGHT_BODY_FAT_SCALE));
                }
            }
        });
        stlb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    weighunit = BodyFatDataUtil.ST;
                    if (bodyFatBleUtilsData != null)
                        bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().setWeightUnit(BodyFatDataUtil.ST, BleDeviceConfig.WEIGHT_BODY_FAT_SCALE));
                }
            }
        });
        lb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    weighunit = BodyFatDataUtil.LB;
                    if (bodyFatBleUtilsData != null)
                        bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().setWeightUnit(BodyFatDataUtil.LB, BleDeviceConfig.WEIGHT_BODY_FAT_SCALE));
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
            mBluetoothService.setOnCallback(this);
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                BodyFatBleUtilsData.init(bleDevice, this, null);
                bodyFatBleUtilsData = BodyFatBleUtilsData.getInstance();
                if (bodyFatBleUtilsData != null) {
                    bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().setWeightUnit(weighunit, BleDeviceConfig.WEIGHT_BODY_FAT_SCALE));
                    bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().synSysTime());
                }


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
    public void onWeightData(int status, float weight, int weightUnit, int decimals) {
        mlogList.add(0, "体重数据类型：" + status + " 体重: " + weight + " 单位：" + weightUnit + " 小数点位: " + decimals);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onStatus(int status) {

        switch (status) {
            case BodyFatDataUtil.WEIGHT_TESTING:
                mlogList.add(0, "测量状态：" + status + " 测量实时体重");
                break;
            case BodyFatDataUtil.WEIGHT_RESULT:
                mlogList.add(0, "测量状态：" + status + " 稳定体重");
                break;
            case BodyFatDataUtil.IMPEDANCE_TESTING:
                mlogList.add(0, "测量状态：" + status + " 阻抗测量中");
                break;
            case BodyFatDataUtil.IMPEDANCE_SUCCESS:
            case BodyFatDataUtil.IMPEDANCE_SUCCESS_DATA:
                mlogList.add(0, "测量状态：" + status + " 阻抗测量成功");
                break;
            case BodyFatDataUtil.IMPEDANCE_FAIL:
                mlogList.add(0, "测量状态：" + status + " 阻抗测量失败");
                break;
            case BodyFatDataUtil.HEART_TESTING:
                mlogList.add(0, "测量状态：" + status + " 心率测量中");
                break;
            case BodyFatDataUtil.HEART_SUCCESS:
                mlogList.add(0, "测量状态：" + status + " 心率测量成功");
                break;
            case BodyFatDataUtil.HEART_FAIL:
                mlogList.add(0, "测量状态：" + status + " 心率测量失败");
                break;
            case BodyFatDataUtil.TEST_FINISH:
                mlogList.add(0, "测量状态：" + status + " 测量完成");
                break;
            case BodyFatDataUtil.MUC_REQUEST_USER_INFO:
                mlogList.add(0, "测量状态：" + status + "请求用户信息");
                if (bodyFatBleUtilsData != null)
                    bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().setUserInfo(selectUser, BleDeviceConfig.WEIGHT_BODY_FAT_SCALE));
                break;
            default:
                mlogList.add(0, "测量状态：" + status);

        }
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onAdc(int adc, int algorithmic) {
        mlogList.add(0, "阻抗：" + adc + " 算法位：" + algorithmic);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onHeartRate(int heartrate) {
        mlogList.add(0, "心率：" + heartrate);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onBodyFat(BodyFatRecord bodyFatRecord) {
        mlogList.add(0, "体脂数：" + bodyFatRecord.toString());
        mMHandler.sendEmptyMessage(ToRefreUi);
    }


    @Override
    public void onError(int code) {
        mlogList.add(0, "错误码：" + code);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onHistoryMcu(McuHistoryRecordBean mcuHistoryRecordBean) {
        mlogList.add(0, "历史记录Mcu：" + mcuHistoryRecordBean.toString());
        mMHandler.sendEmptyMessage(ToRefreUi);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onHistoryApp(AppHistoryRecordBean appHistoryRecordBean) {
        mlogList.add(0, "历史记录app：" + appHistoryRecordBean.toString());
        mMHandler.sendEmptyMessage(ToRefreUi);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onVersion(String version) {
        mlogList.add(0, "版本号：" + version);
        mMHandler.sendEmptyMessage(ToRefreUi);

    }

    @Override
    public void onMcuBatteryStatus(int status, int battery) {
        mlogList.add(0, "电量状态" + status + " 电量：" + battery);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onSysTime(int status, int[] times) {
        mlogList.add(0, "时间状态" + status);
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void requestSynTime() {
        mlogList.add(0, "同步时间");
        if (bodyFatBleUtilsData != null)
            bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().synTime());
    }

    @Override
    public void setTimeCallback(int type, int status) {
        String msg = "";
        if (type == CmdConfig.SET_SYS_TIME) {
            msg = "设置系统当前时间：";
        } else if (type == CmdConfig.SET_DEVICE_TIME) {
            msg = "同步时间";
        }
        if (status == BodyFatDataUtil.STATUS_SUCCESS) {
            msg = msg + status + " 成功";
        } else if (status == BodyFatDataUtil.STATUS_FAIL) {
            msg = msg + status + " 失败";
        } else if (status == BodyFatDataUtil.STATUS_NOSUPORT) {
            msg = msg + status + " 不支持";
        }
        mlogList.add(0, msg);
        mMHandler.sendEmptyMessage(ToRefreUi);

    }

    @Override
    public void requestSynHistoryCallback(int status) {
        if (status == 0) {
            mlogList.add(0, "请求历史记录：" + status + " 无历史记录");
        } else if (status == 1) {
            mlogList.add(0, "请求历史记录：" + status + " 开始发送历史记录");
        } else {
            mlogList.add(0, "请求历史记录：" + status + " 发送历史记录结束");
        }
    }

    @Override
    public void updateUserCallback(int status) {
        if (status == 0) {
            mlogList.add(0, "更新用户或列表回调" + status + " 更新列表成功");
        } else if (status == 1) {
            mlogList.add(0, "更新用户或列表回调" + status + " 更新个人用户成功");
        } else if (status == 2) {
            mlogList.add(0, "更新用户或列表回调" + status + " 更新列表失败");
        } else {
            mlogList.add(0, "更新用户或列表回调" + status + " 更新个人用户失败");
        }
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    /**
     * @param status {@link#Bod}
     */
    @Override
    public void setUnitCallback(int status) {
        if (status == 0) {
            mlogList.add(0, "下发单位回调" + status + " 成功");
        } else if (status == 1) {
            mlogList.add(0, "下发单位回调" + status + " 失败");
        } else {
            mlogList.add(0, "下发单位回调" + status + " 不支持");
        }
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void requestUserData(int status) {
        if (status == 0x01) {
            mlogList.add(0, "下发用户信息 " + status);
        } else if (status == 0x03) {
            mlogList.add(0, "下发用户信息成功 " + status);
        } else {
            mlogList.add(0, "下发用户信息失败 " + status);
        }
        mMHandler.sendEmptyMessage(ToRefreUi);
    }

    @Override
    public void onOtaCallback(int status) {

    }

    @Override
    public void onSetIpStatus(int status) {

    }

    @Override
    public void onSetIpUrlStatus(int status) {

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.history:
                if (bodyFatBleUtilsData != null)
                    bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().requestSynHistory());
                break;
            case R.id.user:
                if (!mEditText.getText().toString().trim().isEmpty()) {
                    int userid = Integer.parseInt(mEditText.getText().toString().trim());
                    if (mUsers.size() >= userid) {
                        selectUser = mUsers.get(userid - 1);
                        if (bodyFatBleUtilsData != null)
                            bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().updataPresentUser(selectUser));
                    }
                }
                break;
            case R.id.userlist:
                for (User mUser : mUsers) {
                    if (bodyFatBleUtilsData != null)
                        bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().setUserInfoList(mUser));
                }
                if (bodyFatBleUtilsData != null)
                    bodyFatBleUtilsData.sendData(BodyFatDataUtil.getInstance().updateUsersComplete());
                break;
            case R.id.adduser:
                AddUserDialog addUserDialog = new AddUserDialog(new AddUserDialog.OnDialogListener() {
                    @Override
                    public void tvSucceedListener(User user) {
                        user.setId(mUsers.size() + 1);
                        mUsers.add(user);
                        mUserlogList.add(user.toString());
                        userAdapter.notifyDataSetChanged();
                    }
                });
                addUserDialog.show(getSupportFragmentManager());
                break;
            case R.id.syntime:
                if (bodyFatBleUtilsData != null) {
                    SendBleBean sendBleBean = BodyFatDataUtil.getInstance().synSysTime();
                    Log.e("time", BleStrUtils.byte2HexStr(sendBleBean.getHex()));
                    bodyFatBleUtilsData.sendData(sendBleBean);
                }
                break;
        }

    }

    private final int ToRefreUi = 300;


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

            }
        }
    }


}
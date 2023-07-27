package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.TempInstrument;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.listener.CallbackDisIm;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnCallbackDis;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleDensityUtil;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;
import cn.net.aicare.modulelibrary.module.TempInstrument.TempInstrumentBleConfig;
import cn.net.aicare.modulelibrary.module.TempInstrument.TempInstrumentDeviceData;


/**
 * xing<br>
 * 2022/03/23<br>
 * 测温仪
 */
public class TempInstrumentActivity extends BleBaseActivity implements OnCallbackDis, OnBleVersionListener, TempInstrumentDeviceData.onTempListener, TempInstrumentDeviceData.onTempConfigListener,
        TempInstrumentDeviceData.onTempSetListener, OnBleCompanyListener, OnMcuParameterListener, View.OnClickListener {

    private static String TAG = TempInstrumentActivity.class.getName();
    private final int REFRESH_DATA = 3;
    private List<String> mList;
    private ArrayAdapter listAdapter;

    /**
     * 服务Intent
     */
    private Context mContext;
    private EditText etAlarmTempC, etAlarmTempF;
    private TempInstrumentDeviceData mBleDevice;
    private String mAddress;
    private BleSendCmdUtil mBleSendCmdUtil;
    private Map<Long, String> mMapLanguage = new HashMap<>();
    private List<String> mListLanguage = new ArrayList<>();
    private Map<Integer, String> mMapDistance = new HashMap<>();
    private Map<Integer, String> mMapAutoClose = new HashMap<>();
    private Map<Integer, String> mMapAlarmSound = new HashMap<>();
    private boolean mConfigInfoStatus = false;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {


                case REFRESH_DATA:
                    if (listAdapter != null)
                        listAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_instrument);
        mContext = this;
        mAddress = getIntent().getStringExtra("mac");
//        type = getIntent().getIntExtra("type", -1);
        mBleSendCmdUtil = BleSendCmdUtil.getInstance();
        init();
    }

    private void init() {
        mList = new ArrayList<>();
        ListView listView = findViewById(R.id.listview);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        listView.setAdapter(listAdapter);

        findViewById(R.id.btnVersion).setOnClickListener(this);
        findViewById(R.id.btnBattery).setOnClickListener(this);
        findViewById(R.id.btn_get_did).setOnClickListener(this);
        findViewById(R.id.btnClear).setOnClickListener(this);

        findViewById(R.id.btnGetConfig).setOnClickListener(this);
        findViewById(R.id.btnSetUnit).setOnClickListener(this);
        findViewById(R.id.btnSetVolume).setOnClickListener(this);
        findViewById(R.id.btnSetLanguage).setOnClickListener(this);
        findViewById(R.id.btnAutoOpen).setOnClickListener(this);
        findViewById(R.id.btnTempBroadcast).setOnClickListener(this);
        findViewById(R.id.btnTestTempDistance).setOnClickListener(this);
        findViewById(R.id.btnBeep).setOnClickListener(this);
        findViewById(R.id.btnTempBodyAdd).setOnClickListener(this);
        findViewById(R.id.btnTempBodyLess).setOnClickListener(this);
        findViewById(R.id.btnTempObjectAdd).setOnClickListener(this);
        findViewById(R.id.btnTempObjectLess).setOnClickListener(this);
        findViewById(R.id.btnTempErr).setOnClickListener(this);
        findViewById(R.id.btnReset).setOnClickListener(this);
        findViewById(R.id.btnAlarmSound).setOnClickListener(this);
        findViewById(R.id.btnSetSensitivity).setOnClickListener(this);
        findViewById(R.id.btnAutoClose).setOnClickListener(this);

        etAlarmTempC = findViewById(R.id.etAlarmTempC);
        etAlarmTempF = findViewById(R.id.etAlarmTempF);

        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_DI, "滴");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_DD, "叮咚");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_EN, "英语");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_IT, "意大利");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_KO, "韩语");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_ES, "西班牙语");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_JP, "日语");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_JP1, "日语1");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_FR, "法语");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_VI, "越南语");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_KM, "柬埔寨语");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_MS, "马来语");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_IN, "印尼语");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_AR, "阿拉伯语");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_TH, "泰语");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_PL, "波兰语");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_ZH, "中文");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_ZH0, "中文0");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_ZH1, "中文1");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_ZH2, "中文2");
        mMapLanguage.put(TempInstrumentBleConfig.LanguageType.LANGUAGE_ZH3, "中文3");
        mListLanguage.add("无语言");
        mListLanguage.add("滴");
        mListLanguage.add("叮咚");
        mListLanguage.add("英语");
        mListLanguage.add("意大利");
        mListLanguage.add("韩语");
        mListLanguage.add("西班牙语");
        mListLanguage.add("日语");
        mListLanguage.add("日语1");
        mListLanguage.add("法语");
        mListLanguage.add("越南语");
        mListLanguage.add("柬埔寨语");
        mListLanguage.add("马来语");
        mListLanguage.add("印尼语");
        mListLanguage.add("阿拉伯语");
        mListLanguage.add("泰语");
        mListLanguage.add("波兰语");
        mListLanguage.add("中文");
        mListLanguage.add("中文0");
        mListLanguage.add("中文1");
        mListLanguage.add("中文2");
        mListLanguage.add("中文3");


        mMapDistance.put(TempInstrumentBleConfig.TestTempDistanceType.DISTANCE_1, "0.6M");
        mMapDistance.put(TempInstrumentBleConfig.TestTempDistanceType.DISTANCE_2, "0.8M");
        mMapDistance.put(TempInstrumentBleConfig.TestTempDistanceType.DISTANCE_3, "1.0M");
        mMapDistance.put(TempInstrumentBleConfig.TestTempDistanceType.DISTANCE_4, "1.2M");
        mMapDistance.put(TempInstrumentBleConfig.TestTempDistanceType.DISTANCE_5, "1.4M");
        mMapDistance.put(TempInstrumentBleConfig.TestTempDistanceType.DISTANCE_6, "1.6M");
        mMapDistance.put(TempInstrumentBleConfig.TestTempDistanceType.DISTANCE_7, "1.8M");
        mMapDistance.put(TempInstrumentBleConfig.TestTempDistanceType.DISTANCE_8, "2.0M");


        mMapAutoClose.put(TempInstrumentBleConfig.AutoCloseType.AUTO_CLOSE_TYPE_8, "24H");
        mMapAutoClose.put(TempInstrumentBleConfig.AutoCloseType.AUTO_CLOSE_TYPE_7, "20H");
        mMapAutoClose.put(TempInstrumentBleConfig.AutoCloseType.AUTO_CLOSE_TYPE_6, "16H");
        mMapAutoClose.put(TempInstrumentBleConfig.AutoCloseType.AUTO_CLOSE_TYPE_5, "12H");
        mMapAutoClose.put(TempInstrumentBleConfig.AutoCloseType.AUTO_CLOSE_TYPE_4, "8H");
        mMapAutoClose.put(TempInstrumentBleConfig.AutoCloseType.AUTO_CLOSE_TYPE_3, "4H");
        mMapAutoClose.put(TempInstrumentBleConfig.AutoCloseType.AUTO_CLOSE_TYPE_2, "2H");
        mMapAutoClose.put(TempInstrumentBleConfig.AutoCloseType.AUTO_CLOSE_TYPE_1, "OFF");


        mMapAlarmSound.put(TempInstrumentBleConfig.SoundType.SOUND_8, "滴");
        mMapAlarmSound.put(TempInstrumentBleConfig.SoundType.SOUND_7, "叮咚");
        mMapAlarmSound.put(TempInstrumentBleConfig.SoundType.SOUND_6, "保留");
        mMapAlarmSound.put(TempInstrumentBleConfig.SoundType.SOUND_5, "保留");
        mMapAlarmSound.put(TempInstrumentBleConfig.SoundType.SOUND_4, "保留");
        mMapAlarmSound.put(TempInstrumentBleConfig.SoundType.SOUND_3, "保留");
        mMapAlarmSound.put(TempInstrumentBleConfig.SoundType.SOUND_2, "保留");
        mMapAlarmSound.put(TempInstrumentBleConfig.SoundType.SOUND_1, "关闭音效");

    }


    @Override
    public void onClick(View v) {
        if (mBleDevice == null)
            return;
        SendBleBean sendBleBean = new SendBleBean();
        switch (v.getId()) {
            case R.id.btnVersion:
                sendBleBean.setHex(mBleSendCmdUtil.getBleVersion());
                mBleDevice.sendData(sendBleBean);
                break;
            case R.id.btnBattery:
                sendBleBean.setHex(mBleSendCmdUtil.getMcuBatteryStatus());
                mBleDevice.sendData(sendBleBean);
                break;
            case R.id.btn_get_did:
                sendBleBean.setHex(mBleSendCmdUtil.getDid());
                mBleDevice.sendData(sendBleBean);
                break;

            case R.id.btnClear:
                if (mList != null)
                    mList.clear();
                mHandler.sendEmptyMessage(REFRESH_DATA);
                break;

            case R.id.btnGetConfig:
                if (mBleDevice != null) {
                    mBleDevice.getDeviceConfig();
                }
                break;
            case R.id.btnSetUnit:
                if (mBleDevice != null) {
                    if (mTempUnit == TempInstrumentBleConfig.UnitType.TEMP_UNIT_C) {
                        mTempUnit = TempInstrumentBleConfig.UnitType.TEMP_UNIT_F;
                    } else {
                        mTempUnit = TempInstrumentBleConfig.UnitType.TEMP_UNIT_C;
                    }
                    mBleDevice.synUnit(mTempUnit);
                }
                break;
            case R.id.btnSetVolume:
                if (!mConfigInfoStatus)
                    return;
                for (int i = 0; i < mVolumeList.size(); i++) {
                    int id = mVolumeList.get(i);
                    if (id == mCurrentVolumeLevel) {
                        if (mVolumeList.size() - 1 == i) {
                            mCurrentVolumeLevel = mVolumeList.get(0);
                        } else {
                            mCurrentVolumeLevel = mVolumeList.get(++i);
                        }
                        break;
                    }
                }
                mBleDevice.synVolume(mCurrentVolumeLevel);
                break;
            case R.id.btnSetLanguage:
                if (!mConfigInfoStatus)
                    return;
                List<Long> languageIdTypeListAll = TempInstrumentDeviceData.LANGUAGE_ID_ALL_TYPE_LIST;//协议支持的全部语言ID,按照顺序保存的
                long currentLanguageId = languageIdTypeListAll.get(mCurrentLanguageIndex);//获取当前的语言ID
                int languageIndex = 0;//下一个语言的下标,在支持的语言ID表中
                for (int i = 0; i < mLanguageList.size(); i++) {
                    long id = mLanguageList.get(i);
                    //循环获得当前语言ID的下标
                    if (id == currentLanguageId) {
                        //获取下一个语言下标
                        if (i == mLanguageList.size() - 1) {
                            languageIndex = 0;
                        } else {
                            languageIndex = ++i;
                        }
                        break;
                    }
                }
                long languageId = mLanguageList.get(languageIndex);//下一个语言要发送的语言ID
                for (int i = 0; i < languageIdTypeListAll.size(); i++) {
                    long aLong = languageIdTypeListAll.get(i);
                    if (aLong == languageId) {
                        mCurrentLanguageIndex = i;//当前要发送的语言下标
                        break;
                    }
                }

                mBleDevice.synLanguage(mCurrentLanguageIndex);
                break;
            case R.id.btnAutoOpen:
                if (mCurrentAutoOpen == 1) {
                    mCurrentAutoOpen = 0;
                } else {
                    mCurrentAutoOpen = 1;
                }
                mBleDevice.synAutoOpen(mCurrentAutoOpen);
                break;
            case R.id.btnTempBroadcast:
                if (mCurrentTempBroadcast == 1) {
                    mCurrentTempBroadcast = 0;
                } else {
                    mCurrentTempBroadcast = 1;
                }
                mBleDevice.synTempBroadcast(mCurrentTempBroadcast);
                break;
            case R.id.btnTestTempDistance:
                if (!mConfigInfoStatus)
                    return;
                for (int i = 0; i < mDistanceList.size(); i++) {
                    int id = mDistanceList.get(i);
                    if (id == mCurrentTestTempDistanceId) {
                        if (mDistanceList.size() - 1 == i) {
                            mCurrentTestTempDistanceId = mDistanceList.get(0);
                        } else {
                            mCurrentTestTempDistanceId = mDistanceList.get(++i);
                        }
                        break;
                    }
                }
                mBleDevice.synTestTempDistance(mCurrentTestTempDistanceId);
                break;
            case R.id.btnBeep:
                if (!mConfigInfoStatus)
                    return;
                for (int i = 0; i < mBeepList.size(); i++) {
                    int id = mBeepList.get(i);
                    if (id == mCurrentBeepId) {
                        if (mBeepList.size() - 1 == i) {
                            mCurrentBeepId = mBeepList.get(0);
                        } else {
                            mCurrentBeepId = mBeepList.get(++i);
                        }
                        break;
                    }
                }
                mBleDevice.synBeepType(mCurrentBeepId);
                break;
            case R.id.btnAlarmSound:
                if (!mConfigInfoStatus)
                    return;
                for (int i = 0; i < mAlarmSoundList.size(); i++) {
                    int id = mAlarmSoundList.get(i);
                    if (id == mCurrentAlarmSoundId) {
                        if (mAlarmSoundList.size() - 1 == i) {
                            mCurrentAlarmSoundId = mAlarmSoundList.get(0);
                        } else {
                            mCurrentAlarmSoundId = mAlarmSoundList.get(++i);
                        }
                        break;
                    }
                }
                mBleDevice.synAlarmSoundType(mCurrentAlarmSoundId);
                break;
            case R.id.btnTempBodyAdd:
                mBleDevice.synBodyTempAdd(0x00);
                break;
            case R.id.btnTempBodyLess:
                mBleDevice.synBodyTempAdd(0x01);
                break;
            case R.id.btnTempObjectAdd:
                mBleDevice.synObjectTempAdd(0x00);
                break;
            case R.id.btnTempObjectLess:
                mBleDevice.synObjectTempAdd(0x01);
                break;
            case R.id.btnTempErr:
                if (mBleDevice != null) {
                    int tempC = Integer.parseInt(etAlarmTempC.getText().toString().trim());
                    int tempF = Integer.parseInt(etAlarmTempF.getText().toString().trim());
                    mBleDevice.synAlarmTemp(tempC, tempF);
                }
                break;
            case R.id.btnSetSensitivity://设置灵敏度
                if (!mConfigInfoStatus)
                    return;
                for (int i = 0; i < mSensitivityList.size(); i++) {
                    int id = mSensitivityList.get(i);
                    if (id == mCurrentSensitivityLevel) {
                        if (mSensitivityList.size() - 1 == i) {
                            mCurrentSensitivityLevel = mSensitivityList.get(0);
                        } else {
                            mCurrentSensitivityLevel = mSensitivityList.get(++i);
                        }
                        break;
                    }
                }
                mBleDevice.synSensitivity(mCurrentSensitivityLevel);
                break;

            case R.id.btnAutoClose://自动关机
                if (!mConfigInfoStatus)
                    return;
                for (int i = 0; i < mAutoCloseList.size(); i++) {
                    int id = mAutoCloseList.get(i);
                    if (id == mCurrentAutoCloseId) {
                        if (mAutoCloseList.size() - 1 == i) {
                            mCurrentAutoCloseId = mAutoCloseList.get(0);
                        } else {
                            mCurrentAutoCloseId = mAutoCloseList.get(++i);
                        }
                        break;
                    }
                }
                mBleDevice.synAutoClose(mCurrentAutoCloseId);
                break;

            case R.id.btnReset:
                if (mBleDevice != null) {
                    mBleDevice.reset();
                }
                break;

        }
    }

    //---------------------------------服务---------------------------------------------------


    @Override
    public void onServiceSuccess() {
        BleLog.i(TAG, "服务与界面建立连接成功");
        CallbackDisIm.getInstance().addListListener(this);
        if (mBluetoothService != null) {
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                mBleDevice = TempInstrumentDeviceData.getInstance(bleDevice);
                mBleDevice.setOnBleVersionListener(TempInstrumentActivity.this);
                mBleDevice.setOnMcuParameterListener(TempInstrumentActivity.this);
                mBleDevice.setOnCompanyListener(TempInstrumentActivity.this);
                mBleDevice.setOnTempConfigListener(this);
                mBleDevice.setOnTempSetListener(this);
                mBleDevice.setOnTempListener(this);
            } else {
                finish();
                Toast.makeText(mContext, "连接获取对象失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onServiceErr() {
        BleLog.i(TAG, "服务与界面连接断开");
        //与服务断开连接
        mBluetoothService = null;
    }

    @Override
    public void unbindServices() {
        CallbackDisIm.getInstance().removeListener(this);
        if (mBleDevice != null) {
            mBleDevice.disconnect();
            mBleDevice.clear();
            mBleDevice = null;
        }
    }


    //-----------------状态-------------------


    @Override
    public void onConnecting(@NonNull String mac) {
        //TODO 连接中
        if (mAddress.equals(mac))
            BleLog.i(TAG, "连接中");
    }

    @Override
    public void onDisConnected(@NonNull String mac, int code) {
        //TODO 连接断开
        BleLog.i(TAG, "连接断开");
        if (mAddress.equals(mac))
            finish();
    }

    @Override
    public void onServicesDiscovered(@NonNull String mac) {
        //TODO 连接成功(获取服务成功)
        BleLog.i(TAG, "连接成功(获取服务成功)");
    }


    @Override
    public void bleOpen() {

    }

    @Override
    public void bleClose() {
        BleLog.i(TAG, "蓝牙未开启,可请求开启");
        finish();
    }

    //-----------------通知-------------------
    @Override
    public void onBmVersion(String version) {
        mList.add(TimeUtils.getTime() + "版本号:" + version);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onData(byte[] status, int type) {
        String data = "";
        if (status != null)
            data = BleStrUtils.byte2HexStr(status);
        if (type == -2) {
            mList.add(TimeUtils.getTime() + "发送的payload数据0x:" + data);
        } else if (type == TempInstrumentBleConfig.DEVICE_CID) {
            mList.add(TimeUtils.getTime() + "无法解析的payload数据0x:" + data);
        } else {
            mList.add(TimeUtils.getTime() + "透传数据:" + data);
        }
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    private List<Integer> mVolumeList = new ArrayList<>();
    private int mCurrentVolumeLevel;
    private List<Long> mLanguageList = new ArrayList<>();
    private int mCurrentLanguageIndex;
    private List<Integer> mBeepList = new ArrayList<>();
    private int mCurrentBeepId;
    private List<Integer> mAlarmSoundList = new ArrayList<>();
    private int mCurrentAlarmSoundId;
    private List<Integer> mSensitivityList = new ArrayList<>();
    private int mCurrentSensitivityLevel;
    private List<Integer> mAutoCloseList = new ArrayList<>();
    private int mCurrentAutoCloseId;
    private List<Integer> mDistanceList = new ArrayList<>();
    private int mCurrentTestTempDistanceId;
    private int mCurrentAutoOpen;
    private int mCurrentTempBroadcast;

    /**
     * @param volumeList                支持的音量档位,空集合代表不支持
     * @param currentVolumeLevel        当前的音量等级
     * @param languageList              支持的语言列表
     * @param currentLanguageIndex      当前的语言下标
     * @param beepList                  支持的音效列表
     * @param currentBeepId             当前的音效ID
     * @param autoOpen                  -1=不支持,0=关闭自动开机(默认),1=打开自动开机
     * @param distanceList              支持的测温距离列表
     * @param currentTestTempDistanceId 当前的测温距离ID
     */
    @Override
    public void onDeviceConfig1(List<Integer> volumeList, int currentVolumeLevel, List<Long> languageList, int currentLanguageIndex, List<Integer> beepList, int currentBeepId, int autoOpen,
                                List<Integer> distanceList, int currentTestTempDistanceId) {
        mConfigInfoStatus = true;
        mVolumeList.clear();
        mVolumeList.addAll(volumeList);

        mCurrentVolumeLevel = currentVolumeLevel;
        mLanguageList.clear();
        mLanguageList.addAll(languageList);
        mCurrentLanguageIndex = currentLanguageIndex;
        mBeepList.clear();
        mBeepList.addAll(beepList);
        mCurrentBeepId = currentBeepId;
        mDistanceList.clear();
        mDistanceList.addAll(distanceList);
        mCurrentTestTempDistanceId = currentTestTempDistanceId;
        String autoOpenStr = "不支持";
        if (autoOpen == 0) {
            autoOpenStr = "关闭自动开机";
        } else if (autoOpen == 1) {
            autoOpenStr = "打开自动开机";
        }

        String showData = "";
//        showData += "支持的音量数量:" + volumeList.size() + " 当前音量等级:" + currentVolumeLevel + "\n";
        showData += "自动开机配置:" + autoOpenStr;

//        showData += "支持的语言数量" + languageList.size() + " 当前语言ID:" + currentLanguageId + "\n";
//        showData += "支持的音效数量" + soundList.size() + " 当前音效ID:" + currentSoundId + "\n";
//        showData += "支持的距离数量:" + distanceList.size() + "当前距离ID:" + currentTestTempDistanceId + "\n";
        mList.add(TimeUtils.getTime() + "配置信息:\n" + showData);
        mHandler.sendEmptyMessage(REFRESH_DATA);

        StringBuilder volumeListStr = new StringBuilder("支持的音量等级:{");
        for (Integer integer : volumeList) {
            volumeListStr.append("V").append(integer).append(" , ");
        }
        volumeListStr.append("}\n当前音量等级:").append(currentVolumeLevel);
        mList.add(TimeUtils.getTime() + volumeListStr);
        mHandler.sendEmptyMessage(REFRESH_DATA);

        boolean supportLanguage = false;
        StringBuilder languageListStr = new StringBuilder("支持的语言ID:{");
        long currentLanguageId = TempInstrumentDeviceData.LANGUAGE_ID_ALL_TYPE_LIST.get(currentLanguageIndex);
        for (Long integer : languageList) {
            String languageName = mMapLanguage.get(integer);
            languageListStr.append(languageName).append(" , ");
            if (integer == currentLanguageId) {
                supportLanguage = true;
            }
        }

        languageListStr.append("}\n当前语言ID:").append(mListLanguage.get(currentLanguageIndex)).append(supportLanguage ? "" : "  不在支持的列表中");
        mList.add(TimeUtils.getTime() + languageListStr);


        StringBuilder soundListStr = new StringBuilder("支持的提示音ID:{");
        for (Integer integer : beepList) {
            soundListStr.append("0x").append(Integer.toHexString(integer)).append(" , ");
        }
        soundListStr.append("}\n当前提示音ID:0x").append(Integer.toHexString(currentBeepId));
        mList.add(TimeUtils.getTime() + soundListStr);

        StringBuilder distanceListStr = new StringBuilder("支持的距离ID:{");
        for (Integer integer : distanceList) {
            String nameDistance = mMapDistance.get(integer);
            distanceListStr.append(nameDistance).append(" , ");
        }
        distanceListStr.append("}\n当前距离ID:").append(mMapDistance.get(currentTestTempDistanceId) == null ? "不在列表中" + currentTestTempDistanceId : mMapDistance.get(currentTestTempDistanceId));
        mList.add(TimeUtils.getTime() + distanceListStr);
        mHandler.sendEmptyMessage(REFRESH_DATA);

    }


    @Override
    public void onDeviceConfig2(float currentAlarmTempC, int tempBodyCompensateSwitchC, float tempBodyCompensateC, int tempObjectCompensateSwitchC, float tempObjectCompensateC, int currentTempUnit,
                                int tempBroadcast, List<Integer> alarmSoundList, int currentAlarmSoundId, List<Integer> sensitivityList, int currentSensitivityLevel, List<Integer> autoCloseList,
                                int currentAutoCloseId) {


        mConfigInfoStatus = true;
        mTempUnit = currentTempUnit;
        String showData = "";
        showData += "报警温度:" + (currentAlarmTempC) + "℃ \n";
        showData += "体温补偿:" + (tempBodyCompensateSwitchC == 1 ? "支持" : "不支持") + "   补偿的温度:" + (tempBodyCompensateC) + "℃\n";
        showData += "物温补偿:" + (tempObjectCompensateSwitchC == 1 ? "支持" : "不支持") + "   补偿的温度:" + (tempObjectCompensateC) + "℃\n";
        showData += "当前单位:" + (currentTempUnit == 0 ? "℃" : "℉") + "\n";

        String tempBroadcastStr = "不支持";
        if (tempBroadcast == 0) {
            tempBroadcastStr = "关闭";
        } else if (tempBroadcast == 1) {
            tempBroadcastStr = "打开";
        }

        showData += "温度值播报:" + tempBroadcastStr;
        mList.add(TimeUtils.getTime() + "配置信息:\n" + showData);


        StringBuilder alarmSoundListStr = new StringBuilder("支持的报警音效ID:{");
        for (Integer integer : alarmSoundList) {
            alarmSoundListStr.append(mMapAlarmSound.get(integer)).append(" , ");
        }
        alarmSoundListStr.append("}\n当前报警音效:").append(mMapAlarmSound.get(currentAlarmSoundId));
        mList.add(TimeUtils.getTime() + alarmSoundListStr);
        mAlarmSoundList.clear();
        mAlarmSoundList.addAll(alarmSoundList);
        mCurrentAlarmSoundId = currentAlarmSoundId;


        StringBuilder sensitivityListStr = new StringBuilder("支持的灵敏度等级:{");
        for (Integer integer : sensitivityList) {
            sensitivityListStr.append("H").append(integer).append(" , ");
        }
        sensitivityListStr.append("}\n当前灵敏度等级:H").append(currentSensitivityLevel);
        mList.add(TimeUtils.getTime() + sensitivityListStr);
        mSensitivityList.clear();
        mSensitivityList.addAll(sensitivityList);
        mCurrentSensitivityLevel = currentSensitivityLevel;

        StringBuilder autoCloseListStr = new StringBuilder("支持的自动关机ID:{");
        for (Integer integer : autoCloseList) {
            autoCloseListStr.append(mMapAutoClose.get(integer)).append(" , ");
        }
        autoCloseListStr.append("}\n当前自动关机选项:").append(mMapAutoClose.get(currentAutoCloseId));
        mList.add(TimeUtils.getTime() + autoCloseListStr);
        mAutoCloseList.clear();
        mAutoCloseList.addAll(autoCloseList);
        mCurrentAutoCloseId = currentAutoCloseId;

        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onDeviceConfig3(float currentAlarmTempF, int tempBodyCompensateSwitchF, float tempBodyCompensateF, int tempObjectCompensateSwitchF, float tempObjectCompensateF, float alarmTempMinC,
                                float alarmTempMaxC) {


        mConfigInfoStatus = true;
        String showData = "";
        showData += "报警温度:" + (currentAlarmTempF) + "℉ \n";
        showData += "体温补偿:" + (tempBodyCompensateSwitchF == 1 ? "支持" : "不支持") + "   补偿的温度:" + (tempBodyCompensateF) + "℉\n";
        showData += "物温补偿:" + (tempObjectCompensateSwitchF == 1 ? "支持" : "不支持") + "   补偿的温度:" + (tempObjectCompensateF) + "℉\n";
        showData += "报警值下限:" + (alarmTempMinC) + " ℃    报警值上限:" + (alarmTempMaxC) + " ℃\n";

        mList.add(TimeUtils.getTime() + showData);
        mHandler.sendEmptyMessage(REFRESH_DATA);

    }

    @Override
    public void onTemp(int type, int temp, int decimal, int tempUnit) {
        String tempStr = BleDensityUtil.getInstance().holdDecimals(temp, decimal);
        String showTempType = (type == 1 ? "当前体温:" : "当前物温:");
        String unitStr = (tempUnit == 0 ? "℃" : "℉");
        mList.add(TimeUtils.getTime() + showTempType + tempStr + unitStr);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    public void onReset(int status) {
        String showStatus = (status == 0 ? "成功" : "失败");
        mList.add(TimeUtils.getTime() + "恢复出厂设置:" + showStatus);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    /**
     * @param status  0=成功,1=失败
     * @param soundId 音效ID{@link TempInstrumentBleConfig.SoundType}
     */
    @Override
    public void onBeepType(int status, int soundId) {
        mCurrentBeepId = soundId;
        String showStatus = (status == 0 ? "成功" : "失败");
        mList.add(TimeUtils.getTime() + "结果:" + showStatus + " 当前提示音ID:0x" + Integer.toHexString(soundId));
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onAlarmSoundType(int status, int soundId) {
        mCurrentAlarmSoundId = soundId;
        String showStatus = (status == 0 ? "成功" : "失败");
        mList.add(TimeUtils.getTime() + "结果:" + showStatus + " 当前报警音效ID:0x" + Integer.toHexString(soundId) + "  " + mMapAlarmSound.get(soundId));
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onAlarmTemp(int status, float tempC, float tempF) {


        String showStatus = (status == 0 ? "成功" : "失败");
        mList.add(TimeUtils.getTime() + "结果:" + showStatus + " 当前报警温度:" + (tempC) + "℃ ; " + (tempF) + "℉");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onBodyTempAdd(int status, float tempC, float tempF) {

        String showStatus;
        if (status == 0) {
            showStatus = "成功";
        } else if (status == 2) {
            showStatus = "已达最大值";
        } else if (status == 3) {
            showStatus = "已达最小值";
        } else {
            showStatus = "失败";
        }

        mList.add(TimeUtils.getTime() + "结果:" + showStatus + " 当前体温校准:" + (tempC) + "℃ ; " + (tempF) + "℉");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onObjectTempAdd(int status, float tempC, float tempF) {

        String showStatus;
        if (status == 0) {
            showStatus = "成功";
        } else if (status == 2) {
            showStatus = "已达最大值";
        } else if (status == 3) {
            showStatus = "已达最小值";
        } else {
            showStatus = "失败";
        }
        mList.add(TimeUtils.getTime() + "结果:" + showStatus + " 当前物温校准:" + (tempC) + "℃ ; " + (tempF) + "℉");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onTestTempDistance(int status, int testTempDistanceId) {
        mCurrentTestTempDistanceId = testTempDistanceId;
        String showStatus = (status == 0 ? "成功" : "失败");
        mList.add(TimeUtils.getTime() + "结果:" + showStatus + " 当前测温距离ID:0x" + Integer.toHexString(testTempDistanceId) + "  " + mMapDistance.get(testTempDistanceId));
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    /**
     * @param status   0=成功,1=失败
     * @param autoOpen 0=关闭自动开机,1=打开自动开机
     */
    @Override
    public void onAutoOpen(int status, int autoOpen) {
        mCurrentAutoOpen = autoOpen;
        String showStatus = (status == 0 ? "成功" : "失败");
        String showAutoOpen = (autoOpen == 0 ? "关闭自动开机" : "打开自动开机");
        mList.add(TimeUtils.getTime() + "结果:" + showStatus + " 操作:" + showAutoOpen);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onTempBroadcast(int status, int autoOpen) {
        mCurrentTempBroadcast = autoOpen;
        String showStatus = (status == 0 ? "成功" : "失败");
        String showAutoOpen = (autoOpen == 0 ? "关闭温度播报" : "打开温度播报");
        mList.add(TimeUtils.getTime() + "结果:" + showStatus + " 操作:" + showAutoOpen);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onLanguage(int status, int languageId) {
        mCurrentLanguageIndex = languageId;
        String showStatus = (status == 0 ? "成功" : "失败");
        mList.add(TimeUtils.getTime() + "结果:" + showStatus + " 当前语言:0x" + Integer.toHexString(languageId) + "  " + mListLanguage.get(languageId));
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onVolume(int status, int volumeLevel) {
        mCurrentVolumeLevel = volumeLevel;
        String showStatus = (status == 0 ? "成功" : "失败");
        mList.add(TimeUtils.getTime() + "结果:" + showStatus + " 当前音量等级:0x" + Integer.toHexString(volumeLevel));
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    private int mTempUnit = 0;

    @Override
    public void onUnit(int status, int tempUnit) {
        mTempUnit = tempUnit;
        String unitStr = (tempUnit == 0 ? "℃" : "℉");
        String showStatus = (status == 0 ? "成功" : "失败");
        mList.add(TimeUtils.getTime() + "单位结果:" + showStatus + unitStr);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onSensitivity(int status, int level) {
        mCurrentSensitivityLevel = level;
        String showStatus = (status == 0 ? "成功" : "失败");
        mList.add(TimeUtils.getTime() + "结果:" + showStatus + " 当前灵敏度等级:0x" + Integer.toHexString(level));
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onAutoClose(int status, int autoCloseId) {
        mCurrentAutoCloseId = autoCloseId;
        String showStatus = (status == 0 ? "成功" : "失败");
        mList.add(TimeUtils.getTime() + "结果:" + showStatus + " 当前自动关机:0x" + Integer.toHexString(autoCloseId) + "   " + mMapAutoClose.get(autoCloseId));
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onErr(int status) {
        String statusStr = "体温过高";
        switch (status) {
            case 0:
                statusStr = "体温过高";
                break;
            case 1:
                statusStr = "体温过低";
                break;

        }
        mList.add(TimeUtils.getTime() + "结果:" + statusStr);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void synErrToConfig(int cmdType) {
        mList.add(TimeUtils.getTime() + "操作失败的指令:" + Integer.toHexString(cmdType));
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    //------------------------

    @Override
    public void OnDID(int cid, int vid, int pid) {
        String didStr = "cid:" + cid + "||vid:" + vid + "||pid:" + pid;
        mList.add(TimeUtils.getTime() + "ID:" + didStr);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onMcuBatteryStatus(int status, int battery) {
        mList.add(TimeUtils.getTime() + "电量:" + battery + "%");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onSysTime(int status, int[] times) {
        String time = times[0] + "-" + times[1] + "-" + times[2] + "  " + times[3] + ":" + times[4] + ":" + times[5];
        mList.add(TimeUtils.getTime() + "系统时间:" + time);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleLog.i(TAG, "onDestroy");
    }
}

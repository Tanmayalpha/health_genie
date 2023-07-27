package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.toothbrush_test;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pingwang.bluetoothlib.device.BleDevice;

import java.util.ArrayList;
import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import cn.net.aicare.modulelibrary.module.BleToothbrush.ToothbrushTestData;
import cn.net.aicare.modulelibrary.module.ToothBrush.ToothBrushBleUtilsData;

public class ToothbrushTestActivity extends BleBaseActivity implements ToothbrushTestData.BleToothbrushCallback, ToothbrushTestAdapter.OnSelectListener {

    private static final int STEP_SUPPORT_MODE = 0;// 牙刷支持模式列表
    private static final int STEP_REQUEST_TOKEN = 1;// 请求绑定
    private static final int STEP_SET_DEFAULT_TIME_MODE = 2;// 设置默认的刷牙时长和刷牙模式
    private static final int STEP_GET_DEFAULT_TIME_MODE = 3;// 获取默认的刷牙时长和刷牙模式
    private static final int STEP_TRY = 4;// 试用
    private static final int STEP_QUERY_WORK_STATUS = 5;// 查询工作状态
    private static final int STEP_SET_CUSTOM = 6;// 手动挡位设置
    private static final int STEP_GET_CUSTOM = 7;// 手动挡位获取
    private static final int STEP_SET_GEAR_TWO = 8;// 设置二级挡位
    private static final int STEP_GET_GEAR_TWO = 9;// 获取二级挡位
    private static final int STEP_WORK = 10;// 让牙刷进行工作
    private static final int STEP_WORK_TRY = 11;// 工作中进行试用
    private static final int STEP_WORK_SET_MODE = 12;// 工作中切换模式
    private static final int STEP_DONE = 13;// 结束

    private static final int RESULT_NULL = 0;
    private static final int RESULT_SUCCESS = 1;
    private static final int RESULT_FAIL = 2;

    private static final long DELAY_FAIL = 2000;// 每个测试项等待多久
    private static final long DELAY_NEXT = 500;// 进入下一个测试需要等多久

    private static final int MSG_FAIL = 100;// 超时
    private static final int MSG_NEXT = 101;// 下一步

    private RecyclerView recycler_view;
    private Button btn_toothbrush_prevent_splash, btn_toothbrush_prevent_splash_test;
    private TextView tv_toothbrush_status;

    private List<ToothbrushTestBean> mList;
    private ToothbrushTestAdapter mAdapter;

    private String mMac;
    private BleDevice mBleDevice;
    private ToothbrushTestData mToothbrushTestData;
    private ToothBrushBleUtilsData mToothBrushBleUtilsData;

    private int mStep;// 当前正在进行哪个步骤
    private int mSupportMode;// 支持的模式
    private int mWorkTryResult = -1;
    private int mWorkSetModeResult = -1;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_FAIL:
                    // 超时就是失败
                    setTestResultStr(null);
                    setTestResult(RESULT_FAIL);
                    // 然后开启下一组测试
                    mStep = getNextStep();
                    mHandler.sendEmptyMessageDelayed(MSG_NEXT, DELAY_NEXT);
                    break;
                case MSG_NEXT:
                    // 开始下一组测试
                    test();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toothbrush_test);

        recycler_view = findViewById(R.id.recycler_view);

        // 获取Mac
        mMac = getIntent().getStringExtra("mac");
        setTitle(mMac);

        // 初始化列表
        mList = new ArrayList<>();
        mAdapter = new ToothbrushTestAdapter(this, mList);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setAdapter(mAdapter);
        mAdapter.setOnSelectListener(this);
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mBluetoothService != null) {
            mBluetoothService.disconnectAll();
        }
        super.onDestroy();
    }

    @Override
    public void onServiceSuccess() {
        mBleDevice = mBluetoothService.getBleDevice(mMac);
        if (mBleDevice != null) {
            mToothbrushTestData = new ToothbrushTestData(mBleDevice);
            mToothbrushTestData.setBleToothbrushCallback(this);

            // 开始进行第一步测试
            addTestTitle(1);
            mStep = STEP_SUPPORT_MODE;
            test();
        }
    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }

    @Override
    public void mcuSupportMode(List<Integer> firstList, List<Integer> secondList) {
        // MCU 回复支持挡位列表
        if (mStep == STEP_SUPPORT_MODE) {
            stepSuccess();
            if (firstList.size() > 0) {
                mSupportMode = firstList.get(0);
            } else {
                mSupportMode = 1;
            }
        }
    }

    @Override
    public void mcuToken(int result) {
        // MCU 回复授权结果
        if (mStep == STEP_REQUEST_TOKEN) {
            stepSuccess();
        }
    }

    @Override
    public void mcuSetDefaultMode(String hexStr, int result) {
        // MCU 回复设置默认模式和时长
        if (mStep == STEP_SET_DEFAULT_TIME_MODE) {
            stepSuccess();
        } else if (mStep == STEP_WORK_SET_MODE) {
            mWorkSetModeResult = result;
            setTestTitleStr("测试项3：测试4.3.8.1牙刷在工作状态下，APP发送设置默认刷牙模式和刷牙时长命令，MCU应该回复设置失败（请启动牙刷后点击右侧\"模式\"按钮））\n\n" + hexStr);
            if (result == 1) {
                stepSuccess();
            }
        }
    }

    @Override
    public void mcuGetDefaultMode(int time, int gear, int gearLevel) {
        // MCU 回复获取默认模式和时长
        if (mStep == STEP_GET_DEFAULT_TIME_MODE) {
            stepSuccess();
        }
    }

    @Override
    public void mcuTry(String hexStr, int result) {
        // MCU 回复试用
        if (mStep == STEP_TRY) {
            stepSuccess();
        } else if (mStep == STEP_WORK_TRY) {
            mWorkTryResult = result;
            setTestTitleStr("测试项2：测试4.3.9牙刷在工作状态下，APP发送试用命令，MCU应该回复 0x05：设置失败，正在工作（请启动牙刷后点击右侧\"试用\"按钮））\n\n" + hexStr);
            if (result == 5) {
                stepSuccess();
            }
        }
    }

    private boolean mHasStop = false;// 是否有关闭电机
    private boolean mHasChange = false;// 是否有切换阶段

    @Override
    public void mcuQueryWorkStatus(int gear, int gearLevel, int stage) {
        // MCU 回复查询工作状态
        if (mStep == STEP_QUERY_WORK_STATUS) {
            stepSuccess();
        } else if (mStep == STEP_WORK) {
            // 测试工作阶段
            if (gear == 0x00) {
                mHasStop = true;
            }
            if (stage == 0xFF) {
                // 如果不支持阶段就当作成功
                mHasChange = true;
            } else {
                // 其他的也算成功
                mHasChange = true;
            }
        }
    }

    @Override
    public void mcuSetCustom(int status) {
        // MCU 回复设置自定义挡位参数
        if (mStep == STEP_SET_CUSTOM) {
            stepSuccess();
        }
    }

    @Override
    public void mcuGetCustom(int frequency, int duty, int time) {
        // MCU 回复获取自定义挡位参数
        if (mStep == STEP_GET_CUSTOM) {
            stepSuccess();
        }
    }

    @Override
    public void mcuSetGearTwo(int status) {
        // MCU 回复设置二级挡位
        if (mStep == STEP_SET_GEAR_TWO) {
            stepSuccess();
        }
    }

    @Override
    public void mcuGetGearTwo(int status) {
        // MCU 回复获取二级挡位
        if (mStep == STEP_GET_GEAR_TWO) {
            stepSuccess();
        }
    }

    @Override
    public void onSelect(int pos) {
        ToothbrushTestBean bean = mList.get(pos);
        if (TextUtils.isEmpty(bean.getResultStr())) {
            if (bean.getResult() == RESULT_FAIL) {
                // 如果是失败，显示失败原因
                switch (bean.getStep()) {
                    case STEP_SUPPORT_MODE:
                        showNgDialog("4.3.2 MCU/APP 获取牙刷支持的模式列表（ Type = 0x36）");
                        break;
                    case STEP_REQUEST_TOKEN:
                        showNgDialog("4.3.3.3 APP 请求绑定");
                        break;
                    case STEP_SET_DEFAULT_TIME_MODE:
                        showNgDialog("4.3.8.1 APP 设置默认的工作模式。");
                        break;
                    case STEP_GET_DEFAULT_TIME_MODE:
                        showNgDialog("4.3.8.2 APP 获取默认的工作模式。");
                        break;
                    case STEP_TRY:
                        showNgDialog("4.3.9 APP 试用指令 (0x7A -- 0x06)");
                        break;
                    case STEP_QUERY_WORK_STATUS:
                        showNgDialog("4.3.10 APP 查询 / MCU 上报 牙刷工作状态 (0x7A -- 0x07)");
                        break;
                    case STEP_SET_CUSTOM:
                        showNgDialog("4.3.11.1 APP 设置自定义档位参数");
                        break;
                    case STEP_GET_CUSTOM:
                        showNgDialog("4.3.11.2 APP 获取自定义档位参数");
                        break;
                    case STEP_SET_GEAR_TWO:
                        showNgDialog("4.3.13.1 APP 设置二级档位默认模式");
                        break;
                    case STEP_GET_GEAR_TWO:
                        showNgDialog("4.3.13.2 APP 获取二级档位默认模式");
                        break;
                    case STEP_WORK:
                        showNgDialog("4.3.10 APP 查询 / MCU 上报 牙刷工作状态 (0x7A -- 0x07)。需要回复关闭电机和模式切换。");
                        break;
                    case STEP_WORK_TRY:
                        showNgDialog("4.3.9 APP 试用指令 (0x7A -- 0x06)。回复5： 设置失败， 正在工作");
                        break;
                    case STEP_WORK_SET_MODE:
                        showNgDialog("4.3.8.1 APP 设置默认的工作模式。回复失败");
                        break;
                }
            }
        } else {
            switch (bean.getStep()) {
                case STEP_WORK:
                    if (mHasStop && mHasChange) {
                        stepSuccess();
                    } else {
                        mHandler.sendEmptyMessage(MSG_FAIL);
                    }
                    break;
                case STEP_WORK_TRY:
                    setTestResultStr(null);
                    mHandler.sendEmptyMessageDelayed(MSG_FAIL, DELAY_FAIL);
                    mToothbrushTestData.appTry(mSupportMode, 1, 1, 1);
                    mHandler.postDelayed(() -> {
                        mToothbrushTestData.appTry(0, 1, 1, 1);
                    }, 3000);
                    break;
                case STEP_WORK_SET_MODE:
                    setTestResultStr(null);
                    mHandler.sendEmptyMessageDelayed(MSG_FAIL, DELAY_FAIL);
                    mToothbrushTestData.appSetDefaultMode(1, mSupportMode, 1);
                    break;
            }
        }
    }

    /**
     * 进行测试
     */
    private void test() {
        switch (mStep) {
            case STEP_SUPPORT_MODE:
                addTest("获取牙刷支持的模式列表");
                mToothbrushTestData.appGetSupportMode();
                break;
            case STEP_REQUEST_TOKEN:
                addTest("请求绑定");
                mToothbrushTestData.appRequestToken();
                break;
            case STEP_SET_DEFAULT_TIME_MODE:
                addTest("设置默认的刷牙时长和刷牙模式");
                mToothbrushTestData.appSetDefaultMode(120, mSupportMode, 1);
                break;
            case STEP_GET_DEFAULT_TIME_MODE:
                addTest("获取默认的刷牙时长和刷牙模式");
                mToothbrushTestData.appGetDefaultMode();
                break;
            case STEP_TRY:
                addTest("试用");
                mToothbrushTestData.appTry(mSupportMode, 1, 1, 1);
                mHandler.postDelayed(() -> {
                    mToothbrushTestData.appTry(0, 1, 1, 1);
                }, 3000);
                break;
            case STEP_QUERY_WORK_STATUS:
                addTest("查询工作状态");
                mToothbrushTestData.appQueryWorkStatus();
                break;
            case STEP_SET_CUSTOM:
                addTest("设置自定义挡位参数（牙刷不支持请忽略）");
                mToothbrushTestData.appSetCustom(1, 1, 120);
                break;
            case STEP_GET_CUSTOM:
                addTest("获取自定义挡位参数（牙刷不支持请忽略）");
                mToothbrushTestData.appGetCustom();
                break;
            case STEP_SET_GEAR_TWO:
                addTest("设置二级挡位（牙刷不支持请忽略）");
                mToothbrushTestData.appSetGearTwo(1);
                break;
            case STEP_GET_GEAR_TWO:
                addTest("获取二级挡位（牙刷不支持请忽略）");
                mToothbrushTestData.appGetGearTwo();
                break;
            case STEP_WORK:
                addTestTitle(2);
//                addTest("4.3.10 APP 查询 / MCU 上报 牙刷工作状态。请开启牙刷，刷牙30秒以上，停止后点击结束");
                addTest("测试项1：测试4.3.10刷牙过程中，MCU应该上报牙刷工作状态（请启动牙刷，刷牙30秒以上，停止后点击右侧\"结束\"按钮）");
                setTestResultStr("结束");
                mHandler.removeMessages(MSG_FAIL);
                break;
            case STEP_WORK_TRY:
                addTest("测试项2：测试4.3.9牙刷在工作状态下，APP发送试用命令，MCU应该回复 0x05：设置失败，正在工作（请启动牙刷后点击右侧\"试用\"按钮））");
                setTestResultStr("试用");
                mHandler.removeMessages(MSG_FAIL);
                break;
            case STEP_WORK_SET_MODE:
//                addTest("牙刷在工作时，是否会响应设置模式指令：请开启牙刷后点击模式");
                addTest("测试项3：测试4.3.8.1牙刷在工作状态下，APP发送设置默认刷牙模式和刷牙时长命令，MCU应该回复设置失败（请启动牙刷后点击右侧\"模式\"按钮））");
                setTestResultStr("模式");
                mHandler.removeMessages(MSG_FAIL);
                break;
            case STEP_DONE:
                addTest("测试结束。以上出现NG的项说明没有回复应该回的指令。如果设备不支持，请忽略");
                mHandler.removeMessages(MSG_FAIL);
                break;
        }
    }

    /**
     * 添加测试项
     *
     * @param title 测试项标题
     */
    private void addTest(String title) {
        mList.add(new ToothbrushTestBean(mStep, title, RESULT_NULL));
        mAdapter.notifyItemInserted(mList.size() - 1);
        recycler_view.smoothScrollToPosition(mList.size() - 1);
        addTimeout();
    }

    /**
     * 添加测试项标题
     *
     * @param type 1：自动测试；2：手动测试
     */
    private void addTestTitle(int type) {
        mList.add(new ToothbrushTestBean(type));
        mAdapter.notifyItemInserted(mList.size() - 1);
        recycler_view.smoothScrollToPosition(mList.size() - 1);
    }

    /**
     * 设置测试结果
     *
     * @param result 0 无，1 成功，2 失败
     */
    private void setTestResult(int result) {
        mList.get(mList.size() - 1).setResult(result);
        mAdapter.notifyItemChanged(mList.size() - 1);
    }

    /**
     * 设置测试结果字符串
     *
     * @param resultStr 字符串
     */
    private void setTestResultStr(String resultStr) {
        mList.get(mList.size() - 1).setResultStr(resultStr);
        mAdapter.notifyItemChanged(mList.size() - 1);
    }

    /**
     * 设置测试标题字符串
     *
     * @param titleStr 字符串
     */
    private void setTestTitleStr(String titleStr) {
        mList.get(mList.size() - 1).setTitle(titleStr);
        mAdapter.notifyItemChanged(mList.size() - 1);
    }

    /**
     * 增加超时检测
     */
    private void addTimeout() {
        mHandler.removeMessages(MSG_FAIL);
        mHandler.sendEmptyMessageDelayed(MSG_FAIL, DELAY_FAIL);
    }

    /**
     * 这一步测试成功
     */
    private void stepSuccess() {
        setTestResultStr(null);
        setTestResult(RESULT_SUCCESS);
        mHandler.removeMessages(MSG_FAIL);
        mHandler.removeMessages(MSG_NEXT);

        if (mStep == STEP_TRY || mStep == STEP_WORK_TRY) {
            mHandler.sendEmptyMessageDelayed(MSG_NEXT, 5000);
        } else {
            mHandler.sendEmptyMessageDelayed(MSG_NEXT, DELAY_NEXT);
        }

        mStep = getNextStep();
    }

    /**
     * 获取下一步
     *
     * @return nextStep
     */
    private int getNextStep() {
        return mStep + 1;
    }

    /**
     * 弹框提示
     *
     * @param str str
     */
    private void showNgDialog(String str) {
        new AlertDialog.Builder(this).setTitle("请按照协议回复：").setMessage("\n" + str).setPositiveButton("确认", null).show();
    }
}

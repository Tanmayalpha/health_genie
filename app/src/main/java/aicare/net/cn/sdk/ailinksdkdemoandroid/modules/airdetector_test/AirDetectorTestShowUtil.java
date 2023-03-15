package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.airdetector_test;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;

import aicare.net.cn.sdk.ailinksdkdemoandroid.modules.airdetector.AirUtil;
import androidx.annotation.NonNull;
import cn.net.aicare.modulelibrary.module.airDetector.AirConst;
import cn.net.aicare.modulelibrary.module.airDetector.CalibrationListBean;
import cn.net.aicare.modulelibrary.module.airDetector.StatusBean;
import cn.net.aicare.modulelibrary.module.airDetector.SupportBean;

public class AirDetectorTestShowUtil {

    /**
     * 支持的功能列表
     *
     * @param supportList
     * @return
     */
    public static String showTextSupport(SparseArray<SupportBean> supportList) {
        int[] supportTypes = new int[supportList.size()];
        for (int i = 0; i < supportList.size(); i++) {
            supportTypes[i] = supportList.keyAt(i);
        }
        return AirUtil.showTypeArrayName(supportTypes);
    }

    /**
     * 实时状态返回
     *
     * @param statusList
     * @param supportList
     * @return
     */
    public static String showTextStatus(SparseArray<StatusBean> statusList, SparseArray<SupportBean> supportList) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < statusList.size(); i++) {
            int type = statusList.keyAt(i);
            // 支持列表不包含，添加空字符串
            SupportBean supportBean = supportList.get(type);
            if (supportBean == null && AirConst.AIR_SETTING_SWITCH_TEMP_UNIT != type && AirConst.AIR_SETTING_BATTEY != type) {
                builder.append("");
                continue;
            }
            StatusBean statusBean = statusList.get(type);
            builder.append(AirUtil.getTypeName(type) + " : ");
            switch (type) {
                case AirConst.AIR_TYPE_FORMALDEHYDE:
                case AirConst.AIR_TYPE_HUMIDITY:
                case AirConst.AIR_TYPE_PM_2_5:
                case AirConst.AIR_TYPE_PM_1:
                case AirConst.AIR_TYPE_PM_10:
                case AirConst.AIR_TYPE_VOC:
                case AirConst.AIR_TYPE_CO2:
                case AirConst.AIR_TYPE_AQI:
                case AirConst.AIR_TYPE_TVOC:
                case AirConst.AIR_SETTING_WARM_DURATION:
                case AirConst.AIR_TYPE_CO:
                    double curValue = statusBean.getValue() / Math.pow(10, supportBean.getPoint());
                    builder.append(curValue);
                    break;
                case AirConst.AIR_TYPE_TEMP:
                    builder.append(statusBean.getValue()).append(AirUtil.getTempUnitName(statusBean.getUnit()));
                    break;
                case AirConst.AIR_SETTING_WARM:
                    int[] warms = (int[]) statusBean.getExtentObject();
                    builder.append(AirUtil.getSwitchStatus(statusBean.isOpen())).append("， 状态：").append(Arrays.toString(warms));
                    break;
                case AirConst.AIR_SETTING_VOICE:
                    builder.append("开关：").append(AirUtil.getSwitchStatus(statusBean.isOpen())).append(", 音量: ").append(statusBean.getValue());
                    break;
                case AirConst.AIR_SETTING_WARM_VOICE:
                case AirConst.AIR_SETTING_DEVICE_SELF_TEST:
                case AirConst.AIR_RESTORE_FACTORY_SETTINGS:
                case AirConst.AIR_DATA_DISPLAY_MODE:
                    builder.append(statusBean.getValue());
                    break;
                case AirConst.AIR_TIME_FORMAT:
                    builder.append(AirUtil.getTimeFormat((int) statusBean.getValue()));
                    break;
                case AirConst.AIR_SETTING_BATTEY:
                    builder.append(AirUtil.getBatteryFormat(statusBean));
                    break;
                case AirConst.AIR_ALARM_CLOCK:
                    builder.append(AirUtil.dealCalResultAllAlarm(statusBean, supportBean));
                    break;
                case AirConst.AIR_CALIBRATION_PARAMETERS:
                    builder.append(AirUtil.dealCalResultAllStatus(statusBean, supportList));
                    break;
                case AirConst.AIR_BRIGHTNESS_EQUIPMENT:
                    builder.append("自动调节：" + AirUtil.getSwitchStatus(statusBean.isOpen()) + ",  亮度值/档位：" + statusBean.getValue());
                    break;
                case AirConst.AIR_KEY_SOUND:
                case AirConst.AIR_ALARM_SOUND_EFFECT:
                case AirConst.AIR_ICON_DISPLAY:
                case AirConst.AIR_MONITORING_DISPLAY_DATA:
                    builder.append(AirUtil.getSwitchStatus(statusBean.isOpen()));
                    break;
                default:
                    break;
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * 设置指令返回
     *
     * @param settingList
     * @param supportList
     * @Param resultInterface
     * @return
     */
    public static String showGetResultSettings(SparseArray<StatusBean> settingList, SparseArray<SupportBean> supportList){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < settingList.size(); i++) {
            int type = settingList.keyAt(i);
            // 支持列表不包含，添加空字符串
            SupportBean supportBean = supportList.get(type);
            if (supportBean == null && AirConst.AIR_SETTING_SWITCH_TEMP_UNIT != type) {
                builder.append("");
                continue;
            }
            StatusBean settingBean = settingList.get(type);
            builder.append(AirUtil.getTypeName(type) + " : ");
            switch (type) {
                case AirConst.AIR_TYPE_FORMALDEHYDE:
                case AirConst.AIR_TYPE_PM_2_5:
                case AirConst.AIR_TYPE_PM_1:
                case AirConst.AIR_TYPE_PM_10:
                case AirConst.AIR_TYPE_VOC:
                case AirConst.AIR_TYPE_CO2:
                case AirConst.AIR_TYPE_AQI:
                case AirConst.AIR_TYPE_TVOC:
                case AirConst.AIR_TYPE_CO:
                    builder.append(AirUtil.getWarmResultStr(supportBean, settingBean));
                    break;
                case AirConst.AIR_TYPE_HUMIDITY:
                    int hPow = (int) Math.pow(10, supportBean.getPoint());
                    String humpStr = "下限值：" + settingBean.getWarmMin() / hPow + ", 上限值： "+ settingBean.getWarmMax() / hPow;
                    builder.append(humpStr);
                    break;
                case AirConst.AIR_TYPE_TEMP:
                    String tempStr = "下限值：" + settingBean.getWarmMin() + ", 上限值： "+ settingBean.getWarmMax();
                    builder.append(tempStr);
                    break;
                case AirConst.AIR_SETTING_VOICE:
                    builder.append("开关：").append(AirUtil.getSwitchStatus(settingBean.isOpen())).append(", Level: ").append(settingBean.getValue());
                    break;
                case AirConst.AIR_SETTING_WARM_DURATION:
                    builder.append("时长：").append(settingBean.getValue()).append(" S");
                    break;
                case AirConst.AIR_SETTING_WARM_VOICE:
                    builder.append("铃声：").append(settingBean.getValue());
                    break;
                case AirConst.AIR_SETTING_DEVICE_SELF_TEST:
                    // todo: 自检，暂时忽略
                    break;
                case AirConst.AIR_SETTING_BIND_DEVICE:
                    // todo: 设备绑定，暂时忽略
                    break;
                case AirConst.AIR_RESTORE_FACTORY_SETTINGS:
                    // todo: 恢复出厂设置：暂时忽略
                    break;
                case AirConst.AIR_TIME_FORMAT:
                    builder.append(AirUtil.getTimeFormat((int) settingBean.getValue()));
                    break;
                case AirConst.AIR_DATA_DISPLAY_MODE:
                    builder.append(settingBean.getValue());
                    break;
                case AirConst.AIR_SETTING_SWITCH_TEMP_UNIT:
                    builder.append("当前单位：").append(AirUtil.getTempUnitName(settingBean.getUnit()));
                    break;
                case AirConst.AIR_ALARM_CLOCK:
                    builder.append(AirUtil.dealCalResultAllAlarm(settingBean, supportBean));
                    break;
                case AirConst.AIR_CALIBRATION_PARAMETERS:
                    builder.append(AirUtil.dealCalResultAllSettings(settingBean, supportList));
                    break;
                case AirConst.AIR_BRIGHTNESS_EQUIPMENT:
                    builder.append("自动调节：" + AirUtil.getSwitchStatus(settingBean.isOpen()) + ", 档位/亮度：" + settingBean.getValue());
                    break;
                case AirConst.AIR_KEY_SOUND:
                case AirConst.AIR_ALARM_SOUND_EFFECT:
                case AirConst.AIR_ICON_DISPLAY:
                case AirConst.AIR_MONITORING_DISPLAY_DATA:
                    builder.append(AirUtil.getSwitchStatus(settingBean.isOpen()));
                    break;
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * 设置指令返回
     *
     * @param settingList settingList
     * @param supportList supportList
     * @Param resultInterface
     */
    public static void showResultSettings(SparseArray<StatusBean> settingList, SparseArray<SupportBean> supportList, @NonNull SettingResultInterface resultInterface) {
        for (int i = 0; i < settingList.size(); i++) {
            int type = settingList.keyAt(i);
            // 支持列表不包含，添加空字符串
            SupportBean supportBean = supportList.get(type);
            // 先把切换单位的屏蔽
            if (supportBean == null && AirConst.AIR_SETTING_SWITCH_TEMP_UNIT != type) {
                continue;
            }
            StatusBean resultBean = settingList.get(type);
            switch (type) {
                case AirConst.AIR_TYPE_FORMALDEHYDE:
                    resultInterface.onWarmResultHCHO(AirUtil.getWarmResultStr(supportBean, resultBean));
                    break;
                case AirConst.AIR_TYPE_PM_2_5:
                    resultInterface.onWarmResultPM2_5(AirUtil.getWarmResultStr(supportBean, resultBean));
                    break;
                case AirConst.AIR_TYPE_PM_1:
                    resultInterface.onWarmResultPM1_0(AirUtil.getWarmResultStr(supportBean, resultBean));
                    break;
                case AirConst.AIR_TYPE_PM_10:
                    resultInterface.onWarmResultPM10(AirUtil.getWarmResultStr(supportBean, resultBean));
                    break;
                case AirConst.AIR_TYPE_VOC:
                    resultInterface.onWarmResultVOC(AirUtil.getWarmResultStr(supportBean, resultBean));
                    break;
                case AirConst.AIR_TYPE_CO2:
                    resultInterface.onWarmResultCO2(AirUtil.getWarmResultStr(supportBean, resultBean));
                    break;
                case AirConst.AIR_TYPE_AQI:
                    resultInterface.onWarmResultAQI(AirUtil.getWarmResultStr(supportBean, resultBean));
                    break;
                case AirConst.AIR_TYPE_TVOC:
                    resultInterface.onWarmResultTVOC(AirUtil.getWarmResultStr(supportBean, resultBean));
                    break;
                case AirConst.AIR_TYPE_CO:
                    resultInterface.onWarmResultCO(AirUtil.getWarmResultStr(supportBean, resultBean));
                    break;
                case AirConst.AIR_TYPE_HUMIDITY:
                    String humpStr = "下限值：" + resultBean.getWarmMin() + ", 上限值： "+ resultBean.getWarmMax();
                    resultInterface.onWarmResultHumidity(humpStr);
                    break;
                case AirConst.AIR_TYPE_TEMP:
                    String tempStr = "下限值：" + resultBean.getWarmMin() + ", 上限值： "+ resultBean.getWarmMax();
                    resultInterface.onWarmResultTemp(tempStr);
                    break;
                case AirConst.AIR_SETTING_VOICE:
                    resultInterface.onResultVoice("开关：" + AirUtil.getSwitchStatus(resultBean.isOpen()) + ", Level: "+ resultBean.getValue());
                    break;
                case AirConst.AIR_SETTING_WARM_DURATION:
                    resultInterface.onResultWarmDuration("时长：" + resultBean.getValue() + " S");
                    break;
                case AirConst.AIR_SETTING_WARM_VOICE:
                    resultInterface.onResultWarmVoiceLevel("铃声：" + resultBean.getValue());
                    break;
                case AirConst.AIR_SETTING_DEVICE_SELF_TEST:
                    // todo: 自检，暂时忽略
                    break;
                case AirConst.AIR_SETTING_BIND_DEVICE:
                    // todo: 设备绑定，暂时忽略
                    break;
                case AirConst.AIR_RESTORE_FACTORY_SETTINGS:
                    // todo: 恢复出厂设置：暂时忽略
                    break;
                case AirConst.AIR_TIME_FORMAT:
                    resultInterface.onResultTimeFormat(AirUtil.getTimeFormat((int) resultBean.getValue()));
                    break;
                case AirConst.AIR_DATA_DISPLAY_MODE:
                    resultInterface.onResultDataDisplayMode("显示模式：" + resultBean.getValue());
                    break;
                case AirConst.AIR_SETTING_SWITCH_TEMP_UNIT:
                    resultInterface.onResultUnitChange("单位：" + AirUtil.getTempUnitName(resultBean.getUnit()));
                    break;
                case AirConst.AIR_ALARM_CLOCK:
                    resultInterface.onSettingAlarmResult(AirUtil.dealCalResultAllAlarm(resultBean, supportBean));
                    break;
                case AirConst.AIR_CALIBRATION_PARAMETERS:
                    dealCalResultSingleType(resultBean, resultInterface);
                    break;
                case AirConst.AIR_BRIGHTNESS_EQUIPMENT:
                    resultInterface.onResultBrightnessEquipment("自动调节：" + AirUtil.getSwitchStatus(resultBean.isOpen()) + ", 档位：" + resultBean.getValue());
                    break;
                case AirConst.AIR_KEY_SOUND:
                    resultInterface.onResultKeySound("按键音效开关：" + AirUtil.getSwitchStatus(resultBean.isOpen()));
                    break;
                case AirConst.AIR_ALARM_SOUND_EFFECT:
                    resultInterface.onResultAlarmSoundEffect("报警音效开关：" + AirUtil.getSwitchStatus(resultBean.isOpen()));
                    break;
                case AirConst.AIR_ICON_DISPLAY:
                    resultInterface.onResultIconDisplay("图标显示开关：" + AirUtil.getSwitchStatus(resultBean.isOpen()));
                    break;
                case AirConst.AIR_MONITORING_DISPLAY_DATA:
                    resultInterface.onResultMonitoringDisplayData("监测显示数据开关：" + AirUtil.getSwitchStatus(resultBean.isOpen()));
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 处理校验信息返回
     *
     * @param resultBean resultBean
     */
    private static void dealCalResultSingleType(StatusBean resultBean, SettingResultInterface resultInterface) {
        if (resultBean.getExtentObject() instanceof CalibrationListBean) {
            CalibrationListBean listBean = (CalibrationListBean) resultBean.getExtentObject();
            ArrayList<CalibrationListBean.CalibrationBean> list = listBean.getCalibrationBeanList();
            if (list == null || list.isEmpty()) {
                return;
            }
            for (CalibrationListBean.CalibrationBean bean : list) {
                String resultStr = AirUtil.getOperateStr(bean.getCalOperate());
                switch (bean.getCalType()){
                    case AirConst.AIR_TYPE_FORMALDEHYDE:
                        resultInterface.onCalResultHCHO(resultStr);
                        break;
                    case AirConst.AIR_TYPE_HUMIDITY:
                        resultInterface.onCalResultHumidity(resultStr);
                        break;
                    case AirConst.AIR_TYPE_PM_2_5:
                        resultInterface.onCalResultPM2_5(resultStr);
                        break;
                    case AirConst.AIR_TYPE_PM_1:
                        resultInterface.onCalResultPM1_0(resultStr);
                        break;
                    case AirConst.AIR_TYPE_PM_10:
                        resultInterface.onCalResultPM10(resultStr);
                        break;
                    case AirConst.AIR_TYPE_VOC:
                        resultInterface.onCalResultVOC(resultStr);
                        break;
                    case AirConst.AIR_TYPE_CO2:
                        resultInterface.onCalResultCO2(resultStr);
                        break;
                    case AirConst.AIR_TYPE_AQI:
                        resultInterface.onCalResultAQI(resultStr);
                        break;
                    case AirConst.AIR_TYPE_TVOC:
                        resultInterface.onCalResultTVOC(resultStr);
                        break;
                    case AirConst.AIR_TYPE_CO:
                        resultInterface.onCalResultCO(resultStr);
                        break;
                    case AirConst.AIR_TYPE_TEMP:
                        resultInterface.onCalResultTemp(resultStr);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    

}

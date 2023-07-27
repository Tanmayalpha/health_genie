package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.airdetector;

import android.util.SparseArray;

import java.util.Arrays;
import java.util.LinkedHashMap;

import cn.net.aicare.modulelibrary.module.airDetector.AirConst;
import cn.net.aicare.modulelibrary.module.airDetector.AlarmClockStatement;
import cn.net.aicare.modulelibrary.module.airDetector.BrightnessStatement;
import cn.net.aicare.modulelibrary.module.airDetector.StatusBean;
import cn.net.aicare.modulelibrary.module.airDetector.SupportBean;

/**
 * @author yesp
 */
public class AirDetectorShowUtil {

    public static String getTextSupportShow(SupportBean bean) {
        int type = bean.getType();
        StringBuilder builder = new StringBuilder();
        builder.append("支持的功能: ").append(AirUtil.getTypeName(type)).append(", ");
        switch (bean.getType()) {
            case AirConst.AIR_TYPE_FORMALDEHYDE:
            case AirConst.AIR_TYPE_HUMIDITY:
            case AirConst.AIR_TYPE_PM_2_5:
            case AirConst.AIR_TYPE_PM_1:
            case AirConst.AIR_TYPE_PM_10:
            case AirConst.AIR_TYPE_VOC:
            case AirConst.AIR_TYPE_CO2:
            case AirConst.AIR_TYPE_AQI:
            case AirConst.AIR_TYPE_TVOC:
            case AirConst.AIR_TYPE_CO:
            case AirConst.AIR_TYPE_TEMP:
                builder.append("max : ").append(bean.getMax()).append(", min: ").append(bean.getMin()).append(", 小数点：").append(bean.getPoint());
                break;
            case AirConst.AIR_SETTING_WARM:
                builder.append("数值：").append(bean.getCurValue()).append(", ").append(AirUtil.dealWarnSwitch((int) bean.getCurValue()));
                break;
            case AirConst.AIR_SETTING_DEVICE_ERROR:
            case AirConst.AIR_SETTING_DEVICE_SELF_TEST:
            case AirConst.AIR_RESTORE_FACTORY_SETTINGS:
                builder.append("数值：").append(bean.getCurValue());
                break;
            case AirConst.AIR_SETTING_VOICE:
            case AirConst.AIR_SETTING_WARM_VOICE:
                builder.append("max：").append(bean.getMax());
                break;
            case AirConst.AIR_SETTING_WARM_DURATION:
                builder.append("max：").append(bean.getMax()).append(" S");
                break;
            case AirConst.AIR_ALARM_CLOCK:
                AlarmClockStatement alarmClockStatement = (AlarmClockStatement) bean.getExtentObject();
                String alarmShow = "闹钟显示：" + alarmClockStatement.isShowIcon();
                String alarmCount = ", 闹钟数量：" + alarmClockStatement.getAlarmCount();
                String supportDelete = ", 支持添加删除闹钟：" + alarmClockStatement.isSupportDelete();
                String mode0 = ", 模式 0:一次性,当天有效：" + alarmClockStatement.isMode0();
                String mode1 = ", 模式 1:每天都响：" + alarmClockStatement.isMode1();
                String mode2 = ", 模式 2:周一至周五：" + alarmClockStatement.isMode2();
                String mode3 = ", 模式 3:周一至周六：" + alarmClockStatement.isMode3();
                String mode4 = ", 模式 4:自定义：" + alarmClockStatement.isMode4();
                builder.append(alarmShow).append(alarmCount).append(supportDelete).append(mode0).append(mode1).append(mode2).append(mode3).append(mode4);
                break;
            case AirConst.AIR_KEY_SOUND:
            case AirConst.AIR_ALARM_SOUND_EFFECT:
            case AirConst.AIR_ICON_DISPLAY:
            case AirConst.AIR_MONITORING_DISPLAY_DATA:
                builder.append("开/关切换支持：").append(bean.getCurValue() == 1 ? "1 - 支持" : "0 - 不支持");
                break;
            case AirConst.AIR_CALIBRATION_PARAMETERS:
                int[] types = (int[]) bean.getExtentObject();
                builder.append(Arrays.toString(types)).append(" - ").append(AirUtil.showTypeArrayName(types));
                break;
            case AirConst.AIR_TIME_FORMAT:
                int val = (int) bean.getCurValue();
                String support12 = ((val & 0x02) == 2) ? "1 - 支持" : "0 - 不支持";
                String support24 = ((val & 0x01) == 1) ? "1 - 支持" : "0 - 不支持";
                builder.append("12h 格式：").append(support12).append(", 24h 格式：").append(support24);
                break;
            case AirConst.AIR_BRIGHTNESS_EQUIPMENT:
                BrightnessStatement statement = (BrightnessStatement) bean.getExtentObject();
                String supportAuto = "自动调节:" + (statement.isAutoAdjust() ? "1 - 支持" : "0 - 不支持");
                String supportManual = "， 手动调节:" + (statement.isManualAdjust() ? "1 - 支持" : "0 - 不支持");
                String mode = "， 模式:" + (statement.getMode() == 1 ? "1 - 档位" : "0 - 百分比模式:0-100%");
                builder.append(supportAuto).append(supportManual).append(mode).append(", 档位：").append(statement.getLevelCount());
                break;
            case AirConst.AIR_DATA_DISPLAY_MODE:
                int[] ids = (int[]) bean.getExtentObject();
                builder.append("模式数量：").append(bean.getCurValue()).append("[vid, pid] - ").append(Arrays.toString(ids));
                break;
            case AirConst.AIR_SETTING_SWITCH_TEMP_UNIT:
                builder.append("温度: ").append(bean.getCurValue() == 1 ? "1 - 支持" : "0 - 不支持");
                break;
            case AirConst.AIR_PROTOCOL_VERSION:
                builder.append(bean.getCurValue());
                break;
            default:
                break;
        }
        return builder.toString();
    }

    /**
     * 实时状态返回
     *
     * @param statusBean
     * @param supportList
     * @return
     */
    public static String getTextStatusShow(StatusBean statusBean, SparseArray<SupportBean> supportList) {
        int type = statusBean.getType();
        // 支持列表不包含，添加空字符串
        SupportBean supportBean = supportList.get(type);
        if (supportBean == null && AirConst.AIR_SETTING_SWITCH_TEMP_UNIT != type && AirConst.AIR_SETTING_BATTEY != type) {
            return "不支持的类型：" + type;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("实时状态，").append(AirUtil.getTypeName(type)).append(" : ");
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
            case AirConst.AIR_TYPE_CO:
                double curValue = statusBean.getValue() / Math.pow(10, supportBean.getPoint());
                builder.append(curValue);
                break;
            case AirConst.AIR_SETTING_WARM_DURATION:
                builder.append(statusBean.getValue()).append(" 秒");
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
                builder.append("自动调节：" + AirUtil.getSwitchStatus(statusBean.isOpen()) + ", 亮度值/档位：" + statusBean.getValue());
                break;
            case AirConst.AIR_KEY_SOUND:
            case AirConst.AIR_ALARM_SOUND_EFFECT:
            case AirConst.AIR_ICON_DISPLAY:
            case AirConst.AIR_MONITORING_DISPLAY_DATA:
                builder.append(AirUtil.getSwitchStatus(statusBean.isOpen()));
                break;
            case AirConst.AIR_SETTING_SWITCH_TEMP_UNIT:
                builder.append("可忽略，不处理");
                break;
            default:
                break;
        }
        return builder.toString();
    }

    /**
     * 设置指令返回
     *
     * @param settingBean
     * @param supportList
     * @return
     */
    public static String getResultSettingsShow(StatusBean settingBean, SparseArray<SupportBean> supportList) {
        StringBuilder builder = new StringBuilder();
        int type = settingBean.getType();
        // 支持列表不包含，添加空字符串
        SupportBean supportBean = supportList.get(type);
        if (supportBean == null && AirConst.AIR_SETTING_SWITCH_TEMP_UNIT != type) {
            return "不支持的类型：" + type;
        }
        builder.append("返回参数值，").append(AirUtil.getTypeName(type)).append(" : ");
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
                String humpStr = "下限值：" + settingBean.getWarmMin() / hPow + ", 上限值： " + settingBean.getWarmMax() / hPow;
                builder.append(humpStr);
                builder.append(", 子开关：").append(AirUtil.getSwitchStatus(settingBean.isOpen()));
                break;
            case AirConst.AIR_TYPE_TEMP:
                String tempStr = "下限值：" + settingBean.getWarmMin() + ", 上限值： " + settingBean.getWarmMax();
                builder.append(tempStr);
                builder.append(", 子开关：").append(AirUtil.getSwitchStatus(settingBean.isOpen()));
                break;
            case AirConst.AIR_SETTING_WARM:
                builder.append(", 总开关：").append(AirUtil.getSwitchStatus(settingBean.isOpen()));
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
        return builder.toString();
    }

    public static LinkedHashMap<String, SetType> initSettingTypes() {
        LinkedHashMap<String, SetType> arrayMap = new LinkedHashMap<>();
        arrayMap.put("甲醛报警", new SetType(0x01, SetType.TYPE_VALUE_SWITCH));
        arrayMap.put("温度报警", new SetType(0x02, SetType.TYPE_MIN_MAX_SWITCH));
        arrayMap.put("湿度报警", new SetType(0x03, SetType.TYPE_MIN_MAX_SWITCH));
        arrayMap.put("PM2.5 报警", new SetType(0x04, SetType.TYPE_VALUE_SWITCH));
        arrayMap.put("PM1.0 报警", new SetType(0x05, SetType.TYPE_VALUE_SWITCH));
        arrayMap.put("PM10 报警", new SetType(0x06, SetType.TYPE_VALUE_SWITCH));
        arrayMap.put("VOC 报警", new SetType(0x07, SetType.TYPE_VALUE_SWITCH));
        arrayMap.put("C02 报警", new SetType(0x08, SetType.TYPE_VALUE_SWITCH));
        arrayMap.put("AQI 报警", new SetType(0x09, SetType.TYPE_VALUE_SWITCH));
        arrayMap.put("TVOC 报警", new SetType(0x10, SetType.TYPE_VALUE_SWITCH));
        arrayMap.put("C0 报警", new SetType(0x15, SetType.TYPE_VALUE_SWITCH));
        arrayMap.put("音量", new SetType(0x0b, SetType.TYPE_VALUE_SWITCH));
        arrayMap.put("报警时长", new SetType(0x0c, SetType.TYPE_VALUE));
        arrayMap.put("报警铃声值", new SetType(0x0d, SetType.TYPE_VALUE));
        arrayMap.put("单位切换", new SetType(0x11, SetType.TYPE_VALUE));
        arrayMap.put("时间格式切换", new SetType(0x19, SetType.TYPE_VALUE));
        arrayMap.put("设备亮度", new SetType(0x1a, SetType.TYPE_VALUE_SWITCH));
        arrayMap.put("按键音效", new SetType(0x1b, SetType.TYPE_SWITCH));
        arrayMap.put("报警音效", new SetType(0x1c, SetType.TYPE_SWITCH));
        arrayMap.put("图标显示", new SetType(0x1d, SetType.TYPE_SWITCH));
        arrayMap.put("监控显示", new SetType(0x1e, SetType.TYPE_SWITCH));
        arrayMap.put("显示模式", new SetType(0x1f, SetType.TYPE_VALUE));
        arrayMap.put("参数校准", new SetType(0x18, SetType.TYPE_CAL));
        arrayMap.put("闹钟", new SetType(0x16, SetType.TYPE_ALARM));
        return arrayMap;
    }
}

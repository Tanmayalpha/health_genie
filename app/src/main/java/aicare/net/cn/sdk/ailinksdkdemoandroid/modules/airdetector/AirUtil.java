package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.airdetector;

import android.text.TextUtils;
import android.util.SparseArray;

import java.util.ArrayList;

import cn.net.aicare.modulelibrary.module.airDetector.AirConst;
import cn.net.aicare.modulelibrary.module.airDetector.AlarmClockInfoList;
import cn.net.aicare.modulelibrary.module.airDetector.CalibrationListBean;
import cn.net.aicare.modulelibrary.module.airDetector.StatusBean;
import cn.net.aicare.modulelibrary.module.airDetector.SupportBean;

/**
 * @author yesp
 */
public class AirUtil {

    public static final SparseArray<String> typeNameArray = new SparseArray<>();

    static {
        typeNameArray.append(AirConst.AIR_TYPE_FORMALDEHYDE, "甲醛");
        typeNameArray.append(AirConst.AIR_TYPE_TEMP, "温度");
        typeNameArray.append(AirConst.AIR_TYPE_HUMIDITY, "湿度");
        typeNameArray.append(AirConst.AIR_TYPE_PM_2_5, "PM2.5");
        typeNameArray.append(AirConst.AIR_TYPE_PM_1, "PM1.0");
        typeNameArray.append(AirConst.AIR_TYPE_PM_10, "PM10");
        typeNameArray.append(AirConst.AIR_TYPE_VOC, "VOC");
        typeNameArray.append(AirConst.AIR_TYPE_CO2, "CO2");
        typeNameArray.append(AirConst.AIR_TYPE_AQI, "AQI");
        typeNameArray.append(AirConst.AIR_SETTING_WARM, "报警功能");
        typeNameArray.append(AirConst.AIR_SETTING_VOICE, "音量");
        typeNameArray.append(AirConst.AIR_SETTING_WARM_DURATION, "报警时长");
        typeNameArray.append(AirConst.AIR_SETTING_WARM_VOICE, "报警铃声");
        typeNameArray.append(AirConst.AIR_SETTING_DEVICE_ERROR, "设备故障");
        typeNameArray.append(AirConst.AIR_SETTING_DEVICE_SELF_TEST, "设备自检");
        typeNameArray.append(AirConst.AIR_TYPE_TVOC, "TVOC");
        typeNameArray.append(AirConst.AIR_SETTING_SWITCH_TEMP_UNIT, "单位切换");
        typeNameArray.append(AirConst.AIR_SETTING_BATTEY, "电池状态");
        typeNameArray.append(AirConst.AIR_SETTING_BIND_DEVICE, "设备绑定");
        typeNameArray.append(AirConst.AIR_SETTING_HEART, "心跳包");
        typeNameArray.append(AirConst.AIR_TYPE_CO, "CO");
        typeNameArray.append(AirConst.AIR_ALARM_CLOCK, "闹钟功能");
        typeNameArray.append(AirConst.AIR_RESTORE_FACTORY_SETTINGS, "恢复出厂设置");
        typeNameArray.append(AirConst.AIR_CALIBRATION_PARAMETERS, "参数校准");
        typeNameArray.append(AirConst.AIR_TIME_FORMAT, "时间格式");
        typeNameArray.append(AirConst.AIR_BRIGHTNESS_EQUIPMENT, "设备亮度");
        typeNameArray.append(AirConst.AIR_KEY_SOUND, "按键音效");
        typeNameArray.append(AirConst.AIR_ALARM_SOUND_EFFECT, "报警音效");
        typeNameArray.append(AirConst.AIR_ICON_DISPLAY, "图标显示");
        typeNameArray.append(AirConst.AIR_MONITORING_DISPLAY_DATA, "监控显示数据");
        typeNameArray.append(AirConst.AIR_DATA_DISPLAY_MODE, "数据显示模式");
    }


    /**
     * 根据 type 显示对应类型的名称
     *
     * @param type type
     * @return 类型的名称
     */
    public static String getTypeName(int type) {
        String typeName = typeNameArray.get(type);
        return TextUtils.isEmpty(typeName) ? "unknown type" : typeName;
    }

    /**
     * 类型名称列表
     *
     * @param types
     * @return
     */
    public static String showTypeArrayName(int[] types) {
        if (null != types) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < types.length; i++) {
                sb.append(getTypeName(types[i]));
                if (i != types.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            return sb.toString();
        }
        return "";
    }

    /**
     * 温度单位
     * @param unit
     * @return
     */
    public static String getTempUnitName(int unit) {
        if (unit == AirConst.UNIT_C) {
            return "℃";
        } else if (unit == AirConst.UNIT_F) {
            return "℉";
        }
        return "";
    }

    /**
     * 开关
     *
     * @param status
     * @return
     */
    public static String getSwitchStatus(boolean status) {
        return status ? "1 - 开" : "0 - 关";
    }

    /**
     * 时间显示格式
     *
     * @param value
     * @return
     */
    public static String getTimeFormat(int value) {
        if (value == 0x01) {
            return "12小时";
        } else if (value == 0x02) {
            return "24小时";
        } else {
            return "未知";
        }
    }

    /**
     * 电池状态
     * @param statusBean
     * @return
     */
    public static String getBatteryFormat(StatusBean statusBean) {
        String text = statusBean.getValue() + ", 充电状态:";
        int[] status = (int[]) statusBean.getExtentObject();
        if (status != null && status.length == 2) {
            if (status[0] == 1) {
                text += "在充电";
            } else {
                text += "未充电";
            }
            if (status[0] == 1) {
                text += ",电压状态:低电";
            } else {
                text += ",电压状态:电压正常";
            }
        }
        return text;
    }

    /**
     * 校准操作
     * @param value
     * @return
     */
    public static String getOperateStr(int value) {
        if (value == 0) {
            return "校准值加一";
        } else {
            return "校准值减一";
        }
    }

    /**
     * 处理校验状态返回
     *
     * @param resultBean resultBean
     */
    public static String dealCalResultAllStatus(StatusBean resultBean, SparseArray<SupportBean> supportList) {
        if (resultBean.getExtentObject() instanceof CalibrationListBean) {
            CalibrationListBean listBean = (CalibrationListBean) resultBean.getExtentObject();
            ArrayList<CalibrationListBean.CalibrationBean> list = listBean.getCalibrationBeanList();
            if (list == null || list.isEmpty()) {
                return "";
            }
            StringBuilder builder = new StringBuilder();
            for (CalibrationListBean.CalibrationBean bean : list) {
                int type = bean.getCalType();
                // 支持列表不包含，下一项
                SupportBean supportBean = supportList.get(type);
                if (supportBean == null) {
                    continue;
                }
                double curValue = bean.getCalValue() / Math.pow(10, supportBean.getPoint());
                builder.append(AirUtil.getTypeName(bean.getCalType())).append("- 校准值: ").append(curValue);
                builder.append("\n");
            }
            return builder.toString();
        }
        return "";
    }

    /**
     * 处理校验设置返回
     *
     * @param resultBean resultBean
     */
    public static String dealCalResultAllSettings(StatusBean resultBean, SparseArray<SupportBean> supportList) {
        if (resultBean.getExtentObject() instanceof CalibrationListBean) {
            CalibrationListBean listBean = (CalibrationListBean) resultBean.getExtentObject();
            ArrayList<CalibrationListBean.CalibrationBean> list = listBean.getCalibrationBeanList();
            if (list == null || list.isEmpty()) {
                return "";
            }
            StringBuilder builder = new StringBuilder();
            for (CalibrationListBean.CalibrationBean bean : list) {
                int type = bean.getCalType();
                // 支持列表不包含，下一项
                SupportBean supportBean = supportList.get(type);
                if (supportBean == null) {
                    continue;
                }
                builder.append(AirUtil.getTypeName(bean.getCalType())).append(getOperateStr(bean.getCalOperate()));
                builder.append("\n");
            }
            return builder.toString();
        }
        return "";
    }

    /**
     * 报警值
     *
     * @param supportBean
     * @param resultBean
     * @return
     */
    public static String getWarmResultStr(SupportBean supportBean, StatusBean resultBean) {
        double curValue = resultBean.getWarmMax() / Math.pow(10, supportBean.getPoint());
        return "报警值：" + curValue + ", 开关：" + AirUtil.getSwitchStatus(resultBean.isOpen());
    }

    /**
     * 闹钟显示
     * @param resultBean
     * @param supportBean
     * @return
     */
    public static String dealCalResultAllAlarm(StatusBean resultBean, SupportBean supportBean) {
        if (resultBean.getExtentObject() instanceof AlarmClockInfoList) {
            AlarmClockInfoList infoList = (AlarmClockInfoList) resultBean.getExtentObject();
            ArrayList<AlarmClockInfoList.AlarmInfo> list = infoList.getAlarmList();
            if (list == null || list.size() == 0) {
                return "[]";
            }
            StringBuilder builder = new StringBuilder();
            for (AlarmClockInfoList.AlarmInfo bean : list) {
                builder.append(getSwitchStatus(bean.getSwitchStatus() == 1));
                builder.append(", 删除：").append(bean.isDeleted());
                builder.append(", 编号：").append(bean.getId());
                builder.append(", 模式：").append(bean.getMode());
                builder.append(", 闹钟时间：").append(bean.getHour()).append(":").append(bean.getMinute());
                builder.append(", 闹钟周期：").append(dealAlarmDay(bean.getAlarmDays()));
                builder.append("\n");
            }
            return builder.toString();
        } else {
            return "[]";
        }
    }

    private static String dealAlarmDay(int[] days){
        if (days == null) {
            return "";
        }
        String[] dayStr = {"未知","周一","周二","周三","周四","周五","周六","周日"};
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 1; i < days.length; i++) {
            if (days[i] == 1) {
                builder.append(dayStr[i]).append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }
}

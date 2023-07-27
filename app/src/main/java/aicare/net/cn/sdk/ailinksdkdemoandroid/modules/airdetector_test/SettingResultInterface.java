package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.airdetector_test;

public interface SettingResultInterface {

    /**
     * 甲醛报警
     * @param content
     */
    public void onWarmResultHCHO(String content);

    /**
     * 温度报警
     * @param content
     */
    public void onWarmResultTemp(String content);
    /**
     * 湿度报警
     * @param content
     */
    public void onWarmResultHumidity(String content);
    /**
     * PM2_5报警
     * @param content
     */
    public void onWarmResultPM2_5(String content);
    /**
     * PM1_0报警
     * @param content
     */
    public void onWarmResultPM1_0(String content);
    /**
     * PM10报警
     * @param content
     */
    public void onWarmResultPM10(String content);
    /**
     * VOC报警
     * @param content
     */
    public void onWarmResultVOC(String content);
    /**
     * CO2报警
     * @param content
     */
    public void onWarmResultCO2(String content);
    /**
     * AQI报警
     * @param content
     */
    public void onWarmResultAQI(String content);
    /**
     * TVOC报警
     * @param content
     */
    public void onWarmResultTVOC(String content);
    /**
     * CO报警
     * @param content
     */
    public void onWarmResultCO(String content);
    /**
     * 音量状态
     * @param content
     */
    public void onResultVoice(String content);
    /**
     * 报警时长
     * @param content
     */
    public void onResultWarmDuration(String content);
    /**
     * 报警铃声值
     * @param content
     */
    public void onResultWarmVoiceLevel(String content);
    /**
     * 温度单位切换
     * @param content
     */
    public void onResultUnitChange(String content);
    /**
     * 时间格式设置
     * @param content
     */
    public void onResultTimeFormat(String content);
    /**
     * 设备亮度
     * @param content
     */
    public void onResultBrightnessEquipment(String content);
    /**
     * 按键音效
     * @param content
     */
    public void onResultKeySound(String content);
    /**
     * 报警音效
     * @param content
     */
    public void onResultAlarmSoundEffect(String content);
    /**
     * 图标显示
     * @param content
     */
    public void onResultIconDisplay(String content);
    /**
     * 监控显示开关
     * @param content
     */
    public void onResultMonitoringDisplayData(String content);
    /**
     * 数据显示模式设置
     * @param content
     */
    public void onResultDataDisplayMode(String content);

    /**
     * 报警总开关设置
     * @param content
     */
    public void onResultMasterWarnSwitch(String content);

    /**
     * 甲醛校准
     * @param content
     */
    public void onCalResultHCHO(String content);

    /**
     * 温度校准
     * @param content
     */
    public void onCalResultTemp(String content);
    /**
     * 湿度校准
     * @param content
     */
    public void onCalResultHumidity(String content);
    /**
     * PM2_5校准
     * @param content
     */
    public void onCalResultPM2_5(String content);
    /**
     * PM1_0校准
     * @param content
     */
    public void onCalResultPM1_0(String content);
    /**
     * PM10校准
     * @param content
     */
    public void onCalResultPM10(String content);
    /**
     * VOC校准
     * @param content
     */
    public void onCalResultVOC(String content);
    /**
     * CO2校准
     * @param content
     */
    public void onCalResultCO2(String content);
    /**
     * AQI校准
     * @param content
     */
    public void onCalResultAQI(String content);
    /**
     * TVOC校准
     * @param content
     */
    public void onCalResultTVOC(String content);
    /**
     * CO校准
     * @param content
     */
    public void onCalResultCO(String content);
    /**
     * 闹钟设置
     * @param content
     */
    public void onSettingAlarmResult(String content);


}

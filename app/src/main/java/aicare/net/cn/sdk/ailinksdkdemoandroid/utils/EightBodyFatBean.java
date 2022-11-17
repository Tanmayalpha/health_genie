package aicare.net.cn.sdk.ailinksdkdemoandroid.utils;

public class EightBodyFatBean {
    private String weight;     //体重，
    private float bmi;     //体质指数，
    private float bfr;     //体脂率，
    private float sfr;     //皮下脂肪率，
    private float uvi;     //内脏脂肪率，
    private float rom;     //肌肉率，
    private float bmr;     //基础代谢率，
    private String bm;     //骨骼质量，
    private float vwc;     //水含量，
    private int bodyAge;     //身体年龄，
    private float pp;     //蛋白率，
    private double adcFoot;     //双脚阻抗，
    private double adcHand;//双手阻抗
    private double adcLeftHand; //左手阻抗
    private double adcRightHand; //右手阻抗
    private double adcLeftFoot;  //左脚阻抗
    private double adcRightFoot; //右脚阻抗
    private double adcLeftBody;
    private double adcRightBody;
    private double adcRightHandLeftFoot;
    private double adcLeftHandRightFoot;
    /**
     * adc身体
     */
    private double adcBody;
    /**
     * 算术
     */
    private int arithmetic;
    /**
     * 心率
     */
    private int heartRate;
    private String fatMassRightTop;     //体脂-右上
    private String fatMassRightBottom;     //体脂-右下
    private String fatMassLeftTop;     //体脂-左上
    private String fatMassLeftBottom;     //体脂-左下
    private String fatMassBody;     //体脂-躯干
    private String muscleMassRightTop;     //肌肉-右上
    private String muscleMassRightBottom;     //肌肉-右下
    private String muscleMassLeftTop;     //肌肉-左上
    private String muscleMassLeftBottom;     //肌肉-左下
    private String muscleMassBody;     //肌肉-躯干


    private String testAdc;

    /**
     * 骨骼肌公斤
     */
    private String bhSkeletalMuscleKg;
    /**
     * 骨骼肌公斤级别
     */
    private String bhSkeletalMuscleKgLevel;
    /**
     * 骨骼肌公斤列表或标准
     */
    private String bhSkeletalMuscleKgListUnderOrStandard;
    /**
     * 骨骼肌公斤标准或优秀列表
     */
    private String bhSkeletalMuscleKgListStandardOrExcellent;


    public void setBhSkeletalMuscleKg(String bhSkeletalMuscleKg) {
        this.bhSkeletalMuscleKg = bhSkeletalMuscleKg;
    }

    public void setBhSkeletalMuscleKgLevel(String bhSkeletalMuscleKgLevel) {
        this.bhSkeletalMuscleKgLevel = bhSkeletalMuscleKgLevel;
    }

    public void setBhSkeletalMuscleKgListUnderOrStandard(String bhSkeletalMuscleKgListUnderOrStandard) {
        this.bhSkeletalMuscleKgListUnderOrStandard = bhSkeletalMuscleKgListUnderOrStandard;
    }

    public void setBhSkeletalMuscleKgListStandardOrExcellent(String bhSkeletalMuscleKgListStandardOrExcellent) {
        this.bhSkeletalMuscleKgListStandardOrExcellent = bhSkeletalMuscleKgListStandardOrExcellent;
    }

    public String getBhSkeletalMuscleKg() {
        return bhSkeletalMuscleKg;
    }

    public String getBhSkeletalMuscleKgLevel() {
        return bhSkeletalMuscleKgLevel;
    }

    public String getBhSkeletalMuscleKgListUnderOrStandard() {
        return bhSkeletalMuscleKgListUnderOrStandard;
    }

    public String getBhSkeletalMuscleKgListStandardOrExcellent() {
        return bhSkeletalMuscleKgListStandardOrExcellent;
    }

    public String getTestAdc() {
        return testAdc;
    }

    public void setTestAdc(String testAdc) {
        this.testAdc = testAdc;
    }



    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public float getBfr() {
        return bfr;
    }

    public void setBfr(float bfr) {
        this.bfr = bfr;
    }

    public float getSfr() {
        return sfr;
    }

    public void setSfr(float sfr) {
        this.sfr = sfr;
    }

    public float getUvi() {
        return uvi;
    }

    public void setUvi(float uvi) {
        this.uvi = uvi;
    }

    public float getRom() {
        return rom;
    }

    public void setRom(float rom) {
        this.rom = rom;
    }

    public float getBmr() {
        return bmr;
    }

    public void setBmr(float bmr) {
        this.bmr = bmr;
    }

    public String getBm() {
        return bm;
    }

    public void setBm(String bm) {
        this.bm = bm;
    }

    public float getVwc() {
        return vwc;
    }

    public void setVwc(float vwc) {
        this.vwc = vwc;
    }

    public int getBodyAge() {
        return bodyAge;
    }

    public void setBodyAge(int bodyAge) {
        this.bodyAge = bodyAge;
    }

    public float getPp() {
        return pp;
    }

    public void setPp(float pp) {
        this.pp = pp;
    }

    public double getAdcFoot() {
        return adcFoot;
    }

    public void setAdcFoot(double adcFoot) {
        this.adcFoot = adcFoot;
    }

    public double getAdcHand() {
        return adcHand;
    }

    public void setAdcHand(double adcHand) {
        this.adcHand = adcHand;
    }

    public double getAdcLeftHand() {
        return adcLeftHand;
    }

    public void setAdcLeftHand(double adcLeftHand) {
        this.adcLeftHand = adcLeftHand;
    }

    public double getAdcRightHand() {
        return adcRightHand;
    }

    public void setAdcRightHand(double adcRightHand) {
        this.adcRightHand = adcRightHand;
    }

    public double getAdcLeftFoot() {
        return adcLeftFoot;
    }

    public void setAdcLeftFoot(double adcLeftFoot) {
        this.adcLeftFoot = adcLeftFoot;
    }

    public double getAdcRightFoot() {
        return adcRightFoot;
    }

    public void setAdcRightFoot(double adcRightFoot) {
        this.adcRightFoot = adcRightFoot;
    }

    public double getAdcLeftBody() {
        return adcLeftBody;
    }

    public void setAdcLeftBody(double adcLeftBody) {
        this.adcLeftBody = adcLeftBody;
    }

    public double getAdcRightBody() {
        return adcRightBody;
    }

    public void setAdcRightBody(double adcRightBody) {
        this.adcRightBody = adcRightBody;
    }

    public double getAdcRightHandLeftFoot() {
        return adcRightHandLeftFoot;
    }

    public void setAdcRightHandLeftFoot(double adcRightHandLeftFoot) {
        this.adcRightHandLeftFoot = adcRightHandLeftFoot;
    }

    public double getAdcLeftHandRightFoot() {
        return adcLeftHandRightFoot;
    }

    public void setAdcLeftHandRightFoot(double adcLeftHandRightFoot) {
        this.adcLeftHandRightFoot = adcLeftHandRightFoot;
    }

    public double getAdcBody() {
        return adcBody;
    }

    public void setAdcBody(double adcBody) {
        this.adcBody = adcBody;
    }

    public String getFatMassRightTop() {
        return fatMassRightTop;
    }

    public void setFatMassRightTop(String fatMassRightTop) {
        this.fatMassRightTop = fatMassRightTop;
    }

    public String getFatMassRightBottom() {
        return fatMassRightBottom;
    }

    public void setFatMassRightBottom(String fatMassRightBottom) {
        this.fatMassRightBottom = fatMassRightBottom;
    }

    public String getFatMassLeftTop() {
        return fatMassLeftTop;
    }

    public void setFatMassLeftTop(String fatMassLeftTop) {
        this.fatMassLeftTop = fatMassLeftTop;
    }

    public String getFatMassLeftBottom() {
        return fatMassLeftBottom;
    }

    public void setFatMassLeftBottom(String fatMassLeftBottom) {
        this.fatMassLeftBottom = fatMassLeftBottom;
    }

    public String getFatMassBody() {
        return fatMassBody;
    }

    public void setFatMassBody(String fatMassBody) {
        this.fatMassBody = fatMassBody;
    }

    public String getMuscleMassRightTop() {
        return muscleMassRightTop;
    }

    public void setMuscleMassRightTop(String muscleMassRightTop) {
        this.muscleMassRightTop = muscleMassRightTop;
    }

    public String getMuscleMassRightBottom() {
        return muscleMassRightBottom;
    }

    public void setMuscleMassRightBottom(String muscleMassRightBottom) {
        this.muscleMassRightBottom = muscleMassRightBottom;
    }

    public String getMuscleMassLeftTop() {
        return muscleMassLeftTop;
    }

    public void setMuscleMassLeftTop(String muscleMassLeftTop) {
        this.muscleMassLeftTop = muscleMassLeftTop;
    }

    public String getMuscleMassLeftBottom() {
        return muscleMassLeftBottom;
    }

    public void setMuscleMassLeftBottom(String muscleMassLeftBottom) {
        this.muscleMassLeftBottom = muscleMassLeftBottom;
    }

    public String getMuscleMassBody() {
        return muscleMassBody;
    }

    public void setMuscleMassBody(String muscleMassBody) {
        this.muscleMassBody = muscleMassBody;
    }

    public int getArithmetic() {
        return arithmetic;
    }

    public void setArithmetic(int arithmetic) {
        this.arithmetic = arithmetic;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getHeartRate() {
        return heartRate;
    }

    @Override
    public String toString() {
        return "EightBodyFatBean{" + "weight='" + weight + '\'' + ", bmi=" + bmi + ", bfr=" + bfr + ", sfr=" + sfr + ", uvi=" + uvi + ", rom=" + rom + ", bmr=" + bmr + ", bm='" + bm + '\'' + ", " +
                "vwc=" + vwc + ", bodyAge=" + bodyAge + ", pp=" + pp + ", adcFoot=" + adcFoot + ", adcHand=" + adcHand + ", adcLeftHand=" + adcLeftHand + ", adcRightHand=" + adcRightHand + ", " +
                "adcLeftFoot=" + adcLeftFoot + ", adcRightFoot=" + adcRightFoot + ", adcLeftBody=" + adcLeftBody + ", adcRightBody=" + adcRightBody + ", adcRightHandLeftFoot=" + adcRightHandLeftFoot + ", adcLeftHandRightFoot=" + adcLeftHandRightFoot + ", adcBody=" + adcBody + ", arithmetic=" + arithmetic + ", heartRate=" + heartRate + ", fatMassRightTop='" + fatMassRightTop + '\'' + ", fatMassRightBottom='" + fatMassRightBottom + '\'' + ", fatMassLeftTop='" + fatMassLeftTop + '\'' + ", fatMassLeftBottom='" + fatMassLeftBottom + '\'' + ", fatMassBody='" + fatMassBody + '\'' + ", muscleMassRightTop='" + muscleMassRightTop + '\'' + ", muscleMassRightBottom='" + muscleMassRightBottom + '\'' + ", muscleMassLeftTop='" + muscleMassLeftTop + '\'' + ", muscleMassLeftBottom='" + muscleMassLeftBottom + '\'' + ", muscleMassBody='" + muscleMassBody + '\'' + ", testAdc='" + testAdc + '\'' + '}';
    }

}

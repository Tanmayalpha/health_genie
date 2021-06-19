package aicare.net.cn.sdk.ailinksdkdemoandroid;

public class EightBodyfatAdc {


    private int adcFoot;     //双脚阻抗，
    private int adcHand;//双手阻抗
    private int adcLeftHand; //左手阻抗
    private int adcRightHand; //右手阻抗
    private int adcLeftFoot;  //左脚阻抗
    private int adcRightFoot; //右脚阻抗
    private int adcLeftBody;
    private int adcRightBody;
    private int adcRightHandLeftFoot;
    private int adcLeftHandRightFoot;
    private int adcBody;


    public int getAdcFoot() {
        return adcFoot;
    }

    public void setAdcFoot(int adcFoot) {
        this.adcFoot = adcFoot;
    }

    public int getAdcHand() {
        return adcHand;
    }

    public void setAdcHand(int adcHand) {
        this.adcHand = adcHand;
    }

    public int getAdcLeftHand() {
        return adcLeftHand;
    }

    public void setAdcLeftHand(int adcLeftHand) {
        this.adcLeftHand = adcLeftHand;
    }

    public int getAdcRightHand() {
        return adcRightHand;
    }

    public void setAdcRightHand(int adcRightHand) {
        this.adcRightHand = adcRightHand;
    }

    public int getAdcLeftFoot() {
        return adcLeftFoot;
    }

    public void setAdcLeftFoot(int adcLeftFoot) {
        this.adcLeftFoot = adcLeftFoot;
    }

    public int getAdcRightFoot() {
        return adcRightFoot;
    }

    public void setAdcRightFoot(int adcRightFoot) {
        this.adcRightFoot = adcRightFoot;
    }

    public int getAdcLeftBody() {
        return adcLeftBody;
    }

    public void setAdcLeftBody(int adcLeftBody) {
        this.adcLeftBody = adcLeftBody;
    }

    public int getAdcRightBody() {
        return adcRightBody;
    }

    public void setAdcRightBody(int adcRightBody) {
        this.adcRightBody = adcRightBody;
    }

    public int getAdcRightHandLeftFoot() {
        return adcRightHandLeftFoot;
    }

    public void setAdcRightHandLeftFoot(int adcRightHandLeftFoot) {
        this.adcRightHandLeftFoot = adcRightHandLeftFoot;
    }

    public int getAdcLeftHandRightFoot() {
        return adcLeftHandRightFoot;
    }

    public void setAdcLeftHandRightFoot(int adcLeftHandRightFoot) {
        this.adcLeftHandRightFoot = adcLeftHandRightFoot;
    }

    public int getAdcBody() {
        return adcBody;
    }

    public void setAdcBody(int adcBody) {
        this.adcBody = adcBody;
    }


    @Override
    public String toString() {
        return "加密阻抗:" +
                " \n双脚=" + adcFoot +
                " \n双手=" + adcHand +
                " \n左手=" + adcLeftHand +
                " \n右手=" + adcRightHand +
                " \n左脚=" + adcLeftFoot +
                " \n右脚=" + adcRightFoot +
                " \n左躯干=" + adcLeftBody +
                " \n右躯干=" + adcRightBody +
                " \n右手左脚=" + adcRightHandLeftFoot +
                " \n左手右脚=" + adcLeftHandRightFoot +
                " \n全身=" + adcBody ;
    }
}

package aicare.net.cn.sdk.ailinksdkdemoandroid.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * xing<br>
 * 2019/4/29<br>
 * 单位转换工具类
 */
public class UnitUtils {

//-------------重量------------

    /**
     * st转lb
     */
    public static float stToLb(float st) {
        float lb = st * 14;
        return lb;
    }
//    public static float LbToSt(float lb) {
//        float st = lb / 14;
//        return st;
//    }

    /**
     * lb转oz
     */
    public static float lbToOz(float lb) {
        float oz = lb * 16;
        return oz;
    }

    /**
     * oz转lb:oz
     */
    public static float[] ozToLb(float ozSize) {
        float[] ozFloatS = new float[2];
        int lb = (int) ozSize / 16;
        float oz = ozSize % 16f;
        ozFloatS[0] = lb;
        ozFloatS[1] = oz;
        return ozFloatS;
    }

    /**
     * oz转lb
     */
    public static float ozToLbLb(float ozSize) {
        return ozSize / 16f;
    }

    /**
     * lb转st:lb
     */
    public static float[] LbToSt(float lbSize) {
        float[] lbFloatS = new float[2];
        int st = (int) lbSize / 14;
        float lb = lbSize % 14f;
        lbFloatS[0] = st;
        lbFloatS[1] = lb;
        return lbFloatS;
    }

    /**
     * lb转st
     */
    public static float LbToStSt(float lbSize) {
        return lbSize / 14f;
    }

    /**
     * oz转g
     */
    public static float ozToG(float oz) {
        float g = oz * 28.3495F;
        return g;
    }

    public static float gToOz(float g) {
        return g / 28.3495F;
    }


    /**
     * 千克转克
     */
    public static float kgToG(float kg) {
        return kg * 1000;
    }

    public static float gToKG(float g) {
        return g / 1000;
    }

    /**
     * 斤转克
     */
    public static float fgToG(float fg) {
        return fg * 500;
    }

    public static float gToFG(float g) {
        return g / 500;
    }

    /**
     * 斤转Kg
     */
    public static float fgToKG(float fg) {
        return fg / 2;
    }


//-----------温度-------------

    /**
     * 摄氏度转华氏度,可能会出现小数点精度问题,例:xx.99999999
     */
    public static float CToF(float c) {
        float f = c * 1.8F + 32;
        return f;
   }


    public static float CToFOne(float c) {
        float f = c * 1.8F + 32;
        return Math.round(f * 10) / 10f;
    }

    /**
     * 华氏度转摄氏度
     */
    public static float FToC(float f) {
        float c = (f - 32) / 1.8f;
        return c;
    }
    /**
     * 华氏度转度
     */
    public static float FToCOne(float f) {
        float c = (f - 32) / 1.8f;
        return Math.round(c * 10) / 10f;
    }

    //------------压力-----------------------

    /**
     * 千帕转毫米汞柱
     */
    public static float kpaToMmhg(float kpa) {
        float mmhg = (kpa * 7.50f);
        return mmhg;
    }

    /**
     * 毫米汞柱转千帕
     */
    public static float mmhgToKpa(float mmhg) {
        float kpa = (mmhg / 7.50f);
        return kpa;
    }

    //-----------长度--------------------

    public static float feetToInch(float feet) {
        float inch = (feet * 12f);
        return inch;
    }

    /**
     * Inch转Feet:Inch
     */
    public static float[] inchToFeet(float inchSize) {
        float[] feetS = new float[2];
        int feet = (int) (inchSize / 12);
        float inch = (inchSize % 12f);
        feetS[0] = feet;
        feetS[1] = inch;
        return feetS;
    }

    /**
     * Inch转Feet
     */
    public static float inchToFeetFeet(float inchSize) {
        return inchSize / 12f;
    }


    public static float inchToCm(float inch) {
        float cm = (inch * 2.54f);
        return cm;
    }

    public static float cmToInch(float cm) {
        return cm / 2.54F;
    }


    /**
     * 米转码
     */
    public static float mToYard(float m) {
        return m * 1.0936133F;
    }


    /**
     * 码转英里
     */
    public static float yardToMi(float yard) {
        return yard / 1760F;
    }


    //----------转换用----------


    /**
     * 保留小数位,不足后面补0
     *
     * @param decimal 保留几位小数,后面补0
     * @param value   原始小数
     * @return 保留后的数据
     */
    public static String getHoldDecimal(int decimal, float value) {
        if (decimal > 0) {

            StringBuilder decimalStr = new StringBuilder("0.");
            for (int i = 0; i < decimal; i++) {
                decimalStr.append("0");
            }

            DecimalFormat df = new DecimalFormat(decimalStr.toString());
            return df.format(value).replace(",", ".");

        }
        String s = String.format(Locale.US, "%."+decimal+"f", value);
        return s;
    }


    /**
     * 保留小数位,不足后面补0
     *
     * @param decimal 保留几位小数,后面补0
     * @param value   原始小数
     * @return 保留后的数据
     */
    public static String getHoldDecimalNotRounding(int decimal, double value) {
        if (decimal > 0) {
            StringBuilder decimalStr = new StringBuilder("0.");
            for (int i = 0; i < decimal; i++) {
                decimalStr.append("0");
            }
            DecimalFormat df = new DecimalFormat(decimalStr.toString());
            df.setRoundingMode(RoundingMode.DOWN);//向下取,丢掉后面的
            return df.format(value).replace(",", ".");

        }
        return String.valueOf((int) value);
    }

}

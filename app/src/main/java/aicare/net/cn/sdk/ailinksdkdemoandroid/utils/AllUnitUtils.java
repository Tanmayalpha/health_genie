package aicare.net.cn.sdk.ailinksdkdemoandroid.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.config.UserUnitConfig;


/**
 * 总的单位换算工具类
 */
public class AllUnitUtils {


    public final static String LB_SPLIT = ":";
    public final static String NO_DATA = "-1";
    public final static String INCH_SPLIT_ONE = "′";
    public final static String INCH_SPLIT_ONE_1 = "'";
    public final static String INCH_SPLIT_TWO = "″";
    public final static String INCH_SPLIT_TWO_1 = "\"";

    public static String getHeightToUnit(int unit, int newUnit, String data, int decimal) {
        String height = "";
        if (TextUtils.isEmpty(data) || NO_DATA.equalsIgnoreCase(data)) {
            return NO_DATA;
        }
        if (unit == newUnit) {
            height = data;
        }
        switch (newUnit) {

            case UserUnitConfig.USER_UNIT_CM:
                height = getHeightToCm(unit, data, decimal);
                break;

            case UserUnitConfig.USER_UNIT_FEET:
                height = getHeightToFeetFeet(unit, data, decimal);
                break;
            case UserUnitConfig.USER_UNIT_INCH:
                height = getHeightToInch(unit, data, decimal);
                break;

        }
        return height;

    }


    public static String getHeightToFeetFeet(int unit, String data, int decimal) {
        String height = "";
        float inch;
        float cm;
        if (TextUtils.isEmpty(data) || NO_DATA.equalsIgnoreCase(data)) {
            return NO_DATA;
        }
        if (unit == UserUnitConfig.USER_UNIT_FEET) {//单位相同直接返回
            height = data;
        }

        switch (unit) {
            case UserUnitConfig.USER_UNIT_CM:
                inch = UnitUtils.cmToInch(Float.parseFloat(data));
                height = getHeightInchToFeet(inch, decimal);
                break;
            case UserUnitConfig.USER_UNIT_INCH:
                height = getHeightInchToFeet(Float.parseFloat(data), decimal);
                break;
        }


        return height;

    }

    private static String getHeightInchToFeet(float heightStr, int decimal) {
        String inch;
        float[] feetS = UnitUtils.inchToFeet(heightStr);
        String decimalStr = "%." + decimal + "f";
        inch = String.format(Locale.US, decimalStr, feetS[1]);
        return ((int) feetS[0]) + INCH_SPLIT_ONE + inch + INCH_SPLIT_TWO;
    }


    /**
     * 服务器返回(数据库读取)
     *
     * @param unit    当前的单位
     * @param data    数据
     * @param decimal 小数位
     * @return 返回指定单位显示
     */
    public static String getHeightToCm(int unit, String data, int decimal) {
        double height = -1.0;
        float inch;
        if (TextUtils.isEmpty(data) || NO_DATA.equalsIgnoreCase(data)) {
            return NO_DATA;
        }
        if (unit == UserUnitConfig.USER_UNIT_CM) {//单位相同直接返回
            height = Float.parseFloat(data);
        }

        switch (unit) {
            case UserUnitConfig.USER_UNIT_INCH:
                if (data.contains(INCH_SPLIT_ONE)) {
                    return NO_DATA;
                }
                height = UnitUtils.inchToCm(Float.parseFloat(data));
                break;

            case UserUnitConfig.USER_UNIT_FEET:
                inch = getHeightFeetToInch(data);
                height = UnitUtils.inchToCm(inch);
                break;

        }
        return getHoldDecimal(decimal, (float) height);
    }


    /**
     * 服务器返回(数据库读取)
     *
     * @param unit    当前的单位
     * @param data    数据
     * @param decimal 小数位
     * @return 返回指定单位显示
     */
    public static String getHeightToInch(int unit, String data, int decimal) {
        float height = -1f;
        float inch;
        if (TextUtils.isEmpty(data) || NO_DATA.equalsIgnoreCase(data)) {
            return NO_DATA;
        }
        if (unit == UserUnitConfig.USER_UNIT_INCH) {//单位相同直接返回
            height = Float.parseFloat(data);
        }

        switch (unit) {
            case UserUnitConfig.USER_UNIT_CM:
                height = UnitUtils.cmToInch(Float.parseFloat(data));
                break;

            case UserUnitConfig.USER_UNIT_FEET:
                height = getHeightFeetToInch(data);
                break;

        }
        return getHoldDecimal(decimal, height);
    }


    /**
     * Feet:Inch转Inch
     */
    private static float getHeightFeetToInch(String feetStr) {
        float feet = 0;
        float inch = 0;
        if (feetStr.contains(INCH_SPLIT_ONE) && feetStr.contains(INCH_SPLIT_TWO)) {
            String[] strings = feetStr.split(INCH_SPLIT_ONE);
            feet = Float.parseFloat(strings[0]);
            inch = Float.parseFloat(strings[1].substring(0, strings[1].length() - 1));
        } else if (feetStr.contains(INCH_SPLIT_ONE_1) && feetStr.contains(INCH_SPLIT_TWO_1)) {
            String[] strings = feetStr.split(INCH_SPLIT_ONE_1);
            feet = Float.parseFloat(strings[0]);
            inch = Float.parseFloat(strings[1].substring(0, strings[1].length() - 1));
        }
        return UnitUtils.feetToInch(feet) + inch;
    }


    /**
     * 服务器返回(数据库读取)
     *
     * @param unit    当前的单位
     * @param data    数据
     * @param decimal 小数位
     * @return 返回指定单位显示
     */
    public static String getWeightToKg(int unit, String data, int decimal) {
        float weight = -1f;
        float g;
        float oz;
        if (TextUtils.isEmpty(data) || NO_DATA.equalsIgnoreCase(data)) {
            return NO_DATA;
        }
        if (unit == UserUnitConfig.USER_UNIT_KG) {//单位相同直接返回
            weight = Float.parseFloat(String.format(Locale.US, data));

        }

        switch (unit) {
            case UserUnitConfig.USER_UNIT_FG:
                weight = UnitUtils.fgToKG(Float.parseFloat(data));
                break;

            case UserUnitConfig.USER_UNIT_OZ:
                g = UnitUtils.ozToG(Float.parseFloat(data));
                weight = UnitUtils.gToKG(g);
                break;
            case UserUnitConfig.USER_UNIT_LB:
                oz = getWeightLbToOz(data);
                g = UnitUtils.ozToG(oz);
                weight = UnitUtils.gToKG(g);
                break;
            case UserUnitConfig.USER_UNIT_G:
                weight = UnitUtils.gToKG(Float.parseFloat(data));
                break;
            case UserUnitConfig.USER_UNIT_LB_LB:
                oz = UnitUtils.lbToOz(Float.parseFloat(data));
                g = UnitUtils.ozToG(oz);
                weight = UnitUtils.gToKG(g);
                break;
            case UserUnitConfig.USER_UNIT_ST:
                String[] weightStlb = null;
                if (data.contains(":")) weightStlb = data.split(":");
                if (weightStlb != null && weightStlb.length >= 2) {
                    oz = UnitUtils.lbToOz(UnitUtils.stToLb(Float.parseFloat(weightStlb[0])) + Float.parseFloat(weightStlb[1]));
                    g = UnitUtils.ozToG(oz);
                    weight = UnitUtils.gToKG(g);
                }

                break;
        }
        return getHoldDecimal(decimal, weight);
    }


    public static String getWeightToFg(int unit, String data, int decimal) {
        float weight = -1f;
        float g;
        float oz;
        if (TextUtils.isEmpty(data) || NO_DATA.equalsIgnoreCase(data)) {
            return NO_DATA;
        }
        if (unit == UserUnitConfig.USER_UNIT_FG) {//单位相同直接返回
            weight = Float.parseFloat(data);
        }

        switch (unit) {
            case UserUnitConfig.USER_UNIT_KG:
                weight = UnitUtils.gToFG(UnitUtils.kgToG(Float.parseFloat(data)));
//                        KgTo(Float.valueOf(data));
                break;

            case UserUnitConfig.USER_UNIT_OZ:
                g = UnitUtils.ozToG(Float.parseFloat(data));
                weight = UnitUtils.gToFG(g);
                break;
            case UserUnitConfig.USER_UNIT_LB:
                oz = getWeightLbToOz(data);
                g = UnitUtils.ozToG(oz);
                weight = UnitUtils.gToFG(g);
                break;
            case UserUnitConfig.USER_UNIT_G:
                weight = UnitUtils.gToFG(Float.parseFloat(data));
                break;
            case UserUnitConfig.USER_UNIT_LB_LB:
                oz = UnitUtils.lbToOz(Float.parseFloat(data));
                g = UnitUtils.ozToG(oz);
                weight = UnitUtils.gToFG(g);
                break;
            case UserUnitConfig.USER_UNIT_ST:
                String[] weightStlb = null;
                if (data.contains(":")) weightStlb = data.split(":");
                if (weightStlb != null && weightStlb.length >= 2) {
                    oz = UnitUtils.lbToOz(UnitUtils.stToLb(Float.parseFloat(weightStlb[0])) + Float.parseFloat(weightStlb[1]));
                    g = UnitUtils.ozToG(oz);
                    weight = UnitUtils.gToFG(g);
                }

                break;
        }
        return getHoldDecimal(decimal, weight);
    }

    /**
     * lb:oz转Oz
     */
    private static float getWeightLbToOz(String lbStr) {
        float lb = 0;
        float oz = 0;
        if (lbStr.contains(LB_SPLIT)) {
            String[] lbOzS = lbStr.split(LB_SPLIT);
            lb = Float.parseFloat(lbOzS[0]);
            oz = Float.parseFloat(lbOzS[1]);
        } else {
            lb = Float.parseFloat(lbStr);
        }

        float ozSize = UnitUtils.lbToOz(lb) + oz;
        return ozSize;
    }

    public static int getWeightToG(int unit, String data, int decimal) {

        int weightg = 0;
        try {
            weightg = (int) (Float.parseFloat(getWeightToKg(unit, data, decimal).trim()) * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return weightg;


    }


    public static String getWeightToUnit(int unit, int newUnit, String data, int decimal) {
        String weight = "";
        if (TextUtils.isEmpty(data) || NO_DATA.equalsIgnoreCase(data)) {
            return NO_DATA;
        }
        if (unit == newUnit) {
            weight = data;
        }
        switch (newUnit) {

            case UserUnitConfig.USER_UNIT_KG:
                weight = getWeightToKg(unit, data, decimal);
                break;
            case UserUnitConfig.USER_UNIT_LB_LB:
                weight = getWeightToLbLb(unit, data, decimal);
                break;
            case UserUnitConfig.USER_UNIT_ST:
                weight = getWeightToStLb(unit, data, decimal);
                break;
            case UserUnitConfig.USER_UNIT_FG:
                weight = getWeightToFg(unit, data, decimal);
                break;


        }
        return weight;

    }


    public static String getWeightKgToOther(int goalUnit, String data, int decimal) {
        return getWeightToUnit(UserUnitConfig.USER_UNIT_KG, goalUnit, data, decimal);
    }


    private static String getWeightToLbLb(int unit, String data, int decimal) {
        float weight = -1f;
        float g;
        float oz;
        if (TextUtils.isEmpty(data) || NO_DATA.equalsIgnoreCase(data)) {
            return NO_DATA;
        }
        if (unit == UserUnitConfig.USER_UNIT_LB_LB) {//单位相同直接返回
            weight = Float.parseFloat(data);
        }

        switch (unit) {
            case UserUnitConfig.USER_UNIT_FG:
                g = UnitUtils.fgToG(Float.parseFloat(data));
                oz = UnitUtils.gToOz(g);
                weight = UnitUtils.ozToLbLb(oz);
                break;
            case UserUnitConfig.USER_UNIT_KG:
                g = UnitUtils.kgToG(Float.parseFloat(data));
                oz = UnitUtils.gToOz(g);
                weight = UnitUtils.ozToLbLb(oz);
                break;
            case UserUnitConfig.USER_UNIT_OZ:
                weight = UnitUtils.ozToLbLb(Float.parseFloat(data));
                break;
            case UserUnitConfig.USER_UNIT_LB:
                oz = getWeightLbToOz(data);
                weight = UnitUtils.ozToLbLb(oz);
                break;
            case UserUnitConfig.USER_UNIT_G:
                oz = UnitUtils.gToOz(Float.parseFloat(data));
                weight = UnitUtils.ozToLbLb(oz);
                break;

        }
        return getHoldDecimal(decimal, weight);
    }

    private static String getWeightToStLb(int unit, String data, int decimal) {
        String stlb_str = "0:0.0";

        float weight = -1f;
        float g;
        float oz;
        if (TextUtils.isEmpty(data) || NO_DATA.equalsIgnoreCase(data)) {
            return NO_DATA;
        }
        if (unit == UserUnitConfig.USER_UNIT_ST) {//单位相同直接返回
            return data;
        }
        switch (unit) {
            case UserUnitConfig.USER_UNIT_FG:
                g = UnitUtils.fgToG(Float.parseFloat(data));
                oz = UnitUtils.gToOz(g);
                weight = UnitUtils.ozToLbLb(oz);
                break;
            case UserUnitConfig.USER_UNIT_KG:
                g = UnitUtils.kgToG(Float.parseFloat(data));
                oz = UnitUtils.gToOz(g);
                weight = UnitUtils.ozToLbLb(oz);
                break;
            case UserUnitConfig.USER_UNIT_OZ:
                weight = UnitUtils.ozToLbLb(Float.parseFloat(data));
                break;
            case UserUnitConfig.USER_UNIT_LB:
                oz = getWeightLbToOz(data);
                weight = UnitUtils.ozToLbLb(oz);
                break;
            case UserUnitConfig.USER_UNIT_G:
                oz = UnitUtils.gToOz(Float.parseFloat(data));
                weight = UnitUtils.ozToLbLb(oz);
                break;

        }


        float[] stlb = UnitUtils.LbToSt(weight);
        stlb_str = (int) stlb[0] + ":" + Math.round(stlb[1] * 10) / 10f;


        return stlb_str;
    }


    public static int getToMmhg(float kpa, int decimal, int unit) {
        float mmhg = 0;
        switch (unit) {
            case UserUnitConfig.USER_UNIT_KPA:
                float data = UnitUtils.kpaToMmhg(kpa);
                mmhg = Float.parseFloat(getHoldDecimal(decimal, data));
                break;
            case UserUnitConfig.USER_UNIT_MMHG:
                mmhg = kpa;
                break;
        }
        return (int) mmhg;
    }

    public static int getToMmhg(String kpa, int decimal, int unit) {
        return getToMmhg(Float.parseFloat(kpa), decimal, unit);
    }

    public static int getC(String temperature, int decimal, int unit) {
        float c = 0f;
        try {
            if (unit == UserUnitConfig.USER_UNIT_F) {
                c = Float.parseFloat(getHoldDecimal(decimal, UnitUtils.FToC(Float.parseFloat(temperature)))) * 100f;
            } else if (unit == UserUnitConfig.USER_UNIT_C) {
                c = Float.parseFloat(temperature) * 100f;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return (int) c;


    }




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
}

package aicare.net.cn.sdk.ailinksdkdemoandroid.utils;

/**
 * xing<br>
 * 2019/3/7<br>
 * 广播解析工具类
 */
public class MyBleStrUtils {


    /**
     * (16进制)
     * BLE蓝牙返回的byte[]
     * byte[]转字符串
     */
    public static String byte2HexStr(byte[] b) {
        if (b == null)
            return "";
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (byte aB : b) {
            int a = aB & 0XFF;
            stmp = getHexString(a);
            if (stmp.length() == 1)
                hs.append("0").append(stmp);
            else
                hs.append(stmp);
            hs.append(" ");
        }
        return hs.toString();
    }

    /**
     * int  10进制转16进制(返回大写字母)
     *
     * @return 16进制String
     */
    public static String getHexString(int number) {
        return Integer.toHexString(number);
    }


}

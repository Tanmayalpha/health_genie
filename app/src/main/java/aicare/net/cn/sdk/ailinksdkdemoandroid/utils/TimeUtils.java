package aicare.net.cn.sdk.ailinksdkdemoandroid.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * xing<br>
 * 2019/5/25<br>
 * java类作用描述
 */
public class TimeUtils {

    public static String getTime(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.US);
        return format.format(System.currentTimeMillis())+":\n";
    }
    public static String getTimeSSS(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS", Locale.US);
        return format.format(System.currentTimeMillis())+":\n";
    }

    public static String getDateString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        return simpleDateFormat.format(System.currentTimeMillis());
    }
    public static String getTime(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return format.format(time)+":\n";
    }
    /**
     * 返回当前年
     */
    public static int getYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    /**
     * 返回当前月(1~12)
     */
    public static int getMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 返回当前日
     */
    public static int getDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH);
    }


    /**
     * 返回当前日
     */
    public static int getHour() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.HOUR_OF_DAY);
    }


    /**
     * 返回当前日
     */
    public static int getMinute() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MINUTE);
    }

    public static int[] getTimeArr(){
        int[] time=new int[3];
        Calendar c = Calendar.getInstance();
        time[0] = c.get(Calendar.HOUR_OF_DAY);
        time[1] = c.get(Calendar.MINUTE);
        time[2]= c.get(Calendar.SECOND);
        return time;
    }



    public static String getTimeM(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.US);
        return format.format(System.currentTimeMillis())+":\n";
    }


}

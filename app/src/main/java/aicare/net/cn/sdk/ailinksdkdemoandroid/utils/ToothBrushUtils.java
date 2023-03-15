package aicare.net.cn.sdk.ailinksdkdemoandroid.utils;

import com.elinkthings.toothscore.ToothScoreUtil;

/**
 * @author xing<br>
 * @date 2022/11/10<br>
 * 牙刷工具类
 */
public class ToothBrushUtils {

    private ToothScoreUtil mToothScoreUtil;

    /**
     * 刷牙时长得分
     *
     * @param defaultTime 默认时长
     * @param totalTime   刷牙时长
     * @return int
     */
    public  int getDurationGrade(int defaultTime, int totalTime){
        if (mToothScoreUtil==null){
            mToothScoreUtil=new ToothScoreUtil();
        }
        return mToothScoreUtil.getDurationGrade(defaultTime,totalTime);
    }

    /**
     * 范围得分
     *
     * @param defaultTime 默认时长
     * @param lTime       左边刷牙时长
     * @param rTime       右边刷牙时长
     * @return int
     */
    public  int getRangeGrade(int defaultTime, int lTime, int rTime){
        if (mToothScoreUtil==null){
            mToothScoreUtil=new ToothScoreUtil();
        }
        return mToothScoreUtil.getRangeGrade(defaultTime,lTime,rTime);
    }

    /**
     * 均匀度得分
     *
     * @param defaultTime 默认时长
     * @param lTime       左边刷牙时长
     * @param rTime       右边刷牙时长
     * @return int
     */
    public  int getAvgGrade(int defaultTime, int lTime, int rTime){
        if (mToothScoreUtil==null){
            mToothScoreUtil=new ToothScoreUtil();
        }
        return mToothScoreUtil.getAvgGrade(defaultTime,lTime,rTime);
    }

    /**
     * 获取总分
     *
     * @param defaultTime 默认时长
     * @param totalTime   刷牙时长
     * @param lTime       左边刷牙时长
     * @param rTime       右边刷牙时长
     * @return int
     */
    public  int getGrade(int defaultTime, int totalTime, int lTime, int rTime){
        if (mToothScoreUtil==null){
            mToothScoreUtil=new ToothScoreUtil();
        }
        return mToothScoreUtil.getGrade(defaultTime,totalTime,lTime,rTime);
    }

}

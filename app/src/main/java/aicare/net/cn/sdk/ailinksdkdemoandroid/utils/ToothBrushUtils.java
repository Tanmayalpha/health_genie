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
     * @param defalutTime 默认时长
     * @param totaltime   刷牙时长
     * @return int
     */
    public  int getDurationGrade(int defalutTime, int totaltime){
        if (mToothScoreUtil==null){
            mToothScoreUtil=new ToothScoreUtil();
        }
        return mToothScoreUtil.getDurationGrade(defalutTime,totaltime);
    }

    /**
     * 范围得分
     *
     * @param defalutTime 默认时长
     * @param lTime       左边刷牙时长
     * @param rTime       右边刷牙时长
     * @return int
     */
    public  int getRangeGrade(int defalutTime, int lTime, int rTime){
        if (mToothScoreUtil==null){
            mToothScoreUtil=new ToothScoreUtil();
        }
        return mToothScoreUtil.getRangeGrade(defalutTime,lTime,rTime);
    }

    /**
     * 均匀度得分
     *
     * @param defalutTime 默认时长
     * @param lTime       左边刷牙时长
     * @param rTime       右边刷牙时长
     * @return int
     */
    public  int getAvgGrade(int defalutTime, int lTime, int rTime){
        if (mToothScoreUtil==null){
            mToothScoreUtil=new ToothScoreUtil();
        }
        return mToothScoreUtil.getAvgGrade(defalutTime,lTime,rTime);
    }

    /**
     * 获取总分
     *
     * @param defalutTime 默认时长
     * @param totalTime   刷牙时长
     * @param lTime       左边刷牙时长
     * @param rTime       右边刷牙时长
     * @return int
     */
    public  int getGrade(int defalutTime, int totalTime, int lTime, int rTime){
        if (mToothScoreUtil==null){
            mToothScoreUtil=new ToothScoreUtil();
        }
        return mToothScoreUtil.getGrade(defalutTime,totalTime,lTime,rTime);
    }

}

package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.airdetector;

public class SetType {

    public static final int TYPE_VALUE_SWITCH = 0;
    public static final int TYPE_MIN_MAX_SWITCH = 1;
    public static final int TYPE_SWITCH = 2;
    public static final int TYPE_CAL = 3;
    public static final int TYPE_VALUE = 4;
    public static final int TYPE_ALARM = 5;
    int type;
    int showType;

    public SetType(int type, int showType) {
        this.type = type;
        this.showType = showType;
    }
}

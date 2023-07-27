package aicare.net.cn.sdk.ailinksdkdemoandroid.dialog;

/**
 * 对话框字符串bean
 *
 * @author xing
 * @date 2023/07/21
 */
public class DialogStringImageBean {

    private String mName;
    private long mType;

    public DialogStringImageBean(String name, long type) {
        mName = name;
        mType = type;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getType() {
        return mType;
    }

    public void setType(long type) {
        mType = type;
    }

    @Override
    public String toString() {
        return "DialogStringImageBean{" + "mName='" + mName + '\'' + ", mType=" + mType + '}';
    }
}
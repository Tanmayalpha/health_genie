package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.airdetector_test;

public class AirDetectorTestBean {
    private int type;// 显示类型；0：正常；1：自动测试；2：手动测试
    private int step;// 第几步
    private String title;
    private int result;// 0 不显示；1 成功；2 失败
    private String resultStr;// 如果不为空，说明提示其它内容
    private String contentStr; // 返回内容

    public AirDetectorTestBean(int step, String title, int result) {
        this.step = step;
        this.title = title;
        this.result = result;
    }

    public AirDetectorTestBean(int step, String title, int result, String contentStr) {
        this.step = step;
        this.title = title;
        this.result = result;
        this.contentStr = contentStr;
    }

    public AirDetectorTestBean(int type) {
        this.type = type;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getResultStr() {
        return resultStr;
    }

    public void setResultStr(String resultStr) {
        this.resultStr = resultStr;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContentStr() {
        return contentStr;
    }

    public void setContentStr(String contentStr) {
        this.contentStr = contentStr;
    }
}

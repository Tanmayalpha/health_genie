package aicare.net.cn.sdk.ailinksdkdemoandroid.config;

/**
 * xing<br>
 * 2019/3/5<br>
 * 设备相关信息配置
 */
public class BleDeviceConfig {


//--------------------设备类型-----------------------


    /**
     * 血压计(sphygmomanometer)
     *
     */
    public final static int BLOOD_PRESSURE =0x01;
    /**
     * 额温枪(Forehead gun)
     */
    public final static int INFRARED_THERMOMETER =0x02;
    /**
     * 体温计(thermometer)
     */
    public final static int THERMOMETER =0x03;
    /**
     * 婴儿秤(Baby scale)
     */
    public final static int BABY_SCALE =0x04;
    /**
     * 身高仪(Height gauge)
     */
    public final static int HEIGHT_METER =0x05;

    /**
     * 智能门锁(Smart door lock)
     */
    public final static int SMART_LOCK =0x0B;

    /**
     * 定制版遥控器(Customized remote control)
     */
    public final static int EL_REMOTE_CONTROL =0x0C;

    /**
     * 连接类型的TPMS(Connection type TPMS)
     */
    public final static int TPMS_CONN_DEVICE =0x0D;

    /**
     * 体重体脂称(Body fat scale)
     */
    public final static int WEIGHT_BODY_FAT_SCALE =0x0E;

    /**
     * 箱包锁(Luggage lock)
     */
    public final static int LUGGAGE_LOCK =0x0F;
    /**
     * 锁遥控器(Lock the remote control)
     */
    public final static int LOCK_REMOTE_CONTROL =0x10;

    /**
     * wifi+ble体脂秤(wifi + ble body fat scale)
     */
    public final static int WEIGHT_BODY_FAT_SCALE_WIFI_BLE=0x11;
    /**
     * wifi+ble牙刷(wifi + ble body fat scale)
     */
    public final static int TOOTHBRUSH_WIFI_BLE=0x12;

    /**
     * 蓝牙牙刷
     */
    public final static int TOOTHBRUSH_BLE = 0x2D;

    /**
     * 八电极体脂秤
     */
    public final static int EIGHT_BODY_FAT_SCALE =0x13;

    /**
     * 风速计
     * cid 为0x14
     */
    public final static int ANEMOMETER=0x14;

    /**
     * 钳表
     * cid 为0x15
     */
    public final static int CLAMP_TABLE=0x15;

    /**
     * 体重体脂称(Body fat scale)艾迪
     */
    public final static int WEIGHT_BODY_FAT_SCALE_AD =0x100E;

    /**
     * 婴儿体脂两用秤
     */
    public final static int BABY_BODY_FAT =0x1A;

    /**
     * 血糖仪
     */
    public final static int BLOOD_GLUCOSE=0x1c;


    /**
     * 运动手表 华盛达手表
     */
    public final static int SPORTS_WATCH_BLE = 0x1D;

    /**
     * 蓝牙营养秤
     */
    public final static int BLE_NUTRITION_SCALE = 0x0034;


    /**
     * 广播秤
     */
    public final static int BROADCAST_SCALE =0x10001;


    /**
     * 智能口罩
     */
    public final static int SMART_MASK =0x0022;


    /**
     * 百丽达
     */
    public final static int BLD_WEIGHT =10086;

    /**
     * 蓝牙血氧仪
     */
    public final static int BLE_BOOLD_OXYGEN =0x21;

    /**
     * 咖啡秤
     */
    public final static int COFFEE_SCALE = 0x24;
    /**
     * 电滑板车
     */
    public final static int SMART_SCOOTER =0x0025;
    public final static int SMART_SCOOTER_CM02 =0x0033;

    /**
     * 共享充电器
     */
    public final static int SHARE_CHARGER = 0x1003;

    /**
     * 共享充电器
     */
    public final static int SHARE_SOCKET = 0x1005;

    /**
     * 共享套套机
     */
    public final static int SHARE_CONDOM = 0x1007;


    /**
     * 跳绳
     */
    public final static int ROPE_SKIPPING = 0x002f;


    /**
     * 身高体脂秤
     */
    public final static int HEIGHT_BODY_FAT = 0x0026;


    /**
     * 寻物器,暂定,待修改
     */
    public final static int FIND_DEVICE = -5;

    /**
     * 食品温度计
     */
    public final static int FOOD_TEMP = 0x002b;
    public final static int TEMP_Humidity = 0x002e;


    /**
     * 验证不握手不加密的id
     */
    public final static int CLEAR_SHAKE_HANDS = -6;

    /**
     * 广播营养秤
     */
    public final static int BROADCAST_NUTRITION = 0x10003;


}

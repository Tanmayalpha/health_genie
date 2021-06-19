package aicare.net.cn.sdk.ailinksdkdemoandroid.find;

import androidx.annotation.Nullable;

/**
 * xing<br>
 * 2021/4/12<br>
 * java类作用描述
 */
public class FindDeviceBean {

    private String mMac;

    private String mName;

    private int mRssi;

    private byte[] mBroadcastData;


    public FindDeviceBean(String mac, String name, int rssi, byte[] broadcastData) {
        mMac = mac;
        mName = name;
        mRssi = rssi;
        mBroadcastData = broadcastData;
    }

    public FindDeviceBean(String mac, String name, int rssi) {
        mMac = mac;
        mName = name;
        mRssi = rssi;
    }

    public FindDeviceBean(String mac, int rssi) {
        mMac = mac;
        mRssi = rssi;
    }

    public FindDeviceBean(String mac, int rssi, byte[] broadcastData) {
        mMac = mac;
        mRssi = rssi;
        mBroadcastData = broadcastData;
    }

    public String getMac() {
        return mMac;
    }

    public void setMac(String mac) {
        mMac = mac;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getRssi() {
        return mRssi;
    }

    public void setRssi(int rssi) {
        mRssi = rssi;
    }

    public byte[] getBroadcastData() {
        return mBroadcastData;
    }

    public void setBroadcastData(byte[] broadcastData) {
        mBroadcastData = broadcastData;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof String) {
            return this.mMac.equalsIgnoreCase((String) obj);
        } else if (obj instanceof FindDeviceBean) {
            return this.mMac.equalsIgnoreCase(((FindDeviceBean) obj).getMac());
        } else {
            return super.equals(obj);
        }
    }
}

package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.net.aicare.modulelibrary.module.wifi.WifiConfig;
import cn.net.aicare.modulelibrary.module.wifi.WifiUtils;

/**
 * wifi配置界面
 */
public class WifiConfigActivity extends AppCompatActivity implements View.OnClickListener, WifiUtils.OnWifiEventListener {
    private String TAG = WifiConfigActivity.class.getName();
    private EditText ssid, paw, key, sn, devicename;
    private ListView mListView;
    private ArrayAdapter logadapter;
    private List<String> loglist;
    private Button btn_connect;
    private Button btn_weight_result;
    private WifiUtils mWifiUtils;
    private Button btn_getdeviceid;
    private Button btn_login;
    private Button get_record;
    private String SN;
    private String myDeviceId;
    private String token;
    private long appid;
    private List<Long> bodyId;
    private RadioButton kg, jing, stlb, lb;
    private String mDeviceName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁止横屏
        setContentView(R.layout.activity_wifi_config);
        ssid = findViewById(R.id.ssid);
        paw = findViewById(R.id.password);
        key = findViewById(R.id.key);
        btn_getdeviceid = findViewById(R.id.deviceid);
        btn_weight_result = findViewById(R.id.weight_result);
        get_record = findViewById(R.id.get_record);
        mListView = findViewById(R.id.log_list);
        btn_connect = findViewById(R.id.btn_connect);
        btn_login = findViewById(R.id.btn_login);
        sn = findViewById(R.id.sn);
        devicename = findViewById(R.id.device_name);
        btn_login.setOnClickListener(this);
        btn_weight_result.setOnClickListener(this);
        btn_connect.setOnClickListener(this);
        btn_getdeviceid.setOnClickListener(this);
        get_record.setOnClickListener(this);
        findViewById(R.id.clear_log).setOnClickListener(this);
        findViewById(R.id.clear_record).setOnClickListener(this);
        loglist = new ArrayList<>();
        logadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, loglist);
        mListView.setAdapter(logadapter);
        mWifiUtils = new WifiUtils(this, this);
        mWifiUtils.setLoopSendEnable(false);
        mWifiUtils.setInterval(5);
        ssid.setText(mWifiUtils.getSsidstring() == null ? "Elink-wif" : mWifiUtils.getSsidstring().replace("\"", ""));
        paw.setText("elink1234567890");
        key.setText("1234567812345678");
        sn.setText("574d0868174f882078a5784b1f8d48");
        devicename.setText("wifi秤");
        bodyId = new ArrayList<>();
        kg = findViewById(R.id.kg);
        jing = findViewById(R.id.jin);
        stlb = findViewById(R.id.st_lb);
        lb = findViewById(R.id.lb);
        kg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                setUnit(WifiConfig.KG);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    }).start();

                }
            }
        });
        jing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                setUnit(WifiConfig.JIN);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
        stlb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                setUnit(WifiConfig.ST);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
        lb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                setUnit(WifiConfig.LB);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_connect) {
            if (mWifiUtils.getRunning() == 0) {
                btn_connect.setText("停止");

                mWifiUtils.startSendData(ssid.getText().toString().trim(), paw.getText().toString().trim(), key.getText().toString().trim());
            } else {
                mWifiUtils.end();
                btn_connect.setText("开始配网");
            }
        } else if (v.getId() == R.id.deviceid) {
            SN = sn.getText().toString().trim();
            mDeviceName = devicename.getText().toString().trim();
            if (appid != 0 && token != null && !SN.isEmpty() && !mDeviceName.isEmpty()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getDeviceid();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                }).start();

            } else {
                if (appid == 0 || token == null) {
                    loglist.add(0, "未登录");
                }
                if (SN.isEmpty()) {
                    loglist.add(0, "没有SN号");
                }
                if (mDeviceName.isEmpty()) {
                    loglist.add(0, "没有设备名称");
                }
            }
            logadapter.notifyDataSetChanged();

        } else if (v.getId() == R.id.weight_result) {
            if (appid != 0 && token != null && myDeviceId != null) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getWeight(appid, token, myDeviceId);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                }).start();

            } else {
                if (appid == 0 || token == null) {
                    loglist.add(0, "未登录");
                }
                if (myDeviceId == null) {
                    loglist.add(0, "没有得到设备Id");
                }
            }
            logadapter.notifyDataSetChanged();
        } else if (v.getId() == R.id.btn_login) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        login();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }).start();
        } else if (v.getId() == R.id.clear_log) {
            loglist.clear();
            logadapter.notifyDataSetChanged();
        } else if (v.getId() == R.id.clear_record) {
            if (bodyId.size() != 0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < bodyId.size(); i++) {
                            try {
                                delRecord(bodyId.get(i));
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    }
                }).start();
            } else {
                loglist.add(0, "没有BodyFatId");
                logadapter.notifyDataSetChanged();
            }
        } else if (v.getId() == R.id.get_record) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        getTestRecord();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public void onEvent(int code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (code) {
                    case WifiConfig.KEY_ILLEGAL:
                        loglist.add(0, "加密key非法");
                        break;
                    case WifiConfig.ERROR_EXCEPTIONAL:
                        loglist.add(0, "异常");
                        break;
                    case WifiConfig.INFORMATION_SUCCESSFUL:
                        loglist.add(0, "生成数据类info 成功");
                        break;
                    case WifiConfig.DEVICE_CONNECTION_SUCCESSFUL:
                        mWifiUtils.end();
                        loglist.add(0, "设备联网成功成功");
                        btn_connect.setText("开始配网");


                        //调取登录 通过SN号去获取deviceId
                        break;
                    case WifiConfig.START_SEND_DATA:
                        loglist.add(0, "开始发送加密包");
                        break;
                    case WifiConfig.ERROR_START_SEND_DATA:
                        loglist.add(0, "开始发送加密包,出错");
                        break;
                    case WifiConfig.START_RECEIVE_DATA:
                        loglist.add(0, "开始接收设备数据");
                        break;
                    case WifiConfig.ERROR_START_RECEIVE_DATA:
                        loglist.add(0, "开始接收设备数据,出错");
                        break;
                    case WifiConfig.SSID_KEY_NULL:
                        loglist.add(0, "ssid为空或key为空");
                        break;

                }

                logadapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWifiUtils.onDestory();
    }


    public void login() throws Throwable {

        URL uri = new URL("http://test.ailink.app.aicare.net.cn/api/user/login?key=inet_elink&username=dhls@qq.com&password=dc483e80a7a0bd9ef71d8cf973673924");
        HttpURLConnection httpURLConnection = (HttpURLConnection) uri.openConnection();
        // Post请求必须设置允许输出
        httpURLConnection.setDoOutput(true);
        // Post请求不能使用缓存
        httpURLConnection.setUseCaches(false);
        // 设置为Post请求
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setInstanceFollowRedirects(true);
        // 配置请求Content-Type
        httpURLConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencode");
        // 设置连接超时时间
        httpURLConnection.setConnectTimeout(6 * 1000);
        // 开始连接
        httpURLConnection.connect();
//        DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
//        dos.write(map.toString().getBytes());
//        dos.flush();
//        dos.close();
        // 判断请求是否成功
        if (httpURLConnection.getResponseCode() == 200) {
            // 获取返回的数据
            byte[] data = readInputStream(httpURLConnection.getInputStream());
            String result = new String(data, "UTF-8");
            Log.i(TAG, result);
            JSONObject jsonObject = new JSONObject(result);
            JSONObject datajson = jsonObject.getJSONObject("data");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        token = jsonObject.getString("token");
                        appid = datajson.getLong("appUserId");
                        loglist.add(0, "登录成功");
                        logadapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } else {
            Log.i(TAG, "Post方式请求失败");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loglist.add(0, "登录失败");
                }
            });
        }

    }


    public void getWeight(long appid, String token, String deviceId) throws Throwable {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://test.ailink.app.aicare.net.cn/api/BFScale/list?key=inet_elink&appUserId=");
        stringBuilder.append(appid);
        stringBuilder.append("&token=");
        stringBuilder.append(token);
        stringBuilder.append("&deviceId=");
        stringBuilder.append(deviceId);
        stringBuilder.append("&subUserId=1&maxId=0");
        URL uri = new URL(stringBuilder.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) uri.openConnection();
//        // Post请求必须设置允许输出
//        httpURLConnection.setDoOutput(true);
//        // Post请求不能使用缓存
//        httpURLConnection.setUseCaches(false);
        // 设置为Post请求
//        httpURLConnection.setRequestMethod("GET");
//        httpURLConnection.setInstanceFollowRedirects(true);
//        // 配置请求Content-Type
//        httpURLConnection.setRequestProperty("Content-Type",
//                "application/x-www-form-urlencode");
        // 设置连接超时时间
        httpURLConnection.setConnectTimeout(6 * 1000);
        // 开始连接
        httpURLConnection.connect();
//        DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
//        dos.write(map.toString().getBytes());
//        dos.flush();
//        dos.close();
        // 判断请求是否成功
        if (httpURLConnection.getResponseCode() == 200) {
            // 获取返回的数据
            byte[] data = readInputStream(httpURLConnection.getInputStream());
            String result = new String(data, "UTF-8");
            JSONObject jsonObject = new JSONObject(result);
            JSONArray datajson = jsonObject.getJSONArray("data");
            bodyId.clear();
            for (int i = 0; i < datajson.length(); i++) {
                bodyId.add(datajson.getJSONObject(i).getLong("bodyFatId"));
            }
            Log.i(TAG, result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loglist.add(0, result);
                    logadapter.notifyDataSetChanged();
                    loglist.add(0, "总共有：" + bodyId.size());
                    logadapter.notifyDataSetChanged();
                }
            });

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loglist.add(0, "请求失败");
                    logadapter.notifyDataSetChanged();
                }
            });
        }
    }


    public void getDeviceid() throws Throwable {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://test.ailink.app.aicare.net.cn/api/device/getDeviceBySN?key=inet_elink&appUserId=");
        stringBuilder.append(appid);
        stringBuilder.append("&token=");
        stringBuilder.append(token);
        stringBuilder.append("&deviceSN=");
        stringBuilder.append(SN);
        Log.e(TAG, stringBuilder.toString());
        URL uri = new URL(stringBuilder.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) uri.openConnection();
        // Post请求必须设置允许输出
        httpURLConnection.setDoOutput(true);
        // Post请求不能使用缓存
        httpURLConnection.setUseCaches(false);
        // 设置为Post请求
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setInstanceFollowRedirects(true);
        // 配置请求Content-Type
        httpURLConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencode");
        // 设置连接超时时间
        httpURLConnection.setConnectTimeout(6 * 1000);
        // 开始连接
        httpURLConnection.connect();
//        DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
//        dos.write(map.toString().getBytes());
//        dos.flush();
//        dos.close();
        // 判断请求是否成功
        if (httpURLConnection.getResponseCode() == 200) {
            // 获取返回的数据
            byte[] data = readInputStream(httpURLConnection.getInputStream());
            String result = new String(data, "UTF-8");
            Log.i(TAG, result);
            JSONObject jsonObject = new JSONObject(result);
            JSONArray datajson = jsonObject.getJSONArray("data");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (datajson.length() > 0)
                            myDeviceId = datajson.getJSONObject(0).getString("deviceId");
                        loglist.add(0, "设备信息" + result);
                        logadapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            bindDevice();


        } else {
            Log.i(TAG, "Post方式请求失败");
        }
    }

    public void bindDevice() throws Throwable {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://test.ailink.app.aicare.net.cn/api/device/update?key=inet_elink&appUserId=");
        stringBuilder.append(appid);
        stringBuilder.append("&token=");
        stringBuilder.append(token);
        stringBuilder.append("&deviceId=");
        stringBuilder.append(myDeviceId);
        stringBuilder.append("&roomId=4326&deviceName=");
        stringBuilder.append(mDeviceName);
        URL uri = new URL(stringBuilder.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) uri.openConnection();
        // Post请求必须设置允许输出
        httpURLConnection.setDoOutput(true);
        // Post请求不能使用缓存
        httpURLConnection.setUseCaches(false);
        // 设置为Post请求
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setInstanceFollowRedirects(true);
        // 配置请求Content-Type
        httpURLConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencode");
        // 设置连接超时时间
        httpURLConnection.setConnectTimeout(6 * 1000);
        // 开始连接
        httpURLConnection.connect();
//        DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
//        dos.write(map.toString().getBytes());
//        dos.flush();
//        dos.close();
        // 判断请求是否成功
        if (httpURLConnection.getResponseCode() == 200) {
            // 获取返回的数据
            byte[] data = readInputStream(httpURLConnection.getInputStream());
            String result = new String(data, "UTF-8");
            Log.i(TAG, result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loglist.add(0, "绑定设备" + result);
                    logadapter.notifyDataSetChanged();
                }
            });


        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loglist.add(0, "绑定设备失败");
                    logadapter.notifyDataSetChanged();
                }
            });
        }

    }


    public void delRecord(Long bodyid) throws Throwable {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://test.ailink.app.aicare.net.cn/api/BFScale/delete?key=inet_elink&appUserId=");
        stringBuilder.append(appid);
        stringBuilder.append("&token=");
        stringBuilder.append(token);
        stringBuilder.append("&bodyFatId=");
        stringBuilder.append(bodyid);
        stringBuilder.append("");
        URL uri = new URL(stringBuilder.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) uri.openConnection();
        // Post请求必须设置允许输出
        httpURLConnection.setDoOutput(true);
        // Post请求不能使用缓存
        httpURLConnection.setUseCaches(false);
        // 设置为Post请求
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setInstanceFollowRedirects(true);
        // 配置请求Content-Type
        httpURLConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencode");
        // 设置连接超时时间
        httpURLConnection.setConnectTimeout(6 * 1000);
        // 开始连接
        httpURLConnection.connect();
//        DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
//        dos.write(map.toString().getBytes());
//        dos.flush();
//        dos.close();
        // 判断请求是否成功
        if (httpURLConnection.getResponseCode() == 200) {
            // 获取返回的数据
            byte[] data = readInputStream(httpURLConnection.getInputStream());
            String result = new String(data, "UTF-8");
            Log.i(TAG, result);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    loglist.add(0, result);

                    logadapter.notifyDataSetChanged();
                }
            });


        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    loglist.add(0, "请求失败");

                    logadapter.notifyDataSetChanged();
                }
            });
        }

    }


    public byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public void setUnit(int unit) throws Throwable {
        if (appid == 0 || token == null || SN == null || SN.isEmpty() || mDeviceName == null || mDeviceName.isEmpty()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    loglist.add(0, "请先登录，然后获取设备id");

                    logadapter.notifyDataSetChanged();
                }
            });
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
//                               http://test.ailink.app.aicare.net.cn/api/device/update?key=inet_elink&appUserId=10429&token=57dce920-8521-42e9-9040-157d08c7d922&deviceId=3001&roomId=4326&deviceName=WIFI秤测试&deviceUnit=1
        stringBuilder.append("http://test.ailink.app.aicare.net.cn/api/device/update?key=inet_elink&appUserId=");
        stringBuilder.append(appid);
        stringBuilder.append("&token=");
        stringBuilder.append(token);
        stringBuilder.append("&deviceId=");
        stringBuilder.append(myDeviceId);
        stringBuilder.append("&roomId=4326&deviceName=");
        stringBuilder.append(mDeviceName);
        stringBuilder.append("&deviceUnit=");
        stringBuilder.append(unit);
        Log.e("设置单位", stringBuilder.toString());
        URL uri = new URL(stringBuilder.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) uri.openConnection();
        // Post请求必须设置允许输出
        httpURLConnection.setDoOutput(true);
        // Post请求不能使用缓存
        httpURLConnection.setUseCaches(false);
        // 设置为Post请求
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setInstanceFollowRedirects(true);
        // 配置请求Content-Type
        httpURLConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencode");
        // 设置连接超时时间
        httpURLConnection.setConnectTimeout(6 * 1000);
        // 开始连接
        httpURLConnection.connect();
//        DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
//        dos.write(map.toString().getBytes());
//        dos.flush();
//        dos.close();
        // 判断请求是否成功
        if (httpURLConnection.getResponseCode() == 200) {
            // 获取返回的数据
            byte[] data = readInputStream(httpURLConnection.getInputStream());
            String result = new String(data, "UTF-8");
            Log.i(TAG, result);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    loglist.add(0, result);

                    logadapter.notifyDataSetChanged();
                }
            });


        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    loglist.add(0, "请求失败");

                    logadapter.notifyDataSetChanged();
                }
            });

        }

    }

    public void getTestRecord() throws Throwable {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://test.ailink.app.aicare.net.cn/api/deDataTest/getDeDataTestPage?key=inet_elink&appUserId=");
        stringBuilder.append(appid);
        stringBuilder.append("&token=");
        stringBuilder.append(token);
        stringBuilder.append("&maxId=0");
        stringBuilder.append("&sortColumns=");
        stringBuilder.append("id desc");

        Log.e(TAG, stringBuilder.toString());
        URL uri = new URL(stringBuilder.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) uri.openConnection();
//        // Post请求必须设置允许输出
//        httpURLConnection.setDoOutput(true);
//        // Post请求不能使用缓存
//        httpURLConnection.setUseCaches(false);
//        // 设置为Post请求
//        httpURLConnection.setRequestMethod("POST");
//        httpURLConnection.setInstanceFollowRedirects(true);
//        // 配置请求Content-Type
//        httpURLConnection.setRequestProperty("Content-Type",
//                "application/x-www-form-urlencode");
        // 设置连接超时时间
        httpURLConnection.setConnectTimeout(6 * 1000);
        // 开始连接
        httpURLConnection.connect();
//        DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
//        dos.write(map.toString().getBytes());
//        dos.flush();
//        dos.close();
        // 判断请求是否成功
        if (httpURLConnection.getResponseCode() == 200) {
            // 获取返回的数据
            byte[] data = readInputStream(httpURLConnection.getInputStream());
            String result = new String(data, "UTF-8");
            Log.i(TAG, result);
            JSONObject jsonObject = new JSONObject(result);
            JSONObject datajson = jsonObject.getJSONObject("data");
            JSONArray listjson = datajson.getJSONArray("list");
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < listjson.length(); i++) {
                long id = listjson.getJSONObject(i).getLong("id");
//                 String createTime="数据时间 "+getDateDefault(listjson.getJSONObject(i).getLong("createTime"));
                String encryptData = "请求的加密值: " + listjson.getJSONObject(i).getString("encryptData");
//                String decryptData = "解密结果： " + listjson.getJSONObject(i).getString("decryptData");
                stringBuffer.append("id: ");
                stringBuffer.append(id);
                stringBuffer.append("\n");
                stringBuffer.append(encryptData);

                stringBuffer.append("\n=================\n");
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    loglist.add(0, "测试数据\n" + stringBuffer.toString());
                    logadapter.notifyDataSetChanged();


                }
            });


        } else {

        }
    }

    public static String getDateDefault(Long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return simpleDateFormat.format(time);
    }

    public void getPermission() {
        String[] permissions = {android.Manifest.permission.ACCESS_COARSE_LOCATION,};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //当前系统大于等于6.0
            for (String perssion : permissions) {
                int isGranted = ContextCompat.checkSelfPermission(this, perssion);
                if (isGranted != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{perssion}, 1);
                }
            }
            LocationManager lm = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                /*Toast.makeText(this, "检测到未开启GPS定位服务,需要开启才能扫描到附件WiFi信息，请开启", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3 * 1000);  //sleep s
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();*/
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("警告！")
                        .setMessage("检测到未开启GPS定位服务,需要开启才能扫描到附件WiFi信息，请开启")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, 1);
                            }
                        }).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Mainactivity", "onRequestPermissionsResult granted");
                } else {
                    Log.d("Mainactivity", "onRequestPermissionsResult denied");
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("警告！")
                            .setMessage("需要授予定位权限才能扫描到附件WiFi信息")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //一般情况下如果用户不授权的话，功能是无法运行的，做退出处理
                                    finish();
                                }
                            }).show();
                }
                break;
            }
            default:
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.TimeUtils;

/**
 * 4G血糖仪
 */
public class BloodSugar4GActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = BloodSugar4GActivity.class.getName();
    private String token;
    private long appid;
    private String imei;
    private List<String> loglist;
    private String myDeviceId;
    private ArrayAdapter logAdapter;
    private ListView listviw;
    private EditText mEditText;
    private Button btn_deviceId;
    private Button btn_data;
    private Button btn_clear;
    private Button btn_login;
    private List<Long> bodyIds;
    private long maxId = 0l;
    private Button btn_test;
    private Button btn_produce;
    private String url = "https://ailink.aicare.net.cn";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodsugar_4g);

        listviw = findViewById(R.id.listviw);
        mEditText = findViewById(R.id.ed_imei);
        btn_deviceId = findViewById(R.id.btn_deviceId);
        btn_data = findViewById(R.id.btn_data);
        btn_clear = findViewById(R.id.btn_clear);
        btn_login = findViewById(R.id.btn_login);
        btn_test = findViewById(R.id.btn_test);
        btn_produce = findViewById(R.id.btn_produce);
        btn_deviceId.setOnClickListener(this);
        btn_data.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_test.setOnClickListener(this);
        btn_produce.setOnClickListener(this);
        loglist = new ArrayList<>();
        bodyIds = new ArrayList<>();
        logAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, loglist);
        listviw.setAdapter(logAdapter);
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

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                myDeviceId = "";
                maxId = 0;

            }
        });
    }

    public void login() throws Throwable {
        String mUrl=url + "/api/user/login?key=inet_elink&username=dhls@qq.com&password=dc483e80a7a0bd9ef71d8cf973673924";
        URL uri = new URL(mUrl);
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
                        logAdapter.notifyDataSetChanged();
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


    public void getDeviceid() throws Throwable {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url);
        stringBuilder.append("/api/device/getDeviceBySN?key=inet_elink&appUserId=");
        stringBuilder.append(appid);
        stringBuilder.append("&token=");
        stringBuilder.append(token);
        stringBuilder.append("&deviceSN=");
        stringBuilder.append(imei);
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
                        logAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        } else {
            Log.i(TAG, "Post方式请求失败");
        }
    }


    public void getData() throws Throwable {
        if (myDeviceId.isEmpty()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loglist.add(0, "请获取设备Id");
                    logAdapter.notifyDataSetChanged();
                }
            });

            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url);
        stringBuilder.append("/api/deDataGlu/listByDeviceId?key=inet_elink&appUserId=");
        stringBuilder.append(appid);
        stringBuilder.append("&token=");
        stringBuilder.append(token);
        stringBuilder.append("&deviceId=");
        stringBuilder.append(myDeviceId);
        stringBuilder.append("&maxId=");
        stringBuilder.append(maxId);
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
            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONArray datajson = dataObject.getJSONArray("list");
            bodyIds.clear();
            for (int i = 0; i < datajson.length(); i++) {
                long id = datajson.getJSONObject(i).getLong("id");
                bodyIds.add(id);
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("id:");
                stringBuffer.append(id);
                stringBuffer.append("   创建时间:");
                stringBuffer.append(TimeUtils.getTime(datajson.getJSONObject(i).getLong("createTime")));
                stringBuffer.append("血糖值(原始值):");
                int value = datajson.getJSONObject(i).getInt("gluFasting");
                stringBuffer.append(value);
                stringBuffer.append(" 小数位:");
                int point = datajson.getJSONObject(i).getInt("gluPoint");
                stringBuffer.append(point);
                stringBuffer.append(" 单位:");
                int unit = datajson.getJSONObject(i).getInt("gluUnit");
                stringBuffer.append(unit);
                stringBuffer.append("\n血糖(计算后)");
                stringBuffer.append(String.format(Locale.US, "%." + point + "f", (value * 1f) * Math.pow(10, 0 - point)));
                stringBuffer.append(unit == 1 ? "mmol/L" : "mg/dL");


                loglist.add(0, stringBuffer.toString());
            }
            if (maxId == 0) {
                loglist.add(0, "总共有：" + bodyIds.size() + " 条");
            } else {
                loglist.add(0, "新增了：" + bodyIds.size() + " 条");

            }
            if (bodyIds.size() > 0) {
                maxId = bodyIds.get(bodyIds.size() - 1);
            }
            Log.i(TAG, result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.notifyDataSetChanged();
                }

            });

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loglist.add(0, "请求失败");
                    logAdapter.notifyDataSetChanged();
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_deviceId) {
            imei = mEditText.getText().toString().trim();
            if (imei == null || imei.isEmpty()) {
                loglist.add("请输入IMEI号");
            } else {

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

            }
        } else if (v.getId() == R.id.btn_data) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        getData();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }).start();
        } else if (v.getId() == R.id.btn_clear) {
            loglist.clear();
            logAdapter.notifyDataSetChanged();
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
        } else if (v.getId() == R.id.btn_produce) {
            url = "https://ailink.aicare.net.cn";

        } else if (v.getId() == R.id.btn_test) {
            url = "http://test.ailink.app.aicare.net.cn";

        }
    }
}

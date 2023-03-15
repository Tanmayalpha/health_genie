package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.body_scale_4g;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.net.aicare.algorithmutil.AlgorithmUtil;
import cn.net.aicare.algorithmutil.BodyFatData;

/**
 * 4G体脂秤
 */
public class BodyScale4GActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = BodyScale4GActivity.class.getName();
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
    private Button btn_bind;
    private EditText et_cid;
    private EditText et_vid;
    private EditText et_pid;
    private String url = "https://ailink.aicare.net.cn";

    private SeekBar seek_height;
    private SeekBar seek_age;
    private TextView tv_height;
    private TextView tv_age;
    private RadioButton rb_female;
    private RadioButton rb_male;

    private int mHeight;
    private int mAge;
    private int mGender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_scale_4g);

        listviw = findViewById(R.id.listviw);
        mEditText = findViewById(R.id.ed_imei);
        btn_deviceId = findViewById(R.id.btn_deviceId);
        btn_data = findViewById(R.id.btn_data);
        btn_clear = findViewById(R.id.btn_clear);
        btn_login = findViewById(R.id.btn_login);
        btn_test = findViewById(R.id.btn_test);
        btn_produce = findViewById(R.id.btn_produce);
        btn_bind = findViewById(R.id.btn_bind);
        btn_deviceId.setOnClickListener(this);
        btn_data.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_test.setOnClickListener(this);
        btn_produce.setOnClickListener(this);
        btn_bind.setOnClickListener(this);
        seek_height = findViewById(R.id.seek_height);
        seek_age = findViewById(R.id.seek_age);
        tv_height = findViewById(R.id.tv_height);
        tv_age = findViewById(R.id.tv_age);
        rb_female = findViewById(R.id.rb_female);
        rb_male = findViewById(R.id.rb_male);
        et_cid = findViewById(R.id.et_cid);
        et_vid = findViewById(R.id.et_vid);
        et_pid = findViewById(R.id.et_pid);

        // 读取默认用户信息
        mHeight = seek_height.getProgress();
        mAge = seek_age.getProgress();
        mGender = rb_female.isChecked() ? 0 : 1;

        // seekBar 联动
        seek_height.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mHeight = progress;
                tv_height.setText(mHeight + "cm");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seek_age.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mAge = progress;
                tv_age.setText(mAge + "岁");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        rb_female.setOnClickListener(v -> {
            mGender = 0;
        });
        rb_male.setOnClickListener(v -> {
            mGender = 1;
        });

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
        String mUrl=url + "/api/user/login?key=inet_elink&username=10037&password=5abd06d6f6ef0e022e11b8a41f57ebda";
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


    public void getDeviceId() throws Throwable {
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
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencode");
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
                        if (datajson.length() > 0) {
                            myDeviceId = datajson.getJSONObject(0).getString("deviceId");
                        }
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

    public void unbindDevice() throws Throwable {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url);
        stringBuilder.append("/api/device/delete?key=inet_elink&appUserId=" + appid);
        stringBuilder.append("&token=" + token);
        stringBuilder.append("&deviceId=" + myDeviceId);
//        stringBuilder.append("&version=" + "1.0");
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
        // 判断请求是否成功
        if (httpURLConnection.getResponseCode() == 200) {
            // 获取返回的数据
            byte[] data = readInputStream(httpURLConnection.getInputStream());
            String result = new String(data, "UTF-8");
            Log.i(TAG, result);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        loglist.add(0, "解绑设备成功：" + result);
                        logAdapter.notifyDataSetChanged();

                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });

            // 解绑设备成功后重新绑定
            bindDevice();

        } else {
            Log.i(TAG, "Post方式请求失败");
        }
    }

    private boolean isFirstBind = true;

    public void bindDevice() throws Throwable {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url);
        stringBuilder.append("/api/device/add?key=inet_elink&appUserId=" + appid);
        stringBuilder.append("&token=" + token);
        stringBuilder.append("&deviceSN=" + imei);
        stringBuilder.append("&mac=" + imei);
        stringBuilder.append("&roomId=" + 86360);
        stringBuilder.append("&deviceName=" + "4G体脂秤");
        stringBuilder.append("&cid=" + et_cid.getText().toString());
        stringBuilder.append("&vid=" + et_vid.getText().toString());
        stringBuilder.append("&pid=" + et_pid.getText().toString());
        stringBuilder.append("&supportUnit=" + "");
//        stringBuilder.append("&version=" + "1.0");
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
//            JSONArray datajson = jsonObject.getJSONArray("data");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        if (datajson.length() > 0)
//                            myDeviceId = datajson.getJSONObject(0).getString("deviceId");
                        loglist.add(0, "获取到已绑定的设备ID：" + result);
                        logAdapter.notifyDataSetChanged();

                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });

            myDeviceId = jsonObject.getJSONObject("data").getString("deviceId");
            if (isFirstBind) {
                // 第一次获取到的可能是错误的，解绑，重新绑定
                isFirstBind = false;
                unbindDevice();
            } else {
                isFirstBind = true;
            }

        } else {
            Log.i(TAG, "Post方式请求失败");
        }
    }


    public void getData() throws Throwable {
        if (TextUtils.isEmpty(myDeviceId)) {
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
        stringBuilder.append("/api/BFScale/list?key=inet_elink&appUserId=");
        stringBuilder.append(appid);
        stringBuilder.append("&token=");
        stringBuilder.append(token);
        stringBuilder.append("&deviceId=");
        stringBuilder.append(myDeviceId);
        stringBuilder.append("&maxId=");
        stringBuilder.append(maxId);
        stringBuilder.append("&subUserId=0");
        Log.e(TAG, stringBuilder.toString());
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
//            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONArray datajson = jsonObject.getJSONArray("data");
            bodyIds.clear();
//            loglist.add(0, "jsonObject：" + jsonObject.toString());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            for (int i = 0; i < datajson.length(); i++) {
                JSONObject json = datajson.getJSONObject(i);
                long id = json.getLong("bodyFatId");
                bodyIds.add(id);

                int weightSource = json.getInt("weight");
                int weightDecimal = json.getInt("weightPoint");
                int weightUnit = json.getInt("weightUnit");
                int adc = json.getInt("adc");
                int algorithm = json.getInt("deviceAlgorithm");

                BigDecimal bigDecimal = BigDecimal.valueOf(weightSource / Math.pow(10, weightDecimal));
                String weightStr = bigDecimal.setScale(weightDecimal, BigDecimal.ROUND_HALF_UP).toString();

                BodyFatData bodyFatData = AlgorithmUtil.getBodyFatData(algorithm, (mGender == 0 ? 2 : 1), mAge, bigDecimal.floatValue(), mHeight, adc);

                switch (weightUnit) {
                    case 0:
                        weightStr += "kg";
                        break;
                    case 1:
                        weightStr += "斤";
                        break;
                    case 4:
                        weightStr += "st:lb";
                        break;
                    case 6:
                        weightStr += "lb";
                        break;
                    default:
                        weightStr += "未知单位";
                        break;
                }

                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("id：" + id + "\n");
                stringBuffer.append("创建时间：" + sdf.format(json.getLong("createTime")) + "\n");
                stringBuffer.append("体重原始值：" + weightSource + "\n");
                stringBuffer.append("体重小数点：" + weightDecimal + "\n");
                stringBuffer.append("体重单位：" + weightUnit + "\n");
                stringBuffer.append("最终体重：" + weightStr + "\n");
                stringBuffer.append("阻抗：" + adc + "\n");
                stringBuffer.append("心率：" + json.getInt("heartRate") + "\n");
                stringBuffer.append("算法id：" + algorithm + "\n");
                stringBuffer.append("体脂率：" + bodyFatData.getBfr() + "");
                loglist.add(0, stringBuffer.toString());
            }
            loglist.add(0, "总共有：" + bodyIds.size() + " 条");
//            if (maxId == 0) {
//                loglist.add(0, "总共有：" + bodyIds.size() + " 条");
//            } else {
//                loglist.add(0, "新增了：" + bodyIds.size() + " 条");
//            }
//            if (bodyIds.size() > 0) {
//                maxId = bodyIds.get(bodyIds.size() - 1);
//            }
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
                            getDeviceId();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                }).start();

            }
        } else if (v.getId() == R.id.btn_bind) {
            imei = mEditText.getText().toString().trim();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bindDevice();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }).start();
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

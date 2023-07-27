package aicare.net.cn.sdk.ailinksdkdemoandroid.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.lang.ref.WeakReference;
import java.util.Map;

import aicare.net.cn.sdk.ailinksdkdemoandroid.dialog.HintDataDialogFragment;

/**
 * @author xing<br>
 * @date 2022/11/24<br>
 * 权限工具,用于动态权限检测
 */
public class CheckPermissionUtils {

    private PermissionRequestFragment mPermissionRequestFragment;

    public CheckPermissionUtils(AppCompatActivity activity) {
        mPermissionRequestFragment = new PermissionRequestFragment();
        WeakReference<AppCompatActivity> mActivity = new WeakReference<>(activity);
        mPermissionRequestFragment.setActivity(mActivity.get());

    }

    public void checkPermissions(OnPermissionListener listener) {
        try {
            if (mPermissionRequestFragment != null) {
                mPermissionRequestFragment.setOnPermissionListener(listener);
                mPermissionRequestFragment.initPermissions();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface OnPermissionListener {
        void onPermissionsOk();
    }


    public static class PermissionRequestFragment extends Fragment {


        private OnPermissionListener mOnPermissionListener;
        private AppCompatActivity mActivity;
        private Context mContext;

        /**
         * 需要申请的权限
         */
        private String[] LOCATION_PERMISSION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        private String[] BLUETOOTH_PERMISSION = new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_CONNECT};
        private ActivityResultLauncher<String[]> mActivityResultLauncher;
        private ActivityResultLauncher<Intent> mActivityResultIntentLauncher;
        private int mErrNumber;
        private FragmentManager mFragmentManager;

        public void setOnPermissionListener(OnPermissionListener onPermissionListener) {
            mOnPermissionListener = onPermissionListener;
        }

        private void setActivity(AppCompatActivity activity) {
            mActivity = activity;
            mContext = activity;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            initPermissions();
            initLocationActivity();
        }

        protected void initPermissions() {
            // 创建Fragment
            if (!isAdded()) {
                if (mActivity != null) {
                    mFragmentManager = mActivity.getSupportFragmentManager();
                    mFragmentManager.beginTransaction().add(0, this).commitAllowingStateLoss();
                    mFragmentManager.executePendingTransactions();
                }
                return;
            }
            if (mContext == null || mActivity == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!checkBluetoothPermission()) {
                    //没有权限,请求开启
                    requestBluetoothPermission();
                } else {
                    onPermissionsOk();
                }
            } else {
                if (!checkBluetoothPermission()) {
                    //没有权限,请求开启
                    requestLocationPermission();
                } else {
                    boolean bleStatus = isLocServiceEnable(mContext);
                    if (!bleStatus) {
                        //没有开启定位服务
                        HintDataDialogFragment.newInstance().setTitle("提示", 0).setContent("请求开启定位服务", true).setOnDialogListener(new HintDataDialogFragment.onDialogListener() {
                            @Override
                            public void onCancelListener(View v) {

                            }

                            @Override
                            public void onSucceedListener(View v) {
                                startLocationActivity();
                            }
                        }).show(mActivity.getSupportFragmentManager());
                    } else {
                        onPermissionsOk();
                    }
                }
            }

        }

        /**
         * 检查蓝牙权限
         *
         * @return boolean
         */
        private boolean checkBluetoothPermission() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return true;
            }
            String[] perms;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                perms = BLUETOOTH_PERMISSION;
            } else {
                perms = LOCATION_PERMISSION;

            }
            for (String perm : perms) {
                if (ContextCompat.checkSelfPermission(mContext, perm) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }

        /**
         * 申请蓝牙权限
         */
        private void requestBluetoothPermission() {

            mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    //权限状态
                    boolean permissionState = true;
                    for (String s : BLUETOOTH_PERMISSION) {
                        if (Boolean.FALSE.equals(result.get(s))) {
                            permissionState = false;
                            break;
                        }
                    }
                    if (permissionState) {
                        //请求的权限都有了
                        onPermissionsOk();
                    } else {
                        //请求的权限有一个或者多个被拒绝
                        HintDataDialogFragment.newInstance().setTitle("提示", 0).setContent("请求蓝牙权限", true).setOnDialogListener(new HintDataDialogFragment.onDialogListener() {
                            @Override
                            public void onCancelListener(View v) {

                            }

                            @Override
                            public void onSucceedListener(View v) {
                                mErrNumber++;
                                if (mErrNumber >= 3) {
                                    startUseSetActivity(mContext);
                                } else {
                                    mActivityResultLauncher.launch(BLUETOOTH_PERMISSION);
                                }
                            }
                        }).show(mActivity.getSupportFragmentManager());
                    }


                }
            });
            mActivityResultLauncher.launch(BLUETOOTH_PERMISSION);
        }


        /**
         * 申请定位权限
         */
        private void requestLocationPermission() {
            mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                //权限状态
                boolean permissionState = true;
                for (String s : LOCATION_PERMISSION) {
                    if (Boolean.FALSE.equals(result.get(s))) {
                        permissionState = false;
                        break;
                    }
                }
                if (permissionState) {
                    //请求的权限都有了
                    boolean bleStatus = isLocServiceEnable(mContext);
                    if (!bleStatus) {
                        //没有开启定位服务
                        HintDataDialogFragment.newInstance().setTitle("提示", 0).setContent("请求位置权限", true).setOnDialogListener(new HintDataDialogFragment.onDialogListener() {
                            @Override
                            public void onCancelListener(View v) {

                            }

                            @Override
                            public void onSucceedListener(View v) {
                                startLocationActivity();
                            }
                        }).show(mActivity.getSupportFragmentManager());
                    } else {
                        onPermissionsOk();
                    }
                } else {
                    //请求的权限有一个或者多个被拒绝
                    HintDataDialogFragment.newInstance().setTitle("提示", 0).setContent("请求定位权限", true).setOnDialogListener(new HintDataDialogFragment.onDialogListener() {
                        @Override
                        public void onCancelListener(View v) {

                        }

                        @Override
                        public void onSucceedListener(View v) {
                            mErrNumber++;
                            if (mErrNumber >= 3) {
                                startUseSetActivity(mContext);
                            } else {
                                mActivityResultLauncher.launch(LOCATION_PERMISSION);
                            }
                        }
                    }).show(mActivity.getSupportFragmentManager());
                }


            });
            mActivityResultLauncher.launch(LOCATION_PERMISSION);
        }

        /**
         * 初始化位置活动
         */
        private void initLocationActivity() {
            mActivityResultIntentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                //定位服务页面返回
                boolean bleStatus = isLocServiceEnable(mContext);
                if (!bleStatus) {
                    //没有开启定位服务
                    HintDataDialogFragment.newInstance().setTitle("提示", 0).setContent("请求开启定位服务", true).setOnDialogListener(new HintDataDialogFragment.onDialogListener() {
                        @Override
                        public void onCancelListener(View v) {

                        }

                        @Override
                        public void onSucceedListener(View v) {
                            startLocationActivity();
                        }
                    }).show(mActivity.getSupportFragmentManager());
                } else {
                    onPermissionsOk();
                }
            });
        }

        /**
         * 启动去设置定位服务
         */
        private void startLocationActivity() {
            try {
                if (mActivityResultIntentLauncher != null) {
                    Intent localIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mActivityResultIntentLauncher.launch(localIntent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 权限ok
         */
        private void onPermissionsOk() {
            if (!isDetached() && !isRemoving()) {
                mFragmentManager.beginTransaction().detach(this).commit();
            }

            if (mOnPermissionListener != null) {
                mOnPermissionListener.onPermissionsOk();
            }
        }

    }


    /**
     * 进入应用设置界面
     */
    public static void startUseSetActivity(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(localIntent);
    }

    /**
     * 手机是否开启位置服务
     */
    public static boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }

}

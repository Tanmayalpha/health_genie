package aicare.net.cn.sdk.ailinksdkdemoandroid.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import aicare.net.cn.sdk.ailinksdkdemoandroid.utils.CheckPermissionUtils;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * xing
 * 2019/4/16 11:55
 * activity的基类, 共有的功能都可以写在这里
 */
public abstract class BleAppBaseActivity extends BleBaseActivity {
    /**
     * 上下文对象
     */
    protected Context mContext;
    protected String TAG = this.getClass().getName();
    protected Handler mHandler = new MyHandler(this);
    @Nullable
    protected TextView mTvTopTitle;
    @Nullable
    protected Toolbar mToolbar;
    private Unbinder bind;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutId() != 0) {
            initWindows();
            setContentView(getLayoutId());
            SaveActivityData(savedInstanceState);
            mContext = this;
            init();
        } else if (getLayoutView() != null) {
            initWindows();
            setContentView(getLayoutView());
            SaveActivityData(savedInstanceState);
            mContext = this;
            init();
        }

    }

    protected void SaveActivityData(Bundle savedInstanceState) {

    }


    /**
     * handler消息,使用弱引用,避免泄露问题
     */
    private static class MyHandler extends Handler {
        private WeakReference<BleAppBaseActivity> mActivity;

        MyHandler(BleAppBaseActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() == null) {
                return;
            }
            mActivity.get().uiHandlerMessage(msg);
        }
    }

    /**
     * handler消息处理
     */
    protected abstract void uiHandlerMessage(Message msg);

    /**
     * 在绑定布局前的操作（状态，任务栏等的设置）
     */
    protected void initWindows() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁止横屏
        //设置状态栏文字为黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


    /**
     * 获取当前activity的布局
     *
     * @return int
     */
    protected abstract int getLayoutId();

    /**
     * 获取当前activity的布局
     *
     * @return View
     */
    protected View getLayoutView() {
        return null;
    }

    /**
     * 初始化
     */
    protected final void init() {
        bind = ButterKnife.bind(this);
        addInit();
        initView();
        initData();
        initListener();
    }





    protected void addInit() {

    }


//-----------------------权限----------------------------------------


    protected void initPermissions() {
        new CheckPermissionUtils(this).checkPermissions(new CheckPermissionUtils.OnPermissionListener() {
            @Override
            public void onPermissionsOk() {
                BleAppBaseActivity.this.onPermissionsOk();
            }
        });

    }


    /**
     * 权限ok
     */
    protected void onPermissionsOk() {

    }


    /**
     * 初始化事件
     */
    protected abstract void initListener();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化，绑定布局
     */
    protected abstract void initView();

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.public_toolbar_menu, menu);
//        MenuItem item = menu.findItem(R.id.img_public_right);
//        if (item != null) {
//            item.setIcon(R.drawable.me_manssage);
//        }
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            myFinish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            myFinish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回
     */
    protected void myFinish() {
        finish();
    }

    protected void onClickRight() {

    }




    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (bind != null) {
            bind.unbind();
        }
    }
}

package aicare.net.cn.sdk.ailinksdkdemoandroid;

import android.os.Message;
import android.view.View;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.AppBaseActivity;

/**
 * xing<br>
 * 2022/4/6<br>
 * java类作用描述
 */
public class AboutActivity extends AppBaseActivity {


    @Override
    protected void uiHandlerMessage(Message msg) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initListener() {
        findViewById(R.id.img_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }
}

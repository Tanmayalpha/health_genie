package aicare.net.cn.sdk.ailinksdkdemoandroid.modules;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import aicare.net.cn.sdk.ailinksdkdemoandroid.base.BleBaseActivity;
import androidx.annotation.Nullable;

/**
 * 噪音计(ble)
 *
 * @author xing
 */
public class BleDemoActivity extends BleBaseActivity {

    private ListView list_view;

    private List<String> mList;
    private ArrayAdapter mListAdapter;
    private String mMac;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        list_view = findViewById(R.id.list_view);


        // 获取Mac
        mMac = getIntent().getStringExtra("mac");

        // 初始化列表
        mList = new ArrayList<>();
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        list_view.setAdapter(mListAdapter);

    }


    @Override
    public void onServiceSuccess() {

    }

    @Override
    public void onServiceErr() {

    }

    @Override
    public void unbindServices() {

    }


    /**
     * 添加一条文本
     *
     * @param text 文本
     */
    private void addText(String text) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
        mList.add(sdf.format(System.currentTimeMillis()) + "：\n" + text);
        if (mListAdapter!=null) {
            mListAdapter.notifyDataSetChanged();
        }
        if (list_view!=null) {
            list_view.smoothScrollToPosition(mList.size() - 1);
        }
    }


}

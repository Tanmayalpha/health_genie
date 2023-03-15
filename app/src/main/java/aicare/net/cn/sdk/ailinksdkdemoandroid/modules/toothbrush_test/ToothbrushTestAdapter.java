package aicare.net.cn.sdk.ailinksdkdemoandroid.modules.toothbrush_test;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ToothbrushTestAdapter extends RecyclerView.Adapter<ToothbrushTestAdapter.ViewHolder> {

    private Context mContext;
    private List<ToothbrushTestBean> mList;

    public interface OnSelectListener {
        void onSelect(int pos);
    }

    private OnSelectListener mOnSelectListener;

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        TextView tv_result;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_result = itemView.findViewById(R.id.tv_result);
        }

        void bind(int pos) {
            ToothbrushTestBean bean = mList.get(pos);
            tv_title.setText(bean.getTitle());

            if (TextUtils.isEmpty(bean.getResultStr())) {
                switch (bean.getResult()) {
                    case 0:
                        tv_result.setText("");
                        break;
                    case 1:
                        tv_result.setText("OK");
                        tv_result.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_blue_dark));
                        break;
                    case 2:
                        tv_result.setText("NG ?");
                        tv_result.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_red_dark));
                        break;
                }
            } else {
                tv_result.setText(bean.getResultStr());
                tv_result.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            }
        }

        void bindTest(int pos) {
            ToothbrushTestBean bean = mList.get(pos);
            if (bean.getType() == 1) {
                tv_title.setText("自动测试（过程中，不要手动启动牙刷）");
            } else {
                tv_title.setText("手动测试");
            }
        }
    }

    public ToothbrushTestAdapter(Context context, List<ToothbrushTestBean> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder;

        if (viewType == 0) {
            viewHolder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_toothbrush_test, parent, false));
            viewHolder.itemView.setOnClickListener(v -> {
                int pos = viewHolder.getAdapterPosition();
                if (pos != -1 && mOnSelectListener != null) {
                    mOnSelectListener.onSelect(pos);
                }
            });
        } else {
            viewHolder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_toothbrush_test_title, parent, false));
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            holder.bind(position);
        } else {
            holder.bindTest(position);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ToothbrushTestBean bean = mList.get(position);
        return bean.getType();
    }
}

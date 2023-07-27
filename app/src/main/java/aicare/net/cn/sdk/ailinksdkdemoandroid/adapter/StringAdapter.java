package aicare.net.cn.sdk.ailinksdkdemoandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pingwang.bluetoothlib.bean.BleValueBean;

import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;

/**
 * xing<br>
 * 2019/8/24<br>
 * 列表适配器<br>
 */
public class StringAdapter extends RecyclerView.Adapter<StringAdapter.KeyViewHolder> {

    private List<BleValueBean> mList;
    private OnItemClickListener listener;
    private Context mContext;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onLongClick(int position);
    }


    public StringAdapter(Context context, List<BleValueBean> list, OnItemClickListener listener) {
        mList = list;
        this.listener = listener;
        mContext = context;
    }


    @NonNull
    @Override
    public KeyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_string_list, parent, false);

        return new KeyViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyViewHolder holder, int position) {
        BleValueBean data = mList.get(position);
        String showData=data.getMac()+"="+data.getName()+"=" + data.getCid() + ";" + data.getVid() + ";" + data.getPid()+"="+data.getRssi();
        holder.mTvTitle.setText(showData);
        holder.mTvTitle.setOnClickListener(v -> {
            if (listener != null)
                listener.onItemClick(position);
        });

        holder.mTvTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null)
                    listener.onLongClick(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    static class KeyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;

        KeyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_list_data);

//            itemView.setOnClickListener(v -> {
//                if (listener != null)
//                    listener.onItemClick(getLayoutPosition());
//            });
//
//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    if (listener != null)
//                        listener.onLongClick(getLayoutPosition());
//                    return true;
//                }
//            });
        }
    }


}

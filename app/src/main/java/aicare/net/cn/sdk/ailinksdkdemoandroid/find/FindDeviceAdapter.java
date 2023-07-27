package aicare.net.cn.sdk.ailinksdkdemoandroid.find;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;

/**
 * xing<br>

 */
public class FindDeviceAdapter extends RecyclerView.Adapter<FindDeviceAdapter.KeyViewHolder> {

    private List<FindDeviceBean> mList;
    private OnItemClickListener listener;
    private Context mContext;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }


    public FindDeviceAdapter(Context context, List<FindDeviceBean> list, OnItemClickListener listener) {
        mList = list;
        this.listener = listener;
        mContext = context;
    }


    @NonNull
    @Override
    public KeyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_find_device, parent, false);

        return new KeyViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyViewHolder holder, int position) {
        FindDeviceBean bean = mList.get(position);
        holder.btn_find_device.setText("MAC="+bean.getMac());
        holder.btn_find_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    static class KeyViewHolder extends RecyclerView.ViewHolder {

        private Button btn_find_device;

        KeyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            btn_find_device = itemView.findViewById(R.id.btn_find_device);

            itemView.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClick(getLayoutPosition());
            });
        }
    }




}

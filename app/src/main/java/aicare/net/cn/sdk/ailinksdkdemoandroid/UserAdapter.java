package aicare.net.cn.sdk.ailinksdkdemoandroid;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.net.aicare.modulelibrary.module.BodyFatScale.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> mUser;
    private Context mContext;

    public UserAdapter(Context context, List<User> mUser) {
        this.mContext = context;
        this.mUser = mUser;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_user_info,parent));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return mUser.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements SeekBar.OnSeekBarChangeListener {

        private TextView id;
        private SeekBar agesb, heightsb, weightsb, adcsb;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.id);
            agesb = itemView.findViewById(R.id.adcsb);
            agesb.setOnSeekBarChangeListener(this);
            heightsb = itemView.findViewById(R.id.heightsb);
            heightsb.setOnSeekBarChangeListener(this);
            weightsb = itemView.findViewById(R.id.weightsb);
            weightsb.setOnSeekBarChangeListener(this);
            adcsb = itemView.findViewById(R.id.adcsb);
            adcsb.setOnSeekBarChangeListener(this);

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int id = seekBar.getId();
            User user = mUser.get(getLayoutPosition());
            if (id == R.id.weightsb) {
                user.setWeight(progress);

            } else if (id == R.id.adcsb) {
                user.setAdc(progress);

            } else if (id == R.id.heightsb) {
                user.setHeight(progress);

            } else if (id == R.id.age) {
                user.setAge(progress);

            }


        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }


}

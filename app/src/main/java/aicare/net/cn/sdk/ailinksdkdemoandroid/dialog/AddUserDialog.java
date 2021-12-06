package aicare.net.cn.sdk.ailinksdkdemoandroid.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import aicare.net.cn.sdk.ailinksdkdemoandroid.R;

import cn.net.aicare.modulelibrary.module.BodyFatScale.BodyFatDataUtil;
import cn.net.aicare.modulelibrary.module.BodyFatScale.User;

public class AddUserDialog extends DialogFragment implements View.OnClickListener , SeekBar.OnSeekBarChangeListener, RadioGroup.OnCheckedChangeListener {
    private boolean mCancelBlank;
    private TextView id;
    private SeekBar agesb, heightsb, weightsb, adcsb;
    private RadioGroup sexRG, modeRG;
    private User user;
    private TextView tv_move_data_ok, tv_move_data_cancel;
    private OnDialogListener mOnDialogListener;


    public AddUserDialog( OnDialogListener onDialogListener) {
        user=new User();
        user.setModeType(BodyFatDataUtil.MODE_ORDINARY);
        user.setSex(BodyFatDataUtil.SEX_MAN);
        user.setAge(0);
        user.setHeight(0);
        user.setAdc(0);
        user.setWeight(0);
        mOnDialogListener = onDialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialogView = new Dialog(requireContext(), R.style.MyDialog);// 创建自定义样式dialog
        dialogView.setCancelable(false);//设置是否可以关闭
        dialogView.setCanceledOnTouchOutside(mCancelBlank);//设置点击空白处是否可以取消
        dialogView.setOnKeyListener((dialog, keyCode, event) -> {
            if (mCancelBlank) {
                return false;
            } else {
                //返回不关闭
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        return dialogView;

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_user_info, container);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        agesb = view.findViewById(R.id.agesb);
        agesb.setOnSeekBarChangeListener(this);
        id = view.findViewById(R.id.id);
        tv_move_data_ok = view.findViewById(R.id.tv_move_data_ok);
        tv_move_data_cancel = view.findViewById(R.id.tv_move_data_cancel);
        tv_move_data_cancel.setOnClickListener(this);
        tv_move_data_ok.setOnClickListener(this);
        heightsb = view.findViewById(R.id.heightsb);
        heightsb.setOnSeekBarChangeListener(this);
        weightsb = view.findViewById(R.id.weightsb);
        weightsb.setOnSeekBarChangeListener(this);
        adcsb = view.findViewById(R.id.adcsb);
        adcsb.setOnSeekBarChangeListener(this);
        sexRG = view.findViewById(R.id.sexrg);
        modeRG = view.findViewById(R.id.moderg);
        sexRG.check(R.id.man);
        modeRG.check(R.id.ordinary);
        sexRG.setOnCheckedChangeListener(this);
        modeRG.setOnCheckedChangeListener(this);
        settext();

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_move_data_ok) {
            mOnDialogListener.tvSucceedListener(user);
            dismiss();
        } else if (v.getId() == R.id.tv_move_data_cancel) {
                  dismiss();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int id = seekBar.getId();
        if (id == R.id.weightsb) {
            user.setWeight(progress);

        } else if (id == R.id.adcsb) {
            user.setAdc(progress);

        } else if (id == R.id.heightsb) {
            user.setHeight(progress);

        } else if (id == R.id.agesb) {
            user.setAge(progress);

        }
        settext();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.man) {
            user.setSex(BodyFatDataUtil.SEX_MAN);
        } else if (checkedId == R.id.female) {
            user.setSex(BodyFatDataUtil.SEX_FEMAN);
        } else if (checkedId == R.id.ordinary) {
            user.setModeType(BodyFatDataUtil.MODE_ORDINARY);
        } else if (checkedId == R.id.athlete) {
            user.setModeType(BodyFatDataUtil.MODE_ATHLETE);
        } else if (checkedId == R.id.pregnant) {
            user.setModeType(BodyFatDataUtil.MODE_PREGNANT);
        }
        settext();
    }

    private void settext() {
        if (id != null) id.setText(user.toString());
    }


    public interface OnDialogListener {

        /**
         * 取消的点击事件
         */
        default void tvCancelListener() {
        }

        /**
         * 成功的点击事件
         */
        default void tvSucceedListener(User user) {
        }

    }

    public void show(@NonNull FragmentManager manager) {
        this.show(manager, "DialogFragment");
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mShow = false;
    }

   private boolean mShow=false;

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        try {
            if (!mShow) {
                super.show(manager, tag);
                mShow = true;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            mShow = false;
            super.dismiss();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

}

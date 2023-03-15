package aicare.net.cn.sdk.ailinksdkdemoandroid.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ResultContact extends ActivityResultContract<Boolean, String> {

    private Context mContext;

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Boolean input) {
        mContext = context;
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    @Override
    public String parseResult(int resultCode, @Nullable Intent intent) {
        if (intent == null || intent.getData() == null || mContext == null) {
            return null;
        }
        Uri uri = intent.getData();
        String fileName = FileUtils.getFileNameWithType(mContext, uri);
        String finalPath = FileUtils.getByFileName() + fileName;
        FileUtils.saveFileFromUri(mContext, uri, finalPath);
        //文件名
        return fileName;
    }
}

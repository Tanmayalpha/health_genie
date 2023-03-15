package aicare.net.cn.sdk.ailinksdkdemoandroid.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.io.File;

import androidx.core.content.FileProvider;

/**
 * 兼容android 7.0以上获取uri异常的工具类
 */
public class FileProvider7 {

    /**
     * 获取uri
     * @return Uri
     */
    public static Uri getUriForFile(Context context, File file) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = getUriForFile24(context, file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    /**
     * android 7.0以上获取uri的方法
     */
    private static Uri getUriForFile24(Context context, File file) {

        return FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider",
                file);
    }






    /**
     * 安装app的操作,兼容7.0
     * @param context 上下文
     * @param intent  intent
     * @param  type     执行的意图(application/vnd.android.package-archive-->安装app)
     * @param file 文件
     * @param writeAble 是否需要写操作
     */
    public static void setIntentDataAndType(Context context,
                                            Intent intent,
                                            String type,
                                            File file,
                                            boolean writeAble) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(getUriForFile(context, file), type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
    }
}
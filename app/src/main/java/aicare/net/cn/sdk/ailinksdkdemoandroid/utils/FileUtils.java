package aicare.net.cn.sdk.ailinksdkdemoandroid.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import aicare.net.cn.sdk.ailinksdkdemoandroid.base.MyApplication;


public class FileUtils {
    private static String TAG = FileUtils.class.getName();
    private static String filesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
    public static String FILE_DIR = "AiLinkBleDemo";
    public static String FILE_NAME = "updateName.img";


    public static void init() {
        filesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        createFileDirectories(FILE_DIR);
        // android 11 以上要在包名下也创建对应的文件夹，后续拷贝文件到存放到文件夹下
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            filesDir = MyApplication.getInstance().getExternalCacheDir().getAbsolutePath();
            createFileDirectories(FILE_DIR);
        }
    }


    public static String getByFileName() {
        return filesDir + File.separator;
    }

    /**
     * 创建文件夹
     */
    private static void createFileDirectories(String directoryName) {
        filesDir = filesDir + File.separator + directoryName;
        File directory = new File(filesDir);
        if (!directory.exists()) {
            boolean mkdirs = directory.mkdirs();
            L.i(TAG, "创建OTA文件:" + mkdirs);
        }
    }

    public static ArrayList<String> list() {
        File f = new File(filesDir);
        File file[] = f.listFiles();
        if (file == null)
            return new ArrayList<>();
        Arrays.sort(file, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return lhs.getPath().compareToIgnoreCase(rhs.getPath());
            }
        });
        L.d("Files", "Size: " + file.length);
        ArrayList<String> names = new ArrayList<String>();
        for (File value : file) {
            L.d("Files", "FileName:" + value.getName());
            names.add(value.getName());
        }
        return names;
    }

    public static final String DOCUMENTS_DIR = "documents";

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);

                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4);
                }
                String[] contentUriPrefixesToTry = new String[]{
                        "content://downloads/public_downloads",
                        "content://downloads/my_downloads"
                };
                for (String contentUriPrefix : contentUriPrefixesToTry) {
                    try {
                        // note: id 可能为字符串，如在华为10.0系统上，选择文件后id为："msf:254"，导致转Long异常
                        Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.parseLong(id));
                        String path = getDataColumn(context, contentUri, null, null);
                        if (path != null && !path.equals("")) {
                            return path;
                        }
                    } catch (Exception e) {
                    }
                }
                // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                String fileName = getFileName(context, uri);
                File cacheDir = getDocumentCacheDir(context);
                File file = generateFileName(fileName, cacheDir);
                String destinationPath = null;
                if (file != null) {
                    destinationPath = file.getAbsolutePath();
                    saveFileFromUri(context, uri, destinationPath);
                }
                return destinationPath;
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            String path = getDataColumn(context, uri, null, null);
            if (path != null && !path.equals("")) return path;
            // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
            String fileName = getFileName(context, uri);
            File cacheDir = getDocumentCacheDir(context);
            File file = generateFileName(fileName, cacheDir);
            String destinationPath = null;
            if (file != null) {
                destinationPath = file.getAbsolutePath();
                saveFileFromUri(context, uri, destinationPath);
            }
            return destinationPath;
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        String path = "";
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                path = cursor.getString(column_index);
                return path;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return path;
    }

    public static String getFileName(@NonNull Context context, Uri uri) {
        String filename = getFileNameWithType(context, uri);
        String[] filename_s = filename.split("\\.", 2);
        return filename_s[0];
    }

    public static String getFileNameWithType(@NonNull Context context, Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        String filename = null;
        if (mimeType == null && context != null) {
            String path = getPath(context, uri);
            if (path == null) {
                filename = getName(uri.toString());
            } else {
                File file = new File(path);
                filename = file.getName();
            }
        } else {
            Cursor returnCursor = context.getContentResolver().query(uri, null,
                    null, null, null);
            if (returnCursor != null) {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                filename = returnCursor.getString(nameIndex);
                returnCursor.close();
            }
        }
        return filename;
    }

    public static String getName(String filename) {
        if (filename == null) {
            return null;
        }
        int index = filename.lastIndexOf('/');
        return filename.substring(index + 1);
    }

    public static File getDocumentCacheDir(@NonNull Context context) {
        Log.d("PickUtils", "getDocumentCacheDir");
        File dir = new File(context.getCacheDir(), DOCUMENTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    @Nullable
    public static File generateFileName(@Nullable String name, File directory) {
        if (name == null) {
            return null;
        }

        File file = new File(directory, name);

        if (file.exists()) {
            String fileName = name;
            String extension = "";
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex > 0) {
                fileName = name.substring(0, dotIndex);
                extension = name.substring(dotIndex);
            }

            int index = 0;

            while (file.exists()) {
                index++;
                name = fileName + '(' + index + ')' + extension;
                file = new File(directory, name);
            }
        }
        try {
            if (!file.createNewFile()) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        return file;
    }

    /**
     * 保存文件到内部目录
     *
     * @param context context
     * @param uri uri
     * @param destinationPath destinationPath
     */
    public static void saveFileFromUri(Context context, Uri uri, String destinationPath) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            if (is == null) {
                return;
            }
            File file = new File(destinationPath);
            file.createNewFile();
            FileOutputStream fos = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                fos = new FileOutputStream(file);
                android.os.FileUtils.copy(is, fos);
            } else {
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024 * 10];
                while (true) {
                    int len = is.read(buffer);
                    if (len == -1) {
                        break;
                    }
                    arrayOutputStream.write(buffer, 0, len);
                }
                arrayOutputStream.close();
                byte[] dataByte = arrayOutputStream.toByteArray();
                if (dataByte.length > 0) {
                    fos = new FileOutputStream(file);
                    fos.write(dataByte);
                }
            }
            if (fos != null)
                fos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

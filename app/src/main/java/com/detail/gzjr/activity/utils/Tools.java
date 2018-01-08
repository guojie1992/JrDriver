package com.detail.gzjr.activity.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;


public class Tools {
    /**
     * 检查是否存在SDCard
     * @return
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 2 * 获取版本号 3 * @return 当前应用的版本号 4
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),0);
            String version = info.versionName;
            int versioncode =info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

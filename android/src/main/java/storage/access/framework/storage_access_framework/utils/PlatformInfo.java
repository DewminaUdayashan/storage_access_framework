package storage.access.framework.storage_access_framework.utils;

import android.os.Build;

public class PlatformInfo {
    public static int getPlatformVersion() {
        return Build.VERSION.SDK_INT;
    }
}

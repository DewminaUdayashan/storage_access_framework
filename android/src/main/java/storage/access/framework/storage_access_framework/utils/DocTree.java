package storage.access.framework.storage_access_framework.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.util.Log;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class DocTree {
    final public static int REQUEST_PERMISSION_CODE = 11111;
    final private static String TAG = "DOC_TREE FUNCTION => ";
    public Activity activity;

    public DocTree(Activity activity) {
        Log.d(TAG, "DocTree: DOC TREE INSTANT CREATED ====================");
        this.activity = activity;
    }

    public void openDocTree(String uri) {
        Intent intent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                            | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
            if (uri != null)
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(uri));
            activity.startActivityForResult(intent, REQUEST_PERMISSION_CODE);
        }
//        StorageManager storageManager = (StorageManager) activity.getSystemService(Context.STORAGE_SERVICE);
//        Intent intent = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//            intent = storageManager.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
//        }
//        String targetDirectory = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";
//        if (intent != null) {
////            Uri urii = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");
////            String scheme = urii.toString();
////            scheme = scheme.replace("/root/", "/document/");
////            scheme += "%3A" + targetDirectory;
////            Log.d(TAG, "openDocTree: SCHEME COPY THIS ==> " + scheme);
////            urii = Uri.parse(uri);
//            Log.d(TAG, "openDocTree: " + uri.toString());
//            intent.putExtra("android.provider.extra.INITIAL_URI", uri);
//            activity.startActivityForResult(intent, REQUEST_PERMISSION_CODE);
//        }
    }

    boolean isPermissionExist = false;

    public void saveDir(Uri uri) {
        Log.d(TAG, "saveDir: LOOKING FOR DIRECTORY TREE");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            List<UriPermission> existing = loadSavedDir();
            if (existing != null && !existing.isEmpty()) {
                Log.d(TAG, "saveDir: UriPermissions list size " + existing.size());
                for (UriPermission permission : existing) {
                    if (permission.getUri().equals(uri)) {
                        isPermissionExist = true;
                        return;
                    }
                }
            }
            if (isPermissionExist) {
                activity.getContentResolver().releasePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                activity.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }
    }

    public List<UriPermission> loadSavedDir() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            return activity.getContentResolver().getPersistedUriPermissions();
        else return null;
    }


    public boolean checkPermissionForUri(String uri) {
        Log.d(TAG, "checkPermissionForUri: REQUESTED URI" + uri);
        List<UriPermission> permissions = loadSavedDir();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            for (UriPermission permission : permissions) {
                Log.d(TAG, "checkPermissionForUri: AVAILABL PERMISSIONS " + permission.getUri().getPath());
                if (permission.getUri().getPath().equals(uri)) {
                    Log.d(TAG, "checkPermissionForUri: Permission Exist");
                    return true;
                }
            }
        return false;
    }


    public boolean ifDirExists(String uri) {
        Log.d(TAG, "ifDirExists: Checking Path => " + uri);
        return new File(uri).exists();
    }

    public void scanMediaFiles() {
        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE));
    }

}

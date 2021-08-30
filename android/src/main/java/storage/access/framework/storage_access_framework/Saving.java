package storage.access.framework.storage_access_framework;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class Saving {
 private final static String TAG = "SAVING FUNCTION => "; 

    public  void save(Activity activity){
        File dir = commonDocumentDirPath("Statuses",activity);
        
    }

    public  static File commonDocumentDirPath(String FolderName,Activity activity){
        File dir = null ;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dir = new File (activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+ "/"+FolderName );
            Log.d(TAG, "commonDocumentDirPath: "+ dir);
        } else {
            dir = new File(Environment.getExternalStorageDirectory() + "/"+FolderName);
            Log.d(TAG, "commonDocumentDirPath: "+ dir);

        }

        // Make sure the path directory exists.
        if (!dir.exists()) {
            // Make it, if it doesn't exit
            boolean success=dir.mkdirs();
            if(!success) {dir=null;}


        }

        return  dir ;

    }
}

package storage.access.framework.storage_access_framework_example;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import io.flutter.embedding.android.FlutterActivity;
import storage.access.framework.storage_access_framework.StorageAccessFrameworkPlugin;
import storage.access.framework.storage_access_framework.utils.DocTree;

public class MainActivity extends FlutterActivity {
    final private String TAG = "ON ACTIVITY RESULT";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == DocTree.REQUEST_PERMISSION_CODE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: SELECTED DIR => " + data.getData().getPath());
            StorageAccessFrameworkPlugin.docTree.saveDir(data.getData());
        } else if (requestCode == DocTree.REQUEST_PERMISSION_CODE && resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "onActivityResult: CANCELLED CHOOSING");
        } else if (requestCode == 100) {
            Log.d(TAG, "onActivityResult: REQUEST CODE 100 WITH START TO ON ACTIVITY RESULT");
        }
    }
}

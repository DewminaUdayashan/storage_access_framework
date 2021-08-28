package storage.access.framework.storage_access_framework;

import android.app.Activity;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.util.Log;


import androidx.annotation.NonNull;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import storage.access.framework.storage_access_framework.image.Image;
import storage.access.framework.storage_access_framework.utils.DocTree;
import storage.access.framework.storage_access_framework.utils.PlatformInfo;

import static io.flutter.plugin.common.PluginRegistry.*;

/**
 * StorageAccessFrameworkPlugin
 */
public class StorageAccessFrameworkPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, ActivityResultListener {
    private MethodChannel channel;
    private Activity activity;
    private DocTree docTree;
    private Result result;
    final private static String TAG = "SAF PLUGIN => ";


    @Override
    public void onAttachedToEngine(FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "storage_access_framework");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        this.result = result;
        try {
//            final String whatsAppUri = arg.get("wa");
//            final String whatsApp4BUri = arg.get("wa4b");
//            final String whatsAppDualUri = arg.get("waDual");
//            final String whatsAppGBUri = arg.get("waGB");
            final Map<String, String> arg = call.arguments();

            switch (call.method) {
                case "getPlatformVersion":
                    result.success(PlatformInfo.getPlatformVersion());
                    break;
                case "openDocumentTree":
                    Log.d(TAG, "onMethodCall: OPEN DOC TREE CALLED");
                    try {
                        final String openDocTreeInitialUri = arg.get("initialUri");
                        docTree.openDocTree(openDocTreeInitialUri);
                    } catch (Exception e) {
                        docTree.openDocTree(null);
                    }
                    break;
                case "checkPermissionForUri":
                    Log.d(TAG, "onMethodCall: Checking Uri Permission");
                    final String checkPermissionForUri = arg.get("checkPermissionFor");
                    result.success(docTree.checkPermissionForUri(checkPermissionForUri));
                    Log.d(TAG, "onMethodCall: permission requested " + Uri.parse(checkPermissionForUri).getPath());
                    for (UriPermission permission : docTree.loadSavedDir()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Log.d(TAG, "onMethodCall: permission " + permission.getUri());
                        }
                    }
                    break;
                case "getImages":
                    final String path = arg.get("imagePath");
                    result.success(Image.getImages(path, activity));
                    break;
                case "isDirExist":
                    final String dirPath = arg.get("dirPath");
                    result.success(docTree.ifDirExists(dirPath));
                    break;
                default:
                    result.notImplemented();
                    break;
            }
        } catch (Exception e) {
            Log.d(TAG, "onMethodCall: MAP EMPTY " + e.getMessage());
        }
    }


    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        activity = binding.getActivity();
        docTree = new DocTree(activity);
        Log.d(TAG, "onAttachedToActivity: ");
        binding.addActivityResultListener(this);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
        docTree.activity = null;
        Log.d(TAG, "onDetachedFromActivityForConfigChanges: ");
    }


    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        activity = binding.getActivity();
        docTree.activity = binding.getActivity();
        binding.addActivityResultListener(this);
        Log.d(TAG, "onReattachedToActivityForConfigChanges: ");
    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
        docTree.activity = null;
        Log.d(TAG, "onDetachedFromActivity: ");
    }


    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DocTree.REQUEST_PERMISSION_CODE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: SELECTED DIR => " + data.getData().getPath());
            result.success(data.getData().getPath());
            docTree.saveDir(data.getData());
            return true;
        } else if (requestCode == DocTree.REQUEST_PERMISSION_CODE && resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "onActivityResult: CANCELLED CHOOSING");
            result.success(null);
            return true;
        }
        return false;
    }


}

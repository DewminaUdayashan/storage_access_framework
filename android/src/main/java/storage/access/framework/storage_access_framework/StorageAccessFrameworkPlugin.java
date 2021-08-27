package storage.access.framework.storage_access_framework;

import android.app.Activity;
import android.content.Intent;
import android.content.UriPermission;
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
import storage.access.framework.storage_access_framework.utils.DocTree;
import storage.access.framework.storage_access_framework.utils.PlatformInfo;

/**
 * StorageAccessFrameworkPlugin
 */
public class StorageAccessFrameworkPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener {
    private MethodChannel channel;
    private Activity activity;
    public static DocTree docTree;
    final private static String TAG = "SAF PLUGIN => ";


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
//        plugin = new StorageAccessFrameworkPlugin();
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "storage_access_framework");
        channel.setMethodCallHandler(this);
    }


    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        try {
//            final String whatsAppUri = arg.get("wa");
//            final String whatsApp4BUri = arg.get("wa4b");
//            final String whatsAppDualUri = arg.get("waDual");
//            final String whatsAppGBUri = arg.get("waGB");
            if (call.method.equals("getPlatformVersion")) {
                result.success(PlatformInfo.getPlatformVersion());
            } else if (call.method.equals("openDocumentTree")) {
                final Map<String, String> arg = call.arguments();
                final String openDocTreeInitialUri = arg.get("initialUri");
                docTree.openDocTree(openDocTreeInitialUri);
                for (UriPermission permission : docTree.loadSavedDir()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Log.d(TAG, "onMethodCall: permission => " + permission.getUri().getPath());
                    }
                }
            } else {
                result.notImplemented();
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
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
        docTree = new DocTree(activity);
        Log.d(TAG, "onAttachedToActivity: ");
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
        docTree.activity = null;
        Log.d(TAG, "onDetachedFromActivityForConfigChanges: ");
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
        docTree.activity = binding.getActivity();

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
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == DocTree.REQUEST_PERMISSION_CODE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: SELECTED DIR => " + data.getData().getPath());
            docTree.saveDir(data.getData());
            return true;
        } else if (requestCode == DocTree.REQUEST_PERMISSION_CODE && resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "onActivityResult: CANCELLED CHOOSING");
            return true;
        } else if (requestCode == 100) {
            Log.d(TAG, "onActivityResult: REQUEST CODE 100 WITH START TO ON ACTIVITY RESULT");
        }
        return false;

    }


}

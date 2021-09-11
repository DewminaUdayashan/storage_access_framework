package storage.access.framework.storage_access_framework;

import android.app.Activity;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import storage.access.framework.storage_access_framework.image.Image;
import storage.access.framework.storage_access_framework.utils.DocTree;

import static io.flutter.plugin.common.PluginRegistry.ActivityResultListener;

/**
 * StorageAccessFrameworkPlugin
 */
public class StorageAccessFrameworkPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, ActivityResultListener {
    private MethodChannel channel;
    private Activity activity;
    private DocTree docTree;
    private Result result;
    private Saving saving = new Saving();
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

            switch (call.method) {
                case "getPlatformVersion":
                    result.success(Build.VERSION.SDK_INT);
                    break;
                case "openDocumentTree":
                    Log.d(TAG, "onMethodCall: OPEN DOC TREE CALLED");
                    final Map<String, String> arg = call.arguments();

                    try {
                        final String openDocTreeInitialUri = arg.get("initialUri");
                        docTree.openDocTree(openDocTreeInitialUri);
                    } catch (Exception e) {
                        docTree.openDocTree(null);
                    }
                    break;
                case "checkPermissionForUri":
                    Log.d(TAG, "onMethodCall: Checking Uri Permission");
                    final Map<String, String> arg1 = call.arguments();
                    final String checkPermissionForUri = arg1.get("checkPermissionFor");
                    result.success(docTree.checkPermissionForUri(checkPermissionForUri));
                    Log.d(TAG, "onMethodCall: permission requested " + Uri.parse(checkPermissionForUri).getPath());
                    for (UriPermission permission : docTree.loadSavedDir()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Log.d(TAG, "onMethodCall: permission " + permission.getUri());
                        }
                    }
                    break;
                case "getImages":
                    final Map<String, ArrayList<String>> arg2 = call.arguments();
                    final String path = Objects.requireNonNull(arg2.get("imagePath")).get(0);
                    final ArrayList<String> types = arg2.get("fileExtensions");
                    if (types == null) Log.d(TAG, "onMethodCall: FILE EXTENSIONS NULL");
                    Log.d(TAG, "onMethodCall: FILE EXTENSIONS LEN : " + types.size());
                    Image image = new Image();
                    image.getImages(path, activity, types, result);
                    break;
                case "isDirExist":
                    final Map<String, String> arg3 = call.arguments();
                    final String dirPath = arg3.get("dirPath");
                    result.success(docTree.ifDirExists(dirPath));
                    break;
                case "scanMediaFiles":
                    docTree.scanMediaFiles();
                    result.success(true);
                    break;
                case "saveMedia":
                    final Map<String, Object> arg4 = call.arguments();
                    final ArrayList<byte[]> bytes = (ArrayList<byte[]>) arg4.get("bytes");
                    final String mimeType = Objects.requireNonNull(arg4.get("mimeType")).toString();
                    if (bytes != null) {
                        result.success(saving.save(activity, bytes, mimeType));
                    } else {
                        result.success(false);
                        result.error("404", "DATA NOT FOUND", "Data not received");
                    }
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
        binding.addActivityResultListener(this);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
        docTree.activity = null;
    }


    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        activity = binding.getActivity();
        docTree.activity = binding.getActivity();
        binding.addActivityResultListener(this);
    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
        docTree.activity = null;
    }


    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DocTree.REQUEST_PERMISSION_CODE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: SELECTED DIR => " + data.getData().getPath());
            result.success(data.getData().getPath());
            docTree.saveDir(data.getData());
            return true;
        } else if (requestCode == DocTree.REQUEST_PERMISSION_CODE && resultCode == Activity.RESULT_CANCELED) {
            result.success("dirNotSelected%#@%@");
            return true;
        }
        return false;
    }


}

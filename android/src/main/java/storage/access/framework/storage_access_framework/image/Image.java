package storage.access.framework.storage_access_framework.image;

import android.app.Activity;
import android.app.ActivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import io.flutter.plugin.common.MethodChannel;

public class Image {

    final private static String TAG = "IMAGE FUNCTIONS";
    final private Handler mainHandler = new Handler();

    public void getImages(String uri, Activity context, ArrayList<String> types, @NonNull MethodChannel.Result result) {
        BackgroundProcess backgroundProcess = new BackgroundProcess(uri, context, types, result);
        new Thread(backgroundProcess).start();
    }


    class BackgroundProcess implements Runnable {

        String uri;
        Activity context;
        ArrayList<String> types;
        MethodChannel.Result result;

        BackgroundProcess(String uri, Activity context, ArrayList<String> types, @NonNull MethodChannel.Result result) {
            this.uri = uri;
            this.context = context;
            this.types = types;
            this.result = result;
        }


        @Override
        public void run() {
            List<byte[]> images = new ArrayList<byte[]>();
            DocumentFile pickedDir = DocumentFile.fromTreeUri(context, Uri.parse(uri));
            if (pickedDir != null) {
                DocumentFile[] files = pickedDir.listFiles();
                Log.d(TAG, "getImages: IMAGES LEN " + files.length);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                Arrays.sort(files, Comparator.comparingLong(DocumentFile::lastModified).reversed());
//            }

                for (DocumentFile file : files) {
                    try {
                        InputStream inputStream = context.getContentResolver().openInputStream(file.getUri());

                        if (types.isEmpty()) {
                            BufferedInputStream bis = new BufferedInputStream(inputStream);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            byte buffer[] = new byte[1024];
                            int len;
                            while ((len = bis.read(buffer, 0, buffer.length)) > 0) {
                                baos.write(buffer, 0, len);
                            }
                            bis.close();
                            byte[] imageData = baos.toByteArray();
                            baos.close();
                            images.add(imageData);
                        } else {
                            for (String type : types) {
                                if (Objects.requireNonNull(file.getName()).contains(type)) {
                                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    byte[] buffer = new byte[1024];
                                    int len;
                                    while ((len = bis.read(buffer, 0, buffer.length)) > 0) {
                                        baos.write(buffer, 0, len);
                                    }
                                    bis.close();
                                    byte[] imageData = baos.toByteArray();
                                    baos.close();
                                    images.add(imageData);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "getImages: Exception Occurred => " + e);
                    }

                }
            }
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    result.success(images);
                }
            });
        }
    }

}

package storage.access.framework.storage_access_framework.image;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Image {

    final private static String TAG = "IMAGE FUNCTIONS";

    public static List<byte[]> getImages(String uri, Activity context, ArrayList<String> types) {
        List<byte[]> images = new ArrayList<byte[]>();
        DocumentFile pickedDir = DocumentFile.fromTreeUri(context, Uri.parse(uri));
        if (pickedDir != null) {
            DocumentFile[] files = pickedDir.listFiles();
            Log.d(TAG, "getImages: IMAGES LEN " + files.length);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                Arrays.sort(files, Comparator.comparingLong(DocumentFile::lastModified).reversed());
//            }
            List<String> arr = new ArrayList<String>();

            for (DocumentFile file : files) {

                try {
                    if (types.isEmpty()) {
                        InputStream iStream = context.getContentResolver().openInputStream(file.getUri());
                        byte[] inputData = getBytes(iStream);
                        Log.d(TAG, "getImages: IMAGE BYTES" + Arrays.toString(inputData));
                        images.add(inputData);
                    } else {
                        for (String type : types) {
                            Log.d(TAG, "getImages: FILE EXTENSION => " + type);
                            if (Objects.requireNonNull(file.getName()).contains(type)) {
                                InputStream iStream = context.getContentResolver().openInputStream(file.getUri());
                                byte[] inputData = getBytes(iStream);
                                Log.d(TAG, "getImages: IMAGE BYTES" + Arrays.toString(inputData));
                                images.add(inputData);
                            }
                        }
                    }

//                    if (Objects.requireNonNull(file.getName()).contains(".jpg") || Objects.requireNonNull(file.getName()).contains(".png") || Objects.requireNonNull(file.getName()).contains(".jpeg")) {
//                        InputStream iStream = context.getContentResolver().openInputStream(file.getUri());
//                        byte[] inputData = getBytes(iStream);
//                        Log.d(TAG, "getImages: IMAGE BYTES" + Arrays.toString(inputData));
//                        images.add(inputData);
//                    }
                } catch (Exception e) {
                    Log.d(TAG, "getImages: Exception Occurred => " + e.getMessage());
                }

            }
        }
        Log.d(TAG, "getImages: IMAGES LEN BEFORE SEND " + images.size());
        return images;
    }

    static ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
    static int bufferSize = 1024;
    static int len = 0;


    private static byte[] getBytes(InputStream inputStream) throws IOException {
        byteBuffer.reset();
        len = 0;
        byte[] buffer = new byte[bufferSize];
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        byteBuffer.close();
        return byteBuffer.toByteArray();
    }


}

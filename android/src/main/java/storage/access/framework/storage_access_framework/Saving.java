package storage.access.framework.storage_access_framework;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Saving {
    private final static String TAG = "SAVING FUNCTION => ";

    public boolean save(Activity activity, ArrayList<byte[]> bytes, String mimeType) {
        String extention;
        boolean saved = false;
        String name;
        final String IMAGES_FOLDER_NAME = "WhatsStatus";
        OutputStream fos;
        if (mimeType.contains("image")) extention = ".jpg";
        else extention = ".mp4";
        for (int i = 0; i < bytes.size(); i++) {
//            Log.d(TAG, "save: EXTENSION ==============> " + extention);
//            Log.d(TAG, "save: MIME TYPE ==============> " + mimeType);
            name = String.valueOf(System.currentTimeMillis()) + i;
            byte[] aByte = bytes.get(i);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver resolver = activity.getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);//"image/jpeg"
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + IMAGES_FOLDER_NAME);
                    Uri imageUri;
                    if ((mimeType.contains("image")))
                        imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    else
                        imageUri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
                    fos = resolver.openOutputStream(imageUri);
                } else {
                    String imagesDir = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DCIM).toString() + File.separator + IMAGES_FOLDER_NAME;
                    File file = new File(imagesDir);
                    if (!file.exists()) {
                        if (file.mkdir()) {
                            Log.d(TAG, "save: DIR CREATED");
                        }
                    }
                    File image = new File(imagesDir, name + extention);
                    fos = new FileOutputStream(image);
                    scanMedia(activity, image.getPath());
                }
                fos.write(aByte);
                fos.flush();
                fos.close();
                saved = true;
            } catch (Exception e) {
                saved = false;
                e.printStackTrace();
            }
        }
        return saved;
    }


    private void scanMedia(Activity activity, String mediaPath) {
        MediaScannerConnection.scanFile(activity, new String[]{mediaPath}, new String[]{"image/jpeg", "video/mp4", "image/png", "image/gif"}, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.d(TAG, "onScanCompleted: Scanned Path : " + path);
            }
        });
    }

}

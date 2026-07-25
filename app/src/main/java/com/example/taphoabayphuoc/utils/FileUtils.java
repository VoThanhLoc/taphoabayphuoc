package com.example.taphoabayphuoc.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class FileUtils {

    public static String saveImageToInternal(Context context, Uri uri) {
        if (uri == null) return null;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File storageDir = context.getExternalFilesDir("product_images");
            if (!storageDir.exists()) storageDir.mkdirs();

            String fileName = "IMG_" + UUID.randomUUID().toString() + ".jpg";
            File file = new File(storageDir, fileName);

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e("FileUtils", "Error saving image", e);
            return null;
        }
    }

    public static Object getGlidePath(String path) {
        if (path == null || path.isEmpty()) return null;

        // If it's a file path
        if (path.startsWith("/")) {
            return new File(path);
        }
        
        // If it starts with file://, strip it and return File object
        if (path.startsWith("file://")) {
            return new File(path.substring(7));
        }

        // If it's content:// or other URIs, return as is (Glide will try to handle)
        return path;
    }
}

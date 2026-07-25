package com.example.taphoabayphuoc.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;

public class ApkInstaller {

    public static void install(Context context, File apkFile) {
        if (apkFile == null || !apkFile.exists()) {
            Log.e("INSTALL", "APK file not found");
            return;
        }

        // Check for Android 8.0+ Unknown Sources permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.getPackageManager().canRequestPackageInstalls()) {
                Toast.makeText(context, "Vui lòng cho phép cài đặt ứng dụng từ nguồn này, sau đó nhấn Cập nhật lại", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            }
        }

        try {
            Uri uri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    apkFile
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("INSTALL", "Error starting installation", e);
            Toast.makeText(context, "Lỗi cài đặt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

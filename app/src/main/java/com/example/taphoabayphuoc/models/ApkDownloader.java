package com.example.taphoabayphuoc.models;

import android.content.Context;
import android.util.Log;

import com.example.taphoabayphuoc.listener.DownloadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ApkDownloader {

    private final Context context;
    private final OkHttpClient client = new OkHttpClient();

    public ApkDownloader(Context context) {
        this.context = context;
    }

    public void download(String url, DownloadCallback callback) {
        // Clear old update file if exists
        File oldFile = new File(context.getExternalFilesDir(null), "update.apk");
        if (oldFile.exists()) oldFile.delete();

        Request request = new Request.Builder()
                .url(url)
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, java.io.IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws java.io.IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Download failed: " + response.code());
                    return;
                }

                try (ResponseBody body = response.body()) {
                    if (body == null) {
                        callback.onError("Response body is null");
                        return;
                    }

                    File apkFile = new File(context.getExternalFilesDir(null), "update.apk");
                    long total = body.contentLength();
                    Log.d("DOWNLOAD", "Total size: " + total);

                    try (InputStream input = body.byteStream();
                         FileOutputStream output = new FileOutputStream(apkFile)) {

                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        long downloaded = 0;

                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                            downloaded += bytesRead;
                            
                            if (total > 0) {
                                int progress = (int) ((downloaded * 100) / total);
                                callback.onProgress(progress);
                            }
                        }
                        output.flush();
                    }

                    Log.d("DOWNLOAD", "Download complete. Size: " + apkFile.length());
                    
                    if (apkFile.length() > 0) {
                        callback.onSuccess(apkFile);
                    } else {
                        callback.onError("Downloaded file is empty");
                    }

                } catch (Exception e) {
                    Log.e("DOWNLOAD", "Error during download", e);
                    callback.onError(e.getMessage());
                }
            }
        });
    }
}

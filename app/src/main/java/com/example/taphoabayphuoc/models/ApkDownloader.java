package com.example.taphoabayphuoc.models;

import android.content.Context;

import com.example.taphoabayphuoc.listener.DownloadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        Request request = new Request.Builder()
                .url(url)
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Download failed: " + response.code());
                    return;
                }

                ResponseBody body = response.body();
                if (body == null) {
                    callback.onError("Response body is null");
                    return;
                }

                File apkFile = new File(context.getExternalFilesDir(null), "update.apk");
                long total = body.contentLength();

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
                    callback.onSuccess(apkFile);

                } catch (Exception e) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }
}

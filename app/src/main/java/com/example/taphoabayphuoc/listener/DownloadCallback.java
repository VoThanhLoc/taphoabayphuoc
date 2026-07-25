package com.example.taphoabayphuoc.listener;

import java.io.File;

public interface DownloadCallback {

    void onProgress(int progress);

    void onSuccess(File apkFile);

    void onError(String message);

}

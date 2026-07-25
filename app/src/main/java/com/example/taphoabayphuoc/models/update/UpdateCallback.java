package com.example.taphoabayphuoc.models.update;

public interface UpdateCallback {

    void onUpdateAvailable(ReleaseInfo release);

    void onLatestVersion();

    void onError(String message);
}

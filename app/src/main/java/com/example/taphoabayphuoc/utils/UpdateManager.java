package com.example.taphoabayphuoc.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.taphoabayphuoc.models.update.ReleaseInfo;
import com.example.taphoabayphuoc.models.update.UpdateCallback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
public class UpdateManager {
    private static final String RELEASE_API = "https://api.github.com/repos/VoThanhLoc/taphoabayphuoc/releases/latest";
    private final Context context;

    public UpdateManager(Context context) {
        this.context = context;
    }

    public void check(UpdateCallback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(RELEASE_API)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Unexpected code " + response);
                    return;
                }

                try (ResponseBody responseBody = response.body()) {
                    if (responseBody != null) {
                        String json = responseBody.string();
                        try {

                            JSONObject object = new JSONObject(json);
                            String latestVersion =
                                    object.getString("tag_name")
                                            .replace("v", "");

                            JSONArray assets = object.getJSONArray("assets");

                            if (assets.length() == 0) {
                                callback.onError("No APK found.");
                                return;
                            }

                            JSONObject apk = assets.getJSONObject(0);

                            String downloadUrl =
                                    apk.getString("browser_download_url");

                            ReleaseInfo release = new ReleaseInfo();

                            release.setVersion(latestVersion);
                            release.setDownloadUrl(downloadUrl);

                            String currentVersion = getCurrentVersion();

                            Log.d("UPDATE", "Current Version: [" + currentVersion + "]");
                            Log.d("UPDATE", "Latest Version from GitHub: [" + latestVersion + "]");

                            if (VersionComparator.hasNewVersion(currentVersion, latestVersion)) {
                                Log.d("UPDATE", "New version available!");
                                callback.onUpdateAvailable(release);
                            } else {
                                Log.d("UPDATE", "Already latest version.");
                                callback.onLatestVersion();
                            }

                        } catch (Exception e) {

                            callback.onError(e.getMessage());

                        }
                        // Here you can parse the JSON and check for updates
                    } else {
                        callback.onError("Response body is null");
                    }
                }
            }
        });
    }

    private String getCurrentVersion() {
        try {
            PackageInfo packageInfo =
                    context.getPackageManager()
                            .getPackageInfo(
                                    context.getPackageName(),
                                    0);
            return packageInfo.versionName;
        } catch (Exception e) {
            return "0.0.0";
        }
    }
}

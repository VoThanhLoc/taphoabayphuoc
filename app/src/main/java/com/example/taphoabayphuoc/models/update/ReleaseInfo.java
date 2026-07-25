package com.example.taphoabayphuoc.models.update;

public class ReleaseInfo {

    private String version;
    private String downloadUrl;

    public ReleaseInfo() {
    }

    public ReleaseInfo(String version, String downloadUrl) {
        this.version = version;
        this.downloadUrl = downloadUrl;
    }

    public String getVersion() {
        return version;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
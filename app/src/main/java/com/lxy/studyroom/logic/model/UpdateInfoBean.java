package com.lxy.studyroom.logic.model;

import com.google.gson.annotations.SerializedName;

public class UpdateInfoBean {

    private Integer versionCode;
    private String versionName;
    private String downloadUrl;

    //是否强制更新
    @SerializedName("status")
    private int forceUpdate;

    //是否检测更新 1是
    @SerializedName("checkUpdate")
    private int checkUpdate;

    public boolean isForceUpdate() {
        return forceUpdate == 1;
    }

    public boolean ignoreUpdate() {
        return checkUpdate == 0;
    }

    public Integer getVersionCode() {
        return versionCode;
    }


    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(int forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public int getCheckUpdate() {
        return checkUpdate;
    }

    public void setCheckUpdate(int checkUpdate) {
        this.checkUpdate = checkUpdate;
    }
}

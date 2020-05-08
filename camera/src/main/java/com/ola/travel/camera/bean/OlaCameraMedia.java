package com.ola.travel.camera.bean;

import android.os.Parcel;
import android.os.Parcelable;


public class OlaCameraMedia implements Parcelable {
    /**
     * file to ID
     */
    private long id;
    /**
     * original path
     */
    private String path;

    /**
     * Note: this field is only returned in Android Q version
     * <p>
     * Android Q image or video path
     */
    private String androidQToPath;
    /**
     * /**
     * file name
     */
    private String fileName;

    public OlaCameraMedia() {
    }

    public void cleanData() {
        fileName = "";
        androidQToPath = "";
    }

    public OlaCameraMedia(String path, String androidQToPath, String fileName) {
        this.path = path;
        this.androidQToPath = androidQToPath;
        this.fileName = fileName;
    }

    public OlaCameraMedia(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAndroidQToPath() {
        return androidQToPath;
    }

    public void setAndroidQToPath(String androidQToPath) {
        this.androidQToPath = androidQToPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.path);
        dest.writeString(this.androidQToPath);
        dest.writeString(this.fileName);
    }

    protected OlaCameraMedia(Parcel in) {
        this.id = in.readLong();
        this.path = in.readString();
        this.androidQToPath = in.readString();
        this.fileName = in.readString();
    }

    public static final Creator<OlaCameraMedia> CREATOR = new Creator<OlaCameraMedia>() {
        @Override
        public OlaCameraMedia createFromParcel(Parcel source) {
            return new OlaCameraMedia(source);
        }

        @Override
        public OlaCameraMedia[] newArray(int size) {
            return new OlaCameraMedia[size];
        }
    };
}

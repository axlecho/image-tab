package com.axlecho.tabgallery;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ImageTabInfo implements Parcelable {


    public long gid;
    public String[] imgs;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.gid);
        dest.writeStringArray(imgs);
    }

    public ImageTabInfo() {
    }

    protected ImageTabInfo(Parcel in) {
        this.gid = in.readLong();
        this.imgs = in.createStringArray();
//        in.readStringArray(this.imgs);
    }

    public static final Creator<ImageTabInfo> CREATOR = new Creator<ImageTabInfo>() {

        @Override
        public ImageTabInfo createFromParcel(Parcel source) {
            return new ImageTabInfo(source);
        }

        @Override
        public ImageTabInfo[] newArray(int size) {
            return new ImageTabInfo[size];
        }
    };

}

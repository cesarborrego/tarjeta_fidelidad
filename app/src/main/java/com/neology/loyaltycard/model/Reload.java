package com.neology.loyaltycard.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 26/01/16.
 */
public class Reload implements Parcelable {

    public static final Creator<Reload> CREATOR = new Creator<Reload>() {
        @Override
        public Reload createFromParcel(Parcel in) {
            return new Reload(in);
        }

        @Override
        public Reload[] newArray(int size) {
            return new Reload[size];
        }
    };
    private String reload;

    public Reload(String reload) {
        this.reload = reload;
    }

    protected Reload(Parcel in) {
        reload = in.readString();
    }

    public String getReload() {
        return reload;
    }

    public void setReload(String reload) {
        this.reload = reload;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reload);
    }
}

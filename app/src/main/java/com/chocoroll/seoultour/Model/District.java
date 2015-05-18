package com.chocoroll.seoultour.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by RA on 2015-05-13.
 */
public class District implements Parcelable {
    private String name;
    private   double mapx, mapy;
    private  String code;

    public District(Parcel in) {
        readFromParcel(in);
    }

    public District(String code, String name, double mapx, double mapy){
        this.code = code;
        this.name = name;
        this.mapx = mapx;
        this.mapy = mapy;
    }

    public String getName() {
        return name;
    }

    public double getMapy() {
        return mapy;
    }

    public double getMapx() {
        return mapx;
    }

    public String getCode() {
        return code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(name);
        parcel.writeDouble(mapx);
        parcel.writeDouble(mapy);
        parcel.writeString(code);

    }

    private void readFromParcel(Parcel in){
        name = in.readString();
        mapx=in.readDouble();
        mapy = in.readDouble();
        code = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public District createFromParcel(Parcel in) {
            return new District(in);
        }

        public District[] newArray(int size) {
            return new District[size];
        }
    };

}

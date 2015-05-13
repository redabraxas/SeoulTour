package com.chocoroll.seoultour.Model;

/**
 * Created by RA on 2015-05-13.
 */
public class Tour {
    private  String name;
    private   String thumbnail;
    private   double mapx, mapy;
    private   String contentID;

    public double getMapx() {
        return mapx;
    }

    public double getMapy() {
        return mapy;
    }

    public String getContentID() {
        return contentID;
    }

    public String getName() {
        return name;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}

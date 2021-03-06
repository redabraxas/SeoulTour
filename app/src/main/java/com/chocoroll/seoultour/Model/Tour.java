package com.chocoroll.seoultour.Model;

/**
 * Created by RA on 2015-05-13.
 */
public class Tour {
    private String name;
    private String thumbnail;
    private double mapx, mapy;
    private String contentID;
    private int contentTypeID;
    private String overView;
    private String addr;
    private String tel;

    public Tour(String name, String thumbnail, double mapx, double mapy, String contentID, int contentTypeID, String addr, String tel){
        this.name = name;
        this.thumbnail =thumbnail;
        this.mapx = mapx;
        this.mapy = mapy;
        this.contentID = contentID;
        this.contentTypeID = contentTypeID;
        this.addr =addr;
        this.tel = tel;

    }
    public void setOverView(String overview){
        this.overView =overview;
    }

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

    public String getOverView() {
        return overView;
    }

    public String getAddr() {
        return addr;
    }

    public int getContentTypeID() {
        return contentTypeID;
    }

    public String getTel() {
        return tel;
    }
}

package com.chocoroll.seoultour.Model;

/**
 * Created by RA on 2015-05-13.
 */
public class District {
    private String name;
    private   double mapx, mapy;
    private  String code;

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
}

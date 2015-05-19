package com.chocoroll.seoultour.Model;

/**
 * Created by Administrator on 2015-05-19.
 */
public class Item {
    private String  name;
    private String  code;
    private int     cnt;


    public Item(String code, String name, int cnt){
        this.code = code;
        this.name = name;
        this.cnt = cnt;
    }

    public String getName() {
        return name;
    }

    public int getCnt() {
        return cnt;
    }

    public String getCode() {
        return code;
    }
}

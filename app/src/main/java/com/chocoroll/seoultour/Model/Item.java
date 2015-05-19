package com.chocoroll.seoultour.Model;

/**
 * Created by Administrator on 2015-05-19.
 */
public class Item {
    private String  name;
    private int  code;
    private int  cnt;


    public Item(int code, String name, int cnt){
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

    public int getCode() {
        return code;
    }
}

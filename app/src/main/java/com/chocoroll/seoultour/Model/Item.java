package com.chocoroll.seoultour.Model;

/**
 * Created by Administrator on 2015-05-19.
 */
public class Item {
    private String  name;
    private int  code;
    private int  cnt;
    private int  total;


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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCode() {
        return code;
    }
}

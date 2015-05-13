package com.chocoroll.seoultour.Model;

/**
 * Created by RA on 2015-05-13.
 */
public class GuestBook {

    private String name;
    private String date;
    private String content;

    public GuestBook(String name, String date, String content){
        this.name = name;
        this.date = date;
        this.content = content;
    }


    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }
}

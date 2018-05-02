package com.example.li.bean;

/**
 * Created by li on 2018/4/22.
 * 用于数据库操作时的Note bean
 */

public class BNote {
    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public void setId(long id) {

        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private long id;
    private String content;
    private String date;
}

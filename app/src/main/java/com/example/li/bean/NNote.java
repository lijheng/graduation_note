package com.example.li.bean;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by li on 2018/4/4.
 */

public class NNote implements Comparable<NNote>{

    private long id;
    private String title;
    private String image;
    private String voice;
    private String text;
    private String time;

    public void setId(long id){
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getId(){
        return id;
    }

    public String getTitle() {

        return title;
    }

    public String getImage() {
        return image;
    }

    public String getVoice() {
        return voice;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    @Override
    public int compareTo(@NonNull NNote o) {
        if (this.time.compareToIgnoreCase(o.time)<0){
            return 1;
        }
        return -1;
    }
}

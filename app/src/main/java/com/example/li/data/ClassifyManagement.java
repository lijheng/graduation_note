package com.example.li.data;

/**
 * Created by li on 2018/4/17.
 * 用户标记分类标题和item
 */

public class ClassifyManagement {

    private int type;
    private Object object;

    public ClassifyManagement(int type,Object object){
        this.type  = type;
        this.object = object;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int getType() {

        return type;
    }

    public Object getObject() {
        return object;
    }
}

package com.frank.simpleframework.beans;

/**
 * bean type
 * Created by Administrator on 2018/2/14.
 */
public enum BeanType {

    BEAN_TYPE_CONTROLLER(0,"controller"),
    BEAN_TYPE_SERVICE(1,"service"),
    BEAN_TYPE_FILTER(2,"filter"),
    BEAN_TYPE_REPOSITORY(3,"Repository"),
    BEAN_TYPE_OTHER(4,"other");

    private int index;

    private String name;

    private BeanType(int index,String name){
        this.index = index;
        this.name = name;
    }
}

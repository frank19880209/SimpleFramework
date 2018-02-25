package com.frank.simpleframework.beans;

/**
 * Created by Administrator on 2018/2/14.
 */
public final class DefineBean {

    private BeanType beanType;

    private Class clazz;

    private String name;

    private Object instance;

    public DefineBean(BeanType beanType, Class clazz, String name, Object instance) {
        this.beanType = beanType;
        this.clazz = clazz;
        this.name = name;
        this.instance = instance;
    }

    public BeanType getBeanType() {
        return beanType;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getInstance() {
        return instance;
    }

}

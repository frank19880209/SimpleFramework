package com.frank.simpleframework.request;


/**
 * Created by Frank （wx:F451209123） on 2017/12/17.
 */
public class MethodParameter implements Comparable {
    private String name;
    private Object value;
    private Integer index;

    public MethodParameter(String name, Object value, Integer index) {
        this.name = name;
        this.value = value;
        this.index = index;
    }

    @Override
    public int compareTo(Object o) {
        if(o != null){
            return this.index - ((MethodParameter)o).index;
        }
        return 0;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public Integer getIndex() {
        return index;
    }
}

package com.frank.simpleframework.response;

/**
 * Created by Frank （wx:F451209123） on 2017/12/16.
 */
public enum ResponseCode {
    RESPONSE_CODE_0000("0000","请求成功"),
    RESPONSE_CODE_1000("1000","请求异常"),
    RESPONSE_CODE_1001("1001","请求不支持该方法"),
    RESPONSE_CODE_1002("1002","请求访问的路径不存在"),
    RESPONSE_CODE_1003("1003","过滤器终止请求执行");
    private String code;
    private String desc;
    private  ResponseCode(String code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

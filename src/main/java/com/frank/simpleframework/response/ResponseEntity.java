package com.frank.simpleframework.response;

/**
 * Created by Frank （wx:F451209123） on 2017/12/16.
 */
public class ResponseEntity {

    private String responseCode;
    private String msg;
    private Object data;

    public ResponseEntity(String responseCode, String msg, Object data) {
        this.responseCode = responseCode;
        this.msg = msg;
        this.data = data;
    }
}

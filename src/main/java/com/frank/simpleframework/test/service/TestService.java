package com.frank.simpleframework.test.service;

import com.frank.simpleframework.annotation.Service;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Frank （wx:F451209123） on 2017/12/17.
 */
@Service
public class TestService {

    public boolean checkMsg(String msg){
        return StringUtils.isNotBlank(msg);
    }
}

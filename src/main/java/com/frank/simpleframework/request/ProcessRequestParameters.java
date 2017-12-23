package com.frank.simpleframework.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Frank （wx:F451209123） on 2017/12/17.
 */
public interface ProcessRequestParameters {

    public List<MethodParameter> processParameter(Method method, HttpServletRequest request, HttpServletResponse response) throws Exception;
}

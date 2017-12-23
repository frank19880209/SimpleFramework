package com.frank.simpleframework.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Frank （wx:F451209123） on 2017/12/23.
 */
public abstract class AbstractInterceptor {

    /**
     * 拦截执行方法
     * @param request 请求对象
     * @param response 响应对象
     * @return 返回true 则通过校验拦截 否则校验不通过
     */
    public abstract boolean doInterceptor(HttpServletRequest request, HttpServletResponse response) throws Exception;

}

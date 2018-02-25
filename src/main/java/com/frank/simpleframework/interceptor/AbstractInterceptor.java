package com.frank.simpleframework.interceptor;

import com.frank.simpleframework.request.RequestContext;

/**
 * Created by Frank （wx:F451209123） on 2017/12/23.
 */
public abstract class AbstractInterceptor {

    /**
     * 拦截执行方法
     * @param requestContext 请求上下文
     * @return 返回true 则通过校验拦截 否则校验不通过
     */
    public abstract boolean doInterceptor(RequestContext requestContext) throws Exception;

}

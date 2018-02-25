package com.frank.simpleframework.test.interceptor;

import com.frank.simpleframework.interceptor.AbstractInterceptor;
import com.frank.simpleframework.request.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Frank （wx:F451209123） on 2017/12/23.
 */
public class TestInterceptor extends AbstractInterceptor {

    /**
     * 拦截执行方法
     *
     * @param requestContext   请求上下文
     * @return 返回true 则通过校验拦截 否则校验不通过
     */
    @Override
    public boolean doInterceptor(RequestContext requestContext) throws Exception {
        return false;
    }
}

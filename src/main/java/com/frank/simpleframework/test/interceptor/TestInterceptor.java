package com.frank.simpleframework.test.interceptor;

import com.frank.simpleframework.interceptor.AbstractInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Frank （wx:F451209123） on 2017/12/23.
 */
public class TestInterceptor extends AbstractInterceptor {

    /**
     * 拦截执行方法
     *
     * @param request   请求对象
     * @param response  响应对象
     * @return 返回true 则通过校验拦截 否则校验不通过
     */
    @Override
    public boolean doInterceptor(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return false;
    }
}

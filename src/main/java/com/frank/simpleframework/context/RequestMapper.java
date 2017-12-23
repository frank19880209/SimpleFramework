package com.frank.simpleframework.context;

import com.frank.simpleframework.interceptor.AbstractInterceptor;
import com.frank.simpleframework.request.RequestMethod;

import java.lang.reflect.Method;

/**
 * Created by Frank （wx:F451209123） on 2017/12/10.
 */
public class RequestMapper {
    //存放对应的实例
    private Object object;
    //对应的执行方法
    private Method method;
    //请求支持的方法
    private RequestMethod suportMethod;
    //前置拦截器
    private AbstractInterceptor beforeInterceptor;
    //后置拦截器
    private AbstractInterceptor afterInterceptor;
    //对应的path路径
    private String path;

    public RequestMapper(Object object, Method method,RequestMethod suportMethod, String path,AbstractInterceptor beforeInterceptor,AbstractInterceptor afterInterceptor) {
        this.object = object;
        this.method = method;
        this.suportMethod = suportMethod;
        this.path = path;
        this.beforeInterceptor = beforeInterceptor;
        this.afterInterceptor = afterInterceptor;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public RequestMethod getSuportMethod() {
        return suportMethod;
    }

    public void setSuportMethod(RequestMethod suportMethod) {
        this.suportMethod = suportMethod;
    }

    public AbstractInterceptor getBeforeInterceptor() {
        return beforeInterceptor;
    }

    public void setBeforeInterceptor(AbstractInterceptor beforeInterceptor) {
        this.beforeInterceptor = beforeInterceptor;
    }

    public AbstractInterceptor getAfterInterceptor() {
        return afterInterceptor;
    }

    public void setAfterInterceptor(AbstractInterceptor afterInterceptor) {
        this.afterInterceptor = afterInterceptor;
    }
}

package com.frank.simpleframework.beans;

import com.frank.simpleframework.annotation.Interceptor;
import com.frank.simpleframework.annotation.RequestAction;
import com.frank.simpleframework.annotation.RequestController;
import com.frank.simpleframework.interceptor.AbstractInterceptor;
import com.frank.simpleframework.request.RequestMethod;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2018/2/14.
 */
public class Controller$$Proxy {

    private Class controllerClazz;

    private Object instance;

    private Method method;

    private RequestMethod requestMethod;

    private String consumes;

    private String produces;

    private String url;

    private AbstractInterceptor beforeInterceptor;

    private AbstractInterceptor afterInterceptor;

    public Controller$$Proxy(Class controllerClazz, Object instance, Method method) throws InstantiationException, IllegalAccessException {
        this.controllerClazz = controllerClazz;
        this.instance = instance;
        this.method = method;
        this.init();
    }

    public void init() throws IllegalAccessException, InstantiationException {
        RequestController requestController = (RequestController) controllerClazz.getAnnotation(RequestController.class);
        String path = requestController.path();
        if(!path.endsWith("/")){
            path = path +"/";
        }
        RequestAction action = this.method.getAnnotation(RequestAction.class);
        this.requestMethod = action.method();
        this.consumes = action.consumes();
        this.produces = action.produces();
        String value = action.value();
        if (StringUtils.isBlank(value)) {
            value = this.method.getName();
        }
        if (value.startsWith("/")) {
            value = value.replaceFirst("/","");
        }
        this.url = path + value;
        Interceptor interceptor = this.method.getAnnotation(Interceptor.class);
        if (null != interceptor) {
            Class<? extends AbstractInterceptor> abstractInterceptor = interceptor.beforeInterceptor();
            if(null != abstractInterceptor){
                this.beforeInterceptor = abstractInterceptor.newInstance();
            }
            abstractInterceptor = interceptor.afterInterceptor();
            if(null != abstractInterceptor){
                this.afterInterceptor = abstractInterceptor.newInstance();
            }
        }
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getConsumes() {
        return consumes;
    }

    public void setConsumes(String consumes) {
        this.consumes = consumes;
    }

    public String getProduces() {
        return produces;
    }

    public void setProduces(String produces) {
        this.produces = produces;
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

    public Class getControllerClazz() {
        return controllerClazz;
    }

    public void setControllerClazz(Class controllerClazz) {
        this.controllerClazz = controllerClazz;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }
}

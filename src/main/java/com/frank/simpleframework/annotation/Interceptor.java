package com.frank.simpleframework.annotation;

import com.frank.simpleframework.interceptor.AbstractInterceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Frank （wx:F451209123） on 2017/12/23.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Interceptor {
    /**
     * 前置对应的处理类，处理类必须是非抽象类且是{@link com.frank.simpleframework.interceptor.AbstractInterceptor}的子类
     * @return
     */
    Class<? extends AbstractInterceptor> beforeInterceptor();

    /**
     * 后置对应的处理类，处理类必须是非抽象类且是{@link com.frank.simpleframework.interceptor.AbstractInterceptor}的子类
     * @return
     */
    Class<? extends AbstractInterceptor> afterInterceptor();
}

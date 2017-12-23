package com.frank.simpleframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Frank （wx:F451209123） on 2017/12/23.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeInterceptor {
    /**
     * 对应的处理类，处理类必须是非抽象类且是{@link com.frank.simpleframework.interceptor.AbstractInterceptor}的子类
     * @return
     */
    Class interceptor();
}

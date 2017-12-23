package com.frank.simpleframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by frank on 2017/12/10.
 * WeChat：F451209123
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestController {
    /**
     * 访问路径
     * @return
     */
    String path() default "/";

}

package com.frank.simpleframework.annotation;

import com.frank.simpleframework.request.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by frank on 2017/12/10.
 * WeChat：F451209123
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestAction {

    String value() default "";

    RequestMethod method() default RequestMethod.GET;

    String consumes() default "application/json";

    String produces() default "application/json";
}

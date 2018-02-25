package com.frank.simpleframework.beans;

import java.util.List;

/**
 * Created by Administrator on 2018/2/14.
 */
public interface BeanFactory {

    Object getBean(String beanName);

    List getBeansByType(BeanType beanType);
}

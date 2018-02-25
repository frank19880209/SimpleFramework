package com.frank.simpleframework.context;

import com.frank.simpleframework.annotation.Filter;
import com.frank.simpleframework.annotation.RequestAction;
import com.frank.simpleframework.beans.AnnotationBeanFactory;
import com.frank.simpleframework.beans.BeanType;
import com.frank.simpleframework.beans.Controller$$Proxy;
import com.frank.simpleframework.beans.DefineBean;
import com.frank.simpleframework.filter.AbstractFilter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletContext;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/2/14.
 */
public final class ApplicationContext extends AnnotationBeanFactory{


    private ServletContext servletContext;

    private Map<String,Controller$$Proxy> requestMapping;

    private List<AbstractFilter> filters = new ArrayList<>();

    public ApplicationContext(ServletContext servletContext){
        this.servletContext = servletContext;
        init();
    }

    public List<AbstractFilter> getFilters(){
        return filters;
    }

    private void init(){
        try {
            initController();
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.exit(1);
        }
        initFilter();
    }

    private void initController(){
        List<DefineBean> defineBeanList = getBeansByType(BeanType.BEAN_TYPE_CONTROLLER);
        defineBeanList.forEach(defineBean -> {
            Class clazz = defineBean.getClazz();
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                RequestAction action = method.getAnnotation(RequestAction.class);
                boolean isPublic = Modifier.isPublic(method.getModifiers());
                if(null != action){
                    if(isPublic){
                        Controller$$Proxy controller = null;
                        try {
                            controller = new Controller$$Proxy(clazz,defineBean.getInstance(),method);
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                            System.exit(-1);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            System.exit(-1);
                        }
                        if(requestMapping.containsKey(controller.getUrl())){
                            throw new RuntimeException(String.format("Request Mapping URL [%s] can not be duplicate",controller.getUrl()));
                        }else{
                            requestMapping.put(controller.getUrl(),controller);
                        }

                    }else{
                        throw new RuntimeException(String.format("%s#%s must be public",clazz.getName(),method.getName()));
                    }
                }
            }
        });

    }

    private void initFilter(){
        List<DefineBean> defineBeanList = getBeansByType(BeanType.BEAN_TYPE_FILTER);
        defineBeanList.forEach(defineBean -> {
            Class clazz = defineBean.getClazz();
            if(clazz.getSuperclass() == AbstractFilter.class){
                Filter filter = (Filter) clazz.getAnnotation(Filter.class);
                if(null != filter){
                    AbstractFilter abstractFilter = (AbstractFilter) defineBean.getInstance();
                    abstractFilter.setName(StringUtils.isBlank(filter.name())?clazz.getSimpleName():filter.name());
                    abstractFilter.setOrder(filter.order());
                    filters.add(abstractFilter);
                }
            }
        });
        Collections.sort(filters);
    }

    public String getProperty(String key){
        return properties.get(key);
    }

    public Controller$$Proxy getController(String url){
        return this.requestMapping.get(url);
    }
}

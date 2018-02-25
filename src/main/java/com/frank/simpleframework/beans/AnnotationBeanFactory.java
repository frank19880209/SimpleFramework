package com.frank.simpleframework.beans;

import com.frank.simpleframework.annotation.*;
import com.frank.simpleframework.util.ClassUtils;
import com.frank.simpleframework.util.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Administrator on 2018/2/14.
 */
public class AnnotationBeanFactory implements BeanFactory {

    protected static Map<String, String> properties = new HashMap<>();

    //用于存放所有的class集合
    private static Set<Class<?>> classSet;

    private static Map<Class,DefineBean> defineBeanMap = new HashMap<>();

    private static final String basePackage = "simple.framework.base.package";

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationBeanFactory.class);

    static {
        loadProperties();
        if(properties.containsKey(basePackage)){
            loadClasses();
        }else{
            LOGGER.error(String.format("can not find %s in properties file",basePackage));
            System.exit(-1);
        }
    }

    private static void loadProperties(){
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        LOGGER.info(String.format("scan properties file path==>", path));
        File file = new File(path);
        if (file.isDirectory()) {
            String[] fileNames = file.list(new FilenameFilter() {
                /**
                 * Tests if a specified file should be included in a file list.
                 *
                 * @param dir  the directory in which the file was found.
                 * @param name the name of the file.
                 * @return <code>true</code> if and only if the name should be
                 * included in the file list; <code>false</code> otherwise.
                 */
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".properties");
                }
            });
            if (null == fileNames || fileNames.length == 0) {
                LOGGER.error("properties file not found");
                System.exit(1);
            }else{
                LOGGER.info("scan properties files :", StringUtils.join(fileNames,","));
                properties = PropertiesUtils.loadProps(fileNames);
            }
        }
    }

    private static void loadClasses() {
        classSet = ClassUtils.getClasses(properties.get(basePackage),true);
        for(Class clazz:classSet){
            if(ClassUtils.hasClassAnnotation(clazz,RequestController.class)){
                initBeans(clazz,BeanType.BEAN_TYPE_CONTROLLER);
            }else if(ClassUtils.hasClassAnnotation(clazz,Service.class)){
                initBeans(clazz,BeanType.BEAN_TYPE_SERVICE);
            } else if(ClassUtils.hasClassAnnotation(clazz,Repository.class)){
                initBeans(clazz,BeanType.BEAN_TYPE_REPOSITORY);
            } else if(ClassUtils.hasClassAnnotation(clazz,Filter.class)){
                initBeans(clazz,BeanType.BEAN_TYPE_FILTER);
            } else {
                initBeans(clazz,BeanType.BEAN_TYPE_OTHER);
            }
        }
        try {
            refresh();
        }catch (IllegalAccessException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage(),e);
            System.exit(-1);
        }
    }

    private static void initBeans(Class clazz,BeanType beanType){
        try {
            Object instance = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            setField(fields, instance);
            defineBeanMap.put(clazz,new DefineBean(beanType,clazz,clazz.getName(),instance));
        } catch (InstantiationException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage(),e);
            System.exit(-1);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage(),e);
            System.exit(-1);
        }
    }

    private static void setField(Field[] fields,Object instance) throws IllegalAccessException {
        for (Field f:fields){
            if(f.isAnnotationPresent(Inject.class)){
                Class cla = f.getType();
                DefineBean defineBean = defineBeanMap.get(cla);
                if(null != defineBean){
                    Object claObj = defineBean.getInstance();
                    if(claObj != null){
                        f.setAccessible(true);
                        f.set(instance,claObj);
                    }
                }
            }
        }
    }

    private static void refresh() throws IllegalAccessException {
        for (Class key:defineBeanMap.keySet()){
            DefineBean defineBean = defineBeanMap.get(key);
            if(null != defineBean){
                Object instance = defineBean.getInstance();
                Field[] fields = key.getDeclaredFields();
                setField(fields,instance);
            }
        }
    }


    @Override
    public Object getBean(String beanName) {
        try {
            Class clazz = Class.forName(beanName);
            return defineBeanMap.get(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage(),e);
        }
        return null;
    }

    @Override
    public List<DefineBean> getBeansByType(BeanType beanType) {
        List<DefineBean> defineBeanList = new ArrayList<>();
        defineBeanMap.forEach((k,v) ->{
            if(beanType == v.getBeanType()){
                defineBeanList.add(v);
            }
        });
        return defineBeanList;
    }
}

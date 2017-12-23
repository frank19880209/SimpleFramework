package com.frank.simpleframework.context;

import com.frank.simpleframework.annotation.*;
import com.frank.simpleframework.interceptor.AbstractInterceptor;
import com.frank.simpleframework.util.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by Frank （wx:F451209123） on 2017/12/10.
 */
public class FrameworkContext implements FrameworkFactory{

    private static final Logger logger = LoggerFactory.getLogger(FrameworkContext.class);

    public static String base_package_path="";

    private ServletConfig servletConfig;

    private ServletContext servletContext;

    //用于存放所有的class集合
    private static Set<Class<?>> classSet;

    //用于存放controller bean
    private static Map<String,Object> ioc_controller = new HashMap<>();


    //用于存放service bean
    private static Map<String,Object> ioc_service = new HashMap<>();
    //用于存放dao bean
    private static Map<String,Object> ioc_dao = new HashMap<>();

    //用于存放all bean
    private static Map<String,Object> ioc_all = new HashMap<>();

    private Object object = new Object();

    //url请求的映射集合
    private Map<String,RequestMapper> requestMappers = new HashMap<>();

    public void init(ServletConfig servletConfig) {
        if(servletConfig == null){
            logger.error("servletConfig is null");
            System.exit(0);
        }
        this.servletContext = servletConfig.getServletContext();
        this.servletConfig = servletConfig;
        try {
            this.loadAllBeans();
        } catch (InstantiationException |IllegalAccessException e) {
            e.printStackTrace();
            logger.error("系统初始化bean失败",e);
            System.exit(0);
        }
        this.initRequestMapping();
    }

    private void loadAllBeans() throws IllegalAccessException, InstantiationException {
        if(classSet == null){
            synchronized (object){
                if(classSet == null){
                    classSet = ClassUtils.getClasses(base_package_path,true);
                }
            }
        }
        for(Class clazz:classSet){
            RequestController controller = (RequestController) clazz.getAnnotation(RequestController.class);
            Service service = (Service) clazz.getAnnotation(Service.class);
            Repository repository = (Repository) clazz.getAnnotation(Repository.class);
            if(controller != null || service != null || repository != null){
                if((controller != null && service != null && repository != null)||(controller != null && service != null) || (service != null && repository != null) ||(controller != null && repository != null)){
                    logger.error(clazz.getName()+" uses both  RequestController annotation and Service annotation,Only one of them can be used");
                    System.exit(0);
                }
                if(clazz.isInterface()){
                    logger.error("Specified class["+clazz.getName()+"] is an interface");
                    System.exit(0);
                }
                Object instance = clazz.newInstance();
                Field[] fields = clazz.getDeclaredFields();
                this.setField(fields,instance);
                ioc_all.put(clazz.getName(),instance);
                if(controller != null){
                    ioc_controller.put(clazz.getName(),instance);
                }else if(service != null){
                    ioc_service.put(clazz.getName(),instance);
                } else {
                    ioc_dao.put(clazz.getName(),instance);
                }
            }
        }
    }

    private void initRequestMapping(){
        for(String key:ioc_controller.keySet()){
            Object instance = ioc_controller.get(key);
            if(instance == null){
                throw new RuntimeException();
            }
            Class clazz = instance.getClass();
            RequestController controller = (RequestController) clazz.getAnnotation(RequestController.class);
            String controllerPath = controller.path();
            if(!controllerPath.endsWith("/")){
                controllerPath = controllerPath + "/";
            }
            Method[] methods = clazz.getMethods();
            for (Method method:methods){
                if(method.isAnnotationPresent(RequestAction.class)){
                    RequestAction requestAction = method.getAnnotation(RequestAction.class);
                    String serviceName = requestAction.serviceName();
                    if(StringUtils.isBlank(serviceName)){
                        serviceName = method.getName();
                    }else{
                        if(serviceName.startsWith("/")){
                            serviceName.replaceFirst("/","");
                        }
                    }
                    String totalPath = controllerPath+serviceName;
                    if(requestMappers.containsKey(totalPath)){
                        logger.error(String.format("Controller[%s] path[%s] already exists one,please check request mapper",clazz.getName(),totalPath));
                        System.exit(0);
                    }else{
                        AbstractInterceptor before = null;
                        AbstractInterceptor after = null;
                        if(method.isAnnotationPresent(BeforeInterceptor.class)){
                            BeforeInterceptor beforeInterceptor = method.getAnnotation(BeforeInterceptor.class);
                            Class beforeClazz = beforeInterceptor.interceptor();
                            if(beforeClazz.isInterface()){
                                logger.error(String.format("class:%s is a interface not a concrete class",clazz.getName()));
                                throw new RuntimeException(String.format("class:%s is a interface not a concrete class",clazz.getName()));
                            }
                            boolean isAbs = Modifier.isAbstract(beforeClazz.getModifiers()) ;
                            if(AbstractInterceptor.class != beforeClazz.getSuperclass() || isAbs){
                                logger.error(String.format("class:%s is not %s type or concrete class",clazz.getName(),AbstractInterceptor.class.getName()));
                                throw new RuntimeException(String.format("class:%s is not %s type",clazz.getName(),AbstractInterceptor.class.getName()));
                            }
                            try {
                                before =(AbstractInterceptor) beforeClazz.newInstance();
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                                System.exit(0);
                            }
                        }
                        if(method.isAnnotationPresent(AfterInterceptor.class)){
                            AfterInterceptor afterInterceptor = method.getAnnotation(AfterInterceptor.class);
                            Class afterClazz = afterInterceptor.interceptor();
                            if(afterClazz.isInterface()){
                                logger.error(String.format("class:%s is a interface not a concrete class",clazz.getName()));
                                throw new RuntimeException(String.format("class:%s is a interface not a concrete class",clazz.getName()));
                            }
                            boolean isAbs = Modifier.isAbstract(afterClazz.getModifiers()) ;
                            if(AbstractInterceptor.class != afterClazz.getSuperclass() || isAbs){
                                logger.error(String.format("class:%s is not %s type or concrete class",clazz.getName(),AbstractInterceptor.class.getName()));
                                throw new RuntimeException(String.format("class:%s is not %s type",clazz.getName(),AbstractInterceptor.class.getName()));
                            }
                            try {
                                after =(AbstractInterceptor) afterClazz.newInstance();
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                                System.exit(0);
                            }
                        }
                        requestMappers.put(totalPath,new RequestMapper(instance,method,requestAction.method(),totalPath,before,after));
                    }

                }
            }
        }
    }

    public void refresh() throws IllegalAccessException {
        for (String key:ioc_all.keySet()){
            Object instance = ioc_all.get(key);
            Class clazz = instance.getClass();
            Field[] fields = clazz.getDeclaredFields();
            this.setField(fields,instance);
            if(ioc_controller.containsKey(key)){
                ioc_controller.put(key,instance);
            }
            if(ioc_service.containsKey(key)){
                ioc_service.put(key,instance);
            }
            if(ioc_dao.containsKey(key)){
                ioc_dao.put(key,instance);
            }
        }
    }


    private void setField(Field[] fields,Object instance) throws IllegalAccessException {
        for (Field f:fields){
            if(f.isAnnotationPresent(Inject.class)){
                Class cla = f.getType();
                Object claObj = ioc_all.get(cla.getName());
                if(claObj != null){
                    f.setAccessible(true);
                    f.set(instance,claObj);
                }
            }
        }
    }

    public static Object getBean(Class<?> clazz){
        return clazz == null?null:ioc_all.get(clazz.getName());
    }

    public static void registerBean(Object object){
        if(object != null){
            String key = object.getClass().getName();
            if(!ioc_all.containsKey(key)){
                ioc_all.put(key,object);
            }
        }

    }

    public RequestMapper getRequestMapper(String path){
        return requestMappers.get(path);
    }

}

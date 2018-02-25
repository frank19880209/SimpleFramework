package com.frank.simpleframework.request;

import com.alibaba.fastjson.JSON;
import com.frank.simpleframework.annotation.RequestParameter;
import com.frank.simpleframework.beans.Controller$$Proxy;
import com.frank.simpleframework.context.ApplicationContext;
import com.frank.simpleframework.util.WebUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Created by Administrator on 2018/2/25.
 */
public class RequestContext {

    private static final Logger logger = LoggerFactory.getLogger(RequestContext.class);

    /**
     * 基本类型、包装类型、String类型
     */
    private static final List<String> types = Arrays.asList("java.lang.Integer",
            "java.lang.Double",
            "java.lang.Float",
            "java.lang.Long",
            "java.lang.Short",
            "java.lang.Byte",
            "java.lang.Boolean",
            "java.lang.Character",
            "java.lang.String",
            "int", "double", "long", "short", "byte", "boolean", "char", "float");

    private ApplicationContext applicationContext;

    private List<MethodParameter> methodParameterList;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private Controller$$Proxy controller$$Proxy;

    public RequestContext(ApplicationContext applicationContext, HttpServletRequest request, HttpServletResponse response,Controller$$Proxy controller$$Proxy) throws IOException {
        this.applicationContext = applicationContext;
        this.request = request;
        this.response = response;
        this.controller$$Proxy = controller$$Proxy;
        processParameter();
    }


    private void processParameter() throws IOException {
        Map parameterMap = new HashMap();
        Enumeration<String> attributes = request.getAttributeNames();
        while (attributes.hasMoreElements()){
            String aName = attributes.nextElement();
            parameterMap.put(aName,request.getAttribute(aName));
        }

        this.getParameter(parameterMap, WebUtils.getString(request.getInputStream()));
        this.getParameter(parameterMap,request.getQueryString());
        Parameter[] parameters = controller$$Proxy.getMethod().getParameters();
        methodParameterList = new ArrayList<>();
        for(int i=0;i<parameters.length;i++){
            Parameter parameter = parameters[i];
            RequestParameter requestParameter = parameter.getAnnotation(RequestParameter.class);
            String name = parameter.getName();
            Integer index = i;
            if(null != requestParameter){
                name = requestParameter.name();
            }
            Object value = null;
            Class<?> type = parameter.getType();
            if (types.contains(type.getName())) {
                value = parameterMap.get(name);
            } else if ("java.util.Date".equalsIgnoreCase(type.getName())) {
                String val = (String) parameterMap.get(name);
                if (StringUtils.isNotBlank(val)) {
                    Date date = WebUtils.parseDate(val);
                    if(date == null){
                        logger.error(String.format("%s格式化失败",val));
                        throw new RuntimeException(String.format("参数%s的值%s解析失败",name,val));
                    }else{
                        value = date;
                    }
                }
            } else if(request.getClass().getName().equalsIgnoreCase(type.getName())){
                value = request;
            } else if(response.getClass().getName().equalsIgnoreCase(type.getName())){
                value = response;
            } else {
                String jsonStr = JSON.toJSONString(parameterMap);
                value = JSON.parseObject(jsonStr,type);
            }
            if(requestParameter.isRequired() && value == null){
                logger.error(String.format("%s参数值缺失",name));
                throw new RuntimeException(String.format("%s参数值缺失",name));
            }
            methodParameterList.add(new MethodParameter(name,value,index));
        }
        Collections.sort(methodParameterList);
    }

    private void getParameter(Map<String,Object> map,String queryStr){
        if(StringUtils.isNotBlank(queryStr)){
            String[] parameters = queryStr.split("&");
            if(ArrayUtils.isNotEmpty(parameters)){
                for (int i = 0; i < parameters.length; i++) {
                    String p = parameters[i];
                    if(StringUtils.isNotBlank(p)){
                        map.put(p.split("=")[0],p.split("=")[1]);
                    }
                }
            }
        }
    }

    public List<MethodParameter> getMethodParameterList() {
        return methodParameterList;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public Controller$$Proxy getController$$Proxy() {
        return controller$$Proxy;
    }
}

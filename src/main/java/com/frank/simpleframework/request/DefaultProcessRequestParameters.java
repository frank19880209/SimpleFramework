package com.frank.simpleframework.request;

import com.alibaba.fastjson.JSON;
import com.frank.simpleframework.annotation.RequestParameter;
import com.frank.simpleframework.util.WebUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Created by Frank （wx:F451209123） on 2017/12/17.
 */
public class DefaultProcessRequestParameters implements ProcessRequestParameters {

    private static final Logger logger = LoggerFactory.getLogger(DefaultProcessRequestParameters.class);

    /**
     * 基本类型、包装类型、String类型
     */
    private final List<String> types = Arrays.asList("java.lang.Integer",
            "java.lang.Double",
            "java.lang.Float",
            "java.lang.Long",
            "java.lang.Short",
            "java.lang.Byte",
            "java.lang.Boolean",
            "java.lang.Character",
            "java.lang.String",
            "int", "double", "long", "short", "byte", "boolean", "char", "float");

    @Override
    public List<MethodParameter> processParameter(Method method, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map complexMap = new HashMap();
        Enumeration<String> attributes = request.getAttributeNames();
        while (attributes.hasMoreElements()){
            String aName = attributes.nextElement();
            complexMap.put(aName,request.getAttribute(aName));
        }
        this.getParameter(complexMap,WebUtils.getString(request.getInputStream()));
        this.getParameter(complexMap,request.getQueryString());
        Parameter[] parameters = method.getParameters();
        List<MethodParameter> methodParameters = new ArrayList<>();
        for(int i=0;i<parameters.length;i++){
            Parameter parameter = parameters[i];
            RequestParameter requestParameter = parameter.getAnnotation(RequestParameter.class);
            String name = parameter.getName();
            Integer index = i;
            if(null != requestParameter){
                name = requestParameter.name();
                index = requestParameter.index();
            }
            Object value = null;
            Class<?> type = parameter.getType();
            if (types.contains(type.getName())) {
                value = complexMap.get(name);
            } else if ("java.util.Date".equalsIgnoreCase(type.getName())) {
                String val = (String) complexMap.get(name);
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
                String jsonStr = JSON.toJSONString(complexMap);
                value = JSON.parseObject(jsonStr,type);
            }
            if(requestParameter.isRequired() && value == null){
                logger.error(String.format("%s参数值缺失",name));
                throw new RuntimeException(String.format("%s参数值缺失",name));
            }
            methodParameters.add(new MethodParameter(name,value,index));
        }
        Collections.sort(methodParameters);
        return methodParameters;
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

}

package com.frank.simpleframework.servlet;

import com.alibaba.fastjson.JSON;
import com.frank.simpleframework.beans.Controller$$Proxy;
import com.frank.simpleframework.context.ApplicationContext;
import com.frank.simpleframework.filter.AbstractFilter;
import com.frank.simpleframework.interceptor.AbstractInterceptor;
import com.frank.simpleframework.request.MethodParameter;
import com.frank.simpleframework.request.RequestContext;
import com.frank.simpleframework.response.ResponseCode;
import com.frank.simpleframework.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by frank on 2017/12/10.
 */
public class SimpleDispatcherServlet extends HttpServlet {

    private static ApplicationContext applicationContext;

    private static final Logger logger = LoggerFactory.getLogger(SimpleDispatcherServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            req.setCharacterEncoding("UTF-8");
            String uri = req.getRequestURI();
            logger.info(uri);
            if (uri.endsWith(".html") || uri.endsWith(".htm") || uri.equals("/")) {
                resp.setStatus(200);
                return;
            }
            String path = uri;
            if (uri.indexOf("?") != -1) {
                path = uri.substring(0, uri.indexOf("?"));
            }
            Controller$$Proxy controller$$Proxy = applicationContext.getController(path);
            if (null == controller$$Proxy) {
                this.outPut(resp, new HashMap() {{
                    put("code", ResponseCode.RESPONSE_CODE_1002);
                    put("msg", "未找到要访问的服务路径，请检查一下请求路径");
                }});
                return;
            }

            String supportMethod = controller$$Proxy.getRequestMethod().name();
            if (supportMethod.equalsIgnoreCase(req.getMethod())) {
                RequestContext requestContext = new RequestContext(applicationContext,req,resp,controller$$Proxy);
                List<AbstractFilter> filters = applicationContext.getFilters();
                int flag = 0;
                for (AbstractFilter filter : filters) {
                    if(!filter.doFilter(requestContext)){
                        flag = 1;
                        break;
                    }
                }
                if(flag == 0){
                    try {
                        Method method = controller$$Proxy.getMethod();
                        AbstractInterceptor before = controller$$Proxy.getBeforeInterceptor();
                        AbstractInterceptor after = controller$$Proxy.getAfterInterceptor();
                        if(null != before){
                            Method beforeMethod = before.getClass().getDeclaredMethod("doInterceptor",RequestContext.class);
                            boolean ifContinue = (boolean) beforeMethod.invoke(before,requestContext);
                            if(!ifContinue){
                                this.outPut(resp, new ResponseEntity(ResponseCode.RESPONSE_CODE_1000.getCode(), String.format("执行%s.doInterceptor方法未通过",before.getClass().getName()), null));
                                return;
                            }
                        }
                        Class<?> returnType = method.getReturnType();
                        List<MethodParameter> parameterList = requestContext.getMethodParameterList();
                        Object[] values = new Object[parameterList.size()];
                        for (int i = 0; i < parameterList.size(); i++) {
                            values[i] = parameterList.get(i).getValue();
                        }
                        if ("void".equalsIgnoreCase(returnType.getName())) {
                            method.invoke(controller$$Proxy.getInstance(), values);
                        } else {
                            Object returnVal = method.invoke(controller$$Proxy.getInstance(), values);
                            this.outPut(resp, returnVal);
                        }
                        if(null != after){
                            Method afterMethod = after.getClass().getDeclaredMethod("doInterceptor",RequestContext.class);
                            afterMethod.invoke(after,requestContext);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("执行方法异常", e);
                        this.outPut(resp, new ResponseEntity(ResponseCode.RESPONSE_CODE_1001.getCode(), e.getMessage(), null));
                        return;
                    }
                }else{
                    this.outPut(resp, new ResponseEntity(ResponseCode.RESPONSE_CODE_1003.getCode(), ResponseCode.RESPONSE_CODE_1003.getDesc(), null));
                    return;
                }

            } else {
                this.outPut(resp, new ResponseEntity(ResponseCode.RESPONSE_CODE_1001.getCode(), String.format("暂不支持%s请求方法", req.getMethod()), null));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> result = new HashMap<>();
            result.put("code", ResponseCode.RESPONSE_CODE_1000.getCode());
            result.put("msg", e.getMessage());
            this.outPut(resp, result);
        }

    }

    @Override
    public void destroy() {
        super.destroy();
    }


    @Override
    public final void init() throws ServletException {
        String startImg = " ######  #### ##     ## ########  ##       ########    ######## ########     ###    ##     ## ######## ##      ##  #######  ########  ##    ## \n" +
                "##    ##  ##  ###   ### ##     ## ##       ##          ##       ##     ##   ## ##   ###   ### ##       ##  ##  ## ##     ## ##     ## ##   ##  \n" +
                "##        ##  #### #### ##     ## ##       ##          ##       ##     ##  ##   ##  #### #### ##       ##  ##  ## ##     ## ##     ## ##  ##   \n" +
                " ######   ##  ## ### ## ########  ##       ######      ######   ########  ##     ## ## ### ## ######   ##  ##  ## ##     ## ########  #####    \n" +
                "      ##  ##  ##     ## ##        ##       ##          ##       ##   ##   ######### ##     ## ##       ##  ##  ## ##     ## ##   ##   ##  ##   \n" +
                "##    ##  ##  ##     ## ##        ##       ##          ##       ##    ##  ##     ## ##     ## ##       ##  ##  ## ##     ## ##    ##  ##   ##  \n" +
                " ######  #### ##     ## ##        ######## ########    ##       ##     ## ##     ## ##     ## ########  ###  ###   #######  ##     ## ##    ## ";
        logger.info(startImg);
        logger.info("-----SimpleFramework starts initialization ----");
        applicationContext = new ApplicationContext(this.getServletContext());
        logger.info("-----SimpleFramework finishes initialization ----");
    }

    public void outPut(HttpServletResponse response, Object result) {
        try {
            String contentType = "";
            String resultData = "";
            if (result instanceof String) {
                resultData = (String) result;
                contentType = "text/html";
            } else {
                contentType = "application/json";
                resultData = JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
            }
            OutputStream outputStream = response.getOutputStream();
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-type", contentType + ";charset=UTF-8");
            byte[] dataByteArr = resultData.getBytes("UTF-8");
            outputStream.write(dataByteArr);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("输出异常", e.getMessage());
        }
    }
}

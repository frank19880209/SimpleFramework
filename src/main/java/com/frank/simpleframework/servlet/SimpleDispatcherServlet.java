package com.frank.simpleframework.servlet;

import com.alibaba.fastjson.JSON;
import com.frank.simpleframework.context.FrameworkContext;
import com.frank.simpleframework.context.RequestMapper;
import com.frank.simpleframework.interceptor.AbstractInterceptor;
import com.frank.simpleframework.request.DefaultProcessRequestParameters;
import com.frank.simpleframework.request.MethodParameter;
import com.frank.simpleframework.request.ProcessRequestParameters;
import com.frank.simpleframework.response.ResponseCode;
import com.frank.simpleframework.response.ResponseEntity;
import com.frank.simpleframework.util.PropertiesUtils;
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
import java.util.Properties;

/**
 * Created by frank on 2017/12/10.
 */
public class SimpleDispatcherServlet extends HttpServlet {

    public static final String Config_bash_package = "base_package";

    private static FrameworkContext frameworkContext;

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
            RequestMapper requestMapper = frameworkContext.getRequestMapper(path);
            if (null == requestMapper) {
                this.outPut(resp, new HashMap() {{
                    put("code", "1020");
                    put("msg", "未找到要访问的服务路径，请检查一下请求路径");
                }});
                return;
            }
            String supportMethod = requestMapper.getSuportMethod().toString();
            if (supportMethod.equalsIgnoreCase(req.getMethod())) {
                ProcessRequestParameters processRequestParameters = new DefaultProcessRequestParameters();
                try {
                    Method method = requestMapper.getMethod();
                    AbstractInterceptor before = requestMapper.getBeforeInterceptor();
                    AbstractInterceptor after = requestMapper.getAfterInterceptor();
                    if(null != before){
                        Method beforeMethod = before.getClass().getDeclaredMethod("doInterceptor",HttpServletRequest.class,HttpServletResponse.class);
                        boolean ifContinue = (boolean) beforeMethod.invoke(before,req,resp);
                        if(!ifContinue){
                            this.outPut(resp, new ResponseEntity(ResponseCode.RESPONSE_CODE_1000.getCode(), String.format("执行%s.doInterceptor方法未通过",before.getClass().getName()), null));
                            return;
                        }
                    }
                    Class<?> returnType = method.getReturnType();
                    List<MethodParameter> parameterList = processRequestParameters.processParameter(method, req,resp);
                    Object[] values = new Object[parameterList.size()];
                    for (int i = 0; i < parameterList.size(); i++) {
                        values[i] = parameterList.get(i).getValue();
                    }
                    if ("void".equalsIgnoreCase(returnType.getName())) {
                        method.invoke(requestMapper.getObject(), values);
                    } else {
                        Object returnVal = method.invoke(requestMapper.getObject(), values);
                        this.outPut(resp, returnVal);
                    }
                    if(null != after){
                        Method afterMethod = after.getClass().getDeclaredMethod("doInterceptor",HttpServletRequest.class,HttpServletResponse.class);
                        afterMethod.invoke(after,req,resp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("执行方法异常", e);
                    this.outPut(resp, new ResponseEntity(ResponseCode.RESPONSE_CODE_1001.getCode(), e.getMessage(), null));
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
        } finally {

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
        System.out.println(startImg);
        logger.info("-----SimpleFramework starts initialization ----");
        logger.info("start loading simple.properties file");
        Properties properties = PropertiesUtils.loadProps("/simple.properties");
        if (properties == null) {
            logger.error("loading simple.properties fail,please check this properties file exists,System will shutdown");
            System.exit(-1);
        }
        if (!properties.containsKey(Config_bash_package)) {
            logger.error(String.format("simple.properties can't contain %s property ,System will shutdown", Config_bash_package));
            System.exit(-1);
        }
        FrameworkContext.base_package_path = properties.getProperty(Config_bash_package);
        frameworkContext = new FrameworkContext();
        frameworkContext.init(this.getServletConfig());
        try {
            frameworkContext.refresh();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            logger.error("frameworkContext refresh exception", e);
            System.exit(0);
        }
        FrameworkContext.registerBean(frameworkContext);
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

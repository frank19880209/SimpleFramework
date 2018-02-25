package com.frank.simpleframework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Frank （wx:F451209123） on 2017/12/10.
 */
public final class PropertiesUtils {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

    private PropertiesUtils(){}

    /**
     * 加载属性文件
     *
     * @param propertiesFiles 文件路径
     * @return
     */
    public synchronized static Map<String,String> loadProps(String ...propertiesFiles) {
        Map<String,String> properties = new HashMap<>();
        if(null != propertiesFiles){
            for (String propertiesFile : propertiesFiles) {
                Properties prop = new Properties();
                try {
                    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFile);
                    prop.load(is);
                    Enumeration names = prop.propertyNames();
                    while (names.hasMoreElements()){
                        String key=(String) names.nextElement();
                        String property=prop.getProperty(key);
                        properties.put(key, property);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                    logger.error(String.format("获取配置文件[%s]异常",propertiesFile),e);
                }
            }
        }
        return properties;
    }
}

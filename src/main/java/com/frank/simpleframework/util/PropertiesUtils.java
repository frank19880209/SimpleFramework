package com.frank.simpleframework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Frank （wx:F451209123） on 2017/12/10.
 */
public final class PropertiesUtils {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

    private PropertiesUtils(){}

    private static Properties properties;

    /**
     * 加载属性文件
     *
     * @param filePath 文件路径
     * @return
     */
    public synchronized static Properties loadProps(String filePath) {
        if(properties == null){
            properties = new Properties();
        }
        try {
            String path = Thread.currentThread().getContextClassLoader().getResource(filePath).getPath();
            InputStream in = new BufferedInputStream(new FileInputStream(path));
            properties.load(in);
        } catch (Exception e) {
            properties = null;
            logger.error("load props error",e);
        }
        return properties;
    }

    /**
     * 读取配置文件
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        return properties.getProperty(key);
    }

    /**
     * 更新配置文件
     *
     * @param keyname 配置属性
     * @param keyvalue
     * @return
     */
    public static void updateProperty(String keyname, String keyvalue) {
        properties.remove(keyname);
        properties.put(keyname,keyvalue);
    }
}

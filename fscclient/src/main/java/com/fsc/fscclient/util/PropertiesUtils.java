package com.fsc.fscclient.util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PropertiesUtils {
    public static PropertiesConfiguration cfg = null;
    static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

    static {
        try {
            String path = getPath() + "config.properties";
            cfg = new PropertiesConfiguration(path);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        cfg.setReloadingStrategy(new FileChangedReloadingStrategy());
        cfg.setAutoSave(true);
    }

    public static Map<String, String> getMap() {
        Iterator<String> it = cfg.getKeys();
        Map<String, String> map = new HashMap<String, String>();
        while (it.hasNext()) {
            String key = it.next();
            map.put(key, getStringValue(key));
        }
        return map;
    }

    public static void setProperties(String keys, String values) throws ConfigurationException, IOException {
        cfg.setProperty(keys, values);
    }

    public static void delProperties(String keys) throws ConfigurationException, IOException {
        cfg.clearProperty(keys);
    }

    public static String getStringValue(String key) {
        return cfg.getString(key);
    }

    public static int getIntValue(String key) {
        return cfg.getInt(key);
    }

    public static boolean getBooleanValue(String key) {
        return cfg.getBoolean(key);
    }

    public static List<?> getListValue(String key) {
        return cfg.getList(key);
    }

    public static String[] getArrayValue(String key) {
        return cfg.getStringArray(key);
    }

    public static String getPath() {
        String path = PropertiesUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.substring(path.indexOf("/"), path.length());
        if (path.contains("jar")) {
            path = path.substring(0, path.lastIndexOf("."));
            return path.substring(0, path.lastIndexOf("/")) + "/";
        }
        return path;
    }

}

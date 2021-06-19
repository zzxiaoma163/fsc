package com.fsc.fscmonitor.controller;

import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class HttpRoute extends ClassLoader {
    private Logger log = Logger.getLogger(HttpRoute.class);
    private static Map<String, Action> httpRouterAction = new HashMap<>();

    public void addRouter(String controllerClass) {
        try {
            Class<?> cls = loadClass(controllerClass);
            Method[] methods = cls.getDeclaredMethods();
            for (Method invokeMethod : methods) {
                Annotation[] annotations = invokeMethod.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType() == RequestMapping.class) {
                        RequestMapping requestMapping = (RequestMapping) annotation;
                        String uri = requestMapping.uri();
                        Action action = new Action(cls.newInstance(), invokeMethod);
                        httpRouterAction.put(uri, action);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static Action getRoute(String uri) {
        return httpRouterAction.get(uri);
    }
}

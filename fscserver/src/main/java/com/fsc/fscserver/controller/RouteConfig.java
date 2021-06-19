package com.fsc.fscserver.controller;

/**
 * 加载controller
 */
public class RouteConfig {
    public static void initConfig() {
        HttpRoute httpRoute = new HttpRoute();
        httpRoute.addRouter(HttpController.class.getName());
    }
}

package com.wang.registry;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wanglibin
 * @version 1.0
 */
public class LocalRegistry {
    /**
     * 注册信息存储
     */
    private static final Map<String, Class<?>> map = new HashMap<>();

    /**
     * 注册服务
     * @param serviceName
     * @param implClass
     */
    public static void register(String serviceName, Class<?> implClass) {
        map.put(serviceName, implClass);
    }

    /**
     * 获取服务实现类
     * @param serviceName
     * @return
     */
    public static Class<?> get(String serviceName) {
        return map.get(serviceName);
    }

    /**
     * 删除服务
     * @param serviceName
     */
    public static void remove(String serviceName) {
        map.remove(serviceName);
    }
}

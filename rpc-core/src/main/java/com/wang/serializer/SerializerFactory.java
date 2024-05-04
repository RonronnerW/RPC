package com.wang.serializer;

import com.wang.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化器工厂（工厂模式，用于获取序列化器对象）
 */
public class SerializerFactory {

    //    /**
//     * 序列化器工厂（用于获取序列化器对象）
//     */
//    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<>(){{
//        put(SerializerKeys.JDK, new JdkSerializer());
//        put(SerializerKeys.JSON, new JsonSerializer());
//        put(SerializerKeys.HESSIAN, new HessianSerializer());
//        put(SerializerKeys.KRYO, new KryoSerializer());
//    }};
    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
//        return KEY_SERIALIZER_MAP.getOrDefault(key, DEFAULT_SERIALIZER);
        return SpiLoader.getInstance(Serializer.class, key);
    }

}
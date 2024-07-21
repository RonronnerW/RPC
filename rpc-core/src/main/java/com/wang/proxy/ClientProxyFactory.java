package com.wang.proxy;

import com.wang.RpcApplication;

import java.lang.reflect.Proxy;

/**
 * @author wanglibin
 * @version 1.0
 * 动态代理工厂 - 根据指定类创建动态代理对象
 */
public class ClientProxyFactory {

    /**
     * 根据服务类获取代理对象
     * @param serviceClass
     * @return
     * @param <T>
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        if(RpcApplication.getRpcConfig().isMock()) {
            return getMockProxy(serviceClass);
        }
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ClientProxy());
    }

    /**
     * mock代理对象
     * @param serviceClass
     * @return
     * @param <T>
     */
    public static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockClientProxy());
    }
}

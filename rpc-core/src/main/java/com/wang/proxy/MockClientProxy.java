package com.wang.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author wanglibin
 * @version 1.0
 */
@Slf4j
public class MockClientProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return getDefaultObject(returnType);
    }

    /**
     * 生成指定类型的默认值对象
     * @param type
     * @return
     */
    private Object getDefaultObject(Class<?> type) {
        // 基本类型
        if(type.isPrimitive()) {
            if(type == boolean.class) {
                return false;
            } else if(type==char.class) {
              return "";
            } else if(type==byte.class) {
                return (byte) 0;
            } else if(type==short.class) {
                return (short) 0;
            } else if(type==int.class) {
                return 0;
            } else if(type==long.class) {
                return 0L;
            } else if(type==float.class) {
                return 0.0f;
            } else if(type==double.class) {
                return 0.0;
            }
        }
        // 对象类型
        return null;
    }
}

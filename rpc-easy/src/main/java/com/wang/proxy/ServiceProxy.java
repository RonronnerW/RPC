package com.wang.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.wang.model.RpcRequest;
import com.wang.model.RpcResponse;
import com.wang.serializer.JdkSerializer;
import com.wang.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author wanglibin
 * @version 1.0
 * 动态代理
 */
public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        Serializer serializer = new JdkSerializer();

        // 发请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // 序列化
            byte[] serialize = serializer.serialize(rpcRequest);
            // 发送请求
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(serialize)
                    .execute()) {
                byte[] result = httpResponse.bodyBytes();
                // 反序列化
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

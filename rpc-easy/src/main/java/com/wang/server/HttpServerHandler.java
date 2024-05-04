package com.wang.server;

import com.wang.model.RpcRequest;
import com.wang.model.RpcResponse;
import com.wang.registry.LocalRegistry;
import com.wang.serializer.JdkSerializer;
import com.wang.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author wanglibin
 * @version 1.0
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest httpServerRequest) {
        // 指定序列化器
        final Serializer serializer = new JdkSerializer();

        // 记录日志
        System.out.println("Received request: "+ httpServerRequest.method()+" "+httpServerRequest.uri());

        // 异步处理 HTTP 请求
        httpServerRequest.bodyHandler(body -> {
            // 1. 反序列化
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            if(rpcRequest==null) {
                rpcResponse.setMessage("RpcRequest is null");
                doResponse(httpServerRequest, rpcResponse, serializer);
                return;
            }

            try {
                // 获取需要调用服务的实现类，通过反射实现
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDateType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            doResponse(httpServerRequest, rpcResponse, serializer);

        });
    }

    /**
     * 响应
     * @param request
     * @param rpcResponse
     * @param serializer
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        byte[] serialize = new byte[0];
        try {
            serialize = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialize));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }

    }
}

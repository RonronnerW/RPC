package com.wang.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.wang.RpcApplication;
import com.wang.config.RpcConfig;
import com.wang.constant.RpcConstant;
import com.wang.model.RpcRequest;
import com.wang.model.RpcResponse;
import com.wang.model.ServiceMetaInfo;
import com.wang.protocol.*;
import com.wang.registry.Registry;
import com.wang.registry.RegistryFactory;
import com.wang.serializer.JdkSerializer;
import com.wang.serializer.Serializer;
import com.wang.serializer.SerializerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author wanglibin
 * @version 1.0
 * 动态代理
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        String serviceName = method.getDeclaringClass().getName();
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

            // 从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            // 1. 根据配置中心注册中心类型获取注册中心对象
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            // 2. 获取键名
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            // 3. 服务发现
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }
            // 4. 暂时先获取第一个注册中心地址
            ServiceMetaInfo metaInfo = serviceMetaInfoList.get(0);
/**
 * #########################################################
 * 使用http发送请求
//            // 发送请求
//            try (HttpResponse httpResponse = HttpRequest.post(metaInfo.getServiceAddress())
//                    .body(serialize)
//                    .execute()) {
//                byte[] result = httpResponse.bodyBytes();
//                // 反序列化
//                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
//                return rpcResponse.getData();
//            }

 * ##########################################################
 */

/**
 * 发送TCP请求
 */
            Vertx vertx = Vertx.vertx();
            NetClient netClient = vertx.createNetClient();
            CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
            netClient.connect(metaInfo.getServicePort(), metaInfo.getServiceHost(), res -> {
                if (res.succeeded()) {
                    log.info("Connected to TCP Server!");
                    NetSocket socket = res.result();
                    // 发送数据 构造消息
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = protocolMessage.getHeader();
                    header.setMagic(ProtocolConstant.MAGIC);
                    header.setVersion(ProtocolConstant.VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    header.setId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);
                    // 编码
                    try {
                        Buffer buffer = ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(buffer);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // 接受响应
                    socket.handler(buffer -> {
                        try {
                            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage =(ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                            // 完成响应
                            responseFuture.complete(rpcResponseProtocolMessage.getBody());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else {
                    log.info("Failed to connect: " + res.cause().getMessage());
                }
            });
            // 异步阻塞，知道请求完成才会继续执行
            RpcResponse rpcResponse = responseFuture.get();
            // 关闭连接
            netClient.close();
            return rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

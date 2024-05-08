package com.wang.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.wang.RpcApplication;
import com.wang.config.RpcConfig;
import com.wang.constant.RpcConstant;
import com.wang.loadbalancer.LoadBalancer;
import com.wang.loadbalancer.LoadBalancerFactory;
import com.wang.model.RpcRequest;
import com.wang.model.RpcResponse;
import com.wang.model.ServiceMetaInfo;
import com.wang.protocol.*;
import com.wang.registry.Registry;
import com.wang.registry.RegistryFactory;
import com.wang.retry.RetryStrategy;
import com.wang.retry.RetryStrategyFactory;
import com.wang.serializer.JdkSerializer;
import com.wang.serializer.Serializer;
import com.wang.serializer.SerializerFactory;
import com.wang.server.tcp.VertxTcpClient;
import com.wang.tolerant.TolerantStrategy;
import com.wang.tolerant.TolerantStrategyFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
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

        String serviceName = method.getDeclaringClass().getName();
        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
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
        // 4. 负载均衡获取一个注册中心地址
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(RpcApplication.getRpcConfig().getLoadBalancer());
        // 使用方法名（请求路径）作为请求参数，使用一致性hash时，调用相同方法请求参数相同，所以总会请求到同一个服务器
        HashMap<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        ServiceMetaInfo selected = loadBalancer.select(requestParams, serviceMetaInfoList);

        // 使用重试机制发送TCP请求
        RpcResponse rpcResponse = null;
        try {
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(RpcApplication.getRpcConfig().getRetryStrategy());
            rpcResponse = retryStrategy.doRetry(() -> VertxTcpClient.doRequest(rpcRequest, selected));
        } catch (Exception e) {
            // 容错机制
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(RpcApplication.getRpcConfig().getTolerantStrategy());
            Map<String, Object> context = new HashMap<>();
            context.put("rpcResponse", rpcResponse);
            tolerantStrategy.doTolerant(context, e);
        }
        return Objects.requireNonNull(rpcResponse).getData();
    }
}

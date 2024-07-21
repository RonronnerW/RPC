package com.wang.proxy;

import cn.hutool.core.collection.CollUtil;
import com.wang.RpcApplication;
import com.wang.client.NettyClient;
import com.wang.client.handler.ClientHandler;
import com.wang.config.RegistryConfig;
import com.wang.config.RpcConfig;
import com.wang.constant.RpcConstant;
import com.wang.loadbalancer.LoadBalancer;
import com.wang.loadbalancer.LoadBalancerFactory;
import com.wang.model.Message;
import com.wang.model.RpcRequest;
import com.wang.model.RpcResponse;
import com.wang.model.ServiceMetaInfo;
import com.wang.registry.Registry;
import com.wang.registry.RegistryFactory;
import com.wang.retry.RetryStrategy;
import com.wang.retry.RetryStrategyFactory;
import com.wang.server.handler.ServerHandler;
import com.wang.tolerant.TolerantStrategy;
import com.wang.tolerant.TolerantStrategyFactory;
import com.wang.utils.SequenceIdUtil;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author wanglibin
 * @version 1.0
 */
public class ClientProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 1. 构建请求消息
        RpcRequest rpcRequest = new RpcRequest();
        int sequenceId = SequenceIdUtil.getSequenceId();
        String serviceName = method.getDeclaringClass().getName();
        rpcRequest.setServiceName(serviceName);
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setArgs(args);
        rpcRequest.setSequenceId(sequenceId);
        rpcRequest.setMessageType(Message.RPC_REQUEST);

        // 从注册中心获取服务提供者请求地址
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());

        if (CollUtil.isEmpty(serviceMetaInfos)) {
            throw new RuntimeException("暂无服务地址");
        }
        // 负载均衡调用服务
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        HashMap<String, Object> param = new HashMap<>();
        param.put("serviceName", serviceName);
        ServiceMetaInfo metaInfo = loadBalancer.select(param, serviceMetaInfos);

        // rpc 请求
        RpcResponse rpcResponse = null;
        try {
            // 使用重试策略
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategyConfig().getRetryStrategy());
            rpcResponse = retryStrategy.doRetry(new Callable<RpcResponse>() {
                @Override
                public RpcResponse call() throws Exception {
                    // 发送请求消息 返回响应
                    return NettyClient.sendMessage(rpcRequest, metaInfo);
                }
            });
        } catch (Exception e) {
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            tolerantStrategy.doTolerant(null, e);
        }
        return rpcResponse != null ? rpcResponse.getData() : null;

    }
}

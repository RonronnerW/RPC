package com.wang;

import cn.hutool.core.collection.CollUtil;
import com.wang.config.RegistryConfig;
import com.wang.config.RpcConfig;
import com.wang.constant.RpcConstant;
import com.wang.model.ServiceMetaInfo;
import com.wang.registry.LocalRegistry;
import com.wang.registry.Registry;
import com.wang.registry.RegistryFactory;
import com.wang.server.VertHttpServer;
import com.wang.service.UserService;
import com.wang.service.UserServiceImpl;

import java.util.List;

/**
 * @author wanglibin
 * @version 1.0
 */
public class ProviderExample {
    public static void main(String[] args) {
        // 初始化
        RpcApplication.init();
        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // 注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 启动web服务器
        VertHttpServer vertHttpServer = new VertHttpServer();
        vertHttpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}

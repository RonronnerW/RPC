package com.wang.bootstrap;

import com.wang.RpcApplication;
import com.wang.config.RegistryConfig;
import com.wang.config.RpcConfig;
import com.wang.model.ServiceMetaInfo;
import com.wang.model.ServiceRegisterInfo;
import com.wang.registry.LocalRegistry;
import com.wang.registry.Registry;
import com.wang.registry.RegistryFactory;
import com.wang.server.NettyServer;
import io.vertx.grpc.VertxServer;

import java.util.List;

/**
 * 服务提供者初始化
 */
public class ProviderBootstrap {
    /**
     * 初始化
     * @param serviceRegisterInfos
     */
    public static void init(List<ServiceRegisterInfo> serviceRegisterInfos) {
        // 初始化（配置中心和注册中心）
        RpcApplication.init();
        // 全局配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        // 注册服务
        for (ServiceRegisterInfo serviceRegisterInfo : serviceRegisterInfos) {
            String serviceName = serviceRegisterInfo.getServiceName();
            // 注册到本地
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());
            // 注册到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(rpcConfig.getVersion());
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // 启动server
        NettyServer server = new NettyServer();
        server.doStart(rpcConfig.getServerPort());
    }
}

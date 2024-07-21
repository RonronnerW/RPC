package com.wang;

import com.wang.bootstrap.ProviderBootstrap;
import com.wang.config.RegistryConfig;
import com.wang.config.RpcConfig;
import com.wang.model.ServiceMetaInfo;
import com.wang.model.ServiceRegisterInfo;
import com.wang.registry.EtcdRegistry;
import com.wang.registry.LocalRegistry;
import com.wang.registry.Registry;
import com.wang.registry.RegistryFactory;
import com.wang.server.NettyServer;
import com.wang.service.UserService;
import com.wang.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wanglibin
 * @version 1.0
 */
public class ProviderExample {
    public static void main(String[] args) {
//        // 全局配置加载
//        RpcApplication.init();
//
//        String serviceName = UserService.class.getName();
//        // 注册到本地注册器
//        LocalRegistry.register(serviceName, UserServiceImpl.class);
//
//        // 注册到注册中心
//        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
//        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
//        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
//        serviceMetaInfo.setServiceName(serviceName);
//        serviceMetaInfo.setServiceVersion(rpcConfig.getVersion());
//        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
//        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
//        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
//        try {
//            registry.register(serviceMetaInfo);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        // 启动server
//        NettyServer server = new NettyServer();
//        server.doStart(rpcConfig.getServerPort());


        // 要注册的服务
        ArrayList<ServiceRegisterInfo> serviceMetaInfos = new ArrayList<>();
        ServiceRegisterInfo registerInfo = new ServiceRegisterInfo<>();
        registerInfo.setServiceName(UserService.class.getName());
        registerInfo.setImplClass(UserServiceImpl.class);
        serviceMetaInfos.add(registerInfo);

        // 服务提供者初始化
        ProviderBootstrap.init(serviceMetaInfos);

    }
}

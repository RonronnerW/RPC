package com.wang;

import com.wang.bootstrap.ProviderBootstrap;
import com.wang.config.RegistryConfig;
import com.wang.config.RpcConfig;
import com.wang.model.ServiceMetaInfo;
import com.wang.model.ServiceRegisterInfo;
import com.wang.registry.LocalRegistry;
import com.wang.registry.Registry;
import com.wang.registry.RegistryFactory;
import com.wang.server.VertxHttpServer;
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
        // 要注册的服务
        List<ServiceRegisterInfo> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserService.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        // 服务提供者初始化
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}

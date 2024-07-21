package com.wang.rpcspringbootstarter.bootstrap;

import com.wang.RpcApplication;
import com.wang.rpcspringbootstarter.annotation.EnableRpc;
import com.wang.config.RpcConfig;
import com.wang.server.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * RPC启动
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    /**
     * Spring 初始化时执行，初始化 RPC 框架
     *
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 框架初始化
        RpcApplication.init();

        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 启动server
        NettyServer server = new NettyServer();
        server.doStart(rpcConfig.getServerPort());

    }
}
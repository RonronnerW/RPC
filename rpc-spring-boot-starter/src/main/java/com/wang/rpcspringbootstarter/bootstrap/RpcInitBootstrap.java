package com.wang.rpcspringbootstarter.bootstrap;

import com.wang.RpcApplication;
import com.wang.rpcspringbootstarter.annotation.EnableRpc;
import com.wang.config.RpcConfig;
import com.wang.server.tcp.VertxTcpServer;
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
        // 获取 EnableRpc 注解的属性值
        Boolean needServer = (Boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName()).get("needServer");

        // 框架初始化
        RpcApplication.init();

        // 全局配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        // 启动服务器
        if(needServer) {
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
        } else {
            log.info("不启动服务器");
        }

    }
}
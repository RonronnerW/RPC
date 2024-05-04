package com.wang;

import com.wang.config.RegistryConfig;
import com.wang.config.RpcConfig;
import com.wang.constant.RpcConstant;
import com.wang.registry.Registry;
import com.wang.registry.RegistryFactory;
import com.wang.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wanglibin
 * @version 1.0
 * 存放了项目全局变量， 使用双检锁单例模式
 */
@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    /**
     * 初始化，支持传入自定义参数
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());

        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig.toString());
    }

    /**
     * 初始化 - 使用双检锁单例模式
     */
    public static void init() {
        RpcConfig newRpcConfig = null;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // 配置加载失败，使用默认
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);

    }

    /**
     * 获取配置
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if(rpcConfig==null) {
            synchronized (RpcApplication.class) {
                if(rpcConfig==null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }

}

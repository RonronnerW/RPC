package com.wang.config;

import com.wang.serializer.SerializerKeys;
import lombok.Data;

/**
 * @author wanglibin
 * @version 1.0
 * rpc 配置
 */
@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name = "rpc";
    /**
     * 版本
     */
    private String version = "1.0";
    /**
     * 服务器主机
     */
    private String serverHost = "localhost";
    /**
     * 服务器端口
     */
    private Integer serverPort = 8888;
    /**
     * 模拟调用
     */
    private boolean mock = false;
    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();
}

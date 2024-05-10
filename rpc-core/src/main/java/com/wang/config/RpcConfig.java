package com.wang.config;

import com.wang.loadbalancer.LoadBalancerKeys;
import com.wang.retry.RetryStrategyKeys;
import com.wang.serializer.SerializerKeys;
import com.wang.tolerant.TolerantStrategy;
import com.wang.tolerant.TolerantStrategyKeys;
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

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.FIXED_TIME_RETRY;

    /**
     * 容错策略
     */
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;
}

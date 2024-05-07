package com.wang.retry;

import com.wang.registry.EtcdRegistry;
import com.wang.registry.Registry;
import com.wang.spi.SpiLoader;

/**
 * @author wanglibin
 * @version 1.0
 * 注册中心工厂
 */
public class RetryStrategyFactory {
    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 默认注册中心
     */
    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

    /**
     * 获取实例
     * @param key
     * @return
     */
    public static RetryStrategy getInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }
}

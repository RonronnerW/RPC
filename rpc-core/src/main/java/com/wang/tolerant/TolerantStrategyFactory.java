package com.wang.tolerant;

import com.wang.retry.NoRetryStrategy;
import com.wang.retry.RetryStrategy;
import com.wang.spi.SpiLoader;

/**
 * @author wanglibin
 * @version 1.0
 * 容错策略工厂
 */
public class TolerantStrategyFactory {
    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 默认注册中心
     */
    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailSafeTolerantStrategy();

    /**
     * 获取实例
     * @param key
     * @return
     */
    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }
}

package com.wang.retry;

/**
 * 重试策略
 */
public interface RetryStrategyKeys {
    /**
     * 不重试
     */
    String NO = "no";
    /**
     * 固定时间间隔
     */
    String FIXED_TIME_RETRY = "fixedTimeRetry";

    /**
     * 指数退避
     */
    String EXPONENTIAL_TIME_RETRY = "exponentialTime";

}

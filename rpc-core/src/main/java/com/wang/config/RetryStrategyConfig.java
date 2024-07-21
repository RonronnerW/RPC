package com.wang.config;

import com.wang.registry.RegistryKeys;
import com.wang.retry.RetryStrategyKeys;
import lombok.Data;

@Data
public class RetryStrategyConfig {

    private String retryStrategy = RetryStrategyKeys.FIXED_TIME_RETRY;

    private long fixedTime = 3L;

    private int retryTimes = 3;
}
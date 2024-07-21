package com.wang.retry;

import com.github.rholder.retry.*;
import com.wang.RpcApplication;
import com.wang.config.RetryStrategyConfig;
import com.wang.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间重试策略
 */
@Slf4j
public class FixedTimeRetryStrategy implements RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        RetryStrategyConfig config = RpcApplication.getRpcConfig().getRetryStrategyConfig();
        long fixedTime = config.getFixedTime();
        int retryTimes = config.getRetryTimes();
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)// 发生异常重试
                .withWaitStrategy(WaitStrategies.fixedWait(fixedTime, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(retryTimes))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数 {}", attempt.getAttemptNumber()) ;
                    }
                })
                .build();
        return retryer.call(callable);
    }
}

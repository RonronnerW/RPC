package com.wang.retry;

import com.github.rholder.retry.*;
import com.wang.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 指数等待时间重试策略
 */
@Slf4j
public class ExponentialTimeRetryStrategy implements RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)// 发生异常重试
                .withWaitStrategy(WaitStrategies.exponentialWait())
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数 {}, 等待时间 {}", attempt.getAttemptNumber(), attempt.getDelaySinceFirstAttempt()) ;
                    }
                })
                .build();
        return retryer.call(callable);
    }
}

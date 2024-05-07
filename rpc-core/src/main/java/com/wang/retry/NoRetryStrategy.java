package com.wang.retry;

import com.wang.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 不重试策略
 * 直接执行一次策略
 */
public class NoRetryStrategy implements RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}

package com.wang.tolerant;

import com.github.rholder.retry.*;
import com.wang.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 快速失败容错策略
 */
@Slf4j
public class FailFastTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务出错 FailFastTolerantStrategy ", e);
    }
}

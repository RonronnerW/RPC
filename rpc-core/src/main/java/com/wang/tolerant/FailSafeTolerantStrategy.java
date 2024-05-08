package com.wang.tolerant;

import com.github.rholder.retry.*;
import com.wang.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 静默处理容错策略
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("FailSafeTolerantStrategy：" +e);
        return new RpcResponse();
    }
}

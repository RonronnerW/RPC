package com.wang.tolerant;

import com.wang.model.RpcResponse;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 容错策略
 */
public interface TolerantStrategy {
    /**
     * 容错机制
     * @param context 上下文 用于传递数据
     * @param e 异常
     * @return
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}

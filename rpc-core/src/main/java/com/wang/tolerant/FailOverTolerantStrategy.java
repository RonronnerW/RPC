package com.wang.tolerant;

import com.wang.model.RpcResponse;

import java.util.Map;

/**
 * 故障转移策略
 */
public class FailOverTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        return null;
    }
}

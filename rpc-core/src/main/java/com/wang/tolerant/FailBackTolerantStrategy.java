package com.wang.tolerant;

import com.wang.model.RpcResponse;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

/**
 * 故障降级策略
 */
public class FailBackTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        return null;
    }
}

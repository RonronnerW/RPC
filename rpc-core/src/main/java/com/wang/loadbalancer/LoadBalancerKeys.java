package com.wang.loadbalancer;

/**
 * 负载均衡器常量
 */
public interface LoadBalancerKeys {
    /**
     * 轮询
     */
    String ROUND_ROBIN = "roundRobin";
    /**
     * 随机
     */
    String RANDOM = "random";
    /**
     * 一致性hash
     */
    String CONSISTENT_HASH = "consistentHash";
}

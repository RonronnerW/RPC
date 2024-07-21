package com.wang.loadbalancer;

import com.wang.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡策略
 * 使用 JUC 的 AtomicInteger 实现原子计数器，防止并发冲突
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfos) {
        if (serviceMetaInfos == null) {
            return null;
        }
        int size = serviceMetaInfos.size();
        if (1 == size) {
            return serviceMetaInfos.get(0);
        }
        int idx = currentIndex.getAndIncrement() % size;
        return serviceMetaInfos.get(idx);
    }
}

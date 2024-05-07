package com.wang.loadbalancer;

import com.wang.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性hash负载均衡
 * 使用TreeMap实现一致性hash环，使用ceilingEntry和firstEntry获取符合算法要求的节点
 * 每次调用节点的负载均衡器时都会重新构造hash环，为了及时处理节点变化
 */
public class ConsistentHashLoadBalancer implements LoadBalancer{

    /**
     * 一致性hash环，存放虚拟节点
     */
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数
     */
    private final static int VIRTUAL_NODE_NUM = 100;
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfos) {
        if(serviceMetaInfos==null) {
            return null;
        }
        // 构建虚拟hash环
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfos) {
            for(int i=0; i<VIRTUAL_NODE_NUM; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress()+"#"+i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }
        // 获取请求调用的hash值
        int hash = getHash(requestParams);

        // 选择最近大于等于调用请求的hash值的节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if(entry==null) {
            return virtualNodes.firstEntry().getValue();
        }
        return entry.getValue();
    }

    private int getHash(Object object) {
        return object.hashCode();
    }
}

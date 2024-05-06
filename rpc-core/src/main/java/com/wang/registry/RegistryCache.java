package com.wang.registry;

import com.wang.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册信息本地缓存
 */
public class RegistryCache {
    /**
     * 服务缓存
     */
    List<ServiceMetaInfo> registryCache;

    /**
     * 读缓存
     * @return
     */
    public List<ServiceMetaInfo> readRegistryCache() {
        return registryCache;
    }

    /**
     * 写缓存
     * @param registryCache
     */
    public void writeRegistryCache(List<ServiceMetaInfo> registryCache) {
        this.registryCache = registryCache;
    }

    /**
     * 清空缓存
     */
    public void clearRegistryCache() {
        registryCache.clear();
    }
}

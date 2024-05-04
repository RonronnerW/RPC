package com.wang.registry;

/**
 * @author wanglibin
 * @version 1.0
 * 注册中心常量
 */
public interface RegistryKeys {
    String ETCD = "etcd";
    String ZOOKEEPER = "zookeeper";
    String ADDRESS = "http://localhost:2380";
    Long TIMEOUT = 1000L;
}

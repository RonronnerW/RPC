package com.wang.config;

import com.wang.registry.RegistryKeys;
import lombok.Data;

@Data
public class RegistryConfig {

    /**
     * 注册中心类别
     */
    private String registry = RegistryKeys.ETCD;

    /**
     * 注册中心地址
     */
    private String address = RegistryKeys.ADDRESS;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间（单位毫秒）
     */
    private Long timeout = RegistryKeys.TIMEOUT;
}
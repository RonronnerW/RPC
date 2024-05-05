package com.wang.model;

import cn.hutool.core.util.StrUtil;
import com.wang.constant.RpcConstant;
import lombok.Data;

/**
 * @author wanglibin
 * @version 1.0
 * 注册信息定义
 */
@Data
public class ServiceMetaInfo {
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 服务版本号
     */
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;
    /**
     * 服务主机
     */
    private String serviceHost;
    /**
     * 服务端口
     */
    private Integer servicePort;
    /**
     * 服务分组
     */
    private String serviceGroup = RpcConstant.DEFAULT_SERVICE_GROUP;

    /**
     * 获取键名
     * @return
     */
    public String getServiceKey() {
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    /**
     * 获取服务注册节点键名
     * @return
     */
    public String getServiceNodeKey() {
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }

    /**
     * 获取服务地址
     * @return
     */
    public String getServiceAddress() {
        if(!StrUtil.contains(serviceHost, "http")) {
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }

}

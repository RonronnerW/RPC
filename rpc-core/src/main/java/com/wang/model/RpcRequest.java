package com.wang.model;

import com.wang.constant.RpcConstant;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author wanglibin
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest extends Message{
    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 参数类型列表
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数列表
     */
    private Object[] args;

    /**
     * 服务版本号
     */
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;

    @Override
    public int getMessageType() {
        return RPC_REQUEST;
    }
}

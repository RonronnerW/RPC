package com.wang.model;

import lombok.*;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @author wanglibin
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse extends Message {
    /**
     * 响应数据
     */
    private Object data;

    /**
     * 响应数据类型
     */
    private Class<?> dateType;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 异常信息
     */
    private Exception exception;

    @Override
    public int getMessageType() {
        return RPC_RESPONSE;
    }
}

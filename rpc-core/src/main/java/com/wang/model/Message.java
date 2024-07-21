package com.wang.model;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wanglibin
 * @version 1.0
 */
@Data
public abstract class Message implements Serializable {
    /**
     * 序列号
     */
    private int sequenceId;
    /**
     * 消息类型
     */
    private int messageType;

    public abstract int getMessageType();

    public static Class<?> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }

    public static final int RPC_REQUEST = 1;
    public static final int  RPC_RESPONSE = 2;
    private static final Map<Integer, Class<?>> messageClasses = new HashMap<>();

    static {
        messageClasses.put(RPC_REQUEST, RpcRequest.class);
        messageClasses.put(RPC_RESPONSE, RpcResponse.class);
    }
}

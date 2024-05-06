package com.wang.protocol;

/**
 * 协议相关常量
 */
public interface ProtocolConstant {
    /**
     * 消息头长度
     */
    int MESSAGE_HEADER_LENGTH = 17;
    /**
     * 魔数
     */
    byte MAGIC = 0x1;
    /**
     * 版本号
     */
    byte VERSION = 0x1;
}

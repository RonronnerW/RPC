package com.wang.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 协议消息结构
 * @param <T>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {
    /**
     * 消息头
     */
    private Header header;
    /**
     * 消息体
     */
    private T body;

    @Data
    public static class Header {
        /**
         * 魔数
         */
        private byte magic;
        /**
         * 版本号
         */
        private byte version;
        /**
         * 序列化方式
         */
        private byte serializer;
        /**
         * 类型（请求/响应）
         */
        private byte type;
        /**
         * 状态
         */
        private byte status;
        /**
         * id
         */
        private long id;
        /**
         * 数据长度
         */
        private int length;
    }
}

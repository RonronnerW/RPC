package com.wang.protocol;

import com.wang.serializer.Serializer;
import com.wang.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 编码器
 */
public class ProtocolMessageEncoder {
    public static Buffer encode(ProtocolMessage protocolMessage) throws IOException {
        if(protocolMessage==null || protocolMessage.getHeader()==null) {
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();

        // 依次向缓冲区写入字节
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getId());

        // 获取序列化器
        ProtocolMessageSerializerEnum enumByKey = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());

        Serializer serializer = SerializerFactory.getInstance(enumByKey.getValue());

        byte[] bytes = serializer.serialize(protocolMessage.getBody());
        buffer.appendInt(bytes.length);
        buffer.appendBytes(bytes);

        return buffer;
    }
}

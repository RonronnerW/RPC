package com.wang.protocol;

import com.fasterxml.jackson.annotation.JsonKey;
import com.wang.RpcApplication;
import com.wang.config.RpcConfig;
import com.wang.model.Message;
import com.wang.serializer.*;
import com.wang.utils.SequenceIdUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author wanglibin
 * @version 1.0
 * 消息加解码处理器
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodec extends MessageToMessageCodec<ByteBuf, Message> {
    /**
     * 加码
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer();
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        // 魔数 4
        buffer.writeInt(ProtocolConstant.MAGIC);
        // 版本号 1
        buffer.writeByte(ProtocolConstant.VERSION);
        // 序列化算法 1
        buffer.writeByte(SerializerEnum.getNumberBySerializer(rpcConfig.getSerializer()));
        // 指令类型 1
        buffer.writeByte(msg.getMessageType());
        // 请求序列号 4
        buffer.writeInt(SequenceIdUtil.getSequenceId());
        // 状态
        buffer.writeByte(ProtocolConstant.STATUS_OK);
        // 序列化
        Serializer serializer = SerializerFactory.getInstance(rpcConfig.getSerializer());
        byte[] bytes = serializer.serialize(msg);
        // length 4
        buffer.writeInt(bytes.length);
        // content
        buffer.writeBytes(bytes);
        out.add(buffer);
    }

    /**
     * 解码
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int magicNum = msg.readInt();
        byte version = msg.readByte();
        byte serializerType = msg.readByte();
        byte messageType = msg.readByte();
        int sequenceId = msg.readInt();
        int status = msg.readByte();
        int length = msg.readInt();

        byte[] bytes = new byte[length];
        msg.readBytes(bytes, 0, length);

        String serializerByValue = SerializerEnum.getSerializerByValue(serializerType);
        Serializer serializer = SerializerFactory.getInstance(serializerByValue);
        Class<?> messageClass = Message.getMessageClass(messageType);
        Object message = serializer.deserialize(bytes, messageClass);

        out.add(message);
    }
}

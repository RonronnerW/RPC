package com.wang.client;

import com.google.j2objc.annotations.LoopTranslation;
import com.wang.RpcApplication;
import com.wang.client.handler.ClientHandler;
import com.wang.config.RegistryConfig;
import com.wang.config.RpcConfig;
import com.wang.model.RpcRequest;
import com.wang.model.RpcResponse;
import com.wang.model.ServiceMetaInfo;
import com.wang.protocol.MessageCodec;
import com.wang.protocol.ProtocolFrameDecoder;
import com.wang.registry.Registry;
import com.wang.registry.RegistryFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;

/**
 * @author wanglibin
 * @version 1.0
 */
@Slf4j
public class NettyClient {

    private static final Bootstrap bootstrap;

    private static final NioEventLoopGroup group;

    private static Channel channel;

    static {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodec MESSAGE_CODEC = new MessageCodec();
        ClientHandler CLIENT_HANDLER = new ClientHandler();

        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(new ProtocolFrameDecoder());
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(CLIENT_HANDLER);

            }
        });
    }

    public static RpcResponse sendMessage(RpcRequest request, ServiceMetaInfo metaInfo) {
        try {
            channel = bootstrap.connect(metaInfo.getServiceHost(), metaInfo.getServicePort()).sync().channel();
            if (channel != null) {
                channel.writeAndFlush(request);
                //sync()堵塞获取结果
                channel.closeFuture().sync();
                // 阻塞的获得结果，通过给channel设计别名，获取特定名字下的channel中的内容（这个在hanlder中设置）
                // AttributeKey是，线程隔离的，不会由线程安全问题。
                // 当前场景下选择堵塞获取结果
                // 其它场景也可以选择添加监听器的方式来异步获取结果 channelFuture.addListener...
                AttributeKey<RpcResponse> key = AttributeKey.valueOf(String.valueOf(request.getSequenceId()));
                RpcResponse response = channel.attr(key).get();
                return response;
            }
        } catch (Exception e) {
            log.error("client error", e);
        }
        return null;
    }
}

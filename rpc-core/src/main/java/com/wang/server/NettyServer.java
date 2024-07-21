package com.wang.server;

import com.wang.RpcApplication;
import com.wang.config.RpcConfig;
import com.wang.model.RpcResponse;
import com.wang.protocol.MessageCodec;
import com.wang.protocol.ProtocolFrameDecoder;
import com.wang.server.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wanglibin
 * @version 1.0
 */
@Slf4j
public class NettyServer implements Server {
    @Override
    public void doStart(int port) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodec MESSAGE_CODEC = new MessageCodec();
        ServerHandler SERVER_HANDLER = new ServerHandler();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(SERVER_HANDLER);
                }
            });
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Channel channel = serverBootstrap.bind(rpcConfig.getServerPort()).sync().channel();
            channel.closeFuture().addListener(future -> {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            log.error("server error", e);
        }
    }
}

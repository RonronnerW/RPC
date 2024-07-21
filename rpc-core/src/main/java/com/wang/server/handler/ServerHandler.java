package com.wang.server.handler;

import com.wang.model.Message;
import com.wang.model.RpcRequest;
import com.wang.model.RpcResponse;
import com.wang.registry.LocalRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wanglibin
 * @version 1.0
 * server 端请求处理器
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setSequenceId(msg.getSequenceId());
        response.setMessageType(Message.RPC_RESPONSE);

        String serviceName = msg.getServiceName();
        String methodName = msg.getMethodName();
        Class[] parameterTypes = msg.getParameterTypes();
        Object[] args = msg.getArgs();

        try {
            // 获取对象
            Class<?> aClass = Class.forName(serviceName);
            Class<?> service = LocalRegistry.get(aClass.getName());
            // 获取方法
            Method method = service.getMethod(methodName, parameterTypes);
            // 调用方法
            Object invoke = method.invoke(service.newInstance(), args);
            // 封装结果
            response.setData(invoke);
        } catch (Exception e) {
            // 封装异常
            response.setException(new RuntimeException("远程调用异常: "+e.getCause().getMessage()));
        }
        // 返回结果
        ctx.writeAndFlush(response);
    }
}

package com.wang.server.tcp;

import com.wang.model.RpcRequest;
import com.wang.model.RpcResponse;
import com.wang.protocol.*;
import com.wang.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import org.apache.zookeeper.server.Request;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * TCP 请求处理器
 */
public class TcpServerHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket netSocket) {
        TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            // 解码
            ProtocolMessage protocolMessage = null;
            try {
                protocolMessage = ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 处理请求 构造响应结果
            RpcResponse rpcResponse = new RpcResponse();
            RpcRequest rpcRequest = (RpcRequest) protocolMessage.getBody();
            try {
                // 获取需要调用服务的实现类，通过反射实现
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDateType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            // 发送响应 编码
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
            try {
                Buffer encode = ProtocolMessageEncoder.encode(rpcResponseProtocolMessage);
                netSocket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        netSocket.handler(tcpBufferHandlerWrapper);
    }
}

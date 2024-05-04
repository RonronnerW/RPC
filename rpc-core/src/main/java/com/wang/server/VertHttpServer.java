package com.wang.server;

import io.vertx.core.Vertx;

/**
 * @author wanglibin
 * @version 1.0
 */
public class VertHttpServer implements HttpServer{
    @Override
    public void doStart(int port) {
        // 创建vertx实例
        Vertx vertx = Vertx.vertx();

        // 创建 http 服务器
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();

        // 监听端口并处理请求
//        httpServer.requestHandler(request -> {
//            // 处理 http 请求
//            System.out.println(request.method()+" "+ request.uri());
//
//            // 发送 http 响应
//            request.response()
//                    .putHeader("content-type", "text/plain")
//                    .end("hello from vert.x http server");
//        });
        // 绑定请求处理器
        httpServer.requestHandler(new HttpServerHandler());

        // 启动 http 服务器并监听指定端口
        httpServer.listen(port, httpServerResult -> {
            if(httpServerResult.succeeded()) {
                System.out.println("Server is now listening on port: "+port);
            } else{
                System.out.println("Fail to start server:"+httpServerResult.cause());
            }
        });
    }
}

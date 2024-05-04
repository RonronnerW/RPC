package com.wang;

import com.wang.registry.LocalRegistry;
import com.wang.server.VertHttpServer;
import com.wang.service.UserService;
import com.wang.service.UserServiceImpl;

/**
 * @author wanglibin
 * @version 1.0
 */
public class ProviderExample {
    public static void main(String[] args) {
        // 初始化
        RpcApplication.getRpcConfig();
//        RpcApplication.init();
        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        // 启动web服务器
        VertHttpServer vertHttpServer = new VertHttpServer();
        vertHttpServer.doStart(8080);
    }
}

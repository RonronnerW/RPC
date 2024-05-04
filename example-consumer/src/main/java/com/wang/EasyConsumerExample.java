package com.wang;

import com.wang.config.RpcConfig;
import com.wang.model.User;
import com.wang.proxy.ServiceProxyFactory;
import com.wang.serializer.Serializer;
import com.wang.service.UserService;
import com.wang.utils.ConfigUtils;

import java.io.IOException;

/**
 * @author wanglibin
 * @version 1.0
 */
public class EasyConsumerExample {
    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc.getName());
        System.out.println(rpc.getVersion());
        System.out.println(rpc.getServerHost());
        System.out.println(rpc.getServerPort());

        User user = new User();
        user.setName("wang");
        // 使用工厂来获取动态代理对象
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User newUser = userService.getUser(user);
        if(newUser==null) {
            System.out.println("user==null");
        } else {
            System.out.println(user.getName());
        }

        int number = userService.getNumber();
        System.out.println("number = " + number);

    }
}
package com.wang;

import com.wang.bootstrap.ConsumerBootstrap;
import com.wang.config.RpcConfig;
import com.wang.model.User;
import com.wang.proxy.ServiceProxyFactory;
import com.wang.service.UserService;
import com.wang.utils.ConfigUtils;

/**
 * @author wanglibin
 * @version 1.0
 */
public class ConsumerExample {
    public static void main(String[] args) {
        // 初始化配置中心和注册中心
        ConsumerBootstrap.init();

        // 获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("wang");
        // 调用
        User serviceUser = userService.getUser(user);

        if(serviceUser==null) {
            System.out.println("serviceUser=null");
        } else {
            System.out.println("serviceUser=null="+serviceUser.getName());
        }
    }
}
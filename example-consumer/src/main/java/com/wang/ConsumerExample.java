package com.wang;

import com.wang.config.RpcConfig;
import com.wang.model.User;
import com.wang.proxy.ClientProxyFactory;
import com.wang.service.UserService;
import com.wang.utils.ConfigUtils;

/**
 * @author wanglibin
 * @version 1.0
 */
public class ConsumerExample {
    public static void main(String[] args) {

        UserService proxy = ClientProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("wang");
        User user1 = proxy.getUser(user);
        System.out.println("user1 = " + user1);

        User user2 = proxy.getUser(user);
        System.out.println("user2 = " + user2);

    }
}
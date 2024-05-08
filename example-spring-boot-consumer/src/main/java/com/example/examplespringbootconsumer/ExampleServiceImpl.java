package com.example.examplespringbootconsumer;

import com.wang.model.User;
import com.wang.rpcspringbootstarter.annotation.RpcReference;
import com.wang.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {
    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("wang");
        User serviceUser = userService.getUser(user);
        System.out.println("serviceUser.getName() = " + serviceUser.getName());
    }
}

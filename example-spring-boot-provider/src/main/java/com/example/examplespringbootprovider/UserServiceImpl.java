package com.example.examplespringbootprovider;

import com.wang.model.User;
import com.wang.rpcspringbootstarter.annotation.RpcService;
import com.wang.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println(user.getName());
        return user;
    }

}

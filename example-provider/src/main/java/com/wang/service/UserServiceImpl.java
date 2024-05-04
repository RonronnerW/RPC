package com.wang.service;

import com.wang.model.User;

/**
 * @author wanglibin
 * @version 1.0
 */
public class UserServiceImpl implements UserService{
    @Override
    public User getUser(User user) {
        System.out.println(user.getName());
        return user;
    }
}

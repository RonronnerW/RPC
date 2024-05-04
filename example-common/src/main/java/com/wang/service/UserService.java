package com.wang.service;

import com.wang.model.User;

/**
 * @author wanglibin
 * @version 1.0
 */
public interface UserService {
    User getUser(User user);
    default int getNumber() {
        return 1;
    }
}

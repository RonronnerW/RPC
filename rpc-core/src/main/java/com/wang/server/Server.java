package com.wang.server;

/**
 * @author wanglibin
 * @version 1.0
 * http 服务接口
 */
public interface Server {
    /**
     * 启动服务器
     * @param port
     */
    void doStart(int port);
}

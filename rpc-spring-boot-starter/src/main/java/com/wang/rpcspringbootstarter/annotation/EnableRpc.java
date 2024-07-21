package com.wang.rpcspringbootstarter.annotation;

import com.wang.rpcspringbootstarter.bootstrap.RpcConsumerBootstrap;
import com.wang.rpcspringbootstarter.bootstrap.RpcInitBootstrap;
import com.wang.rpcspringbootstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 全局标识项目需要引入RPC框架、执行初始化方法
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {
}

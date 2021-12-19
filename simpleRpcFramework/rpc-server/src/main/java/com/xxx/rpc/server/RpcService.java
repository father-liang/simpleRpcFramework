package com.xxx.rpc.server;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//表示这个注解是用于类的
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component //添加该注解，可以被Spring扫描到
public @interface RpcService {
    Class<?> value();

    String version() default "";
}

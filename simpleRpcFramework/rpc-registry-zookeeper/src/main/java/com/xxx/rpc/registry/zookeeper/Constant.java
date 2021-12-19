package com.xxx.rpc.registry.zookeeper;

/**
 * 常量
 */
public interface Constant {

    int ZK_SESSION_TIMEOUT = 5000;//会话超时时间
    int ZK_CONNECTION_TIMEOUT = 1000;//连接超时时间

    String ZK_REGISTRY_PATH = "/registry";//注册地址
}

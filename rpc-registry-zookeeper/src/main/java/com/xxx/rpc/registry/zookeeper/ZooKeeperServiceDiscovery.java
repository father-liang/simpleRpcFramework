package com.xxx.rpc.registry.zookeeper;

import com.xxx.rpc.common.utils.CollectionUtil;
import com.xxx.rpc.registry.ServiceDiscovery;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ZooKeeperServiceDiscovery implements ServiceDiscovery {
    //日志对象
    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceDiscovery.class);

    private String zkAddress;//zookeeper地址

    public ZooKeeperServiceDiscovery(String zkAddress){
        this.zkAddress = zkAddress;

    }

    @Override
    public String discover(String serviceName) {
        //创建zookeeper客户端
        ZkClient zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper");

        try {
            //service节点
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
            if(!zkClient.exists(servicePath)){
                throw new RuntimeException(String.format("can not find any service node on path %s", servicePath));
            }

            //获取service节点下面的所有子节点
            List<String> addressList = zkClient.getChildren(servicePath);
            if(CollectionUtil.isEmpty(addressList)){
                throw new RuntimeException(String.format("can not find any address node on path %s", servicePath));
            }

            String address;
            int size = addressList.size();
            if(size==1){
                address = addressList.get(0);
                LOGGER.debug("get only address node: {}", address);
            }else{
                //如果存在多个地址，则随机获取该地址
                address = addressList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("get random address node: {}", address);
            }

            String addressPath = servicePath + "/" + address;
            return zkClient.readData(addressPath);
        } finally {
            zkClient.close();
        }

    }
}

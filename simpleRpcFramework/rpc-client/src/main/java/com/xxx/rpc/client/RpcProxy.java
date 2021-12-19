package com.xxx.rpc.client;

import com.xxx.rpc.common.bean.RpcRequest;
import com.xxx.rpc.common.bean.RpcResponse;
import com.xxx.rpc.common.utils.StringUtil;
import com.xxx.rpc.registry.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * rpc代理，用于创建rpc服务代理
 * 使用Netty客户端发送rpc请求，获取反馈消息，拿到相关的服务器调用类的相关方法调用结果
 */
public class RpcProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);

    private String serviceAddress;
    //服务地址发现类，该类由注册中心实现
    private ServiceDiscovery serviceDiscovery;

    //不使用注册中心时，直接传入服务地址
    public RpcProxy(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    //当需要注册中心时，传入注册中心的服务地址发现类对象
    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;

    }

    //创建类方法
    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass) {
        return create(interfaceClass, "");
    }

    //创建类方法，带有服务版本参数
    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass, final String serviceVersion) {
        //创建动态代理对象
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //创建RPC 请求对象并设置请求属性
                        RpcRequest request = new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setInterfaceName(method.getDeclaringClass().getName());
                        request.setServiceVersion(serviceVersion);
                        request.setMethodName(method.getName());
                        request.setParamterTypes(method.getParameterTypes());
                        request.setParameters(args);

                        //获取RPC服务地址
                        if(serviceDiscovery != null){
                            String serviceName = interfaceClass.getName();
                            if(StringUtil.isNotEmpty(serviceVersion)){
                                serviceName += "-" + serviceVersion;//服务名称
                            }

                            serviceAddress = serviceDiscovery.discover(serviceName); //远程获取服务地址
                            LOGGER.debug("discover service: {} => {}", serviceName, serviceAddress);
                        }

                        //如果服务器地址为空，就报错
                        if(StringUtil.isEmpty(serviceAddress)){
                            throw new RuntimeException("server address is empty");
                        }

                        //从rpc服务器地址中解析IP地址和端口号
                        String[] array = serviceAddress.split(":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);

                        //创建rpc客户端对象并发送rpc请求
                        RpcClient client = new RpcClient(host, port);
                        long time = System.currentTimeMillis();

                        RpcResponse response = client.send(request);//获取rpc请求的反馈对象
                        LOGGER.debug("time: {}ms", System.currentTimeMillis()-time);

                        if(response == null){
                            throw new RuntimeException("response is null");
                        }

                        //返回RPC响应结果
                        if(response.hasException()){
                            throw response.getException();
                        }else{
                            return response.getResult();
                        }
                    }
                });
    }

}

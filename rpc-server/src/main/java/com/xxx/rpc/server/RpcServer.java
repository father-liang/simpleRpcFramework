package com.xxx.rpc.server;

import com.xxx.rpc.common.bean.RpcRequest;
import com.xxx.rpc.common.bean.RpcResponse;
import com.xxx.rpc.common.codec.RpcDecoder;
import com.xxx.rpc.common.codec.RpcEncoder;
import com.xxx.rpc.common.utils.StringUtil;
import com.xxx.rpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * RPC服务器，用于发布RPC服务
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);
    //服务地址
    private String serviceAddress;
    //服务地址注册类
    private ServiceRegistry serviceRegistry;

    private Map<String, Object> handlerMap = new HashMap<>();

    public RpcServer(String serviceAddress){
        this.serviceAddress = serviceAddress;
    }

    public RpcServer(String serviceAddress, ServiceRegistry serviceRegistry){
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * afterPropertiesSet方法就是在初始化所有Bean之前调用的方法
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new RpcDecoder(RpcRequest.class));
                    pipeline.addLast(new RpcEncoder(RpcResponse.class));
                    pipeline.addLast(new RpcServerHandler(handlerMap));
                }
            });

            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            String[] addressArray = StringUtil.split(serviceAddress, ":");
            String ip = addressArray[0];
            int port = Integer.parseInt(addressArray[1]);

            //启动Rpc服务器
            ChannelFuture future = bootstrap.bind(ip, port).sync();
            //注册RPC服务器地址
            if(serviceRegistry != null){
                for(String serviceName : handlerMap.keySet()){
                    serviceRegistry.register(serviceName, serviceAddress);
                    LOGGER.debug("register service: {} => {}", serviceName, serviceAddress);
                }
            }

            LOGGER.debug("server started on port {}", port);

            //关闭 RPC 服务器
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * 用来操作Spring容器中的类的，这里会扫描带有RpcService注解的类
     * @param ctx
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);

        if(MapUtils.isNotEmpty(serviceBeanMap)){
            //遍历所有带有RpcService注解的类

            for(Object serviceBean:serviceBeanMap.values()){
                //获取注解对象
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);

                String serviceName = rpcService.value().getName();
                String serviceVersion = rpcService.version();

                if(StringUtil.isNotEmpty(serviceVersion)){
                    serviceName += "-" + serviceVersion; //服务名和版本号连接
                }

                handlerMap.put(serviceName, serviceBean);//将服务名，相应的类，存到map中
            }
        }
    }
}

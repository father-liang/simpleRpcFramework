# simpleRpcFramework

## 项目结构描述
![image](https://github.com/father-liang/simpleRpcFramework/blob/main/project.jpg)


### 包含以下工程：
#### (1)rpc-client
实现了rpc的服务动态代理(RpcProxy)以及基于Netty封装的一个客户端网络层(RpcClient)

#### (2)rpc-common
封装了RpcRequest和RpcResponse，即rpc请求和响应的数据结构
基于Netty提供了编解码器
提供了序列化反序列化等工具

#### (3)rpc-registry
提供了服务发现和注册接口

#### (4)rpc-registry-zookeeper
基于zookeeper的服务发现和注册接口

#### (5)rpc-server
rpc服务器(RpcServer)的实现，用来监听rpc请求以及向Zookeeper注册服务地址
rpc服务本地调用

#### (6)rpc-sample-api
rpc测试公共api服务接口

#### (7)rpc-sample-client
rpc测试客户端

#### (8)rpc-sample-server
rpc测试服务启动程序和服务实现    


其中rpc-client、rpc-common、rpc-registry/rpc-registry-zookeeper、rpc-server为框架的实现部分，剩下的rpc-sample-api、rpc-sample-client、rpc-sample-server为调用框架的业务工程。

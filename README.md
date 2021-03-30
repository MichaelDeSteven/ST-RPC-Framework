# ST-RPC-Framework
基于nacos实现的rpc框架，项目架构参考自dubbo
## 特性

* 实现了基于 Java 原生 Socket 传输与 Netty 传输两种网络传输方式
* Socket连接采用了多线程的设计，可自定义配置线程池参数
* 实现了四种序列化算法，Google Protobuf、Hessian、Kryo、Json
* 实现了多种负载均衡算法，随机、轮转、一致性哈希、LRU、LFU等
* 服务端提供了自动注册服务机制
* 基于nacos的服务注册和服务发现
* 底层网络传输为TCP，实现了自定义的通信协议
* 服务可以增加group、version用于处理一个接口多个实现的情况
* 增加Netty心跳机制


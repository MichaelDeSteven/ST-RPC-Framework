package dgut.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @description: 服务注册接口
 * @author: Steven
 * @time: 2021/3/4 21:48
 */
public interface IServiceRegistry {

    /**
     * 服务提供方服务注册接口
     * @param serviceName
     * @param address
     */
    void registry(String serviceName, InetSocketAddress address);
}

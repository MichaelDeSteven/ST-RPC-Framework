package dgut.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @description: IServiceRegistry
 * @author: Steven
 * @time: 2021/3/4 21:48
 */
public interface IServiceRegistry {

    void registry(String serviceName, InetSocketAddress address);
}

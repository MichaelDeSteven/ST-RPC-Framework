package dgut.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @description: IServiceDiscovery
 * @author: Steven
 * @time: 2021/3/10 15:56
 */
public interface IServiceDiscovery {

    InetSocketAddress lookupService(String serviceName);
}

package dgut.rpc.registry;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @description: 服务发现接口
 * @author: Steven
 * @time: 2021/3/10 15:56
 */
public interface IServiceDiscovery {

    /**
     * 服务消费方获取服务提供方信息
     * @param serviceName
     * @return
     */
    List<Instance> lookupService(String serviceName);
}

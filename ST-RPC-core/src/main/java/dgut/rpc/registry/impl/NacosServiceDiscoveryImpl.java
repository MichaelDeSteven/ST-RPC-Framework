package dgut.rpc.registry.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import dgut.rpc.enumeration.RpcError;
import dgut.rpc.loadbalance.ILoadBalancer;
import dgut.rpc.loadbalance.impl.RandomLoadBalancerImpl;
import dgut.rpc.registry.IServiceDiscovery;
import dgut.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @description: NacosServiceDiscoveryImpl
 * @author: Steven
 * @time: 2021/3/10 16:16
 */
public class NacosServiceDiscoveryImpl implements IServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscoveryImpl.class);

    private ILoadBalancer loadBlancer;

    public NacosServiceDiscoveryImpl() {
        loadBlancer = new RandomLoadBalancerImpl();
    }

    public NacosServiceDiscoveryImpl(ILoadBalancer loadBlancer) {
        this.loadBlancer = loadBlancer;
        if (loadBlancer == null) loadBlancer = new RandomLoadBalancerImpl();
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        List<Instance> instances;

        try {
            instances = NacosUtil.getAllInstances(serviceName);
            Instance instance = loadBlancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("找不到对应的服务");
            throw new RuntimeException(RpcError.SERVICE_NOT_FOUND.getMessage());
        }
    }
}

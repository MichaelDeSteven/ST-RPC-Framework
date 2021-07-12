package dgut.rpc.registry.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import dgut.rpc.enumeration.RpcError;
import dgut.rpc.registry.IServiceDiscovery;
import dgut.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: NacosServiceDiscoveryImpl
 * @author: Steven
 * @time: 2021/3/10 16:16
 */
public class NacosServiceDiscoveryImpl implements IServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscoveryImpl.class);

    @Override
    public List<Instance> lookupService(String serviceName) {
        try {
            return NacosUtil.getAllInstances(serviceName);
        } catch (NacosException e) {
            logger.error("[op:NacosServiceDiscoveryImpl:lookupService] 找不到对应的服务");
            throw new RuntimeException(RpcError.SERVICE_NOT_FOUND.getMessage());
        }
    }
}

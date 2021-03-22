package dgut.rpc.registry.impl;

import com.alibaba.nacos.api.exception.NacosException;
import dgut.rpc.enumeration.RpcError;
import dgut.rpc.registry.IServiceRegistry;
import dgut.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @description: NacosServiceRegistryImpl
 * @author: Steven
 * @time: 2021/3/10 15:56
 */
public class NacosServiceRegistryImpl implements IServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistryImpl.class);

    @Override
    public void registry(String serviceName, InetSocketAddress address) {
        try {
            NacosUtil.registerService(serviceName, address);
        } catch (NacosException e) {
            logger.error("注册服务失败");
            throw new RuntimeException(RpcError.REGISTER_SERVICE_FAILED.getMessage());
        }
    }


}

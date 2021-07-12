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
            logger.info("[op:NacosServiceRegistryImpl:registry] 服务注册完毕：" +
                            "服务名为：{},服务提供方为：{}:{}",
                    serviceName, address.getHostString(), address.getPort());
        } catch (NacosException e) {
            logger.error("[op:NacosServiceRegistryImpl:registry] 服务注册失败");
            throw new RuntimeException(RpcError.REGISTER_SERVICE_FAILED.getMessage());
        }
    }

}

package dgut.rpc.provider.impl;

import dgut.rpc.enumeration.RpcError;
import dgut.rpc.provider.IServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: ServiceProviderImpl
 * @author: Steven
 * @time: 2021/3/4 22:16
 */
public class ServiceProviderImpl implements IServiceProvider {

    private Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    private Map<String, Object> serviceProviderMap = new ConcurrentHashMap();
    private Set<String> serviceNameSet = ConcurrentHashMap.newKeySet();


    @Override
    public <T> void addServiceProvider(String serviceName, T service) {
        if (serviceNameSet.contains(serviceName)) {
            logger.info("[op:ServiceProviderImpl:addServiceProvider] 接口: {} 已包含服务: {}！",
                    serviceName, service.getClass().getName());
            return;
        }
        serviceProviderMap.put(serviceName, service);
        serviceNameSet.add(serviceName);
        logger.info("[op:ServiceProviderImpl:addServiceProvider] 向接口: {} 提供服务: {}",
                serviceName, service.getClass().getName());
    }

    @Override
    public Object getServiceProvider(String serviceName) throws Exception {
        Object serviceProvider = serviceProviderMap.get(serviceName);
        if (serviceProvider == null) {
            logger.error("[op:ServiceProviderImpl:getServiceProvider] 找不到对应的服务：{}",
                    serviceName);
            throw new Exception(RpcError.SERVICE_NOT_FOUND.getMessage());
        }
        return serviceProvider;
    }
}

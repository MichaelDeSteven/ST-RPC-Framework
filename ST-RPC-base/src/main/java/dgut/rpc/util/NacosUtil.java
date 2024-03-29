package dgut.rpc.util;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import dgut.rpc.enumeration.RpcError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * @description: NacosUtil
 * @author: Steven
 * @time: 2021/3/10 15:37
 */
public class NacosUtil {

    private static final Logger logger = LoggerFactory.getLogger(NacosUtil.class);

    private static final NamingService namingService;
    private static final Set<String> serviceNames = new HashSet<>();
    private static InetSocketAddress address;

    private static final String SERVER_ADDR = "127.0.0.1:8848";

    static {
        namingService = getNacosNamingService();
    }

    public static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RuntimeException(RpcError
                    .FAILED_TO_CONNECT_TO_SERVICE_REGISTRY
                    .getMessage());
        }
    }

    public static void registerService(String serviceName, InetSocketAddress address)
            throws NacosException {
        Instance instance = new Instance();
        instance.setServiceName(serviceName);
        instance.setIp(address.getHostName());
        instance.setPort(address.getPort());
        Map<String, String> metaData = new HashMap<>();
        metaData.put("host", address.getHostName());
        metaData.put("application", "rpc");
        metaData.put("service", serviceName);
        instance.setMetadata(metaData);
        namingService.registerInstance(serviceName, instance);
        NacosUtil.address = address;
        serviceNames.add(serviceName);
    }

    public static List<Instance> getAllInstances(String serviceName)
            throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    public static void clearRegistry() {
        if (!serviceNames.isEmpty() && address != null) {
            String host = address.getHostName();
            int port = address.getPort();
            Iterator<String> iterator = serviceNames.iterator();
            while (iterator.hasNext()) {
                String serviceName = iterator.next();
                try {
                    namingService.deregisterInstance(serviceName, host, port);
                } catch (NacosException e) {
                    logger.error("[op:NacosUtil:clearRegistry] 注销服务 {} 失败",
                            serviceName, e);
                    throw new RuntimeException(RpcError
                            .CLEAR_REGISTER_SERVICE_FAILED
                            .getMessage());
                }
            }
        }
    }

    public static ConfigService getConfigService(String serverAddr) {
        try {
            return NacosFactory.createConfigService(serverAddr);
        } catch (NacosException e) {
            logger.error("[op:NacosUtil:getConfigService] 创建配置服务出错: ", e);
            throw new RuntimeException(RpcError
                    .FAILED_TO_CREATE_TO_CONFIG_SERVICE
                    .getMessage());
        }
    }

    public static ConfigService getConfigService(Properties serverAddr) {
        try {
            return NacosFactory.createConfigService(serverAddr);
        } catch (NacosException e) {
            logger.error("[op:NacosUtil:getConfigService] 创建配置服务出错: ", e);
            throw new RuntimeException(RpcError
                    .FAILED_TO_CREATE_TO_CONFIG_SERVICE
                    .getMessage());
        }
    }
}

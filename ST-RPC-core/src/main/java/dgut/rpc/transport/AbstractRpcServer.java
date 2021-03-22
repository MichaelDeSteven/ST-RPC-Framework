package dgut.rpc.transport;

import dgut.rpc.annotation.Service;
import dgut.rpc.annotation.ServiceScan;
import dgut.rpc.enumeration.RpcError;
import dgut.rpc.provider.IServiceProvider;
import dgut.rpc.provider.impl.ServiceProviderImpl;
import dgut.rpc.registry.IServiceRegistry;
import dgut.rpc.registry.impl.NacosServiceRegistryImpl;
import dgut.rpc.util.ReflectUtil;
import dgut.rpc.util.RpcServicePropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * @description: AbstractRpcServer
 * @author: Steven
 * @time: 2021/3/10 17:28
 */
public abstract class AbstractRpcServer<T> implements IRpcServer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRpcServer.class);

    protected IServiceProvider serviceProvider;

    protected IServiceRegistry serviceRegistry;

    protected int port;

    public AbstractRpcServer(int port) {
        this.serviceProvider = new ServiceProviderImpl();
        this.serviceRegistry = new NacosServiceRegistryImpl();
        this.port = port;
    }

    public String getServiceName(String serviceName, String group, String version) {
        RpcServicePropertyUtil servicePropertyUtil =
                RpcServicePropertyUtil.builder()
                        .serviceName(serviceName)
                        .group(group)
                        .version(version)
                        .build();
        return servicePropertyUtil.toRpcServiceName();
    }

    public void scanServices() {
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if(!startClass.isAnnotationPresent(ServiceScan.class)) {
                logger.error("启动类缺少 @ServiceScan 注解");
                throw new RuntimeException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND.getMessage());
            }
        } catch (ClassNotFoundException e) {
            logger.error("出现未知错误");
            throw new RuntimeException(RpcError.UNKNOWN_ERROR.getMessage());
        }
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for(Class<?> clazz : classSet) {
            if(clazz.isAnnotationPresent(Service.class)) {
                String serviceName = clazz.getAnnotation(Service.class).name();
                String group = clazz.getAnnotation(Service.class).group();
                String version = clazz.getAnnotation(Service.class).version();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface: interfaces) {
                        publishService(getServiceName(oneInterface.getCanonicalName(),
                                group, version),
                                obj);
                    }
                } else {
                    publishService(getServiceName(serviceName, group, version), obj);
                }
            }
        }
    }


    @Override
    public abstract void start();

    @Override
    public <T> void publishService(String serviceName, T service) {
        serviceRegistry.registry(serviceName, new InetSocketAddress(port));
        serviceProvider.addServiceProvider(serviceName, service);
    }
}

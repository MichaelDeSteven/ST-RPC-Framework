package dgut.rpc.provider;

/**
 * @description: 注册和提供服务实例
 * @author: Steven
 * @time: 2021/3/4 22:11
 */
public interface IServiceProvider {

    /**
     * 服务提供方注册服务实例
     * @param serviceName 服务名
     * @param service 服务
     * @param <T> 服务类型
     */
    <T> void addServiceProvider(String serviceName, T service);

    /**
     * 服务提供方提供服务实例
     * @param serviceName 服务名
     * @return 服务实例
     * @throws Exception
     */
    Object getServiceProvider(String serviceName) throws Exception;
}

package dgut.rpc.provider;

/**
 * @description: 注册和提供服务实例
 * @author: Steven
 * @time: 2021/3/4 22:11
 */
public interface IServiceProvider {

    <T> void addServiceProvider(String serviceName, T service);

    Object getServiceProvider(String serviceName) throws Exception;
}

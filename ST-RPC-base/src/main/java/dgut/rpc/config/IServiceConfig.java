package dgut.rpc.config;

/**
 * @description: 服务配置
 * @author: Steven
 * @time: 2021/4/11 14:48
 */
public interface IServiceConfig {

    /**
     * 发布配置
     * @param content
     */
    void publishConfig(String content);

    /**
     * 配置更新后的回调方法
     */
    void addListener();

    /**
     * 获取配置信息
     * @return
     */
    String getConfig();
}

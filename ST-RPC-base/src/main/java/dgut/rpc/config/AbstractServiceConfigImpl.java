package dgut.rpc.config;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * @description: ServiceConfigImpl
 * @author: Steven
 * @time: 2021/4/11 14:48
 */
public abstract class AbstractServiceConfigImpl implements IServiceConfig {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServiceConfigImpl.class);

    protected String serverAddr;

    protected ConfigService configService;

    protected String dataId;

    protected String group;

    public AbstractServiceConfigImpl() {

    }

    @Override
    public void publishConfig(String content) {
        try {
            configService.publishConfig(dataId, group, content);
        } catch (NacosException e) {
            e.printStackTrace();
            logger.error("发布配置失败");
        }
    }

    @Override
    public void addListener() {
        try {
            configService.addListener(dataId, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String s) {
                    logger.info("配置信息已更新：{}", s);
                    updateConfig(s);
                }
            });
        } catch (NacosException e) {
            e.printStackTrace();
            logger.error("添加监视器失败");
        }
    }

    @Override
    public String getConfig() {
        String config = null;
        try {
            config = configService.getConfig(dataId, group, 3000);
        } catch (NacosException e) {
            logger.info("获取配置信息超时：data：{}, group：{}", dataId, group);
            e.printStackTrace();
        }
        return config;
    }


    protected void updateConfig(String s) {
        throw new UnsupportedOperationException();
    }
}

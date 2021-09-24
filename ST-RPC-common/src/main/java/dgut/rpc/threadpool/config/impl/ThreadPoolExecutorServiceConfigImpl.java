package dgut.rpc.threadpool.config.impl;

import com.alibaba.fastjson.JSON;
import dgut.rpc.config.AbstractServiceConfigImpl;
import dgut.rpc.threadpool.ResizableCapacityLinkedBlockingQueue;
import dgut.rpc.domain.ThreadPoolEntity;
import dgut.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description: ThreadPoolExecutorServiceConfigImpl
 * @author: Steven
 * @time: 2021/4/11 17:46
 */
public class ThreadPoolExecutorServiceConfigImpl extends AbstractServiceConfigImpl {

    private ThreadPoolExecutor threadPoolExecutor;

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolExecutorServiceConfigImpl.class);

    public ThreadPoolExecutorServiceConfigImpl(ThreadPoolExecutor threadPoolExecutor, String serverAddr, String dataId,
                                               String group) {
        this.serverAddr = serverAddr;
        this.dataId = dataId;
        this.group = group;
        this.threadPoolExecutor = threadPoolExecutor;
        configService = NacosUtil.getConfigService(serverAddr);
    }

    private void modifyArg(int corePoolSize, int maxPoolSize, int workQueueSize) {
        ResizableCapacityLinkedBlockingQueue queue = (ResizableCapacityLinkedBlockingQueue) threadPoolExecutor.getQueue();
        threadPoolExecutor.setCorePoolSize(corePoolSize);
        threadPoolExecutor.setMaximumPoolSize(maxPoolSize);
        queue.setCapacity(workQueueSize);
    }

    @Override
    protected void updateConfig(String s) {
        List<ThreadPoolEntity> entityList = JSON.parseArray(s, ThreadPoolEntity.class);
        for (ThreadPoolEntity entity : entityList) {
            modifyArg(entity.getCorePoolSize(), entity.getMaxPoolSize(), entity.getWorkQueueCapacity());
            BlockingQueue queue = threadPoolExecutor.getQueue();
            logger.info(Thread.currentThread().getName() + ":" +
                    " 核心线程数：" + threadPoolExecutor.getCorePoolSize() +
                    " 活动线程数：" + threadPoolExecutor.getActiveCount() +
                    " 最大线程数：" + threadPoolExecutor.getMaximumPoolSize() +
                    " 任务完成数：" + threadPoolExecutor.getCompletedTaskCount() +
                    " 当前排队任务数：" + queue.size() +
                    " 队列总大小：" + queue.size() +
                    " 队列剩余大小：" + queue.remainingCapacity());
        }
    }
}

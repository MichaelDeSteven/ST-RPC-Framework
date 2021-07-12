package dgut.rpc.threadpool;

import dgut.rpc.threadpool.config.impl.ThreadPoolExecutorServiceConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @description: DynamicThreadPoolManager
 * @author: Steven
 * @time: 2021/4/11 14:05
 */
public class DynamicThreadPoolManager {

    private final static Logger logger = LoggerFactory.getLogger(ThreadPoolFactory.class);

    private ThreadPoolExecutor threadPoolExecutor;

    private ThreadPoolExecutorServiceConfigImpl serviceConfig;

    private static class DynamicThreadPoolManagerHolder {
        private static DynamicThreadPoolManager dynamicThreadPoolManager =
                new DynamicThreadPoolManager();
    }

    private DynamicThreadPoolManager() {

    }

    public static DynamicThreadPoolManager getInstance() {
        return DynamicThreadPoolManagerHolder.dynamicThreadPoolManager;
    }

    public ThreadPoolExecutor createThreadPoolExecutor() {
        threadPoolExecutor = (ThreadPoolExecutor) ThreadPoolFactory.createDefaultThreadPool();
        serviceConfig = new ThreadPoolExecutorServiceConfigImpl(threadPoolExecutor,
                "0.0.0.0:8848", "123", "dgut");
        serviceConfig.addListener();
        return threadPoolExecutor;
    }

    public void getStatusThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        BlockingQueue queue = threadPoolExecutor.getQueue();
        logger.info(Thread.currentThread().getName() + ":" +
                " 核心线程数：" + threadPoolExecutor.getCorePoolSize() +
                " 活动线程数：" + threadPoolExecutor.getActiveCount() +
                " 最大线程数：" + threadPoolExecutor.getMaximumPoolSize() +
                " 任务完成数：" + threadPoolExecutor.getCompletedTaskCount() +
                " 当前排队任务数：" + queue.size() +
                " 队列剩余大小：" + queue.remainingCapacity());
    }
}

package dgut.rpc.governance.impl;

import com.alibaba.fastjson.JSON;
import dgut.rpc.config.AbstractServiceConfigImpl;
import dgut.rpc.domain.ThreadPoolEntity;
import dgut.rpc.governance.ThreadPoolResourceManager;
import dgut.rpc.threadpool.ResizableCapacityLinkedBlockingQueue;
import dgut.rpc.threadpool.ThreadPoolFactory;
import dgut.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @description: 线程池资源管理器
 * @author: Steven
 * @time: 2021/9/8 14:51
 */
public class ThreadPoolResourceManagerImpl extends AbstractServiceConfigImpl implements ThreadPoolResourceManager {

    private final static Logger logger = LoggerFactory.getLogger(ThreadPoolResourceManagerImpl.class);

    private final Map<String, ExecutorService> cache = new ConcurrentHashMap();

    private ThreadPoolExecutor publicThreadPoolExecutor;

    private ThreadPoolResourceManagerImpl() {
        this.dataId = "threadPool-config";
        this.group = "dev";
        this.serverAddr = "0.0.0.0:8848";
        configService = NacosUtil.getConfigService(serverAddr);
        addListener();
        // 获取线程池初始配置
        List<ThreadPoolEntity> entityList = JSON.parseArray(getConfig(), ThreadPoolEntity.class);
        entityList.forEach(entity->register(entity));
    }

    private static class ThreadPoolResourceManagerHolder {
        private static ThreadPoolResourceManagerImpl threadPoolResourceManager = new ThreadPoolResourceManagerImpl();
    }

    public ThreadPoolExecutor getPublicThreadPoolExecutor() {
        return publicThreadPoolExecutor;
    }

    public static ThreadPoolResourceManagerImpl getInstance() {
        return ThreadPoolResourceManagerHolder.threadPoolResourceManager;
    }

    @Override
    public void register(ThreadPoolEntity conf) {
        logger.info("[op:ThreadPoolResourceManagerImpl:register] 注册线程池 {}", conf.getThreadPoolName());
        if ("public-pool".equals(conf.getThreadPoolName())) {
            publicThreadPoolExecutor = (ThreadPoolExecutor) ThreadPoolFactory.createDefaultThreadPool();
            cache.put("public-pool", publicThreadPoolExecutor);
            return;
        }
        ExecutorService threadPool = ThreadPoolFactory.createThreadPool(conf.getThreadPoolName(),
                conf.getCorePoolSize(), conf.getMaxPoolSize());
        cache.put(conf.getThreadPoolName(), threadPool);
    }

    @Override
    public void logout(String threadPoolName) {
        logger.info("[op:ThreadPoolResourceManagerImpl:logout] 注销线程池 {}", threadPoolName);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) cache.get(threadPoolName);
        if (null == threadPoolExecutor) {
            logger.error("线程池{}不存在", threadPoolName);
            return;
        }
        cache.remove(threadPoolName);
        threadPoolExecutor.shutdown();
    }

    public ExecutorService getThreadPoolByName(String name) {
        if (null == name) return null;
        return cache.get(name);
    }

    private void modifyArg(ThreadPoolExecutor threadPoolExecutor, int corePoolSize, int maxPoolSize,
                           int workQueueSize) {
        ResizableCapacityLinkedBlockingQueue queue
                = (ResizableCapacityLinkedBlockingQueue) threadPoolExecutor.getQueue();
        threadPoolExecutor.setCorePoolSize(corePoolSize);
        threadPoolExecutor.setMaximumPoolSize(maxPoolSize);
        queue.setCapacity(workQueueSize);
    }

    @Override
    protected void updateConfig(String s) {
        List<ThreadPoolEntity> entityList = JSON.parseArray(s, ThreadPoolEntity.class);
        Set<String> set = new HashSet();
        for (ThreadPoolEntity entity : entityList) {
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) cache.get(entity.getThreadPoolName());
            if (threadPoolExecutor == null) {
                register(entity);
                threadPoolExecutor = (ThreadPoolExecutor) cache.get(entity.getThreadPoolName());
            } else {
                modifyArg(threadPoolExecutor, entity.getCorePoolSize(), entity.getMaxPoolSize(),
                        entity.getWorkQueueCapacity());
            }
            set.add(entity.getThreadPoolName());
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
        for (String threadPoolName : cache.keySet()) {
            if (!set.contains(threadPoolName)) {
                logout(threadPoolName);
            }
        }
    }
}

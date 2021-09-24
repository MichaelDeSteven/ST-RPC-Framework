package dgut.rpc.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: ThreadPoolFactory
 * @author: Steven
 * @time: 2021/3/8 19:07
 */
public class ThreadPoolFactory {
    /**
     * 线程池默认参数
     */
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE_SIZE = 10;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int BLOCKING_QUEUE_CAPACITY = 10;

    private final static Logger logger = LoggerFactory.getLogger(ThreadPoolFactory.class);

    private static ExecutorService threadPool;

    public static ExecutorService createDefaultThreadPool() {
        return createDefaultThreadPool("public-pool", false);
    }

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix) {
        return createDefaultThreadPool(threadNamePrefix, false);
    }

    private static ExecutorService createDefaultThreadPool(String threadNamePrefix, Boolean daemon) {
        BlockingQueue<Runnable> workQueue = new ResizableCapacityLinkedBlockingQueue(BLOCKING_QUEUE_CAPACITY);

        ThreadFactory threadFactory = new myThreadFactory(threadNamePrefix, daemon);

        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                workQueue, threadFactory);
    }

    public static ExecutorService createThreadPool(String threadNamePrefix, int corePoolSize, int maximumPoolSize) {
        BlockingQueue<Runnable> workQueue = new ResizableCapacityLinkedBlockingQueue(BLOCKING_QUEUE_CAPACITY);

        ThreadFactory threadFactory = new myThreadFactory(threadNamePrefix, false);

        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue,
                threadFactory);
    }

    public static void shutdown() {
        if (threadPool.isShutdown() == false) {
            threadPool.shutdown();
        }
    }


    static class myThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        private final ThreadGroup group;

        private final AtomicInteger threadNumber = new AtomicInteger(1);

        private final String namePrefix;

        private final boolean daemon;

        myThreadFactory(String threadPoolName, boolean daemon) {
            this.daemon = daemon;
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = threadPoolName + "-thread-";
        }


        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            t.setDaemon(daemon);
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

}

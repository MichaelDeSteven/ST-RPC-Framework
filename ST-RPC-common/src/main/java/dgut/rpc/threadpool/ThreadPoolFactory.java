package dgut.rpc.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @description: ThreadPoolFactory
 * @author: Steven
 * @time: 2021/3/8 19:07
 */
public class ThreadPoolFactory {
    /**
     * 线程池参数
     */
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private final static Logger logger = LoggerFactory.getLogger(ThreadPoolFactory.class);

    private static ExecutorService threadPool;

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix) {
        return createDefaultThreadPool(threadNamePrefix, false);
    }

    private static ExecutorService createDefaultThreadPool(String threadNamePrefix, Boolean daemon) {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE_SIZE,
                KEEP_ALIVE_TIME, TimeUnit.MINUTES, workQueue, threadFactory);
    }

}

package dgut.rpc.factory;

import java.util.concurrent.*;

/**
 * @description: ThreadPoolFactory
 * @author: Steven
 * @time: 2021/3/4 22:58
 */
public class ThreadPoolFactory {

    /**
     * 线程池参数
     */
    private static int CORE_POOL_SIZE = 100;
    private static int MAX_POOL_SIZE = 100;
    private static int KEEP_ALIVE_TIME = 1;
    private static int BLOCKING_QUEUE_CAPACITY = 100;

    private ThreadPoolFactory() {
    }

    public static ExecutorService createDefaultThreadPool() {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_TIME, TimeUnit.MINUTES, workQueue, threadFactory);
    }
}

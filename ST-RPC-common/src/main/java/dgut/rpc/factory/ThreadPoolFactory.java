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
    private static int PROCESSORS_NUM = Runtime.getRuntime().availableProcessors();

    private static int CORE_POOL_SIZE = PROCESSORS_NUM << 2;

    private static int MAX_POOL_SIZE = PROCESSORS_NUM << 2;

    private static int KEEP_ALIVE_TIME = 0;

    private static int BLOCKING_QUEUE_CAPACITY = 100;

    private static ExecutorService executorService = null;

    private ThreadPoolFactory() {

    }

    public static ExecutorService createDefaultThreadPool(ThreadFactory threadFactory) {
        BlockingQueue<Runnable> blockingQueue =
                new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        return executorService = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS, blockingQueue, threadFactory);
    }

    public static ExecutorService createDefaultThreadPool() {
        return createDefaultThreadPool(Executors.defaultThreadFactory());
    }

    public static void shutdown() {
        if (executorService.isShutdown() == false) {
            executorService.shutdown();
        }
    }
}

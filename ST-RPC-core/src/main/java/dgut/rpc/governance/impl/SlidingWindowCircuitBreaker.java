package dgut.rpc.governance.impl;

import dgut.rpc.enumeration.CircuitBreakerStatus;
import dgut.rpc.governance.CircuitBreaker;
import dgut.rpc.governance.MetricInfo;
import dgut.rpc.governance.SlidingWindowMonitor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;

/**
 * @description: SlidingWindowCircuitBreaker
 * @author: Steven
 * @time: 2021/9/8 14:41
 */
public class SlidingWindowCircuitBreaker implements CircuitBreaker {

    private String circuitBreakerName;

    private long failureThreshold;

    private long errorPercentThreshold;

    private long resetTimeout;

    private LongAdder failureCounter;

    private LongAdder callCounter;

    private AtomicReference<CircuitBreakerStatus> status;

    private ThreadPoolExecutor executor;

    private long lastFailureTime;

    private long executionTimeout;

    private final Object fallback = null;

    private SlidingWindowMonitor slidingWindowMonitor;

    private CircuitBreakerResourceManager manager;

    public SlidingWindowCircuitBreaker(String circuitBreakerName, long failureThreshold, long errorPercentThreshold,
                                       long resetTimeout, String threadPoolName) {
        this.callCounter = new LongAdder();
        this.failureCounter = new LongAdder();
        this.failureThreshold = failureThreshold;
        this.errorPercentThreshold = errorPercentThreshold;
        this.circuitBreakerName = circuitBreakerName;
        this.resetTimeout = resetTimeout;
        this.status.set(CircuitBreakerStatus.CLOSED);
        this.manager = CircuitBreakerResourceManager.getInstance();
    }

    public void reset() {
        this.status = new AtomicReference<>(CircuitBreakerStatus.CLOSED);
        this.lastFailureTime = -1L;
    }

    @Override
    public <T> T call(Supplier<T> supplier) {
        try {
            if (shouldAllowExecution()) {
                slidingWindowMonitor.incrementTotal();
                Future<T> future = this.executor.submit(warp(supplier));
                T result = future.get(executionTimeout, TimeUnit.MILLISECONDS);
                markSuccess();
                return result;
            }
        } catch (RejectedExecutionException ree) {
            markReject();
        } catch (Exception e) {
            markFailure();
        }
        return (T) fallback;
    }

    @Override
    public void remove() {
        reset();
    }

    <T> Callable<T> warp(Supplier<T> supplier) {
        return supplier::get;
    }

    public void call(Runnable runnable) {
        call(() -> {
            runnable.run();
            return null;
        });
    }

    boolean shouldAllowExecution() {
        // 本质是Closed状态
        if (lastFailureTime == -1L) {
            return true;
        }
        // 没到达阈值
        if (errorPercentThreshold > rollingErrorPercentage()) {
            return true;
        }
        return shouldTryAfterRestTimeoutWindow()
                && changeStatus(CircuitBreakerStatus.OPEN, CircuitBreakerStatus.HALF_OPEN);
    }

    boolean shouldTryAfterRestTimeoutWindow() {
        long lastFailureTimeSnap = lastFailureTime;
        long currentTime = System.currentTimeMillis();
        return currentTime > lastFailureTimeSnap + resetTimeout;
    }

    public void markSuccess() {
        slidingWindowMonitor.incrementSuccess();
        if (changeStatus(CircuitBreakerStatus.HALF_OPEN, CircuitBreakerStatus.CLOSED)) {
            reset();
        }
    }

    public void markReject() {
        slidingWindowMonitor.incrementReject();
        if (changeStatus(CircuitBreakerStatus.HALF_OPEN, CircuitBreakerStatus.OPEN)) {
            this.lastFailureTime = System.currentTimeMillis();
        }
    }

    boolean changeStatus(CircuitBreakerStatus o, CircuitBreakerStatus n) {
        boolean r = status.compareAndSet(o, n);
        if (r) {
            manager.report(this.circuitBreakerName, o, n);
        }
        return r;
    }

    public int rollingErrorPercentage() {
        MetricInfo rollingMetricInfo = slidingWindowMonitor.getRollingMetricInfo();
        long rejectCount = rollingMetricInfo.getReject();
        long failureCount = rollingMetricInfo.getFailure();
        long totalCount = rollingMetricInfo.getTotal();
        int errorPercentage = (int) ((double) (rejectCount + failureCount) / totalCount * 100);
        manager.report(this.circuitBreakerName, String.format("错误百分比:%d", errorPercentage));
        return errorPercentage;
    }

    public void markFailure() {
        slidingWindowMonitor.incrementFailure();
        if (changeStatus(CircuitBreakerStatus.HALF_OPEN, CircuitBreakerStatus.OPEN)) {
            this.lastFailureTime = System.currentTimeMillis();
        }
        if (rollingErrorPercentage() >= errorPercentThreshold &&
                changeStatus(CircuitBreakerStatus.CLOSED, CircuitBreakerStatus.OPEN)) {
            this.lastFailureTime = System.currentTimeMillis();
        }
    }
}

package dgut.rpc.governance;

import lombok.*;

import java.util.concurrent.atomic.LongAdder;

/**
 * @description: Bucket
 * @author: Steven
 * @time: 2021/9/12 16:09
 */
@Getter
public class Bucket {
    /**
     * 窗口开始时间戳
     */
    private final long windowStartTimestamp;

    /**
     * 总调用次数
     */
    private final LongAdder total;

    /**
     * 调用成功次数
     */
    private final LongAdder success;

    /**
     * 调用超时次数
     */
    private final LongAdder failure;

    /**
     * 非超时调用失败次数
     */
    private final LongAdder reject;

    public Bucket(long windowStartTimestamp) {
        this.windowStartTimestamp = windowStartTimestamp;
        this.total = new LongAdder();
        this.success = new LongAdder();
        this.reject = new LongAdder();
        this.failure = new LongAdder();
    }

    public void increaseTotal() {
        this.total.increment();
    }

    public void increaseSuccess() {
        this.success.increment();
    }

    public void increaseFailure() {
        this.failure.increment();
    }

    public void increaseReject() {
        this.reject.increment();
    }

    public long totalCount() {
        return this.total.sum();
    }

    public long successCount() {
        return this.success.sum();
    }

    public long failureCount() {
        return this.failure.sum();
    }

    public long rejectCount() {
        return this.reject.sum();
    }

    public void reset() {
        this.total.reset();
        this.success.reset();
        this.failure.reset();
        this.reject.reset();
    }

    @Override
    public String toString() {
        return String.format("Bucket[wt=%d,t=%d,s=%d,f=%d,r=%d]",
                windowStartTimestamp, totalCount(), successCount(), failureCount(), rejectCount());
    }

    public MetricInfo metricInfo() {
        return new MetricInfo(totalCount(), successCount(), failureCount(), rejectCount());
    }
}

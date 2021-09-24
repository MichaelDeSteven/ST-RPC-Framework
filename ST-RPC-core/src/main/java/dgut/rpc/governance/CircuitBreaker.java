package dgut.rpc.governance;

import java.util.function.Supplier;

/**
 * @description: CircuitBreaker
 * @author: Steven
 * @time: 2021/9/8 14:32
 */
public interface CircuitBreaker {

    void call(Runnable runnable);

    /**
     * 在断路器中调用
     * @param supplier
     * @param <T>
     * @return
     */
    <T> T call(Supplier<T> supplier);

    void remove();
}

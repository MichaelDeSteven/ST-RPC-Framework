package dgut.rpc.domain;

import lombok.*;

import java.util.List;

/**
 * @description: CircuitBreakerEntity
 * @author: Steven
 * @time: 2021/9/12 15:59
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CircuitBreakerEntity {
    /**
     * 熔断器名称
     */
    private String circuitBreakerName;

    /**
     * 服务器地址
     */
    private String serverAddr;

    /**
     * 最大执行时间
     */
    private long executionTimeout;

    /**
     * 失败次数阈值
     */
    private long failureThreshold;

    /**
     * 失败百分比阈值
     */
    private long errorPercentThreshold;

    /**
     * 是否启用熔断
     */
    private boolean enable;
}

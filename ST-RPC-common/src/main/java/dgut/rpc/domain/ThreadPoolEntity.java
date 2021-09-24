package dgut.rpc.domain;

import lombok.*;

/**
 * @description: 线程池配置参数
 * @author: Steven
 * @time: 2021/4/12 17:29
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ThreadPoolEntity {

    /**
     * 线程池名称
     */
    private String threadPoolName;

    /**
     * 服务器地址
     */
    private String serverAddr;

    /**
     * 核心线程数
     */
    private int corePoolSize;

    /**
     * 最大线程池数
     */
    private int maxPoolSize;

    /**
     * 工作队列长度
     */
    private int workQueueCapacity;

    /**
     * 最大执行时间
     */
    private long timeout;
}

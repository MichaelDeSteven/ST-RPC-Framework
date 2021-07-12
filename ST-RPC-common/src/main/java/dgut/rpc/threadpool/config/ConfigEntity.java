package dgut.rpc.threadpool.config;

import lombok.*;

/**
 * @description: ConfigEntity
 * @author: Steven
 * @time: 2021/4/12 17:29
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ConfigEntity {
    private Integer corePoolSize;

    private Integer maxPoolSize;

    private Integer workQueueCapacity;
}

package dgut.rpc.governance;

import dgut.rpc.domain.ThreadPoolEntity;

/**
 * @description: 线程池资源管理器
 * @author: Steven
 * @time: 2021/9/8 14:57
 */
public interface ThreadPoolResourceManager {
    /**
     * 注册线程池
     * @param conf
     */
    void register(ThreadPoolEntity conf);

    /**
     * 注销线程池
     * @param threadPoolName
     */
    void logout(String threadPoolName);
}
